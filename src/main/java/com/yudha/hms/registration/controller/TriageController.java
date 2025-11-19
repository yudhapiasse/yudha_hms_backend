package com.yudha.hms.registration.controller;

import com.yudha.hms.registration.dto.TriageAssessmentRequest;
import com.yudha.hms.registration.entity.TriageAssessment;
import com.yudha.hms.registration.service.TriageService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Triage Assessment Management.
 *
 * Implements Emergency Severity Index (ESI) based triage:
 * - Level 1: Requires immediate life-saving intervention
 * - Level 2: High risk, confused/lethargic, severe pain/distress
 * - Level 3: Stable, needs multiple resources (2+)
 * - Level 4: Needs one resource
 * - Level 5: No resources needed
 *
 * Provides endpoints for:
 * - Initial triage assessment
 * - Re-triage for deteriorating/improving patients
 * - Triage history tracking
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@Slf4j
public class TriageController {

    private final TriageService triageService;

    /**
     * Perform initial triage assessment for emergency patient.
     * Auto-calculates ESI level based on vital signs, GCS, red flags, and resource needs.
     *
     * POST /api/emergency/{emergencyId}/triage
     *
     * @param emergencyId emergency registration ID
     * @param request triage assessment request
     * @return created triage assessment
     */
    @PostMapping("/{emergencyId}/triage")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'TRIAGE_NURSE')")
    public ResponseEntity<ApiResponse<TriageAssessment>> performTriage(
            @PathVariable UUID emergencyId,
            @Valid @RequestBody TriageAssessmentRequest request) {
        log.info("Performing triage for emergency: {}", emergencyId);

        // Set emergency ID from path variable
        request.setEmergencyRegistrationId(emergencyId);

        TriageAssessment assessment = triageService.performTriage(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                String.format("Triage completed - ESI Level %d, GCS %d",
                    assessment.getEsiLevel(), assessment.getGcsTotal()),
                assessment
            ));
    }

    /**
     * Perform re-triage for deteriorating or improving patient.
     * Creates new triage assessment linked to previous assessment.
     *
     * POST /api/emergency/{emergencyId}/retriage
     *
     * @param emergencyId emergency registration ID
     * @param request triage assessment request
     * @param retriageReason reason for re-triage
     * @return new triage assessment
     */
    @PostMapping("/{emergencyId}/retriage")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'TRIAGE_NURSE', 'ER_DOCTOR')")
    public ResponseEntity<ApiResponse<TriageAssessment>> performRetriage(
            @PathVariable UUID emergencyId,
            @Valid @RequestBody TriageAssessmentRequest request,
            @RequestParam String retriageReason) {
        log.info("Performing re-triage for emergency: {}, reason: {}", emergencyId, retriageReason);

        // Set emergency ID from path variable
        request.setEmergencyRegistrationId(emergencyId);

        TriageAssessment assessment = triageService.performRetriage(emergencyId, request, retriageReason);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                String.format("Re-triage completed - ESI Level %d (Reason: %s)",
                    assessment.getEsiLevel(), retriageReason),
                assessment
            ));
    }

    /**
     * Get triage history for emergency registration.
     * Returns all triage assessments in reverse chronological order.
     *
     * GET /api/emergency/{emergencyId}/triage-history
     *
     * @param emergencyId emergency registration ID
     * @return list of triage assessments
     */
    @GetMapping("/{emergencyId}/triage-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'TRIAGE_NURSE')")
    public ResponseEntity<ApiResponse<List<TriageAssessment>>> getTriageHistory(
            @PathVariable UUID emergencyId) {
        log.info("Fetching triage history for emergency: {}", emergencyId);

        List<TriageAssessment> assessments = triageService.getTriageHistory(emergencyId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Retrieved %d triage assessments", assessments.size()),
            assessments
        ));
    }

    /**
     * Get latest triage assessment for emergency registration.
     *
     * GET /api/emergency/{emergencyId}/triage/latest
     *
     * @param emergencyId emergency registration ID
     * @return latest triage assessment
     */
    @GetMapping("/{emergencyId}/triage/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'ER_NURSE', 'ER_DOCTOR', 'TRIAGE_NURSE')")
    public ResponseEntity<ApiResponse<TriageAssessment>> getLatestTriage(
            @PathVariable UUID emergencyId) {
        log.info("Fetching latest triage for emergency: {}", emergencyId);

        TriageAssessment assessment = triageService.getLatestTriage(emergencyId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Latest triage - ESI Level %d", assessment.getEsiLevel()),
            assessment
        ));
    }
}
