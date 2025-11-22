package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.SubstitutionStatus;
import com.yudha.hms.workforce.entity.ShiftSubstitution;
import com.yudha.hms.workforce.repository.ShiftSubstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola penggantian shift (Shift Substitution)
 * Manages shift substitution and replacement
 */
@Service
@RequiredArgsConstructor
public class ShiftSubstitutionService {

    private final ShiftSubstitutionRepository shiftSubstitutionRepository;

    @Transactional(readOnly = true)
    public ShiftSubstitution getShiftSubstitutionById(UUID id) {
        return shiftSubstitutionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Penggantian shift tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public ShiftSubstitution getShiftSubstitutionByNumber(String requestNumber) {
        return shiftSubstitutionRepository.findByRequestNumber(requestNumber)
                .orElseThrow(() -> new RuntimeException("Penggantian shift tidak ditemukan dengan nomor: " + requestNumber));
    }

    @Transactional(readOnly = true)
    public List<ShiftSubstitution> getSubstitutionsByOriginalEmployee(UUID employeeId) {
        return shiftSubstitutionRepository.findByOriginalEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<ShiftSubstitution> getSubstitutionsBySubstituteEmployee(UUID employeeId) {
        return shiftSubstitutionRepository.findBySubstituteEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<ShiftSubstitution> getPendingSubstitutions(UUID employeeId) {
        return shiftSubstitutionRepository.findPendingSubstitutions(employeeId);
    }

    @Transactional(readOnly = true)
    public List<ShiftSubstitution> getSubstitutionsByStatus(SubstitutionStatus status) {
        return shiftSubstitutionRepository.findByStatus(status);
    }

    @Transactional
    public ShiftSubstitution createSubstitutionRequest(ShiftSubstitution substitution) {
        substitution.setStatus(SubstitutionStatus.PENDING);
        return shiftSubstitutionRepository.save(substitution);
    }

    @Transactional
    public ShiftSubstitution approveSubstitution(UUID id, UUID approverId, String comments) {
        ShiftSubstitution substitution = getShiftSubstitutionById(id);
        substitution.setStatus(SubstitutionStatus.APPROVED);
        substitution.setApprovedBy(approverId);
        substitution.setApprovedAt(LocalDateTime.now());
        substitution.setApprovalComments(comments);
        return shiftSubstitutionRepository.save(substitution);
    }

    @Transactional
    public ShiftSubstitution rejectSubstitution(UUID id, UUID approverId, String reason) {
        ShiftSubstitution substitution = getShiftSubstitutionById(id);
        substitution.setStatus(SubstitutionStatus.REJECTED);
        substitution.setApprovedBy(approverId);
        substitution.setApprovedAt(LocalDateTime.now());
        substitution.setRejectionReason(reason);
        return shiftSubstitutionRepository.save(substitution);
    }

    @Transactional
    public ShiftSubstitution confirmSubstitute(UUID id) {
        ShiftSubstitution substitution = getShiftSubstitutionById(id);

        if (substitution.getStatus() != SubstitutionStatus.APPROVED) {
            throw new RuntimeException("Penggantian shift harus disetujui terlebih dahulu");
        }

        substitution.setSubstituteConfirmed(true);
        substitution.setSubstituteConfirmedAt(LocalDateTime.now());
        return shiftSubstitutionRepository.save(substitution);
    }

    @Transactional
    public ShiftSubstitution completeSubstitution(UUID id, String completionNotes) {
        ShiftSubstitution substitution = getShiftSubstitutionById(id);

        if (!substitution.getSubstituteConfirmed()) {
            throw new RuntimeException("Penggantian shift harus dikonfirmasi oleh pengganti");
        }

        substitution.setCompleted(true);
        substitution.setCompletedAt(LocalDateTime.now());
        substitution.setCompletionNotes(completionNotes);
        substitution.setStatus(SubstitutionStatus.COMPLETED);

        return shiftSubstitutionRepository.save(substitution);
    }

    @Transactional
    public void cancelSubstitution(UUID id, String reason) {
        ShiftSubstitution substitution = getShiftSubstitutionById(id);

        if (substitution.getCompleted()) {
            throw new RuntimeException("Tidak dapat membatalkan penggantian yang sudah selesai");
        }

        substitution.setStatus(SubstitutionStatus.CANCELLED);
        substitution.setNotes(substitution.getNotes() + "\nDibatalkan: " + reason);
        shiftSubstitutionRepository.save(substitution);
    }
}
