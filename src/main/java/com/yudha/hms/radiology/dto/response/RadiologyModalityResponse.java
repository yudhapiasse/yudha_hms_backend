package com.yudha.hms.radiology.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Modality Response DTO.
 *
 * Response for radiology modality information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyModalityResponse {

    /**
     * Modality ID
     */
    private UUID id;

    /**
     * Modality code
     */
    private String code;

    /**
     * Modality name
     */
    private String name;

    /**
     * Modality description
     */
    private String description;

    /**
     * Whether this modality requires radiation exposure
     */
    private Boolean requiresRadiation;

    /**
     * Average duration in minutes
     */
    private Integer averageDurationMinutes;

    /**
     * Display order
     */
    private Integer displayOrder;

    /**
     * Icon name (for UI)
     */
    private String icon;

    /**
     * Color code (for UI)
     */
    private String color;

    /**
     * Number of examinations using this modality
     */
    private Long examinationCount;

    /**
     * Number of active rooms using this modality
     */
    private Long activeRoomCount;

    /**
     * Active status
     */
    private Boolean isActive;

    /**
     * Created timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Created by user ID
     */
    private String createdBy;

    /**
     * Updated timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by user ID
     */
    private String updatedBy;
}
