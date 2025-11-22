package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tax_calculation", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class TaxCalculation extends SoftDeletableEntity {

    @Column(name = "employee_payroll_id", nullable = false)
    private UUID employeePayrollId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "payroll_period_id", nullable = false)
    private UUID payrollPeriodId;

    // Income
    @Column(name = "gross_income", precision = 15, scale = 2, nullable = false)
    private BigDecimal grossIncome;

    @Column(name = "non_taxable_income", precision = 15, scale = 2)
    private BigDecimal nonTaxableIncome = BigDecimal.ZERO;

    @Column(name = "taxable_income", precision = 15, scale = 2, nullable = false)
    private BigDecimal taxableIncome;

    // PTKP (Penghasilan Tidak Kena Pajak)
    @Column(name = "ptkp_status", length = 10, nullable = false)
    private String ptkpStatus;

    @Column(name = "ptkp_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal ptkpAmount;

    // PKP (Penghasilan Kena Pajak)
    @Column(name = "pkp_annual", precision = 15, scale = 2)
    private BigDecimal pkpAnnual;

    @Column(name = "pkp_monthly", precision = 15, scale = 2)
    private BigDecimal pkpMonthly;

    // Tax Brackets
    @Column(name = "tax_bracket_1", precision = 15, scale = 2)
    private BigDecimal taxBracket1 = BigDecimal.ZERO;

    @Column(name = "tax_bracket_2", precision = 15, scale = 2)
    private BigDecimal taxBracket2 = BigDecimal.ZERO;

    @Column(name = "tax_bracket_3", precision = 15, scale = 2)
    private BigDecimal taxBracket3 = BigDecimal.ZERO;

    @Column(name = "tax_bracket_4", precision = 15, scale = 2)
    private BigDecimal taxBracket4 = BigDecimal.ZERO;

    @Column(name = "tax_bracket_5", precision = 15, scale = 2)
    private BigDecimal taxBracket5 = BigDecimal.ZERO;

    // Tax Amounts
    @Column(name = "tax_amount_bracket_1", precision = 15, scale = 2)
    private BigDecimal taxAmountBracket1 = BigDecimal.ZERO;

    @Column(name = "tax_amount_bracket_2", precision = 15, scale = 2)
    private BigDecimal taxAmountBracket2 = BigDecimal.ZERO;

    @Column(name = "tax_amount_bracket_3", precision = 15, scale = 2)
    private BigDecimal taxAmountBracket3 = BigDecimal.ZERO;

    @Column(name = "tax_amount_bracket_4", precision = 15, scale = 2)
    private BigDecimal taxAmountBracket4 = BigDecimal.ZERO;

    @Column(name = "tax_amount_bracket_5", precision = 15, scale = 2)
    private BigDecimal taxAmountBracket5 = BigDecimal.ZERO;

    @Column(name = "total_annual_tax", precision = 15, scale = 2)
    private BigDecimal totalAnnualTax;

    @Column(name = "monthly_tax", precision = 15, scale = 2)
    private BigDecimal monthlyTax;

    // YTD (Year to Date)
    @Column(name = "ytd_gross_income", precision = 15, scale = 2)
    private BigDecimal ytdGrossIncome;

    @Column(name = "ytd_tax_paid", precision = 15, scale = 2)
    private BigDecimal ytdTaxPaid;

    @Column(name = "calculation_method", length = 30, nullable = false)
    private String calculationMethod;

    @Type(JsonBinaryType.class)
    @Column(name = "calculation_details", columnDefinition = "jsonb")
    private Map<String, Object> calculationDetails;
}
