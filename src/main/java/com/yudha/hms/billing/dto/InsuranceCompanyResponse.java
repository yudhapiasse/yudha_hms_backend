package com.yudha.hms.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for insurance company responses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceCompanyResponse {

    private UUID id;
    private String code;
    private String name;
    private String companyType;
    private String licenseNumber;
    private String taxId;

    // Contact information
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String contactFax;

    // Address
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String website;

    // Contract information
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private String contractNumber;
    private Boolean contractValid;
    private Integer paymentTermsDays;

    // Financial information
    private BigDecimal creditLimit;
    private BigDecimal currentOutstanding;
    private BigDecimal availableCredit;
    private Boolean creditLimitExceeded;

    // Coverage settings
    private BigDecimal defaultCoveragePercentage;
    private Boolean requiresPreAuthorization;
    private Integer claimSubmissionDeadlineDays;

    // Claim submission
    private Boolean electronicClaimSupported;
    private String claimSubmissionEmail;
    private String claimSubmissionPortal;

    // Banking information
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;

    // Status and notes
    private String notes;
    private Boolean active;

    // Audit fields
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
