package org.asupg.asupgservice.api.impl;

import org.asupg.asupgservice.api.TransactionController;
import org.asupg.asupgservice.model.TransactionDTO;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;
import org.asupg.asupgservice.model.response.TransactionSearchResponse;
import org.asupg.asupgservice.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<TransactionSearchResponse> getCompanies(
            @RequestBody @Validated TransactionSearchRequest transactionSearchRequest
    ) {
        TransactionSearchResponse response = transactionService.getCompanies(
                transactionSearchRequest.getFromDate(),
                transactionSearchRequest.getToDate(),
                transactionSearchRequest.getMinAmount(),
                transactionSearchRequest.getMaxAmount(),
                transactionSearchRequest.getTransactionType(),
                transactionSearchRequest.getReconciliationStatus(),
                transactionSearchRequest.getLimit(),
                transactionSearchRequest.getCursor(),
                transactionSearchRequest.getSortBy(),
                transactionSearchRequest.getSortOrder(),
                transactionSearchRequest.getSearch()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
