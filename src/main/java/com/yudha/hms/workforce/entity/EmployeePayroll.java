package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.PaymentMethod;
import com.yudha.hms.workforce.constant.PaymentStatus;
import com.yudha.hms.workforce.constant.PayrollPeriodStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employee_payroll", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class EmployeePayroll extends SoftDeletableEntity {

    @Column(name = "payroll_period_id", nullable = false)
    private UUID payrollPeriodId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "payroll_number", length = 50, nullable = false, unique = true)
    private String payrollNumber;

    // Employee Reference Fields
    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "position_id")
    private UUID positionId;

    @Column(name = "employment_type", length = 20)
    private String employmentType;

    // Attendance Summary
    @Column(name = "working_days", nullable = false)
    private Integer workingDays = 0;

    @Column(name = "actual_working_days", nullable = false)
    private Integer actualWorkingDays = 0;

    @Column(name = "absent_days", nullable = false)
    private Integer absentDays = 0;

    @Column(name = "leave_days", nullable = false)
    private Integer leaveDays = 0;

    @Column(name = "unpaid_leave_days", nullable = false)
    private Integer unpaidLeaveDays = 0;

    // Overtime Summary
    @Column(name = "normal_overtime_hours", precision = 8, scale = 2)
    private BigDecimal normalOvertimeHours = BigDecimal.ZERO;

    @Column(name = "weekend_overtime_hours", precision = 8, scale = 2)
    private BigDecimal weekendOvertimeHours = BigDecimal.ZERO;

    @Column(name = "holiday_overtime_hours", precision = 8, scale = 2)
    private BigDecimal holidayOvertimeHours = BigDecimal.ZERO;

    // Gross Components
    @Column(name = "basic_salary", precision = 15, scale = 2, nullable = false)
    private BigDecimal basicSalary;

    @Column(name = "total_allowances", precision = 15, scale = 2)
    private BigDecimal totalAllowances = BigDecimal.ZERO;

    @Column(name = "total_overtime", precision = 15, scale = 2)
    private BigDecimal totalOvertime = BigDecimal.ZERO;

    @Column(name = "total_shift_differential", precision = 15, scale = 2)
    private BigDecimal totalShiftDifferential = BigDecimal.ZERO;

    @Column(name = "total_incentives", precision = 15, scale = 2)
    private BigDecimal totalIncentives = BigDecimal.ZERO;

    @Column(name = "total_bonuses", precision = 15, scale = 2)
    private BigDecimal totalBonuses = BigDecimal.ZERO;

    @Column(name = "thr_amount", precision = 15, scale = 2)
    private BigDecimal thrAmount = BigDecimal.ZERO;

    @Column(name = "gross_salary", precision = 15, scale = 2, nullable = false)
    private BigDecimal grossSalary;

    // Deductions
    @Column(name = "bpjs_kesehatan_employee", precision = 15, scale = 2)
    private BigDecimal bpjsKesehatanEmployee = BigDecimal.ZERO;

    @Column(name = "bpjs_kesehatan_family", precision = 15, scale = 2)
    private BigDecimal bpjsKesehatanFamily = BigDecimal.ZERO;

    @Column(name = "bpjs_tk_jht", precision = 15, scale = 2)
    private BigDecimal bpjsTkJht = BigDecimal.ZERO;

    @Column(name = "bpjs_tk_jp", precision = 15, scale = 2)
    private BigDecimal bpjsTkJp = BigDecimal.ZERO;

    @Column(name = "total_bpjs_deduction", precision = 15, scale = 2)
    private BigDecimal totalBpjsDeduction = BigDecimal.ZERO;

    @Column(name = "pph21_amount", precision = 15, scale = 2)
    private BigDecimal pph21Amount = BigDecimal.ZERO;

    @Column(name = "loan_deduction", precision = 15, scale = 2)
    private BigDecimal loanDeduction = BigDecimal.ZERO;

    @Column(name = "advance_deduction", precision = 15, scale = 2)
    private BigDecimal advanceDeduction = BigDecimal.ZERO;

    @Column(name = "other_deductions", precision = 15, scale = 2)
    private BigDecimal otherDeductions = BigDecimal.ZERO;

    @Column(name = "total_deductions", precision = 15, scale = 2)
    private BigDecimal totalDeductions = BigDecimal.ZERO;

    // Net Pay
    @Column(name = "net_salary", precision = 15, scale = 2, nullable = false)
    private BigDecimal netSalary;

    // Tax Calculation Details
    @Column(name = "taxable_income", precision = 15, scale = 2)
    private BigDecimal taxableIncome = BigDecimal.ZERO;

    @Column(name = "non_taxable_income", precision = 15, scale = 2)
    private BigDecimal nonTaxableIncome = BigDecimal.ZERO;

    @Column(name = "ptkp_status", length = 10)
    private String ptkpStatus;

    @Column(name = "ptkp_amount", precision = 15, scale = 2)
    private BigDecimal ptkpAmount = BigDecimal.ZERO;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private PayrollPeriodStatus status = PayrollPeriodStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod = PaymentMethod.BANK_TRANSFER;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Bank Details
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_account_holder_name", length = 200)
    private String bankAccountHolderName;

    // Approval
    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Helper methods
    public void setOvertimeHours(java.math.BigDecimal overtimeHours) {
        this.normalOvertimeHours = overtimeHours;
    }

    public java.math.BigDecimal getOvertimeHours() {
        return this.normalOvertimeHours
                .add(this.weekendOvertimeHours)
                .add(this.holidayOvertimeHours);
    }
}
