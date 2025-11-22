package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.OnCallType;
import com.yudha.hms.workforce.constant.RosterStatus;
import com.yudha.hms.workforce.entity.OnCallSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OnCallScheduleRepository extends JpaRepository<OnCallSchedule, UUID> {

    List<OnCallSchedule> findByEmployeeIdAndOnCallDate(UUID employeeId, LocalDate onCallDate);

    List<OnCallSchedule> findByEmployeeIdAndOnCallDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

    List<OnCallSchedule> findByDepartmentIdAndOnCallDate(UUID departmentId, LocalDate onCallDate);

    List<OnCallSchedule> findByDepartmentIdAndOnCallDateBetween(UUID departmentId, LocalDate startDate, LocalDate endDate);

    List<OnCallSchedule> findByOnCallDateAndStatus(LocalDate onCallDate, RosterStatus status);

    @Query("SELECT ocs FROM OnCallSchedule ocs WHERE ocs.departmentId = :departmentId AND ocs.onCallDate = :date AND ocs.onCallType = :type AND ocs.status = :status")
    List<OnCallSchedule> findByDepartmentDateTypeAndStatus(@Param("departmentId") UUID departmentId, @Param("date") LocalDate date, @Param("type") OnCallType type, @Param("status") RosterStatus status);

    @Query("SELECT ocs FROM OnCallSchedule ocs WHERE ocs.onCallDate BETWEEN :startDate AND :endDate AND ocs.wasCalledOut = true")
    List<OnCallSchedule> findCallOuts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(ocs) FROM OnCallSchedule ocs WHERE ocs.employeeId = :employeeId AND ocs.onCallDate BETWEEN :startDate AND :endDate")
    Long countOnCallDays(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
