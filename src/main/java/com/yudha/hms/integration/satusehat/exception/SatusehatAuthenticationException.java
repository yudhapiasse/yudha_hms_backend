package com.yudha.hms.integration.satusehat.exception;

/**
 * Exception for SATUSEHAT OAuth2 authentication failures.
 *
 * Thrown when:
 * - Client credentials are invalid
 * - Token request fails
 * - Token has expired and refresh failed
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class SatusehatAuthenticationException extends SatusehatIntegrationException {

    public SatusehatAuthenticationException(String message) {
        super(message, "SATUSEHAT_AUTH_ERROR", 401);
    }

    public SatusehatAuthenticationException(String message, Throwable cause) {
        super(message, "SATUSEHAT_AUTH_ERROR", 401, cause);
    }

    public SatusehatAuthenticationException(String message, String errorCode) {
        super(message, errorCode, 401);
    }
}
