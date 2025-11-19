package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.EncounterStatus;
import com.yudha.hms.clinical.entity.EncounterType;
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
 * Encounter Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface EncounterRepository extends JpaRepository<Encounter, UUID>, JpaSpecificationExecutor<Encounter> {

    /**
     * Find encounter by encounter number.
     */
    Optional<Encounter> findByEncounterNumber(String encounterNumber);

    /**
     * Check if encounter number exists.
     */
    boolean existsByEncounterNumber(String encounterNumber);

    /**
     * Find all encounters by patient ID.
     */
    List<Encounter> findByPatientIdOrderByEncounterStartDesc(UUID patientId);

    /**
     * Find active encounters by patient ID.
     */
    @Query("SELECT e FROM Encounter e WHERE e.patientId = :patientId AND e.status IN :activeStatuses ORDER BY e.encounterStart DESC")
    List<Encounter> findActiveEncountersByPatientId(
        @Param("patientId") UUID patientId,
        @Param("activeStatuses") List<EncounterStatus> activeStatuses
    );

    /**
     * Find encounters by type.
     */
    List<Encounter> findByEncounterTypeOrderByEncounterStartDesc(EncounterType encounterType);

    /**
     * Find encounters by status.
     */
    List<Encounter> findByStatusOrderByEncounterStartDesc(EncounterStatus status);

    /**
     * Find encounters by department.
     */
    List<Encounter> findByCurrentDepartmentOrderByEncounterStartDesc(String department);

    /**
     * Find encounters by date range.
     */
    @Query("SELECT e FROM Encounter e WHERE e.encounterStart BETWEEN :startDate AND :endDate ORDER BY e.encounterStart DESC")
    List<Encounter> findByEncounterStartBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find encounters by outpatient registration ID.
     */
    Optional<Encounter> findByOutpatientRegistrationId(UUID outpatientRegistrationId);

    /**
     * Find encounters by inpatient admission ID.
     */
    Optional<Encounter> findByInpatientAdmissionId(UUID inpatientAdmissionId);

    /**
     * Find encounters by emergency registration ID.
     */
    Optional<Encounter> findByEmergencyRegistrationId(UUID emergencyRegistrationId);

    /**
     * Find BPJS encounters by SEP number.
     */
    Optional<Encounter> findBySepNumber(String sepNumber);

    /**
     * Count active encounters by patient.
     */
    @Query("SELECT COUNT(e) FROM Encounter e WHERE e.patientId = :patientId AND e.status IN :activeStatuses")
    long countActiveEncountersByPatientId(
        @Param("patientId") UUID patientId,
        @Param("activeStatuses") List<EncounterStatus> activeStatuses
    );

    /**
     * Find encounters by attending doctor.
     */
    List<Encounter> findByAttendingDoctorIdAndStatusInOrderByEncounterStartDesc(
        UUID attendingDoctorId,
        List<EncounterStatus> statuses
    );

    /**
     * Get encounter count by type and date range.
     */
    @Query("SELECT COUNT(e) FROM Encounter e WHERE e.encounterType = :type AND e.encounterStart BETWEEN :startDate AND :endDate")
    long countByTypeAndDateRange(
        @Param("type") EncounterType type,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
