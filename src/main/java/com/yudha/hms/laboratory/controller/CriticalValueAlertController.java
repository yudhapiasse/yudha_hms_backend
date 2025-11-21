package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.AlertAcknowledgmentRequest;
import com.yudha.hms.laboratory.dto.response.AlertStatisticsResponse;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.CriticalValueAlertResponse;
import com.yudha.hms.laboratory.entity.CriticalValueAlert;
import com.yudha.hms.laboratory.service.CriticalValueAlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Critical Value Alert Controller.
 *
 * REST controller for managing critical value alerts.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/alerts")
@RequiredArgsConstructor
@Slf4j
public class CriticalValueAlertController {

    private final CriticalValueAlertService alertService;

    /**
     * Get alert by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CriticalValueAlertResponse>> getAlertById(
            @PathVariable UUID id) {
        log.info("Fetching critical value alert ID: {}", id);

        // Note: getAlertById not available in service, using workaround
        List<CriticalValueAlert> alerts = alertService.getUnacknowledgedAlerts();
        CriticalValueAlert alert = alerts.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));
        CriticalValueAlertResponse response = toResponse(alert);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get unacknowledged alerts
     */
    @GetMapping("/unacknowledged")
    public ResponseEntity<ApiResponse<List<CriticalValueAlertResponse>>> getUnacknowledgedAlerts() {
        log.info("Fetching unacknowledged critical value alerts");

        List<CriticalValueAlert> alerts = alertService.getUnacknowledgedAlerts();
        List<CriticalValueAlertResponse> responses = alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get alerts for a specific result
     */
    @GetMapping("/result/{resultId}")
    public ResponseEntity<ApiResponse<List<CriticalValueAlertResponse>>> getAlertsByResult(
            @PathVariable UUID resultId) {
        log.info("Fetching alerts for result ID: {}", resultId);

        List<CriticalValueAlert> alerts = alertService.getAlertHistory(resultId);
        List<CriticalValueAlertResponse> responses = alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get alerts for a specific patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<CriticalValueAlertResponse>>> getAlertsByPatient(
            @PathVariable UUID patientId) {
        log.info("Fetching alerts for patient ID: {}", patientId);

        List<CriticalValueAlert> alerts = alertService.getPatientAlerts(patientId);
        List<CriticalValueAlertResponse> responses = alerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Acknowledge alert
     */
    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<ApiResponse<CriticalValueAlertResponse>> acknowledgeAlert(
            @PathVariable UUID id,
            @Valid @RequestBody AlertAcknowledgmentRequest request) {
        log.info("Acknowledging critical value alert ID: {}", id);

        CriticalValueAlert alert = alertService.acknowledgeAlertWithNotes(id, request.getAcknowledgedBy(), request.getAcknowledgmentNotes());
        CriticalValueAlertResponse response = toResponse(alert);

        log.info("Alert acknowledged successfully");

        return ResponseEntity.ok(ApiResponse.success("Alert acknowledged successfully", response));
    }

    /**
     * Resolve alert
     */
    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<CriticalValueAlertResponse>> resolveAlert(
            @PathVariable UUID id,
            @RequestParam String resolutionNotes) {
        log.info("Resolving critical value alert ID: {}", id);

        CriticalValueAlert alert = alertService.resolveAlert(id, resolutionNotes);
        CriticalValueAlertResponse response = toResponse(alert);

        log.info("Alert resolved successfully");

        return ResponseEntity.ok(ApiResponse.success("Alert resolved successfully", response));
    }

    /**
     * Escalate unacknowledged alerts
     */
    @PostMapping("/escalate")
    public ResponseEntity<ApiResponse<List<CriticalValueAlertResponse>>> escalateAlerts(
            @RequestParam(defaultValue = "30") int thresholdMinutes) {
        log.info("Escalating unacknowledged alerts older than {} minutes", thresholdMinutes);

        List<CriticalValueAlert> escalatedAlerts = alertService.escalateUnacknowledgedAlerts(thresholdMinutes);
        List<CriticalValueAlertResponse> responses = escalatedAlerts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info("{} alerts escalated", escalatedAlerts.size());

        return ResponseEntity.ok(ApiResponse.success(
                String.format("%d alerts escalated", escalatedAlerts.size()),
                responses
        ));
    }

    /**
     * Get alert statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<AlertStatisticsResponse>> getAlertStatistics(
            @RequestParam(defaultValue = "30") int days) {
        log.info("Fetching alert statistics for last {} days", days);

        java.time.LocalDateTime endDate = java.time.LocalDateTime.now();
        java.time.LocalDateTime startDate = endDate.minusDays(days);
        com.yudha.hms.laboratory.service.CriticalValueAlertService.AlertStatistics stats =
            alertService.getAlertStatistics(startDate, endDate);

        // Convert to response DTO
        AlertStatisticsResponse statistics = new AlertStatisticsResponse();
        statistics.setTotalAlerts(stats.totalAlerts);
        statistics.setAcknowledgedAlerts(stats.acknowledgedCount);
        statistics.setPendingAcknowledgment(stats.unresolvedCount);
        statistics.setPanicValueAlerts(stats.panicValueCount);
        statistics.setCriticalValueAlerts(stats.criticalValueCount);
        statistics.setDeltaCheckAlerts(stats.deltaCheckCount);

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * Convert entity to response DTO
     */
    private CriticalValueAlertResponse toResponse(CriticalValueAlert alert) {
        CriticalValueAlertResponse response = new CriticalValueAlertResponse();
        response.setId(alert.getId());
        response.setResultId(alert.getResult() != null ? alert.getResult().getId() : null);
        response.setTestName(alert.getTestName());
        response.setParameterName(alert.getParameterName());
        response.setResultValue(alert.getResultValue());
        response.setCriticalThreshold(alert.getCriticalThreshold());
        response.setSeverity(alert.getSeverity());
        response.setAlertType(alert.getAlertType());
        response.setNotifiedAt(alert.getNotifiedAt());
        response.setNotifiedTo(alert.getNotifiedTo());
        response.setNotifiedToName(alert.getNotifiedToName());
        response.setNotificationMethod(alert.getNotificationMethod());
        response.setAcknowledged(alert.isAcknowledged());
        response.setAcknowledgedAt(alert.getAcknowledgedAt());
        response.setAcknowledgedBy(alert.getAcknowledgedBy());
        response.setAcknowledgmentNotes(alert.getAcknowledgmentNotes());
        response.setResolved(alert.isResolved());
        response.setResolvedAt(alert.getResolvedAt());
        response.setResolutionNotes(alert.getResolutionNotes());
        response.setCreatedAt(alert.getCreatedAt());
        response.setCreatedBy(alert.getCreatedBy());

        // Set patient details
        response.setPatientId(alert.getPatientId());
        response.setPatientName(alert.getPatientName());

        // Set result parameter ID if available
        if (alert.getResultParameter() != null) {
            response.setResultParameterId(alert.getResultParameter().getId());
        }

        return response;
    }
}
