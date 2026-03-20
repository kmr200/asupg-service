package org.asupg.asupgservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asupg.asupgservice.client.model.response.AccountResponse;
import org.asupg.asupgservice.model.CompanyDashboardResult;
import org.asupg.asupgservice.model.TransactionDashboardResult;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard overview response containing financial and operational summary data")
public class DashboardResponse {

    @Schema(description = "Total debt across all companies with negative balance", example = "-4750901233.33")
    BigDecimal totalDebt;

    @Schema(description = "Total balance across all companies", example = "-2897528536.53")
    BigDecimal totalBalance;

    @Schema(description = "Top 10 companies with the highest debt, ordered by balance ascending")
    List<CompanyDashboardResult.CompanyInDebt> topDebtors;

    @Schema(description = "Summary of company counts and device totals")
    CompanyDashboardResult.CompanySummary companySummary;

    @Schema(description = "Breakdown of bank transactions by reconciliation status")
    List<TransactionDashboardResult.ReconciliationBreakdown> reconciliationBreakdown;

    @Schema(description = "Monthly trend of charges and payments over the last 6 months, ordered chronologically")
    List<TransactionDashboardResult.MonthlyTrend> monthlyTrend;

    @Schema(description = "Accounts connected to bank account")
    List<AccountResponse> accounts;

    public DashboardResponse(
            TransactionDashboardResult transactionDashboardResult,
            CompanyDashboardResult companyDashboardResult,
            BigDecimal totalDevices,
            List<AccountResponse> accounts
    ) {
        totalDebt = companyDashboardResult.getTotalDebt().isEmpty() ?
                BigDecimal.ZERO : companyDashboardResult.getTotalDebt().getFirst().getResult();
        totalBalance = companyDashboardResult.getTotalBalance().isEmpty() ?
                BigDecimal.ZERO : companyDashboardResult.getTotalBalance().getFirst().getResult();
        topDebtors = companyDashboardResult.getTopDebtors();
        companySummary = companyDashboardResult.getCompanySummary().isEmpty() ?
                new CompanyDashboardResult.CompanySummary(0, 0, 0, 0) :
                companyDashboardResult.getCompanySummary().getFirst();
        reconciliationBreakdown = transactionDashboardResult.getReconciliationBreakdown();
        monthlyTrend = transactionDashboardResult.getMonthlyTrend();
        companySummary.setTotalDevices(totalDevices.intValue());
        this.accounts = accounts;
    }

}
