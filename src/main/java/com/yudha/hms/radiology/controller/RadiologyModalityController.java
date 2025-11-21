package com.yudha.hms.radiology.controller;

import com.yudha.hms.radiology.dto.request.RadiologyModalityRequest;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.PageResponse;
import com.yudha.hms.radiology.dto.response.RadiologyModalityResponse;
import com.yudha.hms.radiology.entity.RadiologyModality;
import com.yudha.hms.radiology.service.RadiologyModalityService;
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
 * Radiology Modality Controller.
 *
 * REST controller for managing radiology modalities (imaging equipment types).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/modalities")
@RequiredArgsConstructor
@Slf4j
public class RadiologyModalityController {

    private final RadiologyModalityService modalityService;

    /**
     * Create new radiology modality
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RadiologyModalityResponse>> createModality(
            @Valid @RequestBody RadiologyModalityRequest request) {
        log.info("Creating radiology modality: {}", request.getName());

        RadiologyModality modality = convertToEntity(request);
        RadiologyModality savedModality = modalityService.createModality(modality);
        RadiologyModalityResponse response = toResponse(savedModality);

        log.info("Radiology modality created successfully: {}", savedModality.getCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Modality created successfully", response));
    }

    /**
     * Update existing radiology modality
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyModalityResponse>> updateModality(
            @PathVariable UUID id,
            @Valid @RequestBody RadiologyModalityRequest request) {
        log.info("Updating radiology modality ID: {}", id);

        RadiologyModality modalityUpdate = convertToEntity(request);
        RadiologyModality modality = modalityService.updateModality(id, modalityUpdate);
        RadiologyModalityResponse response = toResponse(modality);

        log.info("Radiology modality updated successfully: {}", modality.getCode());

        return ResponseEntity.ok(ApiResponse.success("Modality updated successfully", response));
    }

    /**
     * Get radiology modality by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyModalityResponse>> getModalityById(
            @PathVariable UUID id) {
        log.info("Fetching radiology modality ID: {}", id);

        RadiologyModality modality = modalityService.getModalityById(id);
        RadiologyModalityResponse response = toResponse(modality);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all radiology modalities
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RadiologyModalityResponse>>> getAllModalities(
            @RequestParam(required = false) Boolean isActive,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.info("Fetching radiology modalities - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<RadiologyModality> modalities = modalityService.getAllModalities(isActive, pageable);
        Page<RadiologyModalityResponse> responsePage = modalities.map(this::toResponse);
        PageResponse<RadiologyModalityResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get modality by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<RadiologyModalityResponse>> getModalityByCode(
            @PathVariable String code) {
        log.info("Fetching radiology modality by code: {}", code);

        RadiologyModality modality = modalityService.getModalityByCode(code);
        RadiologyModalityResponse response = toResponse(modality);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get active modalities
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RadiologyModalityResponse>>> getActiveModalities() {
        log.info("Fetching active radiology modalities");

        List<RadiologyModality> modalities = modalityService.getAllActiveModalities();
        List<RadiologyModalityResponse> responses = modalities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get modalities requiring radiation
     */
    @GetMapping("/radiation")
    public ResponseEntity<ApiResponse<List<RadiologyModalityResponse>>> getModalitiesRequiringRadiation() {
        log.info("Fetching modalities requiring radiation");

        List<RadiologyModality> modalities = modalityService.getModalitiesRequiringRadiation();
        List<RadiologyModalityResponse> responses = modalities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get modalities not requiring radiation
     */
    @GetMapping("/no-radiation")
    public ResponseEntity<ApiResponse<List<RadiologyModalityResponse>>> getModalitiesNotRequiringRadiation() {
        log.info("Fetching modalities not requiring radiation");

        List<RadiologyModality> modalities = modalityService.getModalitiesNotRequiringRadiation();
        List<RadiologyModalityResponse> responses = modalities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Activate radiology modality
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<RadiologyModalityResponse>> activateModality(
            @PathVariable UUID id) {
        log.info("Activating radiology modality ID: {}", id);

        RadiologyModality modality = modalityService.activateModality(id);
        RadiologyModalityResponse response = toResponse(modality);

        log.info("Radiology modality activated successfully: {}", modality.getCode());

        return ResponseEntity.ok(ApiResponse.success("Modality activated successfully", response));
    }

    /**
     * Deactivate radiology modality
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<RadiologyModalityResponse>> deactivateModality(
            @PathVariable UUID id) {
        log.info("Deactivating radiology modality ID: {}", id);

        RadiologyModality modality = modalityService.deactivateModality(id);
        RadiologyModalityResponse response = toResponse(modality);

        log.info("Radiology modality deactivated successfully: {}", modality.getCode());

        return ResponseEntity.ok(ApiResponse.success("Modality deactivated successfully", response));
    }

    /**
     * Soft delete radiology modality
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteModality(
            @PathVariable UUID id) {
        log.info("Deleting radiology modality ID: {}", id);

        modalityService.deleteModality(id, "SYSTEM");
        log.info("Radiology modality deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Modality deleted successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private RadiologyModalityResponse toResponse(RadiologyModality modality) {
        RadiologyModalityResponse response = new RadiologyModalityResponse();
        response.setId(modality.getId());
        response.setCode(modality.getCode());
        response.setName(modality.getName());
        response.setDescription(modality.getDescription());
        response.setRequiresRadiation(modality.getRequiresRadiation());
        response.setAverageDurationMinutes(modality.getAverageDurationMinutes());
        response.setDisplayOrder(modality.getDisplayOrder());
        response.setIcon(modality.getIcon());
        response.setColor(modality.getColor());
        response.setIsActive(modality.getIsActive());
        response.setCreatedAt(modality.getCreatedAt());
        response.setCreatedBy(modality.getCreatedBy());
        response.setUpdatedAt(modality.getUpdatedAt());
        response.setUpdatedBy(modality.getUpdatedBy());
        // Note: examinationCount and activeRoomCount would need to be set from service if needed
        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private RadiologyModality convertToEntity(RadiologyModalityRequest request) {
        RadiologyModality modality = new RadiologyModality();
        modality.setCode(request.getCode());
        modality.setName(request.getName());
        modality.setDescription(request.getDescription());
        modality.setRequiresRadiation(request.getRequiresRadiation());
        modality.setAverageDurationMinutes(request.getAverageDurationMinutes());
        modality.setDisplayOrder(request.getDisplayOrder());
        modality.setIcon(request.getIcon());
        modality.setColor(request.getColor());
        modality.setIsActive(request.getIsActive());
        return modality;
    }
}
