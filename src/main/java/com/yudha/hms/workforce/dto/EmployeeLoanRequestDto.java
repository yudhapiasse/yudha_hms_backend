package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yudha.hms.workforce.constant.LoanType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Request DTO for creating or updating an employee loan.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLoanRequestDto {

    /**
     * Employee ID
     */
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    /**
     * Loan type
     */
    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    /**
     * Loan amount
     */
    @NotNull(message = "Loan amount is required")
    @Positive(message = "Loan amount must be positive")
    private BigDecimal loanAmount;

    /**
     * Interest rate (annual percentage, e.g., 5.5 for 5.5%)
     */
    private BigDecimal interestRate;

    /**
     * Number of installments
     */
    @NotNull(message = "Number of installments is required")
    @Positive(message = "Number of installments must be positive")
    private Integer installments;

    /**
     * Start date for deductions
     */
    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * Approved by user ID
     */
    private UUID approvedBy;

    /**
     * Loan purpose/description
     */
    private String loanPurpose;

    /**
     * Notes
     */
    private String notes;
}
