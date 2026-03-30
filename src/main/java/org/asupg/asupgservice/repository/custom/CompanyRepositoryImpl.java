package org.asupg.asupgservice.repository.custom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.util.PaginationUtil;
import org.bson.types.Decimal128;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private static final String CURRENT_BALANCE_FIELD = "currentBalance";
    private static final String INN_FIELD = "inn";
    private static final String NAME_FIELD = "name";
    private static final String STATUS_FIELD = "status";

    private final MongoTemplate mongoTemplate;
    private final PaginationUtil paginationUtil;

    @Override
    public MongoPageResponse<CompanyDTO> findCompaniesInDebt(
            BigDecimal minBalance,
            BigDecimal maxBalance,
            int limit,
            String cursor,
            SortOrder sortOrder,
            String search
    ) {
        Query query = new Query();
        Map<String, Criteria> criteriaMap = new HashMap<>();

        Criteria balanceCriteria = Criteria.where(CURRENT_BALANCE_FIELD).lt(Decimal128.parse(BigDecimal.ZERO.toPlainString()));
        if (minBalance != null) balanceCriteria = balanceCriteria.gte(Decimal128.parse(minBalance.toPlainString()));
        if (maxBalance != null) balanceCriteria = balanceCriteria.lt(Decimal128.parse(maxBalance.toPlainString()));
        criteriaMap.put(CURRENT_BALANCE_FIELD, balanceCriteria);

        criteriaMap.values().forEach(query::addCriteria);

        List<Criteria> logicalCriteria = new ArrayList<>();
        applySearchCriteria(search, logicalCriteria);

        CompanySearchRequest.SortBy sortBy = CompanySearchRequest.SortBy.CURRENT_BALANCE;
        Sort.Direction direction = sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        paginationUtil.applySorting(query, sortBy.getValue(), direction, limit);
        paginationUtil.applyLogicalCriteria(query, logicalCriteria, cursor, sortBy, direction);

        List<CompanyDTO> results = mongoTemplate.find(query, CompanyDTO.class);
        return paginationUtil.buildPage(results, limit, sortBy, CompanyDTO::getInn);
    }

    @Override
    public MongoPageResponse<CompanyDTO> findCompanies(
            BigDecimal minBalance,
            BigDecimal maxBalance,
            CompanyStatus status,
            Integer limit,
            String cursor,
            CompanySearchRequest.SortBy sortBy,
            SortOrder sortOrder,
            String search
    ) {
        Query query = new Query();
        Map<String, Criteria> criteriaMap = new HashMap<>();

        Criteria balanceCriteria = new Criteria(CURRENT_BALANCE_FIELD);
        if (minBalance != null) balanceCriteria = balanceCriteria.gte(Decimal128.parse(minBalance.toPlainString()));
        if (maxBalance != null) balanceCriteria = balanceCriteria.lte(Decimal128.parse(maxBalance.toPlainString()));
        if (minBalance != null || maxBalance != null) criteriaMap.put(CURRENT_BALANCE_FIELD, balanceCriteria);
        if (status != null) criteriaMap.put(STATUS_FIELD, Criteria.where(STATUS_FIELD).is(status));

        CompanySearchRequest.SortBy effectiveSortBy = sortBy != null ? sortBy : CompanySearchRequest.SortBy.NAME;
        String sortField = effectiveSortBy.getValue();

        if (criteriaMap.containsKey(sortField)) {
            criteriaMap.put(sortField, criteriaMap.get(sortField).ne(null));
        } else {
            criteriaMap.put(sortField, Criteria.where(sortField).ne(null));
        }

        criteriaMap.values().forEach(query::addCriteria);

        List<Criteria> logicalCriteria = new ArrayList<>();
        applySearchCriteria(search, logicalCriteria);

        Sort.Direction direction = sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        paginationUtil.applySorting(query, sortField, direction, limit);
        paginationUtil.applyLogicalCriteria(query, logicalCriteria, cursor, effectiveSortBy, direction);

        List<CompanyDTO> results = mongoTemplate.find(query, CompanyDTO.class);
        return paginationUtil.buildPage(results, limit, effectiveSortBy, CompanyDTO::getInn);
    }

    @Override
    public CompanyDashboardResult getCompanyDashboardAggregation() {
        Aggregation aggregation = Aggregation.newAggregation(
                facet()
                        .and(
                                match(Criteria.where(CURRENT_BALANCE_FIELD).lt(Decimal128.POSITIVE_ZERO)),
                                group().sum(CURRENT_BALANCE_FIELD).as("result")
                        ).as("totalDebt")
                        .and(
                                match(Criteria.where(CURRENT_BALANCE_FIELD).gt(Decimal128.POSITIVE_ZERO)),
                                group().sum(CURRENT_BALANCE_FIELD).as("result")
                        ).as("totalBalance")
                        .and(
                                match(Criteria.where(CURRENT_BALANCE_FIELD).lt(Decimal128.POSITIVE_ZERO)),
                                sort(Sort.Direction.ASC, CURRENT_BALANCE_FIELD),
                                limit(10),
                                project(INN_FIELD, NAME_FIELD, CURRENT_BALANCE_FIELD).andExclude("_id")
                        ).as("topDebtors")
                        .and(
                                group()
                                        .count().as("totalCompanies")
                                        .sum(ConditionalOperators
                                                .when(Criteria.where(STATUS_FIELD).is(CompanyStatus.ACTIVE))
                                                .then(1).otherwise(0)).as("activeCompanies")
                                        .sum(ConditionalOperators
                                                .when(Criteria.where(STATUS_FIELD).is(CompanyStatus.INACTIVE))
                                                .then(1).otherwise(0)).as("inactiveCompanies")
                                        .sum("deviceCount").as("totalDevices")
                        ).as("companySummary")
        );

        return mongoTemplate.aggregate(aggregation, CompanyDTO.class, CompanyDashboardResult.class)
                .getUniqueMappedResult();
    }

    private void applySearchCriteria(String search, List<Criteria> logicalCriteria) {
        if (search == null || search.isBlank()) return;
        Pattern searchPattern = Pattern.compile(Pattern.quote(search.trim()), Pattern.CASE_INSENSITIVE);
        logicalCriteria.add(new Criteria().orOperator(
                Criteria.where(INN_FIELD).regex(searchPattern),
                Criteria.where(NAME_FIELD).regex(searchPattern)
        ));
    }

}
