package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.OvertimeStatus;
import com.yudha.hms.workforce.constant.OvertimeType;
import com.yudha.hms.workforce.entity.OvertimeRecord;
import com.yudha.hms.workforce.repository.OvertimeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola catatan lembur (Overtime Record)
 * Implements Indonesian labor law compliance for overtime calculation
 * - Weekday: 1.5x first hour, 2x subsequent hours
 * - Weekend/Holiday: 2x
 * - Max 3 hours/day, Max 14 hours/week
 */
@Service
@RequiredArgsConstructor
public class OvertimeRecordService {

    private final OvertimeRecordRepository overtimeRecordRepository;

    private static final BigDecimal MAX_DAILY_OVERTIME = BigDecimal.valueOf(3);
    private static final BigDecimal MAX_WEEKLY_OVERTIME = BigDecimal.valueOf(14);

    @Transactional(readOnly = true)
    public OvertimeRecord getOvertimeRecordById(UUID id) {
        return overtimeRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catatan lembur tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public OvertimeRecord getOvertimeRecordByNumber(String overtimeNumber) {
        return overtimeRecordRepository.findByOvertimeNumber(overtimeNumber)
                .orElseThrow(() -> new RuntimeException("Catatan lembur tidak ditemukan dengan nomor: " + overtimeNumber));
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecord> getEmployeeOvertimeRecords(UUID employeeId) {
        return overtimeRecordRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecord> getEmployeeOvertimeForDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return overtimeRecordRepository.findByEmployeeIdAndOvertimeDateBetween(employeeId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecord> getPendingOvertimeForSupervisor(UUID supervisorId) {
        return overtimeRecordRepository.findPendingForSupervisor(supervisorId);
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecord> getUnpaidOvertimes() {
        return overtimeRecordRepository.findUnpaidOvertimes();
    }

    @Transactional(readOnly = true)
    public List<OvertimeRecord> getComplianceViolations() {
        return overtimeRecordRepository.findComplianceViolations();
    }

    @Transactional
    public OvertimeRecord createOvertimeRecord(OvertimeRecord overtimeRecord) {
        // Check daily limit
        LocalDate overtimeDate = overtimeRecord.getOvertimeDate();
        LocalDate weekStart = overtimeDate.minusDays(overtimeDate.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        BigDecimal weeklyTotal = overtimeRecordRepository.sumApprovedOvertimeHours(
            overtimeRecord.getEmployeeId(), weekStart, weekEnd
        );

        if (weeklyTotal == null) {
            weeklyTotal = BigDecimal.ZERO;
        }

        // Check compliance
        if (overtimeRecord.getEffectiveOvertimeHours().compareTo(MAX_DAILY_OVERTIME) > 0) {
            overtimeRecord.setExceedsDailyLimit(true);
            overtimeRecord.setComplianceNotes("Melebihi batas harian 3 jam sesuai UU Ketenagakerjaan");
        }

        if (weeklyTotal.add(overtimeRecord.getEffectiveOvertimeHours()).compareTo(MAX_WEEKLY_OVERTIME) > 0) {
            overtimeRecord.setExceedsWeeklyLimit(true);
            overtimeRecord.setComplianceNotes(
                (overtimeRecord.getComplianceNotes() != null ? overtimeRecord.getComplianceNotes() + "; " : "") +
                "Melebihi batas mingguan 14 jam sesuai UU Ketenagakerjaan"
            );
        }

        // Calculate overtime pay based on Indonesian labor law
        BigDecimal totalPay = calculateOvertimePay(overtimeRecord);
        overtimeRecord.setTotalOvertimePay(totalPay);

        overtimeRecord.setStatus(OvertimeStatus.PENDING);
        return overtimeRecordRepository.save(overtimeRecord);
    }

    @Transactional
    public OvertimeRecord supervisorApproval(UUID id, UUID supervisorId, boolean approved, String comments) {
        OvertimeRecord overtimeRecord = getOvertimeRecordById(id);

        overtimeRecord.setSupervisorId(supervisorId);
        overtimeRecord.setSupervisorApproved(approved);
        overtimeRecord.setSupervisorComments(comments);
        overtimeRecord.setSupervisorApprovedAt(LocalDateTime.now());

        if (!approved) {
            overtimeRecord.setStatus(OvertimeStatus.REJECTED);
            overtimeRecord.setRejectionReason(comments);
        } else {
            overtimeRecord.setHrdApproved(false);
        }

        return overtimeRecordRepository.save(overtimeRecord);
    }

    @Transactional
    public OvertimeRecord hrdApproval(UUID id, UUID hrdId, boolean approved, String comments) {
        OvertimeRecord overtimeRecord = getOvertimeRecordById(id);

        if (!overtimeRecord.getSupervisorApproved()) {
            throw new RuntimeException("Persetujuan supervisor diperlukan terlebih dahulu");
        }

        overtimeRecord.setHrdApproverId(hrdId);
        overtimeRecord.setHrdApproved(approved);
        overtimeRecord.setHrdComments(comments);
        overtimeRecord.setHrdApprovedAt(LocalDateTime.now());

        if (approved) {
            overtimeRecord.setStatus(OvertimeStatus.APPROVED);
        } else {
            overtimeRecord.setStatus(OvertimeStatus.REJECTED);
            overtimeRecord.setRejectionReason(comments);
        }

        return overtimeRecordRepository.save(overtimeRecord);
    }

    @Transactional
    public OvertimeRecord markAsPaid(UUID id, LocalDate paymentDate, String paymentReference) {
        OvertimeRecord overtimeRecord = getOvertimeRecordById(id);

        if (overtimeRecord.getStatus() != OvertimeStatus.APPROVED) {
            throw new RuntimeException("Hanya lembur yang disetujui yang dapat dibayar");
        }

        overtimeRecord.setPaid(true);
        overtimeRecord.setPaymentDate(paymentDate);
        overtimeRecord.setPaymentReference(paymentReference);
        overtimeRecord.setStatus(OvertimeStatus.PAID);

        return overtimeRecordRepository.save(overtimeRecord);
    }

    private BigDecimal calculateOvertimePay(OvertimeRecord overtimeRecord) {
        BigDecimal hours = overtimeRecord.getEffectiveOvertimeHours();
        BigDecimal baseRate = overtimeRecord.getBaseRate();
        OvertimeType type = overtimeRecord.getOvertimeType();

        if (type == OvertimeType.WEEKDAY) {
            // First hour: 1.5x, subsequent hours: 2x
            if (hours.compareTo(BigDecimal.ONE) <= 0) {
                return baseRate.multiply(hours).multiply(BigDecimal.valueOf(1.5));
            } else {
                BigDecimal firstHourPay = baseRate.multiply(BigDecimal.valueOf(1.5));
                BigDecimal remainingHours = hours.subtract(BigDecimal.ONE);
                BigDecimal remainingPay = baseRate.multiply(remainingHours).multiply(BigDecimal.valueOf(2));
                return firstHourPay.add(remainingPay);
            }
        } else {
            // Weekend/Holiday: 2x for all hours
            return baseRate.multiply(hours).multiply(BigDecimal.valueOf(2));
        }
    }
}
