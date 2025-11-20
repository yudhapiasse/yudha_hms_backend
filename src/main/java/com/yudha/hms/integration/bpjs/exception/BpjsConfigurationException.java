package com.yudha.hms.integration.bpjs.exception;

/**
 * Exception for BPJS configuration errors.
 *
 * Thrown when:
 * - Required configuration properties are missing
 * - Configuration values are invalid
 * - BPJS integration is disabled
 * - Environment settings are incorrect
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class BpjsConfigurationException extends BpjsIntegrationException {

    public BpjsConfigurationException(String message) {
        super(message);
    }

    public BpjsConfigurationException(String message, String errorCode) {
        super(message, errorCode);
    }

    public BpjsConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BpjsConfigurationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
