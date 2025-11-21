package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.constant.ReportStatus;
import com.yudha.hms.laboratory.constant.ReportType;
import com.yudha.hms.laboratory.entity.LabReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LabReport entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabReportRepository extends JpaRepository<LabReport, UUID> {

    /**
     * Find by report number
     */
    Optional<LabReport> findByReportNumber(String reportNumber);

    /**
     * Find reports by order
     */
    List<LabReport> findByOrderIdOrderByGeneratedAtDesc(UUID orderId);

    /**
     * Find reports by patient
     */
    Page<LabReport> findByPatientIdOrderByGeneratedAtDesc(UUID patientId, Pageable pageable);

    /**
     * Find reports by encounter
     */
    List<LabReport> findByEncounterIdOrderByGeneratedAtDesc(UUID encounterId);

    /**
     * Find reports by type
     */
    Page<LabReport> findByReportTypeOrderByGeneratedAtDesc(ReportType reportType, Pageable pageable);

    /**
     * Find reports by status
     */
    Page<LabReport> findByStatusOrderByGeneratedAtDesc(ReportStatus status, Pageable pageable);

    /**
     * Find reports in date range
     */
    @Query("SELECT r FROM LabReport r WHERE r.generatedAt BETWEEN :startDate AND :endDate ORDER BY r.generatedAt DESC")
    Page<LabReport> findReportsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find unsigned reports
     */
    @Query("SELECT r FROM LabReport r WHERE r.status = 'FINAL' AND r.signed = false")
    List<LabReport> findUnsignedReports();

    /**
     * Find reports awaiting access
     */
    @Query("SELECT r FROM LabReport r WHERE r.status = 'FINAL' AND r.accessedByClinical = false")
    List<LabReport> findReportsAwaitingAccess();

    /**
     * Count reports by type and status
     */
    long countByReportTypeAndStatus(ReportType reportType, ReportStatus status);
}
