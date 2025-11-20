package com.yudha.hms.integration.bpjs.service;

import com.yudha.hms.integration.bpjs.config.BpjsConfig;
import com.yudha.hms.integration.bpjs.exception.BpjsAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * BPJS Authentication Service.
 *
 * Handles authentication for BPJS Kesehatan web services using HMAC-SHA256 signature.
 * Implements BPJS Trust Mark security requirements.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BpjsAuthenticationService {

    private final BpjsConfig bpjsConfig;

    /**
     * Generate timestamp in BPJS required format (seconds since epoch).
     * Uses Indonesia timezone (UTC+7).
     *
     * @return Timestamp as string
     */
    public String generateTimestamp() {
        ZonedDateTime jakartaTime = ZonedDateTime.now(ZoneId.of(bpjsConfig.getTimezone()));
        long epochSeconds = jakartaTime.toEpochSecond();
        return String.valueOf(epochSeconds);
    }

    /**
     * Generate HMAC-SHA256 signature for BPJS authentication.
     * Format: Base64(HMAC-SHA256(cons_secret, cons_id + "&" + timestamp))
     *
     * @param consId Consumer ID
     * @param timestamp Timestamp in seconds
     * @return Base64 encoded signature
     * @throws BpjsAuthenticationException if signature generation fails
     */
    public String generateSignature(String consId, String timestamp) {
        try {
            String data = consId + "&" + timestamp;

            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                bpjsConfig.getConsSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
            );
            sha256Hmac.init(secretKey);

            byte[] signatureBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signatureBytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to generate BPJS signature", e);
            throw new BpjsAuthenticationException("Failed to generate signature: " + e.getMessage(), e);
        }
    }

    /**
     * Generate complete authentication headers for BPJS API requests.
     *
     * @return Map of header names to values
     */
    public Map<String, String> generateAuthHeaders() {
        if (!bpjsConfig.isConfigured()) {
            throw new BpjsAuthenticationException(
                "BPJS configuration is incomplete. Please check consId, consSecret, and userKey."
            );
        }

        String timestamp = generateTimestamp();
        String signature = generateSignature(bpjsConfig.getConsId(), timestamp);

        Map<String, String> headers = new HashMap<>();
        headers.put("X-cons-id", bpjsConfig.getConsId());
        headers.put("X-timestamp", timestamp);
        headers.put("X-signature", signature);
        headers.put("user_key", bpjsConfig.getUserKey());
        headers.put("Content-Type", "application/json");

        if (bpjsConfig.isLoggingEnabled()) {
            log.debug("Generated BPJS auth headers - timestamp: {}, signature: {}",
                timestamp, signature.substring(0, Math.min(10, signature.length())) + "...");
        }

        return headers;
    }

    /**
     * Validate authentication configuration.
     *
     * @return true if configuration is valid
     */
    public boolean validateConfiguration() {
        if (!bpjsConfig.isConfigured()) {
            log.warn("BPJS configuration is incomplete");
            return false;
        }

        try {
            // Test signature generation
            String testTimestamp = generateTimestamp();
            generateSignature(bpjsConfig.getConsId(), testTimestamp);
            log.info("BPJS authentication configuration is valid");
            return true;
        } catch (Exception e) {
            log.error("BPJS authentication configuration validation failed", e);
            return false;
        }
    }
}
