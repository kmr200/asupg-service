package org.asupg.asupgservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.asupg.asupgservice.model.Transaction;
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
                            schema = @Schema(implementation = Transaction.class)
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
    ResponseEntity<Transaction> getTransaction(String id);

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
    ResponseEntity<TransactionSearchResponse> getTransactions(TransactionSearchRequest transactionSearchRequest);

    @Operation(
            summary = "Reassign transaction to a different company",
            description = """
                    Reassigns a transaction to a different company.
                    Automatically adjusts balances:
                    subtracts the amount from the previously assigned company (if any)
                    and adds it to the new one.
                    Sets isManual=true and records updatedBy and updatedAt for audit purposes.
                    """,
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Transaction ID",
                            required = true,
                            example = "a3f1b2c3d4e5f6a7b8c9d0e1a2b3c4d5e6f7a8b9c0d1e2f3a4b5c6d7e8f9a0b1"
                    ),
                    @Parameter(
                            name = "companyInn",
                            description = "INN of the company to assign the transaction to",
                            required = true,
                            example = "123456789"
                    )
            },
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 404,
                                "error": "Not Found",
                                "message": "Транзакция не найдена",
                                "path": "/api/asupg-service/v1/transactions/{transactionId}/company"
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
                                "path": "/api/asupg-service/v1/transactions/{transactionId}/company"
                            }
                            """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject("""
                            {
                                "timestamp": "timestamp",
                                "status": 403,
                                "error": "Forbidden",
                                "message": "Access denied",
                                "path": "/api/asupg-service/v1/transactions/{transactionId}/company"
                            }
                            """))
            )
    })
    ResponseEntity<Transaction> reassignTransaction(String id, String companyInn, String username);

}
