package com.yudha.hms.billing.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Package Deal Entity for Hospital Billing System.
 *
 * Represents bundled services/packages offered at discounted rates:
 * - Health checkup packages (Basic, Executive, Premium)
 * - Delivery packages (Normal, Caesar)
 * - Surgery packages
 * - Treatment packages
 *
 * Features:
 * - Multiple tariff items bundled together
 * - Package pricing (usually discounted from individual item total)
 * - Validity period
 * - Payment type specific (Cash, Insurance, BPJS)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "package_deal", schema = "billing_schema", indexes = {
    @Index(name = "idx_package_code", columnList = "code", unique = true),
    @Index(name = "idx_package_name", columnList = "name"),
    @Index(name = "idx_package_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDeal extends SoftDeletableEntity {

    /**
     * Package code (unique identifier)
     * Example: "PKG-MCU-BASIC", "PKG-DELIVERY-NORMAL"
     */
    @Column(name = "code", length = 100, nullable = false, unique = true)
    @NotBlank(message = "Package code is required")
    @Size(max = 100, message = "Code must not exceed 100 characters")
    private String code;

    /**
     * Package name
     * Example: "Medical Checkup Basic", "Paket Persalinan Normal"
     */
    @Column(name = "name", length = 300, nullable = false)
    @NotBlank(message = "Package name is required")
    @Size(max = 300, message = "Name must not exceed 300 characters")
    private String name;

    /**
     * Package description
     */
    @Column(name = "description", length = 2000)
    private String description;

    /**
     * Package items (services included)
     */
    @OneToMany(mappedBy = "packageDeal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PackageDealItem> items = new ArrayList<>();

    /**
     * Total regular price (sum of individual item prices)
     */
    @Column(name = "regular_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Regular price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal regularPrice;

    /**
     * Package price (discounted price)
     */
    @Column(name = "package_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Package price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal packagePrice;

    /**
     * Discount amount
     */
    @Column(name = "discount_amount", precision = 15, scale = 2)
    private BigDecimal discountAmount;

    /**
     * Discount percentage
     */
    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    /**
     * Effective date
     */
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /**
     * Expiry date
     */
    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    /**
     * Active status
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Applicable for payment types (comma-separated)
     * Example: "CASH,INSURANCE"
     */
    @Column(name = "applicable_payment_types", length = 200)
    private String applicablePaymentTypes;

    /**
     * Terms and conditions
     */
    @Column(name = "terms_conditions", length = 2000)
    private String termsConditions;

    /**
     * Notes
     */
    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * Add item to package
     *
     * @param item package item
     */
    public void addItem(PackageDealItem item) {
        items.add(item);
        item.setPackageDeal(this);
    }

    /**
     * Remove item from package
     *
     * @param item package item
     */
    public void removeItem(PackageDealItem item) {
        items.remove(item);
        item.setPackageDeal(null);
    }

    /**
     * Check if package is currently valid
     *
     * @return true if valid
     */
    public boolean isCurrentlyValid() {
        LocalDate now = LocalDate.now();
        boolean afterStart = effectiveDate == null || !now.isBefore(effectiveDate);
        boolean beforeEnd = expiryDate == null || !now.isAfter(expiryDate);
        return active && afterStart && beforeEnd;
    }

    /**
     * Calculate discount amount
     */
    public void calculateDiscount() {
        if (regularPrice != null && packagePrice != null) {
            discountAmount = regularPrice.subtract(packagePrice);
            if (regularPrice.compareTo(BigDecimal.ZERO) > 0) {
                discountPercentage = discountAmount.divide(regularPrice, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            }
        }
    }
}
