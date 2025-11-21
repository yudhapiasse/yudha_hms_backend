package com.yudha.hms.radiology.controller;

import com.yudha.hms.radiology.dto.request.*;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.PatientPreparationChecklistResponse;
import com.yudha.hms.radiology.entity.PatientPreparationChecklist;
import com.yudha.hms.radiology.service.PatientPreparationChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Patient Preparation Checklist Controller.
 *
 * REST controller for managing patient preparation verification workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@RestController
@RequestMapping("/api/radiology/preparation")
@RequiredArgsConstructor
@Slf4j
public class PatientPreparationChecklistController {

    private final PatientPreparationChecklistService checklistService;

    /**
     * Create preparation checklist
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> createChecklist(
            @Valid @RequestBody CreateChecklistRequest request) {
        log.info("Creating preparation checklist for order: {}", request.getOrderId());

        PatientPreparationChecklist checklist = checklistService.createChecklist(
                request.getOrderId(),
                request.getExaminationId(),
                request.getPreparationInstructions());
        PatientPreparationChecklistResponse response = toResponse(checklist);

        log.info("Preparation checklist created successfully");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Preparation checklist created successfully", response));
    }

    /**
     * Verify fasting status
     */
    @PostMapping("/{id}/verify-fasting")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> verifyFasting(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyFastingRequest request) {
        log.info("Verifying fasting for checklist: {}", id);

        PatientPreparationChecklist checklist = checklistService.verifyFasting(
                id,
                request.getVerifiedBy(),
                request.getVerified());
        PatientPreparationChecklistResponse response = toResponse(checklist);

        log.info("Fasting verification completed");

        return ResponseEntity.ok(ApiResponse.success("Fasting verification completed", response));
    }

    /**
     * Verify medication hold
     */
    @PostMapping("/{id}/verify-medication-hold")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> verifyMedicationHold(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyMedicationHoldRequest request) {
        log.info("Verifying medication hold for checklist: {}", id);

        PatientPreparationChecklist checklist = checklistService.verifyMedicationHold(
                id,
                request.getVerifiedBy(),
                request.getVerified(),
                request.getMedicationDetails());
        PatientPreparationChecklistResponse response = toResponse(checklist);

        log.info("Medication hold verification completed");

        return ResponseEntity.ok(ApiResponse.success("Medication hold verification completed", response));
    }

    /**
     * Verify IV access
     */
    @PostMapping("/{id}/verify-iv-access")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> verifyIVAccess(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyIVAccessRequest request) {
        log.info("Verifying IV access for checklist: {}", id);

        PatientPreparationChecklist checklist = checklistService.verifyIVAccess(
                id,
                request.getVerifiedBy(),
                request.getVerified(),
                request.getIvGauge());
        PatientPreparationChecklistResponse response = toResponse(checklist);

        log.info("IV access verification completed");

        return ResponseEntity.ok(ApiResponse.success("IV access verification completed", response));
    }

    /**
     * Record pregnancy test
     */
    @PostMapping("/{id}/record-pregnancy-test")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> recordPregnancyTest(
            @PathVariable UUID id,
            @Valid @RequestBody RecordPregnancyTestRequest request) {
        log.info("Recording pregnancy test for checklist: {}", id);

        PatientPreparationChecklist checklist = checklistService.recordPregnancyTest(
                id,
                request.getResult(),
                request.getTestDate());
        PatientPreparationChecklistResponse response = toResponse(checklist);

        log.info("Pregnancy test recorded");

        return ResponseEntity.ok(ApiResponse.success("Pregnancy test recorded", response));
    }

    /**
     * Obtain consent
     */
    @PostMapping("/{id}/obtain-consent")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> obtainConsent(
            @PathVariable UUID id,
            @Valid @RequestBody ObtainConsentRequest request) {
        log.info("Recording consent for checklist: {}", id);

        PatientPreparationChecklist checklist = checklistService.obtainConsent(
                id,
                request.getObtainedBy(),
                request.getConsentFormId());
        PatientPreparationChecklistResponse response = toResponse(checklist);

        log.info("Consent recorded");

        return ResponseEntity.ok(ApiResponse.success("Consent recorded", response));
    }

    /**
     * Mark checklist as complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> markComplete(
            @PathVariable UUID id,
            @Valid @RequestBody MarkCompleteRequest request) {
        log.info("Marking checklist complete: {}", id);

        PatientPreparationChecklist checklist = checklistService.markComplete(
                id,
                request.getCompletedBy());
        PatientPreparationChecklistResponse response = toResponse(checklist);

        log.info("Checklist marked as complete");

        return ResponseEntity.ok(ApiResponse.success("Checklist marked as complete", response));
    }

    /**
     * Get checklist by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> getChecklistById(
            @PathVariable UUID id) {
        log.info("Fetching checklist ID: {}", id);

        PatientPreparationChecklist checklist = checklistService.getChecklistById(id);
        PatientPreparationChecklistResponse response = toResponse(checklist);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get checklist by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PatientPreparationChecklistResponse>> getChecklistByOrderId(
            @PathVariable UUID orderId) {
        log.info("Fetching checklist for order: {}", orderId);

        PatientPreparationChecklist checklist = checklistService.getChecklistByOrderId(orderId);
        PatientPreparationChecklistResponse response = toResponse(checklist);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get incomplete checklists
     */
    @GetMapping("/incomplete")
    public ResponseEntity<ApiResponse<List<PatientPreparationChecklistResponse>>> getIncompleteChecklists() {
        log.info("Fetching incomplete checklists");

        List<PatientPreparationChecklist> checklists = checklistService.getIncompleteChecklists();
        List<PatientPreparationChecklistResponse> responses = checklists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get checklists awaiting fasting verification
     */
    @GetMapping("/awaiting-fasting")
    public ResponseEntity<ApiResponse<List<PatientPreparationChecklistResponse>>> getAwaitingFastingVerification() {
        log.info("Fetching checklists awaiting fasting verification");

        List<PatientPreparationChecklist> checklists = checklistService.getAwaitingFastingVerification();
        List<PatientPreparationChecklistResponse> responses = checklists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get checklists awaiting consent
     */
    @GetMapping("/awaiting-consent")
    public ResponseEntity<ApiResponse<List<PatientPreparationChecklistResponse>>> getAwaitingConsent() {
        log.info("Fetching checklists awaiting consent");

        List<PatientPreparationChecklist> checklists = checklistService.getAwaitingConsent();
        List<PatientPreparationChecklistResponse> responses = checklists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get checklists awaiting pregnancy test
     */
    @GetMapping("/awaiting-pregnancy-test")
    public ResponseEntity<ApiResponse<List<PatientPreparationChecklistResponse>>> getAwaitingPregnancyTest() {
        log.info("Fetching checklists awaiting pregnancy test");

        List<PatientPreparationChecklist> checklists = checklistService.getAwaitingPregnancyTest();
        List<PatientPreparationChecklistResponse> responses = checklists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get checklists awaiting IV access
     */
    @GetMapping("/awaiting-iv-access")
    public ResponseEntity<ApiResponse<List<PatientPreparationChecklistResponse>>> getAwaitingIVAccess() {
        log.info("Fetching checklists awaiting IV access");

        List<PatientPreparationChecklist> checklists = checklistService.getAwaitingIVAccess();
        List<PatientPreparationChecklistResponse> responses = checklists.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Convert entity to response DTO
     */
    private PatientPreparationChecklistResponse toResponse(PatientPreparationChecklist checklist) {
        PatientPreparationChecklistResponse response = new PatientPreparationChecklistResponse();
        response.setId(checklist.getId());

        // Order information
        if (checklist.getOrder() != null) {
            response.setOrderId(checklist.getOrder().getId());
            response.setOrderNumber(checklist.getOrder().getOrderNumber());

            // Patient information from order
            if (checklist.getOrder().getPatient() != null) {
                response.setPatientId(checklist.getOrder().getPatient().getId());
                response.setPatientName(checklist.getOrder().getPatient().getFullName());
                response.setPatientMrn(checklist.getOrder().getPatient().getMrn());
            }
        }

        // Examination information
        if (checklist.getExamination() != null) {
            response.setExaminationId(checklist.getExamination().getId());
            response.setExaminationName(checklist.getExamination().getExamName());
        }

        // Preparation instructions
        response.setPreparationInstructions(checklist.getPreparationInstructions());

        // Fasting verification
        response.setFastingRequired(checklist.getFastingRequired());
        response.setFastingVerified(checklist.getFastingVerified());
        response.setFastingVerifiedBy(checklist.getFastingVerifiedBy());
        response.setFastingVerifiedAt(checklist.getFastingVerifiedAt());
        response.setFastingHoursRequired(checklist.getFastingHoursRequired());

        // Medication hold
        response.setMedicationHoldRequired(checklist.getMedicationHoldRequired());
        response.setMedicationHoldVerified(checklist.getMedicationHoldVerified());
        response.setMedicationHoldVerifiedBy(checklist.getMedicationHoldVerifiedBy());
        response.setMedicationHoldVerifiedAt(checklist.getMedicationHoldVerifiedAt());
        response.setMedicationHoldDetails(checklist.getMedicationHoldDetails());

        // IV access
        response.setIvAccessRequired(checklist.getIvAccessRequired());
        response.setIvAccessVerified(checklist.getIvAccessVerified());
        response.setIvAccessVerifiedBy(checklist.getIvAccessVerifiedBy());
        response.setIvAccessVerifiedAt(checklist.getIvAccessVerifiedAt());
        response.setIvGauge(checklist.getIvGauge());

        // Pregnancy test
        response.setPregnancyTestRequired(checklist.getPregnancyTestRequired());
        response.setPregnancyTestDone(checklist.getPregnancyTestDone());
        response.setPregnancyTestResult(checklist.getPregnancyTestResult());
        response.setPregnancyTestDate(checklist.getPregnancyTestDate());

        // Consent
        response.setConsentObtained(checklist.getConsentObtained());
        response.setConsentObtainedBy(checklist.getConsentObtainedBy());
        response.setConsentObtainedAt(checklist.getConsentObtainedAt());
        response.setConsentFormId(checklist.getConsentFormId());

        // Flexible checklist items
        response.setChecklistItems(checklist.getChecklistItems());

        // Completion status
        response.setAllItemsCompleted(checklist.getAllItemsCompleted());
        response.setCompletedBy(checklist.getCompletedBy());
        response.setCompletedAt(checklist.getCompletedAt());
        response.setNotes(checklist.getNotes());

        // Audit fields
        response.setCreatedAt(checklist.getCreatedAt());
        response.setUpdatedAt(checklist.getUpdatedAt());

        return response;
    }
}
