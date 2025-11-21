package com.yudha.hms.radiology.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Radiology Modality Entity.
 *
 * Imaging modality master (X-Ray, CT, MRI, USG, Mammography, etc.)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "radiology_modality", schema = "radiology_schema", indexes = {
        @Index(name = "idx_radiology_modality_code", columnList = "code", unique = true),
        @Index(name = "idx_radiology_modality_name", columnList = "name"),
        @Index(name = "idx_radiology_modality_active", columnList = "is_active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RadiologyModality extends SoftDeletableEntity {

    /**
     * Modality code (XRAY, CT_SCAN, MRI, etc.)
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Modality name
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Modality description
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Whether this modality requires radiation exposure
     */
    @Column(name = "requires_radiation")
    @Builder.Default
    private Boolean requiresRadiation = false;

    /**
     * Average duration in minutes
     */
    @Column(name = "average_duration_minutes")
    private Integer averageDurationMinutes;

    /**
     * Active status
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Display order for UI
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
}
