package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.EmployeeShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeShiftAssignmentRepository extends JpaRepository<EmployeeShiftAssignment, UUID> {

    Optional<EmployeeShiftAssignment> findByEmployeeIdAndIsCurrentTrue(UUID employeeId);

    List<EmployeeShiftAssignment> findByEmployeeId(UUID employeeId);

    List<EmployeeShiftAssignment> findByShiftRotationId(UUID shiftRotationId);

    List<EmployeeShiftAssignment> findByFixedShiftPatternId(UUID fixedShiftPatternId);

    @Query("SELECT esa FROM EmployeeShiftAssignment esa WHERE esa.employeeId = :employeeId AND :date BETWEEN esa.effectiveFrom AND COALESCE(esa.effectiveTo, :date)")
    Optional<EmployeeShiftAssignment> findActiveAssignmentForDate(@Param("employeeId") UUID employeeId, @Param("date") LocalDate date);

    @Query("SELECT esa FROM EmployeeShiftAssignment esa WHERE esa.shiftRotationId = :rotationId AND esa.isCurrent = true")
    List<EmployeeShiftAssignment> findCurrentByRotation(@Param("rotationId") UUID rotationId);
}
