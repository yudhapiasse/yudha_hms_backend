package com.yudha.hms.radiology.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API Response DTO.
 *
 * Generic wrapper for API responses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Success flag
     */
    private boolean success;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Timestamp
     */
    private LocalDateTime timestamp;

    /**
     * Error code (if any)
     */
    private String errorCode;

    /**
     * Create success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, LocalDateTime.now(), null);
    }

    /**
     * Create success response with custom message and data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }

    /**
     * Create success response with only message
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, LocalDateTime.now(), null);
    }

    /**
     * Create error response with message
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), null);
    }

    /**
     * Create error response with message and error code
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), errorCode);
    }

    /**
     * Create error response with message, error code, and data
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, T data) {
        return new ApiResponse<>(false, message, data, LocalDateTime.now(), errorCode);
    }
}
