package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.LoanDeduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanDeductionRepository extends JpaRepository<LoanDeduction, UUID> {

    List<LoanDeduction> findByEmployeeLoanIdOrderByInstallmentNumber(UUID employeeLoanId);

    List<LoanDeduction> findByEmployeePayrollId(UUID employeePayrollId);

    List<LoanDeduction> findByPayrollPeriodId(UUID payrollPeriodId);

    @Query("SELECT l FROM LoanDeduction l WHERE l.employeeLoanId = :loanId ORDER BY l.installmentNumber DESC")
    List<LoanDeduction> findByLoanOrderByInstallmentDesc(@Param("loanId") UUID loanId);

    @Query("SELECT SUM(l.deductionAmount) FROM LoanDeduction l WHERE l.employeeLoanId = :loanId")
    Double getTotalDeductionByLoan(@Param("loanId") UUID loanId);

    @Query("SELECT SUM(l.deductionAmount) FROM LoanDeduction l WHERE l.payrollPeriodId = :periodId")
    Double getTotalDeductionByPeriod(@Param("periodId") UUID periodId);
}
