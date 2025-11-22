package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.constant.LeaveRequestStatus;
import com.yudha.hms.workforce.entity.LeaveRequest;
import com.yudha.hms.workforce.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller untuk Manajemen Permohonan Cuti (Leave Request Management)
 *
 * Endpoints:
 * - Submit leave request
 * - Approve/reject leave request
 * - View leave requests
 * - Cancel leave request
 *
 * Mendukung cuti tahunan, sakit, melahirkan sesuai UU Ketenagakerjaan Indonesia
 */
@RestController
@RequestMapping("/api/workforce/leave-requests")
@RequiredArgsConstructor
@Slf4j
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    /**
     * Buat permohonan cuti baru
     * POST /api/workforce/leave-requests
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LeaveRequest>> createLeaveRequest(
            @RequestBody LeaveRequest leaveRequest) {

        log.info("POST /api/workforce/leave-requests - Employee: {}, Type: {}",
                leaveRequest.getEmployeeId(), leaveRequest.getLeaveTypeId());

        LeaveRequest created = leaveRequestService.createLeaveRequest(leaveRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Permohonan cuti berhasil diajukan", created));
    }

    /**
     * Update permohonan cuti
     * PUT /api/workforce/leave-requests/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequest>> updateLeaveRequest(
            @PathVariable UUID id,
            @RequestBody LeaveRequest leaveRequest) {

        log.info("PUT /api/workforce/leave-requests/{}", id);

        LeaveRequest updated = leaveRequestService.updateLeaveRequest(id, leaveRequest);

        return ResponseEntity.ok(ApiResponse.success("Permohonan cuti berhasil diupdate", updated));
    }

    /**
     * Dapatkan permohonan cuti berdasarkan ID
     * GET /api/workforce/leave-requests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LeaveRequest>> getLeaveRequestById(@PathVariable UUID id) {

        log.info("GET /api/workforce/leave-requests/{}", id);

        LeaveRequest leaveRequest = leaveRequestService.getLeaveRequestById(id);

        return ResponseEntity.ok(ApiResponse.success("Permohonan cuti ditemukan", leaveRequest));
    }

    /**
     * Dapatkan permohonan cuti karyawan
     * GET /api/workforce/leave-requests/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getEmployeeLeaveRequests(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) LeaveRequestStatus status) {

        log.info("GET /api/workforce/leave-requests/employee/{} - Status: {}", employeeId, status);

        List<LeaveRequest> requests = status != null
                ? leaveRequestService.getEmployeeLeaveRequestsByStatus(employeeId, status)
                : leaveRequestService.getEmployeeLeaveRequests(employeeId);

        return ResponseEntity.ok(ApiResponse.success("Permohonan cuti karyawan", requests));
    }

    /**
     * Dapatkan permohonan cuti yang menunggu persetujuan supervisor
     * GET /api/workforce/leave-requests/supervisor/{supervisorId}/pending
     */
    @GetMapping("/supervisor/{supervisorId}/pending")
    public ResponseEntity<ApiResponse<List<LeaveRequest>>> getPendingForSupervisor(
            @PathVariable UUID supervisorId) {

        log.info("GET /api/workforce/leave-requests/supervisor/{}/pending", supervisorId);

        List<LeaveRequest> requests = leaveRequestService.getPendingLeaveRequestsForSupervisor(supervisorId);

        return ResponseEntity.ok(ApiResponse.success("Permohonan cuti menunggu persetujuan", requests));
    }

    /**
     * Persetujuan supervisor
     * PUT /api/workforce/leave-requests/{id}/supervisor-approval
     */
    @PutMapping("/{id}/supervisor-approval")
    public ResponseEntity<ApiResponse<LeaveRequest>> supervisorApproval(
            @PathVariable UUID id,
            @RequestParam UUID supervisorId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comments) {

        log.info("PUT /api/workforce/leave-requests/{}/supervisor-approval - Supervisor: {}, Approved: {}",
                id, supervisorId, approved);

        LeaveRequest updated = leaveRequestService.supervisorApproval(id, supervisorId, approved, comments);

        return ResponseEntity.ok(ApiResponse.success("Persetujuan supervisor berhasil dicatat", updated));
    }

    /**
     * Persetujuan HRD
     * PUT /api/workforce/leave-requests/{id}/hrd-approval
     */
    @PutMapping("/{id}/hrd-approval")
    public ResponseEntity<ApiResponse<LeaveRequest>> hrdApproval(
            @PathVariable UUID id,
            @RequestParam UUID hrdId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String comments) {

        log.info("PUT /api/workforce/leave-requests/{}/hrd-approval - HRD: {}, Approved: {}",
                id, hrdId, approved);

        LeaveRequest updated = leaveRequestService.hrdApproval(id, hrdId, approved, comments);

        return ResponseEntity.ok(ApiResponse.success("Persetujuan HRD berhasil dicatat", updated));
    }

    /**
     * Batalkan permohonan cuti
     * PUT /api/workforce/leave-requests/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LeaveRequest>> cancelLeaveRequest(
            @PathVariable UUID id,
            @RequestParam UUID cancelledBy,
            @RequestParam String reason) {

        log.info("PUT /api/workforce/leave-requests/{}/cancel - By: {}", id, cancelledBy);

        LeaveRequest cancelled = leaveRequestService.cancelLeaveRequest(id, cancelledBy, reason);

        return ResponseEntity.ok(ApiResponse.success("Permohonan cuti berhasil dibatalkan", cancelled));
    }
}
