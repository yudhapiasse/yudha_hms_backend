package com.yudha.hms.integration.eklaim.exception;

/**
 * Exception for E-Klaim authentication errors.
 *
 * Error codes:
 * - E2003: Consumer ID not found or inactive
 * - E2004: User key authentication failed
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class EklaimAuthenticationException extends EklaimIntegrationException {

    public EklaimAuthenticationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public EklaimAuthenticationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
