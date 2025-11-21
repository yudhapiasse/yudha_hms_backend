package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.CounselingStatus;
import com.yudha.hms.pharmacy.entity.Dispensing;
import com.yudha.hms.pharmacy.entity.PatientCounseling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Patient Counseling Repository.
 *
 * Provides data access for patient counseling records.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface PatientCounselingRepository extends JpaRepository<PatientCounseling, UUID>,
        JpaSpecificationExecutor<PatientCounseling> {

    /**
     * Find counseling by dispensing
     */
    Optional<PatientCounseling> findByDispensing(Dispensing dispensing);

    /**
     * Find by patient
     */
    List<PatientCounseling> findByPatientIdOrderByCounselingDateDesc(UUID patientId);

    /**
     * Find by status
     */
    List<PatientCounseling> findByStatusOrderByCounselingDateDesc(CounselingStatus status);

    /**
     * Find pending counseling sessions
     */
    @Query("SELECT c FROM PatientCounseling c WHERE c.status IN ('PENDING', 'RESCHEDULED') " +
           "AND c.active = true " +
           "ORDER BY c.scheduledDate ASC NULLS LAST, c.createdAt ASC")
    List<PatientCounseling> findPendingSessions();

    /**
     * Find in-progress counseling sessions
     */
    @Query("SELECT c FROM PatientCounseling c WHERE c.status = 'IN_PROGRESS' " +
           "AND c.active = true " +
           "ORDER BY c.counselingDate ASC")
    List<PatientCounseling> findInProgressSessions();

    /**
     * Find counseling by pharmacist
     */
    List<PatientCounseling> findByPharmacistIdOrderByCounselingDateDesc(UUID pharmacistId);

    /**
     * Find counseling sessions in date range
     */
    @Query("SELECT c FROM PatientCounseling c WHERE c.counselingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY c.counselingDate DESC")
    List<PatientCounseling> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Find completed sessions by pharmacist in date range - for reporting
     */
    @Query("SELECT c FROM PatientCounseling c WHERE c.pharmacistId = :pharmacistId " +
           "AND c.status = 'COMPLETED' " +
           "AND c.counselingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY c.counselingDate DESC")
    List<PatientCounseling> findCompletedByPharmacistAndDateRange(
            @Param("pharmacistId") UUID pharmacistId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find sessions requiring follow-up
     */
    @Query("SELECT c FROM PatientCounseling c WHERE c.followUpRequired = true " +
           "AND c.followUpDate <= :date " +
           "AND c.active = true " +
           "ORDER BY c.followUpDate ASC")
    List<PatientCounseling> findRequiringFollowUp(@Param("date") LocalDateTime date);

    /**
     * Find declined counseling sessions - for quality monitoring
     */
    @Query("SELECT c FROM PatientCounseling c WHERE c.status = 'DECLINED' " +
           "AND c.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY c.createdAt DESC")
    List<PatientCounseling> findDeclinedSessions(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    /**
     * Count pending counseling sessions
     */
    @Query("SELECT COUNT(c) FROM PatientCounseling c WHERE c.status IN ('PENDING', 'RESCHEDULED') " +
           "AND c.active = true")
    Long countPendingSessions();

    /**
     * Get average counseling duration
     */
    @Query("SELECT AVG(c.durationMinutes) FROM PatientCounseling c " +
           "WHERE c.status = 'COMPLETED' " +
           "AND c.counselingDate BETWEEN :startDate AND :endDate")
    Double getAverageCounselingDuration(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Find comprehensive counseling sessions (all checkboxes marked)
     */
    @Query("SELECT c FROM PatientCounseling c WHERE c.status = 'COMPLETED' " +
           "AND c.drugInformationProvided = true " +
           "AND c.dosageInstructionsExplained = true " +
           "AND c.sideEffectsDiscussed = true " +
           "AND c.interactionsDiscussed = true " +
           "AND c.storageInstructionsGiven = true " +
           "AND c.counselingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY c.counselingDate DESC")
    List<PatientCounseling> findComprehensiveSessions(@Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);
}
