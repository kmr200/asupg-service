package org.asupg.asupgservice.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Response class for generic error messages")
public class ErrorResponse {

    @Schema(description = "When the exception error was thrown")
    private LocalDateTime timestamp;

    @Schema(description = "Response status", example = "400")
    private int status;

    @Schema(description = "Short error description", example = "Bad request")
    private String error;

    @Schema(description = "Error message", example = "Validation failed. Invalid request body")
    private String message;

    @Schema(description = "Request URI from where the request was initiated", example = "/v1/companies")
    private String path;

    @Schema(description = "List of validation errors if any")
    private List<FieldError> validationErrors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldError {

        @Schema(description = "Field name which failed validation", example = "companyInn")
        private String field;

        @Schema(description = "Validation message", example = "INN field must be 9 characters long")
        private String message;
    }
}