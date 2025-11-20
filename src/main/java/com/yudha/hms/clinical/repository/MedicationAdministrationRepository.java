package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.AdministrationStatus;
import com.yudha.hms.clinical.entity.MedicationAdministration;
import com.yudha.hms.clinical.entity.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for MedicationAdministration entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface MedicationAdministrationRepository extends JpaRepository<MedicationAdministration, UUID> {

    /**
     * Find by MAR number.
     */
    Optional<MedicationAdministration> findByMarNumber(String marNumber);

    /**
     * Find all medications for an encounter.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find all medications for a patient.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.patientId = :patientId ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find due medications for an encounter.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.administered = false " +
           "AND ma.administrationStatus = 'PENDING' " +
           "AND ma.scheduledDateTime <= :now " +
           "ORDER BY ma.scheduledDateTime ASC")
    List<MedicationAdministration> findDueByEncounterId(
        @Param("encounterId") UUID encounterId,
        @Param("now") LocalDateTime now
    );

    /**
     * Find overdue medications for an encounter.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.administered = false " +
           "AND ma.administrationStatus = 'PENDING' " +
           "AND ma.scheduledDateTime < :overdueTime " +
           "ORDER BY ma.scheduledDateTime ASC")
    List<MedicationAdministration> findOverdueByEncounterId(
        @Param("encounterId") UUID encounterId,
        @Param("overdueTime") LocalDateTime overdueTime
    );

    /**
     * Find medications by status.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.administrationStatus = :status " +
           "ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findByEncounterIdAndStatus(
        @Param("encounterId") UUID encounterId,
        @Param("status") AdministrationStatus status
    );

    /**
     * Find medications by schedule type.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.scheduleType = :scheduleType " +
           "ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findByEncounterIdAndScheduleType(
        @Param("encounterId") UUID encounterId,
        @Param("scheduleType") ScheduleType scheduleType
    );

    /**
     * Find PRN medications for an encounter.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.scheduleType = 'PRN' " +
           "AND ma.administrationStatus NOT IN ('DISCONTINUED') " +
           "ORDER BY ma.actualAdministrationDateTime DESC")
    List<MedicationAdministration> findPrnByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find medications with adverse reactions.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.adverseReaction = true " +
           "ORDER BY ma.actualAdministrationDateTime DESC")
    List<MedicationAdministration> findWithAdverseReactionsByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find all adverse reactions for a medication.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.medicationName = :medicationName " +
           "AND ma.adverseReaction = true " +
           "ORDER BY ma.actualAdministrationDateTime DESC")
    List<MedicationAdministration> findAdverseReactionsByMedication(@Param("medicationName") String medicationName);

    /**
     * Find high-alert medications.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.isHighAlertMedication = true " +
           "ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findHighAlertByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find medications requiring witness verification.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.requiresWitness = true " +
           "AND ma.witnessedById IS NULL " +
           "AND ma.administrationStatus = 'PENDING' " +
           "ORDER BY ma.scheduledDateTime ASC")
    List<MedicationAdministration> findRequiringWitness();

    /**
     * Find medications administered in date range.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.actualAdministrationDateTime BETWEEN :startDate AND :endDate " +
           "ORDER BY ma.actualAdministrationDateTime ASC")
    List<MedicationAdministration> findAdministeredInDateRange(
        @Param("encounterId") UUID encounterId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find medications administered by provider.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.administeredById = :providerId " +
           "ORDER BY ma.actualAdministrationDateTime DESC")
    List<MedicationAdministration> findByAdministeredById(@Param("providerId") UUID providerId);

    /**
     * Find refused medications for encounter.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.administrationStatus = 'REFUSED' " +
           "ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findRefusedByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find missed medications for encounter.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.administrationStatus = 'MISSED' " +
           "ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findMissedByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Count medications by status for encounter.
     */
    @Query("SELECT COUNT(ma) FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.administrationStatus = :status")
    long countByEncounterIdAndStatus(
        @Param("encounterId") UUID encounterId,
        @Param("status") AdministrationStatus status
    );

    /**
     * Find medications scheduled for today.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND CAST(ma.scheduledDateTime AS date) = CAST(:date AS date) " +
           "ORDER BY ma.scheduledDateTime ASC")
    List<MedicationAdministration> findScheduledForDate(
        @Param("encounterId") UUID encounterId,
        @Param("date") LocalDateTime date
    );

    /**
     * Find IV medications for encounter.
     */
    @Query("SELECT ma FROM MedicationAdministration ma WHERE ma.encounter.id = :encounterId " +
           "AND ma.route = 'IV' " +
           "ORDER BY ma.scheduledDateTime DESC")
    List<MedicationAdministration> findIvMedicationsByEncounterId(@Param("encounterId") UUID encounterId);
}
