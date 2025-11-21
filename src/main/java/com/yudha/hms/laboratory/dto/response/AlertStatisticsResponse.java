package com.yudha.hms.laboratory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Alert Statistics Response DTO.
 *
 * Response for critical value alert statistics and metrics.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertStatisticsResponse {

    /**
     * Period start date
     */
    private LocalDate periodStart;

    /**
     * Period end date
     */
    private LocalDate periodEnd;

    // ========== Alert Counts ==========

    /**
     * Total alerts
     */
    private Long totalAlerts;

    /**
     * Panic value alerts
     */
    private Long panicValueAlerts;

    /**
     * Critical value alerts
     */
    private Long criticalValueAlerts;

    /**
     * Delta check alerts
     */
    private Long deltaCheckAlerts;

    // ========== By Severity ==========

    /**
     * Critical severity alerts
     */
    private Long criticalSeverityAlerts;

    /**
     * High severity alerts
     */
    private Long highSeverityAlerts;

    /**
     * Medium severity alerts
     */
    private Long mediumSeverityAlerts;

    /**
     * Low severity alerts
     */
    private Long lowSeverityAlerts;

    // ========== Status ==========

    /**
     * Pending acknowledgment
     */
    private Long pendingAcknowledgment;

    /**
     * Acknowledged alerts
     */
    private Long acknowledgedAlerts;

    /**
     * Resolved alerts
     */
    private Long resolvedAlerts;

    // ========== Response Time Metrics ==========

    /**
     * Average time to acknowledgment (minutes)
     */
    private Double avgTimeToAcknowledgmentMinutes;

    /**
     * Average time to resolution (minutes)
     */
    private Double avgTimeToResolutionMinutes;

    /**
     * Median time to acknowledgment (minutes)
     */
    private Double medianTimeToAcknowledgmentMinutes;

    /**
     * Median time to resolution (minutes)
     */
    private Double medianTimeToResolutionMinutes;

    // ========== Top Tests ==========

    /**
     * Most frequent test generating alerts
     */
    private String topAlertGeneratingTest;

    /**
     * Count of alerts for top test
     */
    private Long topAlertGeneratingTestCount;

    // ========== Compliance ==========

    /**
     * Percentage of alerts acknowledged within target time
     */
    private Double acknowledgmentCompliancePercentage;

    /**
     * Target acknowledgment time (minutes)
     */
    private Integer targetAcknowledgmentTimeMinutes;

    /**
     * Percentage of alerts resolved within target time
     */
    private Double resolutionCompliancePercentage;

    /**
     * Target resolution time (minutes)
     */
    private Integer targetResolutionTimeMinutes;
}
