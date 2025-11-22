package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.AttendanceStatus;
import com.yudha.hms.workforce.entity.AttendanceRecord;
import com.yudha.hms.workforce.repository.AttendanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola catatan kehadiran (Attendance Record)
 * Supports fingerprint/face recognition integration and attendance tracking
 */
@Service
@RequiredArgsConstructor
public class AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    @Transactional(readOnly = true)
    public AttendanceRecord getAttendanceRecordById(UUID id) {
        return attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catatan kehadiran tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public AttendanceRecord getEmployeeAttendanceForDate(UUID employeeId, LocalDate date) {
        return attendanceRecordRepository.findByEmployeeIdAndAttendanceDate(employeeId, date)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getEmployeeAttendanceForDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getDepartmentAttendanceForDate(UUID departmentId, LocalDate date) {
        return attendanceRecordRepository.findByDepartmentIdAndAttendanceDate(departmentId, date);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getDepartmentAttendanceForDateRange(UUID departmentId, LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findByDepartmentIdAndAttendanceDateBetween(departmentId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getLateAttendances(LocalDate startDate, LocalDate endDate) {
        return attendanceRecordRepository.findLateAttendances(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<AttendanceRecord> getInvalidAttendances(LocalDate date) {
        return attendanceRecordRepository.findInvalidAttendances(date);
    }

    @Transactional
    public AttendanceRecord recordCheckIn(AttendanceRecord attendanceRecord) {
        // Check if attendance already exists for this employee and date
        AttendanceRecord existing = getEmployeeAttendanceForDate(
            attendanceRecord.getEmployeeId(),
            attendanceRecord.getAttendanceDate()
        );

        if (existing != null) {
            throw new RuntimeException("Kehadiran sudah tercatat untuk tanggal ini");
        }

        attendanceRecord.setCheckInTime(LocalDateTime.now());
        return attendanceRecordRepository.save(attendanceRecord);
    }

    @Transactional
    public AttendanceRecord recordCheckOut(UUID employeeId, LocalDate date, AttendanceRecord checkOutData) {
        AttendanceRecord attendance = getEmployeeAttendanceForDate(employeeId, date);

        if (attendance == null) {
            throw new RuntimeException("Tidak ada catatan check-in untuk tanggal ini");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Check-out sudah tercatat sebelumnya");
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.setCheckOutMethod(checkOutData.getCheckOutMethod());
        attendance.setCheckOutDeviceId(checkOutData.getCheckOutDeviceId());
        attendance.setCheckOutLocation(checkOutData.getCheckOutLocation());
        attendance.setCheckOutLatitude(checkOutData.getCheckOutLatitude());
        attendance.setCheckOutLongitude(checkOutData.getCheckOutLongitude());
        attendance.setCheckOutPhotoUrl(checkOutData.getCheckOutPhotoUrl());

        // TODO: Calculate working hours, break hours, and overtime

        return attendanceRecordRepository.save(attendance);
    }

    @Transactional
    public AttendanceRecord validateAttendance(UUID id, UUID validatorId, boolean isValid, String notes) {
        AttendanceRecord attendance = getAttendanceRecordById(id);
        attendance.setIsValid(isValid);
        attendance.setValidationNotes(notes);
        attendance.setValidatedBy(validatorId);
        attendance.setValidatedAt(LocalDateTime.now());
        return attendanceRecordRepository.save(attendance);
    }

    @Transactional
    public AttendanceRecord approveOvertime(UUID id, UUID approverId) {
        AttendanceRecord attendance = getAttendanceRecordById(id);
        attendance.setOvertimeApproved(true);
        attendance.setOvertimeApprovedBy(approverId);
        attendance.setOvertimeApprovedAt(LocalDateTime.now());
        return attendanceRecordRepository.save(attendance);
    }

    @Transactional(readOnly = true)
    public Long countEmployeeAttendanceByStatus(UUID employeeId, LocalDate startDate, LocalDate endDate, AttendanceStatus status) {
        return attendanceRecordRepository.countByEmployeeDateRangeAndStatus(employeeId, startDate, endDate, status);
    }
}
