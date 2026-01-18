package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.request.CompanyDebtSearchRequest;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.model.request.CreateCompanyRequest;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
import org.asupg.asupgservice.model.response.CompanyDebtResponse;
import org.asupg.asupgservice.model.response.CompanySearchResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Company endpoints")
public interface CompanyController {

    @Operation(
            summary = "Create company", description = "Creates a new company", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDTO.class)
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
                                        "path": "/api/asupg-service/v1/companies",
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
                                        "path": "/api/asupg-service/v1/companies"
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
                                        "path": "/api/asupg-service/v1/companies"
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
                                        "error": "Validation failed",
                                        "message": "Company with id: {id} already exists",
                                        "path": "/api/asupg-service/v1/companies"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<CompanyDTO> createCompany(CreateCompanyRequest createCompanyRequest);

    @Operation(
            summary = "Get companies", description = "Retrieves a list of companies with filtering and pagination", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanySearchResponse.class)
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
                                        "error": "Validation failed",
                                        "message": "Invalid continuation token",
                                        "path": "/api/asupg-service/v1/companies"
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
                                        "path": "/api/asupg-service/v1/companies"
                                    }
                                    """)
                    )
            ),
    })
    ResponseEntity<CompanySearchResponse> getCompanies(CompanySearchRequest companyDebtSearchRequest);

    @Operation(
            summary = "Get company", description = "Retrieves a company by its INN", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDTO.class)
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
                                        "message": "Company with id: 123456789 not found",
                                        "path": "/api/asupg-service/v1/companies/123456789"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<CompanyDTO> getCompany(String id);

    @Operation(
            summary = "Get balance of a company", description = "Retrieves current balance of the company with a monthly breakdown", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyBalanceResponse.class)
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
                                        "message": "Company with id: 123456789 not found",
                                        "path": "/api/asupg-service/v1/companies/123456789"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<CompanyBalanceResponse> getCompanyBalance(String id);

    @Operation(
            summary = "Get companies in debt", description = "Retrieves a list of companies in debt with pagination and filtering", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CompanyDebtResponse.class)
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
                                        "error": "Validation failed",
                                        "message": "Invalid continuation token",
                                        "path": "/api/asupg-service/v1/companies"
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
                                        "path": "/api/asupg-service/v1/companies"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<CompanyDebtResponse> getCompanyDebtors(CompanyDebtSearchRequest companyDebtSearchRequest);

}
