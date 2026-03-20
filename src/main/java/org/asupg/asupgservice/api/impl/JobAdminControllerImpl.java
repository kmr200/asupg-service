package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.api.JobAdminController;
import org.asupg.asupgservice.client.WorkersClient;
import org.asupg.asupgservice.client.model.request.BankConfigUpdateRequest;
import org.asupg.asupgservice.client.model.response.BankConfigResponse;
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

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/monthly-charge")
    public ResponseEntity<Void> triggerMonthlyCharge(
            @RequestParam(required = false) YearMonth month
    ) {
        if (month == null) {
            month = YearMonth.now(ZoneOffset.UTC);
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

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/config")
    public ResponseEntity<BankConfigResponse> getBankConfig() {
        return workersClient.getBankConfig();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/config")
    public ResponseEntity<BankConfigResponse> updateBankConfig(@RequestBody BankConfigUpdateRequest request) {
        return workersClient.updateBankConfig(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/config/password")
    public ResponseEntity<String> getBankPassword() {
        return workersClient.getBankPassword();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/config/username")
    public ResponseEntity<String> getBankUsername() {
        return workersClient.getBankUsername();
    }
}
