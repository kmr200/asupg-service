package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asupg.asupgservice.validation.ValidationDoc;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request class for creating a company")
public class CreateCompanyRequest {

    @NotBlank(message = ValidationDoc.INN_BLANK_MESSAGE)
    @Pattern(
            regexp = "^\\d{9}|\\d{14}$",
            message = ValidationDoc.INN_SIZE_MESSAGE
    )
    @Schema(description = "INN of the company to be created", example = "123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String inn;

    @NotBlank(message = ValidationDoc.NAME_BLANK_MESSAGE)
    @Schema(description = "Name of the company to be created", example = "OOO \"TEST\"", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Email(message = ValidationDoc.EMAIL_VALID_MESSAGE)
    @Schema(description = "Email contact point of the company", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    @Schema(description = "Phone contact point of the company", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phone;

}
