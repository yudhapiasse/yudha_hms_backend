package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.EncounterParticipant;
import com.yudha.hms.clinical.entity.ParticipantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Encounter Participant Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface EncounterParticipantRepository extends JpaRepository<EncounterParticipant, UUID> {

    /**
     * Find all participants for an encounter.
     */
    List<EncounterParticipant> findByEncounterId(UUID encounterId);

    /**
     * Find participants by type for an encounter.
     */
    List<EncounterParticipant> findByEncounterIdAndParticipantType(UUID encounterId, ParticipantType participantType);

    /**
     * Find all encounters for a practitioner.
     */
    List<EncounterParticipant> findByPractitionerIdOrderByPeriodStartDesc(UUID practitionerId);

    /**
     * Find active participations for a practitioner.
     */
    @Query("SELECT ep FROM EncounterParticipant ep WHERE ep.practitionerId = :practitionerId AND ep.periodEnd IS NULL ORDER BY ep.periodStart DESC")
    List<EncounterParticipant> findActiveByPractitionerId(@Param("practitionerId") UUID practitionerId);

    /**
     * Check if practitioner is already a participant in the encounter.
     */
    boolean existsByEncounterIdAndPractitionerIdAndParticipantType(
        UUID encounterId,
        UUID practitionerId,
        ParticipantType participantType
    );
}
