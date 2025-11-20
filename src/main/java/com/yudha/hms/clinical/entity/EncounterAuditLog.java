package com.yudha.hms.clinical.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Audit Log Entity.
 *
 * Tracks all changes to encounters for compliance and security.
 * Records status changes, modifications, and access to sensitive encounters.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "encounter_audit_logs", indexes = {
    @Index(name = "idx_audit_encounter_id", columnList = "encounter_id"),
    @Index(name = "idx_audit_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
    @Index(name = "idx_audit_action_type", columnList = "action_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;

    @Column(name = "encounter_number", length = 50)
    private String encounterNumber;

    @Column(name = "patient_id")
    private UUID patientId;

    @Column(name = "action_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AuditActionType actionType;

    @Column(name = "action_description", columnDefinition = "TEXT")
    private String actionDescription;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "field_changed", length = 100)
    private String fieldChanged;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "user_role", length = 50)
    private String userRole;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "timestamp", nullable = false)
    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column(name = "is_sensitive_access")
    private Boolean isSensitiveAccess; // For VIP, psychiatric encounters

    @Column(name = "access_reason", columnDefinition = "TEXT")
    private String accessReason; // Required for sensitive access

    @Column(name = "supervisor_override")
    private Boolean supervisorOverride;

    @Column(name = "supervisor_id")
    private UUID supervisorId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    /**
     * Audit Action Types.
     */
    public enum AuditActionType {
        CREATED,
        VIEWED,
        UPDATED,
        STATUS_CHANGED,
        DELETED,
        CANCELLED,
        REOPENED,
        CONVERTED,
        PARTICIPANT_ADDED,
        PARTICIPANT_REMOVED,
        DIAGNOSIS_ADDED,
        DIAGNOSIS_UPDATED,
        DIAGNOSIS_REMOVED,
        SENSITIVE_ACCESS,
        SUPERVISOR_OVERRIDE,
        EXPORTED,
        PRINTED,
        ARCHIVED,
        LEGAL_HOLD_APPLIED,
        LEGAL_HOLD_REMOVED
    }
}
