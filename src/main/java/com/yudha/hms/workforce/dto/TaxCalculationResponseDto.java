package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for tax calculation details.
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
public class TaxCalculationResponseDto {

    /**
     * Tax calculation ID
     */
    private UUID id;

    /**
     * Employee payroll ID
     */
    private UUID employeePayrollId;

    /**
     * Employee ID
     */
    private UUID employeeId;

    /**
     * Employee name (denormalized)
     */
    private String employeeName;

    /**
     * Payroll period ID
     */
    private UUID payrollPeriodId;

    /**
     * Gross income (before tax)
     */
    private BigDecimal grossIncome;

    /**
     * Non-taxable income (if any)
     */
    private BigDecimal nonTaxableIncome;

    /**
     * Taxable income
     */
    private BigDecimal taxableIncome;

    /**
     * PTKP status code (e.g., "TK/0", "K/1")
     */
    private String ptkpStatus;

    /**
     * PTKP amount (tax-free income)
     */
    private BigDecimal ptkpAmount;

    /**
     * PKP Annual (Penghasilan Kena Pajak - annual taxable income)
     */
    private BigDecimal pkpAnnual;

    /**
     * PKP Monthly (monthly taxable income)
     */
    private BigDecimal pkpMonthly;

    /**
     * Tax bracket 1 amount (0-60M at 5%)
     */
    private BigDecimal taxBracket1;

    /**
     * Tax bracket 2 amount (60M-250M at 15%)
     */
    private BigDecimal taxBracket2;

    /**
     * Tax bracket 3 amount (250M-500M at 25%)
     */
    private BigDecimal taxBracket3;

    /**
     * Tax bracket 4 amount (500M-5B at 30%)
     */
    private BigDecimal taxBracket4;

    /**
     * Tax bracket 5 amount (>5B at 35%)
     */
    private BigDecimal taxBracket5;

    /**
     * Total annual tax
     */
    private BigDecimal totalAnnualTax;

    /**
     * Monthly tax deduction
     */
    private BigDecimal monthlyTax;

    /**
     * Year-to-date gross income
     */
    private BigDecimal ytdGrossIncome;

    /**
     * Year-to-date tax paid
     */
    private BigDecimal ytdTaxPaid;

    /**
     * Detailed calculation breakdown (stored as JSONB)
     */
    private Map<String, Object> calculationDetails;

    /**
     * Created at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Updated at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
