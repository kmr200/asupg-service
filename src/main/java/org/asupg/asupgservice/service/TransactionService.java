package org.asupg.asupgservice.service;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;
import org.asupg.asupgservice.model.response.TransactionSearchResponse;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final CompanyRepository companyRepository;

    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id).orElseThrow(
                () -> new AppException(404, "Неверный идентификатор транзакции", "Транзакция с id: " + id + " не найдена")
        );
    }

    public TransactionSearchResponse getTransactions(
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
    ) {

        MongoPageResponse<Transaction> page;

        page = transactionRepository.findTransactions(
                fromDate,
                toDate,
                minAmount,
                maxAmount,
                transactionType,
                reconciliationStatus,
                counterpartyInn,
                limit,
                cursor,
                sortBy,
                sortOrder,
                search
        );

        return new TransactionSearchResponse(page.getItems(), page.getNextCursor());
    }

    @Transactional
    public Transaction reassignTransaction(String id, String companyInn, String username) {
        Transaction transaction = getTransactionById(id);
        String oldCompanyInn = transaction.getCounterpartyInn();

        if (companyInn.equals(oldCompanyInn)) {
            throw new AppException(400, "Ошибка валидации", "Транзакция уже привязана к компании с ИНН: " + companyInn);
        }

        Company newCompany = getCompany(companyInn);

        // Subtract from old company if previously assigned
        if (transaction.getReconciliation().getStatus() == ReconciliationStatus.MATCHED) {
            Company oldCompany = getCompany(oldCompanyInn);
            oldCompany.setCurrentBalance(oldCompany.getCurrentBalance().subtract(transaction.getAmount()));
            companyRepository.save(oldCompany);
            logger.info("Subtracted {} from company {}", oldCompanyInn, transaction.getAmount());
        }

        newCompany.setCurrentBalance(newCompany.getCurrentBalance().add(transaction.getAmount()));
        companyRepository.save(newCompany);
        logger.info("Added {} to company {}", companyInn, transaction.getAmount());

        // Update transaction
        transaction.setCounterpartyInn(companyInn);
        transaction.setCounterpartyName(newCompany.getName());

        Reconciliation reconciliation = transaction.getReconciliation();
        reconciliation.setStatus(ReconciliationStatus.MANUALLY_FIXED);
        reconciliation.setManual(true);
        reconciliation.setUpdatedBy(username);
        reconciliation.setUpdatedAt(LocalDateTime.now());
        transaction.setReconciliation(reconciliation);

        Transaction updated = transactionRepository.save(transaction);

        logger.info("Transaction {} reassigned from {} to {} by {}",
            id, oldCompanyInn, companyInn, username
        );

        return updated;
    }

    private Company getCompany(String id) {
        logger.debug("Get company with id {}", id);
        return companyRepository.findById(id).orElseThrow(
                () -> new AppException(404, "Ошибка валидации", "Компания с id: " + id + " не найдена")
        );
    }
}
