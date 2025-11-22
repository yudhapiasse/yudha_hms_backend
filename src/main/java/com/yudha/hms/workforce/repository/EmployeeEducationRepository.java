package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.EducationLevel;
import com.yudha.hms.workforce.entity.EmployeeEducation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeEducationRepository extends JpaRepository<EmployeeEducation, UUID> {

    List<EmployeeEducation> findByEmployeeId(UUID employeeId);

    List<EmployeeEducation> findByEmployeeIdOrderByGraduationDateDesc(UUID employeeId);

    Optional<EmployeeEducation> findByEmployeeIdAndIsHighestEducationTrue(UUID employeeId);

    List<EmployeeEducation> findByEducationLevel(EducationLevel educationLevel);

    @Query("SELECT e FROM EmployeeEducation e WHERE e.employeeId = :employeeId AND e.educationLevel = :level")
    List<EmployeeEducation> findByEmployeeAndLevel(@Param("employeeId") UUID employeeId, @Param("level") EducationLevel level);

    @Query("SELECT e FROM EmployeeEducation e WHERE e.institutionName LIKE %:keyword% OR e.fieldOfStudy LIKE %:keyword%")
    List<EmployeeEducation> searchByInstitutionOrField(@Param("keyword") String keyword);
}
