package com.yudha.hms.billing.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Insurance Company Entity.
 *
 * Master data for insurance companies that have contracts
 * with the hospital for patient coverage.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "insurance_company", schema = "billing_schema", indexes = {
        @Index(name = "idx_insurance_company_code", columnList = "code", unique = true),
        @Index(name = "idx_insurance_company_name", columnList = "name"),
        @Index(name = "idx_insurance_company_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsuranceCompany extends SoftDeletableEntity {

    /**
     * Insurance company code (unique identifier)
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Insurance company name
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Insurance company type (e.g., "Health", "Life", "General")
     */
    @Column(name = "company_type", length = 50)
    private String companyType;

    /**
     * Registration/license number
     */
    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    /**
     * Tax identification number (NPWP)
     */
    @Column(name = "tax_id", length = 50)
    private String taxId;

    /**
     * Contact person name
     */
    @Column(name = "contact_person", length = 200)
    private String contactPerson;

    /**
     * Contact phone number
     */
    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    /**
     * Contact email
     */
    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    /**
     * Contact fax number
     */
    @Column(name = "contact_fax", length = 50)
    private String contactFax;

    /**
     * Office address
     */
    @Column(name = "address", length = 500)
    private String address;

    /**
     * City
     */
    @Column(name = "city", length = 100)
    private String city;

    /**
     * Province
     */
    @Column(name = "province", length = 100)
    private String province;

    /**
     * Postal code
     */
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    /**
     * Website URL
     */
    @Column(name = "website", length = 200)
    private String website;

    /**
     * Contract start date
     */
    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    /**
     * Contract end date
     */
    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    /**
     * Contract number
     */
    @Column(name = "contract_number", length = 100)
    private String contractNumber;

    /**
     * Payment terms in days
     */
    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;

    /**
     * Credit limit amount
     */
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;

    /**
     * Current outstanding amount
     */
    @Column(name = "current_outstanding", precision = 15, scale = 2)
    private BigDecimal currentOutstanding;

    /**
     * Default coverage percentage
     */
    @Column(name = "default_coverage_percentage", precision = 5, scale = 2)
    private BigDecimal defaultCoveragePercentage;

    /**
     * Requires pre-authorization flag
     */
    @Column(name = "requires_pre_authorization")
    private Boolean requiresPreAuthorization;

    /**
     * Claim submission deadline in days after service date
     */
    @Column(name = "claim_submission_deadline_days")
    private Integer claimSubmissionDeadlineDays;

    /**
     * Electronic claim submission supported
     */
    @Column(name = "electronic_claim_supported")
    private Boolean electronicClaimSupported;

    /**
     * Claim submission email
     */
    @Column(name = "claim_submission_email", length = 100)
    private String claimSubmissionEmail;

    /**
     * Claim submission portal URL
     */
    @Column(name = "claim_submission_portal", length = 200)
    private String claimSubmissionPortal;

    /**
     * Bank name for payments
     */
    @Column(name = "bank_name", length = 100)
    private String bankName;

    /**
     * Bank account number
     */
    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    /**
     * Bank account holder name
     */
    @Column(name = "bank_account_holder", length = 200)
    private String bankAccountHolder;

    /**
     * Special notes or instructions
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Helper method to check if contract is currently valid
     *
     * @return true if contract is valid
     */
    public boolean isContractValid() {
        if (contractStartDate == null || contractEndDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(contractStartDate) && !today.isAfter(contractEndDate);
    }

    /**
     * Helper method to check if credit limit is exceeded
     *
     * @return true if credit limit exceeded
     */
    public boolean isCreditLimitExceeded() {
        if (creditLimit == null || currentOutstanding == null) {
            return false;
        }
        return currentOutstanding.compareTo(creditLimit) > 0;
    }

    /**
     * Helper method to get available credit
     *
     * @return available credit amount
     */
    public BigDecimal getAvailableCredit() {
        if (creditLimit == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal outstanding = currentOutstanding != null ? currentOutstanding : BigDecimal.ZERO;
        BigDecimal available = creditLimit.subtract(outstanding);
        return available.max(BigDecimal.ZERO);
    }
}
