package org.asupg.asupgservice.client.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Bank API configuration")
public class BankConfigResponse {

    @Schema(description = "Bank API host URL", example = "https://host:port")
    private String host;

    @Schema(description = "20-digit account number used for transactions", example = "12345678901234567890")
    private String account;

    @Schema(description = "5-digit branch MFO code", example = "12345")
    private String branch;

}
