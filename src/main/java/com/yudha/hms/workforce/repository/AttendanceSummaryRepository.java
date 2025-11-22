package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.AttendanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceSummaryRepository extends JpaRepository<AttendanceSummary, UUID> {

    Optional<AttendanceSummary> findByEmployeeIdAndSummaryYearAndSummaryMonth(UUID employeeId, Integer summaryYear, Integer summaryMonth);

    List<AttendanceSummary> findByEmployeeIdAndSummaryYear(UUID employeeId, Integer summaryYear);

    List<AttendanceSummary> findByDepartmentIdAndSummaryYearAndSummaryMonth(UUID departmentId, Integer summaryYear, Integer summaryMonth);

    List<AttendanceSummary> findBySummaryYearAndSummaryMonth(Integer summaryYear, Integer summaryMonth);

    List<AttendanceSummary> findByEmployeeId(UUID employeeId);

    @Query("SELECT ats FROM AttendanceSummary ats WHERE ats.summaryYear = :year AND ats.summaryMonth = :month AND ats.finalized = false")
    List<AttendanceSummary> findUnfinalizedSummaries(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT ats FROM AttendanceSummary ats WHERE ats.employeeId = :employeeId ORDER BY ats.summaryYear DESC, ats.summaryMonth DESC")
    List<AttendanceSummary> findByEmployeeOrderedDesc(@Param("employeeId") UUID employeeId);

    @Query("SELECT ats FROM AttendanceSummary ats WHERE ats.departmentId = :departmentId AND ats.summaryYear = :year AND ats.summaryMonth = :month ORDER BY ats.attendanceRate ASC")
    List<AttendanceSummary> findByDepartmentOrderedByAttendanceRate(@Param("departmentId") UUID departmentId, @Param("year") Integer year, @Param("month") Integer month);
}
