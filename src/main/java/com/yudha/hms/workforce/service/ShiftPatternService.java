package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.ShiftType;
import com.yudha.hms.workforce.entity.ShiftPattern;
import com.yudha.hms.workforce.repository.ShiftPatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola pola shift (Shift Pattern)
 * Manages shift patterns including pagi (morning), siang (afternoon), malam (night), and libur (off)
 */
@Service
@RequiredArgsConstructor
public class ShiftPatternService {

    private final ShiftPatternRepository shiftPatternRepository;

    @Transactional(readOnly = true)
    public ShiftPattern getShiftPatternById(UUID id) {
        return shiftPatternRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pola shift tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public ShiftPattern getShiftPatternByCode(String shiftCode) {
        return shiftPatternRepository.findByShiftCode(shiftCode)
                .orElseThrow(() -> new RuntimeException("Pola shift tidak ditemukan dengan kode: " + shiftCode));
    }

    @Transactional(readOnly = true)
    public List<ShiftPattern> getAllActiveShiftPatterns() {
        return shiftPatternRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<ShiftPattern> getShiftPatternsByType(ShiftType shiftType) {
        return shiftPatternRepository.findByShiftType(shiftType);
    }

    @Transactional(readOnly = true)
    public List<ShiftPattern> getShiftPatternsByDepartment(UUID departmentId) {
        return shiftPatternRepository.findByDepartmentIdAndActiveTrue(departmentId);
    }

    @Transactional(readOnly = true)
    public List<ShiftPattern> getHospitalWideShiftPatterns() {
        return shiftPatternRepository.findByDepartmentIdIsNullAndActiveTrue();
    }

    @Transactional(readOnly = true)
    public ShiftPattern getDefaultShiftPattern() {
        return shiftPatternRepository.findByIsDefaultTrueAndDepartmentIdIsNull()
                .orElseThrow(() -> new RuntimeException("Pola shift default tidak ditemukan"));
    }

    @Transactional
    public ShiftPattern createShiftPattern(ShiftPattern shiftPattern) {
        return shiftPatternRepository.save(shiftPattern);
    }

    @Transactional
    public ShiftPattern updateShiftPattern(UUID id, ShiftPattern shiftPattern) {
        ShiftPattern existing = getShiftPatternById(id);
        // Update fields
        existing.setShiftName(shiftPattern.getShiftName());
        existing.setShiftNameId(shiftPattern.getShiftNameId());
        existing.setShiftType(shiftPattern.getShiftType());
        existing.setStartTime(shiftPattern.getStartTime());
        existing.setEndTime(shiftPattern.getEndTime());
        existing.setDurationHours(shiftPattern.getDurationHours());
        existing.setEffectiveHours(shiftPattern.getEffectiveHours());
        existing.setBreakDurationMinutes(shiftPattern.getBreakDurationMinutes());
        existing.setOvertimeMultiplier(shiftPattern.getOvertimeMultiplier());
        existing.setHolidayMultiplier(shiftPattern.getHolidayMultiplier());
        existing.setActive(shiftPattern.getActive());
        existing.setNotes(shiftPattern.getNotes());
        return shiftPatternRepository.save(existing);
    }

    @Transactional
    public void deleteShiftPattern(UUID id) {
        ShiftPattern shiftPattern = getShiftPatternById(id);
        shiftPattern.setActive(false);
        shiftPatternRepository.save(shiftPattern);
    }
}
