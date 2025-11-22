package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.CompletionStatus;
import com.yudha.hms.workforce.constant.TrainingType;
import com.yudha.hms.workforce.entity.EmployeeTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeTrainingRepository extends JpaRepository<EmployeeTraining, UUID> {

    List<EmployeeTraining> findByEmployeeId(UUID employeeId);

    List<EmployeeTraining> findByEmployeeIdOrderByStartDateDesc(UUID employeeId);

    List<EmployeeTraining> findByEmployeeIdAndCompletionStatus(UUID employeeId, CompletionStatus completionStatus);

    List<EmployeeTraining> findByTrainingType(TrainingType trainingType);

    List<EmployeeTraining> findByCompletionStatus(CompletionStatus completionStatus);

    @Query("SELECT t FROM EmployeeTraining t WHERE t.employeeId = :employeeId AND t.startDate BETWEEN :startDate AND :endDate")
    List<EmployeeTraining> findByEmployeeAndDateRange(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT t FROM EmployeeTraining t WHERE t.certificateExpiryDate BETWEEN :startDate AND :endDate")
    List<EmployeeTraining> findCertificatesExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.skpCredits) FROM EmployeeTraining t WHERE t.employeeId = :employeeId AND t.completionStatus = 'COMPLETED' AND t.startDate >= :fromDate")
    BigDecimal sumSkpCreditsByEmployeeSinceDate(@Param("employeeId") UUID employeeId, @Param("fromDate") LocalDate fromDate);

    @Query("SELECT COUNT(t) FROM EmployeeTraining t WHERE t.employeeId = :employeeId AND t.completionStatus = 'COMPLETED'")
    Long countCompletedTrainingsByEmployee(@Param("employeeId") UUID employeeId);
}
