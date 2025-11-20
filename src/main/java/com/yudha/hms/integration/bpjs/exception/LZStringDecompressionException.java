package com.yudha.hms.integration.bpjs.exception;

/**
 * Exception for LZ-String decompression errors.
 *
 * Thrown when:
 * - Compressed data is corrupted
 * - Data format is invalid
 * - Decompression algorithm fails
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class LZStringDecompressionException extends BpjsEncryptionException {

    public LZStringDecompressionException(String message) {
        super(message);
    }

    public LZStringDecompressionException(String message, String errorCode) {
        super(message, errorCode);
    }

    public LZStringDecompressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public LZStringDecompressionException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
