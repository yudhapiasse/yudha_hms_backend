package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.constant.AttendanceStatus;
import com.yudha.hms.workforce.entity.AttendanceRecord;
import com.yudha.hms.workforce.service.AttendanceRecordService;
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
 * REST Controller untuk Manajemen Kehadiran (Attendance Management)
 *
 * Endpoints:
 * - Record check-in (fingerprint/face recognition integration)
 * - Record check-out
 * - View attendance records
 * - Generate attendance reports
 * - Validate attendance
 * - Approve overtime
 *
 * Mendukung integrasi dengan sistem fingerprint dan face recognition
 */
@RestController
@RequestMapping("/api/workforce/attendance")
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceRecordService attendanceRecordService;

    /**
     * Catat check-in karyawan
     * POST /api/workforce/attendance/check-in
     */
    @PostMapping("/check-in")
    public ResponseEntity<ApiResponse<AttendanceRecord>> checkIn(
            @RequestBody AttendanceRecord attendanceRecord) {

        log.info("POST /api/workforce/attendance/check-in - Employee: {}, Date: {}",
                attendanceRecord.getEmployeeId(), attendanceRecord.getAttendanceDate());

        AttendanceRecord record = attendanceRecordService.recordCheckIn(attendanceRecord);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Check-in berhasil dicatat", record));
    }

    /**
     * Catat check-out karyawan
     * POST /api/workforce/attendance/check-out
     */
    @PostMapping("/check-out")
    public ResponseEntity<ApiResponse<AttendanceRecord>> checkOut(
            @RequestParam UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody AttendanceRecord checkOutData) {

        log.info("POST /api/workforce/attendance/check-out - Employee: {}, Date: {}", employeeId, date);

        AttendanceRecord record = attendanceRecordService.recordCheckOut(employeeId, date, checkOutData);

        return ResponseEntity.ok(ApiResponse.success("Check-out berhasil dicatat", record));
    }

    /**
     * Dapatkan catatan kehadiran karyawan untuk tanggal tertentu
     * GET /api/workforce/attendance/employee/{employeeId}/date/{date}
     */
    @GetMapping("/employee/{employeeId}/date/{date}")
    public ResponseEntity<ApiResponse<AttendanceRecord>> getEmployeeAttendanceForDate(
            @PathVariable UUID employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("GET /api/workforce/attendance/employee/{}/date/{}", employeeId, date);

        AttendanceRecord record = attendanceRecordService.getEmployeeAttendanceForDate(employeeId, date);

        return ResponseEntity.ok(ApiResponse.success("Catatan kehadiran ditemukan", record));
    }

    /**
     * Dapatkan catatan kehadiran karyawan untuk periode tertentu
     * GET /api/workforce/attendance/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getEmployeeAttendanceForPeriod(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/attendance/employee/{} - Period: {} to {}",
                employeeId, startDate, endDate);

        List<AttendanceRecord> records = attendanceRecordService.getEmployeeAttendanceForDateRange(
                employeeId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Catatan kehadiran ditemukan", records));
    }

    /**
     * Dapatkan catatan kehadiran departemen untuk tanggal tertentu
     * GET /api/workforce/attendance/department/{departmentId}
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getDepartmentAttendance(
            @PathVariable UUID departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/attendance/department/{} - Period: {} to {}",
                departmentId, startDate, endDate);

        List<AttendanceRecord> records = attendanceRecordService.getDepartmentAttendanceForDateRange(
                departmentId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Catatan kehadiran departemen ditemukan", records));
    }

    /**
     * Dapatkan daftar keterlambatan
     * GET /api/workforce/attendance/late
     */
    @GetMapping("/late")
    public ResponseEntity<ApiResponse<List<AttendanceRecord>>> getLateAttendances(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/attendance/late - Period: {} to {}", startDate, endDate);

        List<AttendanceRecord> records = attendanceRecordService.getLateAttendances(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Daftar keterlambatan ditemukan", records));
    }

    /**
     * Validasi catatan kehadiran
     * PUT /api/workforce/attendance/{id}/validate
     */
    @PutMapping("/{id}/validate")
    public ResponseEntity<ApiResponse<AttendanceRecord>> validateAttendance(
            @PathVariable UUID id,
            @RequestParam UUID validatorId,
            @RequestParam boolean isValid,
            @RequestParam(required = false) String notes) {

        log.info("PUT /api/workforce/attendance/{}/validate - Validator: {}", id, validatorId);

        AttendanceRecord record = attendanceRecordService.validateAttendance(id, validatorId, isValid, notes);

        return ResponseEntity.ok(ApiResponse.success("Kehadiran berhasil divalidasi", record));
    }

    /**
     * Setujui lembur
     * PUT /api/workforce/attendance/{id}/approve-overtime
     */
    @PutMapping("/{id}/approve-overtime")
    public ResponseEntity<ApiResponse<AttendanceRecord>> approveOvertime(
            @PathVariable UUID id,
            @RequestParam UUID approverId) {

        log.info("PUT /api/workforce/attendance/{}/approve-overtime - Approver: {}", id, approverId);

        AttendanceRecord record = attendanceRecordService.approveOvertime(id, approverId);

        return ResponseEntity.ok(ApiResponse.success("Lembur berhasil disetujui", record));
    }

    /**
     * Hitung statistik kehadiran karyawan
     * GET /api/workforce/attendance/employee/{employeeId}/statistics
     */
    @GetMapping("/employee/{employeeId}/statistics")
    public ResponseEntity<ApiResponse<Long>> getAttendanceStatistics(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam AttendanceStatus status) {

        log.info("GET /api/workforce/attendance/employee/{}/statistics - Status: {}", employeeId, status);

        Long count = attendanceRecordService.countEmployeeAttendanceByStatus(
                employeeId, startDate, endDate, status);

        return ResponseEntity.ok(ApiResponse.success("Statistik kehadiran", count));
    }
}
