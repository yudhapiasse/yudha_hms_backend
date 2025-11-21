package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Supplier Entity.
 *
 * Pharmaceutical supplier/vendor information for drug procurement.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "supplier", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_supplier_code", columnList = "code", unique = true),
        @Index(name = "idx_supplier_name", columnList = "name"),
        @Index(name = "idx_supplier_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Supplier extends SoftDeletableEntity {

    /**
     * Supplier code (unique identifier)
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Supplier name
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Legal entity name
     */
    @Column(name = "legal_name", length = 200)
    private String legalName;

    /**
     * Tax ID (NPWP)
     */
    @Column(name = "tax_id", length = 50)
    private String taxId;

    /**
     * Business license number
     */
    @Column(name = "license_number", length = 100)
    private String licenseNumber;

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
     * Contact fax
     */
    @Column(name = "contact_fax", length = 50)
    private String contactFax;

    /**
     * Address
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
     * Website
     */
    @Column(name = "website", length = 200)
    private String website;

    /**
     * Payment terms in days
     */
    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;

    /**
     * Delivery lead time in days
     */
    @Column(name = "delivery_lead_time_days")
    private Integer deliveryLeadTimeDays;

    /**
     * Minimum order value
     */
    @Column(name = "minimum_order_value", precision = 15, scale = 2)
    private java.math.BigDecimal minimumOrderValue;

    /**
     * Bank name
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
     * Supplier rating (1-5)
     */
    @Column(name = "rating", precision = 3, scale = 2)
    private java.math.BigDecimal rating;

    /**
     * Is preferred supplier
     */
    @Column(name = "is_preferred")
    private Boolean isPreferred;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
