package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Approval Request DTO.
 *
 * Used for approving department transfers (ICU/special care).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {

    private UUID approvedById;
    private String approvedByName;
    private String approvalNotes;
    private Boolean approved; // true = approve, false = reject
    private String rejectionReason; // Required if approved = false
}
