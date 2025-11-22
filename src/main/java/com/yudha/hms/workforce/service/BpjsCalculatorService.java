package com.yudha.hms.workforce.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for calculating BPJS (Indonesian Social Security) deductions.
 * Handles both BPJS Kesehatan (Health) and BPJS Ketenagakerjaan (Employment).
 */
@Service
@Slf4j
public class BpjsCalculatorService {

    // BPJS Kesehatan (Health Insurance) - Employee Contributions
    private static final BigDecimal BPJS_KESEHATAN_EMPLOYEE_RATE = new BigDecimal("0.04");  // 4%
    private static final BigDecimal BPJS_KESEHATAN_FAMILY_RATE = new BigDecimal("0.01");    // 1%

    // BPJS Ketenagakerjaan - Employee Contributions
    private static final BigDecimal BPJS_TK_JHT_EMPLOYEE_RATE = new BigDecimal("0.02");     // 2% (Jaminan Hari Tua - Old Age)
    private static final BigDecimal BPJS_TK_JP_EMPLOYEE_RATE = new BigDecimal("0.01");      // 1% (Jaminan Pensiun - Pension)

    // Maximum salary caps for BPJS calculations (2024 regulations - in IDR)
    private static final BigDecimal BPJS_KESEHATAN_MAX_SALARY = new BigDecimal("12000000");  // 12 million IDR
    private static final BigDecimal BPJS_TK_JHT_MAX_SALARY = new BigDecimal("10042300");     // Updated annually
    private static final BigDecimal BPJS_TK_JP_MAX_SALARY = new BigDecimal("10042300");      // Updated annually

    /**
     * Calculate all BPJS deductions for an employee.
     *
     * @param basicSalary Employee's basic salary
     * @param totalAllowances Total allowances (tunjangan)
     * @param hasFamily Whether employee has family dependents
     * @return Map containing all BPJS deduction amounts
     */
    public Map<String, BigDecimal> calculateBpjsDeductions(
            BigDecimal basicSalary,
            BigDecimal totalAllowances,
            boolean hasFamily) {

        log.info("Calculating BPJS deductions for basic salary: {}, allowances: {}, hasFamily: {}",
                basicSalary, totalAllowances, hasFamily);

        Map<String, BigDecimal> deductions = new HashMap<>();

        // BPJS Kesehatan calculations
        Map<String, BigDecimal> kesehatanDeductions = calculateBpjsKesehatan(basicSalary, hasFamily);
        deductions.put("bpjsKesehatanEmployee", kesehatanDeductions.get("employeeContribution"));
        deductions.put("bpjsKesehatanFamily", kesehatanDeductions.get("familyContribution"));
        deductions.put("bpjsKesehatanTotal", kesehatanDeductions.get("totalDeduction"));

        // BPJS Ketenagakerjaan calculations
        BigDecimal grossForBpjsTk = basicSalary.add(totalAllowances);
        BigDecimal bpjsTkJht = calculateBpjsTkJht(grossForBpjsTk);
        BigDecimal bpjsTkJp = calculateBpjsTkJp(grossForBpjsTk);

        deductions.put("bpjsTkJht", bpjsTkJht);
        deductions.put("bpjsTkJp", bpjsTkJp);
        deductions.put("bpjsTkTotal", bpjsTkJht.add(bpjsTkJp));

        // Grand total
        BigDecimal grandTotal = kesehatanDeductions.get("totalDeduction")
                .add(bpjsTkJht)
                .add(bpjsTkJp);
        deductions.put("totalBpjsDeduction", grandTotal);

        log.info("BPJS deductions calculated - Kesehatan: {}, TK JHT: {}, TK JP: {}, Total: {}",
                kesehatanDeductions.get("totalDeduction"), bpjsTkJht, bpjsTkJp, grandTotal);

        return deductions;
    }

    /**
     * Calculate BPJS Kesehatan (Health Insurance) deductions.
     *
     * @param basicSalary Employee's basic salary
     * @param hasFamily Whether employee has family dependents
     * @return Map with employee contribution, family contribution, and total
     */
    public Map<String, BigDecimal> calculateBpjsKesehatan(BigDecimal basicSalary, boolean hasFamily) {
        Map<String, BigDecimal> result = new HashMap<>();

        // Apply salary cap
        BigDecimal cappedSalary = basicSalary.min(BPJS_KESEHATAN_MAX_SALARY);

        // Calculate employee contribution (4%)
        BigDecimal employeeContribution = cappedSalary
                .multiply(BPJS_KESEHATAN_EMPLOYEE_RATE)
                .setScale(0, RoundingMode.HALF_UP);

        // Calculate family contribution (1%) if applicable
        BigDecimal familyContribution = hasFamily
                ? cappedSalary.multiply(BPJS_KESEHATAN_FAMILY_RATE).setScale(0, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal totalDeduction = employeeContribution.add(familyContribution);

        result.put("employeeContribution", employeeContribution);
        result.put("familyContribution", familyContribution);
        result.put("totalDeduction", totalDeduction);
        result.put("cappedSalary", cappedSalary);
        result.put("maxSalary", BPJS_KESEHATAN_MAX_SALARY);

        return result;
    }

    /**
     * Calculate BPJS TK JHT (Jaminan Hari Tua - Old Age Security) employee contribution.
     *
     * @param grossSalary Gross salary (basic + allowances)
     * @return JHT deduction amount
     */
    public BigDecimal calculateBpjsTkJht(BigDecimal grossSalary) {
        // Apply salary cap
        BigDecimal cappedSalary = grossSalary.min(BPJS_TK_JHT_MAX_SALARY);

        // Calculate 2% employee contribution
        return cappedSalary
                .multiply(BPJS_TK_JHT_EMPLOYEE_RATE)
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Calculate BPJS TK JP (Jaminan Pensiun - Pension) employee contribution.
     *
     * @param grossSalary Gross salary (basic + allowances)
     * @return JP deduction amount
     */
    public BigDecimal calculateBpjsTkJp(BigDecimal grossSalary) {
        // Apply salary cap
        BigDecimal cappedSalary = grossSalary.min(BPJS_TK_JP_MAX_SALARY);

        // Calculate 1% employee contribution
        return cappedSalary
                .multiply(BPJS_TK_JP_EMPLOYEE_RATE)
                .setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Calculate employer contributions for BPJS (for reporting purposes).
     * This does not affect employee net salary but is useful for total cost calculation.
     *
     * @param basicSalary Basic salary
     * @param totalAllowances Total allowances
     * @param riskCategory Risk category for JKK (0.24% to 1.74%)
     * @return Map containing employer contribution amounts
     */
    public Map<String, BigDecimal> calculateEmployerBpjsContributions(
            BigDecimal basicSalary,
            BigDecimal totalAllowances,
            String riskCategory) {

        Map<String, BigDecimal> contributions = new HashMap<>();

        BigDecimal grossSalary = basicSalary.add(totalAllowances);

        // BPJS Kesehatan - Employer pays 4%
        BigDecimal cappedSalaryKesehatan = basicSalary.min(BPJS_KESEHATAN_MAX_SALARY);
        BigDecimal kesehatanEmployer = cappedSalaryKesehatan
                .multiply(new BigDecimal("0.04"))
                .setScale(0, RoundingMode.HALF_UP);

        // BPJS TK - JHT - Employer pays 3.7%
        BigDecimal cappedSalaryTk = grossSalary.min(BPJS_TK_JHT_MAX_SALARY);
        BigDecimal jhtEmployer = cappedSalaryTk
                .multiply(new BigDecimal("0.037"))
                .setScale(0, RoundingMode.HALF_UP);

        // BPJS TK - JP - Employer pays 2%
        BigDecimal jpEmployer = cappedSalaryTk
                .multiply(new BigDecimal("0.02"))
                .setScale(0, RoundingMode.HALF_UP);

        // BPJS TK - JKK (Work Accident) - Employer only, rate depends on risk category
        BigDecimal jkkRate = getJkkRate(riskCategory);
        BigDecimal jkkEmployer = cappedSalaryTk
                .multiply(jkkRate)
                .setScale(0, RoundingMode.HALF_UP);

        // BPJS TK - JKM (Death) - Employer only, 0.3%
        BigDecimal jkmEmployer = cappedSalaryTk
                .multiply(new BigDecimal("0.003"))
                .setScale(0, RoundingMode.HALF_UP);

        contributions.put("kesehatanEmployer", kesehatanEmployer);
        contributions.put("jhtEmployer", jhtEmployer);
        contributions.put("jpEmployer", jpEmployer);
        contributions.put("jkkEmployer", jkkEmployer);
        contributions.put("jkmEmployer", jkmEmployer);

        BigDecimal totalEmployer = kesehatanEmployer
                .add(jhtEmployer)
                .add(jpEmployer)
                .add(jkkEmployer)
                .add(jkmEmployer);

        contributions.put("totalEmployerContribution", totalEmployer);

        return contributions;
    }

    /**
     * Get JKK (Work Accident) rate based on risk category.
     *
     * @param riskCategory Risk category: VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH
     * @return JKK rate as BigDecimal
     */
    private BigDecimal getJkkRate(String riskCategory) {
        return switch (riskCategory != null ? riskCategory.toUpperCase() : "LOW") {
            case "VERY_LOW" -> new BigDecimal("0.0024");  // 0.24%
            case "LOW" -> new BigDecimal("0.0054");       // 0.54%
            case "MEDIUM" -> new BigDecimal("0.0089");    // 0.89%
            case "HIGH" -> new BigDecimal("0.0127");      // 1.27%
            case "VERY_HIGH" -> new BigDecimal("0.0174"); // 1.74%
            default -> new BigDecimal("0.0054");          // Default to LOW
        };
    }

    /**
     * Get BPJS Kesehatan maximum salary cap.
     *
     * @return Maximum salary for BPJS Kesehatan calculation
     */
    public BigDecimal getBpjsKesehatanMaxSalary() {
        return BPJS_KESEHATAN_MAX_SALARY;
    }

    /**
     * Get BPJS Ketenagakerjaan maximum salary cap.
     *
     * @return Maximum salary for BPJS TK calculation
     */
    public BigDecimal getBpjsTkMaxSalary() {
        return BPJS_TK_JHT_MAX_SALARY;
    }

    /**
     * Calculate total BPJS cost for employer (employee + employer contributions).
     *
     * @param basicSalary Basic salary
     * @param totalAllowances Total allowances
     * @param hasFamily Whether employee has family
     * @param riskCategory Risk category for JKK
     * @return Total BPJS cost
     */
    public BigDecimal calculateTotalBpjsCost(
            BigDecimal basicSalary,
            BigDecimal totalAllowances,
            boolean hasFamily,
            String riskCategory) {

        Map<String, BigDecimal> employeeDeductions = calculateBpjsDeductions(
                basicSalary, totalAllowances, hasFamily);

        Map<String, BigDecimal> employerContributions = calculateEmployerBpjsContributions(
                basicSalary, totalAllowances, riskCategory);

        return employeeDeductions.get("totalBpjsDeduction")
                .add(employerContributions.get("totalEmployerContribution"));
    }
}
