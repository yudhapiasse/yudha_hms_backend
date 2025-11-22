package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.entity.EmployeePayroll;
import com.yudha.hms.workforce.entity.PayrollPeriod;
import com.yudha.hms.workforce.repository.EmployeePayrollRepository;
import com.yudha.hms.workforce.repository.PayrollPeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Salary Slip Generation Service.
 *
 * Generates formatted salary slips (pay slips) for employees.
 * Supports Indonesian payroll requirements including:
 * - BPJS deductions
 * - PPh 21 tax
 * - THR (holiday allowance)
 * - Loan deductions
 * - Overtime pay
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SalarySlipService {

    private final EmployeePayrollRepository employeePayrollRepository;
    private final PayrollPeriodRepository payrollPeriodRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Generate salary slip data for an employee payroll record.
     *
     * @param payrollId employee payroll UUID
     * @return salary slip data as Map
     */
    public Map<String, Object> generateSalarySlipData(UUID payrollId) {
        log.info("Generating salary slip data for payroll ID: {}", payrollId);

        EmployeePayroll payroll = employeePayrollRepository.findById(payrollId)
                .orElseThrow(() -> new RuntimeException("Payroll record not found: " + payrollId));

        PayrollPeriod period = payrollPeriodRepository.findById(payroll.getPayrollPeriodId())
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + payroll.getPayrollPeriodId()));

        Map<String, Object> slipData = new LinkedHashMap<>();

        // Header Information
        slipData.put("payrollNumber", payroll.getPayrollNumber());
        slipData.put("generatedDate", LocalDateTime.now().format(DATE_FORMATTER));

        // Period Information
        Map<String, Object> periodInfo = new LinkedHashMap<>();
        periodInfo.put("code", period.getPeriodCode());
        periodInfo.put("name", period.getPeriodName());
        periodInfo.put("startDate", period.getStartDate().format(DATE_FORMATTER));
        periodInfo.put("endDate", period.getEndDate().format(DATE_FORMATTER));
        periodInfo.put("paymentDate", period.getPaymentDate().format(DATE_FORMATTER));
        slipData.put("period", periodInfo);

        // Employee Information (would normally fetch from Employee entity)
        Map<String, Object> employeeInfo = new LinkedHashMap<>();
        employeeInfo.put("id", payroll.getEmployeeId());
        employeeInfo.put("departmentId", payroll.getDepartmentId());
        employeeInfo.put("positionId", payroll.getPositionId());
        employeeInfo.put("employmentType", payroll.getEmploymentType());
        slipData.put("employee", employeeInfo);

        // Attendance Summary
        Map<String, Object> attendance = new LinkedHashMap<>();
        attendance.put("workingDays", payroll.getWorkingDays());
        attendance.put("actualWorkingDays", payroll.getActualWorkingDays());
        attendance.put("absentDays", payroll.getAbsentDays());
        attendance.put("leaveDays", payroll.getLeaveDays());
        attendance.put("unpaidLeaveDays", payroll.getUnpaidLeaveDays());
        slipData.put("attendance", attendance);

        // Overtime Summary
        Map<String, Object> overtime = new LinkedHashMap<>();
        overtime.put("normalHours", formatCurrency(payroll.getNormalOvertimeHours()));
        overtime.put("weekendHours", formatCurrency(payroll.getWeekendOvertimeHours()));
        overtime.put("holidayHours", formatCurrency(payroll.getHolidayOvertimeHours()));
        overtime.put("totalPay", formatCurrency(payroll.getTotalOvertime()));
        slipData.put("overtime", overtime);

        // Earnings Breakdown
        List<Map<String, Object>> earnings = new ArrayList<>();
        earnings.add(createLineItem("Basic Salary", payroll.getBasicSalary()));

        if (payroll.getTotalAllowances().compareTo(BigDecimal.ZERO) > 0) {
            earnings.add(createLineItem("Total Allowances", payroll.getTotalAllowances()));
        }

        if (payroll.getTotalOvertime().compareTo(BigDecimal.ZERO) > 0) {
            earnings.add(createLineItem("Overtime Pay", payroll.getTotalOvertime()));
        }

        if (payroll.getTotalShiftDifferential().compareTo(BigDecimal.ZERO) > 0) {
            earnings.add(createLineItem("Shift Differential", payroll.getTotalShiftDifferential()));
        }

        if (payroll.getTotalIncentives().compareTo(BigDecimal.ZERO) > 0) {
            earnings.add(createLineItem("Incentives", payroll.getTotalIncentives()));
        }

        if (payroll.getTotalBonuses().compareTo(BigDecimal.ZERO) > 0) {
            earnings.add(createLineItem("Bonuses", payroll.getTotalBonuses()));
        }

        if (payroll.getThrAmount().compareTo(BigDecimal.ZERO) > 0) {
            earnings.add(createLineItem("THR (Holiday Allowance)", payroll.getThrAmount()));
        }

        earnings.add(createLineItem("GROSS SALARY", payroll.getGrossSalary(), true));
        slipData.put("earnings", earnings);

        // Deductions Breakdown
        List<Map<String, Object>> deductions = new ArrayList<>();

        if (payroll.getBpjsKesehatanEmployee().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("BPJS Kesehatan (Employee)", payroll.getBpjsKesehatanEmployee()));
        }

        if (payroll.getBpjsKesehatanFamily().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("BPJS Kesehatan (Family)", payroll.getBpjsKesehatanFamily()));
        }

        if (payroll.getBpjsTkJht().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("BPJS TK - JHT", payroll.getBpjsTkJht()));
        }

        if (payroll.getBpjsTkJp().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("BPJS TK - JP", payroll.getBpjsTkJp()));
        }

        if (payroll.getPph21Amount().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("PPh 21 Tax", payroll.getPph21Amount()));
        }

        if (payroll.getLoanDeduction().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("Loan Deduction", payroll.getLoanDeduction()));
        }

        if (payroll.getAdvanceDeduction().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("Advance Deduction", payroll.getAdvanceDeduction()));
        }

        if (payroll.getOtherDeductions().compareTo(BigDecimal.ZERO) > 0) {
            deductions.add(createLineItem("Other Deductions", payroll.getOtherDeductions()));
        }

        deductions.add(createLineItem("TOTAL DEDUCTIONS", payroll.getTotalDeductions(), true));
        slipData.put("deductions", deductions);

        // Net Salary
        slipData.put("netSalary", formatCurrency(payroll.getNetSalary()));
        slipData.put("netSalaryWords", convertToWords(payroll.getNetSalary()));

        // Payment Information
        Map<String, Object> payment = new LinkedHashMap<>();
        payment.put("method", payroll.getPaymentMethod() != null ? payroll.getPaymentMethod().toString() : "BANK_TRANSFER");
        payment.put("status", payroll.getPaymentStatus() != null ? payroll.getPaymentStatus().toString() : "PENDING");
        if (payroll.getBankName() != null) {
            payment.put("bankName", payroll.getBankName());
        }
        if (payroll.getBankAccountNumber() != null) {
            payment.put("accountNumber", maskAccountNumber(payroll.getBankAccountNumber()));
        }
        if (payroll.getBankAccountHolderName() != null) {
            payment.put("accountHolder", payroll.getBankAccountHolderName());
        }
        slipData.put("payment", payment);

        // Status Information
        slipData.put("status", payroll.getStatus() != null ? payroll.getStatus().toString() : "DRAFT");
        if (payroll.getVerifiedAt() != null) {
            slipData.put("verifiedDate", payroll.getVerifiedAt().format(DATE_FORMATTER));
        }
        if (payroll.getApprovedAt() != null) {
            slipData.put("approvedDate", payroll.getApprovedAt().format(DATE_FORMATTER));
        }
        if (payroll.getPaidAt() != null) {
            slipData.put("paidDate", payroll.getPaidAt().format(DATE_FORMATTER));
        }

        log.info("Salary slip data generated successfully for payroll: {}", payroll.getPayrollNumber());

        return slipData;
    }

    /**
     * Generate salary slip data for all payrolls in a period.
     *
     * @param periodId payroll period UUID
     * @return list of salary slip data
     */
    public List<Map<String, Object>> generatePeriodSalarySlips(UUID periodId) {
        log.info("Generating salary slips for period: {}", periodId);

        List<EmployeePayroll> payrolls = employeePayrollRepository.findByPayrollPeriodId(periodId);
        List<Map<String, Object>> slips = new ArrayList<>();

        for (EmployeePayroll payroll : payrolls) {
            slips.add(generateSalarySlipData(payroll.getId()));
        }

        log.info("Generated {} salary slips for period: {}", slips.size(), periodId);

        return slips;
    }

    /**
     * Create a line item for earnings or deductions.
     */
    private Map<String, Object> createLineItem(String description, BigDecimal amount) {
        return createLineItem(description, amount, false);
    }

    /**
     * Create a line item for earnings or deductions.
     */
    private Map<String, Object> createLineItem(String description, BigDecimal amount, boolean isBold) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("description", description);
        item.put("amount", formatCurrency(amount));
        item.put("amountRaw", amount);
        item.put("bold", isBold);
        return item;
    }

    /**
     * Format currency value to Indonesian Rupiah format.
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "Rp 0";
        }
        return String.format("Rp %,d", amount.longValue());
    }

    /**
     * Mask bank account number for security.
     * Shows only last 4 digits.
     */
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        int visibleDigits = 4;
        int maskedLength = accountNumber.length() - visibleDigits;
        return "*".repeat(maskedLength) + accountNumber.substring(maskedLength);
    }

    /**
     * Convert number to Indonesian words.
     * This is a simplified version - a full implementation would use
     * a proper number-to-words library or more comprehensive logic.
     */
    private String convertToWords(BigDecimal amount) {
        if (amount == null) {
            return "Nol Rupiah";
        }

        long value = amount.longValue();

        if (value == 0) {
            return "Nol Rupiah";
        }

        // Simplified conversion (would normally use a proper library)
        // For now, just return formatted number
        return formatCurrency(amount) + " (formatted as words)";
    }
}
