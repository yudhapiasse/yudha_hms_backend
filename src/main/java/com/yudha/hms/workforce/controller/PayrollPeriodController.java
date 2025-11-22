package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.dto.PayrollPeriodRequestDto;
import com.yudha.hms.workforce.dto.PayrollPeriodResponseDto;
import com.yudha.hms.workforce.dto.PayrollSummaryResponseDto;
import com.yudha.hms.workforce.entity.PayrollPeriod;
import com.yudha.hms.workforce.repository.PayrollPeriodRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Payroll Period REST Controller.
 *
 * Provides RESTful endpoints for payroll period management:
 * - Create payroll periods
 * - Retrieve payroll periods
 * - Update payroll periods
 * - Close/approve payroll periods
 * - Get payroll period summaries
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/workforce/payroll-periods")
@RequiredArgsConstructor
@Slf4j
public class PayrollPeriodController {

    private final PayrollPeriodRepository payrollPeriodRepository;

    /**
     * Create a new payroll period.
     *
     * POST /api/workforce/payroll-periods
     *
     * @param requestDto payroll period request
     * @return created payroll period with 201 CREATED
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PayrollPeriodResponseDto>> createPayrollPeriod(
            @Valid @RequestBody PayrollPeriodRequestDto requestDto) {

        log.info("POST /api/workforce/payroll-periods - Creating payroll period: {}-{}",
                requestDto.getPeriodYear(), requestDto.getPeriodMonth());

        PayrollPeriod payrollPeriod = new PayrollPeriod();
        payrollPeriod.setPeriodYear(requestDto.getPeriodYear());
        payrollPeriod.setPeriodMonth(requestDto.getPeriodMonth());

        // Generate period code (e.g., "2025-01")
        String periodCode = String.format("%04d-%02d",
                requestDto.getPeriodYear(), requestDto.getPeriodMonth());
        payrollPeriod.setPeriodCode(periodCode);

        // Generate period name if not provided
        String periodName = requestDto.getPeriodName() != null
                ? requestDto.getPeriodName()
                : String.format("Payroll %04d-%02d",
                        requestDto.getPeriodYear(), requestDto.getPeriodMonth());
        payrollPeriod.setPeriodName(periodName);

        payrollPeriod.setStartDate(requestDto.getStartDate());
        payrollPeriod.setEndDate(requestDto.getEndDate());
        payrollPeriod.setPaymentDate(requestDto.getPaymentDate());
        payrollPeriod.setCutOffDate(requestDto.getCutOffDate());
        payrollPeriod.setIsThrPeriod(requestDto.getIsThrPeriod() != null ? requestDto.getIsThrPeriod() : false);
        payrollPeriod.setThrType(requestDto.getThrType());
        payrollPeriod.setNotes(requestDto.getNotes());

        PayrollPeriod saved = payrollPeriodRepository.save(payrollPeriod);

        PayrollPeriodResponseDto response = mapToResponseDto(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Payroll period created successfully: " + periodCode,
                        response
                ));
    }

    /**
     * Get all payroll periods.
     *
     * GET /api/workforce/payroll-periods
     *
     * @return list of payroll periods with 200 OK
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PayrollPeriodResponseDto>>> getAllPayrollPeriods() {

        log.info("GET /api/workforce/payroll-periods - Fetching all payroll periods");

        List<PayrollPeriod> periods = payrollPeriodRepository.findAll();
        List<PayrollPeriodResponseDto> response = periods.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Payroll periods retrieved successfully", response)
        );
    }

    /**
     * Get payroll period by ID.
     *
     * GET /api/workforce/payroll-periods/{id}
     *
     * @param id payroll period UUID
     * @return payroll period data with 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PayrollPeriodResponseDto>> getPayrollPeriodById(
            @PathVariable UUID id) {

        log.info("GET /api/workforce/payroll-periods/{} - Fetching payroll period by ID", id);

        PayrollPeriod period = payrollPeriodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + id));

        PayrollPeriodResponseDto response = mapToResponseDto(period);

        return ResponseEntity.ok(
                ApiResponse.success("Payroll period found", response)
        );
    }

    /**
     * Get payroll period by code.
     *
     * GET /api/workforce/payroll-periods/code/{periodCode}
     *
     * @param periodCode period code (e.g., "2025-01")
     * @return payroll period data with 200 OK
     */
    @GetMapping("/code/{periodCode}")
    public ResponseEntity<ApiResponse<PayrollPeriodResponseDto>> getPayrollPeriodByCode(
            @PathVariable String periodCode) {

        log.info("GET /api/workforce/payroll-periods/code/{} - Fetching payroll period by code", periodCode);

        PayrollPeriod period = payrollPeriodRepository.findByPeriodCode(periodCode)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + periodCode));

        PayrollPeriodResponseDto response = mapToResponseDto(period);

        return ResponseEntity.ok(
                ApiResponse.success("Payroll period found", response)
        );
    }

    /**
     * Get payroll periods by year.
     *
     * GET /api/workforce/payroll-periods/year/{year}
     *
     * @param year period year
     * @return list of payroll periods with 200 OK
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<PayrollPeriodResponseDto>>> getPayrollPeriodsByYear(
            @PathVariable Integer year) {

        log.info("GET /api/workforce/payroll-periods/year/{} - Fetching payroll periods by year", year);

        List<PayrollPeriod> periods = payrollPeriodRepository.findByPeriodYearOrderByPeriodMonthDesc(year);
        List<PayrollPeriodResponseDto> response = periods.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Payroll periods retrieved successfully", response)
        );
    }

    /**
     * Update a payroll period.
     *
     * PUT /api/workforce/payroll-periods/{id}
     *
     * @param id payroll period UUID
     * @param requestDto payroll period update request
     * @return updated payroll period with 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PayrollPeriodResponseDto>> updatePayrollPeriod(
            @PathVariable UUID id,
            @Valid @RequestBody PayrollPeriodRequestDto requestDto) {

        log.info("PUT /api/workforce/payroll-periods/{} - Updating payroll period", id);

        PayrollPeriod period = payrollPeriodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + id));

        period.setStartDate(requestDto.getStartDate());
        period.setEndDate(requestDto.getEndDate());
        period.setPaymentDate(requestDto.getPaymentDate());
        period.setCutOffDate(requestDto.getCutOffDate());
        period.setIsThrPeriod(requestDto.getIsThrPeriod() != null ? requestDto.getIsThrPeriod() : false);
        period.setThrType(requestDto.getThrType());
        period.setNotes(requestDto.getNotes());

        PayrollPeriod updated = payrollPeriodRepository.save(period);
        PayrollPeriodResponseDto response = mapToResponseDto(updated);

        return ResponseEntity.ok(
                ApiResponse.success("Payroll period updated successfully", response)
        );
    }

    /**
     * Delete a payroll period (soft delete).
     *
     * DELETE /api/workforce/payroll-periods/{id}
     *
     * @param id payroll period UUID
     * @return success message with 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePayrollPeriod(@PathVariable UUID id) {

        log.info("DELETE /api/workforce/payroll-periods/{} - Deleting payroll period", id);

        PayrollPeriod period = payrollPeriodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll period not found: " + id));

        payrollPeriodRepository.delete(period);

        return ResponseEntity.ok(
                ApiResponse.success("Payroll period deleted successfully", null)
        );
    }

    /**
     * Map PayrollPeriod entity to ResponseDto.
     */
    private PayrollPeriodResponseDto mapToResponseDto(PayrollPeriod period) {
        return PayrollPeriodResponseDto.builder()
                .id(period.getId())
                .periodYear(period.getPeriodYear())
                .periodMonth(period.getPeriodMonth())
                .periodCode(period.getPeriodCode())
                .periodName(period.getPeriodName())
                .startDate(period.getStartDate())
                .endDate(period.getEndDate())
                .paymentDate(period.getPaymentDate())
                .cutOffDate(period.getCutOffDate())
                .status(period.getStatus())
                .isThrPeriod(period.getIsThrPeriod())
                .thrType(period.getThrType())
                .processingStartedAt(period.getProcessingStartedAt())
                .processingCompletedAt(period.getProcessingCompletedAt())
                .approvedBy(period.getApprovedBy())
                .approvedAt(period.getApprovedAt())
                .notes(period.getNotes())
                .createdAt(period.getCreatedAt())
                .createdBy(period.getCreatedBy())
                .updatedAt(period.getUpdatedAt())
                .updatedBy(period.getUpdatedBy())
                .build();
    }
}
