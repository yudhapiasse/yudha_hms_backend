package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.entity.LeaveBalance;
import com.yudha.hms.workforce.repository.LeaveBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola saldo cuti (Leave Balance)
 * Manages leave balance tracking and accrual
 */
@Service
@RequiredArgsConstructor
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Transactional(readOnly = true)
    public LeaveBalance getLeaveBalanceById(UUID id) {
        return leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Saldo cuti tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public LeaveBalance getEmployeeLeaveBalance(UUID employeeId, UUID leaveTypeId, Integer year) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndBalanceYear(employeeId, leaveTypeId, year)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<LeaveBalance> getEmployeeLeaveBalancesForYear(UUID employeeId, Integer year) {
        return leaveBalanceRepository.findByEmployeeIdAndBalanceYear(employeeId, year);
    }

    @Transactional(readOnly = true)
    public List<LeaveBalance> getAvailableBalances(UUID employeeId, Integer year) {
        return leaveBalanceRepository.findAvailableBalances(employeeId, year);
    }

    @Transactional
    public LeaveBalance createLeaveBalance(LeaveBalance leaveBalance) {
        leaveBalance.setLastCalculatedAt(LocalDateTime.now());
        return leaveBalanceRepository.save(leaveBalance);
    }

    @Transactional
    public LeaveBalance updateLeaveBalance(UUID id, LeaveBalance leaveBalance) {
        LeaveBalance existing = getLeaveBalanceById(id);
        existing.setAccruedDays(leaveBalance.getAccruedDays());
        existing.setAdjustmentDays(leaveBalance.getAdjustmentDays());
        existing.setTakenDays(leaveBalance.getTakenDays());
        existing.setPendingDays(leaveBalance.getPendingDays());
        existing.setAvailableDays(calculateAvailableDays(existing));
        existing.setLastCalculatedAt(LocalDateTime.now());
        existing.setNotes(leaveBalance.getNotes());
        return leaveBalanceRepository.save(existing);
    }

    @Transactional
    public LeaveBalance deductLeave(UUID employeeId, UUID leaveTypeId, Integer year, BigDecimal days) {
        LeaveBalance balance = getEmployeeLeaveBalance(employeeId, leaveTypeId, year);

        if (balance == null) {
            throw new RuntimeException("Saldo cuti tidak ditemukan");
        }

        if (balance.getAvailableDays().compareTo(days) < 0) {
            throw new RuntimeException("Saldo cuti tidak mencukupi");
        }

        balance.setTakenDays(balance.getTakenDays().add(days));
        balance.setAvailableDays(calculateAvailableDays(balance));
        balance.setLastCalculatedAt(LocalDateTime.now());

        return leaveBalanceRepository.save(balance);
    }

    @Transactional
    public LeaveBalance restoreLeave(UUID employeeId, UUID leaveTypeId, Integer year, BigDecimal days) {
        LeaveBalance balance = getEmployeeLeaveBalance(employeeId, leaveTypeId, year);

        if (balance == null) {
            throw new RuntimeException("Saldo cuti tidak ditemukan");
        }

        balance.setTakenDays(balance.getTakenDays().subtract(days));
        balance.setAvailableDays(calculateAvailableDays(balance));
        balance.setLastCalculatedAt(LocalDateTime.now());

        return leaveBalanceRepository.save(balance);
    }

    private BigDecimal calculateAvailableDays(LeaveBalance balance) {
        return balance.getOpeningBalance()
                .add(balance.getAccruedDays())
                .add(balance.getAdjustmentDays())
                .subtract(balance.getTakenDays())
                .subtract(balance.getPendingDays());
    }
}
