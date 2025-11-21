package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.MaintenanceType;
import com.yudha.hms.radiology.entity.EquipmentMaintenance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repository for EquipmentMaintenance entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface EquipmentMaintenanceRepository extends JpaRepository<EquipmentMaintenance, UUID> {

    /**
     * Find by room
     */
    List<EquipmentMaintenance> findByRoomIdOrderByScheduledDateDesc(UUID roomId);

    /**
     * Find by maintenance type
     */
    List<EquipmentMaintenance> findByMaintenanceTypeOrderByScheduledDateDesc(MaintenanceType maintenanceType);

    /**
     * Find by scheduled date
     */
    List<EquipmentMaintenance> findByScheduledDateOrderByRoomId(LocalDate scheduledDate);

    /**
     * Find pending maintenance (scheduled but not performed)
     */
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.performedDate IS NULL AND em.scheduledDate <= :date ORDER BY em.scheduledDate ASC")
    List<EquipmentMaintenance> findPendingMaintenance(@Param("date") LocalDate date);

    /**
     * Find overdue maintenance
     */
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.performedDate IS NULL AND em.scheduledDate < :date ORDER BY em.scheduledDate ASC")
    List<EquipmentMaintenance> findOverdueMaintenance(@Param("date") LocalDate date);

    /**
     * Find upcoming maintenance
     */
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.performedDate IS NULL AND em.scheduledDate BETWEEN :startDate AND :endDate ORDER BY em.scheduledDate ASC")
    List<EquipmentMaintenance> findUpcomingMaintenance(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find completed maintenance by date range
     */
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.performedDate BETWEEN :startDate AND :endDate ORDER BY em.performedDate DESC")
    Page<EquipmentMaintenance> findCompletedMaintenanceByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    /**
     * Find by vendor
     */
    List<EquipmentMaintenance> findByVendorNameOrderByPerformedDateDesc(String vendorName);

    /**
     * Find maintenance history for room
     */
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.room.id = :roomId AND em.performedDate IS NOT NULL ORDER BY em.performedDate DESC")
    List<EquipmentMaintenance> findMaintenanceHistory(@Param("roomId") UUID roomId);

    /**
     * Find calibration history for room
     */
    @Query("SELECT em FROM EquipmentMaintenance em WHERE em.room.id = :roomId AND em.maintenanceType = 'CALIBRATION' AND em.performedDate IS NOT NULL ORDER BY em.performedDate DESC")
    List<EquipmentMaintenance> findCalibrationHistory(@Param("roomId") UUID roomId);

    /**
     * Count pending maintenance
     */
    @Query("SELECT COUNT(em) FROM EquipmentMaintenance em WHERE em.performedDate IS NULL AND em.scheduledDate <= :date")
    long countPendingMaintenance(@Param("date") LocalDate date);

    /**
     * Count by maintenance type
     */
    long countByMaintenanceType(MaintenanceType maintenanceType);

    /**
     * Count completed maintenance for room
     */
    @Query("SELECT COUNT(em) FROM EquipmentMaintenance em WHERE em.room.id = :roomId AND em.performedDate IS NOT NULL")
    long countCompletedMaintenanceByRoom(@Param("roomId") UUID roomId);
}
