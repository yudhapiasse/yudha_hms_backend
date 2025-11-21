package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.constant.ResultStatus;
import com.yudha.hms.laboratory.entity.LabResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LabResult entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabResultRepository extends JpaRepository<LabResult, UUID> {

    /**
     * Find by result number
     */
    Optional<LabResult> findByResultNumber(String resultNumber);

    /**
     * Find by order
     */
    List<LabResult> findByOrderIdOrderByEnteredAtDesc(UUID orderId);

    /**
     * Find by order item
     */
    Optional<LabResult> findByOrderItemId(UUID orderItemId);

    /**
     * Find by specimen
     */
    Optional<LabResult> findBySpecimenId(UUID specimenId);

    /**
     * Find by test
     */
    List<LabResult> findByTestId(UUID testId);

    /**
     * Find by status
     */
    Page<LabResult> findByStatusOrderByEnteredAtDesc(ResultStatus status, Pageable pageable);

    /**
     * Find results awaiting validation
     */
    @Query("SELECT r FROM LabResult r WHERE r.status = 'PRELIMINARY' AND r.validatedAt IS NULL")
    Page<LabResult> findResultsAwaitingValidation(Pageable pageable);

    /**
     * Find results awaiting pathologist review
     */
    @Query("SELECT r FROM LabResult r WHERE r.requiresPathologistReview = true AND r.reviewedByPathologist = false")
    List<LabResult> findResultsAwaitingPathologistReview();

    /**
     * Find results with panic values
     */
    @Query("SELECT r FROM LabResult r WHERE r.hasPanicValues = true AND r.panicValueNotified = false")
    List<LabResult> findResultsWithUnnotifiedPanicValues();

    /**
     * Find results with delta check flags
     */
    @Query("SELECT r FROM LabResult r WHERE r.deltaCheckFlagged = true AND r.enteredAt BETWEEN :startDate AND :endDate")
    List<LabResult> findResultsWithDeltaCheckFlags(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find previous result for patient and test (for delta check)
     */
    @Query("SELECT r FROM LabResult r JOIN r.order o WHERE o.patientId = :patientId AND r.test.id = :testId AND r.status = 'FINAL' AND r.id != :currentResultId ORDER BY r.validatedAt DESC")
    List<LabResult> findPreviousResults(@Param("patientId") UUID patientId, @Param("testId") UUID testId, @Param("currentResultId") UUID currentResultId, Pageable pageable);

    /**
     * Find previous result for patient and test (convenience method for delta check)
     */
    default Optional<LabResult> findPreviousResult(UUID patientId, UUID testId, UUID currentResultId) {
        List<LabResult> results = findPreviousResults(patientId, testId, currentResultId, Pageable.ofSize(1));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find patient result history for test
     */
    @Query("SELECT r FROM LabResult r JOIN r.order o WHERE o.patientId = :patientId AND r.test.id = :testId AND r.status = 'FINAL' ORDER BY r.validatedAt DESC")
    List<LabResult> findPatientResultHistory(@Param("patientId") UUID patientId, @Param("testId") UUID testId);

    /**
     * Count results by status
     */
    long countByStatus(ResultStatus status);
}
