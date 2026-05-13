package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.CompanyStatus;

import java.math.BigDecimal;

@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Request class for updating companies")
public class CompanyUpdateRequest {

    @Schema(description = "Name of the company to be updated", example = "OOO \"TEST\"")
    private String name;

    @Schema(description = "Balance of the company to be updated", example = "200000")
    private BigDecimal currentBalance;

    @Schema(description = "Status of the company", example = "ACTIVE")
    private CompanyStatus status;

    @Email
    @Schema(description = "Email contact point of the company", example = "user@example.com")
    private String email;

    @Schema(description = "Phone contact point of the company")
    private String phone;

}
