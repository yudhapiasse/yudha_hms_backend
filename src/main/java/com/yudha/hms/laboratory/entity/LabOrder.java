package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.OrderPriority;
import com.yudha.hms.laboratory.constant.OrderStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Order Entity.
 *
 * Laboratory test orders from clinical modules.
 * Supports electronic lab orders with priority levels (routine, urgent, cito),
 * recurring orders, and integration with clinical and billing systems.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_order", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_order_number", columnList = "order_number", unique = true),
        @Index(name = "idx_lab_order_patient", columnList = "patient_id"),
        @Index(name = "idx_lab_order_encounter", columnList = "encounter_id"),
        @Index(name = "idx_lab_order_doctor", columnList = "ordering_doctor_id"),
        @Index(name = "idx_lab_order_status", columnList = "status"),
        @Index(name = "idx_lab_order_priority", columnList = "priority"),
        @Index(name = "idx_lab_order_date", columnList = "order_date"),
        @Index(name = "idx_lab_order_parent", columnList = "parent_order_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabOrder extends SoftDeletableEntity {

    /**
     * Order number (unique identifier)
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    // ========== Patient and Encounter Information ==========

    /**
     * Patient ID
     */
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    /**
     * Encounter ID (visit/admission)
     */
    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;

    // ========== Ordering Information ==========

    /**
     * Ordering doctor ID
     */
    @Column(name = "ordering_doctor_id", nullable = false)
    private UUID orderingDoctorId;

    /**
     * Ordering department
     */
    @Column(name = "ordering_department", length = 100)
    private String orderingDepartment;

    /**
     * Ordering location
     */
    @Column(name = "ordering_location", length = 100)
    private String orderingLocation;

    /**
     * Order date and time
     */
    @Column(name = "order_date", nullable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    // ========== Priority ==========

    /**
     * Priority level (ROUTINE, URGENT, CITO)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    @Builder.Default
    private OrderPriority priority = OrderPriority.ROUTINE;

    /**
     * Urgency reason (required for URGENT/CITO orders)
     */
    @Column(name = "urgency_reason", columnDefinition = "TEXT")
    private String urgencyReason;

    // ========== Status ==========

    /**
     * Order status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Status reason (e.g., reason for cancellation)
     */
    @Column(name = "status_reason", columnDefinition = "TEXT")
    private String statusReason;

    // ========== Clinical Information ==========

    /**
     * Clinical indication for tests
     */
    @Column(name = "clinical_indication", columnDefinition = "TEXT")
    private String clinicalIndication;

    /**
     * Diagnosis code (ICD-10)
     */
    @Column(name = "diagnosis_code", length = 20)
    private String diagnosisCode;

    /**
     * Diagnosis text
     */
    @Column(name = "diagnosis_text", length = 500)
    private String diagnosisText;

    // ========== Sample Collection ==========

    /**
     * Collection scheduled date and time
     */
    @Column(name = "collection_scheduled_at")
    private LocalDateTime collectionScheduledAt;

    /**
     * Collection location
     */
    @Column(name = "collection_location", length = 200)
    private String collectionLocation;

    // ========== Order Type (Recurring Support) ==========

    /**
     * Is recurring order
     */
    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    /**
     * Recurrence pattern (e.g., "DAILY", "WEEKLY")
     */
    @Column(name = "recurrence_pattern", length = 100)
    private String recurrencePattern;

    /**
     * Recurrence end date
     */
    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    /**
     * Parent order ID (for recurring orders)
     */
    @Column(name = "parent_order_id")
    private UUID parentOrderId;

    // ========== Billing ==========

    /**
     * Payment method (CASH, INSURANCE, BPJS, etc.)
     */
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    /**
     * Insurance company ID
     */
    @Column(name = "insurance_company_id")
    private UUID insuranceCompanyId;

    /**
     * Coverage type
     */
    @Column(name = "coverage_type", length = 50)
    private String coverageType;

    // ========== Cancellation ==========

    /**
     * Cancelled timestamp
     */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /**
     * Cancelled by user ID
     */
    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    /**
     * Cancellation reason
     */
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // ========== Completion ==========

    /**
     * Completed timestamp (all results finalized)
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Result verified timestamp
     */
    @Column(name = "result_verified_at")
    private LocalDateTime resultVerifiedAt;

    /**
     * Result verified by user ID
     */
    @Column(name = "result_verified_by")
    private UUID resultVerifiedBy;

    // ========== Integration ==========

    /**
     * External order ID (from external system)
     */
    @Column(name = "external_order_id", length = 100)
    private String externalOrderId;

    /**
     * External system name
     */
    @Column(name = "external_system", length = 100)
    private String externalSystem;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Helper Methods ==========

    /**
     * Check if order is urgent or cito
     */
    public boolean isUrgent() {
        return priority == OrderPriority.URGENT || priority == OrderPriority.CITO;
    }

    /**
     * Check if order can be cancelled
     */
    public boolean canBeCancelled() {
        return status.canBeCancelled();
    }

    /**
     * Check if order is complete
     */
    public boolean isComplete() {
        return status == OrderStatus.COMPLETED;
    }

    /**
     * Check if order is cancelled
     */
    public boolean isCancelled() {
        return status == OrderStatus.CANCELLED;
    }

    /**
     * Check if order is recurring
     */
    public boolean isRecurringOrder() {
        return Boolean.TRUE.equals(isRecurring);
    }

    /**
     * Check if this is a child order of a recurring series
     */
    public boolean isRecurringChild() {
        return parentOrderId != null;
    }
}
