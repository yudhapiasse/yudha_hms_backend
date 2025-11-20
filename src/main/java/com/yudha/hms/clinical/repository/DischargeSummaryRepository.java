package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.DischargeSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Discharge Summary Repository.
 *
 * Data access layer for discharge summary operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface DischargeSummaryRepository extends JpaRepository<DischargeSummary, UUID> {

    Optional<DischargeSummary> findByDischargeNumber(String dischargeNumber);

    Optional<DischargeSummary> findByEncounterId(UUID encounterId);

    List<DischargeSummary> findByPatientId(UUID patientId);

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.patientId = :patientId ORDER BY ds.dischargeDate DESC")
    List<DischargeSummary> findByPatientIdOrderByDischargeDateDesc(@Param("patientId") UUID patientId);

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.signed = false")
    List<DischargeSummary> findUnsignedSummaries();

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.documentGenerated = false")
    List<DischargeSummary> findWithoutGeneratedDocuments();

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.dischargeDate BETWEEN :startDate AND :endDate")
    List<DischargeSummary> findByDischargeDateBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.dischargeDoctorId = :doctorId ORDER BY ds.dischargeDate DESC")
    List<DischargeSummary> findByDischargeDoctorId(@Param("doctorId") UUID doctorId);

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.dischargeCondition = :condition")
    List<DischargeSummary> findByDischargeCondition(@Param("condition") String condition);

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.dischargeDisposition = :disposition")
    List<DischargeSummary> findByDischargeDisposition(@Param("disposition") String disposition);

    @Query("SELECT COUNT(ds) FROM DischargeSummary ds WHERE ds.dischargeDate BETWEEN :startDate AND :endDate")
    long countDischargesBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT ds FROM DischargeSummary ds WHERE ds.satusehatSubmitted = false AND ds.signed = true")
    List<DischargeSummary> findPendingSatusehatSubmission();

    boolean existsByEncounterId(UUID encounterId);
}
