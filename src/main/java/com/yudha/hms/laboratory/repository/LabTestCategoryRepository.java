package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.LabTestCategory;
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
 * Repository for LabTestCategory entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabTestCategoryRepository extends JpaRepository<LabTestCategory, UUID> {

    /**
     * Find by code
     */
    Optional<LabTestCategory> findByCodeAndDeletedAtIsNull(String code);

    /**
     * Find all active categories
     */
    List<LabTestCategory> findByActiveAndDeletedAtIsNullOrderByDisplayOrder(Boolean active);

    /**
     * Find root categories (level = 0)
     */
    @Query("SELECT c FROM LabTestCategory c WHERE c.level = 0 AND c.active = true AND c.deletedAt IS NULL ORDER BY c.displayOrder")
    List<LabTestCategory> findRootCategories();

    /**
     * Find child categories by parent
     */
    List<LabTestCategory> findByParentIdAndActiveAndDeletedAtIsNullOrderByDisplayOrder(UUID parentId, Boolean active);

    /**
     * Search by name
     */
    @Query("SELECT c FROM LabTestCategory c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) AND c.active = true AND c.deletedAt IS NULL")
    List<LabTestCategory> searchByName(@Param("search") String search);

    /**
     * Find by ID and not deleted
     */
    Optional<LabTestCategory> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find active categories by true
     */
    List<LabTestCategory> findByActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc();

    /**
     * Find by level
     */
    List<LabTestCategory> findByLevelAndActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(Integer level);

    /**
     * Find by parent ID
     */
    List<LabTestCategory> findByParentIdAndActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(UUID parentId);

    /**
     * Search categories with pagination
     */
    @Query("SELECT c FROM LabTestCategory c WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%'))) AND c.deletedAt IS NULL")
    Page<LabTestCategory> searchCategories(@Param("search") String search, Pageable pageable);

    /**
     * Find all not deleted with pagination
     */
    Page<LabTestCategory> findByDeletedAtIsNull(Pageable pageable);

    /**
     * Count active tests by category
     */
    @Query("SELECT COUNT(t) FROM LabTest t WHERE t.category.id = :categoryId AND t.active = true AND t.deletedAt IS NULL")
    long countActiveTestsByCategory(@Param("categoryId") UUID categoryId);
}
