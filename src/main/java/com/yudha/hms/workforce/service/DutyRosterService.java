package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.RosterStatus;
import com.yudha.hms.workforce.entity.DutyRoster;
import com.yudha.hms.workforce.repository.DutyRosterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola jadwal dinas (Duty Roster)
 * Manages employee scheduling and duty rosters per department
 */
@Service
@RequiredArgsConstructor
public class DutyRosterService {

    private final DutyRosterRepository dutyRosterRepository;

    @Transactional(readOnly = true)
    public DutyRoster getDutyRosterById(UUID id) {
        return dutyRosterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal dinas tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getEmployeeRosterForDate(UUID employeeId, LocalDate date) {
        return dutyRosterRepository.findByEmployeeIdAndRosterDate(employeeId, date);
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getEmployeeRosterForDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return dutyRosterRepository.findByEmployeeIdAndRosterDateBetween(employeeId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getDepartmentRosterForDate(UUID departmentId, LocalDate date) {
        return dutyRosterRepository.findByDepartmentIdAndRosterDate(departmentId, date);
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getDepartmentRosterForDateRange(UUID departmentId, LocalDate startDate, LocalDate endDate) {
        return dutyRosterRepository.findByDepartmentIdAndRosterDateBetween(departmentId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getUnpublishedRosters(LocalDate startDate, LocalDate endDate) {
        return dutyRosterRepository.findUnpublishedRosters(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<DutyRoster> getUnapprovedRosters(LocalDate startDate, LocalDate endDate, RosterStatus status) {
        return dutyRosterRepository.findUnapprovedRosters(startDate, endDate, status);
    }

    @Transactional
    public DutyRoster createDutyRoster(DutyRoster dutyRoster) {
        dutyRoster.setStatus(RosterStatus.SCHEDULED);
        return dutyRosterRepository.save(dutyRoster);
    }

    @Transactional
    public DutyRoster updateDutyRoster(UUID id, DutyRoster dutyRoster) {
        DutyRoster existing = getDutyRosterById(id);
        existing.setScheduledStartTime(dutyRoster.getScheduledStartTime());
        existing.setScheduledEndTime(dutyRoster.getScheduledEndTime());
        existing.setScheduledHours(dutyRoster.getScheduledHours());
        existing.setAssignedLocation(dutyRoster.getAssignedLocation());
        existing.setAssignedRole(dutyRoster.getAssignedRole());
        existing.setNotes(dutyRoster.getNotes());
        return dutyRosterRepository.save(existing);
    }

    @Transactional
    public DutyRoster approveRoster(UUID id, UUID approverId) {
        DutyRoster roster = getDutyRosterById(id);
        roster.setApproved(true);
        roster.setApprovedBy(approverId);
        roster.setApprovedAt(LocalDateTime.now());
        roster.setStatus(RosterStatus.CONFIRMED);
        return dutyRosterRepository.save(roster);
    }

    @Transactional
    public DutyRoster publishRoster(UUID id) {
        DutyRoster roster = getDutyRosterById(id);
        if (!roster.getApproved()) {
            throw new RuntimeException("Jadwal harus disetujui sebelum dipublikasikan");
        }
        roster.setPublished(true);
        roster.setPublishedAt(LocalDateTime.now());
        return dutyRosterRepository.save(roster);
    }

    @Transactional
    public void publishRostersForPeriod(LocalDate startDate, LocalDate endDate) {
        List<DutyRoster> rosters = dutyRosterRepository.findByDepartmentDateAndStatus(null, null, RosterStatus.CONFIRMED);
        rosters.forEach(roster -> {
            if (roster.getRosterDate().isAfter(startDate.minusDays(1)) &&
                roster.getRosterDate().isBefore(endDate.plusDays(1))) {
                roster.setPublished(true);
                roster.setPublishedAt(LocalDateTime.now());
                dutyRosterRepository.save(roster);
            }
        });
    }

    @Transactional
    public DutyRoster cancelRoster(UUID id, String reason) {
        DutyRoster roster = getDutyRosterById(id);
        roster.setStatus(RosterStatus.CANCELLED);
        roster.setNotes(roster.getNotes() + "\nDibatalkan: " + reason);
        return dutyRosterRepository.save(roster);
    }

    @Transactional
    public Long countEmployeeRosterDays(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return dutyRosterRepository.countEmployeeRosterDays(employeeId, startDate, endDate);
    }
}
