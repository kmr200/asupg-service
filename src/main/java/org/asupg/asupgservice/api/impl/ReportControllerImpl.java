package org.asupg.asupgservice.api.impl;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.api.ReportController;
import org.asupg.asupgservice.model.response.OverviewResponse;
import org.asupg.asupgservice.model.response.TotalDebt;
import org.asupg.asupgservice.service.CompanyService;
import org.asupg.asupgservice.service.TransactionService;
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

    private final CompanyService companyService;
    private final TransactionService transactionService;

    @GetMapping("/total-debt")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TotalDebt> getCompaniesTotalDebt() {
        BigDecimal totalDebt = companyService.getCompaniesTotalDebt();
        TotalDebt totalDebtResponse = new TotalDebt(totalDebt);

        return new ResponseEntity<>(totalDebtResponse, HttpStatus.OK);
    }

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<OverviewResponse> getOverview() {
        var totalBalance = companyService.getCompaniesTotalBalance();
        var totalDebt = companyService.getCompaniesTotalDebt();
        var totalNotFoundTransactions = transactionService.getTotalNotFoundTransactions();

        OverviewResponse overviewResponse = new OverviewResponse(
                totalBalance,
                totalDebt,
                totalNotFoundTransactions
        );

        return new ResponseEntity<>(overviewResponse, HttpStatus.OK);
    }

}
