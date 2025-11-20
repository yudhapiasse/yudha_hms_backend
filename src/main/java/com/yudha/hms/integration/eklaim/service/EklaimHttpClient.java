package com.yudha.hms.integration.eklaim.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.eklaim.dto.EklaimBaseResponse;
import com.yudha.hms.integration.eklaim.entity.EklaimConfig;
import com.yudha.hms.integration.eklaim.exception.EklaimAuthenticationException;
import com.yudha.hms.integration.eklaim.exception.EklaimHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * E-Klaim HTTP Client Service.
 *
 * Handles HTTP communication with E-Klaim Web Service including:
 * - Request/response encryption
 * - Rate limiting (100 requests/minute default)
 * - Retry logic with exponential backoff
 * - Authentication header management
 * - Error handling and mapping
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EklaimHttpClient {

    @Qualifier("eklaimRestTemplate")
    private final RestTemplate restTemplate;

    private final EklaimEncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    // Rate limiting: Track requests per minute per hospital
    private final Map<String, RateLimitTracker> rateLimiters = new ConcurrentHashMap<>();

    /**
     * Send encrypted POST request to E-Klaim API.
     *
     * @param endpoint API endpoint path
     * @param request Request body (will be encrypted)
     * @param config E-Klaim configuration
     * @param responseType Expected response type
     * @return Decrypted response
     */
    public <T> EklaimBaseResponse<T> post(
        String endpoint,
        Object request,
        EklaimConfig config,
        Class<T> responseType
    ) {
        return sendRequest(endpoint, HttpMethod.POST, request, config, responseType);
    }

    /**
     * Send encrypted PUT request to E-Klaim API.
     */
    public <T> EklaimBaseResponse<T> put(
        String endpoint,
        Object request,
        EklaimConfig config,
        Class<T> responseType
    ) {
        return sendRequest(endpoint, HttpMethod.PUT, request, config, responseType);
    }

    /**
     * Send encrypted GET request to E-Klaim API.
     */
    public <T> EklaimBaseResponse<T> get(
        String endpoint,
        EklaimConfig config,
        Class<T> responseType
    ) {
        return sendRequest(endpoint, HttpMethod.GET, null, config, responseType);
    }

    /**
     * Send encrypted DELETE request to E-Klaim API.
     */
    public <T> EklaimBaseResponse<T> delete(
        String endpoint,
        EklaimConfig config,
        Class<T> responseType
    ) {
        return sendRequest(endpoint, HttpMethod.DELETE, null, config, responseType);
    }

    /**
     * Core request method with encryption, rate limiting, and retry logic.
     */
    private <T> EklaimBaseResponse<T> sendRequest(
        String endpoint,
        HttpMethod method,
        Object requestBody,
        EklaimConfig config,
        Class<T> responseType
    ) {
        // Check rate limit
        checkRateLimit(config);

        String url = config.getEffectiveBaseUrl() + endpoint;
        int maxRetries = config.getMaxRetryAttempts();
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= maxRetries) {
            try {
                // Prepare request
                String encryptedRequest = null;
                if (requestBody != null) {
                    String jsonRequest = objectMapper.writeValueAsString(requestBody);
                    encryptedRequest = encryptionService.encrypt(jsonRequest, config);
                }

                // Build headers
                HttpHeaders headers = buildHeaders(config);

                // Create HTTP entity
                HttpEntity<String> entity = new HttpEntity<>(encryptedRequest, headers);

                // Execute request
                long startTime = System.currentTimeMillis();
                ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    method,
                    entity,
                    String.class
                );
                long executionTime = System.currentTimeMillis() - startTime;

                log.debug("E-Klaim {} {} completed in {}ms (attempt {}/{})",
                    method, endpoint, executionTime, attempt + 1, maxRetries + 1);

                // Decrypt and parse response
                String encryptedResponse = response.getBody();
                if (encryptedResponse == null || encryptedResponse.isEmpty()) {
                    throw new EklaimHttpException("Empty response from E-Klaim", 500);
                }

                String decryptedResponse = encryptionService.decrypt(encryptedResponse, config);

                // Parse to response object
                @SuppressWarnings("unchecked")
                EklaimBaseResponse<T> result = objectMapper.readValue(
                    decryptedResponse,
                    objectMapper.getTypeFactory().constructParametricType(
                        EklaimBaseResponse.class,
                        responseType
                    )
                );

                // Check for E-Klaim errors
                if (result.hasError()) {
                    handleEklaimError(result);
                }

                return result;

            } catch (HttpClientErrorException | HttpServerErrorException e) {
                lastException = e;
                if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                    throw new EklaimAuthenticationException(
                        "Authentication failed: " + e.getMessage(),
                        "E2003"
                    );
                }
                if (!shouldRetry(e.getStatusCode().value())) {
                    throw new EklaimHttpException(
                        "HTTP error: " + e.getMessage(),
                        e.getStatusCode().value(),
                        e
                    );
                }
            } catch (Exception e) {
                lastException = e;
                log.error("E-Klaim request failed (attempt {}/{}): {}",
                    attempt + 1, maxRetries + 1, e.getMessage());
            }

            attempt++;
            if (attempt <= maxRetries) {
                try {
                    // Exponential backoff: 1s, 2s, 4s
                    long backoffMs = (long) Math.pow(2, attempt - 1) * 1000;
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new EklaimHttpException("Request interrupted", 500, ie);
                }
            }
        }

        throw new EklaimHttpException(
            "E-Klaim request failed after " + (maxRetries + 1) + " attempts",
            500,
            lastException
        );
    }

    /**
     * Build HTTP headers for E-Klaim request.
     */
    private HttpHeaders buildHeaders(EklaimConfig config) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-cons-id", config.getConsId());
        headers.set("X-user-key", config.getUserKey());
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    /**
     * Check rate limit before sending request.
     */
    private void checkRateLimit(EklaimConfig config) {
        String hospitalCode = config.getHospitalCode();
        RateLimitTracker tracker = rateLimiters.computeIfAbsent(
            hospitalCode,
            k -> new RateLimitTracker(config.getRateLimitPerMinute())
        );

        if (!tracker.allowRequest()) {
            throw new EklaimHttpException(
                "Rate limit exceeded: " + config.getRateLimitPerMinute() + " requests/minute",
                429
            );
        }
    }

    /**
     * Determine if request should be retried based on status code.
     */
    private boolean shouldRetry(int statusCode) {
        return statusCode >= 500 || statusCode == 429; // Server errors or rate limit
    }

    /**
     * Handle E-Klaim specific error codes.
     */
    private void handleEklaimError(EklaimBaseResponse<?> response) {
        String errorCode = response.getMetadata().getErrorCode();
        String message = response.getMetadata().getMessage();

        if (errorCode != null) {
            if (errorCode.equals("E2003") || errorCode.equals("E2004")) {
                throw new EklaimAuthenticationException(message, errorCode);
            }
            throw new EklaimHttpException(message + " (Error: " + errorCode + ")", 400);
        }
    }

    /**
     * Rate limit tracker using sliding window.
     */
    private static class RateLimitTracker {
        private final int maxRequests;
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private LocalDateTime windowStart = LocalDateTime.now();

        public RateLimitTracker(int maxRequestsPerMinute) {
            this.maxRequests = maxRequestsPerMinute;
        }

        public synchronized boolean allowRequest() {
            LocalDateTime now = LocalDateTime.now();

            // Reset counter if window expired
            if (Duration.between(windowStart, now).toMinutes() >= 1) {
                windowStart = now;
                requestCount.set(0);
            }

            int current = requestCount.incrementAndGet();
            return current <= maxRequests;
        }
    }
}
