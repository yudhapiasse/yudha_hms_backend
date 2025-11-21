package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.LabResultRequest;
import com.yudha.hms.laboratory.dto.request.ResultParameterEntryRequest;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.LabResultParameterResponse;
import com.yudha.hms.laboratory.dto.response.LabResultResponse;
import com.yudha.hms.laboratory.dto.response.PageResponse;
import com.yudha.hms.laboratory.dto.response.PatientResultHistoryResponse;
import com.yudha.hms.laboratory.dto.search.ResultSearchCriteria;
import com.yudha.hms.laboratory.entity.LabResult;
import com.yudha.hms.laboratory.entity.LabResultParameter;
import com.yudha.hms.laboratory.service.LabResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Laboratory Result Controller.
 *
 * REST controller for managing laboratory results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/results")
@RequiredArgsConstructor
@Slf4j
public class LabResultController {

    private final LabResultService resultService;

    /**
     * Create new laboratory result
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LabResultResponse>> createResult(
            @Valid @RequestBody LabResultRequest request) {
        log.info("Creating laboratory result for order item ID: {}", request.getOrderItemId());

        // Create the result
        LabResult result = resultService.createResult(
                request.getOrderItemId(),
                request.getSpecimenId(),
                request.getEnteredBy(),
                request.getEntryMethod()
        );

        // Convert parameter requests to service DTOs
        List<LabResultService.ResultParameterEntry> parameters = request.getParameterResults().stream()
                .map(p -> {
                    LabResultService.ResultParameterEntry entry = new LabResultService.ResultParameterEntry();
                    entry.testParameterId = p.getTestParameterId();
                    entry.resultValue = p.getResultValue();
                    entry.numericValue = p.getNumericValue();
                    entry.textValue = p.getTextValue();
                    entry.notes = p.getNotes();
                    return entry;
                })
                .collect(Collectors.toList());

        // Enter parameters
        result = resultService.enterResultParameters(result.getId(), parameters, request.getEnteredBy());

        // Set additional fields
        if (request.getOverallInterpretation() != null) {
            result.setOverallInterpretation(request.getOverallInterpretation());
        }
        if (request.getNotes() != null) {
            result.setNotes(request.getNotes());
        }

        LabResultResponse response = toResponse(result);
        log.info("Laboratory result created successfully with ID: {}", result.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Result created successfully", response));
    }

    /**
     * Enter result parameters
     */
    @PostMapping("/{id}/parameters")
    public ResponseEntity<ApiResponse<LabResultResponse>> enterResultParameters(
            @PathVariable UUID id,
            @Valid @RequestBody List<ResultParameterEntryRequest> parameterRequests,
            @RequestParam UUID enteredBy) {
        log.info("Entering parameters for result ID: {}", id);

        // Convert parameter requests to service DTOs
        List<LabResultService.ResultParameterEntry> parameters = parameterRequests.stream()
                .map(p -> {
                    LabResultService.ResultParameterEntry entry = new LabResultService.ResultParameterEntry();
                    entry.testParameterId = p.getTestParameterId();
                    entry.resultValue = p.getResultValue();
                    entry.numericValue = p.getNumericValue();
                    entry.textValue = p.getTextValue();
                    entry.notes = p.getNotes();
                    return entry;
                })
                .collect(Collectors.toList());

        LabResult result = resultService.enterResultParameters(id, parameters, enteredBy);
        LabResultResponse response = toResponse(result);

        log.info("Result parameters entered successfully");

        return ResponseEntity.ok(ApiResponse.success("Parameters entered successfully", response));
    }

    /**
     * Get laboratory result by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabResultResponse>> getResultById(
            @PathVariable UUID id) {
        log.info("Fetching laboratory result ID: {}", id);

        LabResult result = resultService.getResultById(id);
        LabResultResponse response = toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search laboratory results
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LabResultResponse>>> searchResults(
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID testId,
            @RequestParam(required = false) String resultStatus,
            @RequestParam(required = false) LocalDateTime enteredDateFrom,
            @RequestParam(required = false) LocalDateTime enteredDateTo,
            @PageableDefault(size = 20, sort = "enteredAt") Pageable pageable) {
        log.info("Searching laboratory results - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        ResultSearchCriteria criteria = new ResultSearchCriteria();
        criteria.setOrderId(orderId);
        criteria.setPatientId(patientId);
        criteria.setTestId(testId);
        criteria.setEnteredDateFrom(enteredDateFrom);
        criteria.setEnteredDateTo(enteredDateTo);

        // Note: searchResults method doesn't exist in service, so we'll use getResultsByOrder for now
        // or return empty page if orderId is not specified
        List<LabResult> results;
        if (orderId != null) {
            results = resultService.getResultsByOrder(orderId);
        } else {
            results = List.of(); // Empty list for now
        }

        // Convert to page manually (this is a workaround until proper search method is implemented)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());
        List<LabResult> pageContent = results.subList(start, end);

        Page<LabResult> resultPage = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, results.size());

        Page<LabResultResponse> responsePage = resultPage.map(this::toResponse);
        PageResponse<LabResultResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get results by order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<LabResultResponse>>> getResultsByOrder(
            @PathVariable UUID orderId) {
        log.info("Fetching results for order ID: {}", orderId);

        List<LabResult> results = resultService.getResultsByOrder(orderId);
        List<LabResultResponse> responses = results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get patient result history for a specific test
     */
    @GetMapping("/patient/{patientId}/test/{testId}/history")
    public ResponseEntity<ApiResponse<List<LabResultResponse>>> getPatientResultHistory(
            @PathVariable UUID patientId,
            @PathVariable UUID testId) {
        log.info("Fetching result history for patient {} and test {}", patientId, testId);

        List<LabResult> history = resultService.getPatientResultHistory(patientId, testId);
        List<LabResultResponse> responses = history.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get results awaiting validation
     */
    @GetMapping("/awaiting-validation")
    public ResponseEntity<ApiResponse<PageResponse<LabResultResponse>>> getResultsAwaitingValidation(
            @PageableDefault(size = 20, sort = "enteredAt") Pageable pageable) {
        log.info("Fetching results awaiting validation - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<LabResult> results = resultService.getResultsAwaitingValidation(pageable);
        Page<LabResultResponse> responsePage = results.map(this::toResponse);
        PageResponse<LabResultResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get results with panic values
     */
    @GetMapping("/panic-values")
    public ResponseEntity<ApiResponse<List<LabResultResponse>>> getResultsWithPanicValues() {
        log.info("Fetching results with panic values");

        List<LabResult> results = resultService.getResultsWithPanicValues();
        List<LabResultResponse> responses = results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Validate result
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<LabResultResponse>> validateResult(
            @PathVariable UUID id,
            @RequestParam UUID validatorUserId) {
        log.info("Validating result ID: {}", id);

        LabResult result = resultService.validateResult(id, validatorUserId);
        LabResultResponse response = toResponse(result);

        log.info("Result validated successfully");

        return ResponseEntity.ok(ApiResponse.success("Result validated successfully", response));
    }

    /**
     * Finalize result
     */
    @PostMapping("/{id}/finalize")
    public ResponseEntity<ApiResponse<LabResultResponse>> finalizeResult(
            @PathVariable UUID id,
            @RequestParam UUID finalizedByUserId) {
        log.info("Finalizing result ID: {}", id);

        LabResult result = resultService.finalizeResult(id, finalizedByUserId);
        LabResultResponse response = toResponse(result);

        log.info("Result finalized successfully");

        return ResponseEntity.ok(ApiResponse.success("Result finalized successfully", response));
    }

    /**
     * Amend result
     */
    @PostMapping("/{id}/amend")
    public ResponseEntity<ApiResponse<LabResultResponse>> amendResult(
            @PathVariable UUID id,
            @RequestParam String amendmentReason,
            @RequestParam UUID amendedByUserId) {
        log.info("Amending result ID: {}", id);

        LabResult result = resultService.amendResult(id, amendmentReason, amendedByUserId);
        LabResultResponse response = toResponse(result);

        log.info("Result amended successfully");

        return ResponseEntity.ok(ApiResponse.success("Result amended successfully", response));
    }

    /**
     * Cancel result
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LabResultResponse>> cancelResult(
            @PathVariable UUID id,
            @RequestParam String cancellationReason,
            @RequestParam UUID cancelledByUserId) {
        log.info("Cancelling result ID: {}", id);

        LabResult result = resultService.cancelResult(id, cancellationReason, cancelledByUserId);
        LabResultResponse response = toResponse(result);

        log.info("Result cancelled successfully");

        return ResponseEntity.ok(ApiResponse.success("Result cancelled successfully", response));
    }

    /**
     * Perform delta check
     */
    @PostMapping("/{id}/delta-check")
    public ResponseEntity<ApiResponse<LabResultResponse>> performDeltaCheck(
            @PathVariable UUID id) {
        log.info("Performing delta check for result ID: {}", id);

        LabResult result = resultService.performDeltaCheck(id);
        LabResultResponse response = toResponse(result);

        return ResponseEntity.ok(ApiResponse.success("Delta check performed", response));
    }

    /**
     * Convert entity to response DTO
     */
    private LabResultResponse toResponse(LabResult result) {
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

        // Audit fields - Note: LabResult entity doesn't have createdBy/updatedBy fields
        // These would need to be added to the entity if required

        return response;
    }

    /**
     * Convert parameter entity to response DTO
     */
    private LabResultParameterResponse toParameterResponse(LabResultParameter parameter) {
        LabResultParameterResponse response = new LabResultParameterResponse();
        response.setId(parameter.getId());
        response.setResultId(parameter.getResult() != null ? parameter.getResult().getId() : null);
        response.setTestParameterId(parameter.getTestParameter() != null ? parameter.getTestParameter().getId() : null);

        // Parameter details
        response.setParameterCode(parameter.getParameterCode());
        response.setParameterName(parameter.getParameterName());

        // Result value
        response.setResultValue(parameter.getResultValue());
        response.setNumericValue(parameter.getNumericValue());
        response.setTextValue(parameter.getTextValue());
        response.setUnit(parameter.getUnit());

        // Reference range
        response.setReferenceRangeLow(parameter.getReferenceRangeLow());
        response.setReferenceRangeHigh(parameter.getReferenceRangeHigh());
        response.setReferenceRangeText(parameter.getReferenceRangeText());

        // Interpretation flags
        response.setInterpretationFlag(parameter.getInterpretationFlag());
        response.setIsAbnormal(parameter.getIsAbnormal());
        response.setIsCritical(parameter.getIsCritical());

        // Delta check
        response.setDeltaCheckFlagged(parameter.getDeltaCheckFlagged());
        response.setPreviousValue(parameter.getPreviousValue());
        response.setDeltaPercentage(parameter.getDeltaPercentage());
        response.setDeltaAbsolute(parameter.getDeltaAbsolute());

        // Method and equipment
        response.setTestMethod(parameter.getTestMethod());
        response.setEquipmentId(parameter.getEquipmentId());
        response.setEquipmentName(parameter.getEquipmentName());

        // QC
        response.setQcLevel(parameter.getQcLevel());
        response.setQcWithinRange(parameter.getQcWithinRange());

        // Notes
        response.setNotes(parameter.getNotes());

        // Audit fields - Note: LabResultParameter entity doesn't have createdBy/updatedBy fields
        // These would need to be added to the entity if required

        return response;
    }
}
