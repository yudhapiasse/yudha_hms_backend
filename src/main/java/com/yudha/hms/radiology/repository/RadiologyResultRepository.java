package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.entity.RadiologyResult;
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
 * Repository for RadiologyResult entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface RadiologyResultRepository extends JpaRepository<RadiologyResult, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<RadiologyResult> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by result number
     */
    Optional<RadiologyResult> findByResultNumberAndDeletedAtIsNull(String resultNumber);

    /**
     * Find by order item
     */
    Optional<RadiologyResult> findByOrderItemIdAndDeletedAtIsNull(UUID orderItemId);

    /**
     * Find by patient
     */
    Page<RadiologyResult> findByPatientIdAndDeletedAtIsNull(UUID patientId, Pageable pageable);

    /**
     * Find by examination
     */
    List<RadiologyResult> findByExaminationIdAndDeletedAtIsNull(UUID examinationId);

    /**
     * Find by DICOM study ID
     */
    Optional<RadiologyResult> findByDicomStudyIdAndDeletedAtIsNull(String dicomStudyId);

    /**
     * Find by technician
     */
    List<RadiologyResult> findByPerformedByTechnicianIdAndDeletedAtIsNull(UUID technicianId);

    /**
     * Find by radiologist
     */
    List<RadiologyResult> findByRadiologistIdAndDeletedAtIsNull(UUID radiologistId);

    /**
     * Find pending results (not finalized)
     */
    @Query("SELECT r FROM RadiologyResult r WHERE r.isFinalized = false AND r.deletedAt IS NULL ORDER BY r.performedDate ASC")
    List<RadiologyResult> findPendingResults();

    /**
     * Find finalized results
     */
    List<RadiologyResult> findByIsFinalizedTrueAndDeletedAtIsNull();

    /**
     * Find amended results
     */
    List<RadiologyResult> findByIsAmendedTrueAndDeletedAtIsNull();

    /**
     * Find results by date range
     */
    @Query("SELECT r FROM RadiologyResult r WHERE r.performedDate BETWEEN :startDate AND :endDate AND r.deletedAt IS NULL")
    Page<RadiologyResult> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find results awaiting radiologist review
     */
    @Query("SELECT r FROM RadiologyResult r WHERE r.performedDate IS NOT NULL AND r.radiologistId IS NULL AND r.isFinalized = false AND r.deletedAt IS NULL ORDER BY r.performedDate ASC")
    List<RadiologyResult> findResultsAwaitingRadiologist();

    /**
     * Search results by result number or patient info
     */
    @Query("SELECT r FROM RadiologyResult r WHERE (LOWER(r.resultNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.patient.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.patient.mrn) LIKE LOWER(CONCAT('%', :search, '%'))) AND r.deletedAt IS NULL")
    Page<RadiologyResult> searchResults(@Param("search") String search, Pageable pageable);

    /**
     * Count finalized results
     */
    long countByIsFinalizedTrueAndDeletedAtIsNull();

    /**
     * Count pending results
     */
    long countByIsFinalizedFalseAndDeletedAtIsNull();

    /**
     * Count results by examination
     */
    long countByExaminationIdAndDeletedAtIsNull(UUID examinationId);

    /**
     * Count results by radiologist
     */
    long countByRadiologistIdAndDeletedAtIsNull(UUID radiologistId);
}
