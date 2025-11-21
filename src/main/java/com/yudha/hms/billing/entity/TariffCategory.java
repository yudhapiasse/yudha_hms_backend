package com.yudha.hms.billing.entity;

import com.yudha.hms.billing.constant.TariffType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Tariff Category Entity for Hospital Billing System.
 *
 * Represents categories for organizing tariffs/pricing items.
 * Examples: "Rawat Jalan", "Rawat Inap", "Laboratorium Kimia Klinik", etc.
 *
 * Features:
 * - Hierarchical structure (parent-child categories)
 * - Type-based categorization
 * - Active/inactive status
 * - Display order for UI
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "tariff_category", schema = "billing_schema", indexes = {
    @Index(name = "idx_tariff_cat_code", columnList = "code", unique = true),
    @Index(name = "idx_tariff_cat_type", columnList = "tariff_type"),
    @Index(name = "idx_tariff_cat_parent", columnList = "parent_id"),
    @Index(name = "idx_tariff_cat_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffCategory extends SoftDeletableEntity {

    /**
     * Category code (unique identifier)
     * Example: "LAB-CHEM", "RAD-XRAY", "ROOM-VIP"
     */
    @Column(name = "code", length = 50, nullable = false, unique = true)
    @NotBlank(message = "Category code is required")
    @Size(max = 50, message = "Code must not exceed 50 characters")
    private String code;

    /**
     * Category name
     * Example: "Laboratorium Kimia Klinik", "Radiologi X-Ray"
     */
    @Column(name = "name", length = 200, nullable = false)
    @NotBlank(message = "Category name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    /**
     * Category description
     */
    @Column(name = "description", length = 500)
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Tariff type this category belongs to
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type", length = 50, nullable = false)
    private TariffType tariffType;

    /**
     * Parent category for hierarchical structure
     * NULL for top-level categories
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private TariffCategory parent;

    /**
     * Display order for sorting in UI
     */
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    /**
     * Active status
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Notes for internal reference
     */
    @Column(name = "notes", length = 1000)
    private String notes;
}
