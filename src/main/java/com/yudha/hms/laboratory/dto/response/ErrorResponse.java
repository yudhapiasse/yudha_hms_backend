package com.yudha.hms.laboratory.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Error Response DTO.
 *
 * Standardized error response for exception handling.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Error code (application-specific)
     */
    private String errorCode;

    /**
     * Error message
     */
    private String message;

    /**
     * Detailed error description
     */
    private String details;

    /**
     * Timestamp of the error
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Request path that caused the error
     */
    private String path;

    /**
     * HTTP method
     */
    private String method;

    /**
     * Validation errors (field-specific errors)
     */
    private Map<String, String> validationErrors;

    /**
     * List of error messages (for multiple errors)
     */
    private List<String> errors;

    /**
     * Stack trace (only in development mode)
     */
    private String stackTrace;

    /**
     * Create simple error response
     */
    public static ErrorResponse of(int status, String errorCode, String message) {
        return ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with details
     */
    public static ErrorResponse of(int status, String errorCode, String message, String details) {
        return ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create error response with path and method
     */
    public static ErrorResponse of(int status, String errorCode, String message, String path, String method) {
        return ErrorResponse.builder()
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .path(path)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create validation error response
     */
    public static ErrorResponse validationError(Map<String, String> validationErrors) {
        return ErrorResponse.builder()
                .status(400)
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed")
                .validationErrors(validationErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Create validation error response with path
     */
    public static ErrorResponse validationError(Map<String, String> validationErrors, String path, String method) {
        return ErrorResponse.builder()
                .status(400)
                .errorCode("VALIDATION_ERROR")
                .message("Validation failed")
                .validationErrors(validationErrors)
                .path(path)
                .method(method)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
