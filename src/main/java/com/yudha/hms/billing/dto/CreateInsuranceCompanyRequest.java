package com.yudha.hms.billing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating insurance companies.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInsuranceCompanyRequest {

    @NotBlank(message = "Company code is required")
    private String code;

    @NotBlank(message = "Company name is required")
    private String name;

    private String companyType;
    private String licenseNumber;
    private String taxId;

    // Contact information
    private String contactPerson;
    private String contactPhone;

    @Email(message = "Invalid email format")
    private String contactEmail;

    private String contactFax;

    // Address
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String website;

    // Contract information
    @NotNull(message = "Contract start date is required")
    private LocalDate contractStartDate;

    @NotNull(message = "Contract end date is required")
    private LocalDate contractEndDate;

    private String contractNumber;
    private Integer paymentTermsDays;

    // Financial information
    private BigDecimal creditLimit;
    private BigDecimal defaultCoveragePercentage;

    // Coverage settings
    private Boolean requiresPreAuthorization;
    private Integer claimSubmissionDeadlineDays;

    // Claim submission
    private Boolean electronicClaimSupported;

    @Email(message = "Invalid email format")
    private String claimSubmissionEmail;

    private String claimSubmissionPortal;

    // Banking information
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;

    // Notes
    private String notes;

    // Status
    private Boolean active;
}
