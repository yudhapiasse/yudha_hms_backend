package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.SubstitutionStatus;
import com.yudha.hms.workforce.entity.ShiftSubstitution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftSubstitutionRepository extends JpaRepository<ShiftSubstitution, UUID> {

    Optional<ShiftSubstitution> findByRequestNumber(String requestNumber);

    List<ShiftSubstitution> findByOriginalEmployeeId(UUID originalEmployeeId);

    List<ShiftSubstitution> findBySubstituteEmployeeId(UUID substituteEmployeeId);

    List<ShiftSubstitution> findByStatus(SubstitutionStatus status);

    List<ShiftSubstitution> findByOriginalRosterId(UUID originalRosterId);

    @Query("SELECT ss FROM ShiftSubstitution ss WHERE ss.originalEmployeeId = :employeeId AND ss.rosterDate BETWEEN :startDate AND :endDate")
    List<ShiftSubstitution> findByOriginalEmployeeAndDateRange(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ss FROM ShiftSubstitution ss WHERE ss.substituteEmployeeId = :employeeId AND ss.rosterDate BETWEEN :startDate AND :endDate")
    List<ShiftSubstitution> findBySubstituteAndDateRange(@Param("employeeId") UUID employeeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ss FROM ShiftSubstitution ss WHERE (ss.originalEmployeeId = :employeeId OR ss.substituteEmployeeId = :employeeId) AND ss.status = 'PENDING'")
    List<ShiftSubstitution> findPendingSubstitutions(@Param("employeeId") UUID employeeId);

    @Query("SELECT ss FROM ShiftSubstitution ss WHERE ss.rosterDate = :date AND ss.status = :status")
    List<ShiftSubstitution> findByDateAndStatus(@Param("date") LocalDate date, @Param("status") SubstitutionStatus status);
}
