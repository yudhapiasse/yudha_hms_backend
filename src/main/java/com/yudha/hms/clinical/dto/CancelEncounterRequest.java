package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cancel Encounter Request DTO.
 *
 * Enhanced encounter cancellation with validation rules.
 * Requires cancellation reason and checks for linked data.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelEncounterRequest {

    @NotBlank(message = "Cancellation reason is required")
    @Size(min = 10, max = 500, message = "Cancellation reason must be between 10 and 500 characters")
    private String cancellationReason;

    @NotBlank(message = "Cancelled by is required")
    private String cancelledBy;

    private Boolean forceCancel; // Override validation checks (requires supervisor)

    private Boolean reverseBilling; // Whether to reverse billing transactions

    private String supervisorApprovalCode; // For force cancellation

    private String additionalNotes;
}
