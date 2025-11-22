package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.ShiftRotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRotationRepository extends JpaRepository<ShiftRotation, UUID> {

    Optional<ShiftRotation> findByRotationCode(String rotationCode);

    List<ShiftRotation> findByActiveTrue();

    List<ShiftRotation> findByDepartmentIdAndActiveTrue(UUID departmentId);

    List<ShiftRotation> findByDepartmentIdIsNullAndActiveTrue();
}
