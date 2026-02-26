package org.asupg.asupgservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response class for report overview")
public class OverviewResponse {

    @Schema(description = "Total sum of balances of all companies")
    BigDecimal totalBalance;

    @Schema(description = "Total sum of balances of companies in debt")
    BigDecimal totalDebt;

    @Schema(description = "Total sum of transactions in NOT_FOUND state")
    BigDecimal totalNotFoundTransactions;

}
