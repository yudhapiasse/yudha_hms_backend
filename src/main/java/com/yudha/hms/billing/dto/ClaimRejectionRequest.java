package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.RejectionReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rejecting insurance claims.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRejectionRequest {

    @NotNull(message = "Rejection reason is required")
    private RejectionReason rejectionReason;

    @NotBlank(message = "Rejection notes are required")
    private String rejectionNotes;

    private String rejectedBy;
}
