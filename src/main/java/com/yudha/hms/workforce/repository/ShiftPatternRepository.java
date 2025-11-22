package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.ShiftType;
import com.yudha.hms.workforce.entity.ShiftPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftPatternRepository extends JpaRepository<ShiftPattern, UUID> {

    Optional<ShiftPattern> findByShiftCode(String shiftCode);

    List<ShiftPattern> findByActiveTrue();

    List<ShiftPattern> findByShiftType(ShiftType shiftType);

    List<ShiftPattern> findByDepartmentIdAndActiveTrue(UUID departmentId);

    List<ShiftPattern> findByDepartmentIdIsNullAndActiveTrue();

    Optional<ShiftPattern> findByIsDefaultTrueAndDepartmentIdIsNull();

    @Query("SELECT sp FROM ShiftPattern sp WHERE sp.departmentId = :departmentId AND sp.shiftType = :shiftType AND sp.active = true")
    List<ShiftPattern> findByDepartmentAndType(@Param("departmentId") UUID departmentId, @Param("shiftType") ShiftType shiftType);
}
