package com.yudha.hms.integration.satusehat.exception;

import lombok.Getter;

/**
 * Exception for SATUSEHAT rate limit errors (HTTP 429).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Getter
public class SatusehatRateLimitException extends SatusehatIntegrationException {

    private final Long retryAfterSeconds;

    public SatusehatRateLimitException(String message) {
        super(message, "SATUSEHAT_RATE_LIMIT", 429);
        this.retryAfterSeconds = null;
    }

    public SatusehatRateLimitException(String message, Long retryAfterSeconds) {
        super(message, "SATUSEHAT_RATE_LIMIT", 429);
        this.retryAfterSeconds = retryAfterSeconds;
    }
}
