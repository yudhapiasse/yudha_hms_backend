package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.PtkpStatus;
import com.yudha.hms.workforce.entity.TaxCalculation;
import com.yudha.hms.workforce.repository.TaxCalculationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for calculating Indonesian PPh 21 (Income Tax Article 21) based on Indonesian tax law.
 * Implements progressive tax calculation with 5 brackets and PTKP (tax-free income) deductions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IndonesianTaxCalculatorService {

    private final TaxCalculationRepository taxCalculationRepository;

    // Indonesian Tax Brackets (Annual - in IDR)
    private static final BigDecimal BRACKET_1_LIMIT = new BigDecimal("60000000");    // 0-60M: 5%
    private static final BigDecimal BRACKET_2_LIMIT = new BigDecimal("250000000");   // 60M-250M: 15%
    private static final BigDecimal BRACKET_3_LIMIT = new BigDecimal("500000000");   // 250M-500M: 25%
    private static final BigDecimal BRACKET_4_LIMIT = new BigDecimal("5000000000");  // 500M-5B: 30%
    // Bracket 5: >5B: 35%

    // Tax Rates
    private static final BigDecimal RATE_1 = new BigDecimal("0.05");  // 5%
    private static final BigDecimal RATE_2 = new BigDecimal("0.15");  // 15%
    private static final BigDecimal RATE_3 = new BigDecimal("0.25");  // 25%
    private static final BigDecimal RATE_4 = new BigDecimal("0.30");  // 30%
    private static final BigDecimal RATE_5 = new BigDecimal("0.35");  // 35%

    // PTKP Amounts (Annual - in IDR) - 2024 values
    private static final Map<PtkpStatus, BigDecimal> PTKP_AMOUNTS = new HashMap<>();

    static {
        // Base PTKP for taxpayer
        BigDecimal BASE_PTKP = new BigDecimal("54000000");  // 54 million
        BigDecimal MARRIED_ADDITION = new BigDecimal("4500000");  // 4.5 million for spouse
        BigDecimal DEPENDENT_ADDITION = new BigDecimal("4500000");  // 4.5 million per dependent (max 3)

        // TK (Tidak Kawin - Not Married)
        PTKP_AMOUNTS.put(PtkpStatus.TK_0, BASE_PTKP);
        PTKP_AMOUNTS.put(PtkpStatus.TK_1, BASE_PTKP.add(DEPENDENT_ADDITION));
        PTKP_AMOUNTS.put(PtkpStatus.TK_2, BASE_PTKP.add(DEPENDENT_ADDITION.multiply(new BigDecimal("2"))));
        PTKP_AMOUNTS.put(PtkpStatus.TK_3, BASE_PTKP.add(DEPENDENT_ADDITION.multiply(new BigDecimal("3"))));

        // K (Kawin - Married)
        PTKP_AMOUNTS.put(PtkpStatus.K_0, BASE_PTKP.add(MARRIED_ADDITION));
        PTKP_AMOUNTS.put(PtkpStatus.K_1, BASE_PTKP.add(MARRIED_ADDITION).add(DEPENDENT_ADDITION));
        PTKP_AMOUNTS.put(PtkpStatus.K_2, BASE_PTKP.add(MARRIED_ADDITION).add(DEPENDENT_ADDITION.multiply(new BigDecimal("2"))));
        PTKP_AMOUNTS.put(PtkpStatus.K_3, BASE_PTKP.add(MARRIED_ADDITION).add(DEPENDENT_ADDITION.multiply(new BigDecimal("3"))));

        // K/I (Kawin Istri Kerja - Married with Working Spouse)
        PTKP_AMOUNTS.put(PtkpStatus.K_I_0, BASE_PTKP);  // Spouse PTKP calculated separately
        PTKP_AMOUNTS.put(PtkpStatus.K_I_1, BASE_PTKP.add(DEPENDENT_ADDITION));
        PTKP_AMOUNTS.put(PtkpStatus.K_I_2, BASE_PTKP.add(DEPENDENT_ADDITION.multiply(new BigDecimal("2"))));
        PTKP_AMOUNTS.put(PtkpStatus.K_I_3, BASE_PTKP.add(DEPENDENT_ADDITION.multiply(new BigDecimal("3"))));
    }

    /**
     * Calculate PPh 21 tax for an employee based on gross income and PTKP status.
     *
     * @param employeePayrollId UUID of the employee payroll record
     * @param employeeId UUID of the employee
     * @param payrollPeriodId UUID of the payroll period
     * @param grossAnnualIncome Annual gross income (before tax)
     * @param ptkpStatus PTKP status (marital status and dependents)
     * @param ytdGrossIncome Year-to-date gross income
     * @param ytdTaxPaid Year-to-date tax already paid
     * @return TaxCalculation entity with calculated tax
     */
    @Transactional
    public TaxCalculation calculatePph21(
            UUID employeePayrollId,
            UUID employeeId,
            UUID payrollPeriodId,
            BigDecimal grossAnnualIncome,
            PtkpStatus ptkpStatus,
            BigDecimal ytdGrossIncome,
            BigDecimal ytdTaxPaid) {

        log.info("Calculating PPh 21 for employee {} with PTKP status {}", employeeId, ptkpStatus);

        // Get PTKP amount based on status
        BigDecimal ptkpAmount = getPtkpAmount(ptkpStatus);

        // Calculate PKP (Penghasilan Kena Pajak - Taxable Income)
        BigDecimal pkpAnnual = grossAnnualIncome.subtract(ptkpAmount);
        if (pkpAnnual.compareTo(BigDecimal.ZERO) < 0) {
            pkpAnnual = BigDecimal.ZERO;
        }

        // Round down to nearest 1000 (Indonesian tax regulation)
        pkpAnnual = pkpAnnual.divide(new BigDecimal("1000"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("1000"));

        // Calculate progressive tax
        Map<String, BigDecimal> taxBreakdown = calculateProgressiveTax(pkpAnnual);

        // Calculate total annual tax
        BigDecimal totalAnnualTax = taxBreakdown.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate monthly tax
        BigDecimal monthlyTax = totalAnnualTax.divide(new BigDecimal("12"), 0, RoundingMode.HALF_UP);

        // Calculate PKP monthly
        BigDecimal pkpMonthly = pkpAnnual.divide(new BigDecimal("12"), 0, RoundingMode.HALF_UP);

        // Prepare calculation details for JSONB
        Map<String, Object> calculationDetails = new HashMap<>();
        calculationDetails.put("grossAnnualIncome", grossAnnualIncome.toString());
        calculationDetails.put("ptkpAmount", ptkpAmount.toString());
        calculationDetails.put("pkpAnnual", pkpAnnual.toString());
        calculationDetails.put("pkpMonthly", pkpMonthly.toString());
        calculationDetails.put("taxBracket1Amount", taxBreakdown.get("bracket1").toString());
        calculationDetails.put("taxBracket2Amount", taxBreakdown.get("bracket2").toString());
        calculationDetails.put("taxBracket3Amount", taxBreakdown.get("bracket3").toString());
        calculationDetails.put("taxBracket4Amount", taxBreakdown.get("bracket4").toString());
        calculationDetails.put("taxBracket5Amount", taxBreakdown.get("bracket5").toString());
        calculationDetails.put("totalAnnualTax", totalAnnualTax.toString());
        calculationDetails.put("monthlyTax", monthlyTax.toString());
        calculationDetails.put("calculationMethod", "Progressive 5 Brackets");
        calculationDetails.put("taxYear", java.time.Year.now().getValue());

        // Create or update TaxCalculation entity
        TaxCalculation taxCalculation = taxCalculationRepository
                .findByEmployeePayrollId(employeePayrollId)
                .orElse(new TaxCalculation());

        taxCalculation.setEmployeePayrollId(employeePayrollId);
        taxCalculation.setEmployeeId(employeeId);
        taxCalculation.setPayrollPeriodId(payrollPeriodId);
        taxCalculation.setGrossIncome(grossAnnualIncome);
        taxCalculation.setNonTaxableIncome(BigDecimal.ZERO);  // Can be extended for allowances
        taxCalculation.setTaxableIncome(grossAnnualIncome);
        taxCalculation.setPtkpStatus(ptkpStatus.getCode());
        taxCalculation.setPtkpAmount(ptkpAmount);
        taxCalculation.setPkpAnnual(pkpAnnual);
        taxCalculation.setPkpMonthly(pkpMonthly);
        taxCalculation.setTaxBracket1(taxBreakdown.get("bracket1"));
        taxCalculation.setTaxBracket2(taxBreakdown.get("bracket2"));
        taxCalculation.setTaxBracket3(taxBreakdown.get("bracket3"));
        taxCalculation.setTaxBracket4(taxBreakdown.get("bracket4"));
        taxCalculation.setTaxBracket5(taxBreakdown.get("bracket5"));
        taxCalculation.setTotalAnnualTax(totalAnnualTax);
        taxCalculation.setMonthlyTax(monthlyTax);
        taxCalculation.setYtdGrossIncome(ytdGrossIncome);
        taxCalculation.setYtdTaxPaid(ytdTaxPaid.add(monthlyTax));
        taxCalculation.setCalculationDetails(calculationDetails);

        TaxCalculation savedTaxCalculation = taxCalculationRepository.save(taxCalculation);

        log.info("PPh 21 calculated: PKP Annual = {}, Total Annual Tax = {}, Monthly Tax = {}",
                pkpAnnual, totalAnnualTax, monthlyTax);

        return savedTaxCalculation;
    }

    /**
     * Calculate progressive tax based on Indonesian tax brackets.
     *
     * @param pkpAnnual Annual taxable income (PKP)
     * @return Map containing tax amount for each bracket
     */
    private Map<String, BigDecimal> calculateProgressiveTax(BigDecimal pkpAnnual) {
        Map<String, BigDecimal> breakdown = new HashMap<>();

        BigDecimal remaining = pkpAnnual;
        BigDecimal taxBracket1 = BigDecimal.ZERO;
        BigDecimal taxBracket2 = BigDecimal.ZERO;
        BigDecimal taxBracket3 = BigDecimal.ZERO;
        BigDecimal taxBracket4 = BigDecimal.ZERO;
        BigDecimal taxBracket5 = BigDecimal.ZERO;

        // Bracket 1: 0-60M at 5%
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal bracket1Income = remaining.min(BRACKET_1_LIMIT);
            taxBracket1 = bracket1Income.multiply(RATE_1).setScale(0, RoundingMode.HALF_UP);
            remaining = remaining.subtract(bracket1Income);
        }

        // Bracket 2: 60M-250M at 15%
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal bracket2Income = remaining.min(BRACKET_2_LIMIT.subtract(BRACKET_1_LIMIT));
            taxBracket2 = bracket2Income.multiply(RATE_2).setScale(0, RoundingMode.HALF_UP);
            remaining = remaining.subtract(bracket2Income);
        }

        // Bracket 3: 250M-500M at 25%
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal bracket3Income = remaining.min(BRACKET_3_LIMIT.subtract(BRACKET_2_LIMIT));
            taxBracket3 = bracket3Income.multiply(RATE_3).setScale(0, RoundingMode.HALF_UP);
            remaining = remaining.subtract(bracket3Income);
        }

        // Bracket 4: 500M-5B at 30%
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal bracket4Income = remaining.min(BRACKET_4_LIMIT.subtract(BRACKET_3_LIMIT));
            taxBracket4 = bracket4Income.multiply(RATE_4).setScale(0, RoundingMode.HALF_UP);
            remaining = remaining.subtract(bracket4Income);
        }

        // Bracket 5: >5B at 35%
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            taxBracket5 = remaining.multiply(RATE_5).setScale(0, RoundingMode.HALF_UP);
        }

        breakdown.put("bracket1", taxBracket1);
        breakdown.put("bracket2", taxBracket2);
        breakdown.put("bracket3", taxBracket3);
        breakdown.put("bracket4", taxBracket4);
        breakdown.put("bracket5", taxBracket5);

        return breakdown;
    }

    /**
     * Get PTKP amount based on marital status and dependents.
     *
     * @param ptkpStatus PTKP status enum
     * @return PTKP amount in IDR
     */
    public BigDecimal getPtkpAmount(PtkpStatus ptkpStatus) {
        return PTKP_AMOUNTS.getOrDefault(ptkpStatus, PTKP_AMOUNTS.get(PtkpStatus.TK_0));
    }

    /**
     * Calculate monthly tax deduction based on gross monthly income.
     * This is a simplified method for quick monthly calculations.
     *
     * @param grossMonthlyIncome Monthly gross income
     * @param ptkpStatus PTKP status
     * @return Monthly tax amount
     */
    public BigDecimal calculateMonthlyTax(BigDecimal grossMonthlyIncome, PtkpStatus ptkpStatus) {
        // Annualize the monthly income
        BigDecimal grossAnnualIncome = grossMonthlyIncome.multiply(new BigDecimal("12"));

        // Get PTKP amount
        BigDecimal ptkpAmount = getPtkpAmount(ptkpStatus);

        // Calculate PKP
        BigDecimal pkpAnnual = grossAnnualIncome.subtract(ptkpAmount);
        if (pkpAnnual.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        // Round down to nearest 1000
        pkpAnnual = pkpAnnual.divide(new BigDecimal("1000"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("1000"));

        // Calculate progressive tax
        Map<String, BigDecimal> taxBreakdown = calculateProgressiveTax(pkpAnnual);

        // Calculate total annual tax
        BigDecimal totalAnnualTax = taxBreakdown.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate monthly tax
        return totalAnnualTax.divide(new BigDecimal("12"), 0, RoundingMode.HALF_UP);
    }

    /**
     * Get tax calculation by employee payroll ID.
     *
     * @param employeePayrollId UUID of employee payroll
     * @return TaxCalculation entity
     */
    @Transactional(readOnly = true)
    public TaxCalculation getTaxCalculationByEmployeePayrollId(UUID employeePayrollId) {
        return taxCalculationRepository.findByEmployeePayrollId(employeePayrollId)
                .orElseThrow(() -> new RuntimeException(
                        "Tax calculation not found for employee payroll id: " + employeePayrollId));
    }

    /**
     * Get total tax amount for a payroll period.
     *
     * @param payrollPeriodId UUID of payroll period
     * @return Total tax amount
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalTaxByPeriod(UUID payrollPeriodId) {
        Double totalTax = taxCalculationRepository.getTotalTaxByPeriod(payrollPeriodId);
        return totalTax != null ? BigDecimal.valueOf(totalTax) : BigDecimal.ZERO;
    }
}
