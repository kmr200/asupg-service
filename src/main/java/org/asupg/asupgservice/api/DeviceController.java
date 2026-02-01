package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asupg.asupgservice.model.DeviceDTO;
import org.asupg.asupgservice.model.request.CreateDeviceRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "Device endpoints")
public interface DeviceController {

    @Operation(summary = "Get device", description = "Retrieves a device by its ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeviceDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 401,
                                        "error": "Authentication failed",
                                        "message": "Invalid or expired JWT token",
                                        "path": "/api/asupg-service/v1/companies"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 404,
                                        "error": "Validation failed",
                                        "message": "Device with id: 123456789 not found",
                                        "path": "/api/asupg-service/v1/devices/123456789"
                                    }
                                    """)
                    )
            )
    })
   ResponseEntity<DeviceDTO> getDevice(String id);

    @Operation(summary = "Create device", description = "Creates a device", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeviceDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 400,
                                        "error": "Validation Failed",
                                        "message": "Invalid input data. Please check the fields.",
                                        "path": "/api/asupg-service/v1/devices",
                                        "validationErrors": [
                                            {
                                                "field": "field name",
                                                "message": "error description"
                                            }
                                        ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 401,
                                        "error": "Authentication failed",
                                        "message": "Invalid or expired JWT token",
                                        "path": "/api/asupg-service/v1/devices"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 403,
                                        "error": "Forbidden",
                                        "message": "Access Denied",
                                        "path": "/api/asupg-service/v1/devices"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject("""
                                    {
                                        "timestamp": "timestamp",
                                        "status": 409,
                                        "error": "Conflict",
                                        "message": "Device with id: {id} already exists",
                                        "path": "/api/asupg-service/v1/devices"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<DeviceDTO> createDevice(CreateDeviceRequest createDeviceRequest);

}
