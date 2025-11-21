package com.yudha.hms.radiology.entity;

import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.radiology.constant.OrderStatus;
import com.yudha.hms.radiology.constant.TransportationStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Order Entity.
 *
 * Radiology examination orders
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "radiology_order", schema = "radiology_schema", indexes = {
        @Index(name = "idx_radiology_order_number", columnList = "order_number", unique = true),
        @Index(name = "idx_radiology_order_patient", columnList = "patient_id"),
        @Index(name = "idx_radiology_order_encounter", columnList = "encounter_id"),
        @Index(name = "idx_radiology_order_doctor", columnList = "ordering_doctor_id"),
        @Index(name = "idx_radiology_order_status", columnList = "status"),
        @Index(name = "idx_radiology_order_priority", columnList = "priority"),
        @Index(name = "idx_radiology_order_date", columnList = "order_date"),
        @Index(name = "idx_radiology_order_scheduled_date", columnList = "scheduled_date"),
        @Index(name = "idx_radiology_order_room", columnList = "room_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RadiologyOrder extends SoftDeletableEntity {

    /**
     * Order number (unique identifier)
     */
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    /**
     * Patient reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Encounter reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    private Encounter encounter;

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
     * Order date
     */
    @Column(name = "order_date", nullable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    /**
     * Priority
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 50)
    @Builder.Default
    private OrderPriority priority = OrderPriority.ROUTINE;

    /**
     * Clinical indication
     */
    @Column(name = "clinical_indication", columnDefinition = "TEXT")
    private String clinicalIndication;

    /**
     * Diagnosis text
     */
    @Column(name = "diagnosis_text", length = 500)
    private String diagnosisText;

    /**
     * Status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    /**
     * Scheduled date
     */
    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    /**
     * Scheduled time
     */
    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    /**
     * Room reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private RadiologyRoom room;

    /**
     * Technician ID
     */
    @Column(name = "technician_id")
    private UUID technicianId;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Special instructions
     */
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // ========== Phase 11.2: Patient Safety Checks ==========

    /**
     * Is patient pregnant? (for female patients)
     */
    @Column(name = "is_pregnant")
    @Builder.Default
    private Boolean isPregnant = false;

    /**
     * Who verified pregnancy status
     */
    @Column(name = "pregnancy_verified_by")
    private UUID pregnancyVerifiedBy;

    /**
     * When was pregnancy status verified
     */
    @Column(name = "pregnancy_verified_at")
    private LocalDateTime pregnancyVerifiedAt;

    /**
     * Does patient have contrast allergy?
     */
    @Column(name = "has_contrast_allergy")
    @Builder.Default
    private Boolean hasContrastAllergy = false;

    /**
     * Details of contrast allergy
     */
    @Column(name = "contrast_allergy_details", columnDefinition = "TEXT")
    private String contrastAllergyDetails;

    /**
     * Who verified contrast allergy status
     */
    @Column(name = "contrast_allergy_verified_by")
    private UUID contrastAllergyVerifiedBy;

    /**
     * When was contrast allergy status verified
     */
    @Column(name = "contrast_allergy_verified_at")
    private LocalDateTime contrastAllergyVerifiedAt;

    // ========== Phase 11.2: Transportation Coordination ==========

    /**
     * Does patient require transportation? (typically for inpatients)
     */
    @Column(name = "requires_transportation")
    @Builder.Default
    private Boolean requiresTransportation = false;

    /**
     * Transportation status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_status", length = 50)
    @Builder.Default
    private TransportationStatus transportationStatus = TransportationStatus.NOT_REQUIRED;

    /**
     * When was transportation requested
     */
    @Column(name = "transportation_requested_at")
    private LocalDateTime transportationRequestedAt;

    /**
     * When was transportation completed (patient returned to ward)
     */
    @Column(name = "transportation_completed_at")
    private LocalDateTime transportationCompletedAt;

    /**
     * Transportation notes
     */
    @Column(name = "transportation_notes", columnDefinition = "TEXT")
    private String transportationNotes;
}
