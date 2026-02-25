package org.asupg.asupgservice.repository.custom;

import org.asupg.asupgservice.model.MongoPageResponse;
import org.asupg.asupgservice.model.ReconciliationStatus;
import org.asupg.asupgservice.model.SortOrder;
import org.asupg.asupgservice.model.TransactionDTO;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepositoryCustom {

    MongoPageResponse<TransactionDTO> findTransactions(
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
    );

}
