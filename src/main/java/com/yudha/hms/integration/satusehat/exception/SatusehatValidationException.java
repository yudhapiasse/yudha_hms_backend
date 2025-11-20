package com.yudha.hms.integration.satusehat.exception;

/**
 * Exception for SATUSEHAT FHIR resource validation errors (HTTP 422).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class SatusehatValidationException extends SatusehatIntegrationException {

    public SatusehatValidationException(String message) {
        super(message, "SATUSEHAT_VALIDATION_ERROR", 422);
    }

    public SatusehatValidationException(String message, Throwable cause) {
        super(message, "SATUSEHAT_VALIDATION_ERROR", 422, cause);
    }
}
