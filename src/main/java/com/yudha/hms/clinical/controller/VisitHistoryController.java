package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.service.VisitHistoryService;
import com.yudha.hms.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Visit History Controller.
 *
 * REST API endpoints for patient encounter history and timeline visualization.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/visit-history")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VisitHistoryController {

    private final VisitHistoryService visitHistoryService;

    /**
     * Get patient visit history with filtering and pagination.
     *
     * GET /api/clinical/visit-history/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<VisitHistoryResponse>> getPatientVisitHistory(
        @PathVariable UUID patientId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @RequestParam(required = false) String encounterType,
        @RequestParam(required = false) String department,
        @RequestParam(required = false) UUID doctorId,
        @RequestParam(required = false) String doctorName,
        @RequestParam(required = false) String diagnosisCode,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Boolean isBpjsOnly,
        @RequestParam(required = false) Boolean readmissionsOnly,
        @RequestParam(required = false, defaultValue = "0") Integer page,
        @RequestParam(required = false, defaultValue = "20") Integer size,
        @RequestParam(required = false, defaultValue = "encounterStart") String sortBy,
        @RequestParam(required = false, defaultValue = "DESC") String sortDirection
    ) {
        log.info("REST: Fetching visit history for patient: {}", patientId);

        VisitHistoryFilterRequest filter = VisitHistoryFilterRequest.builder()
            .startDate(startDate)
            .endDate(endDate)
            .encounterType(encounterType)
            .department(department)
            .doctorId(doctorId)
            .doctorName(doctorName)
            .diagnosisCode(diagnosisCode)
            .status(status)
            .isBpjsOnly(isBpjsOnly)
            .readmissionsOnly(readmissionsOnly)
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .sortDirection(sortDirection)
            .build();

        VisitHistoryResponse response = visitHistoryService.getPatientVisitHistory(patientId, filter);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d visits", response.getTotalVisits()),
            response
        ));
    }

    /**
     * Get patient timeline visualization data.
     *
     * GET /api/clinical/visit-history/patient/{patientId}/timeline
     */
    @GetMapping("/patient/{patientId}/timeline")
    public ResponseEntity<ApiResponse<TimelineResponse>> getPatientTimeline(
        @PathVariable UUID patientId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        log.info("REST: Fetching timeline for patient: {}", patientId);

        TimelineResponse response = visitHistoryService.getPatientTimeline(patientId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Timeline with %d events", response.getTotalEncounters()),
            response
        ));
    }

    /**
     * Get patient visit summary (without details).
     *
     * GET /api/clinical/visit-history/patient/{patientId}/summary
     */
    @GetMapping("/patient/{patientId}/summary")
    public ResponseEntity<ApiResponse<VisitSummaryResponse>> getPatientVisitSummary(
        @PathVariable UUID patientId
    ) {
        log.info("REST: Fetching visit summary for patient: {}", patientId);

        // Get full visit history without pagination
        VisitHistoryFilterRequest filter = VisitHistoryFilterRequest.builder()
            .page(0)
            .size(1000) // Get all
            .build();

        VisitHistoryResponse history = visitHistoryService.getPatientVisitHistory(patientId, filter);

        VisitSummaryResponse summary = VisitSummaryResponse.builder()
            .patientId(patientId)
            .totalVisits(history.getTotalVisits())
            .outpatientVisits(history.getOutpatientVisits())
            .inpatientVisits(history.getInpatientVisits())
            .emergencyVisits(history.getEmergencyVisits())
            .readmissions(history.getReadmissions())
            .bpjsVisits(history.getBpjsVisits())
            .lastVisitDate(history.getVisits().isEmpty() ? null : history.getVisits().get(0).getEncounterStart())
            .hasActiveEncounters(history.getVisits().stream().anyMatch(VisitHistoryItemResponse::getIsActive))
            .build();

        return ResponseEntity.ok(ApiResponse.success(
            "Visit summary",
            summary
        ));
    }

    /**
     * Export patient medical history to PDF.
     *
     * GET /api/clinical/visit-history/patient/{patientId}/export
     */
    @GetMapping("/patient/{patientId}/export")
    public ResponseEntity<ApiResponse<String>> exportMedicalHistory(
        @PathVariable UUID patientId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
        @RequestParam(required = false, defaultValue = "false") Boolean includeTimeline
    ) {
        log.info("REST: Exporting medical history for patient: {}", patientId);

        // TODO: Implement actual PDF generation
        // For now, return a placeholder URL

        String documentUrl = "/exports/medical-history/" + patientId + "_" + System.currentTimeMillis() + ".pdf";

        return ResponseEntity.ok(ApiResponse.success(
            "Medical history export generated",
            documentUrl
        ));
    }
}
