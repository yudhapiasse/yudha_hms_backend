package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.entity.LabTestCategory;
import com.yudha.hms.laboratory.repository.LabTestCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Laboratory Test Category operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LabTestCategoryService {

    private final LabTestCategoryRepository labTestCategoryRepository;

    /**
     * Create new test category
     */
    public LabTestCategory createCategory(LabTestCategory category) {
        log.info("Creating new lab test category: {}", category.getName());

        // Validate unique code
        if (labTestCategoryRepository.findByCodeAndDeletedAtIsNull(category.getCode()).isPresent()) {
            throw new IllegalArgumentException("Category code already exists: " + category.getCode());
        }

        // If parent is specified, validate it exists
        if (category.getParent() != null) {
            LabTestCategory parent = labTestCategoryRepository.findByIdAndDeletedAtIsNull(category.getParent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + category.getParent().getId()));
            category.setLevel(parent.getLevel() + 1);
        } else {
            category.setLevel(0);
        }

        LabTestCategory saved = labTestCategoryRepository.save(category);
        log.info("Lab test category created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Update existing test category
     */
    public LabTestCategory updateCategory(UUID id, LabTestCategory categoryUpdate) {
        log.info("Updating lab test category: {}", id);

        LabTestCategory existing = labTestCategoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        // Check if code is being changed to an existing one
        if (!existing.getCode().equals(categoryUpdate.getCode())) {
            if (labTestCategoryRepository.findByCodeAndDeletedAtIsNull(categoryUpdate.getCode()).isPresent()) {
                throw new IllegalArgumentException("Category code already exists: " + categoryUpdate.getCode());
            }
        }

        // Update fields
        existing.setCode(categoryUpdate.getCode());
        existing.setName(categoryUpdate.getName());
        existing.setDescription(categoryUpdate.getDescription());
        existing.setDisplayOrder(categoryUpdate.getDisplayOrder());
        existing.setIcon(categoryUpdate.getIcon());
        existing.setColor(categoryUpdate.getColor());
        existing.setActive(categoryUpdate.getActive());

        // Handle parent change
        if (categoryUpdate.getParent() != null) {
            UUID newParentId = categoryUpdate.getParent().getId();
            UUID existingParentId = existing.getParent() != null ? existing.getParent().getId() : null;

            if (!newParentId.equals(existingParentId)) {
                LabTestCategory newParent = labTestCategoryRepository.findByIdAndDeletedAtIsNull(newParentId)
                        .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + newParentId));

                // Prevent circular reference
                if (newParent.getParent() != null && newParent.getParent().getId().equals(id)) {
                    throw new IllegalArgumentException("Circular reference detected: cannot set child as parent");
                }

                existing.setParent(newParent);
                existing.setLevel(newParent.getLevel() + 1);
            }
        }

        LabTestCategory updated = labTestCategoryRepository.save(existing);
        log.info("Lab test category updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) test category
     */
    public void deleteCategory(UUID id, String deletedBy) {
        log.info("Deleting lab test category: {}", id);

        LabTestCategory category = labTestCategoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));

        // Check if category has active tests
        long activeTestCount = labTestCategoryRepository.countActiveTestsByCategory(id);
        if (activeTestCount > 0) {
            throw new IllegalStateException("Cannot delete category with active tests. Found " + activeTestCount + " active tests.");
        }

        // Soft delete
        category.setDeletedAt(LocalDateTime.now());
        category.setDeletedBy(deletedBy);
        labTestCategoryRepository.save(category);

        log.info("Lab test category deleted successfully: {}", id);
    }

    /**
     * Get category by ID
     */
    @Transactional(readOnly = true)
    public LabTestCategory getCategoryById(UUID id) {
        return labTestCategoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    /**
     * Get category by code
     */
    @Transactional(readOnly = true)
    public LabTestCategory getCategoryByCode(String code) {
        return labTestCategoryRepository.findByCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with code: " + code));
    }

    /**
     * Get all active categories
     */
    @Transactional(readOnly = true)
    public List<LabTestCategory> getAllActiveCategories() {
        return labTestCategoryRepository.findByActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc();
    }

    /**
     * Get root categories (level 0)
     */
    @Transactional(readOnly = true)
    public List<LabTestCategory> getRootCategories() {
        return labTestCategoryRepository.findByLevelAndActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(0);
    }

    /**
     * Get child categories
     */
    @Transactional(readOnly = true)
    public List<LabTestCategory> getChildCategories(UUID parentId) {
        return labTestCategoryRepository.findByParentIdAndActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(parentId);
        // Note: The repository should use parent.id in query, e.g., "WHERE c.parent.id = :parentId"
    }

    /**
     * Search categories
     */
    @Transactional(readOnly = true)
    public Page<LabTestCategory> searchCategories(String search, Pageable pageable) {
        return labTestCategoryRepository.searchCategories(search, pageable);
    }

    /**
     * Get all categories (including inactive) with pagination
     */
    @Transactional(readOnly = true)
    public Page<LabTestCategory> getAllCategories(Pageable pageable) {
        return labTestCategoryRepository.findByDeletedAtIsNull(pageable);
    }

    /**
     * Activate category
     */
    public void activateCategory(UUID id) {
        log.info("Activating lab test category: {}", id);
        LabTestCategory category = getCategoryById(id);
        category.setActive(true);
        labTestCategoryRepository.save(category);
        log.info("Lab test category activated: {}", id);
    }

    /**
     * Deactivate category
     */
    public void deactivateCategory(UUID id) {
        log.info("Deactivating lab test category: {}", id);
        LabTestCategory category = getCategoryById(id);
        category.setActive(false);
        labTestCategoryRepository.save(category);
        log.info("Lab test category deactivated: {}", id);
    }

    /**
     * Reorder categories
     */
    public void reorderCategories(List<UUID> categoryIds) {
        log.info("Reordering {} categories", categoryIds.size());

        for (int i = 0; i < categoryIds.size(); i++) {
            UUID categoryId = categoryIds.get(i);
            LabTestCategory category = getCategoryById(categoryId);
            category.setDisplayOrder(i + 1);
            labTestCategoryRepository.save(category);
        }

        log.info("Categories reordered successfully");
    }
}
