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
import org.asupg.asupgservice.model.request.UpdateUserRequest;
import org.asupg.asupgservice.model.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 404,
                                        "error": "Validation failed",
                                        "message": "User with username: username not found",
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

    @Operation(
            summary = "Get all users", description = "Retrieve list of all users"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users",
                    content = @Content(
                            mediaType = "application/json"
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
    public ResponseEntity<List<UserDTO>> getAllUsers();

    @Operation(
            summary = "Get user", description = "Retrieve user by its username"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
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
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 404,
                                        "error": "Validation failed",
                                        "message": "User with username: username not found",
                                        "path": "/api/asupg-service/v1/auth/username"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<UserDTO> getUser(String username);

    @Operation(
            summary = "Delete user", description = "Deletes user from the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
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
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 404,
                                        "error": "Validation failed",
                                        "message": "User with username: username not found",
                                        "path": "/api/asupg-service/v1/auth/username"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<UserDTO> deleteUser(String username);

    @Operation(
            summary = "Update user", description = "Updates users profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token generated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class)
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
            ),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 404,
                                        "error": "Validation failed",
                                        "message": "User with username: username not found",
                                        "path": "/api/asupg-service/v1/auth/username"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<UserDTO> updateUser(String username, UpdateUserRequest updateUserRequest);

}
