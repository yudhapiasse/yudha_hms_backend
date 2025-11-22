package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.OvertimeType;
import com.yudha.hms.workforce.entity.OvertimeRecord;
import com.yudha.hms.workforce.repository.OvertimeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for calculating overtime pay based on Indonesian labor law (UU Ketenagakerjaan No. 13/2003).
 *
 * Indonesian Overtime Regulations:
 * - Weekday: 1.5x for first hour, 2x for subsequent hours
 * - Weekend/Holiday: 2x for all hours, 3x if > 8 hours, 4x if > 9 hours (special cases)
 * - Daily limit: Maximum 3 hours per day (4 hours for special circumstances)
 * - Weekly limit: Maximum 14 hours per week
 * - Monthly limit: Maximum 40 hours per month (informally tracked)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OvertimeCalculatorService {

    private final OvertimeRecordRepository overtimeRecordRepository;

    // Indonesian Labor Law Limits
    private static final BigDecimal DAILY_OVERTIME_LIMIT = new BigDecimal("3");     // 3 hours per day
    private static final BigDecimal WEEKLY_OVERTIME_LIMIT = new BigDecimal("14");   // 14 hours per week
    private static final BigDecimal DAILY_OVERTIME_EXTENDED = new BigDecimal("4");  // 4 hours for special circumstances

    // Overtime Multipliers
    private static final BigDecimal WEEKDAY_FIRST_HOUR = new BigDecimal("1.5");
    private static final BigDecimal WEEKDAY_SUBSEQUENT = new BigDecimal("2.0");
    private static final BigDecimal WEEKEND_HOLIDAY_STANDARD = new BigDecimal("2.0");
    private static final BigDecimal WEEKEND_HOLIDAY_EXTENDED = new BigDecimal("3.0");  // > 8 hours
    private static final BigDecimal WEEKEND_HOLIDAY_MAXIMUM = new BigDecimal("4.0");   // > 9 hours

    // Standard working hours per day
    private static final BigDecimal STANDARD_DAILY_HOURS = new BigDecimal("8");
    private static final int STANDARD_MONTHLY_WORKING_DAYS = 21;  // ~21 working days per month

    /**
     * Calculate overtime pay for a given overtime record.
     *
     * @param overtimeRecord Overtime record with hours and type
     * @param monthlySalary Employee's monthly basic salary
     * @return Calculated overtime pay amount
     */
    public BigDecimal calculateOvertimePay(OvertimeRecord overtimeRecord, BigDecimal monthlySalary) {
        // Calculate hourly rate from monthly salary
        BigDecimal hourlyRate = calculateHourlyRate(monthlySalary);

        // Calculate overtime pay based on type
        BigDecimal overtimePay = switch (overtimeRecord.getOvertimeType()) {
            case WEEKDAY, AFTER_HOURS -> calculateWeekdayOvertimePay(
                    overtimeRecord.getEffectiveOvertimeHours(),
                    hourlyRate
            );
            case WEEKEND, HOLIDAY -> calculateWeekendHolidayOvertimePay(
                    overtimeRecord.getEffectiveOvertimeHours(),
                    hourlyRate
            );
        };

        log.info("Overtime pay calculated for {} hours on {}: {} (hourly rate: {})",
                overtimeRecord.getEffectiveOvertimeHours(),
                overtimeRecord.getOvertimeType(),
                overtimePay,
                hourlyRate);

        return overtimePay;
    }

    /**
     * Calculate overtime pay with detailed breakdown.
     *
     * @param effectiveHours Effective overtime hours
     * @param overtimeType Type of overtime
     * @param monthlySalary Monthly basic salary
     * @return Map containing calculation breakdown
     */
    public Map<String, Object> calculateOvertimePayWithBreakdown(
            BigDecimal effectiveHours,
            OvertimeType overtimeType,
            BigDecimal monthlySalary) {

        BigDecimal hourlyRate = calculateHourlyRate(monthlySalary);
        BigDecimal overtimePay;
        BigDecimal averageMultiplier;

        if (overtimeType == OvertimeType.WEEKDAY || overtimeType == OvertimeType.AFTER_HOURS) {
            overtimePay = calculateWeekdayOvertimePay(effectiveHours, hourlyRate);
            averageMultiplier = calculateWeekdayAverageMultiplier(effectiveHours);
        } else {
            overtimePay = calculateWeekendHolidayOvertimePay(effectiveHours, hourlyRate);
            averageMultiplier = calculateWeekendHolidayAverageMultiplier(effectiveHours);
        }

        return Map.of(
                "effectiveHours", effectiveHours,
                "hourlyRate", hourlyRate,
                "averageMultiplier", averageMultiplier,
                "overtimePay", overtimePay,
                "monthlySalary", monthlySalary,
                "overtimeType", overtimeType.name()
        );
    }

    /**
     * Calculate weekday overtime pay (1.5x first hour, 2x subsequent).
     *
     * @param effectiveHours Effective overtime hours
     * @param hourlyRate Hourly rate
     * @return Total overtime pay
     */
    private BigDecimal calculateWeekdayOvertimePay(BigDecimal effectiveHours, BigDecimal hourlyRate) {
        if (effectiveHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPay = BigDecimal.ZERO;

        // First hour at 1.5x
        if (effectiveHours.compareTo(BigDecimal.ONE) >= 0) {
            BigDecimal firstHourPay = hourlyRate.multiply(WEEKDAY_FIRST_HOUR);
            totalPay = totalPay.add(firstHourPay);

            // Subsequent hours at 2x
            if (effectiveHours.compareTo(BigDecimal.ONE) > 0) {
                BigDecimal remainingHours = effectiveHours.subtract(BigDecimal.ONE);
                BigDecimal subsequentPay = hourlyRate.multiply(WEEKDAY_SUBSEQUENT).multiply(remainingHours);
                totalPay = totalPay.add(subsequentPay);
            }
        } else {
            // Less than 1 hour at 1.5x
            totalPay = hourlyRate.multiply(WEEKDAY_FIRST_HOUR).multiply(effectiveHours);
        }

        return totalPay.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Calculate weekend/holiday overtime pay (2x standard, 3x if > 8 hours, 4x if > 9 hours).
     *
     * @param effectiveHours Effective overtime hours
     * @param hourlyRate Hourly rate
     * @return Total overtime pay
     */
    private BigDecimal calculateWeekendHolidayOvertimePay(BigDecimal effectiveHours, BigDecimal hourlyRate) {
        if (effectiveHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalPay = BigDecimal.ZERO;

        // First 8 hours at 2x
        BigDecimal regularHours = effectiveHours.min(STANDARD_DAILY_HOURS);
        BigDecimal regularPay = hourlyRate.multiply(WEEKEND_HOLIDAY_STANDARD).multiply(regularHours);
        totalPay = totalPay.add(regularPay);

        // Hours 8-9 at 3x
        if (effectiveHours.compareTo(STANDARD_DAILY_HOURS) > 0) {
            BigDecimal extendedHours = effectiveHours.subtract(STANDARD_DAILY_HOURS).min(BigDecimal.ONE);
            BigDecimal extendedPay = hourlyRate.multiply(WEEKEND_HOLIDAY_EXTENDED).multiply(extendedHours);
            totalPay = totalPay.add(extendedPay);
        }

        // Hours beyond 9 at 4x
        BigDecimal nineHours = new BigDecimal("9");
        if (effectiveHours.compareTo(nineHours) > 0) {
            BigDecimal maximumHours = effectiveHours.subtract(nineHours);
            BigDecimal maximumPay = hourlyRate.multiply(WEEKEND_HOLIDAY_MAXIMUM).multiply(maximumHours);
            totalPay = totalPay.add(maximumPay);
        }

        return totalPay.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Calculate average multiplier for weekday overtime (for reporting).
     *
     * @param effectiveHours Effective overtime hours
     * @return Average multiplier
     */
    private BigDecimal calculateWeekdayAverageMultiplier(BigDecimal effectiveHours) {
        if (effectiveHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (effectiveHours.compareTo(BigDecimal.ONE) <= 0) {
            return WEEKDAY_FIRST_HOUR;
        }

        // (1.5 * 1 + 2.0 * remaining) / total hours
        BigDecimal remainingHours = effectiveHours.subtract(BigDecimal.ONE);
        BigDecimal totalMultiplier = WEEKDAY_FIRST_HOUR.add(WEEKDAY_SUBSEQUENT.multiply(remainingHours));
        return totalMultiplier.divide(effectiveHours, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate average multiplier for weekend/holiday overtime.
     *
     * @param effectiveHours Effective overtime hours
     * @return Average multiplier
     */
    private BigDecimal calculateWeekendHolidayAverageMultiplier(BigDecimal effectiveHours) {
        if (effectiveHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (effectiveHours.compareTo(STANDARD_DAILY_HOURS) <= 0) {
            return WEEKEND_HOLIDAY_STANDARD;
        }

        BigDecimal totalMultiplier = WEEKEND_HOLIDAY_STANDARD.multiply(STANDARD_DAILY_HOURS);

        BigDecimal nineHours = new BigDecimal("9");
        if (effectiveHours.compareTo(nineHours) <= 0) {
            BigDecimal extendedHours = effectiveHours.subtract(STANDARD_DAILY_HOURS);
            totalMultiplier = totalMultiplier.add(WEEKEND_HOLIDAY_EXTENDED.multiply(extendedHours));
        } else {
            totalMultiplier = totalMultiplier.add(WEEKEND_HOLIDAY_EXTENDED); // 1 hour at 3x
            BigDecimal maximumHours = effectiveHours.subtract(nineHours);
            totalMultiplier = totalMultiplier.add(WEEKEND_HOLIDAY_MAXIMUM.multiply(maximumHours));
        }

        return totalMultiplier.divide(effectiveHours, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate hourly rate from monthly salary.
     * Formula: Monthly Salary / (21 working days * 8 hours)
     *
     * @param monthlySalary Monthly basic salary
     * @return Hourly rate
     */
    public BigDecimal calculateHourlyRate(BigDecimal monthlySalary) {
        BigDecimal monthlyWorkingHours = STANDARD_DAILY_HOURS
                .multiply(new BigDecimal(STANDARD_MONTHLY_WORKING_DAYS));

        return monthlySalary.divide(monthlyWorkingHours, 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total overtime pay for a payroll period.
     *
     * @param employeeId Employee UUID
     * @param payrollPeriodStart Start date of payroll period
     * @param payrollPeriodEnd End date of payroll period
     * @param monthlySalary Employee's monthly salary
     * @return Total overtime pay for the period
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalOvertimeForPeriod(
            UUID employeeId,
            LocalDate payrollPeriodStart,
            LocalDate payrollPeriodEnd,
            BigDecimal monthlySalary) {

        List<OvertimeRecord> overtimeRecords = overtimeRecordRepository
                .findApprovedOvertimeByEmployeeAndPeriod(
                        employeeId,
                        payrollPeriodStart,
                        payrollPeriodEnd
                );

        BigDecimal totalOvertimePay = BigDecimal.ZERO;

        for (OvertimeRecord record : overtimeRecords) {
            BigDecimal overtimePay = calculateOvertimePay(record, monthlySalary);
            totalOvertimePay = totalOvertimePay.add(overtimePay);
        }

        log.info("Total overtime pay for employee {} in period {}-{}: {}",
                employeeId, payrollPeriodStart, payrollPeriodEnd, totalOvertimePay);

        return totalOvertimePay;
    }

    /**
     * Check if overtime hours exceed Indonesian labor law limits.
     *
     * @param employeeId Employee UUID
     * @param overtimeDate Date of overtime
     * @param proposedHours Proposed overtime hours
     * @return Map with compliance status and warnings
     */
    @Transactional(readOnly = true)
    public Map<String, Object> checkOvertimeCompliance(
            UUID employeeId,
            LocalDate overtimeDate,
            BigDecimal proposedHours) {

        boolean exceedsDailyLimit = proposedHours.compareTo(DAILY_OVERTIME_LIMIT) > 0;
        boolean exceedsExtendedLimit = proposedHours.compareTo(DAILY_OVERTIME_EXTENDED) > 0;

        // Check weekly limit
        LocalDate weekStart = overtimeDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = overtimeDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        BigDecimal weeklyOvertimeHours = overtimeRecordRepository
                .getTotalOvertimeHoursByEmployeeAndDateRange(employeeId, weekStart, weekEnd);

        BigDecimal projectedWeeklyHours = weeklyOvertimeHours.add(proposedHours);
        boolean exceedsWeeklyLimit = projectedWeeklyHours.compareTo(WEEKLY_OVERTIME_LIMIT) > 0;

        StringBuilder complianceNotes = new StringBuilder();
        if (exceedsExtendedLimit) {
            complianceNotes.append("CRITICAL: Exceeds maximum daily limit of 4 hours. ");
        } else if (exceedsDailyLimit) {
            complianceNotes.append("WARNING: Exceeds standard daily limit of 3 hours (special approval required). ");
        }

        if (exceedsWeeklyLimit) {
            complianceNotes.append(String.format(
                    "WARNING: Total weekly overtime will be %.2f hours (limit: 14 hours). ",
                    projectedWeeklyHours
            ));
        }

        boolean isCompliant = !exceedsExtendedLimit && !exceedsWeeklyLimit;

        return Map.of(
                "isCompliant", isCompliant,
                "exceedsDailyLimit", exceedsDailyLimit,
                "exceedsExtendedLimit", exceedsExtendedLimit,
                "exceedsWeeklyLimit", exceedsWeeklyLimit,
                "currentWeeklyHours", weeklyOvertimeHours,
                "projectedWeeklyHours", projectedWeeklyHours,
                "weeklyLimit", WEEKLY_OVERTIME_LIMIT,
                "dailyLimit", DAILY_OVERTIME_LIMIT,
                "complianceNotes", complianceNotes.toString()
        );
    }

    /**
     * Get total overtime hours for an employee in a period.
     *
     * @param employeeId Employee UUID
     * @param startDate Period start date
     * @param endDate Period end date
     * @return Total overtime hours
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalOvertimeHours(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return overtimeRecordRepository.getTotalOvertimeHoursByEmployeeAndDateRange(
                employeeId, startDate, endDate);
    }
}
