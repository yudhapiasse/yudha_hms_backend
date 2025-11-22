package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.PaymentStatus;
import com.yudha.hms.workforce.constant.PayrollPeriodStatus;
import com.yudha.hms.workforce.entity.EmployeePayroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeePayrollRepository extends JpaRepository<EmployeePayroll, UUID> {

    Optional<EmployeePayroll> findByPayrollNumber(String payrollNumber);

    List<EmployeePayroll> findByPayrollPeriodIdOrderByEmployeeId(UUID payrollPeriodId);

    List<EmployeePayroll> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    Optional<EmployeePayroll> findByPayrollPeriodIdAndEmployeeId(UUID payrollPeriodId, UUID employeeId);

    Optional<EmployeePayroll> findByEmployeeIdAndPayrollPeriodId(UUID employeeId, UUID payrollPeriodId);

    List<EmployeePayroll> findByPayrollPeriodId(UUID payrollPeriodId);

    List<EmployeePayroll> findByPayrollPeriodIdAndStatus(UUID payrollPeriodId, PayrollPeriodStatus status);

    List<EmployeePayroll> findByPayrollPeriodIdAndPaymentStatus(UUID payrollPeriodId, PaymentStatus paymentStatus);

    @Query("SELECT e FROM EmployeePayroll e WHERE e.payrollPeriodId = :periodId AND e.status = :status ORDER BY e.netSalary DESC")
    List<EmployeePayroll> findByPeriodAndStatusOrderByNetSalary(@Param("periodId") UUID periodId, @Param("status") PayrollPeriodStatus status);

    @Query("SELECT SUM(e.netSalary) FROM EmployeePayroll e WHERE e.payrollPeriodId = :periodId")
    Double getTotalNetSalaryByPeriod(@Param("periodId") UUID periodId);

    @Query("SELECT COUNT(e) FROM EmployeePayroll e WHERE e.payrollPeriodId = :periodId AND e.status = :status")
    Long countByPeriodAndStatus(@Param("periodId") UUID periodId, @Param("status") PayrollPeriodStatus status);
}
