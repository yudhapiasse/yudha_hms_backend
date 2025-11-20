package com.yudha.hms.integration.bpjs.exception;

/**
 * Exception for BPJS HTTP communication errors.
 *
 * Thrown when:
 * - HTTP request fails (4xx, 5xx errors)
 * - BPJS API returns error response
 * - Network timeout occurs
 * - Response parsing fails
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
public class BpjsHttpException extends BpjsIntegrationException {

    private final Integer httpStatusCode;

    public BpjsHttpException(String message) {
        super(message);
        this.httpStatusCode = null;
    }

    public BpjsHttpException(String message, String errorCode) {
        super(message, errorCode);
        this.httpStatusCode = null;
    }

    public BpjsHttpException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = null;
    }

    public BpjsHttpException(String message, String errorCode, Integer httpStatusCode) {
        super(message, errorCode);
        this.httpStatusCode = httpStatusCode;
    }

    public BpjsHttpException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
        this.httpStatusCode = null;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
}
