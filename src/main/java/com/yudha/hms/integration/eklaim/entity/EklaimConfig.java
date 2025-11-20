package com.yudha.hms.integration.eklaim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * E-Klaim Configuration Entity.
 *
 * Stores E-Klaim Web Service 5.10.x configuration per hospital/facility.
 * Supports multiple hospital configurations in a single database.
 *
 * Configuration includes:
 * - API endpoints (dev/production)
 * - Authentication credentials (cons_id, secret_key, user_key)
 * - Rate limiting and timeout settings
 * - Environment flags
 *
 * Security Note:
 * - secret_key and user_key should be encrypted at rest
 * - Credentials should be rotated regularly per security policy
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "eklaim_config", uniqueConstraints = {
    @UniqueConstraint(name = "uk_eklaim_hospital_code", columnNames = "hospital_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EklaimConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Hospital/facility code registered with E-Klaim
     */
    @Column(name = "hospital_code", nullable = false, unique = true, length = 50)
    private String hospitalCode;

    /**
     * E-Klaim base URL
     * Development: https://dvlp.bpjs-kesehatan.go.id:9081/eklaim-ws/ws/
     * Production: https://api.bpjs-kesehatan.go.id/eklaim-ws/ws/
     */
    @Column(name = "base_url", nullable = false, length = 255)
    private String baseUrl;

    /**
     * Consumer ID (cons_id) from BPJS Kesehatan
     */
    @Column(name = "cons_id", nullable = false, length = 100)
    private String consId;

    /**
     * Secret key for signature generation
     * Should be stored encrypted
     */
    @Column(name = "secret_key", nullable = false, columnDefinition = "text")
    private String secretKey;

    /**
     * User key for web service authentication
     * Should be stored encrypted
     */
    @Column(name = "user_key", nullable = false, columnDefinition = "text")
    private String userKey;

    /**
     * Configuration active flag
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    /**
     * Production environment flag
     */
    @Column(name = "is_production")
    private Boolean isProduction = false;

    /**
     * Rate limit: maximum requests per minute
     * Default: 100 requests/minute
     */
    @Column(name = "rate_limit_per_minute")
    private Integer rateLimitPerMinute = 100;

    /**
     * Request timeout in seconds
     * Default: 30 seconds
     */
    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds = 30;

    /**
     * Maximum retry attempts for failed requests
     * Default: 3 attempts
     */
    @Column(name = "max_retry_attempts")
    private Integer maxRetryAttempts = 3;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Get effective base URL based on environment
     */
    public String getEffectiveBaseUrl() {
        if (baseUrl != null && !baseUrl.isEmpty()) {
            return baseUrl;
        }
        // Fallback to default URLs
        return isProduction
                ? "https://api.bpjs-kesehatan.go.id/eklaim-ws/ws/"
                : "https://dvlp.bpjs-kesehatan.go.id:9081/eklaim-ws/ws/";
    }

    /**
     * Check if configuration is valid
     */
    public boolean isValid() {
        return isActive &&
               hospitalCode != null && !hospitalCode.isEmpty() &&
               consId != null && !consId.isEmpty() &&
               secretKey != null && !secretKey.isEmpty() &&
               userKey != null && !userKey.isEmpty() &&
               baseUrl != null && !baseUrl.isEmpty();
    }

    /**
     * Get timeout in milliseconds
     */
    public int getTimeoutMillis() {
        return timeoutSeconds * 1000;
    }

    /**
     * Get environment name
     */
    public String getEnvironmentName() {
        return isProduction ? "production" : "development";
    }
}
