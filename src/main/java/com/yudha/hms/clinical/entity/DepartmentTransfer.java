package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Department Transfer Entity.
 *
 * Manages patient transfers between departments and locations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "department_transfer", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_transfer_number", columnList = "transfer_number"),
        @Index(name = "idx_transfer_encounter", columnList = "encounter_id"),
        @Index(name = "idx_transfer_patient", columnList = "patient_id"),
        @Index(name = "idx_transfer_status", columnList = "transfer_status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Department and location transfers")
public class DepartmentTransfer extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transfer_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Transfer number is required")
    private String transferNumber; // TRF-20250119-0001

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Transfer Details ==========
    @Column(name = "from_department", nullable = false, length = 100)
    @NotBlank(message = "From department is required")
    private String fromDepartment;

    @Column(name = "from_location", length = 200)
    private String fromLocation;

    @Column(name = "to_department", nullable = false, length = 100)
    @NotBlank(message = "To department is required")
    private String toDepartment;

    @Column(name = "to_location", length = 200)
    private String toLocation;

    // ========== Transfer Type ==========
    @Column(name = "transfer_type", nullable = false, length = 30)
    @NotBlank(message = "Transfer type is required")
    private String transferType; // INTERNAL, EXTERNAL, ICU, WARD, OPERATING_ROOM

    // ========== Timing ==========
    @Column(name = "transfer_requested_at", nullable = false)
    @NotNull(message = "Transfer request time is required")
    @Builder.Default
    private LocalDateTime transferRequestedAt = LocalDateTime.now();

    @Column(name = "transfer_accepted_at")
    private LocalDateTime transferAcceptedAt;

    @Column(name = "transfer_completed_at")
    private LocalDateTime transferCompletedAt;

    // ========== Status ==========
    @Column(name = "transfer_status", nullable = false, length = 20)
    @NotBlank(message = "Transfer status is required")
    @Builder.Default
    private String transferStatus = "REQUESTED"; // REQUESTED, ACCEPTED, IN_TRANSIT, COMPLETED, REJECTED, CANCELLED

    // ========== Request Details ==========
    @Column(name = "requested_by_id")
    private UUID requestedById;

    @Column(name = "requested_by_name", length = 200)
    private String requestedByName;

    @Column(name = "reason_for_transfer", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Reason for transfer is required")
    private String reasonForTransfer;

    @Column(name = "urgency", length = 20)
    private String urgency; // ROUTINE, URGENT, EMERGENCY

    // ========== Acceptance/Rejection ==========
    @Column(name = "accepted_by_id")
    private UUID acceptedById;

    @Column(name = "accepted_by_name", length = 200)
    private String acceptedByName;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // ========== Receiving Team ==========
    @Column(name = "receiving_doctor_id")
    private UUID receivingDoctorId;

    @Column(name = "receiving_doctor_name", length = 200)
    private String receivingDoctorName;

    @Column(name = "receiving_nurse_id")
    private UUID receivingNurseId;

    @Column(name = "receiving_nurse_name", length = 200)
    private String receivingNurseName;

    // ========== Handover Notes ==========
    @Column(name = "handover_summary", columnDefinition = "TEXT")
    private String handoverSummary;

    @Column(name = "current_condition", columnDefinition = "TEXT")
    private String currentCondition;

    @Column(name = "active_medications", columnDefinition = "TEXT")
    private String activeMedications;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // ========== Equipment/Transport ==========
    @Column(name = "requires_transport")
    @Builder.Default
    private Boolean requiresTransport = false;

    @Column(name = "requires_equipment", columnDefinition = "TEXT")
    private String requiresEquipment;

    @Column(name = "mode_of_transport", length = 50)
    private String modeOfTransport; // WHEELCHAIR, STRETCHER, BED, AMBULANCE

    // ========== Cancellation ==========
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // ========== Business Methods ==========

    public void accept(UUID acceptedById, String acceptedByName) {
        this.transferStatus = "ACCEPTED";
        this.transferAcceptedAt = LocalDateTime.now();
        this.acceptedById = acceptedById;
        this.acceptedByName = acceptedByName;
    }

    public void reject(String reason) {
        this.transferStatus = "REJECTED";
        this.rejectionReason = reason;
    }

    public void startTransfer() {
        this.transferStatus = "IN_TRANSIT";
    }

    public void complete() {
        this.transferStatus = "COMPLETED";
        this.transferCompletedAt = LocalDateTime.now();
    }

    public void cancel(String reason, String cancelledByUser) {
        this.transferStatus = "CANCELLED";
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancelledBy = cancelledByUser;
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(transferStatus);
    }

    public boolean isPending() {
        return "REQUESTED".equals(transferStatus);
    }
}
