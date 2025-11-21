package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.LabPanelRequest;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.LabPanelResponse;
import com.yudha.hms.laboratory.dto.response.PageResponse;
import com.yudha.hms.laboratory.entity.LabPanel;
import com.yudha.hms.laboratory.service.LabPanelService;
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
 * Laboratory Panel Controller.
 *
 * REST controller for managing laboratory test panels.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/panels")
@RequiredArgsConstructor
@Slf4j
public class LabPanelController {

    private final LabPanelService panelService;

    /**
     * Create new laboratory panel
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LabPanelResponse>> createPanel(
            @Valid @RequestBody LabPanelRequest request) {
        log.info("Creating laboratory panel: {}", request.getName());

        LabPanel panel = convertToEntity(request);
        LabPanel savedPanel = panelService.createPanel(panel);
        LabPanelResponse response = toResponse(savedPanel);

        log.info("Laboratory panel created successfully: {}", savedPanel.getPanelCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Panel created successfully", response));
    }

    /**
     * Update existing laboratory panel
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LabPanelResponse>> updatePanel(
            @PathVariable UUID id,
            @Valid @RequestBody LabPanelRequest request) {
        log.info("Updating laboratory panel ID: {}", id);

        LabPanel panelUpdate = convertToEntity(request);
        LabPanel panel = panelService.updatePanel(id, panelUpdate);
        LabPanelResponse response = toResponse(panel);

        log.info("Laboratory panel updated successfully: {}", panel.getPanelCode());

        return ResponseEntity.ok(ApiResponse.success("Panel updated successfully", response));
    }

    /**
     * Get laboratory panel by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabPanelResponse>> getPanelById(
            @PathVariable UUID id) {
        log.info("Fetching laboratory panel ID: {}", id);

        LabPanel panel = panelService.getPanelById(id);
        LabPanelResponse response = toResponse(panel);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search laboratory panels
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LabPanelResponse>>> searchPanels(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.info("Searching laboratory panels - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<LabPanel> panels = panelService.searchPanels(search, pageable);
        Page<LabPanelResponse> responsePage = panels.map(this::toResponse);
        PageResponse<LabPanelResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get all active panels
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LabPanelResponse>>> getActivePanels() {
        log.info("Fetching all active panels");

        List<LabPanel> panels = panelService.getAllActivePanels();
        List<LabPanelResponse> responses = panels.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Add test to panel
     */
    @PostMapping("/{id}/tests")
    public ResponseEntity<ApiResponse<LabPanelResponse>> addTestToPanel(
            @PathVariable UUID id,
            @RequestParam UUID testId,
            @RequestParam(required = false, defaultValue = "true") Boolean isMandatory) {
        log.info("Adding test {} to panel {}", testId, id);

        panelService.addTestToPanel(id, testId, isMandatory);
        LabPanel panel = panelService.getPanelById(id);
        LabPanelResponse response = toResponse(panel);

        log.info("Test added to panel successfully");

        return ResponseEntity.ok(ApiResponse.success("Test added to panel successfully", response));
    }

    /**
     * Remove test from panel
     */
    @DeleteMapping("/{id}/tests/{testId}")
    public ResponseEntity<ApiResponse<LabPanelResponse>> removeTestFromPanel(
            @PathVariable UUID id,
            @PathVariable UUID testId) {
        log.info("Removing test {} from panel {}", testId, id);

        panelService.removeTestFromPanel(id, testId);
        LabPanel panel = panelService.getPanelById(id);
        LabPanelResponse response = toResponse(panel);

        log.info("Test removed from panel successfully");

        return ResponseEntity.ok(ApiResponse.success("Test removed from panel successfully", response));
    }

    /**
     * Update panel pricing
     */
    @PatchMapping("/{id}/pricing")
    public ResponseEntity<ApiResponse<LabPanelResponse>> updatePanelPricing(
            @PathVariable UUID id,
            @RequestParam BigDecimal basePrice,
            @RequestParam(required = false) BigDecimal discountPercentage) {
        log.info("Updating pricing for panel ID: {}", id);

        LabPanel panel = panelService.updatePanelPricing(id, basePrice, null, discountPercentage);
        LabPanelResponse response = toResponse(panel);

        log.info("Panel pricing updated successfully");

        return ResponseEntity.ok(ApiResponse.success("Panel pricing updated successfully", response));
    }

    /**
     * Soft delete laboratory panel
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePanel(
            @PathVariable UUID id) {
        log.info("Deleting laboratory panel ID: {}", id);

        panelService.deletePanel(id, "SYSTEM");
        log.info("Laboratory panel deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Panel deleted successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private LabPanelResponse toResponse(LabPanel panel) {
        LabPanelResponse response = new LabPanelResponse();
        response.setId(panel.getId());
        response.setName(panel.getName());
        response.setPanelCode(panel.getPanelCode());
        response.setDescription(panel.getDescription());
        response.setCategoryId(panel.getCategory() != null ? panel.getCategory().getId() : null);
        response.setCategoryName(panel.getCategory() != null ? panel.getCategory().getName() : null);
        response.setPackagePrice(panel.getPackagePrice());
        response.setDiscountPercentage(panel.getDiscountPercentage());
        response.setActive(panel.getActive());
        response.setCreatedAt(panel.getCreatedAt());
        response.setUpdatedAt(panel.getUpdatedAt());

        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private LabPanel convertToEntity(LabPanelRequest request) {
        LabPanel panel = new LabPanel();
        panel.setPanelCode(request.getPanelCode());
        panel.setName(request.getName());
        panel.setDescription(request.getDescription());
        panel.setPackagePrice(request.getPackagePrice());
        panel.setBpjsPackageTariff(request.getBpjsPackageTariff());
        panel.setDiscountPercentage(request.getDiscountPercentage());
        panel.setNotes(request.getNotes());
        panel.setActive(request.getActive());

        // Set category - need to create a minimal category object with just the ID
        if (request.getCategoryId() != null) {
            com.yudha.hms.laboratory.entity.LabTestCategory category = new com.yudha.hms.laboratory.entity.LabTestCategory();
            category.setId(request.getCategoryId());
            panel.setCategory(category);
        }

        return panel;
    }
}
