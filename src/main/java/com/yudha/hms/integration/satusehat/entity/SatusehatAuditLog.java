package com.yudha.hms.integration.satusehat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SATUSEHAT Audit Log Entity.
 *
 * Tracks all SATUSEHAT API interactions including OAuth2 authentication,
 * FHIR resource operations, and consent management.
 *
 * Operation Types:
 * - AUTH: OAuth2 token requests
 * - CREATE: Creating new FHIR resources
 * - UPDATE: Updating existing FHIR resources
 * - READ: Reading FHIR resources
 * - SEARCH: Searching FHIR resources
 * - DELETE: Deleting FHIR resources (rare)
 * - CONSENT: Consent management operations
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "satusehat_audit_logs", indexes = {
    @Index(name = "idx_satusehat_audit_config", columnList = "config_id"),
    @Index(name = "idx_satusehat_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_satusehat_audit_operation", columnList = "operation_type"),
    @Index(name = "idx_satusehat_audit_resource", columnList = "resource_type"),
    @Index(name = "idx_satusehat_audit_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SatusehatAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private SatusehatConfig config;

    /**
     * Operation type: AUTH, CREATE, UPDATE, READ, SEARCH, DELETE, CONSENT
     */
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    /**
     * FHIR resource type (Patient, Encounter, Observation, etc.)
     */
    @Column(name = "resource_type", length = 100)
    private String resourceType;

    /**
     * FHIR resource ID
     */
    @Column(name = "resource_id", length = 100)
    private String resourceId;

    /**
     * HTTP method: GET, POST, PUT, PATCH, DELETE
     */
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    /**
     * Full API endpoint URL
     */
    @Column(name = "endpoint", nullable = false, length = 1000)
    private String endpoint;

    /**
     * Request headers (JSONB)
     */
    @Column(name = "request_headers", columnDefinition = "jsonb")
    private String requestHeaders;

    /**
     * Request body (JSONB)
     */
    @Column(name = "request_body", columnDefinition = "jsonb")
    private String requestBody;

    /**
     * HTTP response status code
     */
    @Column(name = "response_status")
    private Integer responseStatus;

    /**
     * Response headers (JSONB)
     */
    @Column(name = "response_headers", columnDefinition = "jsonb")
    private String responseHeaders;

    /**
     * Response body (JSONB)
     */
    @Column(name = "response_body", columnDefinition = "jsonb")
    private String responseBody;

    /**
     * Error code from SATUSEHAT API
     */
    @Column(name = "error_code", length = 50)
    private String errorCode;

    /**
     * Error message
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Request execution time in milliseconds
     */
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    /**
     * Number of retry attempts made
     */
    @Column(name = "retry_count")
    private Integer retryCount = 0;

    /**
     * Client IP address
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User agent string
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * User who performed the operation
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Builder for audit log creation
     */
    public static class Builder {
        private SatusehatAuditLog log = new SatusehatAuditLog();

        public Builder config(SatusehatConfig config) {
            log.config = config;
            return this;
        }

        public Builder operationType(String operationType) {
            log.operationType = operationType;
            return this;
        }

        public Builder resourceType(String resourceType) {
            log.resourceType = resourceType;
            return this;
        }

        public Builder resourceId(String resourceId) {
            log.resourceId = resourceId;
            return this;
        }

        public Builder method(String method) {
            log.method = method;
            return this;
        }

        public Builder endpoint(String endpoint) {
            log.endpoint = endpoint;
            return this;
        }

        public Builder requestHeaders(String requestHeaders) {
            log.requestHeaders = requestHeaders;
            return this;
        }

        public Builder requestBody(String requestBody) {
            log.requestBody = requestBody;
            return this;
        }

        public Builder responseStatus(Integer responseStatus) {
            log.responseStatus = responseStatus;
            return this;
        }

        public Builder responseHeaders(String responseHeaders) {
            log.responseHeaders = responseHeaders;
            return this;
        }

        public Builder responseBody(String responseBody) {
            log.responseBody = responseBody;
            return this;
        }

        public Builder errorCode(String errorCode) {
            log.errorCode = errorCode;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            log.errorMessage = errorMessage;
            return this;
        }

        public Builder executionTimeMs(Integer executionTimeMs) {
            log.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder retryCount(Integer retryCount) {
            log.retryCount = retryCount;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            log.ipAddress = ipAddress;
            return this;
        }

        public Builder userAgent(String userAgent) {
            log.userAgent = userAgent;
            return this;
        }

        public Builder userId(UUID userId) {
            log.userId = userId;
            return this;
        }

        public SatusehatAuditLog build() {
            log.createdAt = LocalDateTime.now();
            return log;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
