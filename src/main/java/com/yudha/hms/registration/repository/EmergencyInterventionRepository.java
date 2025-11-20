package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.EmergencyIntervention;
import com.yudha.hms.registration.entity.InterventionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for EmergencyIntervention entity.
 * Handles emergency intervention data access and queries.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface EmergencyInterventionRepository extends JpaRepository<EmergencyIntervention, UUID> {

    /**
     * Find all interventions for an emergency registration.
     */
    List<EmergencyIntervention> findByEmergencyRegistrationIdOrderByInterventionTimeDesc(UUID emergencyRegistrationId);

    /**
     * Find all interventions for an encounter.
     */
    List<EmergencyIntervention> findByEncounterIdOrderByInterventionTimeDesc(UUID encounterId);

    /**
     * Find interventions by type for an emergency registration.
     */
    List<EmergencyIntervention> findByEmergencyRegistrationIdAndInterventionTypeOrderByInterventionTimeDesc(
        UUID emergencyRegistrationId,
        InterventionType interventionType
    );

    /**
     * Find interventions by type for an encounter.
     */
    List<EmergencyIntervention> findByEncounterIdAndInterventionTypeOrderByInterventionTimeDesc(
        UUID encounterId,
        InterventionType interventionType
    );

    /**
     * Find all resuscitation events for an emergency registration.
     */
    List<EmergencyIntervention> findByEmergencyRegistrationIdAndIsResuscitationTrueOrderByInterventionTimeDesc(
        UUID emergencyRegistrationId
    );

    /**
     * Find resuscitation events for an encounter.
     */
    List<EmergencyIntervention> findByEncounterIdAndIsResuscitationTrueOrderByInterventionTimeDesc(
        UUID encounterId
    );

    /**
     * Find latest intervention for an emergency registration.
     */
    Optional<EmergencyIntervention> findFirstByEmergencyRegistrationIdOrderByInterventionTimeDesc(
        UUID emergencyRegistrationId
    );

    /**
     * Find interventions by performer.
     */
    List<EmergencyIntervention> findByPerformedByIdOrderByInterventionTimeDesc(UUID performedById);

    /**
     * Find interventions with complications.
     */
    @Query("SELECT ei FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND ei.complicationsOccurred = true " +
           "ORDER BY ei.interventionTime DESC")
    List<EmergencyIntervention> findInterventionsWithComplications(
        @Param("emergencyRegistrationId") UUID emergencyRegistrationId
    );

    /**
     * Find critical interventions (resuscitation, airway, etc.).
     */
    @Query("SELECT ei FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND (ei.interventionType = 'RESUSCITATION' " +
           "     OR ei.interventionType = 'AIRWAY_MANAGEMENT' " +
           "     OR ei.interventionType = 'CHEST_TUBE' " +
           "     OR ei.interventionType = 'CARDIOVERSION' " +
           "     OR ei.interventionType = 'DEFIBRILLATION') " +
           "ORDER BY ei.interventionTime DESC")
    List<EmergencyIntervention> findCriticalInterventions(
        @Param("emergencyRegistrationId") UUID emergencyRegistrationId
    );

    /**
     * Find interventions within time range.
     */
    @Query("SELECT ei FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND ei.interventionTime BETWEEN :startTime AND :endTime " +
           "ORDER BY ei.interventionTime DESC")
    List<EmergencyIntervention> findByTimeRange(
        @Param("emergencyRegistrationId") UUID emergencyRegistrationId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    /**
     * Count interventions by type for an emergency registration.
     */
    long countByEmergencyRegistrationIdAndInterventionType(
        UUID emergencyRegistrationId,
        InterventionType interventionType
    );

    /**
     * Count resuscitation events.
     */
    long countByEmergencyRegistrationIdAndIsResuscitationTrue(UUID emergencyRegistrationId);

    /**
     * Check if ROSC was achieved in any resuscitation.
     */
    @Query("SELECT CASE WHEN COUNT(ei) > 0 THEN true ELSE false END " +
           "FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND ei.isResuscitation = true " +
           "AND ei.roscAchieved = true")
    boolean hasSuccessfulResuscitation(@Param("emergencyRegistrationId") UUID emergencyRegistrationId);

    /**
     * Get total resuscitation duration for emergency registration.
     */
    @Query("SELECT SUM(ei.resuscitationDurationMinutes) " +
           "FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND ei.isResuscitation = true")
    Integer getTotalResuscitationDuration(@Param("emergencyRegistrationId") UUID emergencyRegistrationId);

    /**
     * Find most recent resuscitation event.
     */
    @Query("SELECT ei FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND ei.isResuscitation = true " +
           "ORDER BY ei.resuscitationStartTime DESC " +
           "LIMIT 1")
    Optional<EmergencyIntervention> findLatestResuscitation(
        @Param("emergencyRegistrationId") UUID emergencyRegistrationId
    );

    /**
     * Find interventions by urgency level.
     */
    List<EmergencyIntervention> findByEmergencyRegistrationIdAndUrgencyOrderByInterventionTimeDesc(
        UUID emergencyRegistrationId,
        String urgency
    );

    /**
     * Count interventions by performer.
     */
    long countByPerformedById(UUID performedById);

    /**
     * Find incomplete resuscitations (no end time).
     */
    @Query("SELECT ei FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND ei.isResuscitation = true " +
           "AND ei.resuscitationEndTime IS NULL " +
           "ORDER BY ei.resuscitationStartTime DESC")
    List<EmergencyIntervention> findOngoingResuscitations(
        @Param("emergencyRegistrationId") UUID emergencyRegistrationId
    );

    /**
     * Find interventions requiring supervision that haven't been completed.
     */
    @Query("SELECT ei FROM EmergencyIntervention ei " +
           "WHERE ei.emergencyRegistration.id = :emergencyRegistrationId " +
           "AND ei.outcome IS NULL " +
           "AND (ei.interventionType IN ('RESUSCITATION', 'AIRWAY_MANAGEMENT', 'CENTRAL_LINE', 'CHEST_TUBE')) " +
           "ORDER BY ei.interventionTime DESC")
    List<EmergencyIntervention> findPendingCriticalInterventions(
        @Param("emergencyRegistrationId") UUID emergencyRegistrationId
    );
}
