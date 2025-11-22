package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalarySlipRepository extends JpaRepository<SalarySlip, UUID> {

    Optional<SalarySlip> findBySlipNumber(String slipNumber);

    Optional<SalarySlip> findByEmployeePayrollId(UUID employeePayrollId);

    List<SalarySlip> findByEmployeeIdOrderBySlipDateDesc(UUID employeeId);

    List<SalarySlip> findByPayrollPeriodId(UUID payrollPeriodId);

    @Query("SELECT s FROM SalarySlip s WHERE s.employeeId = :employeeId AND s.isSent = false")
    List<SalarySlip> findUnsentByEmployee(@Param("employeeId") UUID employeeId);

    @Query("SELECT s FROM SalarySlip s WHERE s.payrollPeriodId = :periodId AND s.isSent = false")
    List<SalarySlip> findUnsentByPeriod(@Param("periodId") UUID periodId);

    @Query("SELECT COUNT(s) FROM SalarySlip s WHERE s.payrollPeriodId = :periodId AND s.isSent = true")
    Long countSentByPeriod(@Param("periodId") UUID periodId);
}
