package com.yudha.hms.integration.eklaim.exception;

/**
 * Exception for E-Klaim data validation errors.
 *
 * Error codes:
 * - E2007: Required field missing
 * - E2008: Invalid ICD-10 diagnosis code
 * - E2009: Invalid ICD-9-CM procedure code
 * - E2010: Duplicate diagnosis entry
 * - E2011: Duplicate procedure entry
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class EklaimValidationException extends EklaimIntegrationException {

    public EklaimValidationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public EklaimValidationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
