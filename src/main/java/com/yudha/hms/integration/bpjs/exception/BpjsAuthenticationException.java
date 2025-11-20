package com.yudha.hms.integration.bpjs.exception;

/**
 * Exception for BPJS authentication errors.
 *
 * Thrown when:
 * - Configuration is incomplete or invalid
 * - Signature generation fails
 * - Timestamp generation fails
 * - Authentication headers cannot be created
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class BpjsAuthenticationException extends BpjsIntegrationException {

    public BpjsAuthenticationException(String message) {
        super(message);
    }

    public BpjsAuthenticationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public BpjsAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BpjsAuthenticationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
