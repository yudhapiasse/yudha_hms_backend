package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.LabResultParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for LabResultParameter entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabResultParameterRepository extends JpaRepository<LabResultParameter, UUID> {

    /**
     * Find parameters by result
     */
    List<LabResultParameter> findByResultId(UUID resultId);

    /**
     * Find abnormal parameters by result
     */
    @Query("SELECT p FROM LabResultParameter p WHERE p.result.id = :resultId AND p.isAbnormal = true")
    List<LabResultParameter> findAbnormalParameters(@Param("resultId") UUID resultId);

    /**
     * Find critical parameters by result
     */
    @Query("SELECT p FROM LabResultParameter p WHERE p.result.id = :resultId AND p.isCritical = true")
    List<LabResultParameter> findCriticalParameters(@Param("resultId") UUID resultId);

    /**
     * Find parameters with delta check flags by result
     */
    @Query("SELECT p FROM LabResultParameter p WHERE p.result.id = :resultId AND p.deltaCheckFlagged = true")
    List<LabResultParameter> findParametersWithDeltaCheckFlags(@Param("resultId") UUID resultId);

    /**
     * Count abnormal parameters by result
     */
    long countByResultIdAndIsAbnormal(UUID resultId, Boolean isAbnormal);

    /**
     * Count critical parameters by result
     */
    long countByResultIdAndIsCritical(UUID resultId, Boolean isCritical);
}
