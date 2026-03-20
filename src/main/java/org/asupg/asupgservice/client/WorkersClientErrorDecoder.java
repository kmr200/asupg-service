package org.asupg.asupgservice.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.response.ErrorResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WorkersClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final ObjectMapper objectMapper;

    public WorkersClientErrorDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String body = null;
        try {
            if (response.body() != null) {
                body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new AppException(response.status(), "Failed to read error response", e.getMessage());
        }

        // Try to parse as your standard error format
        if (body != null) {
            try {
                ErrorResponse error = objectMapper.readValue(body, ErrorResponse.class);
                throw new AppException(response.status(), error.getError(), error.getMessage());
            } catch (JsonProcessingException ignored) {
                // body is not your standard error format, fall through
            }
        }

        throw new AppException(response.status(), "Upstream error", body);
    }

}
