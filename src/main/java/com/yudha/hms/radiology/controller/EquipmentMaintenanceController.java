package com.yudha.hms.radiology.controller;

import com.yudha.hms.radiology.dto.request.EquipmentMaintenanceRequest;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.EquipmentMaintenanceResponse;
import com.yudha.hms.radiology.entity.EquipmentMaintenance;
import com.yudha.hms.radiology.entity.RadiologyRoom;
import com.yudha.hms.radiology.service.EquipmentMaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Equipment Maintenance Controller.
 *
 * REST controller for managing radiology equipment maintenance.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/maintenance")
@RequiredArgsConstructor
@Slf4j
public class EquipmentMaintenanceController {

    private final EquipmentMaintenanceService maintenanceService;

    /**
     * Schedule maintenance
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EquipmentMaintenanceResponse>> scheduleMaintenance(
            @Valid @RequestBody EquipmentMaintenanceRequest request) {
        log.info("Scheduling maintenance for room ID: {}", request.getRoomId());

        EquipmentMaintenance maintenance = convertToEntity(request);
        EquipmentMaintenance saved = maintenanceService.scheduleMaintenance(maintenance);
        EquipmentMaintenanceResponse response = toResponse(saved);

        log.info("Maintenance scheduled successfully");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Maintenance scheduled successfully", response));
    }

    /**
     * Record maintenance completion
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<EquipmentMaintenanceResponse>> completeMaintenance(
            @PathVariable UUID id,
            @RequestParam String performedBy,
            @RequestParam(required = false) String findings,
            @RequestParam(required = false) String actionsTaken,
            @RequestParam(required = false) BigDecimal cost,
            @RequestParam(required = false) LocalDate nextMaintenanceDate) {
        log.info("Recording completion for maintenance ID: {}", id);

        EquipmentMaintenance maintenance = maintenanceService.recordCompletion(
                id, LocalDate.now(), performedBy, findings, actionsTaken, cost, nextMaintenanceDate);
        EquipmentMaintenanceResponse response = toResponse(maintenance);

        log.info("Maintenance completion recorded successfully");

        return ResponseEntity.ok(ApiResponse.success("Maintenance completed successfully", response));
    }

    /**
     * Get maintenance by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EquipmentMaintenanceResponse>> getMaintenanceById(
            @PathVariable UUID id) {
        log.info("Fetching maintenance ID: {}", id);

        EquipmentMaintenance maintenance = maintenanceService.getMaintenanceById(id);
        EquipmentMaintenanceResponse response = toResponse(maintenance);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get maintenance by room
     */
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<EquipmentMaintenanceResponse>>> getMaintenanceByRoom(
            @PathVariable UUID roomId) {
        log.info("Fetching maintenance for room ID: {}", roomId);

        List<EquipmentMaintenance> maintenanceList = maintenanceService.getAllMaintenanceForRoom(roomId);
        List<EquipmentMaintenanceResponse> responses = maintenanceList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get pending maintenance
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<EquipmentMaintenanceResponse>>> getPendingMaintenance() {
        log.info("Fetching pending maintenance");

        List<EquipmentMaintenance> maintenanceList = maintenanceService.getPendingMaintenance();
        List<EquipmentMaintenanceResponse> responses = maintenanceList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get overdue maintenance
     */
    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<EquipmentMaintenanceResponse>>> getOverdueMaintenance() {
        log.info("Fetching overdue maintenance");

        List<EquipmentMaintenance> maintenanceList = maintenanceService.getOverdueMaintenance();
        List<EquipmentMaintenanceResponse> responses = maintenanceList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get upcoming maintenance
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EquipmentMaintenanceResponse>>> getUpcomingMaintenance(
            @RequestParam(defaultValue = "30") int days) {
        log.info("Fetching upcoming maintenance for next {} days", days);

        List<EquipmentMaintenance> maintenanceList = maintenanceService.getUpcomingMaintenanceForDays(days);
        List<EquipmentMaintenanceResponse> responses = maintenanceList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get calibration alerts
     */
    @GetMapping("/calibration-alerts")
    public ResponseEntity<ApiResponse<List<EquipmentMaintenanceResponse>>> getCalibrationAlerts(
            @RequestParam(defaultValue = "30") int daysAhead) {
        log.info("Fetching calibration alerts for next {} days", daysAhead);

        List<EquipmentMaintenance> maintenanceList = maintenanceService.getCalibrationAlerts(daysAhead);
        List<EquipmentMaintenanceResponse> responses = maintenanceList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Convert entity to response DTO
     */
    private EquipmentMaintenanceResponse toResponse(EquipmentMaintenance maintenance) {
        EquipmentMaintenanceResponse response = new EquipmentMaintenanceResponse();
        response.setId(maintenance.getId());

        // Room information
        if (maintenance.getRoom() != null) {
            response.setRoomId(maintenance.getRoom().getId());
            response.setRoomCode(maintenance.getRoom().getRoomCode());
            response.setRoomName(maintenance.getRoom().getRoomName());
            response.setEquipmentName(maintenance.getRoom().getEquipmentName());
        }

        // Maintenance details
        response.setMaintenanceType(maintenance.getMaintenanceType());
        response.setScheduledDate(maintenance.getScheduledDate());
        response.setPerformedDate(maintenance.getPerformedDate());
        response.setPerformedBy(maintenance.getPerformedBy());
        response.setVendorName(maintenance.getVendorName());
        response.setFindings(maintenance.getFindings());
        response.setActionsTaken(maintenance.getActionsTaken());
        response.setNextMaintenanceDate(maintenance.getNextMaintenanceDate());
        response.setCost(maintenance.getCost());

        // Calculate status
        boolean isCompleted = maintenance.getPerformedDate() != null;
        boolean isOverdue = !isCompleted && maintenance.getScheduledDate().isBefore(LocalDate.now());
        response.setIsCompleted(isCompleted);
        response.setIsOverdue(isOverdue);

        // Calculate days until due
        if (!isCompleted) {
            long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), maintenance.getScheduledDate());
            response.setDaysUntilDue(daysUntil);
        }

        // Audit fields
        response.setCreatedAt(maintenance.getCreatedAt());
        response.setUpdatedAt(maintenance.getUpdatedAt());

        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private EquipmentMaintenance convertToEntity(EquipmentMaintenanceRequest request) {
        EquipmentMaintenance maintenance = new EquipmentMaintenance();

        // Set room - create minimal object with just ID
        if (request.getRoomId() != null) {
            RadiologyRoom room = new RadiologyRoom();
            room.setId(request.getRoomId());
            maintenance.setRoom(room);
        }

        maintenance.setMaintenanceType(request.getMaintenanceType());
        maintenance.setScheduledDate(request.getScheduledDate());
        maintenance.setVendorName(request.getVendorName());
        maintenance.setCost(request.getCost());

        return maintenance;
    }
}
