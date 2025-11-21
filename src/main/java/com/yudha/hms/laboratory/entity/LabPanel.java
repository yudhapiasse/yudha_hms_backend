package com.yudha.hms.laboratory.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Lab Panel Entity.
 *
 * Test panels/packages that group related tests together with package pricing.
 * Examples: Complete Blood Count Panel, Lipid Profile, Liver Function Panel, etc.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_panel", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_panel_code", columnList = "panel_code", unique = true),
        @Index(name = "idx_lab_panel_name", columnList = "name"),
        @Index(name = "idx_lab_panel_category", columnList = "category_id"),
        @Index(name = "idx_lab_panel_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabPanel extends SoftDeletableEntity {

    /**
     * Panel code (internal hospital code)
     */
    @Column(name = "panel_code", nullable = false, unique = true, length = 50)
    private String panelCode;

    /**
     * Panel name
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Panel description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Category (optional grouping)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private LabTestCategory category;

    // ========== Pricing ==========

    /**
     * Package price (usually discounted compared to individual test prices)
     */
    @Column(name = "package_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal packagePrice;

    /**
     * Discount percentage off individual test prices
     */
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    /**
     * BPJS package tariff
     */
    @Column(name = "bpjs_package_tariff", precision = 15, scale = 2)
    private BigDecimal bpjsPackageTariff;

    // ========== Configuration ==========

    /**
     * Is this a popular/frequently ordered panel
     */
    @Column(name = "is_popular")
    @Builder.Default
    private Boolean isPopular = false;

    /**
     * Display order in lists
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * Clinical indication for this panel
     */
    @Column(name = "clinical_indication", columnDefinition = "TEXT")
    private String clinicalIndication;

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

    // ========== Helper Methods ==========

    /**
     * Check if panel is popular
     */
    public boolean isPopular() {
        return Boolean.TRUE.equals(isPopular);
    }

    /**
     * Get effective price (package price or BPJS tariff based on insurance)
     */
    public BigDecimal getEffectivePrice(boolean isBpjsPatient) {
        if (isBpjsPatient && bpjsPackageTariff != null) {
            return bpjsPackageTariff;
        }
        return packagePrice;
    }
}
