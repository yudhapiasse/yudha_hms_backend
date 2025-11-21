package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.ClaimStatus;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Claim Audit Log Entity.
 *
 * Tracks all changes and status transitions for insurance claims.
 * Provides complete audit trail for claim lifecycle.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "claim_audit_log", schema = "billing_schema", indexes = {
        @Index(name = "idx_claim_audit_claim", columnList = "claim_id"),
        @Index(name = "idx_claim_audit_timestamp", columnList = "timestamp"),
        @Index(name = "idx_claim_audit_action", columnList = "action")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClaimAuditLog extends BaseEntity {

    /**
     * Claim ID
     */
    @Column(name = "claim_id", nullable = false)
    private UUID claimId;

    /**
     * Claim number
     */
    @Column(name = "claim_number", nullable = false, length = 50)
    private String claimNumber;

    /**
     * Action performed (e.g., "CREATED", "SUBMITTED", "APPROVED", "REJECTED")
     */
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    /**
     * Previous status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 50)
    private ClaimStatus previousStatus;

    /**
     * New status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 50)
    private ClaimStatus newStatus;

    /**
     * Action timestamp
     */
    @Column(name = "timestamp", nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * User who performed the action
     */
    @Column(name = "performed_by", nullable = false, length = 100)
    private String performedBy;

    /**
     * User role
     */
    @Column(name = "user_role", length = 50)
    private String userRole;

    /**
     * Description of changes
     */
    @Column(name = "change_description", columnDefinition = "TEXT")
    private String changeDescription;

    /**
     * Previous values (JSON format)
     */
    @Column(name = "previous_values", columnDefinition = "TEXT")
    private String previousValues;

    /**
     * New values (JSON format)
     */
    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    /**
     * IP address
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * User agent
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Additional notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Helper method to create audit log for claim creation
     *
     * @param claim claim
     * @param performedBy user
     * @return audit log
     */
    public static ClaimAuditLog forCreation(InsuranceClaim claim, String performedBy) {
        return ClaimAuditLog.builder()
                .claimId(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .action("CREATED")
                .newStatus(claim.getStatus())
                .performedBy(performedBy)
                .changeDescription("Claim created")
                .build();
    }

    /**
     * Helper method to create audit log for status change
     *
     * @param claim claim
     * @param previousStatus previous status
     * @param performedBy user
     * @param description description
     * @return audit log
     */
    public static ClaimAuditLog forStatusChange(InsuranceClaim claim, ClaimStatus previousStatus,
                                                String performedBy, String description) {
        return ClaimAuditLog.builder()
                .claimId(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .action("STATUS_CHANGE")
                .previousStatus(previousStatus)
                .newStatus(claim.getStatus())
                .performedBy(performedBy)
                .changeDescription(description)
                .build();
    }

    /**
     * Helper method to create audit log for document upload
     *
     * @param claim claim
     * @param documentName document name
     * @param performedBy user
     * @return audit log
     */
    public static ClaimAuditLog forDocumentUpload(InsuranceClaim claim, String documentName,
                                                  String performedBy) {
        return ClaimAuditLog.builder()
                .claimId(claim.getId())
                .claimNumber(claim.getClaimNumber())
                .action("DOCUMENT_UPLOADED")
                .newStatus(claim.getStatus())
                .performedBy(performedBy)
                .changeDescription("Document uploaded: " + documentName)
                .build();
    }
}
