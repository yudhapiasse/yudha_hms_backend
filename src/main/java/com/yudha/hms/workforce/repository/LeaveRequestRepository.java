package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.LeaveRequestStatus;
import com.yudha.hms.workforce.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    Optional<LeaveRequest> findByRequestNumber(String requestNumber);

    List<LeaveRequest> findByEmployeeId(UUID employeeId);

    List<LeaveRequest> findByEmployeeIdAndStatus(UUID employeeId, LeaveRequestStatus status);

    List<LeaveRequest> findByStatus(LeaveRequestStatus status);

    List<LeaveRequest> findByLeaveTypeId(UUID leaveTypeId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employeeId = :employeeId AND lr.startDate BETWEEN :startDate AND :endDate")
    List<LeaveRequest> findByEmployeeAndDateRange(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employeeId = :employeeId AND :date BETWEEN lr.startDate AND lr.endDate AND lr.status = :status")
    List<LeaveRequest> findOverlappingLeaves(@Param("employeeId") UUID employeeId, @Param("date") LocalDate date, @Param("status") LeaveRequestStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.immediateSupervisorId = :supervisorId AND lr.status = 'PENDING'")
    List<LeaveRequest> findPendingForSupervisor(@Param("supervisorId") UUID supervisorId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.status = 'APPROVED' AND lr.startDate <= :date AND lr.endDate >= :date")
    List<LeaveRequest> findApprovedLeavesOnDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(lr) FROM LeaveRequest lr WHERE lr.employeeId = :employeeId AND lr.leaveTypeId = :leaveTypeId AND YEAR(lr.startDate) = :year AND lr.status = 'APPROVED'")
    Long countApprovedLeavesByTypeAndYear(@Param("employeeId") UUID employeeId, @Param("leaveTypeId") UUID leaveTypeId, @Param("year") Integer year);
}
