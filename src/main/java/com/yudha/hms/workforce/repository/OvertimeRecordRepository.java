package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.OvertimeStatus;
import com.yudha.hms.workforce.constant.OvertimeType;
import com.yudha.hms.workforce.entity.OvertimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OvertimeRecordRepository extends JpaRepository<OvertimeRecord, UUID> {

    Optional<OvertimeRecord> findByOvertimeNumber(String overtimeNumber);

    List<OvertimeRecord> findByEmployeeId(UUID employeeId);

    List<OvertimeRecord> findByEmployeeIdAndStatus(UUID employeeId, OvertimeStatus status);

    List<OvertimeRecord> findByEmployeeIdAndOvertimeDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

    List<OvertimeRecord> findByDepartmentIdAndOvertimeDateBetween(UUID departmentId, LocalDate startDate, LocalDate endDate);

    List<OvertimeRecord> findByStatus(OvertimeStatus status);

    @Query("SELECT or FROM OvertimeRecord or WHERE or.employeeId = :employeeId AND or.overtimeDate BETWEEN :startDate AND :endDate AND or.status = :status")
    List<OvertimeRecord> findByEmployeeDateRangeAndStatus(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") OvertimeStatus status);

    @Query("SELECT SUM(or.effectiveOvertimeHours) FROM OvertimeRecord or WHERE or.employeeId = :employeeId AND or.overtimeDate BETWEEN :startDate AND :endDate AND or.status = 'APPROVED'")
    BigDecimal sumApprovedOvertimeHours(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT or FROM OvertimeRecord or WHERE or.employeeId = :employeeId AND or.overtimeDate BETWEEN :startDate AND :endDate AND or.status = 'APPROVED'")
    List<OvertimeRecord> findApprovedOvertimeByEmployeeAndPeriod(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(or.effectiveOvertimeHours), 0) FROM OvertimeRecord or WHERE or.employeeId = :employeeId AND or.overtimeDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalOvertimeHoursByEmployeeAndDateRange(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT or FROM OvertimeRecord or WHERE or.supervisorId = :supervisorId AND or.supervisorApproved = false AND or.status = 'PENDING'")
    List<OvertimeRecord> findPendingForSupervisor(@Param("supervisorId") UUID supervisorId);

    @Query("SELECT or FROM OvertimeRecord or WHERE or.status = 'APPROVED' AND or.paid = false")
    List<OvertimeRecord> findUnpaidOvertimes();

    @Query("SELECT or FROM OvertimeRecord or WHERE or.employeeId = :employeeId AND or.overtimeType = :type AND or.overtimeDate BETWEEN :startDate AND :endDate")
    List<OvertimeRecord> findByEmployeeTypeAndDateRange(@Param("employeeId") UUID employeeId, @Param("type") OvertimeType type, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT or FROM OvertimeRecord or WHERE or.exceedsDailyLimit = true OR or.exceedsWeeklyLimit = true")
    List<OvertimeRecord> findComplianceViolations();
}
