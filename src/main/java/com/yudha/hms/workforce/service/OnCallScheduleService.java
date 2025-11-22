package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.OnCallType;
import com.yudha.hms.workforce.constant.RosterStatus;
import com.yudha.hms.workforce.entity.OnCallSchedule;
import com.yudha.hms.workforce.repository.OnCallScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola jadwal jaga (On-Call Schedule)
 * Manages on-call scheduling for doctors and critical staff
 */
@Service
@RequiredArgsConstructor
public class OnCallScheduleService {

    private final OnCallScheduleRepository onCallScheduleRepository;

    @Transactional(readOnly = true)
    public OnCallSchedule getOnCallScheduleById(UUID id) {
        return onCallScheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal jaga tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public List<OnCallSchedule> getEmployeeOnCallForDate(UUID employeeId, LocalDate date) {
        return onCallScheduleRepository.findByEmployeeIdAndOnCallDate(employeeId, date);
    }

    @Transactional(readOnly = true)
    public List<OnCallSchedule> getEmployeeOnCallForDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return onCallScheduleRepository.findByEmployeeIdAndOnCallDateBetween(employeeId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<OnCallSchedule> getDepartmentOnCallForDate(UUID departmentId, LocalDate date) {
        return onCallScheduleRepository.findByDepartmentIdAndOnCallDate(departmentId, date);
    }

    @Transactional(readOnly = true)
    public List<OnCallSchedule> getDepartmentOnCallForDateRange(UUID departmentId, LocalDate startDate, LocalDate endDate) {
        return onCallScheduleRepository.findByDepartmentIdAndOnCallDateBetween(departmentId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<OnCallSchedule> getCallOuts(LocalDate startDate, LocalDate endDate) {
        return onCallScheduleRepository.findCallOuts(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Long countOnCallDays(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return onCallScheduleRepository.countOnCallDays(employeeId, startDate, endDate);
    }

    @Transactional
    public OnCallSchedule createOnCallSchedule(OnCallSchedule onCallSchedule) {
        onCallSchedule.setStatus(RosterStatus.SCHEDULED);
        return onCallScheduleRepository.save(onCallSchedule);
    }

    @Transactional
    public OnCallSchedule updateOnCallSchedule(UUID id, OnCallSchedule onCallSchedule) {
        OnCallSchedule existing = getOnCallScheduleById(id);
        existing.setStartTime(onCallSchedule.getStartTime());
        existing.setEndTime(onCallSchedule.getEndTime());
        existing.setOnCallType(onCallSchedule.getOnCallType());
        existing.setSpecialization(onCallSchedule.getSpecialization());
        existing.setRequiredResponseTimeMinutes(onCallSchedule.getRequiredResponseTimeMinutes());
        existing.setOnCallRate(onCallSchedule.getOnCallRate());
        existing.setCallOutRate(onCallSchedule.getCallOutRate());
        existing.setNotes(onCallSchedule.getNotes());
        return onCallScheduleRepository.save(existing);
    }

    @Transactional
    public OnCallSchedule recordCallOut(UUID id, String reason) {
        OnCallSchedule schedule = getOnCallScheduleById(id);
        schedule.setWasCalledOut(true);
        schedule.setCallOutTime(LocalDateTime.now());
        schedule.setCallOutReason(reason);
        schedule.setStatus(RosterStatus.COMPLETED);
        return onCallScheduleRepository.save(schedule);
    }

    @Transactional
    public OnCallSchedule completeOnCallSchedule(UUID id) {
        OnCallSchedule schedule = getOnCallScheduleById(id);
        schedule.setActualEndTime(LocalDateTime.now());
        schedule.setStatus(RosterStatus.COMPLETED);
        // TODO: Calculate total hours
        return onCallScheduleRepository.save(schedule);
    }

    @Transactional
    public void cancelOnCallSchedule(UUID id, String reason) {
        OnCallSchedule schedule = getOnCallScheduleById(id);
        schedule.setStatus(RosterStatus.CANCELLED);
        schedule.setNotes(schedule.getNotes() + "\nDibatalkan: " + reason);
        onCallScheduleRepository.save(schedule);
    }
}
