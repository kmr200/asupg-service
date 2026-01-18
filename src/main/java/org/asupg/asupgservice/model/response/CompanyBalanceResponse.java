package org.asupg.asupgservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response class for requesting companies balance")
public class CompanyBalanceResponse {

    @Schema(description = "INN of the company to be created", example = "123456789")
    private String inn;

    @Schema(description = "Name of the company to be created", example = "OOO \"TEST\"")
    private String name;

    @Schema(description = "Current balance of the company", example = "200000")
    private BigDecimal balance;

    @Schema(description = "How many months was company charged for")
    private Long monthsElapsed;

    @Schema(description = "Monthly breakdown of each monthly payment company made")
    private List<MonthlyCharge> monthlyBreakdown;

    @Schema(description = "General billing information")
    private BillingInfo billingInfo;

    @Schema(description = "Current status of companies balance. DEBT if in debt and CREDIT if positive.", example = "CREDIT")
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
            Long monthsElapsed,
            List<MonthlyCharge> monthlyBreakdown,
            BillingInfo billingInfo
    ) {
        this.inn = inn;
        this.name = name;
        this.balance = balance;
        this.monthsElapsed = monthsElapsed;
        this.monthlyBreakdown = monthlyBreakdown;
        this.billingInfo = billingInfo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyCharge {

        @Schema(description = "For which period the company was charged", example = "2024-01")
        private String period;

        @Schema(description = "Amount charged for the specified period", example = "100000")
        private BigDecimal charge;

        @Schema(description = "First day of the billing period", example = "2024-01-01")
        private LocalDate startDate;

        @Schema(description = "Last day of the billing period", example = "2024-01-31")
        private LocalDate endDate;

        @Schema(description = "Is the period specified current or was in past", example = "CURRENT")
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingInfo {

        @Schema(description = "When did the company start paying", example = "2025-01")
        private YearMonth billingStartMonth;

        @Schema(description = "How much does company pay each month", example = "100000")
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
