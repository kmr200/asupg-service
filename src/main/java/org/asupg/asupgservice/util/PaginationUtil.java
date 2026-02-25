package org.asupg.asupgservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.CursorPayload;
import org.asupg.asupgservice.model.MongoPageResponse;
import org.asupg.asupgservice.model.request.SortableField;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PaginationUtil {

    private final ObjectMapper objectMapper;

    public <T> void applyCursor(
            Query query,
            String cursor,
            SortableField<T> sortBy,
            Sort.Direction direction
    ) {
        if (cursor == null || cursor.isBlank()) return;

        CursorPayload payload = decodeCursor(cursor);

        if (!sortBy.name().equals(payload.sortBy())) {
            throw new IllegalArgumentException("Cursor sort field mismatch");
        }

        Object sortValue = sortBy.getParser().apply(payload.sortValue());
        String id = payload.id();

        Criteria cursorCriteria = direction == Sort.Direction.ASC
                ? Criteria.where(sortBy.getValue()).gt(sortValue)
                : Criteria.where(sortBy.getValue()).lt(sortValue);

        Criteria tieBreaker = new Criteria().andOperator(
                Criteria.where(sortBy.getValue()).is(sortValue),
                direction == Sort.Direction.ASC
                        ? Criteria.where("_id").gt(id)
                        : Criteria.where("_id").lt(id)
        );

        // Instead of adding orOperator directly (null key collision),
        // append to existing query criteria using $and
        Document existingCriteriaObject = query.getQueryObject();
        Document cursorDoc = new Criteria().orOperator(cursorCriteria, tieBreaker)
                .getCriteriaObject();

        // Merge using $and
        List<Document> andClauses = new ArrayList<>();
        andClauses.add(existingCriteriaObject);
        andClauses.add(cursorDoc);

        Query newQuery = new Query(new Criteria("$and").is(andClauses));
        // copy sort and limit back
        query.getQueryObject().clear();
        query.getQueryObject().putAll(new Document("$and", andClauses));
    }

    public <T> MongoPageResponse<T> buildPage(
            List<T> results,
            int limit,
            SortableField<T> sortBy,
            Function<T, String> idExtractor
    ) {
        boolean hasNext = results.size() > limit;
        if (hasNext) results.remove(limit);

        String nextCursor = null;
        if (hasNext) {
            T last = results.getLast();
            Object sortValue = sortBy.getExtractor().apply(last);
            nextCursor = encodeCursor(sortBy, sortValue, idExtractor.apply(last));
        }

        return new MongoPageResponse<>(results, nextCursor);
    }

    private <T> String encodeCursor(
            SortableField<T> sortBy,
            Object sortValue,
            String id
    ) {
        CursorPayload payload = new CursorPayload(sortBy.name(), sortValue.toString(), id);
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
            throw new AppException(400, "Validation failed", "Invalid cursor");
        }
    }
}