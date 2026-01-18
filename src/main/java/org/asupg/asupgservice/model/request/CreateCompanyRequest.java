package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asupg.asupgservice.validation.ValidationDoc;

import java.time.YearMonth;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request class for creating a company")
public class CreateCompanyRequest {

    @NotBlank(message = ValidationDoc.INN_BLANK_MESSAGE)
    @Size(min = 9, max = 9, message = ValidationDoc.INN_SIZE_MESSAGE)
    @Schema(description = "INN of the company to be created", example = "123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String inn;

    @NotBlank(message = ValidationDoc.NAME_BLANK_MESSAGE)
    @Schema(description = "Name of the company to be created", example = "OOO \"TEST\"", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Monthly subscription cost for the company", defaultValue = "100000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long monthlyRate;

    @Future(message = ValidationDoc.BILLING_START_DATE_FUTURE)
    @Schema(description = "When does company start paying for the subscription. After 1 year from creation date if not specified", example = "2027-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private YearMonth billingStartMonth;

    @Email(message = ValidationDoc.EMAIL_VALID_MESSAGE)
    @Schema(description = "Email contact point of the company", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    @Schema(description = "Phone contact point of the company", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phone;

}
