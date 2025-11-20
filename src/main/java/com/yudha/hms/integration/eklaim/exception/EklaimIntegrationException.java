package com.yudha.hms.integration.eklaim.exception;

/**
 * Base exception for all E-Klaim integration errors.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class EklaimIntegrationException extends RuntimeException {

    private final String errorCode;

    public EklaimIntegrationException(String message) {
        super(message);
        this.errorCode = null;
    }

    public EklaimIntegrationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public EklaimIntegrationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public EklaimIntegrationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
