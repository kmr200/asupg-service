package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.api.JobAdminController;
import org.asupg.asupgservice.client.workers.WorkersClient;
import org.asupg.asupgservice.client.workers.request.BankConfigUpdateRequest;
import org.asupg.asupgservice.client.workers.response.BankConfigResponse;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.response.DeviceSyncResponse;
import org.asupg.asupgservice.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;

@RestController
@RequestMapping("v1/jobs")
@RequiredArgsConstructor
public class JobAdminControllerImpl implements JobAdminController {

    private final WorkersClient workersClient;
    private final DeviceService deviceService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/monthly-charge")
    public ResponseEntity<Void> triggerMonthlyCharge(
            @RequestParam(required = false) YearMonth month
    ) {
        if (month == null) {
            month = YearMonth.now(ZoneOffset.UTC);
        }

        // Sync device status before charging users
        DeviceSyncResponse syncResponse = deviceService.syncDevicesWithCoreAsupg();

        if (syncResponse.getSyncStatus() == DeviceSyncResponse.SyncStatus.FAILED) {
            throw new AppException(500, "Ошибка синхронизации", "Синхронизация устройств не удалась, начисление отменено");
        }

        workersClient.runMonthlyCharge(month.toString());

        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/report-ingestion")
    public ResponseEntity<Void> triggerReportIngestion(
            @RequestParam(required = false) LocalDate date
    ) {
        workersClient.runReportIngest(date);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/device-sync")
    public ResponseEntity<DeviceSyncResponse> syncDevices() {
        DeviceSyncResponse response = deviceService.syncDevicesWithCoreAsupg();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/config")
    public ResponseEntity<BankConfigResponse> getBankConfig() {
        ResponseEntity<BankConfigResponse> response = workersClient.getBankConfig();
        return ResponseEntity.ok(response.getBody());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/config")
    public ResponseEntity<BankConfigResponse> updateBankConfig(@RequestBody BankConfigUpdateRequest request) {
        ResponseEntity<BankConfigResponse> response = workersClient.updateBankConfig(request);
        return ResponseEntity.ok(response.getBody());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/config/password")
    public ResponseEntity<String> getBankPassword() {
        ResponseEntity<String> response = workersClient.getBankPassword();
        return ResponseEntity.ok(response.getBody());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/config/username")
    public ResponseEntity<String> getBankUsername() {
        ResponseEntity<String> response = workersClient.getBankUsername();
        return ResponseEntity.ok(response.getBody());
    }
}
