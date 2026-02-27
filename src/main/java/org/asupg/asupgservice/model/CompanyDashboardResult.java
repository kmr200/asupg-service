package org.asupg.asupgservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Aggregated dashboard data from the company collection")
public class CompanyDashboardResult {

    List<AggregationResult> totalDebt;
    List<AggregationResult> totalBalance;
    List<CompanyInDebt> topDebtors;
    List<CompanySummary> companySummary;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Company with a negative balance")
    public static class CompanyInDebt {
        @Schema(description = "Company tax identification number", example = "202328794")
        private String inn;

        @Schema(description = "Company name", example = "OOO Company A")
        private String name;

        @Schema(description = "Current balance of the company, always negative", example = "-100400000")
        private BigDecimal currentBalance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Aggregated summary of company counts and devices")
    public static class CompanySummary {
        @Schema(description = "Total number of companies", example = "2849")
        private Integer totalCompanies;

        @Schema(description = "Number of active companies", example = "2800")
        private Integer activeCompanies;

        @Schema(description = "Number of inactive companies", example = "49")
        private Integer inactiveCompanies;

        @Schema(description = "Total number of devices across all companies", example = "380")
        private Integer totalDevices;
    }

}
