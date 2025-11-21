package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.constant.SampleType;
import com.yudha.hms.laboratory.entity.LabTest;
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
 * Repository for LabTest entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabTestRepository extends JpaRepository<LabTest, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<LabTest> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by test code
     */
    Optional<LabTest> findByTestCodeAndDeletedAtIsNull(String testCode);

    /**
     * Find by LOINC code
     */
    Optional<LabTest> findByLoincCodeAndDeletedAtIsNull(String loincCode);

    /**
     * Find all active tests (paginated)
     */
    Page<LabTest> findByActiveAndDeletedAtIsNull(Boolean active, Pageable pageable);

    /**
     * Find all active tests (list)
     */
    List<LabTest> findByActiveTrueAndDeletedAtIsNull();

    /**
     * Find by category (paginated)
     */
    Page<LabTest> findByCategoryIdAndActiveAndDeletedAtIsNull(UUID categoryId, Boolean active, Pageable pageable);

    /**
     * Find by category (list)
     */
    List<LabTest> findByCategoryIdAndActiveTrueAndDeletedAtIsNull(UUID categoryId);

    /**
     * Find by sample type
     */
    List<LabTest> findBySampleTypeAndActiveAndDeletedAtIsNull(SampleType sampleType, Boolean active);

    /**
     * Find by sample type (string parameter)
     */
    List<LabTest> findBySampleTypeAndActiveTrueAndDeletedAtIsNull(String sampleType);

    /**
     * Search tests by name or code
     */
    @Query("SELECT t FROM LabTest t WHERE (LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.testCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND t.active = true AND t.deletedAt IS NULL")
    Page<LabTest> searchTests(@Param("search") String search, Pageable pageable);

    /**
     * Find tests with critical values
     */
    @Query("SELECT t FROM LabTest t WHERE t.hasCriticalValues = true AND t.active = true AND t.deletedAt IS NULL")
    List<LabTest> findTestsWithCriticalValues();

    /**
     * Find tests requiring pathologist review
     */
    @Query("SELECT t FROM LabTest t WHERE t.requiresPathologistReview = true AND t.active = true AND t.deletedAt IS NULL")
    List<LabTest> findTestsRequiringPathologistReview();

    /**
     * Find tests requiring approval
     */
    List<LabTest> findByRequiresApprovalTrueAndActiveTrueAndDeletedAtIsNull();

    /**
     * Count active tests
     */
    long countByActiveTrueAndDeletedAtIsNull();

    /**
     * Count tests by category
     */
    long countByCategoryIdAndActiveTrueAndDeletedAtIsNull(UUID categoryId);
}
