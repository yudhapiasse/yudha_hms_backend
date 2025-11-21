package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.LabTestParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LabTestParameter entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabTestParameterRepository extends JpaRepository<LabTestParameter, UUID> {

    /**
     * Find parameters by test
     */
    List<LabTestParameter> findByLabTestIdAndActiveAndDeletedAtIsNullOrderByDisplayOrder(UUID labTestId, Boolean active);

    /**
     * Find by parameter code and test
     */
    Optional<LabTestParameter> findByLabTestIdAndParameterCodeAndDeletedAtIsNull(UUID labTestId, String parameterCode);

    /**
     * Find parameters with critical values
     */
    @Query("SELECT p FROM LabTestParameter p WHERE p.labTest.id = :testId AND (p.criticalLow IS NOT NULL OR p.criticalHigh IS NOT NULL) AND p.active = true AND p.deletedAt IS NULL")
    List<LabTestParameter> findParametersWithCriticalValues(@Param("testId") UUID testId);

    /**
     * Find parameters with delta check enabled
     */
    @Query("SELECT p FROM LabTestParameter p WHERE p.labTest.id = :testId AND p.deltaCheckEnabled = true AND p.active = true AND p.deletedAt IS NULL")
    List<LabTestParameter> findParametersWithDeltaCheck(@Param("testId") UUID testId);

    /**
     * Find by ID and not deleted
     */
    Optional<LabTestParameter> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find active parameters by test
     */
    List<LabTestParameter> findByLabTestIdAndActiveTrueAndDeletedAtIsNullOrderByDisplayOrderAsc(UUID labTestId);

    /**
     * Find mandatory parameters by test
     */
    List<LabTestParameter> findByLabTestIdAndIsMandatoryTrueAndActiveTrueAndDeletedAtIsNull(UUID labTestId);

    /**
     * Find delta check enabled parameters by test
     */
    List<LabTestParameter> findByLabTestIdAndDeltaCheckEnabledTrueAndActiveTrueAndDeletedAtIsNull(UUID labTestId);

    /**
     * Find calculated parameters by test
     */
    List<LabTestParameter> findByLabTestIdAndIsCalculatedTrueAndActiveTrueAndDeletedAtIsNull(UUID labTestId);
}
