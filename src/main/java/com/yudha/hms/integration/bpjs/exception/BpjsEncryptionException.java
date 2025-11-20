package com.yudha.hms.integration.bpjs.exception;

/**
 * Exception for BPJS encryption/decryption errors.
 *
 * Thrown when:
 * - AES decryption fails
 * - LZ-String decompression fails
 * - Encryption configuration is invalid
 * - Data format is incorrect
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class BpjsEncryptionException extends BpjsIntegrationException {

    public BpjsEncryptionException(String message) {
        super(message);
    }

    public BpjsEncryptionException(String message, String errorCode) {
        super(message, errorCode);
    }

    public BpjsEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BpjsEncryptionException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
