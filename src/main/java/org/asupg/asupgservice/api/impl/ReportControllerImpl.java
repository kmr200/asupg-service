package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.api.ReportController;
import org.asupg.asupgservice.model.response.DashboardResponse;
import org.asupg.asupgservice.model.response.TotalDebt;
import org.asupg.asupgservice.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("v1/reports")
@RequiredArgsConstructor
public class ReportControllerImpl implements ReportController {

    private final ReportService reportService;

    @GetMapping("/total-debt")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TotalDebt> getCompaniesTotalDebt() {
        BigDecimal totalDebt = reportService.getCompaniesTotalDebt();
        TotalDebt totalDebtResponse = new TotalDebt(totalDebt);

        return new ResponseEntity<>(totalDebtResponse, HttpStatus.OK);
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<DashboardResponse> getOverview() {
        DashboardResponse dashboardResponse = reportService.getDashboard();

        return new ResponseEntity<>(dashboardResponse, HttpStatus.OK);
    }

}
