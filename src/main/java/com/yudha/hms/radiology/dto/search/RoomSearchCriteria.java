package com.yudha.hms.radiology.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Room Search Criteria DTO.
 *
 * Used for searching and filtering radiology rooms.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchCriteria {

    /**
     * Search term (searches in room code, name, equipment name)
     */
    private String searchTerm;

    /**
     * Modality ID
     */
    private UUID modalityId;

    /**
     * Location
     */
    private String location;

    /**
     * Floor
     */
    private String floor;

    /**
     * Manufacturer
     */
    private String manufacturer;

    /**
     * Operational status
     */
    private Boolean isOperational;

    /**
     * Available status
     */
    private Boolean isAvailable;

    /**
     * Active status
     */
    private Boolean isActive;

    /**
     * Calibration overdue
     */
    private Boolean calibrationOverdue;

    // ========== Pagination and Sorting ==========

    /**
     * Page number (0-indexed)
     */
    @Builder.Default
    private Integer page = 0;

    /**
     * Page size
     */
    @Builder.Default
    private Integer size = 20;

    /**
     * Sort by field
     */
    @Builder.Default
    private String sortBy = "roomName";

    /**
     * Sort direction (ASC or DESC)
     */
    @Builder.Default
    private String sortDirection = "ASC";
}
