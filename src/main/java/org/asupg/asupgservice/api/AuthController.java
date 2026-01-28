package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asupg.asupgservice.model.UserDTO;
import org.asupg.asupgservice.model.request.LoginRequest;
import org.asupg.asupgservice.model.request.RegisterUserRequest;
import org.asupg.asupgservice.model.response.LoginResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Authentication endpoints")
public interface AuthController {

    @Operation(
            summary = "Login", description = "Generates JWT token"
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
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);

    @Operation(summary = "Register", description = "Register a new user", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(
                            schema = @Schema(implementation = UserDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "Unauthorized to create new user",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 403,
                                        "error": "Forbidden",
                                        "message": "Access Denied",
                                        "path": "/api/asupg-service/v1/auth/register"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(responseCode = "409", description = "Specified username already exists",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 409,
                                        "error": "Validation failed",
                                        "message": "User with username: user1 already exists",
                                        "path": "/api/asupg-service/v1/auth/register"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<UserDTO> register(RegisterUserRequest registerUserRequest);

}
