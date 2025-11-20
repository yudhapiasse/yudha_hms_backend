package com.yudha.hms.integration.satusehat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SATUSEHAT Configuration Entity.
 *
 * Stores OAuth2 credentials, API endpoints, and configuration for
 * SATUSEHAT FHIR R4 API integration per organization.
 *
 * Environments:
 * - SANDBOX: Development/testing environment
 * - PRODUCTION: Live production environment
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "satusehat_config", indexes = {
    @Index(name = "idx_satusehat_config_org_id", columnList = "organization_id"),
    @Index(name = "idx_satusehat_config_environment", columnList = "environment, is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SatusehatConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "organization_id", unique = true, nullable = false, length = 100)
    private String organizationId;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    /**
     * Environment: SANDBOX or PRODUCTION
     */
    @Column(name = "environment", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Environment environment = Environment.SANDBOX;

    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * OAuth2 client ID from SATUSEHAT registration
     */
    @Column(name = "client_id", nullable = false)
    private String clientId;

    /**
     * OAuth2 client secret (encrypted with AES-256)
     */
    @Column(name = "client_secret", nullable = false, columnDefinition = "text")
    private String clientSecret;

    /**
     * OAuth2 authentication endpoint
     * Sandbox: https://api-satusehat-stg.dto.kemkes.go.id/oauth2/v1
     * Production: https://api-satusehat.kemkes.go.id/oauth2/v1
     */
    @Column(name = "auth_url", nullable = false, length = 500)
    private String authUrl;

    /**
     * FHIR R4 base URL
     * Sandbox: https://api-satusehat-stg.dto.kemkes.go.id/fhir-r4/v1
     * Production: https://api-satusehat.kemkes.go.id/fhir-r4/v1
     */
    @Column(name = "fhir_base_url", nullable = false, length = 500)
    private String fhirBaseUrl;

    /**
     * Consent management URL
     * Sandbox: https://api-satusehat-stg.dto.kemkes.go.id/consent/v1
     * Production: https://api-satusehat.kemkes.go.id/consent/v1
     */
    @Column(name = "consent_url", nullable = false, length = 500)
    private String consentUrl;

    /**
     * Current cached access token (also stored in Redis)
     */
    @Column(name = "current_access_token", columnDefinition = "text")
    private String currentAccessToken;

    /**
     * Token expiration timestamp
     */
    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    /**
     * Token issue timestamp
     */
    @Column(name = "token_issued_at")
    private LocalDateTime tokenIssuedAt;

    /**
     * Rate limit: requests per second (default: 100)
     */
    @Column(name = "rate_limit_per_second")
    private Integer rateLimitPerSecond = 100;

    /**
     * Rate limit: burst requests per minute (default: 1000)
     */
    @Column(name = "rate_limit_burst")
    private Integer rateLimitBurst = 1000;

    /**
     * Maximum retry attempts for failed requests (default: 3)
     */
    @Column(name = "max_retry_attempts")
    private Integer maxRetryAttempts = 3;

    /**
     * Initial backoff delay in milliseconds for retries (default: 1000ms)
     */
    @Column(name = "retry_backoff_ms")
    private Integer retryBackoffMs = 1000;

    /**
     * Request timeout in seconds (default: 30)
     */
    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds = 30;

    /**
     * Location IDs for hospital facilities (JSONB array)
     * Example: ["Location-123", "Location-456"]
     */
    @Column(name = "location_ids", columnDefinition = "jsonb")
    private String locationIds;

    /**
     * Mapping of NIK (National ID) to SATUSEHAT Practitioner IDs (JSONB object)
     * Example: {"3201234567890123": "Practitioner-abc123"}
     */
    @Column(name = "practitioner_mapping", columnDefinition = "jsonb")
    private String practitionerMapping;

    // Audit fields
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Environment enumeration
     */
    public enum Environment {
        SANDBOX,
        PRODUCTION
    }

    /**
     * Check if token is expired or about to expire (within 5 minutes)
     */
    public boolean isTokenExpired() {
        if (tokenExpiresAt == null) {
            return true;
        }
        // Consider token expired if less than 5 minutes remaining
        return LocalDateTime.now().plusMinutes(5).isAfter(tokenExpiresAt);
    }

    /**
     * Get full authentication endpoint URL
     */
    public String getAuthEndpoint() {
        return authUrl + "/accesstoken?grant_type=client_credentials";
    }
}
