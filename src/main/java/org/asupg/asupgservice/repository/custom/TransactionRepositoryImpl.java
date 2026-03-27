package org.asupg.asupgservice.repository.custom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.TransactionSearchRequest;
import org.asupg.asupgservice.util.PaginationUtil;
import org.bson.types.Decimal128;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

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
            String counterpartyInn,
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
        String sortField = effectiveSortBy.getValue();

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

        if (counterpartyInn != null) {
            criteriaMap.put(COUNTERPARTY_INN_FIELD, Criteria.where(COUNTERPARTY_INN_FIELD).is(counterpartyInn));
        }

        // Collect logical criteria to avoid null criteria collisions
        List<Criteria> logicalCriteria = new ArrayList<>();

        applySearchCriteria(search, logicalCriteria, counterpartyInn);

        criteriaMap.merge(sortField, Criteria.where(sortField).ne(null),
                (existing, update) -> existing.ne(null));

        criteriaMap.values().forEach(query::addCriteria);

        Sort.Direction direction = sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        paginationUtil.applySorting(query, sortField, direction, limit);
        paginationUtil.applyLogicalCriteria(query, logicalCriteria, cursor, effectiveSortBy, direction);

        List<TransactionDTO> results = mongoTemplate.find(query, TransactionDTO.class);
        return paginationUtil.buildPage(results, limit, effectiveSortBy, TransactionDTO::getTransactionId);
    }

    @Override
    public TransactionDashboardResult getTransactionDashboardAggregation() {
        Aggregation aggregation = Aggregation.newAggregation(
                facet()
                        .and(
                                match(Criteria.where(RECONCILIATION_STATUS_FIELD).exists(true).ne(null)
                                        .and(TRANSACTION_TYPE_FIELD).is(TransactionDTO.TransactionType.BANK_PAYMENT)),
                                group(RECONCILIATION_STATUS_FIELD)
                                        .count().as("count")
                                        .sum(AMOUNT_FIELD).as("totalAmount"),
                                project("count", "totalAmount").and("_id").as("status").andExclude("_id")
                        ).as("reconciliationBreakdown")
                        .and(
                                match(Criteria.where(DATE_FIELD).gte(LocalDate.now().minusMonths(6))),
                                addFields().addField("month").withValue(DateOperators.DateToString.dateOf(DATE_FIELD).toString("%Y-%m")).build(),
                                group("month")
                                        .sum(ConditionalOperators
                                                .when(Criteria.where(TRANSACTION_TYPE_FIELD).is(TransactionDTO.TransactionType.MONTHLY_CHARGE))
                                                .then(ArithmeticOperators.Multiply.valueOf("$amount").multiplyBy(-1))
                                                .otherwise(Decimal128.POSITIVE_ZERO)).as("totalCharged")
                                        .sum(ConditionalOperators
                                                .when(Criteria.where(TRANSACTION_TYPE_FIELD).is(TransactionDTO.TransactionType.BANK_PAYMENT))
                                                .then("$amount").otherwise(Decimal128.POSITIVE_ZERO)).as("totalPaid"),
                                project("totalCharged", "totalPaid").and("_id").as("month").andExclude("_id"),
                                sort(Sort.Direction.ASC, "month")
                        ).as("monthlyTrend")
        );

        return mongoTemplate.aggregate(aggregation, TransactionDTO.class, TransactionDashboardResult.class)
                .getUniqueMappedResult();
    }

    private void applySearchCriteria(String search, List<Criteria> logicalCriteria, String counterpartyInn) {
        if (search == null || search.isBlank()) return;
        Pattern searchPattern = Pattern.compile(Pattern.quote(search.trim()), Pattern.CASE_INSENSITIVE);

        List<Criteria> orCriteria = new ArrayList<>();

        if (counterpartyInn == null) {
            orCriteria.add(Criteria.where(COUNTERPARTY_INN_FIELD).regex(searchPattern));
        }

        orCriteria.add(Criteria.where(DESCRIPTION_FIELD).regex(searchPattern));
        orCriteria.add(Criteria.where(COUNTERPARTY_NAME).regex(searchPattern));

        logicalCriteria.add(new Criteria().orOperator(orCriteria.toArray(new Criteria[0])));
    }

}
