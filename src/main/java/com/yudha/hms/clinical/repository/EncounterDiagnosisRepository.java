package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.ClinicalStatus;
import com.yudha.hms.clinical.entity.DiagnosisType;
import com.yudha.hms.clinical.entity.EncounterDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Encounter Diagnosis Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Repository
public interface EncounterDiagnosisRepository extends JpaRepository<EncounterDiagnosis, UUID> {

    /**
     * Find all diagnoses for an encounter.
     */
    List<EncounterDiagnosis> findByEncounterIdOrderByRankAsc(UUID encounterId);

    /**
     * Find diagnoses by type for an encounter.
     */
    List<EncounterDiagnosis> findByEncounterIdAndDiagnosisTypeOrderByRankAsc(
        UUID encounterId,
        DiagnosisType diagnosisType
    );

    /**
     * Find primary diagnosis for an encounter.
     */
    @Query("SELECT ed FROM EncounterDiagnosis ed WHERE ed.encounter.id = :encounterId AND (ed.diagnosisType = 'PRIMARY' OR ed.rank = 1) ORDER BY ed.rank ASC")
    Optional<EncounterDiagnosis> findPrimaryDiagnosisByEncounterId(@Param("encounterId") UUID encounterId);

    /**
     * Find active diagnoses for an encounter.
     */
    List<EncounterDiagnosis> findByEncounterIdAndClinicalStatusOrderByRankAsc(
        UUID encounterId,
        ClinicalStatus clinicalStatus
    );

    /**
     * Find diagnoses by code.
     */
    List<EncounterDiagnosis> findByDiagnosisCodeOrderByRecordedDateDesc(String diagnosisCode);

    /**
     * Find encounters with a specific diagnosis code.
     */
    @Query("SELECT ed FROM EncounterDiagnosis ed WHERE ed.diagnosisCode = :code AND ed.clinicalStatus = :status ORDER BY ed.recordedDate DESC")
    List<EncounterDiagnosis> findByDiagnosisCodeAndStatus(
        @Param("code") String code,
        @Param("status") ClinicalStatus status
    );

    /**
     * Count diagnoses for an encounter.
     */
    long countByEncounterId(UUID encounterId);
}
