package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asupg.asupgservice.model.response.DashboardResponse;
import org.asupg.asupgservice.model.response.TotalDebt;
import org.springframework.http.ResponseEntity;

@Tag(name = "Report endpoints")
public interface ReportController {

    @Operation(
            summary = "Get total debt", description = "Retrieves total debt of companies", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TotalDebt.class)
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
            )
    })
    ResponseEntity<TotalDebt> getCompaniesTotalDebt();

    @Operation(
            summary = "Get overview", description = "Retrieves overview of summaries", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DashboardResponse.class)
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
            )
    })
    public ResponseEntity<DashboardResponse> getOverview();

}
