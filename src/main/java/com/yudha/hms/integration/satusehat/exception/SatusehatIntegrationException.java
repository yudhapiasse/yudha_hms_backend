package com.yudha.hms.integration.satusehat.exception;

import lombok.Getter;

/**
 * Base exception for SATUSEHAT integration errors.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Getter
public class SatusehatIntegrationException extends RuntimeException {

    private final String errorCode;
    private final Integer httpStatus;

    public SatusehatIntegrationException(String message) {
        super(message);
        this.errorCode = "SATUSEHAT_ERROR";
        this.httpStatus = null;
    }

    public SatusehatIntegrationException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = null;
    }

    public SatusehatIntegrationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "SATUSEHAT_ERROR";
        this.httpStatus = null;
    }

    public SatusehatIntegrationException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = null;
    }

    public SatusehatIntegrationException(String message, String errorCode, Integer httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public SatusehatIntegrationException(String message, String errorCode, Integer httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
