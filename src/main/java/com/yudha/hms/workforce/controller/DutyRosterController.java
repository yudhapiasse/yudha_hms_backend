package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.constant.RosterStatus;
import com.yudha.hms.workforce.entity.DutyRoster;
import com.yudha.hms.workforce.service.DutyRosterService;
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
 * REST Controller untuk Manajemen Jadwal Dinas (Duty Roster Management)
 *
 * Endpoints:
 * - Create/update duty roster
 * - View roster by employee/department
 * - Approve roster
 * - Publish roster
 * - Cancel roster
 *
 * Mendukung penjadwalan shift per departemen
 */
@RestController
@RequestMapping("/api/workforce/duty-roster")
@RequiredArgsConstructor
@Slf4j
public class DutyRosterController {

    private final DutyRosterService dutyRosterService;

    /**
     * Buat jadwal dinas baru
     * POST /api/workforce/duty-roster
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DutyRoster>> createDutyRoster(
            @RequestBody DutyRoster dutyRoster) {

        log.info("POST /api/workforce/duty-roster - Employee: {}, Date: {}",
                dutyRoster.getEmployeeId(), dutyRoster.getRosterDate());

        DutyRoster created = dutyRosterService.createDutyRoster(dutyRoster);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Jadwal dinas berhasil dibuat", created));
    }

    /**
     * Update jadwal dinas
     * PUT /api/workforce/duty-roster/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DutyRoster>> updateDutyRoster(
            @PathVariable UUID id,
            @RequestBody DutyRoster dutyRoster) {

        log.info("PUT /api/workforce/duty-roster/{}", id);

        DutyRoster updated = dutyRosterService.updateDutyRoster(id, dutyRoster);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas berhasil diupdate", updated));
    }

    /**
     * Dapatkan jadwal dinas karyawan untuk tanggal tertentu
     * GET /api/workforce/duty-roster/employee/{employeeId}/date/{date}
     */
    @GetMapping("/employee/{employeeId}/date/{date}")
    public ResponseEntity<ApiResponse<List<DutyRoster>>> getEmployeeRosterForDate(
            @PathVariable UUID employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("GET /api/workforce/duty-roster/employee/{}/date/{}", employeeId, date);

        List<DutyRoster> rosters = dutyRosterService.getEmployeeRosterForDate(employeeId, date);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas ditemukan", rosters));
    }

    /**
     * Dapatkan jadwal dinas karyawan untuk periode tertentu
     * GET /api/workforce/duty-roster/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<DutyRoster>>> getEmployeeRosterForPeriod(
            @PathVariable UUID employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/duty-roster/employee/{} - Period: {} to {}",
                employeeId, startDate, endDate);

        List<DutyRoster> rosters = dutyRosterService.getEmployeeRosterForDateRange(
                employeeId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas ditemukan", rosters));
    }

    /**
     * Dapatkan jadwal dinas departemen
     * GET /api/workforce/duty-roster/department/{departmentId}
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<DutyRoster>>> getDepartmentRoster(
            @PathVariable UUID departmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/duty-roster/department/{} - Period: {} to {}",
                departmentId, startDate, endDate);

        List<DutyRoster> rosters = dutyRosterService.getDepartmentRosterForDateRange(
                departmentId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas departemen ditemukan", rosters));
    }

    /**
     * Setujui jadwal dinas
     * PUT /api/workforce/duty-roster/{id}/approve
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<DutyRoster>> approveRoster(
            @PathVariable UUID id,
            @RequestParam UUID approverId) {

        log.info("PUT /api/workforce/duty-roster/{}/approve - Approver: {}", id, approverId);

        DutyRoster approved = dutyRosterService.approveRoster(id, approverId);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas berhasil disetujui", approved));
    }

    /**
     * Publikasikan jadwal dinas
     * PUT /api/workforce/duty-roster/{id}/publish
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<DutyRoster>> publishRoster(@PathVariable UUID id) {

        log.info("PUT /api/workforce/duty-roster/{}/publish", id);

        DutyRoster published = dutyRosterService.publishRoster(id);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas berhasil dipublikasikan", published));
    }

    /**
     * Publikasikan jadwal dinas untuk periode tertentu
     * PUT /api/workforce/duty-roster/publish-period
     */
    @PutMapping("/publish-period")
    public ResponseEntity<ApiResponse<String>> publishRostersForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("PUT /api/workforce/duty-roster/publish-period - Period: {} to {}", startDate, endDate);

        dutyRosterService.publishRostersForPeriod(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas periode berhasil dipublikasikan", null));
    }

    /**
     * Batalkan jadwal dinas
     * PUT /api/workforce/duty-roster/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<DutyRoster>> cancelRoster(
            @PathVariable UUID id,
            @RequestParam String reason) {

        log.info("PUT /api/workforce/duty-roster/{}/cancel", id);

        DutyRoster cancelled = dutyRosterService.cancelRoster(id, reason);

        return ResponseEntity.ok(ApiResponse.success("Jadwal dinas berhasil dibatalkan", cancelled));
    }

    /**
     * Dapatkan jadwal yang belum dipublikasikan
     * GET /api/workforce/duty-roster/unpublished
     */
    @GetMapping("/unpublished")
    public ResponseEntity<ApiResponse<List<DutyRoster>>> getUnpublishedRosters(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/duty-roster/unpublished - Period: {} to {}", startDate, endDate);

        List<DutyRoster> rosters = dutyRosterService.getUnpublishedRosters(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Jadwal yang belum dipublikasikan", rosters));
    }
}
