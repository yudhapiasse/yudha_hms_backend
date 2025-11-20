package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for reporting adverse drug reaction.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdverseReactionRequest {

    @NotBlank(message = "Adverse reaction type is required")
    private String adverseReactionType; // ALLERGIC, SIDE_EFFECT, OTHER

    @NotBlank(message = "Adverse reaction details are required")
    private String adverseReactionDetails;

    @NotBlank(message = "Severity is required")
    private String adverseReactionSeverity; // MILD, MODERATE, SEVERE
}
