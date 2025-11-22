package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.LeaveCategory;
import com.yudha.hms.workforce.entity.LeaveType;
import com.yudha.hms.workforce.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola jenis cuti (Leave Type)
 * Manages leave types including statutory leaves per Indonesian labor law
 */
@Service
@RequiredArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    @Transactional(readOnly = true)
    public LeaveType getLeaveTypeById(UUID id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jenis cuti tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public LeaveType getLeaveTypeByCode(String leaveCode) {
        return leaveTypeRepository.findByLeaveCode(leaveCode)
                .orElseThrow(() -> new RuntimeException("Jenis cuti tidak ditemukan dengan kode: " + leaveCode));
    }

    @Transactional(readOnly = true)
    public List<LeaveType> getAllActiveLeaveTypes() {
        return leaveTypeRepository.findAllActiveOrdered();
    }

    @Transactional(readOnly = true)
    public List<LeaveType> getLeaveTypesByCategory(LeaveCategory category) {
        return leaveTypeRepository.findByLeaveCategoryAndActiveTrue(category);
    }

    @Transactional(readOnly = true)
    public List<LeaveType> getStatutoryLeaveTypes() {
        return leaveTypeRepository.findByIsStatutoryTrue();
    }

    @Transactional(readOnly = true)
    public List<LeaveType> getPaidLeaveTypes() {
        return leaveTypeRepository.findByIsPaidTrueAndActiveTrue();
    }

    @Transactional
    public LeaveType createLeaveType(LeaveType leaveType) {
        return leaveTypeRepository.save(leaveType);
    }

    @Transactional
    public LeaveType updateLeaveType(UUID id, LeaveType leaveType) {
        LeaveType existing = getLeaveTypeById(id);
        existing.setLeaveName(leaveType.getLeaveName());
        existing.setLeaveNameId(leaveType.getLeaveNameId());
        existing.setLeaveCategory(leaveType.getLeaveCategory());
        existing.setIsPaid(leaveType.getIsPaid());
        existing.setRequiresApproval(leaveType.getRequiresApproval());
        existing.setMaxDaysPerYear(leaveType.getMaxDaysPerYear());
        existing.setMaxConsecutiveDays(leaveType.getMaxConsecutiveDays());
        existing.setRequiresMedicalCertificate(leaveType.getRequiresMedicalCertificate());
        existing.setMedicalCertAfterDays(leaveType.getMedicalCertAfterDays());
        existing.setCanCarryForward(leaveType.getCanCarryForward());
        existing.setCarryForwardMaxDays(leaveType.getCarryForwardMaxDays());
        existing.setAccrualMethod(leaveType.getAccrualMethod());
        existing.setAccrualRate(leaveType.getAccrualRate());
        existing.setActive(leaveType.getActive());
        existing.setNotes(leaveType.getNotes());
        return leaveTypeRepository.save(existing);
    }

    @Transactional
    public void deleteLeaveType(UUID id) {
        LeaveType leaveType = getLeaveTypeById(id);
        leaveType.setActive(false);
        leaveTypeRepository.save(leaveType);
    }
}
