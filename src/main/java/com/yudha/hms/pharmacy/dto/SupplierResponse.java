package com.yudha.hms.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Supplier Response DTO.
 *
 * Response object for supplier information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {

    /**
     * Supplier ID
     */
    private UUID id;

    /**
     * Supplier code
     */
    private String code;

    /**
     * Supplier name
     */
    private String name;

    /**
     * Legal entity name
     */
    private String legalName;

    /**
     * Tax ID (NPWP)
     */
    private String taxId;

    /**
     * Business license number
     */
    private String licenseNumber;

    /**
     * Contact person name
     */
    private String contactPerson;

    /**
     * Contact phone number
     */
    private String contactPhone;

    /**
     * Contact email
     */
    private String contactEmail;

    /**
     * Contact fax
     */
    private String contactFax;

    /**
     * Address
     */
    private String address;

    /**
     * City
     */
    private String city;

    /**
     * Province
     */
    private String province;

    /**
     * Postal code
     */
    private String postalCode;

    /**
     * Website
     */
    private String website;

    /**
     * Payment terms in days
     */
    private Integer paymentTermsDays;

    /**
     * Delivery lead time in days
     */
    private Integer deliveryLeadTimeDays;

    /**
     * Minimum order value
     */
    private BigDecimal minimumOrderValue;

    /**
     * Bank name
     */
    private String bankName;

    /**
     * Bank account number
     */
    private String bankAccountNumber;

    /**
     * Bank account holder name
     */
    private String bankAccountHolder;

    /**
     * Supplier rating (1-5)
     */
    private BigDecimal rating;

    /**
     * Is preferred supplier
     */
    private Boolean isPreferred;

    /**
     * Notes
     */
    private String notes;

    /**
     * Active status
     */
    private Boolean active;

    /**
     * Created timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
