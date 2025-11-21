package com.yudha.hms.radiology.controller;

import com.yudha.hms.radiology.dto.request.RadiologyExaminationRequest;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.PageResponse;
import com.yudha.hms.radiology.dto.response.RadiologyExaminationResponse;
import com.yudha.hms.radiology.entity.RadiologyExamination;
import com.yudha.hms.radiology.entity.RadiologyModality;
import com.yudha.hms.radiology.service.RadiologyExaminationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Radiology Examination Controller.
 *
 * REST controller for managing radiology examinations (examination catalog).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/examinations")
@RequiredArgsConstructor
@Slf4j
public class RadiologyExaminationController {

    private final RadiologyExaminationService examinationService;

    /**
     * Create new radiology examination
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> createExamination(
            @Valid @RequestBody RadiologyExaminationRequest request) {
        log.info("Creating radiology examination: {}", request.getExamName());

        RadiologyExamination examination = convertToEntity(request);
        RadiologyExamination savedExamination = examinationService.createExamination(examination);
        RadiologyExaminationResponse response = toResponse(savedExamination);

        log.info("Radiology examination created successfully: {}", savedExamination.getExamCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Examination created successfully", response));
    }

    /**
     * Update existing radiology examination
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> updateExamination(
            @PathVariable UUID id,
            @Valid @RequestBody RadiologyExaminationRequest request) {
        log.info("Updating radiology examination ID: {}", id);

        RadiologyExamination examinationUpdate = convertToEntity(request);
        RadiologyExamination examination = examinationService.updateExamination(id, examinationUpdate);
        RadiologyExaminationResponse response = toResponse(examination);

        log.info("Radiology examination updated successfully: {}", examination.getExamCode());

        return ResponseEntity.ok(ApiResponse.success("Examination updated successfully", response));
    }

    /**
     * Get radiology examination by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> getExaminationById(
            @PathVariable UUID id) {
        log.info("Fetching radiology examination ID: {}", id);

        RadiologyExamination examination = examinationService.getExaminationById(id);
        RadiologyExaminationResponse response = toResponse(examination);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search radiology examinations
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RadiologyExaminationResponse>>> searchExaminations(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "examName") Pageable pageable) {
        log.info("Searching radiology examinations - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<RadiologyExamination> examinations = examinationService.searchExaminations(search, pageable);
        Page<RadiologyExaminationResponse> responsePage = examinations.map(this::toResponse);
        PageResponse<RadiologyExaminationResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get examination by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> getExaminationByCode(
            @PathVariable String code) {
        log.info("Fetching radiology examination by code: {}", code);

        RadiologyExamination examination = examinationService.getExaminationByCode(code);
        RadiologyExaminationResponse response = toResponse(examination);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get examination by CPT code
     */
    @GetMapping("/cpt/{cptCode}")
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> getExaminationByCptCode(
            @PathVariable String cptCode) {
        log.info("Fetching radiology examination by CPT code: {}", cptCode);

        RadiologyExamination examination = examinationService.getExaminationByCptCode(cptCode);
        RadiologyExaminationResponse response = toResponse(examination);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get examinations by modality
     */
    @GetMapping("/modality/{modalityId}")
    public ResponseEntity<ApiResponse<List<RadiologyExaminationResponse>>> getExaminationsByModality(
            @PathVariable UUID modalityId) {
        log.info("Fetching examinations for modality ID: {}", modalityId);

        List<RadiologyExamination> examinations = examinationService.getExaminationsByModality(modalityId);
        List<RadiologyExaminationResponse> responses = examinations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get examinations by body part
     */
    @GetMapping("/body-part/{bodyPart}")
    public ResponseEntity<ApiResponse<List<RadiologyExaminationResponse>>> getExaminationsByBodyPart(
            @PathVariable String bodyPart) {
        log.info("Fetching examinations for body part: {}", bodyPart);

        List<RadiologyExamination> examinations = examinationService.searchExaminationsByBodyPart(bodyPart);
        List<RadiologyExaminationResponse> responses = examinations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get examinations requiring contrast
     */
    @GetMapping("/contrast")
    public ResponseEntity<ApiResponse<List<RadiologyExaminationResponse>>> getExaminationsRequiringContrast() {
        log.info("Fetching examinations requiring contrast");

        List<RadiologyExamination> examinations = examinationService.getExaminationsRequiringContrast();
        List<RadiologyExaminationResponse> responses = examinations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get examinations requiring fasting
     */
    @GetMapping("/fasting")
    public ResponseEntity<ApiResponse<List<RadiologyExaminationResponse>>> getExaminationsRequiringFasting() {
        log.info("Fetching examinations requiring fasting");

        List<RadiologyExamination> examinations = examinationService.getExaminationsRequiringFasting();
        List<RadiologyExaminationResponse> responses = examinations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Update examination pricing
     */
    @PatchMapping("/{id}/pricing")
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> updateExaminationPricing(
            @PathVariable UUID id,
            @RequestParam BigDecimal baseCost,
            @RequestParam(required = false) BigDecimal contrastCost,
            @RequestParam(required = false) BigDecimal bpjsTariff) {
        log.info("Updating pricing for examination ID: {}", id);

        RadiologyExamination examination = examinationService.updateExaminationPricing(
                id, baseCost, contrastCost != null ? contrastCost : BigDecimal.ZERO, bpjsTariff);
        RadiologyExaminationResponse response = toResponse(examination);

        log.info("Examination pricing updated successfully");

        return ResponseEntity.ok(ApiResponse.success("Examination pricing updated successfully", response));
    }

    /**
     * Activate radiology examination
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> activateExamination(
            @PathVariable UUID id) {
        log.info("Activating radiology examination ID: {}", id);

        RadiologyExamination examination = examinationService.activateExamination(id);
        RadiologyExaminationResponse response = toResponse(examination);

        log.info("Radiology examination activated successfully: {}", examination.getExamCode());

        return ResponseEntity.ok(ApiResponse.success("Examination activated successfully", response));
    }

    /**
     * Deactivate radiology examination
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<RadiologyExaminationResponse>> deactivateExamination(
            @PathVariable UUID id) {
        log.info("Deactivating radiology examination ID: {}", id);

        RadiologyExamination examination = examinationService.deactivateExamination(id);
        RadiologyExaminationResponse response = toResponse(examination);

        log.info("Radiology examination deactivated successfully: {}", examination.getExamCode());

        return ResponseEntity.ok(ApiResponse.success("Examination deactivated successfully", response));
    }

    /**
     * Soft delete radiology examination
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExamination(
            @PathVariable UUID id) {
        log.info("Deleting radiology examination ID: {}", id);

        examinationService.deleteExamination(id, "SYSTEM");
        log.info("Radiology examination deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Examination deleted successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private RadiologyExaminationResponse toResponse(RadiologyExamination examination) {
        RadiologyExaminationResponse response = new RadiologyExaminationResponse();
        response.setId(examination.getId());
        response.setExamCode(examination.getExamCode());
        response.setExamName(examination.getExamName());
        response.setShortName(examination.getShortName());

        // Modality information
        if (examination.getModality() != null) {
            response.setModalityId(examination.getModality().getId());
            response.setModalityCode(examination.getModality().getCode());
            response.setModalityName(examination.getModality().getName());
        }

        // Coding
        response.setCptCode(examination.getCptCode());
        response.setIcdProcedureCode(examination.getIcdProcedureCode());

        // Preparation requirements
        response.setPreparationInstructions(examination.getPreparationInstructions());
        response.setFastingRequired(examination.getFastingRequired());
        response.setFastingDurationHours(examination.getFastingDurationHours());

        // Contrast requirements
        response.setRequiresContrast(examination.getRequiresContrast());
        response.setContrastType(examination.getContrastType());
        response.setContrastVolumeMl(examination.getContrastVolumeMl());

        // Timing information
        response.setExamDurationMinutes(examination.getExamDurationMinutes());
        response.setReportingTimeMinutes(examination.getReportingTimeMinutes());

        // Cost information
        response.setBaseCost(examination.getBaseCost());
        response.setContrastCost(examination.getContrastCost());
        response.setBpjsTariff(examination.getBpjsTariff());

        // Body part and positioning
        response.setBodyPart(examination.getBodyPart());
        response.setLateralityApplicable(examination.getLateralityApplicable());
        response.setPositioningNotes(examination.getPositioningNotes());

        // Clinical information
        response.setClinicalIndication(examination.getClinicalIndication());
        response.setInterpretationGuide(examination.getInterpretationGuide());

        // Approval and status
        response.setRequiresApproval(examination.getRequiresApproval());
        response.setIsActive(examination.getIsActive());

        // Audit fields
        response.setCreatedAt(examination.getCreatedAt());
        response.setCreatedBy(examination.getCreatedBy());
        response.setUpdatedAt(examination.getUpdatedAt());
        response.setUpdatedBy(examination.getUpdatedBy());

        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private RadiologyExamination convertToEntity(RadiologyExaminationRequest request) {
        RadiologyExamination examination = new RadiologyExamination();
        examination.setExamCode(request.getExamCode());
        examination.setExamName(request.getExamName());
        examination.setShortName(request.getShortName());
        examination.setCptCode(request.getCptCode());
        examination.setIcdProcedureCode(request.getIcdProcedureCode());
        examination.setPreparationInstructions(request.getPreparationInstructions());
        examination.setFastingRequired(request.getFastingRequired());
        examination.setFastingDurationHours(request.getFastingDurationHours());
        examination.setRequiresContrast(request.getRequiresContrast());
        examination.setContrastType(request.getContrastType());
        examination.setContrastVolumeMl(request.getContrastVolumeMl());
        examination.setExamDurationMinutes(request.getExamDurationMinutes());
        examination.setReportingTimeMinutes(request.getReportingTimeMinutes());
        examination.setBaseCost(request.getBaseCost());
        examination.setContrastCost(request.getContrastCost());
        examination.setBpjsTariff(request.getBpjsTariff());
        examination.setBodyPart(request.getBodyPart());
        examination.setLateralityApplicable(request.getLateralityApplicable());
        examination.setPositioningNotes(request.getPositioningNotes());
        examination.setClinicalIndication(request.getClinicalIndication());
        examination.setInterpretationGuide(request.getInterpretationGuide());
        examination.setRequiresApproval(request.getRequiresApproval());
        examination.setIsActive(request.getIsActive());

        // Set modality - need to create a minimal modality object with just the ID
        if (request.getModalityId() != null) {
            RadiologyModality modality = new RadiologyModality();
            modality.setId(request.getModalityId());
            examination.setModality(modality);
        }

        return examination;
    }
}
