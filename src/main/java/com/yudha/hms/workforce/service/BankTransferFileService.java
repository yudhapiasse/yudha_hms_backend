package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.BankFileFormat;
import com.yudha.hms.workforce.entity.EmployeePayroll;
import com.yudha.hms.workforce.entity.PayrollPeriod;
import com.yudha.hms.workforce.repository.EmployeePayrollRepository;
import com.yudha.hms.workforce.repository.PayrollPeriodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bank Transfer File Generation Service for Indonesian Banks.
 *
 * Generates formatted files for bank transfers compatible with major Indonesian banks:
 * - BCA (Bank Central Asia)
 * - Mandiri
 * - BNI (Bank Negara Indonesia)
 * - BRI (Bank Rakyat Indonesia)
 * - Standard CSV format
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BankTransferFileService {

    private final EmployeePayrollRepository employeePayrollRepository;
    private final PayrollPeriodRepository payrollPeriodRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter BCA_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter MANDIRI_DATE_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    /**
     * Generate bank transfer file for a payroll period.
     *
     * @param periodId payroll period UUID
     * @param format bank file format
     * @return file content as String
     */
    public String generateBankTransferFile(UUID periodId, BankFileFormat format) {
        log.info("Generating {} bank transfer file for period: {}", format, periodId);

        PayrollPeriod period = payrollPeriodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + periodId));

        List<EmployeePayroll> payrolls = employeePayrollRepository.findByPayrollPeriodId(periodId)
                .stream()
                .filter(p -> p.getNetSalary().compareTo(BigDecimal.ZERO) > 0)
                .filter(p -> p.getBankAccountNumber() != null && !p.getBankAccountNumber().isEmpty())
                .collect(Collectors.toList());

        if (payrolls.isEmpty()) {
            throw new RuntimeException("No payroll records with bank account found for period: " + periodId);
        }

        String fileContent = switch (format) {
            case STANDARD_CSV -> generateStandardCSV(payrolls, period);
            case BCA -> generateBCAFormat(payrolls, period);
            case MANDIRI -> generateMandiriFormat(payrolls, period);
            case BNI -> generateBNIFormat(payrolls, period);
            case BRI -> generateBRIFormat(payrolls, period);
        };

        log.info("Successfully generated {} bank transfer file with {} records", 
                format, payrolls.size());

        return fileContent;
    }

    /**
     * Generate bank transfer file grouped by bank.
     *
     * @param periodId payroll period UUID
     * @return map of bank name to file content
     */
    public Map<String, String> generateBankTransferFilesByBank(UUID periodId) {
        log.info("Generating bank transfer files grouped by bank for period: {}", periodId);

        PayrollPeriod period = payrollPeriodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + periodId));

        List<EmployeePayroll> payrolls = employeePayrollRepository.findByPayrollPeriodId(periodId)
                .stream()
                .filter(p -> p.getNetSalary().compareTo(BigDecimal.ZERO) > 0)
                .filter(p -> p.getBankAccountNumber() != null && !p.getBankAccountNumber().isEmpty())
                .collect(Collectors.toList());

        // Group by bank name
        Map<String, List<EmployeePayroll>> payrollsByBank = payrolls.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getBankName() != null ? p.getBankName() : "UNKNOWN"
                ));

        Map<String, String> filesByBank = new HashMap<>();

        for (Map.Entry<String, List<EmployeePayroll>> entry : payrollsByBank.entrySet()) {
            String bankName = entry.getKey();
            List<EmployeePayroll> bankPayrolls = entry.getValue();

            BankFileFormat format = determineBankFormat(bankName);
            String fileContent = generateBankFile(bankPayrolls, period, format);

            filesByBank.put(bankName, fileContent);
        }

        log.info("Successfully generated bank transfer files for {} banks", filesByBank.size());

        return filesByBank;
    }

    /**
     * Determine bank file format from bank name.
     */
    private BankFileFormat determineBankFormat(String bankName) {
        if (bankName == null) {
            return BankFileFormat.STANDARD_CSV;
        }

        String upperName = bankName.toUpperCase();
        if (upperName.contains("BCA")) {
            return BankFileFormat.BCA;
        } else if (upperName.contains("MANDIRI")) {
            return BankFileFormat.MANDIRI;
        } else if (upperName.contains("BNI")) {
            return BankFileFormat.BNI;
        } else if (upperName.contains("BRI")) {
            return BankFileFormat.BRI;
        }

        return BankFileFormat.STANDARD_CSV;
    }

    /**
     * Generate bank file for a list of payrolls.
     */
    private String generateBankFile(List<EmployeePayroll> payrolls, PayrollPeriod period, BankFileFormat format) {
        return switch (format) {
            case STANDARD_CSV -> generateStandardCSV(payrolls, period);
            case BCA -> generateBCAFormat(payrolls, period);
            case MANDIRI -> generateMandiriFormat(payrolls, period);
            case BNI -> generateBNIFormat(payrolls, period);
            case BRI -> generateBRIFormat(payrolls, period);
        };
    }

    /**
     * Generate standard CSV format (universal).
     * Format: Account Number,Account Holder Name,Amount,Description
     */
    private String generateStandardCSV(List<EmployeePayroll> payrolls, PayrollPeriod period) {
        StringBuilder sb = new StringBuilder();
        sb.append("Account Number,Account Holder Name,Amount,Description\n");

        for (EmployeePayroll payroll : payrolls) {
            sb.append(payroll.getBankAccountNumber()).append(",");
            sb.append(escapeCSV(payroll.getBankAccountHolderName())).append(",");
            sb.append(payroll.getNetSalary().longValue()).append(",");
            sb.append(escapeCSV("Salary " + period.getPeriodCode())).append("\n");
        }

        return sb.toString();
    }

    /**
     * Generate BCA (Bank Central Asia) format.
     * Format: H~Record Count~Total Amount~Transfer Date
     *         D~Account Number~Account Name~Amount~Description
     */
    private String generateBCAFormat(List<EmployeePayroll> payrolls, PayrollPeriod period) {
        StringBuilder sb = new StringBuilder();

        // Calculate totals
        int recordCount = payrolls.size();
        BigDecimal totalAmount = payrolls.stream()
                .map(EmployeePayroll::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Header record
        String transferDate = period.getPaymentDate().format(BCA_DATE_FORMATTER);
        sb.append("H~").append(recordCount).append("~")
                .append(totalAmount.longValue()).append("~")
                .append(transferDate).append("\n");

        // Detail records
        for (EmployeePayroll payroll : payrolls) {
            sb.append("D~");
            sb.append(payroll.getBankAccountNumber()).append("~");
            sb.append(payroll.getBankAccountHolderName()).append("~");
            sb.append(payroll.getNetSalary().longValue()).append("~");
            sb.append("Salary ").append(period.getPeriodCode()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Generate Bank Mandiri format.
     * Format: Fixed length fields with specific positions
     */
    private String generateMandiriFormat(List<EmployeePayroll> payrolls, PayrollPeriod period) {
        StringBuilder sb = new StringBuilder();

        String transferDate = period.getPaymentDate().format(MANDIRI_DATE_FORMATTER);

        for (EmployeePayroll payroll : payrolls) {
            // Account number (16 characters, left aligned, space padded)
            sb.append(padRight(payroll.getBankAccountNumber(), 16));

            // Account holder name (30 characters, left aligned, space padded)
            sb.append(padRight(payroll.getBankAccountHolderName(), 30));

            // Amount (15 digits, right aligned, zero padded)
            sb.append(padLeft(String.valueOf(payroll.getNetSalary().longValue()), 15, '0'));

            // Transfer date (8 digits: DDMMYYYY)
            sb.append(transferDate);

            // Description (35 characters, left aligned, space padded)
            sb.append(padRight("Salary " + period.getPeriodCode(), 35));

            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Generate BNI (Bank Negara Indonesia) format.
     * Format similar to standard CSV but with BNI-specific fields
     */
    private String generateBNIFormat(List<EmployeePayroll> payrolls, PayrollPeriod period) {
        StringBuilder sb = new StringBuilder();

        String transferDate = period.getPaymentDate().format(DATE_FORMATTER);

        for (EmployeePayroll payroll : payrolls) {
            sb.append(payroll.getBankAccountNumber()).append("|");
            sb.append(payroll.getBankAccountHolderName()).append("|");
            sb.append(payroll.getNetSalary().longValue()).append("|");
            sb.append(transferDate).append("|");
            sb.append("IDR").append("|");  // Currency
            sb.append("Salary ").append(period.getPeriodCode()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Generate BRI (Bank Rakyat Indonesia) format.
     * Format: Tab-separated values
     */
    private String generateBRIFormat(List<EmployeePayroll> payrolls, PayrollPeriod period) {
        StringBuilder sb = new StringBuilder();
        sb.append("NO REKENING\tNAMA\tNOMINAL\tKETERANGAN\n");

        for (EmployeePayroll payroll : payrolls) {
            sb.append(payroll.getBankAccountNumber()).append("\t");
            sb.append(payroll.getBankAccountHolderName()).append("\t");
            sb.append(payroll.getNetSalary().longValue()).append("\t");
            sb.append("Gaji ").append(period.getPeriodCode()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Escape CSV values (wrap in quotes if contains comma, newline, or quote).
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\n") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Pad string to the right with spaces.
     */
    private String padRight(String value, int length) {
        if (value == null) {
            value = "";
        }
        if (value.length() >= length) {
            return value.substring(0, length);
        }
        return value + " ".repeat(length - value.length());
    }

    /**
     * Pad string to the left with specified character.
     */
    private String padLeft(String value, int length, char padChar) {
        if (value == null) {
            value = "";
        }
        if (value.length() >= length) {
            return value.substring(0, length);
        }
        return String.valueOf(padChar).repeat(length - value.length()) + value;
    }

    /**
     * Get file metadata for a generated bank transfer file.
     *
     * @param periodId payroll period UUID
     * @param format bank file format
     * @return metadata map
     */
    public Map<String, Object> getBankTransferFileMetadata(UUID periodId, BankFileFormat format) {
        PayrollPeriod period = payrollPeriodRepository.findById(periodId)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + periodId));

        List<EmployeePayroll> payrolls = employeePayrollRepository.findByPayrollPeriodId(periodId)
                .stream()
                .filter(p -> p.getNetSalary().compareTo(BigDecimal.ZERO) > 0)
                .filter(p -> p.getBankAccountNumber() != null && !p.getBankAccountNumber().isEmpty())
                .collect(Collectors.toList());

        BigDecimal totalAmount = payrolls.stream()
                .map(EmployeePayroll::getNetSalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("periodCode", period.getPeriodCode());
        metadata.put("periodName", period.getPeriodName());
        metadata.put("paymentDate", period.getPaymentDate());
        metadata.put("format", format.toString());
        metadata.put("recordCount", payrolls.size());
        metadata.put("totalAmount", totalAmount);
        metadata.put("fileName", generateFileName(period, format));

        return metadata;
    }

    /**
     * Generate file name for bank transfer file.
     */
    private String generateFileName(PayrollPeriod period, BankFileFormat format) {
        String dateStr = LocalDate.now().format(DATE_FORMATTER);
        String periodCode = period.getPeriodCode().replace("-", "");

        return switch (format) {
            case STANDARD_CSV -> String.format("PAYROLL_%s_%s.csv", periodCode, dateStr);
            case BCA -> String.format("BCA_PAYROLL_%s_%s.txt", periodCode, dateStr);
            case MANDIRI -> String.format("MANDIRI_PAYROLL_%s_%s.txt", periodCode, dateStr);
            case BNI -> String.format("BNI_PAYROLL_%s_%s.txt", periodCode, dateStr);
            case BRI -> String.format("BRI_PAYROLL_%s_%s.txt", periodCode, dateStr);
        };
    }
}
