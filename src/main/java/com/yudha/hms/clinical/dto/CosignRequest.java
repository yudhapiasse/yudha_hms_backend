package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for cosigning a progress note.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CosignRequest {

    @NotNull(message = "Cosigning doctor ID is required")
    private UUID cosignedById;

    @NotBlank(message = "Cosigning doctor name is required")
    private String cosignedByName;
}
