package org.asupg.asupgservice.model.request;

import java.util.Arrays;
import java.util.function.Function;

public interface SortableField<T> {

    String name();

    String getMongoField();

    Function<T, Object> getExtractor();

    Function<String, Object> getParser();

    default String getValue() {
        return getMongoField();
    }

    static <T, E extends Enum<E> & SortableField<T>> E fromValue(String value, Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(s -> s.getMongoField().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported sort field: " + value));
    }

}
