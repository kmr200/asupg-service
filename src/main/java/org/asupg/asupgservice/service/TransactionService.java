package org.asupg.asupgservice.service;

import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.MongoPageResponse;
import org.asupg.asupgservice.model.ReconciliationStatus;
import org.asupg.asupgservice.model.SortOrder;
import org.asupg.asupgservice.model.TransactionDTO;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;
import org.asupg.asupgservice.model.response.TransactionSearchResponse;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionDTO getTransactionById(String id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new AppException(404, "Invalid transaction id", "Transaction with id: " + id + " not found")
        );
    }

    public TransactionSearchResponse getCompanies(
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            TransactionDTO.TransactionType transactionType,
            ReconciliationStatus reconciliationStatus,
            Integer limit,
            String cursor,
            TransactionSearchRequest.SortBy sortBy,
            SortOrder sortOrder,
            String search
    ) {

        MongoPageResponse<TransactionDTO> page;

        page = transactionRepository.findTransactions(
                fromDate,
                toDate,
                minAmount,
                maxAmount,
                transactionType,
                reconciliationStatus,
                limit,
                cursor,
                sortBy,
                sortOrder,
                search
        );

        return new TransactionSearchResponse(page.getItems(), page.getNextCursor());
    }
}
