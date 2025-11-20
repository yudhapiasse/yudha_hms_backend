package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.CommonDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for CommonDiagnosis entity.
 * Provides quick access to department-specific common diagnoses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface CommonDiagnosisRepository extends JpaRepository<CommonDiagnosis, UUID> {

    /**
     * Get top diagnoses for a department (top 10 by default).
     */
    @Query("""
        SELECT cd FROM CommonDiagnosis cd
        WHERE cd.departmentCode = :departmentCode
        AND cd.isActive = true
        ORDER BY cd.rankOrder ASC
        """)
    List<CommonDiagnosis> findTopDiagnosesByDepartment(@Param("departmentCode") String departmentCode);

    /**
     * Get top N diagnoses for a department.
     */
    @Query("""
        SELECT cd FROM CommonDiagnosis cd
        WHERE cd.departmentCode = :departmentCode
        AND cd.isActive = true
        ORDER BY cd.rankOrder ASC
        LIMIT :limit
        """)
    List<CommonDiagnosis> findTopNDiagnosesByDepartment(
        @Param("departmentCode") String departmentCode,
        @Param("limit") int limit
    );

    /**
     * Find by department and ICD-10 code.
     */
    Optional<CommonDiagnosis> findByDepartmentCodeAndIcd10Code_Code(String departmentCode, String icd10Code);

    /**
     * Find all active common diagnoses for a department.
     */
    List<CommonDiagnosis> findByDepartmentCodeAndIsActiveTrueOrderByRankOrderAsc(String departmentCode);

    /**
     * Find pinned diagnoses for a department.
     */
    List<CommonDiagnosis> findByDepartmentCodeAndIsPinnedTrueAndIsActiveTrueOrderByRankOrderAsc(
        String departmentCode
    );

    /**
     * Find auto-calculated diagnoses (not pinned).
     */
    List<CommonDiagnosis> findByDepartmentCodeAndAutoCalculatedTrueAndIsActiveTrueOrderByUsageCountDesc(
        String departmentCode
    );

    /**
     * Get usage statistics for a department.
     */
    @Query("""
        SELECT cd.departmentCode,
               COUNT(cd),
               SUM(cd.usageCount),
               AVG(cd.usageCount)
        FROM CommonDiagnosis cd
        WHERE cd.isActive = true
        AND (:departmentCode IS NULL OR cd.departmentCode = :departmentCode)
        GROUP BY cd.departmentCode
        ORDER BY cd.departmentCode
        """)
    List<Object[]> getUsageStatisticsByDepartment(@Param("departmentCode") String departmentCode);

    /**
     * Find diagnoses with trending patterns.
     */
    @Query("""
        SELECT cd FROM CommonDiagnosis cd
        WHERE cd.departmentCode = :departmentCode
        AND cd.isActive = true
        AND cd.trend = :trend
        ORDER BY cd.rankOrder ASC
        """)
    List<CommonDiagnosis> findByDepartmentAndTrend(
        @Param("departmentCode") String departmentCode,
        @Param("trend") String trend
    );

    /**
     * Check if diagnosis exists in department.
     */
    boolean existsByDepartmentCodeAndIcd10Code_Code(String departmentCode, String icd10Code);

    /**
     * Count common diagnoses per department.
     */
    @Query("""
        SELECT cd.departmentCode, cd.departmentName, COUNT(cd)
        FROM CommonDiagnosis cd
        WHERE cd.isActive = true
        GROUP BY cd.departmentCode, cd.departmentName
        ORDER BY COUNT(cd) DESC
        """)
    List<Object[]> countByDepartment();

    /**
     * Find diagnoses that need recalculation (older than cutoff date).
     */
    @Query("""
        SELECT cd FROM CommonDiagnosis cd
        WHERE cd.autoCalculated = true
        AND cd.isActive = true
        AND (cd.lastRecalculatedDate IS NULL
             OR cd.lastRecalculatedDate < :cutoffDate)
        ORDER BY cd.departmentCode, cd.rankOrder
        """)
    List<CommonDiagnosis> findDiagnosesNeedingRecalculation(@Param("cutoffDate") java.time.LocalDate cutoffDate);

    /**
     * Get all distinct department codes with common diagnoses.
     */
    @Query("""
        SELECT DISTINCT cd.departmentCode
        FROM CommonDiagnosis cd
        WHERE cd.isActive = true
        ORDER BY cd.departmentCode
        """)
    List<String> findDistinctDepartmentCodes();

    /**
     * Delete inactive diagnoses older than specified date.
     */
    @Query("""
        DELETE FROM CommonDiagnosis cd
        WHERE cd.isActive = false
        AND cd.lastUsedDate < :cutoffDate
        """)
    void deleteInactiveDiagnosesOlderThan(@Param("cutoffDate") java.time.LocalDate cutoffDate);
}
