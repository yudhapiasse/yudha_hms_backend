package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.dto.OvertimeRecordRequestDto;
import com.yudha.hms.workforce.dto.OvertimeRecordResponseDto;
import com.yudha.hms.workforce.entity.OvertimeRecord;
import com.yudha.hms.workforce.repository.OvertimeRecordRepository;
import com.yudha.hms.workforce.service.OvertimeCalculatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Overtime Record REST Controller.
 *
 * Provides RESTful endpoints for overtime management:
 * - Create overtime records
 * - Retrieve overtime records
 * - Approve overtime
 * - Calculate overtime pay
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/workforce/payroll/overtime")
@RequiredArgsConstructor
@Slf4j
public class OvertimeRecordController {

    private final OvertimeRecordRepository overtimeRecordRepository;
    private final OvertimeCalculatorService overtimeCalculatorService;

    /**
     * Create a new overtime record.
     *
     * POST /api/workforce/overtime
     *
     * @param requestDto overtime record request
     * @return created overtime record with 201 CREATED
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OvertimeRecordResponseDto>> createOvertimeRecord(
            @Valid @RequestBody OvertimeRecordRequestDto requestDto) {

        log.info("POST /api/workforce/overtime - Creating overtime record for employee: {}",
                requestDto.getEmployeeId());

        OvertimeRecord overtimeRecord = new OvertimeRecord();
        overtimeRecord.setEmployeeId(requestDto.getEmployeeId());
        overtimeRecord.setDepartmentId(requestDto.getDepartmentId());
        overtimeRecord.setOvertimeDate(requestDto.getOvertimeDate());
        overtimeRecord.setOvertimeType(requestDto.getOvertimeType());
        // Convert LocalTime to LocalDateTime by combining with the overtime date
        overtimeRecord.setStartTime(requestDto.getStartTime().atDate(requestDto.getOvertimeDate()));
        overtimeRecord.setEndTime(requestDto.getEndTime().atDate(requestDto.getOvertimeDate()));
        overtimeRecord.setBreakHours(
                requestDto.getBreakDurationHours() != null
                        ? requestDto.getBreakDurationHours()
                        : BigDecimal.ZERO
        );
        overtimeRecord.setSupervisorId(requestDto.getSupervisorId());
        overtimeRecord.setOvertimeReason(requestDto.getTaskDescription() != null ? requestDto.getTaskDescription() : "");
        overtimeRecord.setWorkDescription(requestDto.getTaskDescription());
        overtimeRecord.setNotes(requestDto.getNotes());

        // Generate overtime number (OT-YYYYMMDD-XXXXX format)
        String overtimeNumber = String.format("OT-%s-%05d",
                requestDto.getOvertimeDate().toString().replace("-", ""),
                System.currentTimeMillis() % 100000);
        overtimeRecord.setOvertimeNumber(overtimeNumber);

        // Calculate overtime hours and amounts
        // This would normally call the overtime calculator service
        // For now, set basic values
        overtimeRecord.setTotalHours(BigDecimal.ZERO);
        overtimeRecord.setEffectiveOvertimeHours(BigDecimal.ZERO);
        overtimeRecord.setOvertimeMultiplier(BigDecimal.ZERO);
        overtimeRecord.setTotalOvertimePay(BigDecimal.ZERO);

        OvertimeRecord saved = overtimeRecordRepository.save(overtimeRecord);

        OvertimeRecordResponseDto response = mapToResponseDto(saved);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Overtime record created successfully: " + overtimeNumber,
                        response
                ));
    }

    /**
     * Get all overtime records.
     *
     * GET /api/workforce/overtime
     *
     * @return list of overtime records with 200 OK
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OvertimeRecordResponseDto>>> getAllOvertimeRecords() {

        log.info("GET /api/workforce/overtime - Fetching all overtime records");

        List<OvertimeRecord> records = overtimeRecordRepository.findAll();
        List<OvertimeRecordResponseDto> response = records.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Overtime records retrieved successfully", response)
        );
    }

    /**
     * Get overtime record by ID.
     *
     * GET /api/workforce/overtime/{id}
     *
     * @param id overtime record UUID
     * @return overtime record data with 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OvertimeRecordResponseDto>> getOvertimeRecordById(
            @PathVariable UUID id) {

        log.info("GET /api/workforce/overtime/{} - Fetching overtime record by ID", id);

        OvertimeRecord record = overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Overtime record not found: " + id));

        OvertimeRecordResponseDto response = mapToResponseDto(record);

        return ResponseEntity.ok(
                ApiResponse.success("Overtime record found", response)
        );
    }

    /**
     * Get overtime records by employee.
     *
     * GET /api/workforce/overtime/employee/{employeeId}
     *
     * @param employeeId employee UUID
     * @return list of overtime records with 200 OK
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<OvertimeRecordResponseDto>>> getOvertimeByEmployee(
            @PathVariable UUID employeeId) {

        log.info("GET /api/workforce/overtime/employee/{} - Fetching overtime records for employee", employeeId);

        List<OvertimeRecord> records = overtimeRecordRepository.findByEmployeeId(employeeId);
        List<OvertimeRecordResponseDto> response = records.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Employee overtime records retrieved successfully", response)
        );
    }

    /**
     * Update an overtime record.
     *
     * PUT /api/workforce/overtime/{id}
     *
     * @param id overtime record UUID
     * @param requestDto overtime record update request
     * @return updated overtime record with 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OvertimeRecordResponseDto>> updateOvertimeRecord(
            @PathVariable UUID id,
            @Valid @RequestBody OvertimeRecordRequestDto requestDto) {

        log.info("PUT /api/workforce/overtime/{} - Updating overtime record", id);

        OvertimeRecord record = overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Overtime record not found: " + id));

        record.setOvertimeDate(requestDto.getOvertimeDate());
        record.setOvertimeType(requestDto.getOvertimeType());
        record.setStartTime(requestDto.getStartTime().atDate(requestDto.getOvertimeDate()));
        record.setEndTime(requestDto.getEndTime().atDate(requestDto.getOvertimeDate()));
        record.setBreakHours(
                requestDto.getBreakDurationHours() != null
                        ? requestDto.getBreakDurationHours()
                        : BigDecimal.ZERO
        );
        record.setOvertimeReason(requestDto.getTaskDescription() != null ? requestDto.getTaskDescription() : "");
        record.setWorkDescription(requestDto.getTaskDescription());
        record.setNotes(requestDto.getNotes());

        OvertimeRecord updated = overtimeRecordRepository.save(record);
        OvertimeRecordResponseDto response = mapToResponseDto(updated);

        return ResponseEntity.ok(
                ApiResponse.success("Overtime record updated successfully", response)
        );
    }

    /**
     * Delete an overtime record (soft delete).
     *
     * DELETE /api/workforce/overtime/{id}
     *
     * @param id overtime record UUID
     * @return success message with 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOvertimeRecord(@PathVariable UUID id) {

        log.info("DELETE /api/workforce/overtime/{} - Deleting overtime record", id);

        OvertimeRecord record = overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Overtime record not found: " + id));

        overtimeRecordRepository.delete(record);

        return ResponseEntity.ok(
                ApiResponse.success("Overtime record deleted successfully", null)
        );
    }

    /**
     * Map OvertimeRecord entity to ResponseDto.
     */
    private OvertimeRecordResponseDto mapToResponseDto(OvertimeRecord record) {
        return OvertimeRecordResponseDto.builder()
                .id(record.getId())
                .overtimeNumber(record.getOvertimeNumber())
                .employeeId(record.getEmployeeId())
                .departmentId(record.getDepartmentId())
                .overtimeDate(record.getOvertimeDate())
                .overtimeType(record.getOvertimeType())
                .startTime(record.getStartTime() != null ? record.getStartTime().toLocalTime() : null)
                .endTime(record.getEndTime() != null ? record.getEndTime().toLocalTime() : null)
                .breakDurationHours(record.getBreakHours())
                .totalHours(record.getTotalHours())
                .effectiveOvertimeHours(record.getEffectiveOvertimeHours())
                .overtimeMultiplier(record.getOvertimeMultiplier())
                .overtimePayAmount(record.getTotalOvertimePay())
                .status(record.getStatus())
                .supervisorId(record.getSupervisorId())
                .supervisorApproved(record.getSupervisorApproved())
                .supervisorApprovalDate(record.getSupervisorApprovedAt())
                .supervisorComments(record.getSupervisorComments())
                .exceedsDailyLimit(record.getExceedsDailyLimit())
                .exceedsWeeklyLimit(record.getExceedsWeeklyLimit())
                .taskDescription(record.getWorkDescription())
                .notes(record.getNotes())
                .paid(record.getPaid())
                .paymentDate(record.getPaymentDate() != null ? record.getPaymentDate().atStartOfDay() : null)
                .createdAt(record.getCreatedAt())
                .createdBy(record.getCreatedBy())
                .updatedAt(record.getUpdatedAt())
                .updatedBy(record.getUpdatedBy())
                .build();
    }
}
