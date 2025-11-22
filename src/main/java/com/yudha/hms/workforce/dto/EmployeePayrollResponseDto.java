package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yudha.hms.workforce.constant.PaymentMethod;
import com.yudha.hms.workforce.constant.PaymentStatus;
import com.yudha.hms.workforce.constant.PayrollPeriodStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for employee payroll details.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeePayrollResponseDto {

    /**
     * Employee payroll ID
     */
    private UUID id;

    /**
     * Employee ID
     */
    private UUID employeeId;

    /**
     * Employee name (denormalized)
     */
    private String employeeName;

    /**
     * Employee number (denormalized)
     */
    private String employeeNumber;

    /**
     * Department ID
     */
    private UUID departmentId;

    /**
     * Department name (denormalized)
     */
    private String departmentName;

    /**
     * Position ID
     */
    private UUID positionId;

    /**
     * Position name (denormalized)
     */
    private String positionName;

    /**
     * Employment type
     */
    private String employmentType;

    /**
     * Payroll period ID
     */
    private UUID payrollPeriodId;

    /**
     * Payroll period name (denormalized)
     */
    private String payrollPeriodName;

    /**
     * Payroll number
     */
    private String payrollNumber;

    /**
     * Working days in the period
     */
    private Integer workingDays;

    /**
     * Actual working days (attendance)
     */
    private Integer actualWorkingDays;

    /**
     * Absent days
     */
    private Integer absentDays;

    // === Earnings ===

    /**
     * Basic salary
     */
    private BigDecimal basicSalary;

    /**
     * Total allowances
     */
    private BigDecimal totalAllowances;

    /**
     * Normal overtime hours
     */
    private BigDecimal normalOvertimeHours;

    /**
     * Weekend overtime hours
     */
    private BigDecimal weekendOvertimeHours;

    /**
     * Holiday overtime hours
     */
    private BigDecimal holidayOvertimeHours;

    /**
     * Total overtime pay
     */
    private BigDecimal totalOvertime;

    /**
     * THR (holiday allowance) amount
     */
    private BigDecimal thrAmount;

    /**
     * Total incentives
     */
    private BigDecimal totalIncentives;

    /**
     * Gross salary (before deductions)
     */
    private BigDecimal grossSalary;

    // === Deductions ===

    /**
     * BPJS Kesehatan employee contribution
     */
    private BigDecimal bpjsKesehatanEmployee;

    /**
     * BPJS Kesehatan family contribution
     */
    private BigDecimal bpjsKesehatanFamily;

    /**
     * BPJS TK JHT (old age security)
     */
    private BigDecimal bpjsTkJht;

    /**
     * BPJS TK JP (pension)
     */
    private BigDecimal bpjsTkJp;

    /**
     * PPh 21 (income tax)
     */
    private BigDecimal pph21Amount;

    /**
     * Loan deduction
     */
    private BigDecimal loanDeduction;

    /**
     * Other deductions
     */
    private BigDecimal otherDeductions;

    /**
     * Total deductions
     */
    private BigDecimal totalDeductions;

    /**
     * Net salary (take-home pay)
     */
    private BigDecimal netSalary;

    // === Payment Info ===

    /**
     * Payroll status
     */
    private PayrollPeriodStatus status;

    /**
     * Payment status
     */
    private PaymentStatus paymentStatus;

    /**
     * Payment method
     */
    private PaymentMethod paymentMethod;

    /**
     * Payment date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime paymentDate;

    /**
     * Payment reference number
     */
    private String paymentReference;

    /**
     * Bank account number (last 4 digits)
     */
    private String bankAccountLastDigits;

    /**
     * Created at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
