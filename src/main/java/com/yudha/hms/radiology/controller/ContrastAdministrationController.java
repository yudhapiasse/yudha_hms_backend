package com.yudha.hms.radiology.controller;

import com.yudha.hms.radiology.constant.ReactionSeverity;
import com.yudha.hms.radiology.dto.request.ContrastAdministrationRequest;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.ContrastAdministrationResponse;
import com.yudha.hms.radiology.entity.ContrastAdministration;
import com.yudha.hms.radiology.entity.RadiologyOrderItem;
import com.yudha.hms.radiology.service.ContrastAdministrationService;
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
 * Contrast Administration Controller.
 *
 * REST controller for managing contrast media administration and reactions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/contrast")
@RequiredArgsConstructor
@Slf4j
public class ContrastAdministrationController {

    private final ContrastAdministrationService contrastService;

    /**
     * Record contrast administration
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ContrastAdministrationResponse>> recordAdministration(
            @Valid @RequestBody ContrastAdministrationRequest request) {
        log.info("Recording contrast administration for order item ID: {}", request.getOrderItemId());

        ContrastAdministration administration = convertToEntity(request);
        ContrastAdministration saved = contrastService.recordAdministration(administration);
        ContrastAdministrationResponse response = toResponse(saved);

        log.info("Contrast administration recorded successfully");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contrast administration recorded successfully", response));
    }

    /**
     * Record contrast reaction
     */
    @PostMapping("/{id}/reaction")
    public ResponseEntity<ApiResponse<ContrastAdministrationResponse>> recordReaction(
            @PathVariable UUID id,
            @RequestParam ReactionSeverity severity,
            @RequestParam String description,
            @RequestParam(required = false) String treatment) {
        log.info("Recording reaction for contrast administration ID: {}", id);

        ContrastAdministration administration = contrastService.recordReaction(id, severity, description, treatment);
        ContrastAdministrationResponse response = toResponse(administration);

        log.info("Contrast reaction recorded successfully");

        return ResponseEntity.ok(ApiResponse.success("Contrast reaction recorded successfully", response));
    }

    /**
     * Get contrast administration by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContrastAdministrationResponse>> getAdministrationById(
            @PathVariable UUID id) {
        log.info("Fetching contrast administration ID: {}", id);

        ContrastAdministration administration = contrastService.getAdministrationById(id);
        ContrastAdministrationResponse response = toResponse(administration);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get patient contrast history
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<ContrastAdministrationResponse>>> getPatientHistory(
            @PathVariable UUID patientId) {
        log.info("Fetching contrast history for patient ID: {}", patientId);

        List<ContrastAdministration> history = contrastService.getPatientContrastHistory(patientId);
        List<ContrastAdministrationResponse> responses = history.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get all contrast reactions
     */
    @GetMapping("/reactions")
    public ResponseEntity<ApiResponse<List<ContrastAdministrationResponse>>> getAllReactions() {
        log.info("Fetching all contrast reactions");

        List<ContrastAdministration> reactions = contrastService.getAdministrationsWithReactions();
        List<ContrastAdministrationResponse> responses = reactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get severe contrast reactions
     */
    @GetMapping("/severe-reactions")
    public ResponseEntity<ApiResponse<List<ContrastAdministrationResponse>>> getSevereReactions() {
        log.info("Fetching severe contrast reactions");

        List<ContrastAdministration> reactions = contrastService.getSevereReactions();
        List<ContrastAdministrationResponse> responses = reactions.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get administrations by batch number
     */
    @GetMapping("/batch/{batchNumber}")
    public ResponseEntity<ApiResponse<List<ContrastAdministrationResponse>>> getByBatchNumber(
            @PathVariable String batchNumber) {
        log.info("Fetching contrast administrations for batch: {}", batchNumber);

        List<ContrastAdministration> administrations = contrastService.getAdministrationsByBatch(batchNumber);
        List<ContrastAdministrationResponse> responses = administrations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Convert entity to response DTO
     */
    private ContrastAdministrationResponse toResponse(ContrastAdministration administration) {
        ContrastAdministrationResponse response = new ContrastAdministrationResponse();
        response.setId(administration.getId());

        // Order item information
        if (administration.getOrderItem() != null) {
            response.setOrderItemId(administration.getOrderItem().getId());
            if (administration.getOrderItem().getOrder() != null) {
                response.setOrderNumber(administration.getOrderItem().getOrder().getOrderNumber());
            }
        }

        // Patient information
        if (administration.getPatient() != null) {
            response.setPatientId(administration.getPatient().getId());
        }

        // Contrast details
        response.setContrastName(administration.getContrastName());
        response.setContrastType(administration.getContrastType());
        response.setVolumeMl(administration.getVolumeMl());
        response.setBatchNumber(administration.getBatchNumber());

        // Administration details
        response.setAdministeredBy(administration.getAdministeredBy());
        response.setAdministeredDate(administration.getAdministeredAt());

        // Reaction details
        response.setReactionObserved(administration.getReactionObserved());
        response.setReactionSeverity(administration.getReactionSeverity());
        response.setReactionDescription(administration.getReactionDescription());
        response.setTreatmentGiven(administration.getTreatmentGiven());

        // Audit fields
        response.setCreatedAt(administration.getCreatedAt());
        response.setUpdatedAt(administration.getUpdatedAt());

        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private ContrastAdministration convertToEntity(ContrastAdministrationRequest request) {
        ContrastAdministration administration = new ContrastAdministration();

        // Set order item - create minimal object with just ID
        if (request.getOrderItemId() != null) {
            RadiologyOrderItem orderItem = new RadiologyOrderItem();
            orderItem.setId(request.getOrderItemId());
            administration.setOrderItem(orderItem);
        }

        administration.setContrastName(request.getContrastName());
        administration.setContrastType(request.getContrastType());
        administration.setVolumeMl(request.getVolumeMl());
        administration.setBatchNumber(request.getBatchNumber());
        administration.setAdministeredBy(request.getAdministeredBy());

        return administration;
    }
}
