package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.Shift;
import com.yudha.hms.clinical.entity.VitalSigns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for VitalSigns entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface VitalSignsRepository extends JpaRepository<VitalSigns, UUID> {

    /**
     * Find all vital signs for an encounter.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find all vital signs for a patient.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.patientId = :patientId ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find latest vital signs for an encounter.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId ORDER BY vs.measurementTime DESC LIMIT 1")
    Optional<VitalSigns> findLatestByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find vital signs by encounter and date range.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId " +
           "AND vs.measurementTime BETWEEN :startDate AND :endDate " +
           "ORDER BY vs.measurementTime ASC")
    List<VitalSigns> findByEncounterIdAndDateRange(
        @Param("encounterId") UUID encounterId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find vital signs by encounter and shift.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId AND vs.shift = :shift ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findByEncounterIdAndShift(
        @Param("encounterId") UUID encounterId,
        @Param("shift") Shift shift
    );

    /**
     * Find abnormal vital signs for an encounter.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId AND vs.isAbnormal = true ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findAbnormalByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find vital signs requiring notification.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.requiresNotification = true AND vs.notificationSent = false ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findRequiringNotification();

    /**
     * Find vital signs requiring notification for specific encounter.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId " +
           "AND vs.requiresNotification = true AND vs.notificationSent = false " +
           "ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findRequiringNotificationByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find vital signs for charting (last 24 hours).
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId " +
           "AND vs.measurementTime >= :since " +
           "ORDER BY vs.measurementTime ASC")
    List<VitalSigns> findForCharting(
        @Param("encounterId") UUID encounterId,
        @Param("since") LocalDateTime since
    );

    /**
     * Find latest vital signs for multiple encounters (for ward census).
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id IN :encounterIds " +
           "AND vs.id IN (SELECT MAX(vs2.id) FROM VitalSigns vs2 WHERE vs2.encounter.id IN :encounterIds GROUP BY vs2.encounter.id)")
    List<VitalSigns> findLatestForEncounters(@Param("encounterIds") List<UUID> encounterIds);

    /**
     * Count vital signs by encounter.
     */
    @Query("SELECT COUNT(vs) FROM VitalSigns vs WHERE vs.encounter.id = :encounterId")
    long countByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find vital signs with low GCS scores (critical).
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.gcsTotal IS NOT NULL AND vs.gcsTotal < 9 " +
           "ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findCriticalGcsScores();

    /**
     * Find vital signs by measurement type.
     */
    @Query("SELECT vs FROM VitalSigns vs WHERE vs.encounter.id = :encounterId " +
           "AND vs.measurementType = :measurementType " +
           "ORDER BY vs.measurementTime DESC")
    List<VitalSigns> findByEncounterIdAndMeasurementType(
        @Param("encounterId") UUID encounterId,
        @Param("measurementType") String measurementType
    );

    /**
     * Check if encounter has vital signs recorded today.
     */
    @Query("SELECT CASE WHEN COUNT(vs) > 0 THEN true ELSE false END FROM VitalSigns vs " +
           "WHERE vs.encounter.id = :encounterId " +
           "AND CAST(vs.measurementTime AS date) = CAST(:date AS date)")
    boolean hasVitalSignsForDate(
        @Param("encounterId") UUID encounterId,
        @Param("date") LocalDateTime date
    );
}
