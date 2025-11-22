package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.ThrType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for calculating THR (Tunjangan Hari Raya - Religious Holiday Allowance).
 *
 * Based on Indonesian Government Regulation:
 * - Peraturan Pemerintah (PP) No. 36 Tahun 2021
 * - Peraturan Menteri Ketenagakerjaan No. 6 Tahun 2016
 *
 * THR Rules:
 * - Employees who have worked >= 1 month are entitled to THR
 * - Amount: 1 month salary for >= 12 months service, prorated for < 12 months
 * - Calculation base: Basic salary + fixed allowances
 * - Payment: Maximum 7 days before the religious holiday
 * - Applies to: Idul Fitri (Lebaran), Christmas, Chinese New Year, and other religious holidays
 */
@Service
@Slf4j
public class ThrCalculatorService {

    private static final int FULL_THR_MONTHS = 12;  // 12 months for full THR
    private static final int MINIMUM_MONTHS = 1;     // Minimum 1 month to qualify

    /**
     * Calculate THR amount based on length of service.
     *
     * @param basicSalary Employee's basic monthly salary
     * @param fixedAllowances Fixed monthly allowances (tunjangan tetap)
     * @param employmentStartDate Employee's start date
     * @param thrCalculationDate Date when THR is calculated (usually near holiday)
     * @param thrType Type of THR (IDUL_FITRI, CHRISTMAS, etc.)
     * @return Map containing THR calculation details
     */
    public Map<String, Object> calculateThr(
            BigDecimal basicSalary,
            BigDecimal fixedAllowances,
            LocalDate employmentStartDate,
            LocalDate thrCalculationDate,
            ThrType thrType) {

        log.info("Calculating THR for employee starting {}, calculation date: {}, THR type: {}",
                employmentStartDate, thrCalculationDate, thrType);

        // Calculate months of service
        long monthsOfService = ChronoUnit.MONTHS.between(employmentStartDate, thrCalculationDate);

        // Check eligibility
        if (monthsOfService < MINIMUM_MONTHS) {
            return Map.of(
                    "eligible", false,
                    "monthsOfService", monthsOfService,
                    "thrAmount", BigDecimal.ZERO,
                    "reason", "Employee has not completed minimum 1 month of service"
            );
        }

        // Calculate THR base (basic salary + fixed allowances)
        BigDecimal thrBase = basicSalary.add(fixedAllowances);

        // Calculate THR amount
        BigDecimal thrAmount;
        BigDecimal thrPercentage;

        if (monthsOfService >= FULL_THR_MONTHS) {
            // Full THR: 1 month salary
            thrAmount = thrBase;
            thrPercentage = new BigDecimal("100");
        } else {
            // Prorated THR: (months of service / 12) * base salary
            thrPercentage = new BigDecimal(monthsOfService)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(FULL_THR_MONTHS), 2, RoundingMode.HALF_UP);

            thrAmount = thrBase
                    .multiply(new BigDecimal(monthsOfService))
                    .divide(new BigDecimal(FULL_THR_MONTHS), 0, RoundingMode.HALF_UP);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("eligible", true);
        result.put("monthsOfService", monthsOfService);
        result.put("basicSalary", basicSalary);
        result.put("fixedAllowances", fixedAllowances);
        result.put("thrBase", thrBase);
        result.put("thrPercentage", thrPercentage);
        result.put("thrAmount", thrAmount);
        result.put("thrType", thrType.name());
        result.put("employmentStartDate", employmentStartDate);
        result.put("calculationDate", thrCalculationDate);
        result.put("isFullThr", monthsOfService >= FULL_THR_MONTHS);

        log.info("THR calculated: {} months service, {}% of base salary, amount: {}",
                monthsOfService, thrPercentage, thrAmount);

        return result;
    }

    /**
     * Calculate THR for Idul Fitri (most common THR in Indonesia).
     *
     * @param basicSalary Basic monthly salary
     * @param fixedAllowances Fixed monthly allowances
     * @param employmentStartDate Employment start date
     * @param idulFitriDate Date of Idul Fitri holiday
     * @return THR amount
     */
    public BigDecimal calculateIdulFitriThr(
            BigDecimal basicSalary,
            BigDecimal fixedAllowances,
            LocalDate employmentStartDate,
            LocalDate idulFitriDate) {

        // Calculate THR 7 days before Idul Fitri
        LocalDate calculationDate = idulFitriDate.minusDays(7);

        Map<String, Object> thrCalculation = calculateThr(
                basicSalary,
                fixedAllowances,
                employmentStartDate,
                calculationDate,
                ThrType.IDUL_FITRI
        );

        return (BigDecimal) thrCalculation.getOrDefault("thrAmount", BigDecimal.ZERO);
    }

    /**
     * Calculate prorated THR based on exact months of service.
     *
     * @param basicSalary Basic monthly salary
     * @param fixedAllowances Fixed monthly allowances
     * @param monthsOfService Number of months worked
     * @return Prorated THR amount
     */
    public BigDecimal calculateProratedThr(
            BigDecimal basicSalary,
            BigDecimal fixedAllowances,
            int monthsOfService) {

        if (monthsOfService < MINIMUM_MONTHS) {
            return BigDecimal.ZERO;
        }

        BigDecimal thrBase = basicSalary.add(fixedAllowances);

        if (monthsOfService >= FULL_THR_MONTHS) {
            return thrBase;
        }

        return thrBase
                .multiply(new BigDecimal(monthsOfService))
                .divide(new BigDecimal(FULL_THR_MONTHS), 0, RoundingMode.HALF_UP);
    }

    /**
     * Calculate THR for resigned employee (prorated to resignation date).
     *
     * @param basicSalary Basic monthly salary
     * @param fixedAllowances Fixed monthly allowances
     * @param employmentStartDate Employment start date
     * @param resignationDate Resignation date
     * @param lastThrPaymentDate Date of last THR payment (to avoid duplicate)
     * @return Map with THR calculation details
     */
    public Map<String, Object> calculateResignationThr(
            BigDecimal basicSalary,
            BigDecimal fixedAllowances,
            LocalDate employmentStartDate,
            LocalDate resignationDate,
            LocalDate lastThrPaymentDate) {

        // Calculate from last THR payment or employment start
        LocalDate calculationStartDate = (lastThrPaymentDate != null && lastThrPaymentDate.isAfter(employmentStartDate))
                ? lastThrPaymentDate
                : employmentStartDate;

        long monthsOfService = ChronoUnit.MONTHS.between(calculationStartDate, resignationDate);

        Map<String, Object> result = new HashMap<>();

        if (monthsOfService < MINIMUM_MONTHS) {
            result.put("eligible", false);
            result.put("thrAmount", BigDecimal.ZERO);
            result.put("reason", "Less than 1 month since last THR payment or employment start");
            return result;
        }

        BigDecimal thrBase = basicSalary.add(fixedAllowances);
        BigDecimal thrAmount = calculateProratedThr(basicSalary, fixedAllowances, (int) monthsOfService);

        result.put("eligible", true);
        result.put("monthsOfService", monthsOfService);
        result.put("thrBase", thrBase);
        result.put("thrAmount", thrAmount);
        result.put("calculationStartDate", calculationStartDate);
        result.put("resignationDate", resignationDate);
        result.put("thrType", "RESIGNATION");

        log.info("Resignation THR calculated: {} months from {} to {}, amount: {}",
                monthsOfService, calculationStartDate, resignationDate, thrAmount);

        return result;
    }

    /**
     * Check if employee is eligible for THR.
     *
     * @param employmentStartDate Employment start date
     * @param checkDate Date to check eligibility
     * @return true if eligible (>= 1 month service)
     */
    public boolean isEligibleForThr(LocalDate employmentStartDate, LocalDate checkDate) {
        long monthsOfService = ChronoUnit.MONTHS.between(employmentStartDate, checkDate);
        return monthsOfService >= MINIMUM_MONTHS;
    }

    /**
     * Get THR payment deadline (7 days before holiday).
     *
     * @param holidayDate Date of religious holiday
     * @return Deadline for THR payment
     */
    public LocalDate getThrPaymentDeadline(LocalDate holidayDate) {
        return holidayDate.minusDays(7);
    }

    /**
     * Calculate total THR for multiple employees (for budget planning).
     *
     * @param employeeCount Number of employees
     * @param averageBasicSalary Average basic salary
     * @param averageFixedAllowances Average fixed allowances
     * @return Estimated total THR budget
     */
    public BigDecimal calculateThrBudget(
            int employeeCount,
            BigDecimal averageBasicSalary,
            BigDecimal averageFixedAllowances) {

        BigDecimal thrPerEmployee = averageBasicSalary.add(averageFixedAllowances);
        BigDecimal totalBudget = thrPerEmployee.multiply(new BigDecimal(employeeCount));

        log.info("THR budget calculated for {} employees: {} per employee, total: {}",
                employeeCount, thrPerEmployee, totalBudget);

        return totalBudget;
    }

    /**
     * Calculate THR with detailed breakdown for payroll integration.
     *
     * @param basicSalary Basic salary
     * @param fixedAllowances Fixed allowances
     * @param variableAllowances Variable allowances (not included in THR base)
     * @param employmentStartDate Employment start date
     * @param thrDate THR calculation date
     * @param thrType Type of THR
     * @return Detailed breakdown map
     */
    public Map<String, Object> calculateThrWithBreakdown(
            BigDecimal basicSalary,
            BigDecimal fixedAllowances,
            BigDecimal variableAllowances,
            LocalDate employmentStartDate,
            LocalDate thrDate,
            ThrType thrType) {

        Map<String, Object> thrCalculation = calculateThr(
                basicSalary,
                fixedAllowances,
                employmentStartDate,
                thrDate,
                thrType
        );

        // Add additional breakdown
        thrCalculation.put("variableAllowances", variableAllowances);
        thrCalculation.put("variableAllowancesIncluded", false);
        thrCalculation.put("note", "THR is calculated based on basic salary + fixed allowances only. " +
                "Variable allowances are not included per Indonesian labor law.");

        // Tax information note
        thrCalculation.put("taxNote", "THR is subject to PPh 21 tax and should be included in annual income calculation.");

        return thrCalculation;
    }
}
