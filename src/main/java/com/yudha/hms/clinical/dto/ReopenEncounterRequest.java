package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Reopen Encounter Request DTO.
 *
 * Request to reopen a finished/discharged encounter for missed documentation.
 * Requires supervisor approval and must be within time limit.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReopenEncounterRequest {

    @NotBlank(message = "Reopen reason is required")
    @Size(min = 10, max = 500, message = "Reopen reason must be between 10 and 500 characters")
    private String reopenReason;

    @NotBlank(message = "Requested by is required")
    private String requestedBy;

    @NotNull(message = "Supervisor ID is required")
    private UUID supervisorId;

    @NotBlank(message = "Supervisor approval code is required")
    private String supervisorApprovalCode;

    private String documentationNeeded; // What documentation is missing

    private String additionalNotes;
}
