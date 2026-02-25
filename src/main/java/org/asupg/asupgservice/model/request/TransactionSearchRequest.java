package org.asupg.asupgservice.model.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.ReconciliationStatus;
import org.asupg.asupgservice.model.SortOrder;
import org.asupg.asupgservice.model.TransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Function;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Request class for searching transactions")
public class TransactionSearchRequest {

    @Schema(description = "Earliest date", example = "2026-01-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate fromDate;

    @Schema(description = "Earliest date", example = "2026-01-02", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    LocalDate toDate;

    @Schema(description = "Minimum amount in transaction", example = "100000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    BigDecimal minAmount;

    @Schema(description = "Maximum amount in transaction", example = "200000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    BigDecimal maxAmount;

    @Schema(description = "Type of transaction", example = "MONTHLY_CHARGE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    TransactionDTO.TransactionType transactionType;

    @Schema(description = "Reconciliation status of a transaction", example = "NOT_FOUND", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    ReconciliationStatus reconciliationStatus;

    @Min(1)
    @Max(100)
    @JsonProperty(defaultValue = "10")
    @Schema(description = "Maximum elements per page", defaultValue = "10", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer limit = 10;

    @Schema(description = "Cursor to retrieve next page", example = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String cursor;

    @JsonProperty(defaultValue = "date")
    @Schema(description = "Specifies the field by which the result will be sorted", example = "amount", defaultValue = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    SortBy sortBy = SortBy.DATE;

    @JsonProperty(defaultValue = "DESC")
    @Schema(description = "Sorting order by balance of the company", defaultValue = "DESC", example = "ASC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    SortOrder sortOrder = SortOrder.DESC;

    @Size(max = 100)
    @Schema(description = "Search field for searching from company INN, description or company name, case insensitive", example = "123", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String search;

    @AllArgsConstructor
    @Getter
    public enum SortBy implements SortableField<TransactionDTO> {

        DATE(
                "date",
                TransactionDTO::getDate,
                date -> LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        ),
        AMOUNT(
                "amount",
                TransactionDTO::getAmount,
                BigDecimal::new
        );

        private final String mongoField;
        private final Function<TransactionDTO, Object> extractor;
        private final Function<String, Object> parser;

        @JsonCreator
        public static SortBy fromValue(String value) {
            return SortableField.fromValue(value, SortBy.class);
        }

        @JsonValue
        public String getValue() {
            return mongoField;
        }
    }

}
