package org.asupg.asupgservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.client.asupg.model.response.AsupgDevice;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.Device;
import org.asupg.asupgservice.model.DeviceStatus;
import org.asupg.asupgservice.model.response.DeviceSyncFailure;
import org.asupg.asupgservice.model.response.DeviceSyncResponse;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final CompanyRepository companyRepository;
    private final AsupgCoreService asupgCoreService;
    private final DeviceRepository deviceRepository;

    private final AtomicBoolean deviceSynInProgress = new AtomicBoolean(false);

    @Value("${asupg.billing.free-period}")
    private Integer billingFreePeriod;

    @Value("${asupg.billing.monthly-rate}")
    private Long billingMonthlyRate;

    public Device getDevice(String deviceId) {
        return deviceRepository.findById(deviceId).orElseThrow(
                () -> new AppException(404, "Ошибка валидации", "Устройство с id: " + deviceId + " не найдено")
        );
    }

    public Device createDevice(
            String deviceId,
            String deviceName,
            String companyInn,
            BigDecimal monthlyRate,
            YearMonth freeUntil,
            DeviceStatus status
    ) {
        if (freeUntil == null) freeUntil = YearMonth.now().plusYears(billingFreePeriod);
        if (monthlyRate == null) monthlyRate = BigDecimal.valueOf(billingMonthlyRate);

        if (!companyRepository.existsById(companyInn)) {
            log.error("Company specified during device creation does not exist");
            throw new AppException(400, "Ошибка валидации", "Компании с ИНН: " + companyInn + " не существует");
        }

        Device device = new Device(
                deviceId,
                deviceName,
                companyInn,
                "Gas Meter",
                monthlyRate,
                LocalDate.now(ZoneOffset.UTC),
                freeUntil,
                null,
                status
        );

        try {
            return deviceRepository.insert(device);
        } catch (DuplicateKeyException e) {
            log.info("Device with id {} already exists", deviceId);
            throw new AppException(409, "Конфликт", "Устройство с id: " + deviceId + " уже зарегистрировано");
        }
    }

    @Transactional
    public Device updateDevice(
            String inn,
            String deviceName,
            String companyInn,
            BigDecimal monthlyRate,
            YearMonth freeUntil,
            DeviceStatus status
    ) {
        Device device = getDevice(inn);

        if (deviceName != null && !deviceName.isBlank()) device.setDeviceName(deviceName);
        if (monthlyRate != null) device.setMonthlyRate(monthlyRate);
        if (freeUntil != null) device.setFreeUntil(freeUntil);
        if (status != null) device.setStatus(status);

        if (companyInn != null && !companyInn.isBlank() && !companyInn.equals(device.getCompanyInn())) {
            companyRepository.findById(companyInn).orElseThrow(
                    () -> new AppException(404, "Ресурс не найден", "Компания с id: " + companyInn + " не найдена")
            );

            device.setCompanyInn(companyInn);
        }

        return deviceRepository.save(device);
    }

    @Transactional
    public Device deleteDevice(String inn) {
        Device device = getDevice(inn);
        deviceRepository.delete(device);

        return device;
    }

    public DeviceSyncResponse syncDevicesWithCoreAsupg() {
        if (!deviceSynInProgress.compareAndSet(false, true)) {
            throw new AppException(409, "Конфликт", "Синхронизация устройств уже выполняется");
        }

        try {
            Map<String, AsupgDevice> asupgDeviceMap = aggregateObjects(
                    asupgCoreService.retrieveDevices(), AsupgDevice::getObjectGuid);
            Map<String, Device> deviceMap = aggregateObjects(
                    deviceRepository.findAll(), Device::getDeviceId);

            return processDiff(asupgDeviceMap, deviceMap);

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            return DeviceSyncResponse.builder()
                    .syncStatus(DeviceSyncResponse.SyncStatus.FAILED)
                    .failures(List.of(new DeviceSyncFailure(null, null, "Неожиданная ошибка: " + e.getMessage())))
                    .build();
        } finally {
            deviceSynInProgress.set(false);
        }
    }

    private DeviceSyncResponse processDiff(Map<String, AsupgDevice> asupgDeviceMap, Map<String, Device> deviceMap) {
        List<Device> updatedDevices = new ArrayList<>();
        List<DeviceSyncFailure> failures = new ArrayList<>();

        for (String key : deviceMap.keySet()) {
            Device device = deviceMap.get(key);

            if (asupgDeviceMap.containsKey(key)) {
                AsupgDevice asupgDevice = asupgDeviceMap.get(key);
                DeviceStatus asupgDeviceStatus = asupgCoreService.mapStatusGuidToDeviceStatus(asupgDevice.getStatusGuid());

                if (isDeviceOutOfSync(device, asupgDevice, asupgDeviceStatus)) {
                    device.setStatus(asupgDeviceStatus);
                    device.setDeviceName(asupgDevice.getObjectName());
                    updatedDevices.add(device);
                }
            } else {
                log.warn("Device {} not found in ASUPG", device.getDeviceId());
                failures.add(new DeviceSyncFailure(
                        device.getDeviceId(),
                        device.getDeviceName(),
                        "Устройство не найдено в asupg.uz"
                ));
            }
        }

        deviceRepository.updateDevices(updatedDevices);

        return DeviceSyncResponse.builder()
                .total(deviceMap.size())
                .updated(updatedDevices.size())
                .failed(failures.size())
                .failures(failures)
                .build();
    }

    private <T> Map<String, T> aggregateObjects(List<T> objects, Function<? super T, String> keyExtractor) {
        return objects.stream()
                .collect(Collectors.toMap(
                        keyExtractor,
                        Function.identity(),
                        (existing, duplicate) -> {
                            log.warn("Duplicate key detected, keeping first occurrence: {}", keyExtractor.apply(existing));
                            return existing;
                        }
                ));
    }

    private boolean isDeviceOutOfSync(Device device, AsupgDevice asupgDevice, DeviceStatus asupgDeviceStatus) {
        DeviceStatus deviceStatus = device.getStatus();

        return deviceStatus != asupgDeviceStatus ||
                !device.getDeviceName().equals(asupgDevice.getObjectName());
    }

}
