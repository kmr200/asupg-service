package org.asupg.asupgservice.client.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Bank account information")
public class AccountResponse {

    @Schema(description = "20-digit account number", example = "20208000012345678001")
    private String account;

    @Schema(description = "5-digit branch MFO code", example = "00444")
    private String branch;

    @Schema(description = "Account name", example = "OOO 'Client Name' основной счет")
    private String name;

    @Schema(description = "Currency code", example = "000")
    private String currency;

    @Schema(description = "Opening balance for the current operational day in tiyin", example = "13000000")
    private Long balanceIn;

    @Schema(description = "Current / closing balance in tiyin", example = "10000000")
    private Long balanceOut;

    @Schema(description = "Total credit turnover for the current operational day in tiyin", example = "6900000")
    private Long creditTurnover;

    @Schema(description = "Total debit turnover for the current operational day in tiyin", example = "9900000")
    private Long debitTurnover;

    @Schema(description = "Date of last transaction in DD.MM.YYYY format", example = "11.06.2020")
    private String lastTransactionDate;

    @Schema(description = "Account opening date in DD.MM.YYYY format", example = "11.06.2017")
    private String openDate;

    @Schema(
            description = """
                    Account state:
                    0 - Undefined,
                    1 - Open,
                    2 - Approved (transactions allowed),
                    3 - Closed,
                    4 - Blocked
                    """,
            example = "2",
            allowableValues = {"0", "1", "2", "3", "4"}
    )
    private Integer state;

    @Schema(description = "Indicates whether payments can be made from this account via API", example = "true")
    private boolean paymentAllowed;
}