package com.yudha.hms.integration.eklaim.exception;

/**
 * Exception for E-Klaim HTTP communication errors.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class EklaimHttpException extends EklaimIntegrationException {

    private final int statusCode;

    public EklaimHttpException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public EklaimHttpException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
