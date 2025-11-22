package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.ApprovalStatus;
import com.yudha.hms.workforce.constant.LeaveRequestStatus;
import com.yudha.hms.workforce.entity.LeaveRequest;
import com.yudha.hms.workforce.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola permohonan cuti (Leave Request)
 * Manages leave requests including cuti tahunan, sakit, melahirkan
 * Implements Indonesian labor law compliance (UU Ketenagakerjaan)
 */
@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceService leaveBalanceService;

    @Transactional(readOnly = true)
    public LeaveRequest getLeaveRequestById(UUID id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permohonan cuti tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public LeaveRequest getLeaveRequestByNumber(String requestNumber) {
        return leaveRequestRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new RuntimeException("Permohonan cuti tidak ditemukan dengan nomor: " + requestNumber));
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getEmployeeLeaveRequests(UUID employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getEmployeeLeaveRequestsByStatus(UUID employeeId, LeaveRequestStatus status) {
        return leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, status);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getPendingLeaveRequestsForSupervisor(UUID supervisorId) {
        return leaveRequestRepository.findPendingForSupervisor(supervisorId);
    }

    @Transactional(readOnly = true)
    public List<LeaveRequest> getApprovedLeavesOnDate(LocalDate date) {
        return leaveRequestRepository.findApprovedLeavesOnDate(date);
    }

    @Transactional
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        // Check for overlapping leaves
        List<LeaveRequest> overlapping = leaveRequestRepository.findOverlappingLeaves(
            leaveRequest.getEmployeeId(),
            leaveRequest.getStartDate(),
            LeaveRequestStatus.APPROVED
        );

        if (!overlapping.isEmpty()) {
            throw new RuntimeException("Sudah ada cuti yang disetujui pada periode yang sama");
        }

        // Check leave balance
        // TODO: Implement leave balance checking

        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        leaveRequest.setImmediateSupervisorStatus(ApprovalStatus.PENDING);

        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest updateLeaveRequest(UUID id, LeaveRequest leaveRequest) {
        LeaveRequest existing = getLeaveRequestById(id);

        if (existing.getStatus() != LeaveRequestStatus.PENDING) {
            throw new RuntimeException("Hanya permohonan yang berstatus PENDING yang dapat diubah");
        }

        existing.setStartDate(leaveRequest.getStartDate());
        existing.setEndDate(leaveRequest.getEndDate());
        existing.setTotalDays(leaveRequest.getTotalDays());
        existing.setReason(leaveRequest.getReason());
        existing.setEmergencyContactDuringLeave(leaveRequest.getEmergencyContactDuringLeave());
        existing.setEmergencyPhoneDuringLeave(leaveRequest.getEmergencyPhoneDuringLeave());

        return leaveRequestRepository.save(existing);
    }

    @Transactional
    public LeaveRequest supervisorApproval(UUID id, UUID supervisorId, boolean approved, String comments) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        leaveRequest.setImmediateSupervisorId(supervisorId);
        leaveRequest.setImmediateSupervisorStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        leaveRequest.setImmediateSupervisorComments(comments);
        leaveRequest.setImmediateSupervisorActionAt(LocalDateTime.now());

        if (!approved) {
            leaveRequest.setStatus(LeaveRequestStatus.REJECTED);
            leaveRequest.setRejectionReason(comments);
        } else {
            // Move to HRD approval
            leaveRequest.setHrdStatus(ApprovalStatus.PENDING);
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest hrdApproval(UUID id, UUID hrdId, boolean approved, String comments) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        if (leaveRequest.getImmediateSupervisorStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("Persetujuan supervisor diperlukan terlebih dahulu");
        }

        leaveRequest.setHrdApproverId(hrdId);
        leaveRequest.setHrdStatus(approved ? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED);
        leaveRequest.setHrdComments(comments);
        leaveRequest.setHrdActionAt(LocalDateTime.now());

        if (approved) {
            leaveRequest.setStatus(LeaveRequestStatus.APPROVED);
            // TODO: Deduct from leave balance
        } else {
            leaveRequest.setStatus(LeaveRequestStatus.REJECTED);
            leaveRequest.setRejectionReason(comments);
        }

        return leaveRequestRepository.save(leaveRequest);
    }

    @Transactional
    public LeaveRequest cancelLeaveRequest(UUID id, UUID cancelledBy, String reason) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);

        if (leaveRequest.getStatus() == LeaveRequestStatus.APPROVED &&
            leaveRequest.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Tidak dapat membatalkan cuti yang sudah dimulai");
        }

        leaveRequest.setStatus(LeaveRequestStatus.CANCELLED);
        leaveRequest.setCancelledBy(cancelledBy);
        leaveRequest.setCancellationReason(reason);
        leaveRequest.setCancelledAt(LocalDateTime.now());

        // TODO: Restore leave balance if already deducted

        return leaveRequestRepository.save(leaveRequest);
    }
}
