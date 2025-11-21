package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.constant.ValidationLevel;
import com.yudha.hms.laboratory.constant.ValidationStatus;
import com.yudha.hms.laboratory.entity.ResultValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for ResultValidation entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface ResultValidationRepository extends JpaRepository<ResultValidation, UUID> {

    /**
     * Find validations by result
     */
    List<ResultValidation> findByResultIdOrderByValidationStepAsc(UUID resultId);

    /**
     * Find validations by result and level
     */
    List<ResultValidation> findByResultIdAndValidationLevelOrderByValidatedAtDesc(UUID resultId, ValidationLevel validationLevel);

    /**
     * Find latest validation for result
     */
    @Query("SELECT v FROM ResultValidation v WHERE v.result.id = :resultId ORDER BY v.validationStep DESC, v.validatedAt DESC LIMIT 1")
    ResultValidation findLatestValidationByResultId(@Param("resultId") UUID resultId);

    /**
     * Count validations by result and status
     */
    long countByResultIdAndValidationStatus(UUID resultId, ValidationStatus validationStatus);

    /**
     * Find validations by validator
     */
    List<ResultValidation> findByValidatedByOrderByValidatedAtDesc(UUID validatedBy);
}
