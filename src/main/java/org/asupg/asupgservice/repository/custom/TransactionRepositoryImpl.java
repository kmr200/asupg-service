package org.asupg.asupgservice.repository.custom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;
import org.asupg.asupgservice.util.PaginationUtil;
import org.bson.types.Decimal128;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    private static final String DATE_FIELD = "date";
    private static final String AMOUNT_FIELD = "amount";
    private static final String TRANSACTION_TYPE_FIELD = "transactionType";
    private static final String RECONCILIATION_STATUS_FIELD = "reconciliation.status";
    private static final String COUNTERPARTY_INN_FIELD = "counterpartyInn";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String COUNTERPARTY_NAME = "counterpartyName";

    private final MongoTemplate mongoTemplate;
    private final PaginationUtil paginationUtil;

    public MongoPageResponse<TransactionDTO> findTransactions(
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
        Query query = new Query();
        Map<String, Criteria> criteriaMap = new HashMap<>();

        TransactionSearchRequest.SortBy effectiveSortBy =
                sortBy != null ? sortBy : TransactionSearchRequest.SortBy.DATE;
        String sortField = effectiveSortBy.getValue(); // move this up

        // Date range
        if (fromDate != null) {
            criteriaMap.merge(DATE_FIELD, Criteria.where(DATE_FIELD).gte(fromDate),
                    (existing, update) -> existing.gte(fromDate));
        }
        if (toDate != null) {
            criteriaMap.merge(DATE_FIELD, Criteria.where(DATE_FIELD).lte(toDate),
                    (existing, update) -> existing.lte(toDate));
        }

        // Amount range
        if (minAmount != null) {
            criteriaMap.merge(AMOUNT_FIELD, Criteria.where(AMOUNT_FIELD).gte(Decimal128.parse(minAmount.toPlainString())),
                    (existing, update) -> existing.gte(Decimal128.parse(minAmount.toPlainString())));
        }
        if (maxAmount != null) {
            criteriaMap.merge(AMOUNT_FIELD, Criteria.where(AMOUNT_FIELD).lte(Decimal128.parse(maxAmount.toPlainString())),
                    (existing, update) -> existing.lte(Decimal128.parse(maxAmount.toPlainString())));
        }

        if (transactionType != null) {
            criteriaMap.put(TRANSACTION_TYPE_FIELD, Criteria.where(TRANSACTION_TYPE_FIELD).is(transactionType));
        }
        if (reconciliationStatus != null) {
            criteriaMap.put(RECONCILIATION_STATUS_FIELD, Criteria.where(RECONCILIATION_STATUS_FIELD).is(reconciliationStatus));
        }

        if (search != null && !search.isBlank()) {
            String escapedSearch = Pattern.quote(search.trim());
            Pattern searchPattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where(COUNTERPARTY_INN_FIELD).regex(searchPattern),
                    Criteria.where(DESCRIPTION_FIELD).regex(searchPattern),
                    Criteria.where(COUNTERPARTY_NAME).regex(searchPattern)
            ));
        }

        criteriaMap.merge(sortField, Criteria.where(sortField).ne(null),
                (existing, update) -> existing.ne(null));

        criteriaMap.values().forEach(query::addCriteria);

        Sort.Direction direction = sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.with(Sort.by(direction, sortField, "_id"));
        query.limit(limit + 1);
        paginationUtil.applyCursor(query, cursor, effectiveSortBy, direction);

        List<TransactionDTO> results = mongoTemplate.find(query, TransactionDTO.class);
        return paginationUtil.buildPage(results, limit, effectiveSortBy, TransactionDTO::getTransactionId);
    }

}
