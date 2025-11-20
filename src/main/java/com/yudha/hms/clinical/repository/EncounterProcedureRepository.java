package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.EncounterProcedure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Encounter Procedure Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface EncounterProcedureRepository extends JpaRepository<EncounterProcedure, UUID> {

    /**
     * Find procedure by procedure number.
     */
    Optional<EncounterProcedure> findByProcedureNumber(String procedureNumber);

    /**
     * Check if procedure number exists.
     */
    boolean existsByProcedureNumber(String procedureNumber);

    /**
     * Find procedures by encounter.
     */
    List<EncounterProcedure> findByEncounterOrderByProcedureDateDesc(Encounter encounter);

    /**
     * Find procedures by encounter ID.
     */
    List<EncounterProcedure> findByEncounter_IdOrderByProcedureDateDesc(UUID encounterId);

    /**
     * Find procedures by patient ID.
     */
    List<EncounterProcedure> findByPatientIdOrderByProcedureDateDesc(UUID patientId);

    /**
     * Find procedures by ICD-9-CM code.
     */
    List<EncounterProcedure> findByProcedureCodeOrderByProcedureDateDesc(String procedureCode);

    /**
     * Find procedures by primary provider.
     */
    List<EncounterProcedure> findByPrimaryProviderIdOrderByProcedureDateDesc(UUID providerId);

    /**
     * Find procedures by status.
     */
    List<EncounterProcedure> findByProcedureStatusOrderByProcedureDateDesc(EncounterProcedure.ProcedureStatus status);

    /**
     * Find unsigned procedure reports.
     */
    @Query("SELECT ep FROM EncounterProcedure ep WHERE ep.reportSigned = false AND ep.procedureStatus = 'COMPLETED' ORDER BY ep.procedureDate DESC")
    List<EncounterProcedure> findUnsignedReports();

    /**
     * Find unsigned procedure reports by provider.
     */
    @Query("SELECT ep FROM EncounterProcedure ep WHERE ep.reportSigned = false AND ep.procedureStatus = 'COMPLETED' AND ep.primaryProviderId = :providerId ORDER BY ep.procedureDate DESC")
    List<EncounterProcedure> findUnsignedReportsByProvider(@Param("providerId") UUID providerId);

    /**
     * Find procedures with complications.
     */
    @Query("SELECT ep FROM EncounterProcedure ep WHERE ep.complications IS NOT NULL AND ep.complications != '' ORDER BY ep.procedureDate DESC")
    List<EncounterProcedure> findWithComplications();

    /**
     * Find unbilled procedures.
     */
    @Query("SELECT ep FROM EncounterProcedure ep WHERE ep.billable = true AND ep.billed = false AND ep.procedureStatus = 'COMPLETED' ORDER BY ep.procedureDate DESC")
    List<EncounterProcedure> findUnbilledProcedures();

    /**
     * Find procedures by date range.
     */
    @Query("SELECT ep FROM EncounterProcedure ep WHERE ep.procedureDate BETWEEN :startDate AND :endDate ORDER BY ep.procedureDate DESC")
    List<EncounterProcedure> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find procedures by procedure type.
     */
    List<EncounterProcedure> findByProcedureTypeOrderByProcedureDateDesc(EncounterProcedure.ProcedureType procedureType);

    /**
     * Count procedures for a patient.
     */
    long countByPatientId(UUID patientId);

    /**
     * Count procedures by provider.
     */
    long countByPrimaryProviderId(UUID providerId);

    /**
     * Count unsigned reports by provider.
     */
    long countByPrimaryProviderIdAndReportSignedFalse(UUID providerId);
}
