package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request class for updating user")
public class UpdateUserRequest {

    @Schema(description = "New first name for the user", example = "John", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String firstName;

    @Schema(description = "New last name for the user", example = "Doe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String lastName;

    @Schema(description = "New password for the user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String password;

    @Schema(description = "New set of roles for the user", example = "['ADMIN']", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Set<String> roles;

    @Schema(description = "Whether the user is locked", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Boolean locked;

}
