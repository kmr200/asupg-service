package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.validation.ValidationDoc;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request class for registering new users")
public class RegisterUserRequest {

    @NotBlank(message = ValidationDoc.USERNAME_BLANK_MESSAGE)
    @Schema(description = "Login of the user trying to be registered", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = ValidationDoc.FIRSTNAME_BLANK_MESSAGE)
    @Schema(description = "First name of the user trying to be registered", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank(message = ValidationDoc.LASTNAME_BLANK_MESSAGE)
    @Schema(description = "Last name of the user trying to be registered", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @NotBlank(message = ValidationDoc.PASSWORD_BLANK_MESSAGE)
    @Schema(description = "Password of the user trying to be registered", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotEmpty(message = ValidationDoc.ROLES_EMPTY_MESSAGE)
    @Schema(description = "Roles of the user trying to be registered. Currently only 'ADMIN' and 'USER' roles are supported", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> roles;

}
