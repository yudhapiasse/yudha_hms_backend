package com.yudha.hms.integration.satusehat.exception;

import lombok.Getter;

/**
 * Exception for SATUSEHAT HTTP communication errors.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Getter
public class SatusehatHttpException extends SatusehatIntegrationException {

    private final String responseBody;

    public SatusehatHttpException(String message, Integer httpStatus) {
        super(message, "SATUSEHAT_HTTP_ERROR", httpStatus);
        this.responseBody = null;
    }

    public SatusehatHttpException(String message, Integer httpStatus, String responseBody) {
        super(message, "SATUSEHAT_HTTP_ERROR", httpStatus);
        this.responseBody = responseBody;
    }

    public SatusehatHttpException(String message, Integer httpStatus, Throwable cause) {
        super(message, "SATUSEHAT_HTTP_ERROR", httpStatus, cause);
        this.responseBody = null;
    }
}
