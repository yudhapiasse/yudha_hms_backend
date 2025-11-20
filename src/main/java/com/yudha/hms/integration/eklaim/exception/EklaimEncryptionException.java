package com.yudha.hms.integration.eklaim.exception;

/**
 * Exception for E-Klaim encryption/decryption errors.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class EklaimEncryptionException extends EklaimIntegrationException {

    public EklaimEncryptionException(String message) {
        super(message, "E2001");
    }

    public EklaimEncryptionException(String message, Throwable cause) {
        super(message, "E2001", cause);
    }
}
