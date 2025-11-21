package com.yudha.hms.billing.dto;

import com.yudha.hms.billing.constant.ClaimType;
import com.yudha.hms.billing.constant.TariffType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO for creating insurance claims.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInsuranceClaimRequest {

    @NotNull(message = "Insurance company ID is required")
    private UUID insuranceCompanyId;

    @NotNull(message = "Invoice ID is required")
    private UUID invoiceId;

    @NotBlank(message = "Policy number is required")
    private String policyNumber;

    private String policyHolderName;
    private String relationshipToHolder;

    @NotNull(message = "Claim type is required")
    private ClaimType claimType;

    @NotNull(message = "Service start date is required")
    private LocalDate serviceStartDate;

    @NotNull(message = "Service end date is required")
    private LocalDate serviceEndDate;

    private String diagnosisCodes;
    private String primaryDiagnosis;
    private String procedureCodes;

    private UUID treatingPhysicianId;
    private String treatingPhysicianName;

    private BigDecimal coveragePercentage;

    // Pre-authorization
    private String preAuthorizationNumber;
    private LocalDate preAuthorizationDate;

    // Coordination of Benefits (COB)
    private Boolean requiresCob;
    private String primaryClaimNumber;
    private String primaryInsuranceCompany;
    private BigDecimal primaryInsurancePaid;

    // Notes
    private String notes;

    // Claim items
    @Valid
    private List<ClaimItemRequest> items;

    /**
     * Nested DTO for claim item requests
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClaimItemRequest {

        @NotNull(message = "Service date is required")
        private LocalDate serviceDate;

        private UUID invoiceItemId;

        @NotNull(message = "Item type is required")
        private TariffType itemType;

        private String itemCode;

        @NotBlank(message = "Item description is required")
        private String itemDescription;

        private String diagnosisCode;
        private String procedureCode;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        private String unit;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
        private BigDecimal unitPrice;

        private BigDecimal claimAmount;

        private UUID providerId;
        private String providerName;
        private String departmentName;

        // Dental specific
        private String toothCode;

        // Surgical specific
        private String bodySite;

        private String notes;
    }
}
