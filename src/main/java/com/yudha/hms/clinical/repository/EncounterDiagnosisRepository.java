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
     * Find all diagnoses by Encounter entity.
     */
    List<EncounterDiagnosis> findByEncounter(com.yudha.hms.clinical.entity.Encounter encounter);

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

    // ========== Diagnosis History Queries ==========

    /**
     * Get diagnosis history for a patient across all encounters.
     */
    @Query("""
        SELECT ed FROM EncounterDiagnosis ed
        JOIN ed.encounter e
        WHERE e.patientId = :patientId
        ORDER BY ed.recordedDate DESC, ed.rank ASC
        """)
    List<EncounterDiagnosis> findDiagnosisHistoryByPatient(@Param("patientId") UUID patientId);

    /**
     * Get diagnosis history for a patient with specific diagnosis code.
     */
    @Query("""
        SELECT ed FROM EncounterDiagnosis ed
        JOIN ed.encounter e
        WHERE e.patientId = :patientId
        AND ed.diagnosisCode = :diagnosisCode
        ORDER BY ed.recordedDate DESC
        """)
    List<EncounterDiagnosis> findPatientDiagnosisHistory(
        @Param("patientId") UUID patientId,
        @Param("diagnosisCode") String diagnosisCode
    );

    /**
     * Get all unique diagnosis codes for a patient.
     */
    @Query("""
        SELECT DISTINCT ed.diagnosisCode, ed.diagnosisText
        FROM EncounterDiagnosis ed
        JOIN ed.encounter e
        WHERE e.patientId = :patientId
        ORDER BY ed.diagnosisCode ASC
        """)
    List<Object[]> findUniquePatientDiagnoses(@Param("patientId") UUID patientId);

    /**
     * Get chronic/recurring diagnoses for a patient (appears 3+ times).
     */
    @Query("""
        SELECT ed.diagnosisCode, ed.diagnosisText, COUNT(ed)
        FROM EncounterDiagnosis ed
        JOIN ed.encounter e
        WHERE e.patientId = :patientId
        GROUP BY ed.diagnosisCode, ed.diagnosisText
        HAVING COUNT(ed) >= :minOccurrences
        ORDER BY COUNT(ed) DESC
        """)
    List<Object[]> findRecurringDiagnoses(
        @Param("patientId") UUID patientId,
        @Param("minOccurrences") long minOccurrences
    );

    /**
     * Get active diagnoses for a patient across all active encounters.
     */
    @Query("""
        SELECT ed FROM EncounterDiagnosis ed
        JOIN ed.encounter e
        WHERE e.patientId = :patientId
        AND ed.clinicalStatus = 'ACTIVE'
        AND e.status IN ('IN_PROGRESS', 'ARRIVED', 'TRIAGED')
        ORDER BY ed.recordedDate DESC
        """)
    List<EncounterDiagnosis> findActivePatientDiagnoses(@Param("patientId") UUID patientId);

    /**
     * Get diagnosis count by code for analytics.
     */
    @Query("""
        SELECT ed.diagnosisCode, ed.diagnosisText, COUNT(ed)
        FROM EncounterDiagnosis ed
        WHERE ed.recordedDate >= :startDate
        GROUP BY ed.diagnosisCode, ed.diagnosisText
        ORDER BY COUNT(ed) DESC
        """)
    List<Object[]> countDiagnosisUsageSince(@Param("startDate") java.time.LocalDateTime startDate);

    /**
     * Get diagnosis statistics by department.
     */
    @Query("""
        SELECT e.departmentId, ed.diagnosisCode, ed.diagnosisText, COUNT(ed)
        FROM EncounterDiagnosis ed
        JOIN ed.encounter e
        WHERE e.departmentId IS NOT NULL
        AND ed.recordedDate >= :startDate
        GROUP BY e.departmentId, ed.diagnosisCode, ed.diagnosisText
        ORDER BY e.departmentId, COUNT(ed) DESC
        """)
    List<Object[]> getDiagnosisStatsByDepartment(@Param("startDate") java.time.LocalDateTime startDate);

    /**
     * Check if patient has specific diagnosis in history.
     */
    @Query("""
        SELECT CASE WHEN COUNT(ed) > 0 THEN true ELSE false END
        FROM EncounterDiagnosis ed
        JOIN ed.encounter e
        WHERE e.patientId = :patientId
        AND ed.diagnosisCode = :diagnosisCode
        """)
    boolean hasPatientBeenDiagnosedWith(
        @Param("patientId") UUID patientId,
        @Param("diagnosisCode") String diagnosisCode
    );
}
