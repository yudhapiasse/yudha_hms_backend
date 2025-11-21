package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.entity.RadiologyExamination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RadiologyExamination entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface RadiologyExaminationRepository extends JpaRepository<RadiologyExamination, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<RadiologyExamination> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by exam code
     */
    Optional<RadiologyExamination> findByExamCodeAndDeletedAtIsNull(String examCode);

    /**
     * Find by CPT code
     */
    Optional<RadiologyExamination> findByCptCodeAndDeletedAtIsNull(String cptCode);

    /**
     * Find all active examinations (paginated)
     */
    Page<RadiologyExamination> findByIsActiveAndDeletedAtIsNull(Boolean isActive, Pageable pageable);

    /**
     * Find all active examinations (list)
     */
    List<RadiologyExamination> findByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find by modality (paginated)
     */
    Page<RadiologyExamination> findByModalityIdAndIsActiveAndDeletedAtIsNull(UUID modalityId, Boolean isActive, Pageable pageable);

    /**
     * Find by modality (list)
     */
    List<RadiologyExamination> findByModalityIdAndIsActiveTrueAndDeletedAtIsNull(UUID modalityId);

    /**
     * Find by body part
     */
    List<RadiologyExamination> findByBodyPartAndIsActiveTrueAndDeletedAtIsNull(String bodyPart);

    /**
     * Find examinations requiring contrast
     */
    List<RadiologyExamination> findByRequiresContrastTrueAndIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find examinations requiring fasting
     */
    List<RadiologyExamination> findByFastingRequiredTrueAndIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find examinations requiring approval
     */
    List<RadiologyExamination> findByRequiresApprovalTrueAndIsActiveTrueAndDeletedAtIsNull();

    /**
     * Find examinations with laterality applicable
     */
    List<RadiologyExamination> findByLateralityApplicableTrueAndIsActiveTrueAndDeletedAtIsNull();

    /**
     * Search examinations by name or code
     */
    @Query("SELECT e FROM RadiologyExamination e WHERE (LOWER(e.examName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.examCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND e.isActive = true AND e.deletedAt IS NULL")
    Page<RadiologyExamination> searchExaminations(@Param("search") String search, Pageable pageable);

    /**
     * Search examinations by body part
     */
    @Query("SELECT e FROM RadiologyExamination e WHERE LOWER(e.bodyPart) LIKE LOWER(CONCAT('%', :bodyPart, '%')) AND e.isActive = true AND e.deletedAt IS NULL")
    List<RadiologyExamination> searchByBodyPart(@Param("bodyPart") String bodyPart);

    /**
     * Count active examinations
     */
    long countByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Count examinations by modality
     */
    long countByModalityIdAndIsActiveTrueAndDeletedAtIsNull(UUID modalityId);
}
