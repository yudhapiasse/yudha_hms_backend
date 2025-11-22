package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.constant.reporting.ComparisonChange;
import com.yudha.hms.radiology.entity.reporting.ReportComparison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportComparisonRepository extends JpaRepository<ReportComparison, UUID> {

    List<ReportComparison> findByCurrentReportId(UUID currentReportId);

    List<ReportComparison> findByPriorReportId(UUID priorReportId);

    Optional<ReportComparison> findByCurrentReportIdAndPriorReportId(UUID currentReportId, UUID priorReportId);

    List<ReportComparison> findByOverallChange(ComparisonChange overallChange);

    @Query("SELECT r FROM ReportComparison r WHERE r.comparedBy = :userId AND r.comparisonDate BETWEEN :startDate AND :endDate")
    List<ReportComparison> findByComparedByAndDateRange(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM ReportComparison r WHERE r.currentReportId = :reportId OR r.priorReportId = :reportId")
    List<ReportComparison> findAllComparisonsForReport(@Param("reportId") UUID reportId);

    @Query("SELECT COUNT(r) FROM ReportComparison r WHERE r.currentReportId = :reportId")
    long countByCurrentReportId(@Param("reportId") UUID reportId);
}
