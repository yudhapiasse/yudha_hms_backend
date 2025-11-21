package com.yudha.hms.radiology.dto.search;

import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.radiology.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Order Search Criteria DTO.
 *
 * Used for searching and filtering radiology orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCriteria {

    /**
     * Search term (searches in order number, patient name, MRN)
     */
    private String searchTerm;

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Encounter ID
     */
    private UUID encounterId;

    /**
     * Ordering doctor ID
     */
    private UUID orderingDoctorId;

    /**
     * Status
     */
    private OrderStatus status;

    /**
     * Priority
     */
    private OrderPriority priority;

    /**
     * Order date from
     */
    private LocalDate orderDateFrom;

    /**
     * Order date to
     */
    private LocalDate orderDateTo;

    /**
     * Scheduled date from
     */
    private LocalDate scheduledDateFrom;

    /**
     * Scheduled date to
     */
    private LocalDate scheduledDateTo;

    /**
     * Room ID
     */
    private UUID roomId;

    /**
     * Technician ID
     */
    private UUID technicianId;

    /**
     * Modality ID
     */
    private UUID modalityId;

    /**
     * Department
     */
    private String department;

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
    private String sortBy = "orderDate";

    /**
     * Sort direction (ASC or DESC)
     */
    @Builder.Default
    private String sortDirection = "DESC";
}
