package org.asupg.asupgservice.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Repository
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public CompanyRepositoryImpl(MongoTemplate mongoTemplate, ObjectMapper objectMapper) {
        this.mongoTemplate = mongoTemplate;
        this.objectMapper = objectMapper;
    }

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

        query.addCriteria(Criteria.where("currentBalance").lt(BigDecimal.ZERO));

        if (minBalance != null) {
            query.addCriteria(Criteria.where("currentBalance").gte(minBalance));
        }
        if (maxBalance != null) {
            query.addCriteria(Criteria.where("currentBalance").lt(maxBalance));
        }

        if (search != null && !search.isBlank()) {
            String escapedSearch = Pattern.quote(search.trim());
            Pattern searchPattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("inn").regex(searchPattern),
                    Criteria.where("name").regex(searchPattern)
            ));
        }

        Sort.Direction direction =
                sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        CompanySearchRequest.SortBy sortBy = CompanySearchRequest.SortBy.CURRENT_BALANCE;

        query.with(Sort.by(direction, sortBy.getValue(), "_id"));
        query.limit(limit + 1);

        applyCursor(query, cursor, sortBy, direction);

        List<CompanyDTO> results = mongoTemplate.find(query, CompanyDTO.class);

        return buildPage(results, limit, sortBy);
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

        if (minBalance != null)
            query.addCriteria(Criteria.where("currentBalance").gte(minBalance));
        if (maxBalance != null)
            query.addCriteria(Criteria.where("currentBalance").lte(maxBalance));
        if (status != null)
            query.addCriteria(Criteria.where("status").is(status));

        if (search != null && !search.isBlank()) {
            String escapedSearch = Pattern.quote(search.trim());
            Pattern searchPattern = Pattern.compile(escapedSearch, Pattern.CASE_INSENSITIVE);
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("inn").regex(searchPattern),
                    Criteria.where("name").regex(searchPattern)
            ));
        }

        CompanySearchRequest.SortBy effectiveSortBy =
                sortBy != null ? sortBy : CompanySearchRequest.SortBy.NAME;

        Sort.Direction direction =
                sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        query.addCriteria(
                Criteria.where(effectiveSortBy.getValue()).ne(null)
        );

        query.with(Sort.by(direction, effectiveSortBy.getValue(), "_id"));
        query.limit(limit + 1);

        applyCursor(query, cursor, effectiveSortBy, direction);

        List<CompanyDTO> results = mongoTemplate.find(query, CompanyDTO.class);

        return buildPage(results, limit, effectiveSortBy);

    }

    private void applyCursor(
            Query query,
            String cursor,
            CompanySearchRequest.SortBy sortBy,
            Sort.Direction direction
    ) {
        if (cursor == null || cursor.isBlank()) return;

        CursorPayload payload = decodeCursor(cursor);

        CompanySearchRequest.SortBy cursorSortBy =
                CompanySearchRequest.SortBy.valueOf(payload.sortBy());

        if (cursorSortBy != sortBy) {
            throw new IllegalArgumentException("Cursor sort field mismatch");
        }

        Object sortValue = cursorSortBy.getParser().apply(payload.sortValue());
        String id = payload.id();

        Criteria cursorCriteria = new Criteria().orOperator(
                direction == Sort.Direction.ASC
                        ? Criteria.where(sortBy.getValue()).gt(sortValue)
                        : Criteria.where(sortBy.getValue()).lt(sortValue),

                new Criteria().andOperator(
                        Criteria.where(sortBy.getValue()).is(sortValue),
                        direction == Sort.Direction.ASC
                                ? Criteria.where("_id").gt(id)
                                : Criteria.where("_id").lt(id)
                )
        );

        query.addCriteria(cursorCriteria);
    }

    private MongoPageResponse<CompanyDTO> buildPage(
            List<CompanyDTO> results,
            int limit,
            CompanySearchRequest.SortBy sortBy
    ) {
        boolean hasNext = results.size() > limit;

        if (hasNext) {
            results.remove(limit);
        }

        String nextCursor = null;
        if (hasNext) {
            CompanyDTO last = results.getLast();
            Object sortValue = sortBy.getExtractor().apply(last);

            nextCursor = encodeCursor(
                    sortBy,
                    sortValue,
                    last.getInn()
            );
        }

        return new MongoPageResponse<>(results, nextCursor);
    }

    private String encodeCursor(
            CompanySearchRequest.SortBy sortBy,
            Object sortValue,
            String id
    ) {
        CursorPayload payload = new CursorPayload(
                sortBy.name(),
                sortValue.toString(),
                id
        );

        try {
            String json = objectMapper.writeValueAsString(payload);
            return Base64.getUrlEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to encode cursor", e);
        }
    }

    private CursorPayload decodeCursor(String cursor) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);
            return objectMapper.readValue(decoded, CursorPayload.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor", e);
        }
    }

}
