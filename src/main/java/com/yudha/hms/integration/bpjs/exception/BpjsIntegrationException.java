package com.yudha.hms.integration.bpjs.exception;

/**
 * Base exception for all BPJS integration errors.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class BpjsIntegrationException extends RuntimeException {

    private final String errorCode;

    public BpjsIntegrationException(String message) {
        super(message);
        this.errorCode = null;
    }

    public BpjsIntegrationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BpjsIntegrationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public BpjsIntegrationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
