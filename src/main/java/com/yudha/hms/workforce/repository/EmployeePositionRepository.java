package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.PositionLevel;
import com.yudha.hms.workforce.entity.EmployeePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeePositionRepository extends JpaRepository<EmployeePosition, UUID> {

    Optional<EmployeePosition> findByPositionCode(String positionCode);

    List<EmployeePosition> findByDepartmentIdAndIsActiveTrue(UUID departmentId);

    List<EmployeePosition> findByParentPositionId(UUID parentPositionId);

    List<EmployeePosition> findByPositionLevel(PositionLevel positionLevel);

    List<EmployeePosition> findByRequiresStrTrueAndIsActiveTrue();

    List<EmployeePosition> findByRequiresSipTrueAndIsActiveTrue();

    List<EmployeePosition> findByIsActiveTrue();

    @Query("SELECT p FROM EmployeePosition p WHERE p.departmentId = :departmentId AND p.positionLevel = :positionLevel AND p.isActive = true")
    List<EmployeePosition> findByDepartmentAndLevel(@Param("departmentId") UUID departmentId, @Param("positionLevel") PositionLevel positionLevel);

    @Query("SELECT p FROM EmployeePosition p WHERE (p.requiresStr = true OR p.requiresSip = true) AND p.isActive = true")
    List<EmployeePosition> findPositionsRequiringLicense();
}
