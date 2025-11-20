package com.yudha.hms.integration.bpjs.dto.icare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Patient History Access Information.
 *
 * Contains information about patient history access for audit logging
 * and session management. This should be stored temporarily for tracking
 * which doctors have accessed which patient histories and when.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryAccessInfo {

    /**
     * BPJS card number (No Kartu BPJS).
     */
    private String cardNumber;

    /**
     * Patient name (if available from hospital records).
     */
    private String patientName;

    /**
     * Doctor code who requested access.
     */
    private Integer doctorCode;

    /**
     * Doctor name (if available from hospital records).
     */
    private String doctorName;

    /**
     * Secure URL with token.
     */
    private String historyUrl;

    /**
     * Extracted token from URL.
     */
    private String token;

    /**
     * Timestamp when the access was granted.
     */
    private LocalDateTime accessedAt;

    /**
     * Estimated token expiry time.
     * Based on BPJS policy (typically 24 hours or session-based).
     */
    private LocalDateTime expiresAt;

    /**
     * User ID who initiated the request (for audit).
     */
    private String requestedBy;

    /**
     * IP address of the requesting user (for audit).
     */
    private String ipAddress;

    /**
     * Purpose or context of access (e.g., "Consultation", "Emergency", "Referral").
     */
    private String accessPurpose;

    /**
     * Check if token is likely expired.
     *
     * @return true if current time is past expiry time
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if token is still valid.
     *
     * @return true if token has not expired
     */
    public boolean isValid() {
        return !isExpired();
    }
}
