package com.yudha.hms.pharmacy.controller;

import com.yudha.hms.pharmacy.dto.CreateDrugCategoryRequest;
import com.yudha.hms.pharmacy.dto.DrugCategoryResponse;
import com.yudha.hms.pharmacy.service.DrugService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Drug Category Controller.
 *
 * REST API endpoints for drug category management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/v1/pharmacy/categories")
@RequiredArgsConstructor
public class DrugCategoryController {

    private final DrugService drugService;

    /**
     * Create new drug category
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PHARMACY_ADMIN', 'ADMIN')")
    public ResponseEntity<DrugCategoryResponse> createDrugCategory(
            @Valid @RequestBody CreateDrugCategoryRequest request,
            @RequestAttribute("currentUserId") String userId) {
        DrugCategoryResponse response = drugService.createDrugCategory(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all root categories
     */
    @GetMapping("/root")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<DrugCategoryResponse>> getRootCategories() {
        List<DrugCategoryResponse> responses = drugService.getRootCategories();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get subcategories
     */
    @GetMapping("/{parentId}/subcategories")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<DrugCategoryResponse>> getSubcategories(@PathVariable UUID parentId) {
        List<DrugCategoryResponse> responses = drugService.getSubcategories(parentId);
        return ResponseEntity.ok(responses);
    }
}
