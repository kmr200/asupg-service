package org.asupg.asupgservice.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.CompanyStatus;
import org.asupg.asupgservice.model.SortOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request class for searching companies")
public class CompanySearchRequest {

    @Schema(description = "Minimum balance of the company", example = "100000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    BigDecimal minBalance;

    @Schema(description = "Maximum balance of the company", example = "200000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    BigDecimal maxBalance;

    @Schema(description = "Filter companies by upper boundary for subscription start date", example = "2026-01-17", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate subscriptionStartDateFrom;

    @Schema(description = "Filter companies by lower boundary for subscription start date", example = "2025-01-17", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate subscriptionStartDateTo;

    @Schema(description = "Filter companies by upper boundary for billing start month", example = "2026-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    YearMonth billingStartMonthFrom;

    @Schema(description = "Filter companies by lower boundary for subscription start date", example = "2025-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    YearMonth billingStartMonthTo;

    @Schema(description = "Filter companies by their status", example = "ACTIVE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    CompanyStatus status;

    @Min(1)
    @Max(100)
    @JsonProperty(defaultValue = "10")
    @Schema(description = "Maximum elements per page", defaultValue = "10", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer limit = 10;

    @Schema(description = "Continuation token to retrieve next page", example = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String continuationToken;

    @JsonProperty(defaultValue = "name")
    @Schema(description = "Specifies the field by which the result will be sorted", example = "inn", defaultValue = "name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    SortBy sortBy = SortBy.NAME;

    @JsonProperty(defaultValue = "DESC")
    @Schema(description = "Sorting order by balance of the company", defaultValue = "DESC", example = "ASC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    SortOrder sortOrder = SortOrder.DESC;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum SortBy {
        MONTHLY_RATE("monthlyRate"),
        SUBSCRIPTION_START_DATE("subscriptionStartDate"),
        BILLING_START_MONTH("billingStartMonth"),
        CURRENT_BALANCE("currentBalance"),
        INN("inn"),
        NAME("name");

        private String value;

        @JsonCreator
        public static SortBy fromValue(String value) {
            for (SortBy s : SortBy.values()) {
                if (s.value.equals(value)) {
                    return s;
                }
            }
            throw new IllegalArgumentException(value);
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }

}
