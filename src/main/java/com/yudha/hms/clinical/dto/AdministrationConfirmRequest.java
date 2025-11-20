package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for confirming medication administration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministrationConfirmRequest {

    @NotNull(message = "Administered by ID is required")
    private UUID administeredById;

    @NotBlank(message = "Administered by name is required")
    private String administeredByName;

    private String administeredByRole;

    private LocalDateTime actualAdministrationDateTime;

    private String administrationSite;

    private String patientResponse;

    private String administrationNotes;

    // PRN specific
    private String prnReason;

    private String prnEffectiveness;
}
