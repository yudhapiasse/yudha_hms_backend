package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.ICD10Code;
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
 * Repository for ICD10Code entity.
 * Provides search, autocomplete, and master data queries.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface ICD10CodeRepository extends JpaRepository<ICD10Code, UUID> {

    /**
     * Find ICD-10 code by code value.
     */
    Optional<ICD10Code> findByCode(String code);

    /**
     * Find active ICD-10 codes.
     */
    List<ICD10Code> findByIsActiveTrueOrderByCodeAsc();

    /**
     * Find common (frequently used) codes.
     */
    List<ICD10Code> findByIsCommonTrueAndIsActiveTrueOrderByUsageCountDesc();

    /**
     * Find codes by chapter.
     */
    List<ICD10Code> findByChapterCodeAndIsActiveTrueOrderByCodeAsc(String chapterCode);

    /**
     * Find codes by category.
     */
    List<ICD10Code> findByCategoryCodeAndIsActiveTrueOrderByCodeAsc(String categoryCode);

    /**
     * Search for diagnosis codes (autocomplete).
     * Searches in code, English description, and Indonesian description.
     */
    @Query("""
        SELECT i FROM ICD10Code i
        WHERE i.isActive = true
        AND (
            LOWER(i.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(i.descriptionEn) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(i.descriptionId) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(i.searchTerms) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
        ORDER BY
            CASE WHEN LOWER(i.code) = LOWER(:searchTerm) THEN 1
                 WHEN LOWER(i.code) LIKE LOWER(CONCAT(:searchTerm, '%')) THEN 2
                 WHEN i.isCommon = true THEN 3
                 ELSE 4
            END,
            i.usageCount DESC,
            i.code ASC
        """)
    Page<ICD10Code> searchDiagnoses(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Advanced search with multiple criteria.
     */
    @Query("""
        SELECT i FROM ICD10Code i
        WHERE i.isActive = true
        AND (:code IS NULL OR LOWER(i.code) LIKE LOWER(CONCAT('%', :code, '%')))
        AND (:description IS NULL OR (
            LOWER(i.descriptionEn) LIKE LOWER(CONCAT('%', :description, '%'))
            OR LOWER(i.descriptionId) LIKE LOWER(CONCAT('%', :description, '%'))
        ))
        AND (:chapterCode IS NULL OR i.chapterCode = :chapterCode)
        AND (:categoryCode IS NULL OR i.categoryCode = :categoryCode)
        AND (:isBillable IS NULL OR i.isBillable = :isBillable)
        ORDER BY i.usageCount DESC, i.code ASC
        """)
    Page<ICD10Code> advancedSearch(
        @Param("code") String code,
        @Param("description") String description,
        @Param("chapterCode") String chapterCode,
        @Param("categoryCode") String categoryCode,
        @Param("isBillable") Boolean isBillable,
        Pageable pageable
    );

    /**
     * Find most used codes for analytics.
     */
    @Query("""
        SELECT i FROM ICD10Code i
        WHERE i.isActive = true
        ORDER BY i.usageCount DESC
        """)
    Page<ICD10Code> findMostUsedCodes(Pageable pageable);

    /**
     * Find codes that require additional information for insurance.
     */
    List<ICD10Code> findByRequiresAdditionalInfoTrueAndIsActiveTrue();

    /**
     * Find billable codes.
     */
    @Query("""
        SELECT i FROM ICD10Code i
        WHERE i.isActive = true
        AND i.isBillable = true
        ORDER BY i.code ASC
        """)
    List<ICD10Code> findBillableCodes();

    /**
     * Check if code exists and is active.
     */
    boolean existsByCodeAndIsActiveTrue(String code);

    /**
     * Count active codes by chapter.
     */
    @Query("""
        SELECT i.chapterCode, COUNT(i)
        FROM ICD10Code i
        WHERE i.isActive = true
        GROUP BY i.chapterCode
        ORDER BY i.chapterCode
        """)
    List<Object[]> countByChapter();

    /**
     * Get deprecated codes that need replacement.
     */
    @Query("""
        SELECT i FROM ICD10Code i
        WHERE i.isActive = false
        AND i.deprecatedDate IS NOT NULL
        AND i.deprecatedDate <= CURRENT_DATE
        ORDER BY i.deprecatedDate DESC
        """)
    List<ICD10Code> findDeprecatedCodes();

    /**
     * Autocomplete suggestion - returns top 10 matches.
     */
    @Query("""
        SELECT i FROM ICD10Code i
        WHERE i.isActive = true
        AND (
            LOWER(i.code) LIKE LOWER(CONCAT(:prefix, '%'))
            OR LOWER(i.descriptionId) LIKE LOWER(CONCAT('%', :prefix, '%'))
        )
        ORDER BY
            CASE WHEN LOWER(i.code) LIKE LOWER(CONCAT(:prefix, '%')) THEN 1 ELSE 2 END,
            i.isCommon DESC,
            i.usageCount DESC,
            i.code ASC
        LIMIT 10
        """)
    List<ICD10Code> findAutocompleteSuggestions(@Param("prefix") String prefix);
}
