package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.DischargeReadiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Discharge Readiness Repository.
 *
 * Data access layer for discharge readiness assessment operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface DischargeReadinessRepository extends JpaRepository<DischargeReadiness, UUID> {

    Optional<DischargeReadiness> findByEncounterId(UUID encounterId);

    List<DischargeReadiness> findByPatientId(UUID patientId);

    @Query("SELECT dr FROM DischargeReadiness dr WHERE dr.readyForDischarge = true")
    List<DischargeReadiness> findReadyForDischarge();

    @Query("SELECT dr FROM DischargeReadiness dr WHERE dr.readyForDischarge = false")
    List<DischargeReadiness> findNotReadyForDischarge();

    @Query("SELECT dr FROM DischargeReadiness dr WHERE dr.hasDischargeBarriers = true AND dr.barriersResolved = false")
    List<DischargeReadiness> findWithUnresolvedBarriers();

    @Query("SELECT dr FROM DischargeReadiness dr WHERE dr.medicationsReconciled = false")
    List<DischargeReadiness> findPendingMedicationReconciliation();

    @Query("SELECT dr FROM DischargeReadiness dr WHERE dr.followUpScheduled = false")
    List<DischargeReadiness> findPendingFollowUpScheduling();

    @Query("SELECT dr FROM DischargeReadiness dr WHERE dr.patientEducationCompleted = false")
    List<DischargeReadiness> findPendingPatientEducation();

    boolean existsByEncounterId(UUID encounterId);

    @Query("SELECT COUNT(dr) FROM DischargeReadiness dr WHERE dr.readyForDischarge = true")
    long countReadyForDischarge();

    @Query("SELECT COUNT(dr) FROM DischargeReadiness dr WHERE dr.hasDischargeBarriers = true AND dr.barriersResolved = false")
    long countWithUnresolvedBarriers();
}
