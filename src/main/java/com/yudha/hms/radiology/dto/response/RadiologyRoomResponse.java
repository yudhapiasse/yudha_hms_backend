package com.yudha.hms.radiology.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Room Response DTO.
 *
 * Response for radiology room information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyRoomResponse {

    /**
     * Room ID
     */
    private UUID id;

    /**
     * Room code
     */
    private String roomCode;

    /**
     * Room name
     */
    private String roomName;

    /**
     * Location
     */
    private String location;

    /**
     * Floor
     */
    private String floor;

    /**
     * Modality ID
     */
    private UUID modalityId;

    /**
     * Modality code
     */
    private String modalityCode;

    /**
     * Modality name
     */
    private String modalityName;

    // ========== Equipment Information ==========

    /**
     * Equipment name
     */
    private String equipmentName;

    /**
     * Equipment model
     */
    private String equipmentModel;

    /**
     * Manufacturer
     */
    private String manufacturer;

    /**
     * Installation date
     */
    private LocalDate installationDate;

    // ========== Calibration ==========

    /**
     * Last calibration date
     */
    private LocalDate lastCalibrationDate;

    /**
     * Next calibration date
     */
    private LocalDate nextCalibrationDate;

    /**
     * Days until next calibration
     */
    private Long daysUntilCalibration;

    /**
     * Is calibration overdue
     */
    private Boolean calibrationOverdue;

    // ========== Status ==========

    /**
     * Operational status
     */
    private Boolean isOperational;

    /**
     * Available status
     */
    private Boolean isAvailable;

    // ========== Capacity ==========

    /**
     * Maximum bookings per day
     */
    private Integer maxBookingsPerDay;

    /**
     * Current bookings today
     */
    private Integer currentBookingsToday;

    /**
     * Available slots today
     */
    private Integer availableSlotsToday;

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
