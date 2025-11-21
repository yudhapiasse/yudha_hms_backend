package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.AlertSeverity;
import com.yudha.hms.laboratory.constant.AlertType;
import com.yudha.hms.laboratory.constant.NotificationMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Critical Value Alert Response DTO.
 *
 * Response for critical value alerts with full details.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriticalValueAlertResponse {

    /**
     * Alert ID
     */
    private UUID id;

    /**
     * Result ID
     */
    private UUID resultId;

    /**
     * Result number
     */
    private String resultNumber;

    /**
     * Result parameter ID
     */
    private UUID resultParameterId;

    // ========== Alert Details ==========

    /**
     * Alert type
     */
    private AlertType alertType;

    /**
     * Severity
     */
    private AlertSeverity severity;

    // ========== Test and Parameter ==========

    /**
     * Test name
     */
    private String testName;

    /**
     * Parameter name
     */
    private String parameterName;

    /**
     * Result value
     */
    private String resultValue;

    /**
     * Critical threshold
     */
    private String criticalThreshold;

    // ========== Patient Information ==========

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Patient name
     */
    private String patientName;

    /**
     * Patient MRN
     */
    private String patientMrn;

    /**
     * Patient location
     */
    private String patientLocation;

    // ========== Notification ==========

    /**
     * Notified to user ID
     */
    private UUID notifiedTo;

    /**
     * Notified to name
     */
    private String notifiedToName;

    /**
     * Notified at
     */
    private LocalDateTime notifiedAt;

    /**
     * Notification method
     */
    private NotificationMethod notificationMethod;

    // ========== Acknowledgment ==========

    /**
     * Acknowledged
     */
    private Boolean acknowledged;

    /**
     * Acknowledged by user ID
     */
    private UUID acknowledgedBy;

    /**
     * Acknowledged by name
     */
    private String acknowledgedByName;

    /**
     * Acknowledged at
     */
    private LocalDateTime acknowledgedAt;

    /**
     * Acknowledgment notes
     */
    private String acknowledgmentNotes;

    // ========== Clinical Action ==========

    /**
     * Action taken
     */
    private String actionTaken;

    /**
     * Action taken by user ID
     */
    private UUID actionTakenBy;

    /**
     * Action taken by name
     */
    private String actionTakenByName;

    /**
     * Action taken at
     */
    private LocalDateTime actionTakenAt;

    // ========== Alert Resolution ==========

    /**
     * Resolved
     */
    private Boolean resolved;

    /**
     * Resolved at
     */
    private LocalDateTime resolvedAt;

    /**
     * Resolution notes
     */
    private String resolutionNotes;

    // ========== Time Metrics ==========

    /**
     * Time to acknowledgment in minutes
     */
    private Long timeToAcknowledgmentMinutes;

    /**
     * Time to resolution in minutes
     */
    private Long timeToResolutionMinutes;

    // ========== Audit Fields ==========

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;
}
