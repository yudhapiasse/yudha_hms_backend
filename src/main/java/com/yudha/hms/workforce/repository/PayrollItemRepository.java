package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.PayrollComponentType;
import com.yudha.hms.workforce.entity.PayrollItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollItemRepository extends JpaRepository<PayrollItem, UUID> {

    List<PayrollItem> findByEmployeePayrollId(UUID employeePayrollId);

    List<PayrollItem> findByEmployeePayrollIdAndComponentType(UUID employeePayrollId, PayrollComponentType componentType);

    List<PayrollItem> findByPayrollComponentId(UUID payrollComponentId);

    @Query("SELECT p FROM PayrollItem p WHERE p.employeePayrollId = :payrollId AND p.isTaxable = true")
    List<PayrollItem> findTaxableByPayrollId(@Param("payrollId") UUID payrollId);

    @Query("SELECT SUM(p.amount) FROM PayrollItem p WHERE p.employeePayrollId = :payrollId AND p.componentType = :type")
    Double getTotalAmountByTypeAndPayroll(@Param("payrollId") UUID payrollId, @Param("type") PayrollComponentType type);
}
