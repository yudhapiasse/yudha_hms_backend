package com.yudha.hms.laboratory.dto.search;

import com.yudha.hms.laboratory.constant.OrderPriority;
import com.yudha.hms.laboratory.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Order Search Criteria DTO.
 *
 * Used for searching and filtering lab orders.
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
     * Search term (searches in order number, patient name, patient MRN)
     */
    private String searchTerm;

    /**
     * Order number
     */
    private String orderNumber;

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Patient MRN
     */
    private String patientMrn;

    /**
     * Encounter ID
     */
    private UUID encounterId;

    /**
     * Ordering doctor ID
     */
    private UUID orderingDoctorId;

    /**
     * Ordering department
     */
    private String orderingDepartment;

    /**
     * Order status
     */
    private OrderStatus status;

    /**
     * Multiple statuses
     */
    private List<OrderStatus> statuses;

    /**
     * Priority
     */
    private OrderPriority priority;

    /**
     * Order date from
     */
    private LocalDateTime orderDateFrom;

    /**
     * Order date to
     */
    private LocalDateTime orderDateTo;

    /**
     * Collection scheduled from
     */
    private LocalDateTime collectionScheduledFrom;

    /**
     * Collection scheduled to
     */
    private LocalDateTime collectionScheduledTo;

    /**
     * Payment method
     */
    private String paymentMethod;

    /**
     * Insurance company ID
     */
    private UUID insuranceCompanyId;

    /**
     * Is recurring
     */
    private Boolean isRecurring;

    /**
     * Has panic values
     */
    private Boolean hasPanicValues;

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
