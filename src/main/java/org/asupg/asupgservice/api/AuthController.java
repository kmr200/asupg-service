package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.asupg.asupgservice.model.request.LoginRequest;
import org.asupg.asupgservice.model.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Authentication endpoints")
public interface AuthController {

    @Operation(
        summary = "Login", description = "Generates JWT token", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token generated",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = LoginResponse.class)
                )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(
                        mediaType = "application/json",
                        examples = @ExampleObject("""
                                {
                                    "timestamp": "timestamp",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Invalid username or password",
                                    "path": "/api/asupg-service/v1/auth/login"
                                }
                                """)
                )
            )
    })
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest);

}
