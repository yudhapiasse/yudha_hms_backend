package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.EncounterLocationHistory;
import com.yudha.hms.clinical.entity.LocationEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for EncounterLocationHistory entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface EncounterLocationHistoryRepository extends JpaRepository<EncounterLocationHistory, UUID> {

    /**
     * Find all location history for an encounter.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find all location history for a patient.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.patientId = :patientId ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find current location for an encounter.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId AND elh.isCurrent = true")
    Optional<EncounterLocationHistory> findCurrentByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find location history by event type.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId " +
           "AND elh.locationEventType = :eventType " +
           "ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findByEncounterIdAndEventType(
        @Param("encounterId") UUID encounterId,
        @Param("eventType") LocationEventType eventType
    );

    /**
     * Find ICU stays for an encounter.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId " +
           "AND elh.isIcu = true " +
           "ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findIcuStaysByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find current ICU patients.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.isIcu = true AND elh.isCurrent = true " +
           "ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findCurrentIcuPatients();

    /**
     * Find patients in isolation.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.isolationRequired = true AND elh.isCurrent = true " +
           "ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findPatientsInIsolation();

    /**
     * Find location history by bed.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.bedId = :bedId ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findByBedId(@Param("bedId") UUID bedId);

    /**
     * Find current occupant of a bed.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.bedId = :bedId AND elh.isCurrent = true")
    Optional<EncounterLocationHistory> findCurrentByBedId(@Param("bedId") UUID bedId);

    /**
     * Find location history by department.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.departmentId = :departmentId " +
           "ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Find current patients in a department.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.departmentId = :departmentId " +
           "AND elh.isCurrent = true " +
           "ORDER BY elh.startTime DESC")
    List<EncounterLocationHistory> findCurrentByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Find location history by date range.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId " +
           "AND elh.startTime BETWEEN :startDate AND :endDate " +
           "ORDER BY elh.startTime ASC")
    List<EncounterLocationHistory> findByEncounterIdAndDateRange(
        @Param("encounterId") UUID encounterId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Mark all previous locations as not current for an encounter.
     */
    @Modifying
    @Query("UPDATE EncounterLocationHistory elh SET elh.isCurrent = false WHERE elh.encounter.id = :encounterId AND elh.isCurrent = true")
    void markAllAsNotCurrent(@Param("encounterId") UUID encounterId);

    /**
     * End current location stay for an encounter.
     */
    @Modifying
    @Query("UPDATE EncounterLocationHistory elh SET elh.endTime = :endTime, elh.isCurrent = false " +
           "WHERE elh.encounter.id = :encounterId AND elh.isCurrent = true")
    void endCurrentStay(@Param("encounterId") UUID encounterId, @Param("endTime") LocalDateTime endTime);

    /**
     * Count location changes for an encounter.
     */
    @Query("SELECT COUNT(elh) FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId")
    long countByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Calculate total ICU duration for an encounter.
     */
    @Query("SELECT SUM(elh.durationHours) FROM EncounterLocationHistory elh " +
           "WHERE elh.encounter.id = :encounterId AND elh.isIcu = true AND elh.endTime IS NOT NULL")
    Integer calculateTotalIcuHours(@Param("encounterId") UUID encounterId);

    /**
     * Find admission event for an encounter.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId " +
           "AND elh.locationEventType = 'ADMISSION' " +
           "ORDER BY elh.startTime ASC LIMIT 1")
    Optional<EncounterLocationHistory> findAdmissionByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find discharge event for an encounter.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.encounter.id = :encounterId " +
           "AND elh.locationEventType = 'DISCHARGE' " +
           "ORDER BY elh.startTime DESC LIMIT 1")
    Optional<EncounterLocationHistory> findDischargeByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find encounters in a specific location.
     */
    @Query("SELECT elh.encounter.id FROM EncounterLocationHistory elh " +
           "WHERE elh.locationId = :locationId AND elh.isCurrent = true")
    List<UUID> findEncounterIdsByLocationId(@Param("locationId") UUID locationId);

    /**
     * Count patients by department (census).
     */
    @Query("SELECT COUNT(elh) FROM EncounterLocationHistory elh " +
           "WHERE elh.departmentId = :departmentId AND elh.isCurrent = true")
    long countCurrentPatientsByDepartmentId(@Param("departmentId") UUID departmentId);

    /**
     * Find bed occupancy history.
     */
    @Query("SELECT elh FROM EncounterLocationHistory elh WHERE elh.bedId = :bedId " +
           "AND elh.startTime BETWEEN :startDate AND :endDate " +
           "ORDER BY elh.startTime ASC")
    List<EncounterLocationHistory> findBedOccupancyHistory(
        @Param("bedId") UUID bedId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
