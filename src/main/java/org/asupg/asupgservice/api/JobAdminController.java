package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asupg.asupgservice.client.workers.request.BankConfigUpdateRequest;
import org.asupg.asupgservice.client.workers.response.BankConfigResponse;
import org.asupg.asupgservice.model.response.DeviceSyncResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.YearMonth;

@Tag(name = "Job endpoints")
public interface JobAdminController {

    @Operation(
            summary = "Trigger monthly charge job",
            description = """
                Triggers monthly charge job in workers service. Defaults to current month if not provided.
                Prior to charging, performs a device sync with ASUPG core — if the sync fails entirely, the charge job is aborted.
                """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "500",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                        {
                            "timestamp": "timestamp",
                            "status": 500,
                            "error": "Ошибка синхронизации",
                            "message": "Синхронизация устройств не удалась, начисление отменено",
                            "path": "/api/asupg-service/v1/jobs/monthly-charge"
                        }
                        """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                        {
                            "timestamp": "timestamp",
                            "status": 401,
                            "error": "Authentication failed",
                            "message": "Invalid or expired JWT token",
                            "path": "/api/asupg-service/v1/jobs/monthly-charge"
                        }
                        """))
            ),
            @ApiResponse(
                    responseCode = "502",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                        {
                            "timestamp": "timestamp",
                            "status": 502,
                            "error": "Bad Gateway",
                            "message": "Workers service unavailable",
                            "path": "/api/asupg-service/v1/jobs/monthly-charge"
                        }
                        """))
            )
    })
    ResponseEntity<Void> triggerMonthlyCharge(YearMonth month);

    @Operation(
            summary = "Trigger report ingestion job",
            description = "Triggers report ingestion job in workers service. Defaults to today if date not provided.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json")),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 401,
                                "error": "Authentication failed",
                                "message": "Invalid or expired JWT token",
                                "path": "/api/asupg-service/v1/jobs/report-ingestion"
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "502",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 502,
                                "error": "Bad Gateway",
                                "message": "Workers service unavailable",
                                "path": "/api/asupg-service/v1/jobs/report-ingestion"
                            }
                            """))
            )
    })
    ResponseEntity<Void> triggerReportIngestion(LocalDate date);

    @Operation(
            summary = "Get bank configuration",
            description = "Returns current bank configuration. Password is excluded. Accessible by ADMIN and USER roles.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankConfigResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 401,
                                "error": "Authentication failed",
                                "message": "Invalid or expired JWT token",
                                "path": "/api/asupg-service/v1/jobs/config"
                            }
                            """))
            )
    })
    ResponseEntity<BankConfigResponse> getBankConfig();

    @Operation(
            summary = "Update bank configuration",
            description = "Partially updates bank configuration. Only provided fields will be updated.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankConfigResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 401,
                                "error": "Authentication failed",
                                "message": "Invalid or expired JWT token",
                                "path": "/api/asupg-service/v1/jobs/config"
                            }
                            """))
            )
    })
    ResponseEntity<BankConfigResponse> updateBankConfig(BankConfigUpdateRequest request);

    @Operation(
            summary = "Get bank password",
            description = "Returns current bank API password. Accessible by ADMIN role only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("\"s3cr3t\""))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 401,
                                "error": "Authentication failed",
                                "message": "Invalid or expired JWT token",
                                "path": "/api/asupg-service/v1/jobs/config/password"
                            }
                            """))
            )
    })
    ResponseEntity<String> getBankPassword();

    @Operation(
            summary = "Get bank username",
            description = "Returns current bank API username. Accessible by ADMIN role only.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("\"demo\""))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 401,
                                "error": "Authentication failed",
                                "message": "Invalid or expired JWT token",
                                "path": "/api/asupg-service/v1/jobs/config/username"
                            }
                            """))
            )
    })
    ResponseEntity<String> getBankUsername();


    @Operation(
            summary = "Sync devices with ASUPG",
            description = "Syncs local device registry with ASUPG core. Updates device names and statuses. Devices not found in ASUPG are reported as failures.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeviceSyncResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                        {
                            "timestamp": "timestamp",
                            "status": 409,
                            "error": "Конфликт",
                            "message": "Синхронизация устройств уже выполняется",
                            "path": "/api/asupg-service/v1/jobs/device-sync"
                        }
                        """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                        {
                            "timestamp": "timestamp",
                            "status": 401,
                            "error": "Authentication failed",
                            "message": "Invalid or expired JWT token",
                            "path": "/api/asupg-service/v1/jobs/device-sync"
                        }
                        """))
            )
    })
    ResponseEntity<DeviceSyncResponse> syncDevices();
}