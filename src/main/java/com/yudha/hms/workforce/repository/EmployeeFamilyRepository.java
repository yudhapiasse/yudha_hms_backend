package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.FamilyRelationship;
import com.yudha.hms.workforce.entity.EmployeeFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeFamilyRepository extends JpaRepository<EmployeeFamily, UUID> {

    List<EmployeeFamily> findByEmployeeId(UUID employeeId);

    List<EmployeeFamily> findByEmployeeIdAndRelationship(UUID employeeId, FamilyRelationship relationship);

    List<EmployeeFamily> findByEmployeeIdAndIsDependentTrue(UUID employeeId);

    List<EmployeeFamily> findByEmployeeIdAndIsEmergencyContactTrue(UUID employeeId);

    List<EmployeeFamily> findByEmployeeIdAndCoveredByHealthInsuranceTrue(UUID employeeId);

    @Query("SELECT f FROM EmployeeFamily f WHERE f.employeeId = :employeeId AND f.isDependent = true")
    List<EmployeeFamily> findDependentsByEmployee(@Param("employeeId") UUID employeeId);

    @Query("SELECT COUNT(f) FROM EmployeeFamily f WHERE f.employeeId = :employeeId AND f.coveredByHealthInsurance = true")
    Long countBpjsCoveredMembers(@Param("employeeId") UUID employeeId);
}
