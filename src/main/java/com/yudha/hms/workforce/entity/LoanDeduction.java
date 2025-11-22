package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "loan_deduction", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class LoanDeduction extends SoftDeletableEntity {

    @Column(name = "employee_loan_id", nullable = false)
    private UUID employeeLoanId;

    @Column(name = "employee_payroll_id", nullable = false)
    private UUID employeePayrollId;

    @Column(name = "payroll_period_id", nullable = false)
    private UUID payrollPeriodId;

    @Column(name = "installment_number", nullable = false)
    private Integer installmentNumber;

    @Column(name = "deduction_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal deductionAmount;

    @Column(name = "principal_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal principalAmount;

    @Column(name = "interest_amount", precision = 15, scale = 2)
    private BigDecimal interestAmount = BigDecimal.ZERO;

    @Column(name = "outstanding_before", precision = 15, scale = 2)
    private BigDecimal outstandingBefore;

    @Column(name = "outstanding_after", precision = 15, scale = 2)
    private BigDecimal outstandingAfter;
}
