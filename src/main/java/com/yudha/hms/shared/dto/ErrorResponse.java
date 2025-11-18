package com.yudha.hms.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response DTO.
 *
 * Returned for all API errors with consistent structure.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
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
     * Error type/category
     * E.g., "VALIDATION_ERROR", "NOT_FOUND", "DUPLICATE", "INTERNAL_ERROR"
     */
    private String error;

    /**
     * User-friendly error message (in Indonesian for HMS)
     */
    private String message;

    /**
     * API path where error occurred
     */
    private String path;

    /**
     * Timestamp when error occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Field-specific validation errors
     * Map of field name to error message
     * Only included for validation errors
     */
    private Map<String, String> errors;

    /**
     * Additional debug information
     * Only included in development environment
     */
    private String debugInfo;

    /**
     * Create error response for validation error
     */
    public static ErrorResponse validationError(String message, Map<String, String> errors, String path) {
        return ErrorResponse.builder()
            .status(400)
            .error("VALIDATION_ERROR")
            .message(message)
            .errors(errors)
            .path(path)
            .build();
    }

    /**
     * Create error response for not found
     */
    public static ErrorResponse notFound(String message, String path) {
        return ErrorResponse.builder()
            .status(404)
            .error("NOT_FOUND")
            .message(message)
            .path(path)
            .build();
    }

    /**
     * Create error response for duplicate resource
     */
    public static ErrorResponse duplicate(String message, String path) {
        return ErrorResponse.builder()
            .status(409)
            .error("DUPLICATE")
            .message(message)
            .path(path)
            .build();
    }

    /**
     * Create error response for internal server error
     */
    public static ErrorResponse internalError(String message, String path) {
        return ErrorResponse.builder()
            .status(500)
            .error("INTERNAL_ERROR")
            .message(message)
            .path(path)
            .build();
    }
}