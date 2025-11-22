package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.dto.EmployeeLoanRequestDto;
import com.yudha.hms.workforce.dto.EmployeeLoanResponseDto;
import com.yudha.hms.workforce.entity.EmployeeLoan;
import com.yudha.hms.workforce.repository.EmployeeLoanRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Employee Loan REST Controller.
 *
 * Provides RESTful endpoints for employee loan management:
 * - Create loan records
 * - Retrieve loan records
 * - Calculate loan repayment
 * - Track loan status
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/workforce/loans")
@RequiredArgsConstructor
@Slf4j
public class EmployeeLoanController {

    private final EmployeeLoanRepository employeeLoanRepository;

    /**
     * Create a new employee loan.
     *
     * POST /api/workforce/loans
     *
     * @param requestDto employee loan request
     * @return created loan record with 201 CREATED
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeLoanResponseDto>> createEmployeeLoan(
            @Valid @RequestBody EmployeeLoanRequestDto requestDto) {

        log.info("POST /api/workforce/loans - Creating loan for employee: {}",
                requestDto.getEmployeeId());

        EmployeeLoan loan = new EmployeeLoan();
        loan.setEmployeeId(requestDto.getEmployeeId());
        loan.setLoanType(requestDto.getLoanType());
        loan.setLoanAmount(requestDto.getLoanAmount());
        loan.setInterestRate(
                requestDto.getInterestRate() != null
                        ? requestDto.getInterestRate()
                        : BigDecimal.ZERO
        );
        loan.setInstallmentCount(requestDto.getInstallments());
        loan.setStartDate(requestDto.getStartDate());
        loan.setApprovedBy(requestDto.getApprovedBy());
        loan.setPurpose(requestDto.getLoanPurpose());
        loan.setNotes(requestDto.getNotes());

        // Generate loan number (LOAN-YYYYMMDD-XXXXX format)
        String loanNumber = String.format("LOAN-%s-%05d",
                requestDto.getStartDate().toString().replace("-", ""),
                System.currentTimeMillis() % 100000);
        loan.setLoanNumber(loanNumber);

        // Calculate total amount with interest
        BigDecimal interestAmount = requestDto.getLoanAmount()
                .multiply(requestDto.getInterestRate() != null ? requestDto.getInterestRate() : BigDecimal.ZERO)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmountWithInterest = requestDto.getLoanAmount().add(interestAmount);
        loan.setTotalAmount(totalAmountWithInterest);

        // Calculate monthly installment
        BigDecimal installmentAmount = totalAmountWithInterest
                .divide(new BigDecimal(requestDto.getInstallments()), 2, RoundingMode.HALF_UP);
        loan.setInstallmentAmount(installmentAmount);

        // Initialize tracking fields
        loan.setPaidInstallments(0);
        loan.setRemainingInstallments(requestDto.getInstallments());
        loan.setPaidAmount(BigDecimal.ZERO);
        loan.setOutstandingAmount(totalAmountWithInterest);

        EmployeeLoan saved = employeeLoanRepository.save(loan);

        EmployeeLoanResponseDto response = mapToResponseDto(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Employee loan created successfully: " + loanNumber,
                        response
                ));
    }

    /**
     * Get all employee loans.
     *
     * GET /api/workforce/loans
     *
     * @return list of loans with 200 OK
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeLoanResponseDto>>> getAllEmployeeLoans() {

        log.info("GET /api/workforce/loans - Fetching all employee loans");

        List<EmployeeLoan> loans = employeeLoanRepository.findAll();
        List<EmployeeLoanResponseDto> response = loans.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Employee loans retrieved successfully", response)
        );
    }

    /**
     * Get loan by ID.
     *
     * GET /api/workforce/loans/{id}
     *
     * @param id loan UUID
     * @return loan data with 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeLoanResponseDto>> getEmployeeLoanById(
            @PathVariable UUID id) {

        log.info("GET /api/workforce/loans/{} - Fetching loan by ID", id);

        EmployeeLoan loan = employeeLoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee loan not found: " + id));

        EmployeeLoanResponseDto response = mapToResponseDto(loan);

        return ResponseEntity.ok(
                ApiResponse.success("Employee loan found", response)
        );
    }

    /**
     * Get loans by employee.
     *
     * GET /api/workforce/loans/employee/{employeeId}
     *
     * @param employeeId employee UUID
     * @return list of loans with 200 OK
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<EmployeeLoanResponseDto>>> getLoansByEmployee(
            @PathVariable UUID employeeId) {

        log.info("GET /api/workforce/loans/employee/{} - Fetching loans for employee", employeeId);

        List<EmployeeLoan> loans = employeeLoanRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
        List<EmployeeLoanResponseDto> response = loans.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Employee loans retrieved successfully", response)
        );
    }

    /**
     * Update a loan record.
     *
     * PUT /api/workforce/loans/{id}
     *
     * @param id loan UUID
     * @param requestDto loan update request
     * @return updated loan with 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeLoanResponseDto>> updateEmployeeLoan(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeLoanRequestDto requestDto) {

        log.info("PUT /api/workforce/loans/{} - Updating employee loan", id);

        EmployeeLoan loan = employeeLoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee loan not found: " + id));

        loan.setPurpose(requestDto.getLoanPurpose());
        loan.setNotes(requestDto.getNotes());

        EmployeeLoan updated = employeeLoanRepository.save(loan);
        EmployeeLoanResponseDto response = mapToResponseDto(updated);

        return ResponseEntity.ok(
                ApiResponse.success("Employee loan updated successfully", response)
        );
    }

    /**
     * Delete a loan record (soft delete).
     *
     * DELETE /api/workforce/loans/{id}
     *
     * @param id loan UUID
     * @return success message with 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployeeLoan(@PathVariable UUID id) {

        log.info("DELETE /api/workforce/loans/{} - Deleting employee loan", id);

        EmployeeLoan loan = employeeLoanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee loan not found: " + id));

        employeeLoanRepository.delete(loan);

        return ResponseEntity.ok(
                ApiResponse.success("Employee loan deleted successfully", null)
        );
    }

    /**
     * Map EmployeeLoan entity to ResponseDto.
     */
    private EmployeeLoanResponseDto mapToResponseDto(EmployeeLoan loan) {
        return EmployeeLoanResponseDto.builder()
                .id(loan.getId())
                .loanNumber(loan.getLoanNumber())
                .employeeId(loan.getEmployeeId())
                .loanType(loan.getLoanType())
                .loanAmount(loan.getLoanAmount())
                .interestRate(loan.getInterestRate())
                .totalAmountWithInterest(loan.getTotalAmount())
                .installmentAmount(loan.getInstallmentAmount())
                .installments(loan.getInstallmentCount())
                .paidInstallments(loan.getPaidInstallments())
                .remainingInstallments(loan.getRemainingInstallments())
                .totalPaid(loan.getPaidAmount())
                .remainingBalance(loan.getOutstandingAmount())
                .startDate(loan.getStartDate())
                .status(loan.getStatus())
                .approvedBy(loan.getApprovedBy())
                .approvalDate(loan.getApprovedAt())
                .loanPurpose(loan.getPurpose())
                .notes(loan.getNotes())
                .createdAt(loan.getCreatedAt())
                .createdBy(loan.getCreatedBy())
                .updatedAt(loan.getUpdatedAt())
                .updatedBy(loan.getUpdatedBy())
                .build();
    }
}
