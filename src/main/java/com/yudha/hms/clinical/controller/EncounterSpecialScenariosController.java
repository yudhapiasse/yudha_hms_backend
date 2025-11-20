package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.EncounterType;
import com.yudha.hms.clinical.service.EncounterService;
import com.yudha.hms.clinical.service.EncounterSpecialScenariosService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Encounter Special Scenarios Controller.
 *
 * REST API endpoints for handling special encounter scenarios:
 * - Emergency to Inpatient conversion
 * - Duplicate encounter checking
 * - Enhanced cancellation
 * - Encounter reopening
 * - External patient encounters
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/encounter-special")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class EncounterSpecialScenariosController {

    private final EncounterSpecialScenariosService specialScenariosService;
    private final EncounterService encounterService;

    /**
     * Convert emergency encounter to inpatient.
     *
     * POST /api/clinical/encounter-special/convert-to-inpatient
     *
     * @param request Conversion request
     * @return The new inpatient encounter
     */
    @PostMapping("/convert-to-inpatient")
    public ResponseEntity<ApiResponse<EncounterResponse>> convertEmergencyToInpatient(
            @Valid @RequestBody ConvertEmergencyToInpatientRequest request
    ) {
        log.info("API: Converting emergency encounter {} to inpatient",
            request.getEmergencyEncounterId());

        Encounter inpatientEncounter = specialScenariosService.convertEmergencyToInpatient(request);
        EncounterResponse response = encounterService.mapToResponse(inpatientEncounter);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Emergency encounter successfully converted to inpatient",
                response
            ));
    }

    /**
     * Check for duplicate encounters.
     *
     * GET /api/clinical/encounter-special/check-duplicate
     *
     * @param patientId Patient ID
     * @param encounterType Encounter type
     * @param departmentId Department ID
     * @return Duplicate check response
     */
    @GetMapping("/check-duplicate")
    public ResponseEntity<ApiResponse<DuplicateEncounterCheckResponse>> checkDuplicateEncounters(
            @RequestParam UUID patientId,
            @RequestParam EncounterType encounterType,
            @RequestParam(required = false) UUID departmentId
    ) {
        log.info("API: Checking duplicate encounters for patient: {}", patientId);

        DuplicateEncounterCheckResponse response = specialScenariosService
            .checkDuplicateEncounters(patientId, encounterType, departmentId);

        return ResponseEntity.ok(ApiResponse.success(
            "Duplicate encounter check completed",
            response
        ));
    }

    /**
     * Validate if encounter can be cancelled.
     *
     * GET /api/clinical/encounter-special/{encounterId}/validate-cancellation
     *
     * @param encounterId Encounter ID
     * @return Cancellation validation response
     */
    @GetMapping("/{encounterId}/validate-cancellation")
    public ResponseEntity<ApiResponse<CancellationValidationResponse>> validateCancellation(
            @PathVariable UUID encounterId
    ) {
        log.info("API: Validating cancellation for encounter: {}", encounterId);

        CancellationValidationResponse response = specialScenariosService
            .validateCancellation(encounterId);

        return ResponseEntity.ok(ApiResponse.success(
            "Cancellation validation completed",
            response
        ));
    }

    /**
     * Cancel encounter with validation.
     *
     * POST /api/clinical/encounter-special/{encounterId}/cancel
     *
     * @param encounterId Encounter ID
     * @param request Cancellation request
     * @return Cancelled encounter
     */
    @PostMapping("/{encounterId}/cancel")
    public ResponseEntity<ApiResponse<EncounterResponse>> cancelEncounter(
            @PathVariable UUID encounterId,
            @Valid @RequestBody CancelEncounterRequest request
    ) {
        log.info("API: Cancelling encounter: {} by {}", encounterId, request.getCancelledBy());

        Encounter encounter = specialScenariosService.cancelEncounter(encounterId, request);
        EncounterResponse response = encounterService.mapToResponse(encounter);

        return ResponseEntity.ok(ApiResponse.success(
            "Encounter cancelled successfully",
            response
        ));
    }

    /**
     * Reopen finished encounter.
     *
     * POST /api/clinical/encounter-special/{encounterId}/reopen
     *
     * @param encounterId Encounter ID
     * @param request Reopen request
     * @return Reopened encounter
     */
    @PostMapping("/{encounterId}/reopen")
    public ResponseEntity<ApiResponse<EncounterResponse>> reopenEncounter(
            @PathVariable UUID encounterId,
            @Valid @RequestBody ReopenEncounterRequest request
    ) {
        log.info("API: Reopening encounter: {} by {}", encounterId, request.getRequestedBy());

        Encounter encounter = specialScenariosService.reopenEncounter(encounterId, request);
        EncounterResponse response = encounterService.mapToResponse(encounter);

        return ResponseEntity.ok(ApiResponse.success(
            "Encounter reopened successfully",
            response
        ));
    }

    /**
     * Create external patient encounter.
     *
     * POST /api/clinical/encounter-special/external-patient
     *
     * Creates an encounter for emergency patients without full registration.
     *
     * @param request External patient encounter request
     * @return Created encounter
     */
    @PostMapping("/external-patient")
    public ResponseEntity<ApiResponse<EncounterResponse>> createExternalPatientEncounter(
            @Valid @RequestBody ExternalPatientEncounterRequest request
    ) {
        log.info("API: Creating external patient encounter for: {}", request.getPatientName());

        Encounter encounter = specialScenariosService.createExternalPatientEncounter(request);
        EncounterResponse response = encounterService.mapToResponse(encounter);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "External patient encounter created successfully",
                response
            ));
    }

    /**
     * Get reopening time limit.
     *
     * GET /api/clinical/encounter-special/reopen-time-limit
     *
     * @return Time limit in hours
     */
    @GetMapping("/reopen-time-limit")
    public ResponseEntity<ApiResponse<Integer>> getReopenTimeLimit() {
        // Return constant from service
        int timeLimit = 24;

        return ResponseEntity.ok(ApiResponse.success(
            "Reopen time limit retrieved",
            timeLimit
        ));
    }

    /**
     * Check if encounter can be reopened.
     *
     * GET /api/clinical/encounter-special/{encounterId}/can-reopen
     *
     * @param encounterId Encounter ID
     * @return Boolean indicating if can reopen
     */
    @GetMapping("/{encounterId}/can-reopen")
    public ResponseEntity<ApiResponse<Boolean>> canReopenEncounter(
            @PathVariable UUID encounterId
    ) {
        log.info("API: Checking if encounter can be reopened: {}", encounterId);

        // This will throw exception if cannot reopen, otherwise return true
        try {
            Encounter encounter = encounterService.getEncounterEntity(encounterId);

            if (encounter.getStatus().name().equals("FINISHED")) {
                if (encounter.getEncounterEnd() != null) {
                    java.time.Duration timeSinceFinish = java.time.Duration.between(
                        encounter.getEncounterEnd(),
                        java.time.LocalDateTime.now()
                    );
                    boolean canReopen = timeSinceFinish.toHours() <= 24;

                    return ResponseEntity.ok(ApiResponse.success(
                        canReopen ? "Encounter can be reopened" : "Time limit exceeded",
                        canReopen
                    ));
                }
            }

            return ResponseEntity.ok(ApiResponse.success(
                "Encounter is not in finished status",
                false
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success(
                "Cannot reopen: " + e.getMessage(),
                false
            ));
        }
    }
}
