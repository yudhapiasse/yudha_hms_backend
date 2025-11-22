package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.TaxCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaxCalculationRepository extends JpaRepository<TaxCalculation, UUID> {

    Optional<TaxCalculation> findByEmployeePayrollId(UUID employeePayrollId);

    List<TaxCalculation> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    List<TaxCalculation> findByPayrollPeriodId(UUID payrollPeriodId);

    @Query("SELECT t FROM TaxCalculation t WHERE t.employeeId = :employeeId AND t.payrollPeriodId = :periodId")
    Optional<TaxCalculation> findByEmployeeAndPeriod(@Param("employeeId") UUID employeeId, @Param("periodId") UUID periodId);

    @Query("SELECT SUM(t.monthlyTax) FROM TaxCalculation t WHERE t.payrollPeriodId = :periodId")
    Double getTotalTaxByPeriod(@Param("periodId") UUID periodId);

    @Query("SELECT SUM(t.ytdTaxPaid) FROM TaxCalculation t WHERE t.employeeId = :employeeId")
    Double getYtdTaxByEmployee(@Param("employeeId") UUID employeeId);
}
