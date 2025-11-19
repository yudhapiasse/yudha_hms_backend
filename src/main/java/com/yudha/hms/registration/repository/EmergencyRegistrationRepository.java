package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.EmergencyRegistration;
import com.yudha.hms.registration.entity.EmergencyStatus;
import com.yudha.hms.registration.entity.TriageLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Emergency Registration operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface EmergencyRegistrationRepository extends JpaRepository<EmergencyRegistration, UUID> {

    /**
     * Find emergency registration by emergency number.
     */
    Optional<EmergencyRegistration> findByEmergencyNumber(String emergencyNumber);

    /**
     * Find all active emergency registrations (in ER).
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.status IN ('REGISTERED', 'TRIAGED', 'IN_TREATMENT', 'WAITING_RESULTS') AND e.deletedAt IS NULL ORDER BY e.isCritical DESC, e.triagePriority ASC, e.registrationTime ASC")
    List<EmergencyRegistration> findAllActive();

    /**
     * Find all critical patients in ER.
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.isCritical = true AND e.status IN ('REGISTERED', 'TRIAGED', 'IN_TREATMENT', 'WAITING_RESULTS') AND e.deletedAt IS NULL ORDER BY e.triagePriority ASC, e.registrationTime ASC")
    List<EmergencyRegistration> findAllCritical();

    /**
     * Find emergency registrations by triage level.
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.triageLevel = :triageLevel AND e.status IN ('REGISTERED', 'TRIAGED', 'IN_TREATMENT', 'WAITING_RESULTS') AND e.deletedAt IS NULL ORDER BY e.registrationTime ASC")
    List<EmergencyRegistration> findByTriageLevel(@Param("triageLevel") TriageLevel triageLevel);

    /**
     * Find emergency registrations by status.
     */
    List<EmergencyRegistration> findByStatusAndDeletedAtIsNull(EmergencyStatus status);

    /**
     * Find emergency registrations by patient ID.
     */
    List<EmergencyRegistration> findByPatientIdAndDeletedAtIsNullOrderByRegistrationDateDesc(UUID patientId);

    /**
     * Find unknown patients.
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.isUnknownPatient = true AND e.deletedAt IS NULL ORDER BY e.registrationTime DESC")
    List<EmergencyRegistration> findAllUnknownPatients();

    /**
     * Find police cases.
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.isPoliceCase = true AND e.deletedAt IS NULL ORDER BY e.registrationTime DESC")
    List<EmergencyRegistration> findAllPoliceCases();

    /**
     * Find trauma cases.
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.isTraumaCase = true AND e.deletedAt IS NULL ORDER BY e.registrationTime DESC")
    List<EmergencyRegistration> findAllTraumaCases();

    /**
     * Find by ER zone.
     */
    List<EmergencyRegistration> findByErZoneAndStatusInAndDeletedAtIsNull(
        String erZone,
        List<EmergencyStatus> statuses
    );

    /**
     * Find registrations within date range.
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.registrationDate BETWEEN :startDate AND :endDate AND e.deletedAt IS NULL ORDER BY e.registrationDate DESC")
    List<EmergencyRegistration> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find patients waiting for triage.
     */
    @Query("SELECT e FROM EmergencyRegistration e WHERE e.status = 'REGISTERED' AND e.triageTime IS NULL AND e.deletedAt IS NULL ORDER BY e.arrivalTime ASC")
    List<EmergencyRegistration> findWaitingForTriage();

    /**
     * Count active emergency registrations.
     */
    @Query("SELECT COUNT(e) FROM EmergencyRegistration e WHERE e.status IN ('REGISTERED', 'TRIAGED', 'IN_TREATMENT', 'WAITING_RESULTS') AND e.deletedAt IS NULL")
    Long countActive();

    /**
     * Count by triage level.
     */
    @Query("SELECT COUNT(e) FROM EmergencyRegistration e WHERE e.triageLevel = :triageLevel AND e.status IN ('REGISTERED', 'TRIAGED', 'IN_TREATMENT', 'WAITING_RESULTS') AND e.deletedAt IS NULL")
    Long countByTriageLevel(@Param("triageLevel") TriageLevel triageLevel);

    /**
     * Count today's registrations.
     */
    @Query("SELECT COUNT(e) FROM EmergencyRegistration e WHERE cast(e.registrationDate as date) = CURRENT_DATE AND e.deletedAt IS NULL")
    Long countTodayRegistrations();

    /**
     * Get latest emergency number for sequence generation.
     */
    @Query("SELECT e.emergencyNumber FROM EmergencyRegistration e WHERE e.emergencyNumber LIKE :prefix% ORDER BY e.emergencyNumber DESC LIMIT 1")
    Optional<String> findLatestEmergencyNumberWithPrefix(@Param("prefix") String prefix);
}
