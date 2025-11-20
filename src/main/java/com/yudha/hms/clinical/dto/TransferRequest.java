package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.TransferType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Transfer Request DTO.
 *
 * Used for initiating a new department transfer.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    // ========== Required Fields ==========

    @NotNull(message = "Encounter ID is required")
    private UUID encounterId;

    @NotNull(message = "Transfer type is required")
    private TransferType transferType;

    @NotBlank(message = "Destination department is required")
    private String toDepartment;

    @NotBlank(message = "Reason for transfer is required")
    private String reasonForTransfer;

    // ========== Optional Department/Location References ==========

    private UUID toDepartmentId; // If using department master table
    private String toLocation; // Bed/room name
    private UUID toLocationId; // If using location/bed master table

    // ========== Transferring Practitioner ==========

    private UUID transferringPractitionerId;
    private String transferringPractitionerName;

    // ========== Urgency ==========

    private String urgency; // ROUTINE, URGENT, EMERGENCY

    // ========== Handover Notes (Clinical Summary) ==========

    private String handoverSummary;
    private String currentCondition;
    private String activeMedications;
    private String specialInstructions;

    // ========== Transport Requirements ==========

    private Boolean requiresTransport;
    private String requiresEquipment;
    private String modeOfTransport; // WHEELCHAIR, STRETCHER, BED, AMBULANCE

    // ========== Additional Notes ==========

    private String additionalNotes;
}
