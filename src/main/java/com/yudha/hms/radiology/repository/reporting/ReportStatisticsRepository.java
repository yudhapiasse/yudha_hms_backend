package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.entity.reporting.ReportStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportStatisticsRepository extends JpaRepository<ReportStatistics, UUID> {

    List<ReportStatistics> findByPeriodType(String periodType);

    List<ReportStatistics> findByRadiologistId(UUID radiologistId);

    List<ReportStatistics> findByDepartmentId(UUID departmentId);

    List<ReportStatistics> findByModalityCode(String modalityCode);

    @Query("SELECT r FROM ReportStatistics r WHERE r.statisticsDate BETWEEN :startDate AND :endDate")
    List<ReportStatistics> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM ReportStatistics r WHERE r.periodType = :periodType AND r.statisticsDate BETWEEN :startDate AND :endDate")
    List<ReportStatistics> findByPeriodTypeAndDateRange(
            @Param("periodType") String periodType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM ReportStatistics r WHERE r.radiologistId = :radiologistId AND r.periodType = :periodType AND r.periodStartDate >= :startDate")
    List<ReportStatistics> findByRadiologistAndPeriodTypeAfterDate(
            @Param("radiologistId") UUID radiologistId,
            @Param("periodType") String periodType,
            @Param("startDate") LocalDate startDate
    );

    Optional<ReportStatistics> findByPeriodTypeAndPeriodStartDateAndPeriodEndDateAndRadiologistId(
            String periodType,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            UUID radiologistId
    );

    @Query("SELECT SUM(r.totalReports) FROM ReportStatistics r WHERE r.periodType = :periodType AND r.statisticsDate BETWEEN :startDate AND :endDate")
    Long sumTotalReportsByPeriodTypeAndDateRange(
            @Param("periodType") String periodType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
