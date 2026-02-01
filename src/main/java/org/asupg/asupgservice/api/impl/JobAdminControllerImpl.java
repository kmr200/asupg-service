package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.api.JobAdminController;
import org.asupg.asupgservice.client.WorkersClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false)LocalDate to
    ) {
        if ((from == null && to != null) || (from != null && to == null)) {
            return ResponseEntity.badRequest().build();
        }

        workersClient.runReportIngest(from, to);
        return ResponseEntity.ok().build();
    }
}
