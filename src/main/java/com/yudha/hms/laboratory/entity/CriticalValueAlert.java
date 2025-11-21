package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.AlertSeverity;
import com.yudha.hms.laboratory.constant.AlertType;
import com.yudha.hms.laboratory.constant.NotificationMethod;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Critical Value Alert Entity.
 *
 * Tracks critical value notifications and acknowledgments.
 * Ensures critical/panic values are communicated to clinicians
 * and documented with proper acknowledgment workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "critical_value_alert", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_critical_value_alert_result", columnList = "result_id"),
        @Index(name = "idx_critical_value_alert_patient", columnList = "patient_id"),
        @Index(name = "idx_critical_value_alert_type", columnList = "alert_type"),
        @Index(name = "idx_critical_value_alert_severity", columnList = "severity"),
        @Index(name = "idx_critical_value_alert_acknowledged", columnList = "acknowledged"),
        @Index(name = "idx_critical_value_alert_resolved", columnList = "resolved"),
        @Index(name = "idx_critical_value_alert_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalValueAlert {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Result reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private LabResult result;

    /**
     * Result parameter reference (if specific parameter)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_parameter_id")
    private LabResultParameter resultParameter;

    // ========== Alert Details ==========

    /**
     * Alert type (PANIC_VALUE, CRITICAL_VALUE, DELTA_CHECK)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 50)
    private AlertType alertType;

    /**
     * Severity (LOW, MEDIUM, HIGH, CRITICAL)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 50)
    private AlertSeverity severity;

    // ========== Test and Parameter ==========

    /**
     * Test name
     */
    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    /**
     * Parameter name
     */
    @Column(name = "parameter_name", length = 200)
    private String parameterName;

    /**
     * Result value
     */
    @Column(name = "result_value", nullable = false, length = 500)
    private String resultValue;

    /**
     * Critical threshold description
     */
    @Column(name = "critical_threshold", length = 200)
    private String criticalThreshold;

    // ========== Patient Information ==========

    /**
     * Patient ID
     */
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    /**
     * Patient name
     */
    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    // ========== Notification ==========

    /**
     * Notified to (doctor/nurse ID)
     */
    @Column(name = "notified_to", nullable = false)
    private UUID notifiedTo;

    /**
     * Notified to name
     */
    @Column(name = "notified_to_name", nullable = false, length = 200)
    private String notifiedToName;

    /**
     * Notified timestamp
     */
    @Column(name = "notified_at", nullable = false)
    @Builder.Default
    private LocalDateTime notifiedAt = LocalDateTime.now();

    /**
     * Notification method (PHONE, SMS, EMAIL, IN_PERSON, SYSTEM_ALERT)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", length = 50)
    private NotificationMethod notificationMethod;

    // ========== Acknowledgment ==========

    /**
     * Acknowledged
     */
    @Column(name = "acknowledged")
    @Builder.Default
    private Boolean acknowledged = false;

    /**
     * Acknowledged by user ID
     */
    @Column(name = "acknowledged_by")
    private UUID acknowledgedBy;

    /**
     * Acknowledged timestamp
     */
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    /**
     * Acknowledgment notes
     */
    @Column(name = "acknowledgment_notes", columnDefinition = "TEXT")
    private String acknowledgmentNotes;

    // ========== Clinical Action ==========

    /**
     * Action taken description
     */
    @Column(name = "action_taken", columnDefinition = "TEXT")
    private String actionTaken;

    /**
     * Action taken by user ID
     */
    @Column(name = "action_taken_by")
    private UUID actionTakenBy;

    /**
     * Action taken timestamp
     */
    @Column(name = "action_taken_at")
    private LocalDateTime actionTakenAt;

    // ========== Alert Resolution ==========

    /**
     * Resolved
     */
    @Column(name = "resolved")
    @Builder.Default
    private Boolean resolved = false;

    /**
     * Resolved timestamp
     */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /**
     * Resolution notes
     */
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    // ========== Audit Fields ==========

    /**
     * Created timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Created by user
     */
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /**
     * Version for optimistic locking
     */
    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ========== Helper Methods ==========

    /**
     * Check if alert has been acknowledged
     */
    public boolean isAcknowledged() {
        return Boolean.TRUE.equals(acknowledged);
    }

    /**
     * Check if alert has been resolved
     */
    public boolean isResolved() {
        return Boolean.TRUE.equals(resolved);
    }

    /**
     * Check if alert is pending acknowledgment
     */
    public boolean isPendingAcknowledgment() {
        return !isAcknowledged();
    }

    /**
     * Check if action has been taken
     */
    public boolean hasActionTaken() {
        return actionTaken != null && actionTakenAt != null;
    }

    /**
     * Mark as acknowledged
     */
    public void markAcknowledged(UUID acknowledgedBy, String notes) {
        this.acknowledged = true;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = LocalDateTime.now();
        this.acknowledgmentNotes = notes;
    }

    /**
     * Mark as resolved
     */
    public void markResolved(String resolutionNotes) {
        this.resolved = true;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNotes = resolutionNotes;
    }
}
