package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.VitalSignsChartDto;
import com.yudha.hms.clinical.dto.VitalSignsRequest;
import com.yudha.hms.clinical.dto.VitalSignsResponse;
import com.yudha.hms.clinical.service.VitalSignsService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Vital Signs Controller.
 * REST API endpoints for vital signs monitoring.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical")
@RequiredArgsConstructor
@Slf4j
public class VitalSignsController {

    private final VitalSignsService vitalSignsService;

    /**
     * POST /api/clinical/encounters/{encounterId}/vital-signs
     * Record vital signs.
     */
    @PostMapping("/encounters/{encounterId}/vital-signs")
    public ResponseEntity<ApiResponse<VitalSignsResponse>> recordVitalSigns(
        @PathVariable UUID encounterId,
        @Valid @RequestBody VitalSignsRequest request
    ) {
        log.info("POST /api/clinical/encounters/{}/vital-signs - Recording vital signs", encounterId);

        VitalSignsResponse response = vitalSignsService.recordVitalSigns(encounterId, request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.<VitalSignsResponse>builder()
                .success(true)
                .message("Vital signs berhasil dicatat" +
                    (Boolean.TRUE.equals(response.getRequiresNotification()) ?
                        " - PERHATIAN: Nilai vital signs kritis!" : ""))
                .data(response)
                .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/vital-signs
     * Get all vital signs for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/vital-signs")
    public ResponseEntity<ApiResponse<List<VitalSignsResponse>>> getVitalSignsByEncounter(
        @PathVariable UUID encounterId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        log.info("GET /api/clinical/encounters/{}/vital-signs", encounterId);

        List<VitalSignsResponse> vitalSigns;
        if (startDate != null && endDate != null) {
            vitalSigns = vitalSignsService.getVitalSignsByDateRange(encounterId, startDate, endDate);
        } else {
            vitalSigns = vitalSignsService.getVitalSignsByEncounter(encounterId);
        }

        return ResponseEntity.ok(ApiResponse.<List<VitalSignsResponse>>builder()
            .success(true)
            .message("Daftar vital signs berhasil diambil")
            .data(vitalSigns)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/vital-signs/latest
     * Get latest vital signs for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/vital-signs/latest")
    public ResponseEntity<ApiResponse<VitalSignsResponse>> getLatestVitalSigns(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/vital-signs/latest", encounterId);

        VitalSignsResponse response = vitalSignsService.getLatestVitalSigns(encounterId);

        return ResponseEntity.ok(ApiResponse.<VitalSignsResponse>builder()
            .success(true)
            .message("Vital signs terbaru berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/vital-signs/chart
     * Get vital signs for charting (last 24 hours).
     */
    @GetMapping("/encounters/{encounterId}/vital-signs/chart")
    public ResponseEntity<ApiResponse<List<VitalSignsChartDto>>> getVitalSignsForCharting(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/vital-signs/chart", encounterId);

        List<VitalSignsChartDto> chartData = vitalSignsService.getVitalSignsForCharting(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<VitalSignsChartDto>>builder()
            .success(true)
            .message("Data chart vital signs berhasil diambil")
            .data(chartData)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/vital-signs/abnormal
     * Get abnormal vital signs for an encounter.
     */
    @GetMapping("/encounters/{encounterId}/vital-signs/abnormal")
    public ResponseEntity<ApiResponse<List<VitalSignsResponse>>> getAbnormalVitalSigns(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/vital-signs/abnormal", encounterId);

        List<VitalSignsResponse> vitalSigns = vitalSignsService.getAbnormalVitalSigns(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<VitalSignsResponse>>builder()
            .success(true)
            .message("Vital signs abnormal berhasil diambil")
            .data(vitalSigns)
            .build());
    }

    /**
     * GET /api/clinical/encounters/{encounterId}/vital-signs/critical
     * Get vital signs requiring notification.
     */
    @GetMapping("/encounters/{encounterId}/vital-signs/critical")
    public ResponseEntity<ApiResponse<List<VitalSignsResponse>>> getCriticalVitalSigns(
        @PathVariable UUID encounterId
    ) {
        log.info("GET /api/clinical/encounters/{}/vital-signs/critical", encounterId);

        List<VitalSignsResponse> vitalSigns = vitalSignsService.getVitalSignsRequiringNotification(encounterId);

        return ResponseEntity.ok(ApiResponse.<List<VitalSignsResponse>>builder()
            .success(true)
            .message("Vital signs kritis berhasil diambil")
            .data(vitalSigns)
            .build());
    }

    /**
     * GET /api/clinical/vital-signs/critical-gcs
     * Get all critical GCS scores (GCS < 9).
     */
    @GetMapping("/vital-signs/critical-gcs")
    public ResponseEntity<ApiResponse<List<VitalSignsResponse>>> getCriticalGcsScores() {
        log.info("GET /api/clinical/vital-signs/critical-gcs");

        List<VitalSignsResponse> vitalSigns = vitalSignsService.getCriticalGcsScores();

        return ResponseEntity.ok(ApiResponse.<List<VitalSignsResponse>>builder()
            .success(true)
            .message("Pasien dengan GCS kritis berhasil diambil")
            .data(vitalSigns)
            .build());
    }

    /**
     * GET /api/clinical/vital-signs/{id}
     * Get vital signs by ID.
     */
    @GetMapping("/vital-signs/{id}")
    public ResponseEntity<ApiResponse<VitalSignsResponse>> getVitalSignsById(
        @PathVariable UUID id
    ) {
        log.info("GET /api/clinical/vital-signs/{}", id);

        VitalSignsResponse response = vitalSignsService.getVitalSignsById(id);

        return ResponseEntity.ok(ApiResponse.<VitalSignsResponse>builder()
            .success(true)
            .message("Vital signs berhasil diambil")
            .data(response)
            .build());
    }

    /**
     * PUT /api/clinical/vital-signs/{id}
     * Update vital signs.
     */
    @PutMapping("/vital-signs/{id}")
    public ResponseEntity<ApiResponse<VitalSignsResponse>> updateVitalSigns(
        @PathVariable UUID id,
        @Valid @RequestBody VitalSignsRequest request
    ) {
        log.info("PUT /api/clinical/vital-signs/{} - Updating vital signs", id);

        VitalSignsResponse response = vitalSignsService.updateVitalSigns(id, request);

        return ResponseEntity.ok(ApiResponse.<VitalSignsResponse>builder()
            .success(true)
            .message("Vital signs berhasil diperbarui")
            .data(response)
            .build());
    }

    /**
     * PATCH /api/clinical/vital-signs/{id}/notification-sent
     * Mark notification as sent.
     */
    @PatchMapping("/vital-signs/{id}/notification-sent")
    public ResponseEntity<ApiResponse<VitalSignsResponse>> markNotificationSent(
        @PathVariable UUID id,
        @RequestParam UUID notifiedProviderId
    ) {
        log.info("PATCH /api/clinical/vital-signs/{}/notification-sent - Provider: {}", id, notifiedProviderId);

        VitalSignsResponse response = vitalSignsService.markNotificationSent(id, notifiedProviderId);

        return ResponseEntity.ok(ApiResponse.<VitalSignsResponse>builder()
            .success(true)
            .message("Notifikasi berhasil ditandai sebagai terkirim")
            .data(response)
            .build());
    }

    /**
     * DELETE /api/clinical/vital-signs/{id}
     * Delete vital signs.
     */
    @DeleteMapping("/vital-signs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVitalSigns(
        @PathVariable UUID id
    ) {
        log.info("DELETE /api/clinical/vital-signs/{}", id);

        vitalSignsService.deleteVitalSigns(id);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
            .success(true)
            .message("Vital signs berhasil dihapus")
            .build());
    }
}
