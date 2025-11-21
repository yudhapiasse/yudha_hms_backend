package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.constant.MaintenanceType;
import com.yudha.hms.radiology.entity.EquipmentMaintenance;
import com.yudha.hms.radiology.entity.RadiologyRoom;
import com.yudha.hms.radiology.repository.EquipmentMaintenanceRepository;
import com.yudha.hms.radiology.repository.RadiologyRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for Equipment Maintenance operations.
 *
 * Handles CRUD operations and business logic for radiology equipment maintenance.
 *
 * Features:
 * - Schedule maintenance (preventive, corrective, calibration)
 * - Record maintenance completion
 * - Track maintenance costs
 * - Get upcoming maintenance schedules
 * - Get overdue maintenance
 * - Get maintenance history for room/equipment
 * - Update next maintenance date
 * - Alert for due calibrations
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EquipmentMaintenanceService {

    private final EquipmentMaintenanceRepository maintenanceRepository;
    private final RadiologyRoomRepository roomRepository;

    /**
     * Schedule a new maintenance.
     *
     * @param maintenance Maintenance to schedule
     * @return Created maintenance record
     * @throws IllegalArgumentException if room not found
     */
    public EquipmentMaintenance scheduleMaintenance(EquipmentMaintenance maintenance) {
        log.info("Scheduling {} maintenance for room: {} on {}",
                maintenance.getMaintenanceType(), maintenance.getRoom().getId(), maintenance.getScheduledDate());

        // Validate room exists
        RadiologyRoom room = roomRepository.findByIdAndDeletedAtIsNull(maintenance.getRoom().getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + maintenance.getRoom().getId()));

        // Validate scheduled date is not in the past
        if (maintenance.getScheduledDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot schedule maintenance in the past: " + maintenance.getScheduledDate());
        }

        EquipmentMaintenance saved = maintenanceRepository.save(maintenance);
        log.info("Maintenance scheduled successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Schedule maintenance with details.
     *
     * @param roomId Room ID
     * @param maintenanceType Type of maintenance
     * @param scheduledDate Scheduled date
     * @param vendorName Vendor name (optional)
     * @return Created maintenance record
     */
    public EquipmentMaintenance scheduleMaintenance(UUID roomId, MaintenanceType maintenanceType,
                                                     LocalDate scheduledDate, String vendorName) {
        log.info("Scheduling {} maintenance for room: {} on {}", maintenanceType, roomId, scheduledDate);

        RadiologyRoom room = roomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        EquipmentMaintenance maintenance = EquipmentMaintenance.builder()
                .room(room)
                .maintenanceType(maintenanceType)
                .scheduledDate(scheduledDate)
                .vendorName(vendorName)
                .build();

        EquipmentMaintenance saved = maintenanceRepository.save(maintenance);
        log.info("Maintenance scheduled successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Record maintenance completion.
     *
     * @param maintenanceId Maintenance ID
     * @param performedDate Date performed
     * @param performedBy Person who performed maintenance
     * @param findings Findings during maintenance
     * @param actionsTaken Actions taken
     * @param cost Cost of maintenance
     * @param nextMaintenanceDate Next scheduled maintenance date
     * @return Updated maintenance record
     * @throws IllegalArgumentException if maintenance not found
     */
    public EquipmentMaintenance recordCompletion(UUID maintenanceId, LocalDate performedDate, String performedBy,
                                                  String findings, String actionsTaken, BigDecimal cost,
                                                  LocalDate nextMaintenanceDate) {
        log.info("Recording completion for maintenance: {}", maintenanceId);

        EquipmentMaintenance maintenance = getMaintenanceById(maintenanceId);

        // Check if already completed
        if (maintenance.getPerformedDate() != null) {
            throw new IllegalStateException("Maintenance is already completed: " + maintenanceId);
        }

        maintenance.setPerformedDate(performedDate);
        maintenance.setPerformedBy(performedBy);
        maintenance.setFindings(findings);
        maintenance.setActionsTaken(actionsTaken);
        maintenance.setCost(cost);
        maintenance.setNextMaintenanceDate(nextMaintenanceDate);

        // Update room's calibration date if this was a calibration
        if (maintenance.getMaintenanceType() == MaintenanceType.CALIBRATION) {
            RadiologyRoom room = maintenance.getRoom();
            room.setLastCalibrationDate(performedDate);
            room.setNextCalibrationDate(nextMaintenanceDate);
            roomRepository.save(room);
            log.info("Updated room calibration dates for room: {}", room.getId());
        }

        EquipmentMaintenance updated = maintenanceRepository.save(maintenance);
        log.info("Maintenance completion recorded successfully");
        return updated;
    }

    /**
     * Update maintenance details.
     *
     * @param maintenanceId Maintenance ID
     * @param maintenanceUpdate Updated maintenance data
     * @return Updated maintenance record
     * @throws IllegalArgumentException if maintenance not found
     * @throws IllegalStateException if maintenance is already completed
     */
    public EquipmentMaintenance updateMaintenance(UUID maintenanceId, EquipmentMaintenance maintenanceUpdate) {
        log.info("Updating maintenance: {}", maintenanceId);

        EquipmentMaintenance existing = getMaintenanceById(maintenanceId);

        // Check if maintenance can be updated
        if (existing.getPerformedDate() != null) {
            throw new IllegalStateException("Cannot update completed maintenance: " + maintenanceId);
        }

        existing.setMaintenanceType(maintenanceUpdate.getMaintenanceType());
        existing.setScheduledDate(maintenanceUpdate.getScheduledDate());
        existing.setVendorName(maintenanceUpdate.getVendorName());

        EquipmentMaintenance updated = maintenanceRepository.save(existing);
        log.info("Maintenance updated successfully");
        return updated;
    }

    /**
     * Cancel scheduled maintenance.
     *
     * @param maintenanceId Maintenance ID
     * @throws IllegalArgumentException if maintenance not found
     * @throws IllegalStateException if maintenance is already completed
     */
    public void cancelMaintenance(UUID maintenanceId) {
        log.info("Cancelling maintenance: {}", maintenanceId);

        EquipmentMaintenance maintenance = getMaintenanceById(maintenanceId);

        if (maintenance.getPerformedDate() != null) {
            throw new IllegalStateException("Cannot cancel completed maintenance: " + maintenanceId);
        }

        maintenanceRepository.delete(maintenance);
        log.info("Maintenance cancelled successfully");
    }

    /**
     * Get maintenance by ID.
     *
     * @param id Maintenance ID
     * @return Maintenance record
     * @throws IllegalArgumentException if maintenance not found
     */
    @Transactional(readOnly = true)
    public EquipmentMaintenance getMaintenanceById(UUID id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance not found with ID: " + id));
    }

    /**
     * Get maintenance history for a room.
     *
     * @param roomId Room ID
     * @return List of completed maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getMaintenanceHistory(UUID roomId) {
        return maintenanceRepository.findMaintenanceHistory(roomId);
    }

    /**
     * Get all maintenance for a room (scheduled and completed).
     *
     * @param roomId Room ID
     * @return List of maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getAllMaintenanceForRoom(UUID roomId) {
        return maintenanceRepository.findByRoomIdOrderByScheduledDateDesc(roomId);
    }

    /**
     * Get calibration history for a room.
     *
     * @param roomId Room ID
     * @return List of calibration records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getCalibrationHistory(UUID roomId) {
        return maintenanceRepository.findCalibrationHistory(roomId);
    }

    /**
     * Get maintenance by type.
     *
     * @param maintenanceType Maintenance type
     * @return List of maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getMaintenanceByType(MaintenanceType maintenanceType) {
        return maintenanceRepository.findByMaintenanceTypeOrderByScheduledDateDesc(maintenanceType);
    }

    /**
     * Get maintenance scheduled for a specific date.
     *
     * @param date Target date
     * @return List of maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getMaintenanceByDate(LocalDate date) {
        return maintenanceRepository.findByScheduledDateOrderByRoomId(date);
    }

    /**
     * Get pending maintenance (scheduled but not yet performed).
     *
     * @return List of pending maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getPendingMaintenance() {
        return maintenanceRepository.findPendingMaintenance(LocalDate.now());
    }

    /**
     * Get upcoming maintenance within date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of upcoming maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getUpcomingMaintenance(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findUpcomingMaintenance(startDate, endDate);
    }

    /**
     * Get upcoming maintenance for next N days.
     *
     * @param days Number of days ahead
     * @return List of upcoming maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getUpcomingMaintenanceForDays(int days) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days);
        return getUpcomingMaintenance(startDate, endDate);
    }

    /**
     * Get overdue maintenance.
     *
     * @return List of overdue maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getOverdueMaintenance() {
        return maintenanceRepository.findOverdueMaintenance(LocalDate.now());
    }

    /**
     * Get completed maintenance by date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of completed maintenance records
     */
    @Transactional(readOnly = true)
    public Page<EquipmentMaintenance> getCompletedMaintenanceByDateRange(LocalDate startDate, LocalDate endDate,
                                                                           Pageable pageable) {
        return maintenanceRepository.findCompletedMaintenanceByDateRange(startDate, endDate, pageable);
    }

    /**
     * Get maintenance by vendor.
     *
     * @param vendorName Vendor name
     * @return List of maintenance records
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getMaintenanceByVendor(String vendorName) {
        return maintenanceRepository.findByVendorNameOrderByPerformedDateDesc(vendorName);
    }

    /**
     * Update next maintenance date.
     *
     * @param maintenanceId Maintenance ID
     * @param nextMaintenanceDate Next maintenance date
     * @return Updated maintenance record
     * @throws IllegalArgumentException if maintenance not found
     */
    public EquipmentMaintenance updateNextMaintenanceDate(UUID maintenanceId, LocalDate nextMaintenanceDate) {
        log.info("Updating next maintenance date for maintenance: {} to {}", maintenanceId, nextMaintenanceDate);

        EquipmentMaintenance maintenance = getMaintenanceById(maintenanceId);
        maintenance.setNextMaintenanceDate(nextMaintenanceDate);

        EquipmentMaintenance updated = maintenanceRepository.save(maintenance);
        log.info("Next maintenance date updated successfully");
        return updated;
    }

    /**
     * Count pending maintenance.
     *
     * @return Count of pending maintenance
     */
    @Transactional(readOnly = true)
    public long countPendingMaintenance() {
        return maintenanceRepository.countPendingMaintenance(LocalDate.now());
    }

    /**
     * Count maintenance by type.
     *
     * @param maintenanceType Maintenance type
     * @return Count of maintenance records
     */
    @Transactional(readOnly = true)
    public long countMaintenanceByType(MaintenanceType maintenanceType) {
        return maintenanceRepository.countByMaintenanceType(maintenanceType);
    }

    /**
     * Count completed maintenance for a room.
     *
     * @param roomId Room ID
     * @return Count of completed maintenance
     */
    @Transactional(readOnly = true)
    public long countCompletedMaintenanceByRoom(UUID roomId) {
        return maintenanceRepository.countCompletedMaintenanceByRoom(roomId);
    }

    /**
     * Calculate total maintenance cost for a room.
     *
     * @param roomId Room ID
     * @return Total cost
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalMaintenanceCost(UUID roomId) {
        List<EquipmentMaintenance> history = getMaintenanceHistory(roomId);
        return history.stream()
                .map(EquipmentMaintenance::getCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate maintenance cost for date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Total cost
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateMaintenanceCostByDateRange(LocalDate startDate, LocalDate endDate) {
        Page<EquipmentMaintenance> maintenances = getCompletedMaintenanceByDateRange(
                startDate, endDate, Pageable.unpaged());

        return maintenances.stream()
                .map(EquipmentMaintenance::getCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get alert for due calibrations.
     * Returns list of rooms needing calibration within the next N days.
     *
     * @param daysAhead Number of days to look ahead
     * @return List of maintenance records for due calibrations
     */
    @Transactional(readOnly = true)
    public List<EquipmentMaintenance> getCalibrationAlerts(int daysAhead) {
        LocalDate endDate = LocalDate.now().plusDays(daysAhead);
        return maintenanceRepository.findUpcomingMaintenance(LocalDate.now(), endDate).stream()
                .filter(m -> m.getMaintenanceType() == MaintenanceType.CALIBRATION)
                .filter(m -> m.getPerformedDate() == null)
                .toList();
    }

    /**
     * Check if room needs maintenance soon.
     *
     * @param roomId Room ID
     * @param daysAhead Number of days to check ahead
     * @return True if room has maintenance scheduled within the period
     */
    @Transactional(readOnly = true)
    public boolean roomNeedsMaintenanceSoon(UUID roomId, int daysAhead) {
        LocalDate endDate = LocalDate.now().plusDays(daysAhead);
        List<EquipmentMaintenance> upcoming = getUpcomingMaintenance(LocalDate.now(), endDate);

        return upcoming.stream()
                .anyMatch(m -> m.getRoom().getId().equals(roomId) && m.getPerformedDate() == null);
    }
}
