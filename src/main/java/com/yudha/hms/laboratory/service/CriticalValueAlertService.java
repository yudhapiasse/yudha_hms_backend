package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.constant.AlertSeverity;
import com.yudha.hms.laboratory.constant.AlertType;
import com.yudha.hms.laboratory.constant.NotificationMethod;
import com.yudha.hms.laboratory.entity.CriticalValueAlert;
import com.yudha.hms.laboratory.entity.LabResult;
import com.yudha.hms.laboratory.entity.LabResultParameter;
import com.yudha.hms.laboratory.entity.LabTestParameter;
import com.yudha.hms.laboratory.repository.CriticalValueAlertRepository;
import com.yudha.hms.laboratory.repository.LabResultParameterRepository;
import com.yudha.hms.laboratory.repository.LabResultRepository;
import com.yudha.hms.laboratory.repository.LabTestParameterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing critical value alerts.
 *
 * Handles detection, notification, and tracking of critical/panic laboratory values
 * that require immediate clinical attention. Ensures proper documentation and
 * acknowledgment of critical value communications.
 *
 * Features:
 * - Automatic critical value detection
 * - Alert generation and tracking
 * - Multi-channel notification support
 * - Acknowledgment workflow
 * - Escalation for unacknowledged alerts
 * - Delta check alerts
 * - Alert history and reporting
 *
 * Critical Value Protocol:
 * 1. Detection: Automatically detect critical values based on test parameters
 * 2. Alert: Generate alert record with severity classification
 * 3. Notify: Notify ordering physician and nursing station
 * 4. Document: Track notification method and recipient
 * 5. Acknowledge: Require acknowledgment from recipient
 * 6. Escalate: Escalate if not acknowledged within threshold
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CriticalValueAlertService {

    private final LabResultRepository labResultRepository;
    private final LabResultParameterRepository labResultParameterRepository;
    private final LabTestParameterRepository labTestParameterRepository;
    private final CriticalValueAlertRepository criticalValueAlertRepository;

    /**
     * Check for critical values in a result and generate alerts.
     *
     * @param resultId Result ID
     * @return List of generated alerts
     * @throws IllegalArgumentException if result not found
     */
    public List<CriticalValueAlert> checkForCriticalValues(UUID resultId) {
        log.info("Checking for critical values in result: {}", resultId);

        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        List<CriticalValueAlert> alerts = new ArrayList<>();

        // Get all parameters for this result
        List<LabResultParameter> parameters = labResultParameterRepository.findByResultId(resultId);

        // Check each parameter for critical values
        for (LabResultParameter parameter : parameters) {
            if (parameter.isCritical()) {
                CriticalValueAlert alert = generateCriticalValueAlert(
                        parameter.getId(),
                        determineCriticalAlertType(parameter),
                        buildCriticalAlertMessage(parameter)
                );
                alerts.add(alert);
            }
        }

        // Check for delta check flags
        List<LabResultParameter> deltaCheckParams = labResultParameterRepository.findParametersWithDeltaCheckFlags(resultId);
        for (LabResultParameter parameter : deltaCheckParams) {
            CriticalValueAlert alert = generateDeltaCheckAlert(parameter);
            alerts.add(alert);
        }

        // Update result flags
        if (!alerts.isEmpty()) {
            result.setHasPanicValues(true);
            labResultRepository.save(result);
            log.warn("Critical values detected in result: {}. Alert count: {}", resultId, alerts.size());
        }

        return alerts;
    }

    /**
     * Generate a critical value alert.
     *
     * @param resultParameterId Result parameter ID
     * @param alertType Alert type (PANIC_VALUE, CRITICAL_VALUE, DELTA_CHECK)
     * @param alertMessage Alert message
     * @return Created alert
     * @throws IllegalArgumentException if result parameter not found
     */
    public CriticalValueAlert generateCriticalValueAlert(UUID resultParameterId, AlertType alertType, String alertMessage) {
        log.info("Generating critical value alert for parameter: {}, type: {}", resultParameterId, alertType);

        LabResultParameter parameter = labResultParameterRepository.findById(resultParameterId)
                .orElseThrow(() -> new IllegalArgumentException("Result parameter not found with ID: " + resultParameterId));

        LabResult result = parameter.getResult();
        UUID patientId = result.getOrder().getPatientId();
        UUID orderingDoctorId = result.getOrder().getOrderingDoctorId();

        // Determine severity
        AlertSeverity severity = determineAlertSeverity(parameter, alertType);

        // Get critical threshold description
        String criticalThreshold = buildCriticalThresholdDescription(parameter);

        // Create alert
        CriticalValueAlert alert = CriticalValueAlert.builder()
                .result(result)
                .resultParameter(parameter)
                .alertType(alertType)
                .severity(severity)
                .testName(result.getTestName())
                .parameterName(parameter.getParameterName())
                .resultValue(parameter.getResultValue())
                .criticalThreshold(criticalThreshold)
                .patientId(patientId)
                .patientName("Patient-" + patientId) // TODO: Get actual patient name
                .notifiedTo(orderingDoctorId)
                .notifiedToName("Doctor-" + orderingDoctorId) // TODO: Get actual doctor name
                .notifiedAt(LocalDateTime.now())
                .notificationMethod(NotificationMethod.SYSTEM_ALERT) // Default, will be updated when actual notification sent
                .acknowledged(false)
                .resolved(false)
                .createdBy("SYSTEM")
                .build();

        alert = criticalValueAlertRepository.save(alert);

        log.info("Critical value alert generated. Alert ID: {}", alert.getId());
        return alert;
    }

    /**
     * Generate a delta check alert.
     *
     * @param parameter Result parameter with delta check flag
     * @return Created alert
     */
    public CriticalValueAlert generateDeltaCheckAlert(LabResultParameter parameter) {
        log.info("Generating delta check alert for parameter: {}", parameter.getId());

        LabResult result = parameter.getResult();
        UUID patientId = result.getOrder().getPatientId();
        UUID orderingDoctorId = result.getOrder().getOrderingDoctorId();

        String alertMessage = String.format(
                "Unusual change detected: %s changed from %.2f to %.2f (%.1f%% change)",
                parameter.getParameterName(),
                parameter.getPreviousValue(),
                parameter.getNumericValue(),
                parameter.getDeltaPercentage()
        );

        CriticalValueAlert alert = CriticalValueAlert.builder()
                .result(result)
                .resultParameter(parameter)
                .alertType(AlertType.DELTA_CHECK)
                .severity(AlertSeverity.MEDIUM)
                .testName(result.getTestName())
                .parameterName(parameter.getParameterName())
                .resultValue(parameter.getResultValue())
                .criticalThreshold(String.format("Previous: %.2f, Change: %.1f%%",
                        parameter.getPreviousValue(), parameter.getDeltaPercentage()))
                .patientId(patientId)
                .patientName("Patient-" + patientId)
                .notifiedTo(orderingDoctorId)
                .notifiedToName("Doctor-" + orderingDoctorId)
                .notifiedAt(LocalDateTime.now())
                .notificationMethod(NotificationMethod.SYSTEM_ALERT)
                .acknowledged(false)
                .resolved(false)
                .createdBy("SYSTEM")
                .build();

        alert = criticalValueAlertRepository.save(alert);

        log.info("Delta check alert generated. Alert ID: {}", alert.getId());
        return alert;
    }

    /**
     * Notify ordering physician about critical value.
     * TODO: Integrate with actual notification service.
     *
     * @param resultId Result ID
     * @param doctorId Doctor ID to notify
     * @return List of alerts that were notified
     */
    public List<CriticalValueAlert> notifyOrderingPhysician(UUID resultId, UUID doctorId) {
        log.info("Notifying ordering physician about critical values. Result: {}, Doctor: {}", resultId, doctorId);

        List<CriticalValueAlert> alerts = criticalValueAlertRepository.findByResultIdOrderByCreatedAtDesc(resultId);

        for (CriticalValueAlert alert : alerts) {
            if (!alert.isAcknowledged()) {
                // TODO: Send actual notification via email, SMS, or in-app notification
                // For now, just log
                log.warn("CRITICAL VALUE ALERT: {} - {} = {} (Threshold: {})",
                        alert.getTestName(),
                        alert.getParameterName(),
                        alert.getResultValue(),
                        alert.getCriticalThreshold());

                // Update notification method (this would be set based on actual notification sent)
                alert.setNotificationMethod(NotificationMethod.SYSTEM_ALERT);
            }
        }

        // Update result
        LabResult result = labResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("Result not found with ID: " + resultId));

        result.setPanicValueNotified(true);
        result.setPanicValueNotifiedAt(LocalDateTime.now());
        result.setPanicValueNotifiedTo("Doctor-" + doctorId);
        labResultRepository.save(result);

        return alerts;
    }

    /**
     * Acknowledge a critical value alert.
     *
     * @param alertId Alert ID
     * @param acknowledgedBy User ID who acknowledged
     * @param acknowledgedAt Acknowledgment timestamp
     * @return Updated alert
     * @throws IllegalArgumentException if alert not found
     */
    public CriticalValueAlert acknowledgeAlert(UUID alertId, UUID acknowledgedBy, LocalDateTime acknowledgedAt) {
        log.info("Acknowledging alert: {} by user: {}", alertId, acknowledgedBy);

        CriticalValueAlert alert = criticalValueAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with ID: " + alertId));

        if (alert.isAcknowledged()) {
            log.warn("Alert already acknowledged: {}", alertId);
            return alert;
        }

        alert.setAcknowledged(true);
        alert.setAcknowledgedBy(acknowledgedBy);
        alert.setAcknowledgedAt(acknowledgedAt != null ? acknowledgedAt : LocalDateTime.now());

        alert = criticalValueAlertRepository.save(alert);

        log.info("Alert acknowledged successfully: {}", alertId);
        return alert;
    }

    /**
     * Acknowledge an alert with notes.
     *
     * @param alertId Alert ID
     * @param acknowledgedBy User ID who acknowledged
     * @param notes Acknowledgment notes
     * @return Updated alert
     */
    public CriticalValueAlert acknowledgeAlertWithNotes(UUID alertId, UUID acknowledgedBy, String notes) {
        CriticalValueAlert alert = acknowledgeAlert(alertId, acknowledgedBy, LocalDateTime.now());
        alert.setAcknowledgmentNotes(notes);
        return criticalValueAlertRepository.save(alert);
    }

    /**
     * Record action taken for an alert.
     *
     * @param alertId Alert ID
     * @param actionTaken Description of action taken
     * @param actionTakenBy User ID who took action
     * @return Updated alert
     * @throws IllegalArgumentException if alert not found
     */
    public CriticalValueAlert recordActionTaken(UUID alertId, String actionTaken, UUID actionTakenBy) {
        log.info("Recording action for alert: {}", alertId);

        CriticalValueAlert alert = criticalValueAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with ID: " + alertId));

        alert.setActionTaken(actionTaken);
        alert.setActionTakenBy(actionTakenBy);
        alert.setActionTakenAt(LocalDateTime.now());

        alert = criticalValueAlertRepository.save(alert);

        log.info("Action recorded for alert: {}", alertId);
        return alert;
    }

    /**
     * Resolve an alert.
     *
     * @param alertId Alert ID
     * @param resolutionNotes Resolution notes
     * @return Updated alert
     * @throws IllegalArgumentException if alert not found
     */
    public CriticalValueAlert resolveAlert(UUID alertId, String resolutionNotes) {
        log.info("Resolving alert: {}", alertId);

        CriticalValueAlert alert = criticalValueAlertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found with ID: " + alertId));

        if (!alert.isAcknowledged()) {
            log.warn("Resolving alert that has not been acknowledged: {}", alertId);
        }

        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolutionNotes(resolutionNotes);

        alert = criticalValueAlertRepository.save(alert);

        log.info("Alert resolved successfully: {}", alertId);
        return alert;
    }

    /**
     * Get all unacknowledged alerts.
     *
     * @return List of unacknowledged alerts ordered by severity and creation time
     */
    @Transactional(readOnly = true)
    public List<CriticalValueAlert> getUnacknowledgedAlerts() {
        return criticalValueAlertRepository.findUnacknowledgedAlerts();
    }

    /**
     * Get alert history for a result.
     *
     * @param resultId Result ID
     * @return List of alerts for the result
     */
    @Transactional(readOnly = true)
    public List<CriticalValueAlert> getAlertHistory(UUID resultId) {
        return criticalValueAlertRepository.findByResultIdOrderByCreatedAtDesc(resultId);
    }

    /**
     * Get alerts for a patient.
     *
     * @param patientId Patient ID
     * @return List of alerts for the patient
     */
    @Transactional(readOnly = true)
    public List<CriticalValueAlert> getPatientAlerts(UUID patientId) {
        return criticalValueAlertRepository.findByPatientIdOrderByCreatedAtDesc(patientId, null)
                .getContent();
    }

    /**
     * Escalate unacknowledged alerts.
     * Identifies alerts that have not been acknowledged within the threshold time
     * and triggers escalation.
     *
     * @param minutesThreshold Time threshold in minutes
     * @return List of escalated alerts
     */
    public List<CriticalValueAlert> escalateUnacknowledgedAlerts(int minutesThreshold) {
        log.info("Checking for alerts to escalate (threshold: {} minutes)", minutesThreshold);

        List<CriticalValueAlert> unacknowledgedAlerts = getUnacknowledgedAlerts();
        List<CriticalValueAlert> escalatedAlerts = new ArrayList<>();
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(minutesThreshold);

        for (CriticalValueAlert alert : unacknowledgedAlerts) {
            if (alert.getNotifiedAt().isBefore(cutoffTime)) {
                log.warn("Escalating unacknowledged alert: {} (created: {}, severity: {})",
                        alert.getId(), alert.getCreatedAt(), alert.getSeverity());

                // TODO: Implement actual escalation logic
                // This might involve:
                // 1. Notifying senior staff or supervisor
                // 2. Sending additional notifications via different channels
                // 3. Creating escalation records
                // 4. Updating alert priority

                // For now, add to escalation notes
                String escalationNote = String.format(
                        "ESCALATED at %s: Alert unacknowledged for %d minutes. Severity: %s",
                        LocalDateTime.now(),
                        java.time.Duration.between(alert.getNotifiedAt(), LocalDateTime.now()).toMinutes(),
                        alert.getSeverity()
                );

                alert.setAcknowledgmentNotes(
                        (alert.getAcknowledgmentNotes() != null ? alert.getAcknowledgmentNotes() + "\n\n" : "") +
                        escalationNote
                );

                criticalValueAlertRepository.save(alert);
                escalatedAlerts.add(alert);
            }
        }

        log.info("Escalated {} alerts out of {} unacknowledged", escalatedAlerts.size(), unacknowledgedAlerts.size());
        return escalatedAlerts;
    }

    /**
     * Get alert statistics for a date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Alert statistics
     */
    @Transactional(readOnly = true)
    public AlertStatistics getAlertStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<CriticalValueAlert> alerts = criticalValueAlertRepository.findAlertsByDateRange(startDate, endDate);

        AlertStatistics stats = new AlertStatistics();
        stats.totalAlerts = alerts.size();
        stats.acknowledgedCount = alerts.stream().filter(CriticalValueAlert::isAcknowledged).count();
        stats.unresolvedCount = alerts.stream().filter(a -> !a.isResolved()).count();
        stats.panicValueCount = alerts.stream().filter(a -> a.getAlertType() == AlertType.PANIC_VALUE).count();
        stats.criticalValueCount = alerts.stream().filter(a -> a.getAlertType() == AlertType.CRITICAL_VALUE).count();
        stats.deltaCheckCount = alerts.stream().filter(a -> a.getAlertType() == AlertType.DELTA_CHECK).count();

        return stats;
    }

    /**
     * Determine alert type based on parameter values.
     *
     * @param parameter Result parameter
     * @return Alert type
     */
    private AlertType determineCriticalAlertType(LabResultParameter parameter) {
        LabTestParameter testParam = parameter.getTestParameter();

        // Check if it's a panic value (most critical)
        if (testParam.isPanicValue(parameter.getNumericValue())) {
            return AlertType.PANIC_VALUE;
        }

        // Otherwise it's a critical value
        return AlertType.CRITICAL_VALUE;
    }

    /**
     * Determine alert severity.
     *
     * @param parameter Result parameter
     * @param alertType Alert type
     * @return Alert severity
     */
    private AlertSeverity determineAlertSeverity(LabResultParameter parameter, AlertType alertType) {
        if (alertType == AlertType.PANIC_VALUE) {
            return AlertSeverity.CRITICAL;
        } else if (alertType == AlertType.CRITICAL_VALUE) {
            return AlertSeverity.HIGH;
        } else if (alertType == AlertType.DELTA_CHECK) {
            // Determine based on delta percentage
            if (parameter.getDeltaPercentage() != null &&
                parameter.getDeltaPercentage().compareTo(new BigDecimal("100")) > 0) {
                return AlertSeverity.HIGH;
            }
            return AlertSeverity.MEDIUM;
        }
        return AlertSeverity.LOW;
    }

    /**
     * Build critical alert message.
     *
     * @param parameter Result parameter
     * @return Alert message
     */
    private String buildCriticalAlertMessage(LabResultParameter parameter) {
        return String.format(
                "Critical value detected: %s = %s (Normal range: %s)",
                parameter.getParameterName(),
                parameter.getResultValue(),
                parameter.getReferenceRangeText() != null ? parameter.getReferenceRangeText() :
                        String.format("%.2f - %.2f %s", parameter.getReferenceRangeLow(),
                                parameter.getReferenceRangeHigh(), parameter.getUnit())
        );
    }

    /**
     * Build critical threshold description.
     *
     * @param parameter Result parameter
     * @return Threshold description
     */
    private String buildCriticalThresholdDescription(LabResultParameter parameter) {
        LabTestParameter testParam = parameter.getTestParameter();

        if (testParam.getPanicLow() != null || testParam.getPanicHigh() != null) {
            return String.format("Panic: < %.2f or > %.2f %s",
                    testParam.getPanicLow(), testParam.getPanicHigh(), parameter.getUnit());
        } else if (testParam.getCriticalLow() != null || testParam.getCriticalHigh() != null) {
            return String.format("Critical: < %.2f or > %.2f %s",
                    testParam.getCriticalLow(), testParam.getCriticalHigh(), parameter.getUnit());
        }

        return "Critical threshold exceeded";
    }

    /**
     * Alert statistics DTO.
     */
    public static class AlertStatistics {
        public long totalAlerts;
        public long acknowledgedCount;
        public long unresolvedCount;
        public long panicValueCount;
        public long criticalValueCount;
        public long deltaCheckCount;
        public double acknowledgmentRate;
        public double averageAcknowledgmentTimeMinutes;
    }
}
