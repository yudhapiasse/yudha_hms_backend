package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.EmploymentStatus;
import com.yudha.hms.workforce.constant.EmploymentType;
import com.yudha.hms.workforce.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByNik(String nik);

    List<Employee> findByDepartmentIdAndIsActiveTrue(UUID departmentId);

    List<Employee> findByPositionIdAndIsActiveTrue(UUID positionId);

    List<Employee> findByEmploymentStatusAndIsActiveTrue(EmploymentStatus employmentStatus);

    List<Employee> findByEmploymentTypeAndIsActiveTrue(EmploymentType employmentType);

    List<Employee> findByIsActiveTrue();

    @Query("SELECT e FROM Employee e WHERE e.employmentStatus = :status AND e.contractEndDate BETWEEN :startDate AND :endDate")
    List<Employee> findContractsExpiringBetween(@Param("status") EmploymentStatus status, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM Employee e WHERE e.departmentId = :departmentId AND e.employmentStatus = :status AND e.isActive = true")
    List<Employee> findByDepartmentAndStatus(@Param("departmentId") UUID departmentId, @Param("status") EmploymentStatus status);

    @Query("SELECT e FROM Employee e WHERE e.fullName LIKE %:keyword% OR e.employeeNumber LIKE %:keyword% OR e.nik LIKE %:keyword%")
    List<Employee> searchEmployees(@Param("keyword") String keyword);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.departmentId = :departmentId AND e.isActive = true")
    Long countActiveByDepartment(@Param("departmentId") UUID departmentId);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employmentStatus = :status")
    Long countByStatus(@Param("status") EmploymentStatus status);
}
