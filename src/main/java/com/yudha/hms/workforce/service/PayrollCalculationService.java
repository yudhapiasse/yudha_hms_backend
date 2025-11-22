package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.PtkpStatus;
import com.yudha.hms.workforce.entity.*;
import com.yudha.hms.workforce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * Main service for calculating employee payroll including all components:
 * - Basic salary and allowances
 * - Overtime pay
 * - THR (holiday allowance)
 * - BPJS deductions (Kesehatan and Ketenagakerjaan)
 * - PPh 21 tax
 * - Loan deductions
 * - Gross and net salary calculation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollCalculationService {

    // Calculator Services
    private final IndonesianTaxCalculatorService taxCalculatorService;
    private final BpjsCalculatorService bpjsCalculatorService;
    private final OvertimeCalculatorService overtimeCalculatorService;
    private final ThrCalculatorService thrCalculatorService;

    // Repositories
    private final EmployeePayrollRepository employeePayrollRepository;
    private final PayrollPeriodRepository payrollPeriodRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Calculate complete payroll for an employee for a specific period.
     *
     * @param employeeId Employee UUID
     * @param payrollPeriodId Payroll period UUID
     * @return Calculated EmployeePayroll entity
     */
    @Transactional
    public EmployeePayroll calculateEmployeePayroll(UUID employeeId, UUID payrollPeriodId) {
        log.info("Starting payroll calculation for employee {} in period {}", employeeId, payrollPeriodId);

        // Get payroll period
        PayrollPeriod payrollPeriod = payrollPeriodRepository.findById(payrollPeriodId)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + payrollPeriodId));

        // Get employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        // Get or create employee payroll record
        EmployeePayroll employeePayroll = employeePayrollRepository
                .findByEmployeeIdAndPayrollPeriodId(employeeId, payrollPeriodId)
                .orElse(new EmployeePayroll());

        employeePayroll.setEmployeeId(employeeId);
        employeePayroll.setPayrollPeriodId(payrollPeriodId);
        employeePayroll.setPayrollNumber(generatePayrollNumber(employeeId, payrollPeriod));
        employeePayroll.setDepartmentId(employee.getDepartmentId());
        employeePayroll.setPositionId(employee.getPositionId());
        employeePayroll.setEmploymentType(employee.getEmploymentType() != null ? employee.getEmploymentType().name() : null);

        // Simplified attendance summary (TODO: integrate with attendance records)
        int workingDays = countWorkingDays(payrollPeriod.getStartDate(), payrollPeriod.getEndDate());
        employeePayroll.setWorkingDays(workingDays);
        employeePayroll.setActualWorkingDays(workingDays);  // Assume full attendance for now
        employeePayroll.setAbsentDays(0);

        // Calculate basic salary
        BigDecimal basicSalary = employee.getBasicSalary();
        employeePayroll.setBasicSalary(basicSalary);

        // Calculate allowances (TODO: integrate with payroll components)
        BigDecimal totalAllowances = BigDecimal.ZERO;  // Placeholder
        employeePayroll.setTotalAllowances(totalAllowances);

        // Calculate overtime
        BigDecimal totalOvertime = overtimeCalculatorService.calculateTotalOvertimeForPeriod(
                employeeId,
                payrollPeriod.getStartDate(),
                payrollPeriod.getEndDate(),
                basicSalary
        );
        employeePayroll.setTotalOvertime(totalOvertime);
        employeePayroll.setOvertimeHours(overtimeCalculatorService.getTotalOvertimeHours(
                employeeId, payrollPeriod.getStartDate(), payrollPeriod.getEndDate()));

        // Calculate THR if applicable
        BigDecimal thrAmount = BigDecimal.ZERO;
        if (payrollPeriod.getIsThrPeriod() && payrollPeriod.getThrType() != null) {
            thrAmount = calculateThrAmount(employee, payrollPeriod, basicSalary, totalAllowances);
        }
        employeePayroll.setThrAmount(thrAmount);

        // Calculate gross salary
        BigDecimal grossSalary = basicSalary
                .add(totalAllowances)
                .add(totalOvertime)
                .add(thrAmount);
        employeePayroll.setGrossSalary(grossSalary);

        // Calculate BPJS deductions
        Map<String, BigDecimal> bpjsDeductions = bpjsCalculatorService.calculateBpjsDeductions(
                basicSalary,
                totalAllowances,
                employee.getHasFamily()
        );

        employeePayroll.setBpjsKesehatanEmployee(bpjsDeductions.get("bpjsKesehatanEmployee"));
        employeePayroll.setBpjsKesehatanFamily(bpjsDeductions.get("bpjsKesehatanFamily"));
        employeePayroll.setBpjsTkJht(bpjsDeductions.get("bpjsTkJht"));
        employeePayroll.setBpjsTkJp(bpjsDeductions.get("bpjsTkJp"));

        // Calculate loan deductions (TODO: integrate with loan records)
        BigDecimal loanDeduction = BigDecimal.ZERO;  // Placeholder
        employeePayroll.setLoanDeduction(loanDeduction);

        // Calculate other deductions (TODO: integrate with deduction components)
        BigDecimal otherDeductions = BigDecimal.ZERO;  // Placeholder
        employeePayroll.setOtherDeductions(otherDeductions);

        // Calculate total deductions (before tax)
        BigDecimal totalDeductionsBeforeTax = employeePayroll.getBpjsKesehatanEmployee()
                .add(employeePayroll.getBpjsKesehatanFamily())
                .add(employeePayroll.getBpjsTkJht())
                .add(employeePayroll.getBpjsTkJp())
                .add(loanDeduction)
                .add(otherDeductions);

        // Calculate PPh 21 tax
        BigDecimal annualGross = grossSalary.multiply(new BigDecimal("12"));
        BigDecimal ytdGross = annualGross;  // Simplified for now
        BigDecimal ytdTax = BigDecimal.ZERO;  // Simplified for now

        PtkpStatus ptkpStatus = PtkpStatus.valueOf(employee.getPtkpStatus());
        TaxCalculation taxCalculation = taxCalculatorService.calculatePph21(
                employeePayroll.getId() != null ? employeePayroll.getId() : UUID.randomUUID(),
                employeeId,
                payrollPeriodId,
                annualGross,
                ptkpStatus,
                ytdGross,
                ytdTax
        );

        BigDecimal pph21Amount = taxCalculation.getMonthlyTax();
        employeePayroll.setPph21Amount(pph21Amount);

        // Calculate total deductions
        BigDecimal totalDeductions = totalDeductionsBeforeTax.add(pph21Amount);
        employeePayroll.setTotalDeductions(totalDeductions);

        // Calculate net salary
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);
        employeePayroll.setNetSalary(netSalary);

        // Save employee payroll
        employeePayroll = employeePayrollRepository.save(employeePayroll);

        log.info("Payroll calculation completed for employee {}: Gross={}, Deductions={}, Net={}",
                employeeId, grossSalary, totalDeductions, netSalary);

        return employeePayroll;
    }

    /**
     * Calculate THR amount if this is a THR period.
     */
    private BigDecimal calculateThrAmount(Employee employee, PayrollPeriod period,
                                          BigDecimal basicSalary, BigDecimal totalAllowances) {
        if (!period.getIsThrPeriod() || period.getThrType() == null) {
            return BigDecimal.ZERO;
        }

        try {
            // Period.getThrType() returns a ThrType enum directly
            com.yudha.hms.workforce.constant.ThrType thrType = period.getThrType();

            Map<String, Object> thrCalculation = thrCalculatorService.calculateThr(
                    basicSalary,
                    totalAllowances,
                    employee.getEmploymentStartDate(),
                    period.getEndDate(),
                    thrType
            );

            return (BigDecimal) thrCalculation.getOrDefault("thrAmount", BigDecimal.ZERO);
        } catch (NullPointerException e) {
            log.warn("Null THR type for period: {}", period.getPeriodCode());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Generate unique payroll number.
     */
    private String generatePayrollNumber(UUID employeeId, PayrollPeriod period) {
        return String.format("PAY-%s-%04d%02d-%s",
                period.getPeriodCode(),
                period.getPeriodYear(),
                period.getPeriodMonth(),
                employeeId.toString().substring(0, 8).toUpperCase()
        );
    }

    /**
     * Count working days (weekdays) in a period.
     */
    private int countWorkingDays(LocalDate startDate, LocalDate endDate) {
        int workingDays = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (current.getDayOfWeek().getValue() < 6) {  // Monday-Friday
                workingDays++;
            }
            current = current.plusDays(1);
        }

        return workingDays;
    }

    /**
     * Get hourly rate for an employee.
     */
    public BigDecimal getHourlyRate(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        return overtimeCalculatorService.calculateHourlyRate(employee.getBasicSalary());
    }

    /**
     * Get total payroll cost for a period.
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalPayrollCostForPeriod(UUID payrollPeriodId) {
        List<EmployeePayroll> payrolls = employeePayrollRepository
                .findByPayrollPeriodId(payrollPeriodId);

        return payrolls.stream()
                .map(EmployeePayroll::getGrossSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get net payroll for a period (amount to be paid out).
     */
    @Transactional(readOnly = true)
    public BigDecimal getNetPayrollForPeriod(UUID payrollPeriodId) {
        List<EmployeePayroll> payrolls = employeePayrollRepository
                .findByPayrollPeriodId(payrollPeriodId);

        return payrolls.stream()
                .map(EmployeePayroll::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get payroll summary for a period.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPayrollSummary(UUID payrollPeriodId) {
        List<EmployeePayroll> payrolls = employeePayrollRepository
                .findByPayrollPeriodId(payrollPeriodId);

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalNet = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal totalBpjs = BigDecimal.ZERO;
        BigDecimal totalOvertime = BigDecimal.ZERO;

        for (EmployeePayroll payroll : payrolls) {
            totalGross = totalGross.add(payroll.getGrossSalary());
            totalNet = totalNet.add(payroll.getNetSalary());
            totalTax = totalTax.add(payroll.getPph21Amount());
            totalBpjs = totalBpjs
                    .add(payroll.getBpjsKesehatanEmployee())
                    .add(payroll.getBpjsKesehatanFamily())
                    .add(payroll.getBpjsTkJht())
                    .add(payroll.getBpjsTkJp());
            totalOvertime = totalOvertime.add(payroll.getTotalOvertime());
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("employeeCount", payrolls.size());
        summary.put("totalGrossSalary", totalGross);
        summary.put("totalNetSalary", totalNet);
        summary.put("totalTax", totalTax);
        summary.put("totalBpjsDeductions", totalBpjs);
        summary.put("totalOvertimePay", totalOvertime);
        summary.put("totalDeductions", totalGross.subtract(totalNet));

        return summary;
    }
}
