package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for payroll period summary.
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
public class PayrollSummaryResponseDto {

    /**
     * Payroll period ID
     */
    private UUID payrollPeriodId;

    /**
     * Payroll period code
     */
    private String payrollPeriodCode;

    /**
     * Payroll period name
     */
    private String payrollPeriodName;

    /**
     * Total number of employees
     */
    private Integer employeeCount;

    /**
     * Number of employees calculated
     */
    private Integer calculatedCount;

    /**
     * Number of employees pending
     */
    private Integer pendingCount;

    /**
     * Number of employees approved
     */
    private Integer approvedCount;

    /**
     * Number of employees paid
     */
    private Integer paidCount;

    // === Financial Summary ===

    /**
     * Total basic salary
     */
    private BigDecimal totalBasicSalary;

    /**
     * Total allowances
     */
    private BigDecimal totalAllowances;

    /**
     * Total overtime pay
     */
    private BigDecimal totalOvertimePay;

    /**
     * Total THR amount
     */
    private BigDecimal totalThrAmount;

    /**
     * Total incentives
     */
    private BigDecimal totalIncentives;

    /**
     * Total gross salary
     */
    private BigDecimal totalGrossSalary;

    /**
     * Total BPJS deductions
     */
    private BigDecimal totalBpjsDeductions;

    /**
     * Total PPh 21 tax
     */
    private BigDecimal totalTax;

    /**
     * Total loan deductions
     */
    private BigDecimal totalLoanDeductions;

    /**
     * Total other deductions
     */
    private BigDecimal totalOtherDeductions;

    /**
     * Total all deductions
     */
    private BigDecimal totalDeductions;

    /**
     * Total net salary (to be paid out)
     */
    private BigDecimal totalNetSalary;

    /**
     * Total paid amount (already processed)
     */
    private BigDecimal totalPaidAmount;

    /**
     * Total unpaid amount (pending payment)
     */
    private BigDecimal totalUnpaidAmount;
}
