package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Document Audit Log Entity.
 *
 * Maintains comprehensive audit trail for all document operations including
 * creation, viewing, modification, printing, and distribution.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "document_audit_logs", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_doc_audit_document", columnList = "document_id"),
        @Index(name = "idx_doc_audit_user", columnList = "user_id"),
        @Index(name = "idx_doc_audit_action", columnList = "action_type"),
        @Index(name = "idx_doc_audit_timestamp", columnList = "action_timestamp"),
        @Index(name = "idx_doc_audit_ip", columnList = "ip_address")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Comprehensive audit trail for document operations")
public class DocumentAuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Document Reference ==========
    @Column(name = "document_id", nullable = false)
    @NotNull(message = "Document ID is required")
    private UUID documentId;

    @Column(name = "document_number", length = 50)
    private String documentNumber;

    @Column(name = "document_type", length = 50)
    private String documentType;

    // ========== Action Details ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    @NotNull(message = "Action type is required")
    private AuditAction actionType;

    @Column(name = "action_timestamp", nullable = false)
    @NotNull(message = "Action timestamp is required")
    @Builder.Default
    private LocalDateTime actionTimestamp = LocalDateTime.now();

    @Column(name = "action_description", columnDefinition = "TEXT")
    private String actionDescription;

    // ========== User Information ==========
    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID is required")
    private UUID userId;

    @Column(name = "user_name", nullable = false, length = 200)
    @NotBlank(message = "User name is required")
    private String userName;

    @Column(name = "user_role", length = 100)
    private String userRole;

    @Column(name = "user_department", length = 100)
    private String userDepartment;

    // ========== Session and Security ==========
    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_type", length = 50)
    private String deviceType; // DESKTOP, MOBILE, TABLET

    @Column(name = "location", length = 200)
    private String location; // Physical location if available

    // ========== Change Tracking ==========
    @Column(name = "before_value", columnDefinition = "TEXT")
    private String beforeValue; // JSON of values before change

    @Column(name = "after_value", columnDefinition = "TEXT")
    private String afterValue; // JSON of values after change

    @Column(name = "changed_fields", columnDefinition = "TEXT")
    private String changedFields; // JSON array of changed field names

    // ========== Operation Context ==========
    @Column(name = "operation_status", length = 20)
    @Builder.Default
    private String operationStatus = "SUCCESS"; // SUCCESS, FAILURE, PARTIAL

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "duration_ms")
    private Long durationMs; // Operation duration in milliseconds

    // ========== Additional Context ==========
    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "encounter_id")
    private UUID encounterId;

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // ENCOUNTER, PROCEDURE, PRESCRIPTION, etc.

    @Column(name = "related_entity_id")
    private UUID relatedEntityId;

    // ========== Access Control ==========
    @Column(name = "access_granted")
    @Builder.Default
    private Boolean accessGranted = true;

    @Column(name = "access_denial_reason", columnDefinition = "TEXT")
    private String accessDenialReason;

    @Column(name = "security_level", length = 20)
    private String securityLevel;

    // ========== Compliance and Regulation ==========
    @Column(name = "is_hipaa_relevant")
    @Builder.Default
    private Boolean isHipaaRelevant = false;

    @Column(name = "is_gdpr_relevant")
    @Builder.Default
    private Boolean isGdprRelevant = false;

    @Column(name = "retention_period_days")
    private Integer retentionPeriodDays;

    // ========== Metadata ==========
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Audit Action Enum ==========
    public enum AuditAction {
        // Document Lifecycle
        CREATED("Document Created"),
        UPDATED("Document Updated"),
        DELETED("Document Deleted"),
        VOIDED("Document Voided"),
        RESTORED("Document Restored"),

        // Versioning
        VERSION_CREATED("New Version Created"),
        VERSION_COMPARED("Versions Compared"),

        // Viewing and Access
        VIEWED("Document Viewed"),
        DOWNLOADED("Document Downloaded"),
        PREVIEWED("Document Previewed"),
        ACCESSED("Document Accessed"),
        ACCESS_DENIED("Access Denied"),

        // Printing and Distribution
        PRINTED("Document Printed"),
        EMAILED("Document Emailed"),
        FAXED("Document Faxed"),
        SENT_SMS("Document Sent via SMS"),
        SHARED("Document Shared"),

        // Signatures
        SIGNATURE_REQUESTED("Signature Requested"),
        SIGNATURE_ADDED("Signature Added"),
        SIGNATURE_VERIFIED("Signature Verified"),
        SIGNATURE_REJECTED("Signature Rejected"),
        SIGNATURE_REVOKED("Signature Revoked"),

        // PDF Operations
        PDF_GENERATED("PDF Generated"),
        PDF_REGENERATED("PDF Regenerated"),

        // Status Changes
        STATUS_CHANGED("Status Changed"),
        APPROVED("Document Approved"),
        REJECTED("Document Rejected"),
        ISSUED("Document Issued"),
        EXPIRED("Document Expired"),

        // Security
        ENCRYPTED("Document Encrypted"),
        DECRYPTED("Document Decrypted"),
        WATERMARK_APPLIED("Watermark Applied"),

        // External Integration
        EXPORTED("Document Exported"),
        IMPORTED("Document Imported"),
        SYNCHRONIZED("Document Synchronized"),

        // Administrative
        ARCHIVED("Document Archived"),
        UNARCHIVED("Document Unarchived"),
        LOCKED("Document Locked"),
        UNLOCKED("Document Unlocked");

        private final String description;

        AuditAction(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ========== Business Methods ==========

    /**
     * Create audit log for document creation.
     */
    public static DocumentAuditLog forCreation(ClinicalDocument document, UUID userId, String userName) {
        return DocumentAuditLog.builder()
                .documentId(document.getId())
                .documentNumber(document.getDocumentNumber())
                .documentType(document.getDocumentType().name())
                .actionType(AuditAction.CREATED)
                .actionDescription("Document created: " + document.getDocumentTitle())
                .userId(userId)
                .userName(userName)
                .patientId(document.getPatientId())
                .encounterId(document.getEncounterId())
                .operationStatus("SUCCESS")
                .build();
    }

    /**
     * Create audit log for document viewing.
     */
    public static DocumentAuditLog forViewing(ClinicalDocument document, UUID userId, String userName, String ipAddress) {
        return DocumentAuditLog.builder()
                .documentId(document.getId())
                .documentNumber(document.getDocumentNumber())
                .documentType(document.getDocumentType().name())
                .actionType(AuditAction.VIEWED)
                .actionDescription("Document viewed")
                .userId(userId)
                .userName(userName)
                .ipAddress(ipAddress)
                .patientId(document.getPatientId())
                .encounterId(document.getEncounterId())
                .operationStatus("SUCCESS")
                .build();
    }

    /**
     * Create audit log for document printing.
     */
    public static DocumentAuditLog forPrinting(ClinicalDocument document, UUID userId, String userName) {
        return DocumentAuditLog.builder()
                .documentId(document.getId())
                .documentNumber(document.getDocumentNumber())
                .documentType(document.getDocumentType().name())
                .actionType(AuditAction.PRINTED)
                .actionDescription("Document printed (count: " + document.getPrintCount() + ")")
                .userId(userId)
                .userName(userName)
                .patientId(document.getPatientId())
                .encounterId(document.getEncounterId())
                .operationStatus("SUCCESS")
                .build();
    }
}
