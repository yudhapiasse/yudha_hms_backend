package com.yudha.hms.workforce.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yudha.hms.workforce.constant.LoanStatus;
import com.yudha.hms.workforce.constant.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for employee loan queries.
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
public class EmployeeLoanResponseDto {

    /**
     * Loan ID
     */
    private UUID id;

    /**
     * Loan number
     */
    private String loanNumber;

    /**
     * Employee ID
     */
    private UUID employeeId;

    /**
     * Employee name (denormalized)
     */
    private String employeeName;

    /**
     * Employee number (denormalized)
     */
    private String employeeNumber;

    /**
     * Loan type
     */
    private LoanType loanType;

    /**
     * Original loan amount
     */
    private BigDecimal loanAmount;

    /**
     * Interest rate
     */
    private BigDecimal interestRate;

    /**
     * Total amount with interest
     */
    private BigDecimal totalAmountWithInterest;

    /**
     * Monthly installment amount
     */
    private BigDecimal installmentAmount;

    /**
     * Total number of installments
     */
    private Integer installments;

    /**
     * Installments paid so far
     */
    private Integer paidInstallments;

    /**
     * Remaining installments
     */
    private Integer remainingInstallments;

    /**
     * Total amount paid
     */
    private BigDecimal totalPaid;

    /**
     * Remaining balance
     */
    private BigDecimal remainingBalance;

    /**
     * Start date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * Expected completion date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectedCompletionDate;

    /**
     * Actual completion date
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate completionDate;

    /**
     * Loan status
     */
    private LoanStatus status;

    /**
     * Approved by user ID
     */
    private UUID approvedBy;

    /**
     * Approval date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalDate;

    /**
     * Loan purpose
     */
    private String loanPurpose;

    /**
     * Notes
     */
    private String notes;

    /**
     * Created at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
