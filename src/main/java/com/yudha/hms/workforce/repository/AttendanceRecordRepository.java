package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.AttendanceStatus;
import com.yudha.hms.workforce.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, UUID> {

    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(UUID employeeId, LocalDate attendanceDate);

    List<AttendanceRecord> findByEmployeeIdAndAttendanceDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecord> findByDepartmentIdAndAttendanceDate(UUID departmentId, LocalDate attendanceDate);

    List<AttendanceRecord> findByDepartmentIdAndAttendanceDateBetween(UUID departmentId, LocalDate startDate, LocalDate endDate);

    List<AttendanceRecord> findByAttendanceStatus(AttendanceStatus status);

    List<AttendanceRecord> findByEmployeeIdAndAttendanceStatus(UUID employeeId, AttendanceStatus status);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employeeId = :employeeId AND ar.attendanceDate BETWEEN :startDate AND :endDate AND ar.attendanceStatus = :status")
    List<AttendanceRecord> findByEmployeeDateRangeAndStatus(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") AttendanceStatus status);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.attendanceDate BETWEEN :startDate AND :endDate AND ar.isLate = true")
    List<AttendanceRecord> findLateAttendances(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.employeeId = :employeeId AND ar.attendanceDate BETWEEN :startDate AND :endDate AND ar.overtimeHours > 0")
    List<AttendanceRecord> findOvertimeAttendances(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(ar) FROM AttendanceRecord ar WHERE ar.employeeId = :employeeId AND ar.attendanceDate BETWEEN :startDate AND :endDate AND ar.attendanceStatus = :status")
    Long countByEmployeeDateRangeAndStatus(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") AttendanceStatus status);

    @Query("SELECT ar FROM AttendanceRecord ar WHERE ar.attendanceDate = :date AND ar.isValid = false")
    List<AttendanceRecord> findInvalidAttendances(@Param("date") LocalDate date);
}
