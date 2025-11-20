package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.TransferStatus;
import com.yudha.hms.clinical.entity.TransferType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transfer Response DTO.
 *
 * Returns transfer information to the client.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    // ========== Identity ==========
    private UUID id;
    private String transferNumber;
    private UUID encounterId;
    private UUID patientId;

    // ========== Transfer Details ==========
    private TransferType transferType;
    private String transferTypeDisplay;
    private String transferTypeIndonesian;

    private UUID fromDepartmentId;
    private String fromDepartment;
    private UUID fromLocationId;
    private String fromLocation;

    private UUID toDepartmentId;
    private String toDepartment;
    private UUID toLocationId;
    private String toLocation;

    // ========== Status ==========
    private TransferStatus transferStatus;
    private String transferStatusDisplay;
    private String transferStatusIndonesian;

    // ========== Timeline ==========
    private LocalDateTime transferRequestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime transferAcceptedAt;
    private LocalDateTime transferCompletedAt;
    private LocalDateTime cancelledAt;

    // Calculated durations (in minutes)
    private Long approvalDurationMinutes;
    private Long totalTransferDurationMinutes;

    // ========== Request Details ==========
    private UUID requestedById;
    private String requestedByName;
    private String reasonForTransfer;
    private String urgency;

    // ========== Transferring Practitioner ==========
    private UUID transferringPractitionerId;
    private String transferringPractitionerName;

    // ========== Approval ==========
    private Boolean requiresApproval;
    private UUID approvedById;
    private String approvedByName;
    private String approvalNotes;

    // ========== Acceptance/Rejection ==========
    private UUID acceptedById;
    private String acceptedByName;
    private String rejectionReason;

    // ========== Receiving Team ==========
    private UUID receivingPractitionerId;
    private String receivingPractitionerName;
    private UUID receivingDoctorId;
    private String receivingDoctorName;
    private UUID receivingNurseId;
    private String receivingNurseName;

    // ========== Handover Notes ==========
    private String handoverSummary;
    private String currentCondition;
    private String activeMedications;
    private String specialInstructions;

    // ========== Transport ==========
    private Boolean requiresTransport;
    private String requiresEquipment;
    private String modeOfTransport;

    // ========== Cancellation ==========
    private String cancelledBy;
    private String cancellationReason;

    // ========== Computed Flags ==========
    private Boolean isActive;
    private Boolean isCompleted;
    private Boolean isPending;
    private Boolean canBeCancelled;
    private Boolean needsApproval;

    // ========== Audit ==========
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
