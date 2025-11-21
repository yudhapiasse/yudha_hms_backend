package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.entity.RadiologyExamination;
import com.yudha.hms.radiology.entity.RadiologyModality;
import com.yudha.hms.radiology.repository.RadiologyExaminationRepository;
import com.yudha.hms.radiology.repository.RadiologyModalityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Radiology Examination operations.
 *
 * Handles CRUD operations and business logic for radiology examination catalog
 * (similar to laboratory test catalog).
 *
 * Features:
 * - CRUD operations with validation
 * - Get examinations by modality, body part, contrast requirement
 * - Get examinations by CPT code
 * - Search examinations with criteria
 * - Activate/deactivate examination
 * - Update pricing (base cost, contrast cost, BPJS tariff)
 * - Validation: unique exam code, valid modality
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RadiologyExaminationService {

    private final RadiologyExaminationRepository examinationRepository;
    private final RadiologyModalityRepository modalityRepository;

    /**
     * Create a new radiology examination.
     *
     * @param examination Examination to create
     * @return Created examination
     * @throws IllegalArgumentException if exam code exists or modality not found
     */
    public RadiologyExamination createExamination(RadiologyExamination examination) {
        log.info("Creating new radiology examination: {}", examination.getExamName());

        // Validate unique exam code
        if (examinationRepository.findByExamCodeAndDeletedAtIsNull(examination.getExamCode()).isPresent()) {
            throw new IllegalArgumentException("Examination code already exists: " + examination.getExamCode());
        }

        // Validate CPT code uniqueness if provided
        if (examination.getCptCode() != null && !examination.getCptCode().isEmpty()) {
            if (examinationRepository.findByCptCodeAndDeletedAtIsNull(examination.getCptCode()).isPresent()) {
                throw new IllegalArgumentException("CPT code already exists: " + examination.getCptCode());
            }
        }

        // Validate modality exists
        if (examination.getModality() == null || examination.getModality().getId() == null) {
            throw new IllegalArgumentException("Modality is required for examination");
        }
        modalityRepository.findByIdAndDeletedAtIsNull(examination.getModality().getId())
                .orElseThrow(() -> new IllegalArgumentException("Modality not found: " + examination.getModality().getId()));

        // Set defaults
        if (examination.getIsActive() == null) {
            examination.setIsActive(true);
        }
        if (examination.getRequiresContrast() == null) {
            examination.setRequiresContrast(false);
        }
        if (examination.getFastingRequired() == null) {
            examination.setFastingRequired(false);
        }
        if (examination.getLateralityApplicable() == null) {
            examination.setLateralityApplicable(false);
        }
        if (examination.getRequiresApproval() == null) {
            examination.setRequiresApproval(false);
        }
        if (examination.getContrastCost() == null) {
            examination.setContrastCost(BigDecimal.ZERO);
        }

        RadiologyExamination saved = examinationRepository.save(examination);
        log.info("Examination created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Update an existing radiology examination.
     *
     * @param id ID of examination to update
     * @param examinationUpdate Updated examination data
     * @return Updated examination
     * @throws IllegalArgumentException if examination not found or code exists
     */
    public RadiologyExamination updateExamination(UUID id, RadiologyExamination examinationUpdate) {
        log.info("Updating radiology examination: {}", id);

        RadiologyExamination existing = examinationRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found with ID: " + id));

        // Check exam code uniqueness if changed
        if (!existing.getExamCode().equals(examinationUpdate.getExamCode())) {
            if (examinationRepository.findByExamCodeAndDeletedAtIsNull(examinationUpdate.getExamCode()).isPresent()) {
                throw new IllegalArgumentException("Examination code already exists: " + examinationUpdate.getExamCode());
            }
        }

        // Check CPT code uniqueness if changed
        if (examinationUpdate.getCptCode() != null && !examinationUpdate.getCptCode().equals(existing.getCptCode())) {
            if (examinationRepository.findByCptCodeAndDeletedAtIsNull(examinationUpdate.getCptCode()).isPresent()) {
                throw new IllegalArgumentException("CPT code already exists: " + examinationUpdate.getCptCode());
            }
        }

        // Update fields
        existing.setExamCode(examinationUpdate.getExamCode());
        existing.setExamName(examinationUpdate.getExamName());
        existing.setShortName(examinationUpdate.getShortName());
        existing.setCptCode(examinationUpdate.getCptCode());
        existing.setIcdProcedureCode(examinationUpdate.getIcdProcedureCode());
        existing.setPreparationInstructions(examinationUpdate.getPreparationInstructions());
        existing.setFastingRequired(examinationUpdate.getFastingRequired());
        existing.setFastingDurationHours(examinationUpdate.getFastingDurationHours());
        existing.setRequiresContrast(examinationUpdate.getRequiresContrast());
        existing.setContrastType(examinationUpdate.getContrastType());
        existing.setContrastVolumeMl(examinationUpdate.getContrastVolumeMl());
        existing.setExamDurationMinutes(examinationUpdate.getExamDurationMinutes());
        existing.setReportingTimeMinutes(examinationUpdate.getReportingTimeMinutes());
        existing.setBaseCost(examinationUpdate.getBaseCost());
        existing.setContrastCost(examinationUpdate.getContrastCost());
        existing.setBpjsTariff(examinationUpdate.getBpjsTariff());
        existing.setBodyPart(examinationUpdate.getBodyPart());
        existing.setLateralityApplicable(examinationUpdate.getLateralityApplicable());
        existing.setPositioningNotes(examinationUpdate.getPositioningNotes());
        existing.setClinicalIndication(examinationUpdate.getClinicalIndication());
        existing.setInterpretationGuide(examinationUpdate.getInterpretationGuide());
        existing.setIsActive(examinationUpdate.getIsActive());
        existing.setRequiresApproval(examinationUpdate.getRequiresApproval());

        // Update modality if changed
        if (examinationUpdate.getModality() != null &&
                !existing.getModality().getId().equals(examinationUpdate.getModality().getId())) {
            RadiologyModality newModality = modalityRepository.findByIdAndDeletedAtIsNull(examinationUpdate.getModality().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Modality not found: " + examinationUpdate.getModality().getId()));
            existing.setModality(newModality);
        }

        RadiologyExamination updated = examinationRepository.save(existing);
        log.info("Examination updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) a radiology examination.
     *
     * @param id ID of examination to delete
     * @param deletedBy User ID who performed deletion
     * @throws IllegalArgumentException if examination not found
     */
    public void deleteExamination(UUID id, String deletedBy) {
        log.info("Deleting radiology examination: {}", id);

        RadiologyExamination examination = examinationRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found with ID: " + id));

        // Soft delete
        examination.setDeletedAt(LocalDateTime.now());
        examination.setDeletedBy(deletedBy);
        examinationRepository.save(examination);

        log.info("Examination deleted successfully: {}", id);
    }

    /**
     * Get examination by ID.
     *
     * @param id Examination ID
     * @return Examination
     * @throws IllegalArgumentException if examination not found
     */
    @Transactional(readOnly = true)
    public RadiologyExamination getExaminationById(UUID id) {
        return examinationRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found with ID: " + id));
    }

    /**
     * Get examination by code.
     *
     * @param examCode Examination code
     * @return Examination
     * @throws IllegalArgumentException if examination not found
     */
    @Transactional(readOnly = true)
    public RadiologyExamination getExaminationByCode(String examCode) {
        return examinationRepository.findByExamCodeAndDeletedAtIsNull(examCode)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found with code: " + examCode));
    }

    /**
     * Get examination by CPT code.
     *
     * @param cptCode CPT code
     * @return Examination
     * @throws IllegalArgumentException if examination not found
     */
    @Transactional(readOnly = true)
    public RadiologyExamination getExaminationByCptCode(String cptCode) {
        return examinationRepository.findByCptCodeAndDeletedAtIsNull(cptCode)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found with CPT code: " + cptCode));
    }

    /**
     * Get all active examinations.
     *
     * @return List of active examinations
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> getAllActiveExaminations() {
        return examinationRepository.findByIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get all examinations (paginated).
     *
     * @param isActive Filter by active status (null for all)
     * @param pageable Pagination parameters
     * @return Page of examinations
     */
    @Transactional(readOnly = true)
    public Page<RadiologyExamination> getAllExaminations(Boolean isActive, Pageable pageable) {
        if (isActive != null) {
            return examinationRepository.findByIsActiveAndDeletedAtIsNull(isActive, pageable);
        }
        return examinationRepository.findAll(pageable);
    }

    /**
     * Get examinations by modality.
     *
     * @param modalityId Modality ID
     * @return List of examinations
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> getExaminationsByModality(UUID modalityId) {
        return examinationRepository.findByModalityIdAndIsActiveTrueAndDeletedAtIsNull(modalityId);
    }

    /**
     * Get examinations by modality (paginated).
     *
     * @param modalityId Modality ID
     * @param isActive Filter by active status
     * @param pageable Pagination parameters
     * @return Page of examinations
     */
    @Transactional(readOnly = true)
    public Page<RadiologyExamination> getExaminationsByModality(UUID modalityId, Boolean isActive, Pageable pageable) {
        return examinationRepository.findByModalityIdAndIsActiveAndDeletedAtIsNull(modalityId, isActive, pageable);
    }

    /**
     * Get examinations by body part.
     *
     * @param bodyPart Body part
     * @return List of examinations
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> getExaminationsByBodyPart(String bodyPart) {
        return examinationRepository.findByBodyPartAndIsActiveTrueAndDeletedAtIsNull(bodyPart);
    }

    /**
     * Search examinations by body part (partial match).
     *
     * @param bodyPart Body part search term
     * @return List of examinations
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> searchExaminationsByBodyPart(String bodyPart) {
        return examinationRepository.searchByBodyPart(bodyPart);
    }

    /**
     * Get examinations requiring contrast.
     *
     * @return List of examinations requiring contrast
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> getExaminationsRequiringContrast() {
        return examinationRepository.findByRequiresContrastTrueAndIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get examinations requiring fasting.
     *
     * @return List of examinations requiring fasting
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> getExaminationsRequiringFasting() {
        return examinationRepository.findByFastingRequiredTrueAndIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get examinations requiring approval.
     *
     * @return List of examinations requiring approval
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> getExaminationsRequiringApproval() {
        return examinationRepository.findByRequiresApprovalTrueAndIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get examinations with laterality applicable.
     *
     * @return List of examinations with laterality
     */
    @Transactional(readOnly = true)
    public List<RadiologyExamination> getExaminationsWithLaterality() {
        return examinationRepository.findByLateralityApplicableTrueAndIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Search examinations by name or code.
     *
     * @param search Search term
     * @param pageable Pagination parameters
     * @return Page of matching examinations
     */
    @Transactional(readOnly = true)
    public Page<RadiologyExamination> searchExaminations(String search, Pageable pageable) {
        return examinationRepository.searchExaminations(search, pageable);
    }

    /**
     * Activate an examination.
     *
     * @param id Examination ID
     * @return Updated examination
     * @throws IllegalArgumentException if examination not found
     */
    public RadiologyExamination activateExamination(UUID id) {
        log.info("Activating examination: {}", id);

        RadiologyExamination examination = getExaminationById(id);
        examination.setIsActive(true);

        RadiologyExamination updated = examinationRepository.save(examination);
        log.info("Examination activated successfully: {}", id);
        return updated;
    }

    /**
     * Deactivate an examination.
     *
     * @param id Examination ID
     * @return Updated examination
     * @throws IllegalArgumentException if examination not found
     */
    public RadiologyExamination deactivateExamination(UUID id) {
        log.info("Deactivating examination: {}", id);

        RadiologyExamination examination = getExaminationById(id);
        examination.setIsActive(false);

        RadiologyExamination updated = examinationRepository.save(examination);
        log.info("Examination deactivated successfully: {}", id);
        return updated;
    }

    /**
     * Update examination pricing.
     *
     * @param id Examination ID
     * @param baseCost Base cost
     * @param contrastCost Contrast cost
     * @param bpjsTariff BPJS tariff
     * @return Updated examination
     * @throws IllegalArgumentException if examination not found
     */
    public RadiologyExamination updateExaminationPricing(UUID id, BigDecimal baseCost,
                                                          BigDecimal contrastCost, BigDecimal bpjsTariff) {
        log.info("Updating examination pricing for: {}", id);

        RadiologyExamination examination = getExaminationById(id);
        examination.setBaseCost(baseCost);
        examination.setContrastCost(contrastCost);
        examination.setBpjsTariff(bpjsTariff);

        RadiologyExamination updated = examinationRepository.save(examination);
        log.info("Examination pricing updated successfully: {}", id);
        return updated;
    }

    /**
     * Count active examinations.
     *
     * @return Count of active examinations
     */
    @Transactional(readOnly = true)
    public long countActiveExaminations() {
        return examinationRepository.countByIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Count examinations by modality.
     *
     * @param modalityId Modality ID
     * @return Count of examinations
     */
    @Transactional(readOnly = true)
    public long countExaminationsByModality(UUID modalityId) {
        return examinationRepository.countByModalityIdAndIsActiveTrueAndDeletedAtIsNull(modalityId);
    }

    /**
     * Check if an examination code exists.
     *
     * @param examCode Examination code
     * @return True if code exists
     */
    @Transactional(readOnly = true)
    public boolean examinationCodeExists(String examCode) {
        return examinationRepository.findByExamCodeAndDeletedAtIsNull(examCode).isPresent();
    }

    /**
     * Check if a CPT code exists.
     *
     * @param cptCode CPT code
     * @return True if code exists
     */
    @Transactional(readOnly = true)
    public boolean cptCodeExists(String cptCode) {
        return examinationRepository.findByCptCodeAndDeletedAtIsNull(cptCode).isPresent();
    }
}
