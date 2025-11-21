package com.yudha.hms.pharmacy.dto;

import com.yudha.hms.pharmacy.constant.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPrescriptionRequest {

    @NotNull(message = "Verification status is required")
    private VerificationStatus status;

    private Boolean interactionsFound;
    private String interactionDetails;

    private Boolean dosageIssuesFound;
    private String dosageIssues;

    private Boolean allergiesFound;
    private String allergyDetails;

    private String changesMade;
    private String rejectionReason;
    private String clarificationNeeded;
    private String comments;
}
