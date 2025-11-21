package com.yudha.hms.radiology.controller;

import com.yudha.hms.radiology.dto.request.RadiologyImageRequest;
import com.yudha.hms.radiology.dto.request.RadiologyResultRequest;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.PageResponse;
import com.yudha.hms.radiology.dto.response.RadiologyImageResponse;
import com.yudha.hms.radiology.dto.response.RadiologyResultResponse;
import com.yudha.hms.radiology.entity.RadiologyImage;
import com.yudha.hms.radiology.entity.RadiologyResult;
import com.yudha.hms.radiology.service.RadiologyResultService;
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
 * Radiology Result Controller.
 *
 * REST controller for managing radiology examination results and reports.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/results")
@RequiredArgsConstructor
@Slf4j
public class RadiologyResultController {

    private final RadiologyResultService resultService;

    /**
     * Create new radiology result
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> createResult(
            @Valid @RequestBody RadiologyResultRequest request) {
        log.info("Creating radiology result for order item ID: {}", request.getOrderItemId());

        RadiologyResult result = resultService.createResult(
                request.getOrderItemId(),
                request.getPerformedByTechnicianId());
        RadiologyResultResponse response = toResponse(result);

        log.info("Radiology result created successfully: {}", result.getResultNumber());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Result created successfully", response));
    }

    /**
     * Update result findings
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> updateResultFindings(
            @PathVariable UUID id,
            @RequestParam String findings,
            @RequestParam String impression,
            @RequestParam(required = false) String recommendations) {
        log.info("Updating findings for result ID: {}", id);

        RadiologyResult result = resultService.enterFindings(id, findings, impression, recommendations);
        RadiologyResultResponse response = toResponse(result);

        log.info("Result findings updated successfully");

        return ResponseEntity.ok(ApiResponse.success("Result findings updated successfully", response));
    }

    /**
     * Attach DICOM study to result
     */
    @PostMapping("/{id}/attach-dicom")
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> attachDicomStudy(
            @PathVariable UUID id,
            @RequestParam String dicomStudyId) {
        log.info("Attaching DICOM study to result ID: {}", id);

        RadiologyResult result = resultService.attachDicomStudy(id, dicomStudyId);
        RadiologyResultResponse response = toResponse(result);

        return ResponseEntity.ok(ApiResponse.success("DICOM study attached successfully", response));
    }

    /**
     * Add image to result
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> addImage(
            @PathVariable UUID id,
            @Valid @RequestBody RadiologyImageRequest request) {
        log.info("Adding image to result ID: {}", id);

        RadiologyResult result = resultService.addImageToResult(id);
        RadiologyResultResponse response = toResponse(result);

        return ResponseEntity.ok(ApiResponse.success("Image added successfully", response));
    }

    /**
     * Radiologist review
     */
    @PostMapping("/{id}/radiologist-review")
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> radiologistReview(
            @PathVariable UUID id,
            @RequestParam UUID radiologistId,
            @RequestParam String findings,
            @RequestParam String impression,
            @RequestParam(required = false) String recommendations) {
        log.info("Radiologist reviewing result ID: {}", id);

        RadiologyResult result = resultService.radiologistReview(id, radiologistId, findings, impression, recommendations);
        RadiologyResultResponse response = toResponse(result);

        return ResponseEntity.ok(ApiResponse.success("Radiologist review completed successfully", response));
    }

    /**
     * Finalize result
     */
    @PostMapping("/{id}/finalize")
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> finalizeResult(
            @PathVariable UUID id,
            @RequestParam UUID finalizedBy) {
        log.info("Finalizing result ID: {}", id);

        RadiologyResult result = resultService.finalizeResult(id, finalizedBy);
        RadiologyResultResponse response = toResponse(result);

        log.info("Result finalized successfully");

        return ResponseEntity.ok(ApiResponse.success("Result finalized successfully", response));
    }

    /**
     * Amend result
     */
    @PostMapping("/{id}/amend")
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> amendResult(
            @PathVariable UUID id,
            @RequestParam String amendmentReason,
            @RequestParam UUID amendedBy) {
        log.info("Amending result ID: {}", id);

        RadiologyResult result = resultService.amendResult(id, amendmentReason, amendedBy);
        RadiologyResultResponse response = toResponse(result);

        log.info("Result amended successfully");

        return ResponseEntity.ok(ApiResponse.success("Result amended successfully", response));
    }

    /**
     * Get radiology result by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyResultResponse>> getResultById(
            @PathVariable UUID id) {
        log.info("Fetching radiology result ID: {}", id);

        RadiologyResult result = resultService.getResultById(id);
        RadiologyResultResponse response = toResponse(result);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search radiology results
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RadiologyResultResponse>>> searchResults(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "performedDate") Pageable pageable) {
        log.info("Searching radiology results - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<RadiologyResult> results = resultService.searchResults(search, pageable);
        Page<RadiologyResultResponse> responsePage = results.map(this::toResponse);
        PageResponse<RadiologyResultResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get results by patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<PageResponse<RadiologyResultResponse>>> getResultsByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "performedDate") Pageable pageable) {
        log.info("Fetching results for patient ID: {}", patientId);

        Page<RadiologyResult> results = resultService.getResultsByPatient(patientId, pageable);
        Page<RadiologyResultResponse> responsePage = results.map(this::toResponse);
        PageResponse<RadiologyResultResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get pending results
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<RadiologyResultResponse>>> getPendingResults() {
        log.info("Fetching pending radiology results");

        List<RadiologyResult> results = resultService.getPendingResults();
        List<RadiologyResultResponse> responses = results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get results awaiting radiologist
     */
    @GetMapping("/awaiting-radiologist")
    public ResponseEntity<ApiResponse<List<RadiologyResultResponse>>> getResultsAwaitingRadiologist() {
        log.info("Fetching results awaiting radiologist review");

        List<RadiologyResult> results = resultService.getResultsAwaitingRadiologist();
        List<RadiologyResultResponse> responses = results.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get result images
     */
    @GetMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<RadiologyImageResponse>>> getResultImages(
            @PathVariable UUID id) {
        log.info("Fetching images for result ID: {}", id);

        List<RadiologyImage> images = resultService.getResultImages(id);
        List<RadiologyImageResponse> responses = images.stream()
                .map(this::toImageResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Cancel result
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelResult(
            @PathVariable UUID id) {
        log.info("Cancelling radiology result ID: {}", id);

        resultService.cancelResult(id, "SYSTEM");
        log.info("Radiology result cancelled successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Result cancelled successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private RadiologyResultResponse toResponse(RadiologyResult result) {
        RadiologyResultResponse response = new RadiologyResultResponse();
        response.setId(result.getId());
        response.setResultNumber(result.getResultNumber());

        // Order item information
        if (result.getOrderItem() != null) {
            response.setOrderItemId(result.getOrderItem().getId());
            if (result.getOrderItem().getOrder() != null) {
                response.setOrderNumber(result.getOrderItem().getOrder().getOrderNumber());
            }
        }

        // Examination information
        if (result.getExamination() != null) {
            response.setExaminationId(result.getExamination().getId());
            response.setExaminationCode(result.getExamination().getExamCode());
            response.setExaminationName(result.getExamination().getExamName());
            if (result.getExamination().getModality() != null) {
                response.setModalityCode(result.getExamination().getModality().getCode());
                response.setModalityName(result.getExamination().getModality().getName());
            }
        }

        // Patient information
        if (result.getPatient() != null) {
            response.setPatientId(result.getPatient().getId());
        }

        // Examination execution
        response.setPerformedDate(result.getPerformedDate());
        response.setPerformedByTechnicianId(result.getPerformedByTechnicianId());

        // Report content
        response.setFindings(result.getFindings());
        response.setImpression(result.getImpression());
        response.setRecommendations(result.getRecommendations());

        // Radiologist information
        response.setRadiologistId(result.getRadiologistId());
        response.setReportedDate(result.getReportedDate());

        // Status
        response.setIsFinalized(result.getIsFinalized());
        response.setFinalizedDate(result.getFinalizedDate());
        response.setIsAmended(result.getIsAmended());
        response.setAmendmentReason(result.getAmendmentReason());

        // Images
        response.setImageCount(result.getImageCount());
        response.setDicomStudyId(result.getDicomStudyId());

        // Audit fields
        response.setCreatedAt(result.getCreatedAt());
        response.setCreatedBy(result.getCreatedBy());
        response.setUpdatedAt(result.getUpdatedAt());
        response.setUpdatedBy(result.getUpdatedBy());

        return response;
    }

    /**
     * Convert image entity to response DTO
     */
    private RadiologyImageResponse toImageResponse(RadiologyImage image) {
        RadiologyImageResponse response = new RadiologyImageResponse();
        response.setId(image.getId());
        response.setResultId(image.getResult().getId());

        // DICOM Information
        response.setDicomStudyUid(image.getDicomStudyUid());
        response.setDicomSeriesUid(image.getDicomSeriesUid());
        response.setDicomInstanceUid(image.getDicomInstanceUid());
        response.setModality(image.getModality());
        response.setBodyPartExamined(image.getBodyPartExamined());
        response.setImageType(image.getImageType());

        // File Information
        response.setFilePath(image.getFilePath());
        response.setFileSizeBytes(image.getFileSizeBytes());
        response.setAcquisitionDate(image.getAcquisitionDate());

        // Positioning
        response.setViewPosition(image.getViewPosition());
        response.setIsKeyImage(image.getIsKeyImage());
        response.setNotes(image.getNotes());

        // Audit fields
        response.setCreatedAt(image.getCreatedAt());
        response.setUpdatedAt(image.getUpdatedAt());

        return response;
    }
}
