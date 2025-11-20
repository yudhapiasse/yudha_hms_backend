package com.yudha.hms.integration.satusehat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.satusehat.dto.OAuth2TokenResponse;
import com.yudha.hms.integration.satusehat.dto.TokenInfo;
import com.yudha.hms.integration.satusehat.entity.SatusehatConfig;
import com.yudha.hms.integration.satusehat.exception.SatusehatAuthenticationException;
import com.yudha.hms.integration.satusehat.exception.SatusehatIntegrationException;
import com.yudha.hms.integration.satusehat.repository.SatusehatConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SATUSEHAT OAuth2 Authentication Service.
 *
 * Handles OAuth2 client credentials flow for SATUSEHAT API:
 * 1. Obtain access token from auth endpoint
 * 2. Cache token in database with expiry tracking
 * 3. Auto-refresh token before expiry (5 minutes buffer)
 * 4. Handle token errors and retries
 *
 * Token Storage:
 * - Database (satusehat_config table)
 * - Tokens are cached with expiry timestamp
 * - Auto-refresh when token expires or is about to expire
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SatusehatAuthService {

    private final SatusehatConfigRepository configRepository;
    private final SatusehatAuditService auditService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Get valid access token for organization.
     * Returns cached token if available and not expired,
     * otherwise requests new token.
     */
    public String getAccessToken(String organizationId, UUID userId) {
        log.debug("Getting access token for organization: {}", organizationId);

        // Get active configuration
        SatusehatConfig config = getActiveConfig(organizationId);

        // Check if cached token in database is still valid
        if (config.getCurrentAccessToken() != null && !config.isTokenExpired()) {
            log.debug("Using cached token from database for organization: {}", organizationId);
            return config.getCurrentAccessToken();
        }

        // Request new token
        log.info("Requesting new access token for organization: {}", organizationId);
        return requestNewToken(config, userId);
    }

    /**
     * Request new access token from SATUSEHAT OAuth2 endpoint.
     */
    @Transactional
    public String requestNewToken(SatusehatConfig config, UUID userId) {
        long startTime = System.currentTimeMillis();

        try {
            // Prepare request
            String endpoint = config.getAuthEndpoint();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", config.getClientId());
            body.add("client_secret", config.getClientSecret());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            // Execute request
            log.debug("POST {} (OAuth2 token request)", endpoint);
            ResponseEntity<OAuth2TokenResponse> response = restTemplate.exchange(
                endpoint,
                HttpMethod.POST,
                request,
                OAuth2TokenResponse.class
            );

            long executionTime = System.currentTimeMillis() - startTime;

            // Validate response
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new SatusehatAuthenticationException(
                    "Failed to obtain access token: Invalid response"
                );
            }

            OAuth2TokenResponse tokenResponse = response.getBody();
            if (!tokenResponse.isValid()) {
                throw new SatusehatAuthenticationException(
                    "Invalid token response from SATUSEHAT"
                );
            }

            // Create token info
            TokenInfo tokenInfo = TokenInfo.fromOAuth2Response(
                tokenResponse,
                config.getOrganizationId(),
                config.getEnvironment().name()
            );

            // Update database with new token
            config.setCurrentAccessToken(tokenInfo.getAccessToken());
            config.setTokenExpiresAt(tokenInfo.getExpiresAt());
            config.setTokenIssuedAt(tokenInfo.getIssuedAt());
            configRepository.save(config);

            // Audit log
            auditService.logAuthSuccess(
                config,
                endpoint,
                "POST",
                objectMapper.writeValueAsString(tokenResponse),
                (int) executionTime,
                userId
            );

            log.info("Successfully obtained access token for organization: {} (expires in {} seconds)",
                config.getOrganizationId(), tokenInfo.getExpiresIn());

            return tokenInfo.getAccessToken();

        } catch (SatusehatAuthenticationException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            auditService.logAuthFailure(config, config.getAuthEndpoint(), "POST",
                e.getMessage(), (int) executionTime, userId);
            throw e;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            auditService.logAuthFailure(config, config.getAuthEndpoint(), "POST",
                e.getMessage(), (int) executionTime, userId);
            throw new SatusehatAuthenticationException(
                "Failed to obtain access token: " + e.getMessage(), e
            );
        }
    }

    /**
     * Refresh access token if it's about to expire.
     */
    public void refreshTokenIfNeeded(String organizationId, UUID userId) {
        SatusehatConfig config = getActiveConfig(organizationId);

        if (config.isTokenExpired()) {
            log.info("Token expired or about to expire for organization: {}, refreshing...",
                organizationId);
            requestNewToken(config, userId);
        }
    }

    /**
     * Invalidate cached token (force refresh on next request).
     */
    @Transactional
    public void invalidateToken(String organizationId, String environment) {
        SatusehatConfig config = getActiveConfig(organizationId);
        config.setCurrentAccessToken(null);
        config.setTokenExpiresAt(null);
        config.setTokenIssuedAt(null);
        configRepository.save(config);
        log.info("Invalidated token for organization: {} environment: {}",
            organizationId, environment);
    }

    /**
     * Get active configuration by organization ID.
     */
    private SatusehatConfig getActiveConfig(String organizationId) {
        return configRepository.findByOrganizationIdAndIsActiveTrue(organizationId)
            .orElseThrow(() -> new SatusehatIntegrationException(
                "SATUSEHAT configuration not found for organization: " + organizationId,
                "SATUSEHAT_CONFIG_NOT_FOUND"
            ));
    }

    /**
     * Validate token format (basic JWT validation).
     */
    public boolean isValidTokenFormat(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        // JWT format: header.payload.signature
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}
