package com.yudha.hms.shared.exception;

/**
 * File Storage Exception.
 *
 * Thrown when file storage operations fail.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}