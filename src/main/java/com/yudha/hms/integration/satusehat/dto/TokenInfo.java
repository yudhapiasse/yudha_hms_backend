package com.yudha.hms.integration.satusehat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Token Information.
 *
 * Internal representation of SATUSEHAT OAuth2 token.
 * Used for caching and token management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {

    /**
     * Access token (JWT)
     */
    private String accessToken;

    /**
     * Token type (usually "Bearer")
     */
    private String tokenType;

    /**
     * Expiration time in seconds
     */
    private Integer expiresIn;

    /**
     * Token issue timestamp
     */
    private LocalDateTime issuedAt;

    /**
     * Token expiration timestamp
     */
    private LocalDateTime expiresAt;

    /**
     * Organization ID this token belongs to
     */
    private String organizationId;

    /**
     * Environment (SANDBOX or PRODUCTION)
     */
    private String environment;

    /**
     * Check if token is expired or about to expire (within 5 minutes)
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return true;
        }
        // Consider token expired if less than 5 minutes remaining
        return LocalDateTime.now().plusMinutes(5).isAfter(expiresAt);
    }

    /**
     * Get remaining validity time in seconds
     */
    public long getRemainingSeconds() {
        if (expiresAt == null) {
            return 0;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) {
            return 0;
        }
        return java.time.Duration.between(now, expiresAt).getSeconds();
    }

    /**
     * Create TokenInfo from OAuth2TokenResponse
     */
    public static TokenInfo fromOAuth2Response(OAuth2TokenResponse response,
                                                String organizationId,
                                                String environment) {
        LocalDateTime issuedAt = LocalDateTime.now();
        Integer expiresIn = response.getExpiresIn();
        LocalDateTime expiresAt = issuedAt.plusSeconds(expiresIn != null ? expiresIn : 3599);

        return TokenInfo.builder()
            .accessToken(response.getAccessToken())
            .tokenType(response.getTokenType())
            .expiresIn(expiresIn)
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .organizationId(organizationId)
            .environment(environment)
            .build();
    }

    /**
     * Get authorization header value
     */
    public String getAuthorizationHeader() {
        return (tokenType != null ? tokenType : "Bearer") + " " + accessToken;
    }
}
