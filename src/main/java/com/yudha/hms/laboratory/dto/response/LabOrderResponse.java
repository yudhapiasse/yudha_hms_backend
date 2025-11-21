package com.yudha.hms.laboratory.dto.response;

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
 * Lab Order Response DTO.
 *
 * Response for lab order information with full details.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabOrderResponse {

    /**
     * Order ID
     */
    private UUID id;

    /**
     * Order number
     */
    private String orderNumber;

    // ========== Patient and Encounter Information ==========

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Patient name
     */
    private String patientName;

    /**
     * Patient medical record number
     */
    private String patientMrn;

    /**
     * Patient age
     */
    private Integer patientAge;

    /**
     * Patient gender
     */
    private String patientGender;

    /**
     * Encounter ID
     */
    private UUID encounterId;

    /**
     * Encounter type
     */
    private String encounterType;

    // ========== Ordering Information ==========

    /**
     * Ordering doctor ID
     */
    private UUID orderingDoctorId;

    /**
     * Ordering doctor name
     */
    private String orderingDoctorName;

    /**
     * Ordering department
     */
    private String orderingDepartment;

    /**
     * Ordering location
     */
    private String orderingLocation;

    /**
     * Order date
     */
    private LocalDateTime orderDate;

    // ========== Priority ==========

    /**
     * Priority level
     */
    private OrderPriority priority;

    /**
     * Urgency reason
     */
    private String urgencyReason;

    // ========== Status ==========

    /**
     * Order status
     */
    private OrderStatus status;

    /**
     * Status reason
     */
    private String statusReason;

    // ========== Clinical Information ==========

    /**
     * Clinical indication
     */
    private String clinicalIndication;

    /**
     * Diagnosis code
     */
    private String diagnosisCode;

    /**
     * Diagnosis text
     */
    private String diagnosisText;

    // ========== Order Items ==========

    /**
     * Order items (tests/panels)
     */
    private List<LabOrderItemResponse> items;

    /**
     * Total items count
     */
    private Integer totalItems;

    // ========== Sample Collection ==========

    /**
     * Collection scheduled at
     */
    private LocalDateTime collectionScheduledAt;

    /**
     * Collection location
     */
    private String collectionLocation;

    // ========== Billing ==========

    /**
     * Payment method
     */
    private String paymentMethod;

    /**
     * Insurance company ID
     */
    private UUID insuranceCompanyId;

    /**
     * Insurance company name
     */
    private String insuranceCompanyName;

    /**
     * Coverage type
     */
    private String coverageType;

    // ========== Recurring ==========

    /**
     * Is recurring order
     */
    private Boolean isRecurring;

    /**
     * Recurrence pattern
     */
    private String recurrencePattern;

    /**
     * Recurrence end date
     */
    private LocalDate recurrenceEndDate;

    /**
     * Parent order ID
     */
    private UUID parentOrderId;

    // ========== Cancellation ==========

    /**
     * Cancelled at
     */
    private LocalDateTime cancelledAt;

    /**
     * Cancelled by
     */
    private String cancelledBy;

    /**
     * Cancellation reason
     */
    private String cancellationReason;

    // ========== Completion ==========

    /**
     * Completed at
     */
    private LocalDateTime completedAt;

    /**
     * Result verified at
     */
    private LocalDateTime resultVerifiedAt;

    /**
     * Result verified by
     */
    private UUID resultVerifiedBy;

    /**
     * Result verified by name
     */
    private String resultVerifiedByName;

    // ========== Integration ==========

    /**
     * External order ID
     */
    private String externalOrderId;

    /**
     * External system
     */
    private String externalSystem;

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
