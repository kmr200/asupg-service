package org.asupg.asupgservice.repository.custom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.CompanyStatus;
import org.asupg.asupgservice.model.MongoPageResponse;
import org.asupg.asupgservice.model.SortOrder;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.util.PaginationUtil;
import org.bson.types.Decimal128;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

        // currentBalance < 0 is the base condition
        Criteria balanceCriteria = Criteria.where(CURRENT_BALANCE_FIELD).lt(Decimal128.parse(BigDecimal.ZERO.toPlainString()));
        if (minBalance != null) balanceCriteria = balanceCriteria.gte(Decimal128.parse(minBalance.toPlainString()));
        if (maxBalance != null) balanceCriteria = balanceCriteria.lt(Decimal128.parse(maxBalance.toPlainString()));
        criteriaMap.put(CURRENT_BALANCE_FIELD, balanceCriteria);

        criteriaMap.values().forEach(query::addCriteria);

        if (search != null && !search.isBlank()) {
            String escapedSearch = Pattern.quote(search.trim());
            Pattern searchPattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where(INN_FIELD).regex(searchPattern),
                    Criteria.where(NAME_FIELD).regex(searchPattern)
            ));
        }

        Sort.Direction direction =
                sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        CompanySearchRequest.SortBy sortBy = CompanySearchRequest.SortBy.CURRENT_BALANCE;

        query.with(Sort.by(direction, sortBy.getValue(), "_id"));
        query.limit(limit + 1);

        paginationUtil.applyCursor(query, cursor, sortBy, direction);

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

        // Balance range
        Criteria balanceCriteria = new Criteria(CURRENT_BALANCE_FIELD);
        if (minBalance != null) balanceCriteria = balanceCriteria.gte(Decimal128.parse(minBalance.toPlainString()));
        if (maxBalance != null) balanceCriteria = balanceCriteria.lte(Decimal128.parse(maxBalance.toPlainString()));
        if (minBalance != null || maxBalance != null) criteriaMap.put(CURRENT_BALANCE_FIELD, balanceCriteria);

        if (status != null) {
            criteriaMap.put(STATUS_FIELD, Criteria.where(STATUS_FIELD).is(status));
        }

        if (search != null && !search.isBlank()) {
            String escapedSearch = Pattern.quote(search.trim());
            Pattern searchPattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where(INN_FIELD).regex(searchPattern),
                    Criteria.where(NAME_FIELD).regex(searchPattern)
            ));
        }

        CompanySearchRequest.SortBy effectiveSortBy =
                sortBy != null ? sortBy : CompanySearchRequest.SortBy.NAME;

        String sortField = effectiveSortBy.getValue();

        if (criteriaMap.containsKey(sortField)) {
            criteriaMap.put(sortField, criteriaMap.get(sortField).ne(null));
        } else {
            criteriaMap.put(sortField, Criteria.where(sortField).ne(null));
        }

        criteriaMap.values().forEach(query::addCriteria);

        Sort.Direction direction =
                sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        query.with(Sort.by(direction, sortField, "_id"));
        query.limit(limit + 1);

        paginationUtil.applyCursor(query, cursor, effectiveSortBy, direction);

        List<CompanyDTO> results = mongoTemplate.find(query, CompanyDTO.class);
        return paginationUtil.buildPage(results, limit, effectiveSortBy, CompanyDTO::getInn);
    }

}
