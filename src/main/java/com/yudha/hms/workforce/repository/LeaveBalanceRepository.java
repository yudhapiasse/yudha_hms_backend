package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndBalanceYear(UUID employeeId, UUID leaveTypeId, Integer balanceYear);

    List<LeaveBalance> findByEmployeeIdAndBalanceYear(UUID employeeId, Integer balanceYear);

    List<LeaveBalance> findByEmployeeId(UUID employeeId);

    List<LeaveBalance> findByBalanceYear(Integer balanceYear);

    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.employeeId = :employeeId AND lb.balanceYear = :year AND lb.availableDays > 0")
    List<LeaveBalance> findAvailableBalances(@Param("employeeId") UUID employeeId, @Param("year") Integer year);

    @Query("SELECT lb FROM LeaveBalance lb WHERE lb.balanceYear = :year AND lb.carryForwardExpiryDate IS NOT NULL AND lb.carryForwardExpiryDate < CURRENT_DATE")
    List<LeaveBalance> findExpiredCarryForwards(@Param("year") Integer year);
}
