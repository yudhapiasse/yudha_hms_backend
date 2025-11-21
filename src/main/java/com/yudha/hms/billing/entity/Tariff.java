package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.TariffType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tariff Entity for Hospital Billing System.
 *
 * Master data for all service pricing in the hospital including:
 * - Room charges (by class)
 * - Doctor fees (consultation, visit, procedures)
 * - Procedure costs
 * - Laboratory test fees
 * - Radiology examination costs
 * - Medicine charges
 * - Other services
 *
 * Indonesian-specific features:
 * - BPJS pricing (Class 1, 2, 3)
 * - INA-CBGs code mapping
 * - Different pricing for cash vs insurance
 * - PPh 23 tax handling
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "tariff", schema = "billing_schema", indexes = {
    @Index(name = "idx_tariff_code", columnList = "code", unique = true),
    @Index(name = "idx_tariff_type", columnList = "tariff_type"),
    @Index(name = "idx_tariff_category", columnList = "category_id"),
    @Index(name = "idx_tariff_active", columnList = "is_active"),
    @Index(name = "idx_tariff_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tariff extends SoftDeletableEntity {

    // ========================================================================
    // BASIC INFORMATION
    // ========================================================================

    /**
     * Tariff code (unique identifier)
     * Example: "ROOM-VIP-001", "LAB-HB-001", "RAD-XRAY-CHEST"
     */
    @Column(name = "code", length = 100, nullable = false, unique = true)
    @NotBlank(message = "Tariff code is required")
    @Size(max = 100, message = "Code must not exceed 100 characters")
    private String code;

    /**
     * Tariff name
     * Example: "VIP Room - Daily", "Complete Blood Count", "Chest X-Ray PA"
     */
    @Column(name = "name", length = 300, nullable = false)
    @NotBlank(message = "Tariff name is required")
    @Size(max = 300, message = "Name must not exceed 300 characters")
    private String name;

    /**
     * Tariff description
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Tariff type (ROOM, DOCTOR_FEE, PROCEDURE, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", length = 50, nullable = false)
    @NotNull(message = "Tariff type is required")
    private TariffType tariffType;

    /**
     * Tariff category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private TariffCategory category;

    // ========================================================================
    // PRICING
    // ========================================================================

    /**
     * Base price (standard/cash price in IDR)
     */
    @Column(name = "base_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be non-negative")
    private BigDecimal basePrice;

    /**
     * BPJS Class 1 price (for BPJS patients class 1)
     */
    @Column(name = "bpjs_class1_price", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal bpjsClass1Price;

    /**
     * BPJS Class 2 price (for BPJS patients class 2)
     */
    @Column(name = "bpjs_class2_price", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal bpjsClass2Price;

    /**
     * BPJS Class 3 price (for BPJS patients class 3)
     */
    @Column(name = "bpjs_class3_price", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal bpjsClass3Price;

    /**
     * Insurance price (for private insurance)
     */
    @Column(name = "insurance_price", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal insurancePrice;

    /**
     * Company price (for corporate agreements)
     */
    @Column(name = "company_price", precision = 15, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal companyPrice;

    // ========================================================================
    // INDONESIAN SPECIFIC
    // ========================================================================

    /**
     * INA-CBGs code (Indonesian Case Based Groups)
     * For hospital services tariff grouping
     */
    @Column(name = "ina_cbgs_code", length = 50)
    private String inaCbgsCode;

    /**
     * ICD-10 code reference (if applicable)
     */
    @Column(name = "icd10_code", length = 20)
    private String icd10Code;

    /**
     * ICD-9-CM procedure code reference (if applicable)
     */
    @Column(name = "icd9_code", length = 20)
    private String icd9Code;

    /**
     * PPh 23 tax applicable (for professional services)
     * Indonesian tax on services
     */
    @Column(name = "is_pph23_applicable")
    @Builder.Default
    private Boolean pph23Applicable = false;

    /**
     * PPh 23 tax percentage (typically 2% for medical services)
     */
    @Column(name = "pph23_percentage", precision = 5, scale = 2)
    private BigDecimal pph23Percentage;

    // ========================================================================
    // BILLING CONFIGURATION
    // ========================================================================

    /**
     * Unit of measurement
     * Example: "per day", "per procedure", "per test", "per item"
     */
    @Column(name = "unit", length = 50)
    private String unit;

    /**
     * Minimum quantity
     */
    @Column(name = "min_quantity")
    @Builder.Default
    private Integer minQuantity = 1;

    /**
     * Maximum quantity
     */
    @Column(name = "max_quantity")
    private Integer maxQuantity;

    /**
     * Allow discount
     */
    @Column(name = "allow_discount")
    @Builder.Default
    private Boolean allowDiscount = true;

    /**
     * Maximum discount percentage allowed
     */
    @Column(name = "max_discount_percentage", precision = 5, scale = 2)
    private BigDecimal maxDiscountPercentage;

    // ========================================================================
    // VALIDITY
    // ========================================================================

    /**
     * Effective date (when this tariff becomes active)
     */
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /**
     * Expiry date (when this tariff expires)
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Active status
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // ========================================================================
    // REFERENCE DATA
    // ========================================================================

    /**
     * External code/SKU (for integration with other systems)
     */
    @Column(name = "external_code", length = 100)
    private String externalCode;

    /**
     * Notes for internal reference
     */
    @Column(name = "notes", length = 1000)
    private String notes;

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Get price based on payment type
     *
     * @param paymentType payment type (CASH, BPJS_CLASS1, BPJS_CLASS2, BPJS_CLASS3, INSURANCE, COMPANY)
     * @return price for the given payment type
     */
    public BigDecimal getPriceByPaymentType(String paymentType) {
        return switch (paymentType.toUpperCase()) {
            case "BPJS_CLASS1" -> bpjsClass1Price != null ? bpjsClass1Price : basePrice;
            case "BPJS_CLASS2" -> bpjsClass2Price != null ? bpjsClass2Price : basePrice;
            case "BPJS_CLASS3" -> bpjsClass3Price != null ? bpjsClass3Price : basePrice;
            case "INSURANCE" -> insurancePrice != null ? insurancePrice : basePrice;
            case "COMPANY" -> companyPrice != null ? companyPrice : basePrice;
            default -> basePrice;
        };
    }

    /**
     * Check if tariff is currently valid
     *
     * @return true if valid
     */
    public boolean isCurrentlyValid() {
        LocalDate now = LocalDate.now();
        boolean afterStart = effectiveDate == null || !now.isBefore(effectiveDate);
        boolean beforeEnd = expiryDate == null || !now.isAfter(expiryDate);
        return active && afterStart && beforeEnd;
    }
}
