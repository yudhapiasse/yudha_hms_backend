package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.LeaveCategory;
import com.yudha.hms.workforce.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, UUID> {

    Optional<LeaveType> findByLeaveCode(String leaveCode);

    List<LeaveType> findByActiveTrue();

    List<LeaveType> findByLeaveCategoryAndActiveTrue(LeaveCategory leaveCategory);

    List<LeaveType> findByIsStatutoryTrue();

    @Query("SELECT lt FROM LeaveType lt WHERE lt.active = true ORDER BY lt.displayOrder")
    List<LeaveType> findAllActiveOrdered();

    List<LeaveType> findByIsPaidTrueAndActiveTrue();
}
