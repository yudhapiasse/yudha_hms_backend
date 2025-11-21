package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.ResultValidationRequest;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.LabResultResponse;
import com.yudha.hms.laboratory.dto.response.PageResponse;
import com.yudha.hms.laboratory.dto.response.ResultValidationResponse;
import com.yudha.hms.laboratory.entity.LabResult;
import com.yudha.hms.laboratory.entity.ResultValidation;
import com.yudha.hms.laboratory.service.ResultValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Result Validation Controller.
 *
 * REST controller for managing laboratory result validation workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/validation")
@RequiredArgsConstructor
@Slf4j
public class ResultValidationController {

    private final ResultValidationService validationService;

    /**
     * Validate result at any level
     */
    @PostMapping("/{resultId}")
    public ResponseEntity<ApiResponse<ResultValidationResponse>> validateResult(
            @PathVariable UUID resultId,
            @Valid @RequestBody ResultValidationRequest request) {
        log.info("Validating result ID: {} at level: {}", resultId, request.getValidationLevel());

        ResultValidation validation = validationService.validateResult(
            resultId,
            request.getValidationLevel(),
            request.getValidatedBy(),
            request.getValidatorName(),
            request.getValidationStatus(),
            request.getValidationNotes()
        );
        ResultValidationResponse response = toResponse(validation);

        log.info("Result validation recorded successfully");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Result validated successfully", response));
    }

    /**
     * Technician validation (Level 1)
     */
    @PostMapping("/{resultId}/technician")
    public ResponseEntity<ApiResponse<ResultValidationResponse>> technicianValidation(
            @PathVariable UUID resultId,
            @Valid @RequestBody ResultValidationRequest request) {
        log.info("Performing technician validation for result ID: {}", resultId);

        ResultValidation validation = validationService.technicianValidation(
            resultId, request.getValidatedBy(), request.getValidatorName(), request.getValidationNotes());
        ResultValidationResponse response = toResponse(validation);

        log.info("Technician validation completed");

        return ResponseEntity.ok(ApiResponse.success("Technician validation completed", response));
    }

    /**
     * Senior technician validation (Level 2)
     */
    @PostMapping("/{resultId}/senior-tech")
    public ResponseEntity<ApiResponse<ResultValidationResponse>> seniorTechValidation(
            @PathVariable UUID resultId,
            @Valid @RequestBody ResultValidationRequest request) {
        log.info("Performing senior tech validation for result ID: {}", resultId);

        ResultValidation validation = validationService.seniorTechValidation(
            resultId, request.getValidatedBy(), request.getValidatorName(), request.getValidationNotes());
        ResultValidationResponse response = toResponse(validation);

        log.info("Senior technician validation completed");

        return ResponseEntity.ok(ApiResponse.success("Senior technician validation completed", response));
    }

    /**
     * Pathologist validation (Level 3)
     */
    @PostMapping("/{resultId}/pathologist")
    public ResponseEntity<ApiResponse<ResultValidationResponse>> pathologistValidation(
            @PathVariable UUID resultId,
            @Valid @RequestBody ResultValidationRequest request) {
        log.info("Performing pathologist validation for result ID: {}", resultId);

        ResultValidation validation = validationService.pathologistValidation(
            resultId, request.getValidatedBy(), request.getValidatorName(), request.getValidationNotes());
        ResultValidationResponse response = toResponse(validation);

        log.info("Pathologist validation completed");

        return ResponseEntity.ok(ApiResponse.success("Pathologist validation completed", response));
    }

    /**
     * Clinical reviewer validation (Level 4)
     */
    @PostMapping("/{resultId}/clinical-reviewer")
    public ResponseEntity<ApiResponse<ResultValidationResponse>> clinicalReviewerValidation(
            @PathVariable UUID resultId,
            @Valid @RequestBody ResultValidationRequest request) {
        log.info("Performing clinical reviewer validation for result ID: {}", resultId);

        ResultValidation validation = validationService.clinicalReviewerValidation(
            resultId, request.getValidatedBy(), request.getValidatorName(), request.getValidationNotes());
        ResultValidationResponse response = toResponse(validation);

        log.info("Clinical reviewer validation completed");

        return ResponseEntity.ok(ApiResponse.success("Clinical reviewer validation completed", response));
    }

    /**
     * Get validation history for a result
     */
    @GetMapping("/{resultId}/history")
    public ResponseEntity<ApiResponse<List<ResultValidationResponse>>> getValidationHistory(
            @PathVariable UUID resultId) {
        log.info("Fetching validation history for result ID: {}", resultId);

        List<ResultValidation> validations = validationService.getValidationHistory(resultId);
        List<ResultValidationResponse> responses = validations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get pending validations by level
     */
    @GetMapping("/pending/{level}")
    public ResponseEntity<ApiResponse<PageResponse<LabResultResponse>>> getPendingValidations(
            @PathVariable String level,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("Fetching pending validations for level: {} - page: {}, size: {}",
                level, pageable.getPageNumber(), pageable.getPageSize());

        com.yudha.hms.laboratory.constant.ValidationLevel validationLevel =
            com.yudha.hms.laboratory.constant.ValidationLevel.valueOf(level);
        Page<LabResult> results = validationService.getResultsAwaitingValidation(validationLevel, pageable);
        Page<LabResultResponse> responsePage = results.map(this::toResultResponse);
        PageResponse<LabResultResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Convert validation entity to response DTO
     */
    private ResultValidationResponse toResponse(ResultValidation validation) {
        ResultValidationResponse response = new ResultValidationResponse();
        response.setId(validation.getId());
        response.setResultId(validation.getResult().getId());
        response.setValidationLevel(validation.getValidationLevel());
        response.setValidationStatus(validation.getValidationStatus());
        response.setValidatedBy(validation.getValidatedBy());
        response.setValidatorName(validation.getValidatorName());
        response.setValidatedAt(validation.getValidatedAt());
        response.setValidationNotes(validation.getValidationNotes());
        response.setIssuesIdentified(validation.getIssuesIdentified());
        response.setCorrectiveAction(validation.getCorrectiveAction());
        response.setCreatedAt(validation.getCreatedAt());
        return response;
    }

    /**
     * Convert result entity to response DTO
     */
    private LabResultResponse toResultResponse(LabResult result) {
        LabResultResponse response = new LabResultResponse();
        response.setId(result.getId());
        response.setResultNumber(result.getResultNumber());

        // Order information
        if (result.getOrder() != null) {
            response.setOrderId(result.getOrder().getId());
            response.setOrderNumber(result.getOrder().getOrderNumber());
            response.setPatientId(result.getOrder().getPatientId());
        }

        // Order item information
        if (result.getOrderItem() != null) {
            response.setOrderItemId(result.getOrderItem().getId());
        }

        // Specimen information
        if (result.getSpecimen() != null) {
            response.setSpecimenId(result.getSpecimen().getId());
            response.setSpecimenNumber(result.getSpecimen().getSpecimenNumber());
        }

        // Test information
        if (result.getTest() != null) {
            response.setTestId(result.getTest().getId());
            response.setTestCode(result.getTest().getTestCode());
            response.setTestName(result.getTest().getName());
        }

        // Result status
        response.setStatus(result.getStatus());

        // Entry information
        response.setEnteredAt(result.getEnteredAt());
        response.setEnteredBy(result.getEnteredBy());
        response.setEntryMethod(result.getEntryMethod());

        // Validation
        response.setValidatedAt(result.getValidatedAt());
        response.setValidatedBy(result.getValidatedBy());
        response.setValidationNotes(result.getValidationNotes());

        // Pathologist review
        response.setRequiresPathologistReview(result.getRequiresPathologistReview());
        response.setReviewedByPathologist(result.getReviewedByPathologist());
        response.setPathologistId(result.getPathologistId());
        response.setPathologistReviewedAt(result.getPathologistReviewedAt());
        response.setPathologistComments(result.getPathologistComments());

        // Result interpretation
        response.setOverallInterpretation(result.getOverallInterpretation());
        response.setClinicalSignificance(result.getClinicalSignificance());
        response.setRecommendations(result.getRecommendations());

        // Delta check
        response.setDeltaCheckPerformed(result.getDeltaCheckPerformed());
        response.setDeltaCheckFlagged(result.getDeltaCheckFlagged());
        response.setDeltaCheckNotes(result.getDeltaCheckNotes());
        response.setPreviousResultId(result.getPreviousResultId());

        // Panic/Critical values
        response.setHasPanicValues(result.getHasPanicValues());
        response.setPanicValueNotified(result.getPanicValueNotified());
        response.setPanicValueNotifiedAt(result.getPanicValueNotifiedAt());
        response.setPanicValueNotifiedTo(result.getPanicValueNotifiedTo());

        // Amendment
        response.setIsAmended(result.getIsAmended());
        response.setAmendedAt(result.getAmendedAt());
        response.setAmendedBy(result.getAmendedBy());
        response.setAmendmentReason(result.getAmendmentReason());
        response.setOriginalResultId(result.getOriginalResultId());

        // LIS interface
        response.setLisResultId(result.getLisResultId());
        response.setLisImportedAt(result.getLisImportedAt());

        // QC information
        response.setQcResultId(result.getQcResultId());
        response.setQcStatus(result.getQcStatus());

        // Report
        response.setReportGenerated(result.getReportGenerated());
        response.setReportGeneratedAt(result.getReportGeneratedAt());
        response.setReportSentToClinical(result.getReportSentToClinical());
        response.setReportSentAt(result.getReportSentAt());

        // Notes
        response.setNotes(result.getNotes());

        return response;
    }
}
