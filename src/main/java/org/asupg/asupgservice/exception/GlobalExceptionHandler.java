package org.asupg.asupgservice.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.servlet.http.HttpServletRequest;
import org.asupg.asupgservice.model.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleException(
            AppException e,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(e.getStatusCode())
                        .error(e.getError())
                        .message(e.getMessage())
                        .path(request.getRequestURI())
                        .build(),
                HttpStatus.valueOf(e.getStatusCode())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        logger.error("Validation error occurred: {}", ex.getMessage());

        List<ErrorResponse.FieldError> validationErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.add(new ErrorResponse.FieldError(fieldName, errorMessage));
            logger.debug("Field: {}, Error: {}", fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid input data. Please check the fields.")
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        logger.error("JSON parse error: {}", ex.getMessage());

        String message = "Invalid request body. Please check your input.";
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidDefinitionException || cause instanceof MismatchedInputException) {
            message = ((JsonMappingException) cause).getOriginalMessage();
        }

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Malformed JSON")
                        .message(message)
                        .path(request.getRequestURI())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AuthorizationDeniedException e,
            HttpServletRequest request
    ) {
        logger.error("Access denied: {}", e.getMessage());

        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(403)
                        .error("Forbidden")
                        .message("Access Denied")
                        .path(request.getRequestURI())
                        .build(),
                HttpStatus.FORBIDDEN
        );
    }

}
