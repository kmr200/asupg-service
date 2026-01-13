package org.asupg.asupgservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyBalanceResponse {
    private String inn;
    private String name;
    private BigDecimal balance;
    private BigDecimal totalPaid;
    private BigDecimal totalCharges;
    private Long monthsElapsed;
    private List<MonthlyCharge> monthlyBreakdown;
    private BillingInfo billingInfo;
    private BalanceStatus balanceStatus;

    // Simple constructor
    public CompanyBalanceResponse(String inn, String name, BigDecimal balance) {
        this.inn = inn;
        this.name = name;
        this.balance = balance;
    }

    public BalanceStatus getBalanceStatus() {
        if (balance == null) return BalanceStatus.UNKNOWN;
        int comparison = balance.compareTo(BigDecimal.ZERO);
        if (comparison > 0) return BalanceStatus.CREDIT;
        if (comparison < 0) return BalanceStatus.DEBT;
        return BalanceStatus.CURRENT;
    }

    public CompanyBalanceResponse(
            String inn,
            String name,
            BigDecimal balance,
            BigDecimal totalPaid,
            BigDecimal totalCharges,
            Long monthsElapsed,
            List<MonthlyCharge> monthlyBreakdown,
            BillingInfo billingInfo
    ) {
        this.inn = inn;
        this.name = name;
        this.balance = balance;
        this.totalPaid = totalPaid;
        this.totalCharges = totalCharges;
        this.monthsElapsed = monthsElapsed;
        this.monthlyBreakdown = monthlyBreakdown;
        this.billingInfo = billingInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCharge {
        private String period;              // "2024-01" or "January 2024"
        private BigDecimal charge;          // Amount charged this month
        private LocalDate startDate;        // First day of billing period
        private LocalDate endDate;          // Last day of billing period
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingInfo {
        private LocalDate billingStartDate;
        private BigDecimal monthlyRate;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public enum BalanceStatus {
        UNKNOWN("UNKNOWN"),
        DEBT("DEBT"),
        CREDIT("CREDIT"),
        CURRENT("CURRENT");
        private String status;
    }
}
