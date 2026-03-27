package org.asupg.asupgservice.api.impl;

import org.asupg.asupgservice.api.TransactionController;
import org.asupg.asupgservice.model.TransactionDTO;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;
import org.asupg.asupgservice.model.response.TransactionSearchResponse;
import org.asupg.asupgservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionControllerImpl implements TransactionController {

    private final TransactionService transactionService;

    public TransactionControllerImpl(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable String id) {
        TransactionDTO transaction = transactionService.getTransactionById(id);

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TransactionSearchResponse> getTransactions(
            @RequestBody @Validated TransactionSearchRequest transactionSearchRequest
    ) {
        TransactionSearchResponse response = transactionService.getTransactions(
                transactionSearchRequest.getFromDate(),
                transactionSearchRequest.getToDate(),
                transactionSearchRequest.getMinAmount(),
                transactionSearchRequest.getMaxAmount(),
                transactionSearchRequest.getTransactionType(),
                transactionSearchRequest.getReconciliationStatus(),
                transactionSearchRequest.getInn(),
                transactionSearchRequest.getLimit(),
                transactionSearchRequest.getCursor(),
                transactionSearchRequest.getSortBy(),
                transactionSearchRequest.getSortOrder(),
                transactionSearchRequest.getSearch()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{id}/company")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionDTO> reassignTransaction(
            @PathVariable String id,
            @RequestParam String companyInn,
            @AuthenticationPrincipal String username
    ) {
        TransactionDTO updatedTransaction = transactionService.reassignTransaction(
                id,
                companyInn,
                username
        );

        return new ResponseEntity<>(updatedTransaction, HttpStatus.OK);
    }

}
