package org.asupg.asupgservice.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
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
public class CompanySearchRequest {

    BigDecimal minBalance;

    BigDecimal maxBalance;

    LocalDate subscriptionStartDateFrom;

    LocalDate subscriptionStartDateTo;

    YearMonth billingStartMonthFrom;

    YearMonth billingStartMonthTo;

    CompanyStatus status;

    @Min(1)
    @Max(100)
    @JsonProperty(defaultValue = "10")
    Integer limit = 10;

    String continuationToken;

    @JsonProperty(defaultValue = "name")
    SortBy sortBy = SortBy.NAME;

    @JsonProperty(defaultValue = "DESC")
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
