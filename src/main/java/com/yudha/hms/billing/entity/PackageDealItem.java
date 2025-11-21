package com.yudha.hms.billing.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Package Deal Item Entity for Hospital Billing System.
 *
 * Represents individual tariff items included in a package deal.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "package_deal_item", schema = "billing_schema", indexes = {
    @Index(name = "idx_pkg_item_package", columnList = "package_id"),
    @Index(name = "idx_pkg_item_tariff", columnList = "tariff_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageDealItem extends SoftDeletableEntity {

    /**
     * Package deal reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    @NotNull(message = "Package is required")
    private PackageDeal packageDeal;

    /**
     * Tariff reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tariff_id", nullable = false)
    @NotNull(message = "Tariff is required")
    private Tariff tariff;

    /**
     * Quantity
     */
    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Unit price (at the time of package creation)
     */
    @Column(name = "unit_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal unitPrice;

    /**
     * Total price (quantity * unitPrice)
     */
    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal totalPrice;

    /**
     * Optional flag (can be excluded from package)
     */
    @Column(name = "is_optional")
    @Builder.Default
    private Boolean optional = false;

    /**
     * Notes
     */
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * Calculate total price
     */
    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
