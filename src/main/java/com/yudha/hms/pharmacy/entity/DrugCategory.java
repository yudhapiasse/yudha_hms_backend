package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Drug Category Entity.
 *
 * Hierarchical categorization of drugs (e.g., Antibiotics > Beta-Lactams > Penicillins).
 * Supports multi-level classification for better drug organization.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "drug_category", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_drug_category_code", columnList = "code", unique = true),
        @Index(name = "idx_drug_category_name", columnList = "name"),
        @Index(name = "idx_drug_category_parent", columnList = "parent_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DrugCategory extends SoftDeletableEntity {

    /**
     * Category code (unique identifier)
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Category name
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Category description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Parent category (for hierarchical structure)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private DrugCategory parent;

    /**
     * Category level (0 = root, 1 = first level, etc.)
     */
    @Column(name = "level")
    private Integer level;

    /**
     * Display order
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Check if this is a root category
     */
    public boolean isRootCategory() {
        return parent == null;
    }

    /**
     * Get full category path
     */
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }
}
