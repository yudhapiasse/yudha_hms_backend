package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.clinical.entity.PhysicalExamination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Physical Examination Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface PhysicalExaminationRepository extends JpaRepository<PhysicalExamination, UUID> {

    /**
     * Find physical examination by encounter.
     */
    List<PhysicalExamination> findByEncounterOrderByExaminationDateDesc(Encounter encounter);

    /**
     * Find physical examination by encounter ID.
     */
    List<PhysicalExamination> findByEncounter_IdOrderByExaminationDateDesc(UUID encounterId);

    /**
     * Find the most recent physical examination for an encounter.
     */
    Optional<PhysicalExamination> findFirstByEncounterOrderByExaminationDateDesc(Encounter encounter);

    /**
     * Find physical examinations by patient ID.
     */
    List<PhysicalExamination> findByPatientIdOrderByExaminationDateDesc(UUID patientId);

    /**
     * Find physical examinations by examiner.
     */
    List<PhysicalExamination> findByExaminerIdOrderByExaminationDateDesc(UUID examinerId);

    /**
     * Find unsigned physical examinations.
     */
    @Query("SELECT pe FROM PhysicalExamination pe WHERE pe.isSigned = false ORDER BY pe.examinationDate DESC")
    List<PhysicalExamination> findUnsignedExaminations();

    /**
     * Find unsigned physical examinations by examiner.
     */
    @Query("SELECT pe FROM PhysicalExamination pe WHERE pe.isSigned = false AND pe.examinerId = :examinerId ORDER BY pe.examinationDate DESC")
    List<PhysicalExamination> findUnsignedExaminationsByExaminer(@Param("examinerId") UUID examinerId);

    /**
     * Find physical examinations with abnormal findings.
     */
    @Query("SELECT pe FROM PhysicalExamination pe WHERE pe.abnormalFindings IS NOT NULL AND pe.abnormalFindings != '' ORDER BY pe.examinationDate DESC")
    List<PhysicalExamination> findWithAbnormalFindings();

    /**
     * Find physical examinations by date range.
     */
    @Query("SELECT pe FROM PhysicalExamination pe WHERE pe.examinationDate BETWEEN :startDate AND :endDate ORDER BY pe.examinationDate DESC")
    List<PhysicalExamination> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * Count physical examinations for a patient.
     */
    long countByPatientId(UUID patientId);

    /**
     * Count unsigned examinations by examiner.
     */
    long countByExaminerIdAndIsSignedFalse(UUID examinerId);
}
