package com.yudha.hms.registration.repository;

import com.yudha.hms.registration.entity.TriageAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Triage Assessment operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface TriageAssessmentRepository extends JpaRepository<TriageAssessment, UUID> {

    /**
     * Find all triage assessments for an emergency registration.
     */
    @Query("SELECT t FROM TriageAssessment t WHERE t.emergencyRegistration.id = :emergencyId ORDER BY t.triageTime DESC")
    List<TriageAssessment> findByEmergencyRegistrationId(@Param("emergencyId") UUID emergencyId);

    /**
     * Find latest triage assessment for an emergency registration.
     */
    @Query("SELECT t FROM TriageAssessment t WHERE t.emergencyRegistration.id = :emergencyId ORDER BY t.triageTime DESC LIMIT 1")
    Optional<TriageAssessment> findLatestByEmergencyRegistrationId(@Param("emergencyId") UUID emergencyId);

    /**
     * Find re-triage assessments.
     */
    @Query("SELECT t FROM TriageAssessment t WHERE t.isRetriage = true ORDER BY t.triageTime DESC")
    List<TriageAssessment> findAllRetriages();

    /**
     * Find assessments by ESI level.
     */
    List<TriageAssessment> findByEsiLevel(Integer esiLevel);

    /**
     * Find assessments with critical red flags.
     */
    @Query("SELECT t FROM TriageAssessment t WHERE (t.hasChestPain = true OR t.hasDifficultyBreathing = true OR t.hasAlteredConsciousness = true OR t.hasSevereBleeding = true OR t.hasSeizures = true) ORDER BY t.triageTime DESC")
    List<TriageAssessment> findWithRedFlags();

    /**
     * Count assessments by ESI level.
     */
    @Query("SELECT COUNT(t) FROM TriageAssessment t WHERE t.esiLevel = :esiLevel")
    Long countByEsiLevel(@Param("esiLevel") Integer esiLevel);
}
