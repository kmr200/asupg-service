package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asupg.asupgservice.model.TransactionDTO;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;
import org.asupg.asupgservice.model.response.TransactionSearchResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Transaction endpoints")
public interface TransactionController {

    @Operation(
            summary = "Get transaction", description = "Retrieves a transaction by its transactionId", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionDTO.class)
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
                                        "error": "Invalid transaction id",
                                        "message": "Transaction with id: transactionId not found",
                                        "path": "/api/asupg-service/v1/transactions/transactionId"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<TransactionDTO> getTransaction(String id);

    @Operation(
            summary = "Get transactions", description = "Retrieves a list of transactions with filtering and pagination", security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TransactionSearchResponse.class)
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
                                        "error": "Invalid transaction id",
                                        "message": "Transaction with id: transactionId not found",
                                        "path": "/api/asupg-service/v1/transactions/transactionId"
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<TransactionSearchResponse> getCompanies(TransactionSearchRequest transactionSearchRequest);

}
