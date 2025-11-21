package com.yudha.hms.pharmacy.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create Supplier Request DTO.
 *
 * Request object for creating new supplier.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSupplierRequest {

    /**
     * Supplier code
     */
    @NotBlank(message = "Supplier code is required")
    @Size(max = 50, message = "Supplier code must not exceed 50 characters")
    private String code;

    /**
     * Supplier name
     */
    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Supplier name must not exceed 200 characters")
    private String name;

    /**
     * Legal entity name
     */
    @Size(max = 200, message = "Legal name must not exceed 200 characters")
    private String legalName;

    /**
     * Tax ID (NPWP)
     */
    @Size(max = 50, message = "Tax ID must not exceed 50 characters")
    private String taxId;

    /**
     * Business license number
     */
    @Size(max = 100, message = "License number must not exceed 100 characters")
    private String licenseNumber;

    /**
     * Contact person name
     */
    @Size(max = 200, message = "Contact person must not exceed 200 characters")
    private String contactPerson;

    /**
     * Contact phone number
     */
    @Size(max = 50, message = "Contact phone must not exceed 50 characters")
    private String contactPhone;

    /**
     * Contact email
     */
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Contact email must not exceed 100 characters")
    private String contactEmail;

    /**
     * Contact fax
     */
    @Size(max = 50, message = "Contact fax must not exceed 50 characters")
    private String contactFax;

    /**
     * Address
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    /**
     * City
     */
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    /**
     * Province
     */
    @Size(max = 100, message = "Province must not exceed 100 characters")
    private String province;

    /**
     * Postal code
     */
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    /**
     * Website
     */
    @Size(max = 200, message = "Website must not exceed 200 characters")
    private String website;

    /**
     * Payment terms in days
     */
    @Min(value = 0, message = "Payment terms cannot be negative")
    private Integer paymentTermsDays;

    /**
     * Delivery lead time in days
     */
    @Min(value = 0, message = "Delivery lead time cannot be negative")
    private Integer deliveryLeadTimeDays;

    /**
     * Minimum order value
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum order value cannot be negative")
    private BigDecimal minimumOrderValue;

    /**
     * Bank name
     */
    @Size(max = 100, message = "Bank name must not exceed 100 characters")
    private String bankName;

    /**
     * Bank account number
     */
    @Size(max = 50, message = "Bank account number must not exceed 50 characters")
    private String bankAccountNumber;

    /**
     * Bank account holder name
     */
    @Size(max = 200, message = "Bank account holder must not exceed 200 characters")
    private String bankAccountHolder;

    /**
     * Supplier rating (1-5)
     */
    @DecimalMin(value = "1.0", message = "Rating must be at least 1.0")
    @DecimalMax(value = "5.0", message = "Rating must not exceed 5.0")
    private BigDecimal rating;

    /**
     * Is preferred supplier
     */
    @Builder.Default
    private Boolean isPreferred = false;

    /**
     * Notes
     */
    private String notes;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean active = true;
}
