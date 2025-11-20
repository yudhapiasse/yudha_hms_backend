package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for witness verification of high-alert medication.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WitnessVerificationRequest {

    @NotNull(message = "Witness ID is required")
    private UUID witnessedById;

    @NotBlank(message = "Witness name is required")
    private String witnessedByName;

    @NotBlank(message = "Witness signature is required")
    private String witnessSignature;
}
