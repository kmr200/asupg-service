package org.asupg.asupgservice.repository.custom;

import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransactionRepositoryCustom {

    MongoPageResponse<Transaction> findTransactions(
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Transaction.TransactionType transactionType,
            ReconciliationStatus reconciliationStatus,
            String counterpartyInn,
            Integer limit,
            String cursor,
            TransactionSearchRequest.SortBy sortBy,
            SortOrder sortOrder,
            String search
    );

    TransactionDashboardResult getTransactionDashboardAggregation();

}
