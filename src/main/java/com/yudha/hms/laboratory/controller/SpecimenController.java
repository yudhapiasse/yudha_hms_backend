package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.SpecimenCollectionRequest;
import com.yudha.hms.laboratory.dto.request.SpecimenQualityCheckRequest;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.PageResponse;
import com.yudha.hms.laboratory.dto.response.SpecimenResponse;
import com.yudha.hms.laboratory.dto.search.SpecimenSearchCriteria;
import com.yudha.hms.laboratory.entity.Specimen;
import com.yudha.hms.laboratory.service.SpecimenService;
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
 * Specimen Controller.
 *
 * REST controller for managing laboratory specimens.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/specimens")
@RequiredArgsConstructor
@Slf4j
public class SpecimenController {

    private final SpecimenService specimenService;

    /**
     * Collect specimen
     */
    @PostMapping("/collect")
    public ResponseEntity<ApiResponse<SpecimenResponse>> collectSpecimen(
            @Valid @RequestBody SpecimenCollectionRequest request) {
        log.info("Collecting specimen for order item ID: {}", request.getOrderItemId());

        Specimen specimen = specimenService.collectSpecimen(
                request.getOrderItemId(),
                request.getCollectedBy(),
                request.getCollectedAt()
        );
        SpecimenResponse response = toResponse(specimen);

        log.info("Specimen collected successfully: {}", specimen.getBarcode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Specimen collected successfully", response));
    }

    /**
     * Receive specimen in laboratory
     */
    @PostMapping("/{barcode}/receive")
    public ResponseEntity<ApiResponse<SpecimenResponse>> receiveSpecimen(
            @PathVariable String barcode,
            @RequestParam UUID receivedBy) {
        log.info("Receiving specimen: {}", barcode);

        Specimen specimen = specimenService.receiveSpecimen(barcode, receivedBy);
        SpecimenResponse response = toResponse(specimen);

        log.info("Specimen received successfully: {}", specimen.getBarcode());

        return ResponseEntity.ok(ApiResponse.success("Specimen received successfully", response));
    }

    /**
     * Perform quality check
     */
    @PostMapping("/{barcode}/quality-check")
    public ResponseEntity<ApiResponse<SpecimenResponse>> qualityCheckSpecimen(
            @PathVariable String barcode,
            @Valid @RequestBody SpecimenQualityCheckRequest request) {
        log.info("Performing quality check on specimen: {}", barcode);

        Specimen specimen = specimenService.performQualityCheck(
                barcode,
                request.getQualityStatus(),
                request.getHemolysisDetected(),
                request.getLipemiaDetected(),
                request.getIcterusDetected(),
                request.getQualityNotes()
        );
        SpecimenResponse response = toResponse(specimen);

        log.info("Quality check completed for specimen: {}", specimen.getBarcode());

        return ResponseEntity.ok(ApiResponse.success("Quality check completed", response));
    }

    /**
     * Reject specimen
     */
    @PostMapping("/{barcode}/reject")
    public ResponseEntity<ApiResponse<SpecimenResponse>> rejectSpecimen(
            @PathVariable String barcode,
            @RequestParam String rejectionReason) {
        log.info("Rejecting specimen: {}", barcode);

        Specimen specimen = specimenService.rejectSpecimen(barcode, rejectionReason);
        SpecimenResponse response = toResponse(specimen);

        log.info("Specimen rejected: {}", specimen.getBarcode());

        return ResponseEntity.ok(ApiResponse.success("Specimen rejected", response));
    }

    /**
     * Start processing specimen
     */
    @PostMapping("/{barcode}/process")
    public ResponseEntity<ApiResponse<SpecimenResponse>> startProcessing(
            @PathVariable String barcode) {
        log.info("Starting processing for specimen: {}", barcode);

        Specimen specimen = specimenService.processSpecimen(barcode);
        SpecimenResponse response = toResponse(specimen);

        log.info("Processing started for specimen: {}", specimen.getBarcode());

        return ResponseEntity.ok(ApiResponse.success("Processing started", response));
    }

    /**
     * Complete processing specimen
     */
    @PostMapping("/{barcode}/complete")
    public ResponseEntity<ApiResponse<SpecimenResponse>> completeProcessing(
            @PathVariable String barcode) {
        log.info("Completing processing for specimen: {}", barcode);

        Specimen specimen = specimenService.completeSpecimenProcessing(barcode);
        SpecimenResponse response = toResponse(specimen);

        log.info("Processing completed for specimen: {}", specimen.getBarcode());

        return ResponseEntity.ok(ApiResponse.success("Processing completed", response));
    }

    /**
     * Store specimen
     */
    @PostMapping("/{barcode}/store")
    public ResponseEntity<ApiResponse<SpecimenResponse>> storeSpecimen(
            @PathVariable String barcode,
            @RequestParam String storageLocation,
            @RequestParam(required = false) java.math.BigDecimal storageTemperature) {
        log.info("Storing specimen: {}", barcode);

        Specimen specimen = specimenService.storeSpecimen(barcode, storageLocation, storageTemperature);
        SpecimenResponse response = toResponse(specimen);

        log.info("Specimen stored: {}", specimen.getBarcode());

        return ResponseEntity.ok(ApiResponse.success("Specimen stored", response));
    }

    /**
     * Dispose specimen
     */
    @PostMapping("/{barcode}/dispose")
    public ResponseEntity<ApiResponse<SpecimenResponse>> disposeSpecimen(
            @PathVariable String barcode,
            @RequestParam UUID disposedBy,
            @RequestParam String disposalMethod) {
        log.info("Disposing specimen: {}", barcode);

        Specimen specimen = specimenService.disposeSpecimen(barcode, disposedBy, disposalMethod);
        SpecimenResponse response = toResponse(specimen);

        log.info("Specimen disposed: {}", specimen.getBarcode());

        return ResponseEntity.ok(ApiResponse.success("Specimen disposed", response));
    }

    /**
     * Get specimen by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SpecimenResponse>> getSpecimenById(
            @PathVariable UUID id) {
        log.info("Fetching specimen ID: {}", id);

        Specimen specimen = specimenService.getSpecimenById(id);
        SpecimenResponse response = toResponse(specimen);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get specimen by barcode
     */
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<SpecimenResponse>> getSpecimenByBarcode(
            @PathVariable String barcode) {
        log.info("Fetching specimen by barcode: {}", barcode);

        Specimen specimen = specimenService.getSpecimenByBarcode(barcode);
        SpecimenResponse response = toResponse(specimen);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search specimens
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<SpecimenResponse>>> searchSpecimens(
            @RequestParam(required = false) String barcode,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) com.yudha.hms.laboratory.constant.SpecimenStatus status,
            @RequestParam(required = false) com.yudha.hms.laboratory.constant.SampleType specimenType,
            @RequestParam(required = false) LocalDateTime collectionDateFrom,
            @RequestParam(required = false) LocalDateTime collectionDateTo,
            @PageableDefault(size = 20, sort = "collectedAt") Pageable pageable) {
        log.info("Searching specimens - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        SpecimenSearchCriteria criteria = new SpecimenSearchCriteria();
        criteria.setBarcode(barcode);
        criteria.setOrderId(orderId);
        criteria.setPatientId(patientId);
        criteria.setStatus(status);
        criteria.setSpecimenType(specimenType);
        criteria.setCollectedDateFrom(collectionDateFrom);
        criteria.setCollectedDateTo(collectionDateTo);

        // Note: searchSpecimens method doesn't exist in service yet
        // Using getSpecimensByOrder as workaround for now
        List<Specimen> specimens;
        if (orderId != null) {
            specimens = specimenService.getSpecimensByOrder(orderId);
        } else {
            specimens = List.of(); // Empty list for now
        }

        // Convert to page manually (this is a workaround until proper search method is implemented)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), specimens.size());
        List<Specimen> pageContent = start < specimens.size() ? specimens.subList(start, end) : List.of();

        Page<Specimen> specimenPage = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, specimens.size());

        Page<SpecimenResponse> responsePage = specimenPage.map(this::toResponse);
        PageResponse<SpecimenResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get specimens by order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<List<SpecimenResponse>>> getSpecimensByOrder(
            @PathVariable UUID orderId) {
        log.info("Fetching specimens for order ID: {}", orderId);

        List<Specimen> specimens = specimenService.getSpecimensByOrder(orderId);
        List<SpecimenResponse> responses = specimens.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get specimens with quality issues
     */
    @GetMapping("/quality-issues")
    public ResponseEntity<ApiResponse<List<SpecimenResponse>>> getSpecimensWithQualityIssues() {
        log.info("Fetching specimens with quality issues");

        List<Specimen> specimens = specimenService.getSpecimensWithQualityIssues();
        List<SpecimenResponse> responses = specimens.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Convert entity to response DTO
     */
    private SpecimenResponse toResponse(Specimen specimen) {
        SpecimenResponse response = new SpecimenResponse();
        response.setId(specimen.getId());
        response.setSpecimenNumber(specimen.getSpecimenNumber());
        response.setBarcode(specimen.getBarcode());

        // Order information
        if (specimen.getOrder() != null) {
            response.setOrderId(specimen.getOrder().getId());
            response.setOrderNumber(specimen.getOrder().getOrderNumber());
            response.setPatientId(specimen.getOrder().getPatientId());
        }

        // Specimen details
        response.setSpecimenType(specimen.getSpecimenType());
        response.setSpecimenSource(specimen.getSpecimenSource());
        response.setVolumeMl(specimen.getVolumeMl());
        response.setContainerType(specimen.getContainerType());

        // Collection information
        response.setCollectedAt(specimen.getCollectedAt());
        response.setCollectedBy(specimen.getCollectedBy());
        response.setCollectionMethod(specimen.getCollectionMethod());
        response.setCollectionSite(specimen.getCollectionSite());

        // Reception
        response.setReceivedAt(specimen.getReceivedAt());
        response.setReceivedBy(specimen.getReceivedBy());

        // Quality checks
        response.setQualityStatus(specimen.getQualityStatus());
        response.setQualityNotes(specimen.getQualityNotes());
        response.setRejectionReason(specimen.getRejectionReason());

        // Pre-analytical validations
        response.setFastingStatusMet(specimen.getFastingStatusMet());
        response.setVolumeAdequate(specimen.getVolumeAdequate());
        response.setContainerAppropriate(specimen.getContainerAppropriate());
        response.setLabelingCorrect(specimen.getLabelingCorrect());
        response.setTemperatureAppropriate(specimen.getTemperatureAppropriate());
        response.setHemolysisDetected(specimen.getHemolysisDetected());
        response.setLipemiaDetected(specimen.getLipemiaDetected());
        response.setIcterusDetected(specimen.getIcterusDetected());

        // Storage
        response.setStorageLocation(specimen.getStorageLocation());
        response.setStorageTemperature(specimen.getStorageTemperature());
        response.setStoredAt(specimen.getStoredAt());

        // Processing
        response.setProcessingStartedAt(specimen.getProcessingStartedAt());
        response.setProcessingCompletedAt(specimen.getProcessingCompletedAt());
        response.setProcessedBy(specimen.getProcessedBy());

        // Status
        response.setStatus(specimen.getStatus());

        // Disposal
        response.setDisposedAt(specimen.getDisposedAt());
        response.setDisposedBy(specimen.getDisposedBy());
        response.setDisposalMethod(specimen.getDisposalMethod());

        // Notes
        response.setNotes(specimen.getNotes());

        // Audit fields
        response.setCreatedAt(specimen.getCreatedAt());
        response.setUpdatedAt(specimen.getUpdatedAt());

        return response;
    }
}
