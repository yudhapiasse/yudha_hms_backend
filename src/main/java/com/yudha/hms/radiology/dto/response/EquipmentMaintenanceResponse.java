package com.yudha.hms.radiology.dto.response;

import com.yudha.hms.radiology.constant.MaintenanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Equipment Maintenance Response DTO.
 *
 * Response for equipment maintenance information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentMaintenanceResponse {

    /**
     * Maintenance ID
     */
    private UUID id;

    // ========== Room Information ==========

    /**
     * Room ID
     */
    private UUID roomId;

    /**
     * Room code
     */
    private String roomCode;

    /**
     * Room name
     */
    private String roomName;

    /**
     * Equipment name
     */
    private String equipmentName;

    /**
     * Modality name
     */
    private String modalityName;

    // ========== Maintenance Information ==========

    /**
     * Maintenance type
     */
    private MaintenanceType maintenanceType;

    /**
     * Scheduled date
     */
    private LocalDate scheduledDate;

    /**
     * Performed date
     */
    private LocalDate performedDate;

    /**
     * Is completed
     */
    private Boolean isCompleted;

    /**
     * Is overdue
     */
    private Boolean isOverdue;

    /**
     * Days until due (negative if overdue)
     */
    private Long daysUntilDue;

    /**
     * Performed by
     */
    private String performedBy;

    /**
     * Vendor name
     */
    private String vendorName;

    /**
     * Findings
     */
    private String findings;

    /**
     * Actions taken
     */
    private String actionsTaken;

    /**
     * Next maintenance date
     */
    private LocalDate nextMaintenanceDate;

    /**
     * Cost
     */
    private BigDecimal cost;

    /**
     * Notes
     */
    private String notes;

    // ========== Audit Fields ==========

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
