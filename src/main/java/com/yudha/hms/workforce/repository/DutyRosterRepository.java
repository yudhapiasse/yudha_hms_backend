package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.RosterStatus;
import com.yudha.hms.workforce.entity.DutyRoster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DutyRosterRepository extends JpaRepository<DutyRoster, UUID> {

    List<DutyRoster> findByEmployeeIdAndRosterDate(UUID employeeId, LocalDate rosterDate);

    List<DutyRoster> findByEmployeeIdAndRosterDateBetween(UUID employeeId, LocalDate startDate, LocalDate endDate);

    List<DutyRoster> findByDepartmentIdAndRosterDate(UUID departmentId, LocalDate rosterDate);

    List<DutyRoster> findByDepartmentIdAndRosterDateBetween(UUID departmentId, LocalDate startDate, LocalDate endDate);

    List<DutyRoster> findByRosterDateAndStatus(LocalDate rosterDate, RosterStatus status);

    Optional<DutyRoster> findByEmployeeIdAndRosterDateAndShiftPatternId(UUID employeeId, LocalDate rosterDate, UUID shiftPatternId);

    @Query("SELECT dr FROM DutyRoster dr WHERE dr.departmentId = :departmentId AND dr.rosterDate = :date AND dr.status = :status")
    List<DutyRoster> findByDepartmentDateAndStatus(@Param("departmentId") UUID departmentId, @Param("date") LocalDate date, @Param("status") RosterStatus status);

    @Query("SELECT dr FROM DutyRoster dr WHERE dr.rosterDate BETWEEN :startDate AND :endDate AND dr.published = false")
    List<DutyRoster> findUnpublishedRosters(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT dr FROM DutyRoster dr WHERE dr.rosterDate BETWEEN :startDate AND :endDate AND dr.approved = false AND dr.status = :status")
    List<DutyRoster> findUnapprovedRosters(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") RosterStatus status);

    @Query("SELECT COUNT(dr) FROM DutyRoster dr WHERE dr.employeeId = :employeeId AND dr.rosterDate BETWEEN :startDate AND :endDate")
    Long countEmployeeRosterDays(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
