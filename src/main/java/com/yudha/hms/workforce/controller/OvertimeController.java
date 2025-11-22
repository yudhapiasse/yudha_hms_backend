package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.entity.OvertimeRecord;
import com.yudha.hms.workforce.service.OvertimeRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller untuk Manajemen Lembur (Overtime Management)
 *
 * Endpoints:
 * - Submit overtime request
 * - Approve/reject overtime
 * - View overtime records
 * - Mark overtime as paid
 *
 * Implementasi sesuai UU Ketenagakerjaan Indonesia:
 * - Hari kerja: 1.5x jam pertama, 2x jam berikutnya
 * - Akhir pekan/libur: 2x
 * - Maksimal 3 jam/hari, 14 jam/minggu
 */
@RestController
@RequestMapping("/api/workforce/overtime")
@RequiredArgsConstructor
@Slf4j
public class OvertimeController {

    private final OvertimeRecordService overtimeRecordService;

    /**
     * Buat catatan lembur baru
     * POST /api/workforce/overtime
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OvertimeRecord>> createOvertimeRecord(
            @RequestBody OvertimeRecord overtimeRecord) {

        log.info("POST /api/workforce/overtime - Employee: {}, Date: {}",
                overtimeRecord.getEmployeeId(), overtimeRecord.getOvertimeDate());

        OvertimeRecord created = overtimeRecordService.createOvertimeRecord(overtimeRecord);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Catatan lembur berhasil dibuat", created));
    }

    /**
     * Dapatkan catatan lembur berdasarkan ID
     * GET /api/workforce/overtime/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OvertimeRecord>> getOvertimeRecordById(@PathVariable UUID id) {

        log.info("GET /api/workforce/overtime/{}", id);

        OvertimeRecord record = overtimeRecordService.getOvertimeRecordById(id);

        return ResponseEntity.ok(ApiResponse.success("Catatan lembur ditemukan", record));
    }

    /**
     * Dapatkan catatan lembur karyawan
     * GET /api/workforce/overtime/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<OvertimeRecord>>> getEmployeeOvertimeRecords(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/overtime/employee/{} - Period: {} to {}", employeeId, startDate, endDate);

        List<OvertimeRecord> records = (startDate != null && endDate != null)
                ? overtimeRecordService.getEmployeeOvertimeForDateRange(employeeId, startDate, endDate)
                : overtimeRecordService.getEmployeeOvertimeRecords(employeeId);

        return ResponseEntity.ok(ApiResponse.success("Catatan lembur karyawan", records));
    }

    /**
     * Dapatkan lembur yang menunggu persetujuan supervisor
     * GET /api/workforce/overtime/supervisor/{supervisorId}/pending
     */
    @GetMapping("/supervisor/{supervisorId}/pending")
    public ResponseEntity<ApiResponse<List<OvertimeRecord>>> getPendingForSupervisor(
            @PathVariable UUID supervisorId) {

        log.info("GET /api/workforce/overtime/supervisor/{}/pending", supervisorId);

        List<OvertimeRecord> records = overtimeRecordService.getPendingOvertimeForSupervisor(supervisorId);

        return ResponseEntity.ok(ApiResponse.success("Lembur menunggu persetujuan", records));
    }

    /**
     * Persetujuan supervisor
     * PUT /api/workforce/overtime/{id}/supervisor-approval
     */
    @PutMapping("/{id}/supervisor-approval")
    public ResponseEntity<ApiResponse<OvertimeRecord>> supervisorApproval(
            @PathVariable UUID id,
            @RequestParam UUID supervisorId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comments) {

        log.info("PUT /api/workforce/overtime/{}/supervisor-approval - Supervisor: {}, Approved: {}",
                id, supervisorId, approved);

        OvertimeRecord updated = overtimeRecordService.supervisorApproval(id, supervisorId, approved, comments);

        return ResponseEntity.ok(ApiResponse.success("Persetujuan supervisor berhasil dicatat", updated));
    }

    /**
     * Persetujuan HRD
     * PUT /api/workforce/overtime/{id}/hrd-approval
     */
    @PutMapping("/{id}/hrd-approval")
    public ResponseEntity<ApiResponse<OvertimeRecord>> hrdApproval(
            @PathVariable UUID id,
            @RequestParam UUID hrdId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comments) {

        log.info("PUT /api/workforce/overtime/{}/hrd-approval - HRD: {}, Approved: {}",
                id, hrdId, approved);

        OvertimeRecord updated = overtimeRecordService.hrdApproval(id, hrdId, approved, comments);

        return ResponseEntity.ok(ApiResponse.success("Persetujuan HRD berhasil dicatat", updated));
    }

    /**
     * Tandai lembur sebagai sudah dibayar
     * PUT /api/workforce/overtime/{id}/mark-paid
     */
    @PutMapping("/{id}/mark-paid")
    public ResponseEntity<ApiResponse<OvertimeRecord>> markAsPaid(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam String paymentReference) {

        log.info("PUT /api/workforce/overtime/{}/mark-paid - Payment Date: {}", id, paymentDate);

        OvertimeRecord updated = overtimeRecordService.markAsPaid(id, paymentDate, paymentReference);

        return ResponseEntity.ok(ApiResponse.success("Lembur ditandai sebagai sudah dibayar", updated));
    }

    /**
     * Dapatkan lembur yang belum dibayar
     * GET /api/workforce/overtime/unpaid
     */
    @GetMapping("/unpaid")
    public ResponseEntity<ApiResponse<List<OvertimeRecord>>> getUnpaidOvertimes() {

        log.info("GET /api/workforce/overtime/unpaid");

        List<OvertimeRecord> records = overtimeRecordService.getUnpaidOvertimes();

        return ResponseEntity.ok(ApiResponse.success("Lembur yang belum dibayar", records));
    }

    /**
     * Dapatkan pelanggaran compliance lembur
     * GET /api/workforce/overtime/compliance-violations
     */
    @GetMapping("/compliance-violations")
    public ResponseEntity<ApiResponse<List<OvertimeRecord>>> getComplianceViolations() {

        log.info("GET /api/workforce/overtime/compliance-violations");

        List<OvertimeRecord> records = overtimeRecordService.getComplianceViolations();

        return ResponseEntity.ok(ApiResponse.success(
                "Catatan lembur yang melanggar batas UU Ketenagakerjaan", records));
    }
}
