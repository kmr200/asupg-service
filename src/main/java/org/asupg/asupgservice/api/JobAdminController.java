package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.YearMonth;

@Tag(name = "Job endponts")
public interface JobAdminController {

    @Operation(summary = "Trigger monthly charge job", description = "Triggers monthly charge job in workers service", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json"
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
                                        "path": "/api/asupg-service/v1/jobs/monthly-charge"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> triggerMonthlyCharge(YearMonth month);

    @Operation(summary = "Trigger report-ingestion job", description = "Triggers report ingestion job in workers service", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json"
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
                                        "path": "/api/asupg-service/v1/jobs/report-ingestion"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> triggerReportIngestion(LocalDate from, LocalDate to);

}
