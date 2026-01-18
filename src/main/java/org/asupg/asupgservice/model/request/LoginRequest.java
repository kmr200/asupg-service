package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request class for logging in")
public class LoginRequest {

    @NotBlank
    @Schema(description = "Login of the user trying to authenticate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Schema(description = "Password of the user trying to authenticate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

}
