package com.yudha.hms.billing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for approving insurance claims.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApprovalRequest {

    @NotNull(message = "Approved amount is required")
    @DecimalMin(value = "0.0", message = "Approved amount must be non-negative")
    private BigDecimal approvedAmount;

    private String approvedBy;

    private String approvalNotes;

    private Boolean partialApproval;
}
