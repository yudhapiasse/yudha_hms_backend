package com.yudha.hms.laboratory.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Lab Test Category Entity.
 *
 * Test categories (Hematology, Chemistry, Microbiology, etc.)
 * Supports hierarchical categorization.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_test_category", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_test_category_code", columnList = "code", unique = true),
        @Index(name = "idx_lab_test_category_name", columnList = "name"),
        @Index(name = "idx_lab_test_category_parent", columnList = "parent_id"),
        @Index(name = "idx_lab_test_category_active", columnList = "active"),
        @Index(name = "idx_lab_test_category_level", columnList = "level")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabTestCategory extends SoftDeletableEntity {

    /**
     * Category code
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
    private LabTestCategory parent;

    /**
     * Hierarchy level (0 = root)
     */
    @Column(name = "level", nullable = false)
    @Builder.Default
    private Integer level = 0;

    /**
     * Display order
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * Icon name (for UI)
     */
    @Column(name = "icon", length = 100)
    private String icon;

    /**
     * Color code (for UI)
     */
    @Column(name = "color", length = 50)
    private String color;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
