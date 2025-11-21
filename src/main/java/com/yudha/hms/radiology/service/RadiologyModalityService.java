package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.entity.RadiologyModality;
import com.yudha.hms.radiology.repository.RadiologyModalityRepository;
import com.yudha.hms.radiology.repository.RadiologyExaminationRepository;
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
 * Service for Radiology Modality operations.
 *
 * Handles CRUD operations and business logic for imaging modalities
 * (X-Ray, CT, MRI, USG, Mammography, etc.)
 *
 * Features:
 * - CRUD operations with validation
 * - Activate/deactivate modality
 * - Get modalities by radiation requirement
 * - Validation before deletion (check active examinations)
 * - Soft delete support
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RadiologyModalityService {

    private final RadiologyModalityRepository modalityRepository;
    private final RadiologyExaminationRepository examinationRepository;

    /**
     * Create a new radiology modality.
     *
     * @param modality Modality to create
     * @return Created modality
     * @throws IllegalArgumentException if code already exists
     */
    public RadiologyModality createModality(RadiologyModality modality) {
        log.info("Creating new radiology modality: {}", modality.getName());

        // Validate unique code
        if (modalityRepository.findByCodeAndDeletedAtIsNull(modality.getCode()).isPresent()) {
            throw new IllegalArgumentException("Modality code already exists: " + modality.getCode());
        }

        // Set defaults if not provided
        if (modality.getIsActive() == null) {
            modality.setIsActive(true);
        }
        if (modality.getRequiresRadiation() == null) {
            modality.setRequiresRadiation(false);
        }

        RadiologyModality saved = modalityRepository.save(modality);
        log.info("Modality created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Update an existing radiology modality.
     *
     * @param id ID of modality to update
     * @param modalityUpdate Updated modality data
     * @return Updated modality
     * @throws IllegalArgumentException if modality not found or code exists
     */
    public RadiologyModality updateModality(UUID id, RadiologyModality modalityUpdate) {
        log.info("Updating radiology modality: {}", id);

        RadiologyModality existing = modalityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Modality not found with ID: " + id));

        // Check code uniqueness if changed
        if (!existing.getCode().equals(modalityUpdate.getCode())) {
            if (modalityRepository.findByCodeAndDeletedAtIsNull(modalityUpdate.getCode()).isPresent()) {
                throw new IllegalArgumentException("Modality code already exists: " + modalityUpdate.getCode());
            }
        }

        // Update fields
        existing.setCode(modalityUpdate.getCode());
        existing.setName(modalityUpdate.getName());
        existing.setDescription(modalityUpdate.getDescription());
        existing.setRequiresRadiation(modalityUpdate.getRequiresRadiation());
        existing.setAverageDurationMinutes(modalityUpdate.getAverageDurationMinutes());
        existing.setIsActive(modalityUpdate.getIsActive());
        existing.setDisplayOrder(modalityUpdate.getDisplayOrder());
        existing.setIcon(modalityUpdate.getIcon());
        existing.setColor(modalityUpdate.getColor());

        RadiologyModality updated = modalityRepository.save(existing);
        log.info("Modality updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) a radiology modality.
     * Validates that no active examinations use this modality.
     *
     * @param id ID of modality to delete
     * @param deletedBy User ID who performed deletion
     * @throws IllegalArgumentException if modality not found
     * @throws IllegalStateException if modality has active examinations
     */
    public void deleteModality(UUID id, String deletedBy) {
        log.info("Deleting radiology modality: {}", id);

        RadiologyModality modality = modalityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Modality not found with ID: " + id));

        // Check if modality has active examinations
        long examinationCount = examinationRepository.countByModalityIdAndIsActiveTrueAndDeletedAtIsNull(id);
        if (examinationCount > 0) {
            throw new IllegalStateException("Cannot delete modality with " + examinationCount +
                    " active examinations. Deactivate examinations first.");
        }

        // Soft delete
        modality.setDeletedAt(LocalDateTime.now());
        modality.setDeletedBy(deletedBy);
        modalityRepository.save(modality);

        log.info("Modality deleted successfully: {}", id);
    }

    /**
     * Get modality by ID.
     *
     * @param id Modality ID
     * @return Modality
     * @throws IllegalArgumentException if modality not found
     */
    @Transactional(readOnly = true)
    public RadiologyModality getModalityById(UUID id) {
        return modalityRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Modality not found with ID: " + id));
    }

    /**
     * Get modality by code.
     *
     * @param code Modality code
     * @return Modality
     * @throws IllegalArgumentException if modality not found
     */
    @Transactional(readOnly = true)
    public RadiologyModality getModalityByCode(String code) {
        return modalityRepository.findByCodeAndDeletedAtIsNull(code)
                .orElseThrow(() -> new IllegalArgumentException("Modality not found with code: " + code));
    }

    /**
     * Get all active modalities.
     *
     * @return List of active modalities
     */
    @Transactional(readOnly = true)
    public List<RadiologyModality> getAllActiveModalities() {
        return modalityRepository.findByIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get all modalities (paginated).
     *
     * @param isActive Filter by active status (null for all)
     * @param pageable Pagination parameters
     * @return Page of modalities
     */
    @Transactional(readOnly = true)
    public Page<RadiologyModality> getAllModalities(Boolean isActive, Pageable pageable) {
        if (isActive != null) {
            return modalityRepository.findByIsActiveAndDeletedAtIsNull(isActive, pageable);
        }
        return modalityRepository.findAll(pageable);
    }

    /**
     * Get modalities that require radiation exposure.
     *
     * @return List of modalities requiring radiation
     */
    @Transactional(readOnly = true)
    public List<RadiologyModality> getModalitiesRequiringRadiation() {
        return modalityRepository.findByRequiresRadiationTrueAndIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get modalities that do not require radiation exposure.
     *
     * @return List of modalities not requiring radiation
     */
    @Transactional(readOnly = true)
    public List<RadiologyModality> getModalitiesNotRequiringRadiation() {
        return modalityRepository.findByRequiresRadiationFalseAndIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Search modalities by name or code.
     *
     * @param search Search term
     * @param pageable Pagination parameters
     * @return Page of matching modalities
     */
    @Transactional(readOnly = true)
    public Page<RadiologyModality> searchModalities(String search, Pageable pageable) {
        return modalityRepository.searchModalities(search, pageable);
    }

    /**
     * Activate a modality.
     *
     * @param id Modality ID
     * @return Updated modality
     * @throws IllegalArgumentException if modality not found
     */
    public RadiologyModality activateModality(UUID id) {
        log.info("Activating modality: {}", id);

        RadiologyModality modality = getModalityById(id);
        modality.setIsActive(true);

        RadiologyModality updated = modalityRepository.save(modality);
        log.info("Modality activated successfully: {}", id);
        return updated;
    }

    /**
     * Deactivate a modality.
     *
     * @param id Modality ID
     * @return Updated modality
     * @throws IllegalArgumentException if modality not found
     */
    public RadiologyModality deactivateModality(UUID id) {
        log.info("Deactivating modality: {}", id);

        RadiologyModality modality = getModalityById(id);
        modality.setIsActive(false);

        RadiologyModality updated = modalityRepository.save(modality);
        log.info("Modality deactivated successfully: {}", id);
        return updated;
    }

    /**
     * Count active modalities.
     *
     * @return Count of active modalities
     */
    @Transactional(readOnly = true)
    public long countActiveModalities() {
        return modalityRepository.countByIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Check if a modality code exists.
     *
     * @param code Modality code
     * @return True if code exists
     */
    @Transactional(readOnly = true)
    public boolean modalityCodeExists(String code) {
        return modalityRepository.findByCodeAndDeletedAtIsNull(code).isPresent();
    }
}
