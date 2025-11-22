package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.radiology.constant.reporting.FindingSeverity;
import com.yudha.hms.radiology.constant.reporting.NotificationMethod;
import com.yudha.hms.radiology.constant.reporting.NotificationPriority;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "critical_finding_notification", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriticalFindingNotification extends SoftDeletableEntity {

    @Column(name = "report_id", nullable = false)
    private UUID reportId;

    @Column(name = "finding_description", columnDefinition = "TEXT", nullable = false)
    private String findingDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "finding_severity", length = 20, nullable = false)
    private FindingSeverity findingSeverity;

    @Column(name = "finding_category", length = 50)
    private String findingCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20, nullable = false)
    private NotificationPriority priority = NotificationPriority.HIGH;

    @Column(name = "requires_immediate_action")
    private Boolean requiresImmediateAction = true;

    @Column(name = "recommended_action", columnDefinition = "TEXT")
    private String recommendedAction;

    @Column(name = "notified_to", length = 200, nullable = false)
    private String notifiedTo;

    @Column(name = "notified_to_role", length = 50)
    private String notifiedToRole;

    @Column(name = "notified_to_contact", length = 100)
    private String notifiedToContact;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_method", length = 30, nullable = false)
    private NotificationMethod notificationMethod;

    @Column(name = "notification_attempts")
    private Integer notificationAttempts = 1;

    @Column(name = "acknowledged")
    private Boolean acknowledged = false;

    @Column(name = "acknowledged_by", length = 200)
    private String acknowledgedBy;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "acknowledgment_method", length = 30)
    private String acknowledgmentMethod;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_instructions", columnDefinition = "TEXT")
    private String followUpInstructions;

    @Column(name = "follow_up_completed")
    private Boolean followUpCompleted = false;

    @Column(name = "follow_up_completed_at")
    private LocalDateTime followUpCompletedAt;

    @Column(name = "communication_notes", columnDefinition = "TEXT")
    private String communicationNotes;

    @Column(name = "read_back_verified")
    private Boolean readBackVerified = false;

    @Column(name = "time_to_notification_minutes")
    private Integer timeToNotificationMinutes;

    @Column(name = "notification_within_policy")
    private Boolean notificationWithinPolicy;

    @Column(name = "notified_by", nullable = false)
    private UUID notifiedBy;

    @Column(name = "notified_at", nullable = false)
    private LocalDateTime notifiedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
