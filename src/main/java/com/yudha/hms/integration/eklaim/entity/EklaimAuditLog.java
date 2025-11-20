package com.yudha.hms.integration.eklaim.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * E-Klaim Audit Log Entity.
 *
 * Tracks all E-Klaim API interactions for compliance, troubleshooting,
 * and audit trail purposes. Records requests, responses, and errors.
 *
 * Retention Policy:
 * - Audit logs must be retained for minimum 5 years per Indonesian regulation
 * - Encrypted data stored separately for security compliance
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "eklaim_audit_logs", indexes = {
    @Index(name = "idx_eklaim_audit_claim_id", columnList = "claim_id"),
    @Index(name = "idx_eklaim_audit_created_at", columnList = "created_at"),
    @Index(name = "idx_eklaim_audit_action", columnList = "action"),
    @Index(name = "idx_eklaim_audit_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EklaimAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    private EklaimClaim claim;

    /**
     * E-Klaim action/method name (e.g., "new_claim", "set_claim_data", "grouper_1")
     */
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    /**
     * HTTP method used (GET, POST, PUT, DELETE)
     */
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    /**
     * Request data (decrypted JSON)
     */
    @Column(name = "request_data", columnDefinition = "jsonb")
    private String requestData;

    /**
     * Encrypted request (as sent to E-Klaim)
     */
    @Column(name = "encrypted_request", columnDefinition = "text")
    private String encryptedRequest;

    /**
     * Response data (decrypted JSON)
     */
    @Column(name = "response_data", columnDefinition = "jsonb")
    private String responseData;

    /**
     * Encrypted response (as received from E-Klaim)
     */
    @Column(name = "encrypted_response", columnDefinition = "text")
    private String encryptedResponse;

    /**
     * HTTP status code
     */
    @Column(name = "status_code", length = 10)
    private String statusCode;

    /**
     * Error message if request failed
     */
    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    /**
     * Execution time in milliseconds
     */
    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

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
     * User who performed the action
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Old values (for update operations)
     */
    @Column(name = "old_values", columnDefinition = "jsonb")
    private String oldValues;

    /**
     * New values (for update operations)
     */
    @Column(name = "new_values", columnDefinition = "jsonb")
    private String newValues;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Builder method for audit log creation
     */
    public static class Builder {
        private EklaimAuditLog log = new EklaimAuditLog();

        public Builder claim(EklaimClaim claim) {
            log.claim = claim;
            return this;
        }

        public Builder action(String action) {
            log.action = action;
            return this;
        }

        public Builder method(String method) {
            log.method = method;
            return this;
        }

        public Builder requestData(String requestData) {
            log.requestData = requestData;
            return this;
        }

        public Builder encryptedRequest(String encryptedRequest) {
            log.encryptedRequest = encryptedRequest;
            return this;
        }

        public Builder responseData(String responseData) {
            log.responseData = responseData;
            return this;
        }

        public Builder encryptedResponse(String encryptedResponse) {
            log.encryptedResponse = encryptedResponse;
            return this;
        }

        public Builder statusCode(String statusCode) {
            log.statusCode = statusCode;
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

        public Builder oldValues(String oldValues) {
            log.oldValues = oldValues;
            return this;
        }

        public Builder newValues(String newValues) {
            log.newValues = newValues;
            return this;
        }

        public EklaimAuditLog build() {
            log.createdAt = LocalDateTime.now();
            return log;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
