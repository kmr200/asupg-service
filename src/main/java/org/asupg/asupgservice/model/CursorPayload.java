package org.asupg.asupgservice.model;

public record CursorPayload(
        String sortBy,
        String sortValue,
        String id
) {
}
