package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Order Item Response DTO.
 *
 * Represents medication, laboratory, or radiology orders linked to an encounter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private UUID id;
    private String orderNumber;
    private String orderType; // MEDICATION, LABORATORY, RADIOLOGY, PROCEDURE
    private String orderDescription;
    private LocalDateTime orderDate;
    private String orderStatus; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String orderedByName;
    private String priority; // ROUTINE, URGENT, STAT
    private LocalDateTime completedDate;
    private Boolean hasResults;
    private Boolean resultsAbnormal;
    private String resultsSummary;
}
