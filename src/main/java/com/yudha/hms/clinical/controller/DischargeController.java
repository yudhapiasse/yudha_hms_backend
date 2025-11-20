package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.DischargeCondition;
import com.yudha.hms.clinical.entity.DischargeDisposition;
import com.yudha.hms.clinical.service.DischargeService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Discharge Controller.
 *
 * REST API endpoints for discharge workflow management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/discharge")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DischargeController {

    private final DischargeService dischargeService;

    // ========== Discharge Summary Endpoints ==========

    /**
     * Create discharge summary.
     *
     * POST /api/clinical/discharge/summary
     */
    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<DischargeSummaryResponse>> createDischargeSummary(
        @Valid @RequestBody DischargeSummaryRequest request
    ) {
        log.info("REST: Creating discharge summary for encounter: {}", request.getEncounterId());

        DischargeSummaryResponse response = dischargeService.createDischargeSummary(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Discharge summary berhasil dibuat",
                response
            ));
    }

    /**
     * Get discharge summary by ID.
     *
     * GET /api/clinical/discharge/summary/{id}
     */
    @GetMapping("/summary/{id}")
    public ResponseEntity<ApiResponse<DischargeSummaryResponse>> getDischargeSummaryById(
        @PathVariable UUID id
    ) {
        log.info("REST: Fetching discharge summary: {}", id);

        DischargeSummaryResponse response = dischargeService.getDischargeSummaryById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get discharge summary by encounter ID.
     *
     * GET /api/clinical/discharge/summary/encounter/{encounterId}
     */
    @GetMapping("/summary/encounter/{encounterId}")
    public ResponseEntity<ApiResponse<DischargeSummaryResponse>> getDischargeSummaryByEncounterId(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Fetching discharge summary for encounter: {}", encounterId);

        DischargeSummaryResponse response =
            dischargeService.getDischargeSummaryByEncounterId(encounterId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get discharge summaries by patient ID.
     *
     * GET /api/clinical/discharge/summary/patient/{patientId}
     */
    @GetMapping("/summary/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<DischargeSummaryResponse>>> getDischargeSummariesByPatientId(
        @PathVariable UUID patientId
    ) {
        log.info("REST: Fetching discharge summaries for patient: {}", patientId);

        List<DischargeSummaryResponse> summaries =
            dischargeService.getDischargeSummariesByPatientId(patientId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d discharge summaries", summaries.size()),
            summaries
        ));
    }

    /**
     * Sign discharge summary.
     *
     * POST /api/clinical/discharge/summary/{id}/sign
     */
    @PostMapping("/summary/{id}/sign")
    public ResponseEntity<ApiResponse<DischargeSummaryResponse>> signDischargeSummary(
        @PathVariable UUID id,
        @RequestParam UUID doctorId,
        @RequestParam String doctorName
    ) {
        log.info("REST: Signing discharge summary: {} by: {}", id, doctorName);

        DischargeSummaryResponse response =
            dischargeService.signDischargeSummary(id, doctorId, doctorName);

        return ResponseEntity.ok(ApiResponse.success(
            "Discharge summary berhasil ditandatangani",
            response
        ));
    }

    /**
     * Generate discharge summary document.
     *
     * POST /api/clinical/discharge/summary/{id}/generate-document
     */
    @PostMapping("/summary/{id}/generate-document")
    public ResponseEntity<ApiResponse<DischargeSummaryResponse>> generateDocument(
        @PathVariable UUID id
    ) {
        log.info("REST: Generating discharge summary document: {}", id);

        DischargeSummaryResponse response = dischargeService.generateDocument(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Dokumen discharge summary berhasil dibuat",
            response
        ));
    }

    // ========== Discharge Readiness Endpoints ==========

    /**
     * Create discharge readiness assessment.
     *
     * POST /api/clinical/discharge/readiness
     */
    @PostMapping("/readiness")
    public ResponseEntity<ApiResponse<DischargeReadinessResponse>> createDischargeReadiness(
        @RequestParam UUID encounterId,
        @RequestParam UUID patientId
    ) {
        log.info("REST: Creating discharge readiness for encounter: {}", encounterId);

        DischargeReadinessResponse response =
            dischargeService.createDischargeReadiness(encounterId, patientId);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Discharge readiness assessment dibuat",
                response
            ));
    }

    /**
     * Get discharge readiness by encounter ID.
     *
     * GET /api/clinical/discharge/readiness/encounter/{encounterId}
     */
    @GetMapping("/readiness/encounter/{encounterId}")
    public ResponseEntity<ApiResponse<DischargeReadinessResponse>> getDischargeReadinessByEncounterId(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Fetching discharge readiness for encounter: {}", encounterId);

        DischargeReadinessResponse response =
            dischargeService.getDischargeReadinessByEncounterId(encounterId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Assess medical stability.
     *
     * POST /api/clinical/discharge/readiness/{encounterId}/medical-stability
     */
    @PostMapping("/readiness/{encounterId}/medical-stability")
    public ResponseEntity<ApiResponse<DischargeReadinessResponse>> assessMedicalStability(
        @PathVariable UUID encounterId,
        @RequestParam boolean met,
        @RequestParam(required = false) String notes,
        @RequestParam String assessedBy
    ) {
        log.info("REST: Assessing medical stability for encounter: {}", encounterId);

        DischargeReadinessResponse response =
            dischargeService.assessMedicalStability(encounterId, met, notes, assessedBy);

        return ResponseEntity.ok(ApiResponse.success(
            "Medical stability assessment updated",
            response
        ));
    }

    /**
     * Reconcile medications.
     *
     * POST /api/clinical/discharge/readiness/{encounterId}/reconcile-medications
     */
    @PostMapping("/readiness/{encounterId}/reconcile-medications")
    public ResponseEntity<ApiResponse<DischargeReadinessResponse>> reconcileMedications(
        @PathVariable UUID encounterId,
        @RequestParam(required = false) String notes,
        @RequestParam String reconciledBy
    ) {
        log.info("REST: Reconciling medications for encounter: {}", encounterId);

        DischargeReadinessResponse response =
            dischargeService.reconcileMedications(encounterId, notes, reconciledBy);

        return ResponseEntity.ok(ApiResponse.success(
            "Medications reconciled successfully",
            response
        ));
    }

    /**
     * Schedule follow-up appointment.
     *
     * POST /api/clinical/discharge/readiness/{encounterId}/schedule-followup
     */
    @PostMapping("/readiness/{encounterId}/schedule-followup")
    public ResponseEntity<ApiResponse<DischargeReadinessResponse>> scheduleFollowUp(
        @PathVariable UUID encounterId,
        @RequestParam LocalDateTime appointmentDate,
        @RequestParam String provider,
        @RequestParam String department
    ) {
        log.info("REST: Scheduling follow-up for encounter: {}", encounterId);

        DischargeReadinessResponse response =
            dischargeService.scheduleFollowUp(encounterId, appointmentDate, provider, department);

        return ResponseEntity.ok(ApiResponse.success(
            "Follow-up appointment scheduled",
            response
        ));
    }

    /**
     * Mark as ready for discharge.
     *
     * POST /api/clinical/discharge/readiness/{encounterId}/mark-ready
     */
    @PostMapping("/readiness/{encounterId}/mark-ready")
    public ResponseEntity<ApiResponse<DischargeReadinessResponse>> markReadyForDischarge(
        @PathVariable UUID encounterId,
        @RequestParam UUID assessedById,
        @RequestParam String assessedByName
    ) {
        log.info("REST: Marking encounter {} as ready for discharge", encounterId);

        DischargeReadinessResponse response =
            dischargeService.markReadyForDischarge(encounterId, assessedById, assessedByName);

        return ResponseEntity.ok(ApiResponse.success(
            "Encounter marked as ready for discharge",
            response
        ));
    }

    // ========== Prescription Endpoints ==========

    /**
     * Add discharge prescription.
     *
     * POST /api/clinical/discharge/summary/{summaryId}/prescriptions
     */
    @PostMapping("/summary/{summaryId}/prescriptions")
    public ResponseEntity<ApiResponse<DischargePrescriptionResponse>> addPrescription(
        @PathVariable UUID summaryId,
        @Valid @RequestBody DischargePrescriptionRequest request
    ) {
        log.info("REST: Adding prescription to discharge summary: {}", summaryId);

        DischargePrescriptionResponse response =
            dischargeService.addPrescription(summaryId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Prescription added successfully",
                response
            ));
    }

    /**
     * Get prescriptions for discharge summary.
     *
     * GET /api/clinical/discharge/summary/{summaryId}/prescriptions
     */
    @GetMapping("/summary/{summaryId}/prescriptions")
    public ResponseEntity<ApiResponse<List<DischargePrescriptionResponse>>> getPrescriptionsBySummaryId(
        @PathVariable UUID summaryId
    ) {
        log.info("REST: Fetching prescriptions for discharge summary: {}", summaryId);

        List<DischargePrescriptionResponse> prescriptions =
            dischargeService.getPrescriptionsBySummaryId(summaryId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d prescriptions", prescriptions.size()),
            prescriptions
        ));
    }

    // ========== Instruction Endpoints ==========

    /**
     * Add discharge instruction.
     *
     * POST /api/clinical/discharge/summary/{summaryId}/instructions
     */
    @PostMapping("/summary/{summaryId}/instructions")
    public ResponseEntity<ApiResponse<DischargeInstructionResponse>> addInstruction(
        @PathVariable UUID summaryId,
        @Valid @RequestBody DischargeInstructionRequest request
    ) {
        log.info("REST: Adding instruction to discharge summary: {}", summaryId);

        DischargeInstructionResponse response =
            dischargeService.addInstruction(summaryId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Instruction added successfully",
                response
            ));
    }

    /**
     * Get instructions for discharge summary.
     *
     * GET /api/clinical/discharge/summary/{summaryId}/instructions
     */
    @GetMapping("/summary/{summaryId}/instructions")
    public ResponseEntity<ApiResponse<List<DischargeInstructionResponse>>> getInstructionsBySummaryId(
        @PathVariable UUID summaryId
    ) {
        log.info("REST: Fetching instructions for discharge summary: {}", summaryId);

        List<DischargeInstructionResponse> instructions =
            dischargeService.getInstructionsBySummaryId(summaryId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d instructions", instructions.size()),
            instructions
        ));
    }

    /**
     * Mark instruction as educated.
     *
     * POST /api/clinical/discharge/instructions/{instructionId}/mark-educated
     */
    @PostMapping("/instructions/{instructionId}/mark-educated")
    public ResponseEntity<ApiResponse<DischargeInstructionResponse>> markInstructionAsEducated(
        @PathVariable UUID instructionId,
        @RequestParam String educatorName,
        @RequestParam boolean understanding
    ) {
        log.info("REST: Marking instruction {} as educated", instructionId);

        DischargeInstructionResponse response =
            dischargeService.markInstructionAsEducated(instructionId, educatorName, understanding);

        return ResponseEntity.ok(ApiResponse.success(
            "Instruction marked as educated",
            response
        ));
    }

    // ========== Reference Data Endpoints ==========

    /**
     * Get all discharge conditions.
     *
     * GET /api/clinical/discharge/conditions
     */
    @GetMapping("/conditions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDischargeConditions() {
        log.info("REST: Fetching discharge conditions");

        List<Map<String, Object>> conditions = Arrays.stream(DischargeCondition.values())
            .map(condition -> {
                Map<String, Object> conditionInfo = new HashMap<>();
                conditionInfo.put("value", condition.name());
                conditionInfo.put("displayName", condition.getDisplayName());
                conditionInfo.put("indonesianName", condition.getIndonesianName());
                conditionInfo.put("description", condition.getDescription());
                conditionInfo.put("isPositiveOutcome", condition.isPositiveOutcome());
                conditionInfo.put("isNegativeOutcome", condition.isNegativeOutcome());
                conditionInfo.put("isDeceased", condition.isDeceased());
                return conditionInfo;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Discharge conditions retrieved successfully",
            conditions
        ));
    }

    /**
     * Get all discharge dispositions.
     *
     * GET /api/clinical/discharge/dispositions
     */
    @GetMapping("/dispositions")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDischargeDispositions() {
        log.info("REST: Fetching discharge dispositions");

        List<Map<String, Object>> dispositions = Arrays.stream(DischargeDisposition.values())
            .map(disposition -> {
                Map<String, Object> dispositionInfo = new HashMap<>();
                dispositionInfo.put("value", disposition.name());
                dispositionInfo.put("displayName", disposition.getDisplayName());
                dispositionInfo.put("indonesianName", disposition.getIndonesianName());
                dispositionInfo.put("description", disposition.getDescription());
                dispositionInfo.put("isHomeDischarge", disposition.isHomeDischarge());
                dispositionInfo.put("isTransfer", disposition.isTransfer());
                dispositionInfo.put("isAgainstAdvice", disposition.isAgainstAdvice());
                dispositionInfo.put("isDeceased", disposition.isDeceased());
                return dispositionInfo;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Discharge dispositions retrieved successfully",
            dispositions
        ));
    }

    /**
     * Get instruction categories.
     *
     * GET /api/clinical/discharge/instruction-categories
     */
    @GetMapping("/instruction-categories")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getInstructionCategories() {
        log.info("REST: Fetching instruction categories");

        List<Map<String, String>> categories = List.of(
            Map.of("value", "WOUND_CARE", "displayName", "Wound Care", "indonesianName", "Perawatan Luka"),
            Map.of("value", "DIET", "displayName", "Diet", "indonesianName", "Diet"),
            Map.of("value", "ACTIVITY", "displayName", "Activity", "indonesianName", "Aktivitas"),
            Map.of("value", "PHYSICAL_THERAPY", "displayName", "Physical Therapy", "indonesianName", "Terapi Fisik"),
            Map.of("value", "MEDICATION", "displayName", "Medication", "indonesianName", "Obat-obatan"),
            Map.of("value", "GENERAL", "displayName", "General", "indonesianName", "Umum")
        );

        return ResponseEntity.ok(ApiResponse.success(
            "Instruction categories retrieved successfully",
            categories
        ));
    }
}
