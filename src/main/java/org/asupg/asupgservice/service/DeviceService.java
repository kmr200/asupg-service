package org.asupg.asupgservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.DeviceDTO;
import org.asupg.asupgservice.model.DeviceStatus;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {

    private final CompanyRepository companyRepository;
    @Value("${asupg.billing.free-period}")
    private Integer billingFreePeriod;

    @Value("${asupg.billing.monthly-rate}")
    private Long billingMonthlyRate;

    private final DeviceRepository deviceRepository;

    public DeviceDTO getDevice(String deviceId) {
        return deviceRepository.findById(deviceId).orElseThrow(
                () -> new AppException(404, "Validation failed", "Device with id: " + deviceId + " not found")
        );
    }

    public DeviceDTO createDevice(
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
            throw new AppException(400, "Validation failed", "Company with INN: " + companyInn + " does not exist");
        }

        DeviceDTO deviceDTO = new DeviceDTO(
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
            return deviceRepository.insert(deviceDTO);
        } catch (DuplicateKeyException e) {
            log.info("Device with id {} already exists", deviceId);
            throw new AppException(409, "Conflict", "Device with id: " + deviceId + " already exists");
        }
    }
}
