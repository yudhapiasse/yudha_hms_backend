package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.LabTestCategoryRequest;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.LabTestCategoryResponse;
import com.yudha.hms.laboratory.dto.response.LabTestResponse;
import com.yudha.hms.laboratory.dto.response.PageResponse;
import com.yudha.hms.laboratory.entity.LabTestCategory;
import com.yudha.hms.laboratory.service.LabTestCategoryService;
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
 * Laboratory Test Category Controller.
 *
 * REST controller for managing laboratory test categories.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/categories")
@RequiredArgsConstructor
@Slf4j
public class LabTestCategoryController {

    private final LabTestCategoryService categoryService;

    /**
     * Create new test category
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LabTestCategoryResponse>> createCategory(
            @Valid @RequestBody LabTestCategoryRequest request) {
        log.info("Creating test category: {}", request.getName());

        LabTestCategory category = LabTestCategory.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder())
                .icon(request.getIcon())
                .color(request.getColor())
                .active(request.getActive())
                .build();

        // Set parent if specified
        if (request.getParentId() != null) {
            LabTestCategory parent = new LabTestCategory();
            parent.setId(request.getParentId());
            category.setParent(parent);
        }

        category = categoryService.createCategory(category);

        LabTestCategoryResponse response = toResponse(category);
        log.info("Test category created successfully: {}", category.getCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", response));
    }

    /**
     * Update existing test category
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LabTestCategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody LabTestCategoryRequest request) {
        log.info("Updating test category ID: {}", id);

        LabTestCategory categoryUpdate = LabTestCategory.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder())
                .icon(request.getIcon())
                .color(request.getColor())
                .active(request.getActive())
                .build();

        // Set parent if specified
        if (request.getParentId() != null) {
            LabTestCategory parent = new LabTestCategory();
            parent.setId(request.getParentId());
            categoryUpdate.setParent(parent);
        }

        LabTestCategory category = categoryService.updateCategory(id, categoryUpdate);

        LabTestCategoryResponse response = toResponse(category);
        log.info("Test category updated successfully: {}", category.getCode());

        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
    }

    /**
     * Get test category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabTestCategoryResponse>> getCategoryById(
            @PathVariable UUID id) {
        log.info("Fetching test category ID: {}", id);

        LabTestCategory category = categoryService.getCategoryById(id);
        LabTestCategoryResponse response = toResponse(category);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all test categories with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LabTestCategoryResponse>>> getAllCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "displayOrder") Pageable pageable) {
        log.info("Fetching all test categories - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<LabTestCategory> categories;
        if (name != null || active != null) {
            categories = categoryService.searchCategories(name, pageable);
        } else {
            categories = categoryService.getAllCategories(pageable);
        }
        Page<LabTestCategoryResponse> responsePage = categories.map(this::toResponse);
        PageResponse<LabTestCategoryResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get all active test categories
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LabTestCategoryResponse>>> getActiveCategories() {
        log.info("Fetching all active test categories");

        List<LabTestCategory> categories = categoryService.getAllActiveCategories();
        List<LabTestCategoryResponse> responses = categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get child categories
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<LabTestCategoryResponse>>> getChildCategories(
            @PathVariable UUID id) {
        log.info("Fetching child categories for parent ID: {}", id);

        List<LabTestCategory> children = categoryService.getChildCategories(id);
        List<LabTestCategoryResponse> responses = children.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get tests in category
     */
    @GetMapping("/{id}/tests")
    public ResponseEntity<ApiResponse<List<LabTestResponse>>> getTestsInCategory(
            @PathVariable UUID id) {
        log.info("Fetching tests in category ID: {}", id);

        // Note: This method needs to be implemented in the service
        // For now, we'll return an empty list or throw UnsupportedOperationException
        throw new UnsupportedOperationException("getTestsInCategory not yet implemented in service");
    }

    /**
     * Soft delete test category
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable UUID id) {
        log.info("Deleting test category ID: {}", id);

        // Note: deleteCategory requires userId parameter in service
        categoryService.deleteCategory(id, "SYSTEM"); // TODO: Get actual user ID from security context
        log.info("Test category deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private LabTestCategoryResponse toResponse(LabTestCategory category) {
        LabTestCategoryResponse response = new LabTestCategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setCode(category.getCode());
        response.setDescription(category.getDescription());
        response.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        response.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        response.setLevel(category.getLevel());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setIcon(category.getIcon());
        response.setColor(category.getColor());
        response.setActive(category.getActive());
        response.setCreatedAt(category.getCreatedAt());
        response.setCreatedBy(category.getCreatedBy());
        response.setUpdatedAt(category.getUpdatedAt());
        response.setUpdatedBy(category.getUpdatedBy());
        return response;
    }
}
