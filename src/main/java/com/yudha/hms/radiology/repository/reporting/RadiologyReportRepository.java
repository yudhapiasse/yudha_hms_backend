package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.constant.reporting.ReportStatus;
import com.yudha.hms.radiology.entity.reporting.RadiologyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RadiologyReportRepository extends JpaRepository<RadiologyReport, UUID> {

    Optional<RadiologyReport> findByReportNumber(String reportNumber);

    List<RadiologyReport> findByStudyId(UUID studyId);

    List<RadiologyReport> findByPatientId(UUID patientId);

    List<RadiologyReport> findByReportStatus(ReportStatus reportStatus);

    List<RadiologyReport> findByReportedByAndReportStatus(UUID reportedBy, ReportStatus reportStatus);

    List<RadiologyReport> findByHasCriticalFindingsTrue();

    @Query("SELECT r FROM RadiologyReport r WHERE r.reportStatus = :status AND r.createdAt BETWEEN :startDate AND :endDate")
    List<RadiologyReport> findByStatusAndDateRange(
            @Param("status") ReportStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT r FROM RadiologyReport r WHERE r.reportedBy = :reportedBy AND r.examinationDate BETWEEN :startDate AND :endDate")
    List<RadiologyReport> findByRadiologistAndDateRange(
            @Param("reportedBy") UUID reportedBy,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(r) FROM RadiologyReport r WHERE r.reportStatus = :status")
    long countByStatus(@Param("status") ReportStatus status);

    boolean existsByReportNumber(String reportNumber);
}
