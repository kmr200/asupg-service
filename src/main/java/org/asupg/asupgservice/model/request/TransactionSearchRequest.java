package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.TransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

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

}
