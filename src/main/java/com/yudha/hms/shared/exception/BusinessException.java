package com.yudha.hms.shared.exception;

/**
 * Exception thrown for business rule violations.
 * Used when business logic constraints are not met.
 *
 * Examples:
 * - Patient already has active admission
 * - No available beds in requested room class
 * - Insufficient deposit paid
 * - Cannot discharge inactive admission
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String format, Object... args) {
        super(String.format(format, args));
    }
}
