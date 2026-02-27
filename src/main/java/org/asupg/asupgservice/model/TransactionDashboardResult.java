package org.asupg.asupgservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Decimal128;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Aggregated dashboard data from the transaction collection")
public class TransactionDashboardResult {

    List<ReconciliationBreakdown> reconciliationBreakdown;
    List<MonthlyTrend> monthlyTrend;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Bank transaction counts and amounts grouped by reconciliation status")
    public static class ReconciliationBreakdown {
        @Schema(description = "Reconciliation status", example = "MATCHED")
        String status;

        @Schema(description = "Number of transactions with this status", example = "9353")
        Integer count;

        @Schema(description = "Total transaction amount for this status", example = "7184171463.47")
        Decimal128 totalAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Monthly aggregation of charges and payments over the last 6 months")
    public static class MonthlyTrend {
        @Schema(description = "Month in yyyy-MM format", example = "2025-09")
        String month;

        @Schema(description = "Total amount charged (MONTHLY_CHARGE transactions), always positive", example = "1200000000.00")
        Decimal128 totalCharged;

        @Schema(description = "Total amount paid (BANK_PAYMENT transactions)", example = "980000000.00")
        Integer totalPaid;
    }

}
