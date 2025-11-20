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
    // Department IDs (if using department master table)
    @Column(name = "from_department_id")
    private UUID fromDepartmentId;

    @Column(name = "to_department_id")
    private UUID toDepartmentId;

    // Department names (for display and backward compatibility)
    @Column(name = "from_department", nullable = false, length = 100)
    @NotBlank(message = "From department is required")
    private String fromDepartment;

    @Column(name = "to_department", nullable = false, length = 100)
    @NotBlank(message = "To department is required")
    private String toDepartment;

    // Location IDs (bed/room if using location master table)
    @Column(name = "from_location_id")
    private UUID fromLocationId;

    @Column(name = "to_location_id")
    private UUID toLocationId;

    // Location names (for display and backward compatibility)
    @Column(name = "from_location", length = 200)
    private String fromLocation;

    @Column(name = "to_location", length = 200)
    private String toLocation;

    // ========== Transfer Type ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_type", nullable = false, length = 30)
    @NotNull(message = "Transfer type is required")
    private TransferType transferType;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status", nullable = false, length = 20)
    @NotNull(message = "Transfer status is required")
    @Builder.Default
    private TransferStatus transferStatus = TransferStatus.REQUESTED;

    // ========== Request Details ==========
    @Column(name = "requested_by_id")
    private UUID requestedById;

    @Column(name = "requested_by_name", length = 200)
    private String requestedByName;

    // Transferring practitioner (who is sending the patient)
    @Column(name = "transferring_practitioner_id")
    private UUID transferringPractitionerId;

    @Column(name = "transferring_practitioner_name", length = 200)
    private String transferringPractitionerName;

    @Column(name = "reason_for_transfer", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Reason for transfer is required")
    private String reasonForTransfer;

    @Column(name = "urgency", length = 20)
    private String urgency; // ROUTINE, URGENT, EMERGENCY

    // ========== Approval (for ICU/special care transfers) ==========
    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;

    @Column(name = "approved_by_id")
    private UUID approvedById;

    @Column(name = "approved_by_name", length = 200)
    private String approvedByName;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;

    // ========== Acceptance/Rejection ==========
    @Column(name = "accepted_by_id")
    private UUID acceptedById;

    @Column(name = "accepted_by_name", length = 200)
    private String acceptedByName;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // ========== Receiving Team ==========
    @Column(name = "receiving_practitioner_id")
    private UUID receivingPractitionerId;

    @Column(name = "receiving_practitioner_name", length = 200)
    private String receivingPractitionerName;

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

    /**
     * Change transfer status with validation.
     *
     * @param newStatus the target status
     * @throws com.yudha.hms.clinical.exception.InvalidStatusTransitionException if transition is invalid
     */
    public void changeStatus(TransferStatus newStatus) {
        if (!this.transferStatus.canTransitionTo(newStatus)) {
            throw new com.yudha.hms.clinical.exception.InvalidStatusTransitionException(
                String.format("Transfer status transition from %s to %s is not allowed",
                    transferStatus.getIndonesianName(), newStatus.getIndonesianName())
            );
        }

        this.transferStatus = newStatus;
        updateTimestampsForStatus(newStatus);
    }

    /**
     * Update timestamps based on new status.
     */
    private void updateTimestampsForStatus(TransferStatus newStatus) {
        LocalDateTime now = LocalDateTime.now();
        switch (newStatus) {
            case APPROVED -> approvedAt = now;
            case ACCEPTED -> transferAcceptedAt = now;
            case COMPLETED -> transferCompletedAt = now;
            case CANCELLED -> cancelledAt = now;
        }
    }

    /**
     * Request approval for transfer (for ICU/special care transfers).
     */
    public void requestApproval() {
        if (this.transferType.requiresApproval()) {
            this.requiresApproval = true;
            changeStatus(TransferStatus.PENDING_APPROVAL);
        }
    }

    /**
     * Approve transfer request.
     *
     * @param approvedById ID of approver
     * @param approvedByName name of approver
     * @param notes approval notes
     */
    public void approve(UUID approvedById, String approvedByName, String notes) {
        changeStatus(TransferStatus.APPROVED);
        this.approvedById = approvedById;
        this.approvedByName = approvedByName;
        this.approvalNotes = notes;
    }

    /**
     * Accept transfer request.
     *
     * @param acceptedById ID of person accepting
     * @param acceptedByName name of person accepting
     */
    public void accept(UUID acceptedById, String acceptedByName) {
        changeStatus(TransferStatus.ACCEPTED);
        this.acceptedById = acceptedById;
        this.acceptedByName = acceptedByName;
    }

    /**
     * Reject transfer request.
     *
     * @param reason reason for rejection
     */
    public void reject(String reason) {
        changeStatus(TransferStatus.REJECTED);
        this.rejectionReason = reason;
    }

    /**
     * Start transfer (patient in transit).
     */
    public void startTransfer() {
        changeStatus(TransferStatus.IN_TRANSIT);
    }

    /**
     * Complete transfer.
     */
    public void complete() {
        changeStatus(TransferStatus.COMPLETED);
    }

    /**
     * Cancel transfer.
     *
     * @param reason reason for cancellation
     * @param cancelledByUser user who cancelled
     */
    public void cancel(String reason, String cancelledByUser) {
        if (!this.transferStatus.canBeCancelled()) {
            throw new IllegalStateException(
                "Cannot cancel transfer in status: " + transferStatus.getIndonesianName()
            );
        }
        changeStatus(TransferStatus.CANCELLED);
        this.cancellationReason = reason;
        this.cancelledBy = cancelledByUser;
    }

    /**
     * Check if transfer is completed.
     */
    public boolean isCompleted() {
        return transferStatus.isCompleted();
    }

    /**
     * Check if transfer is pending.
     */
    public boolean isPending() {
        return transferStatus.isPending();
    }

    /**
     * Check if transfer is active.
     */
    public boolean isActive() {
        return transferStatus.isActive();
    }

    /**
     * Check if transfer can be cancelled.
     */
    public boolean canBeCancelled() {
        return transferStatus.canBeCancelled();
    }

    /**
     * Check if transfer requires approval.
     */
    public boolean needsApproval() {
        return Boolean.TRUE.equals(requiresApproval) || transferType.requiresApproval();
    }
}
