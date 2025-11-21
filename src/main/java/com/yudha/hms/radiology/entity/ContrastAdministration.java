package com.yudha.hms.radiology.entity;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.radiology.constant.ContrastType;
import com.yudha.hms.radiology.constant.ReactionSeverity;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrast Administration Entity.
 *
 * Contrast media tracking
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "contrast_administration", schema = "radiology_schema", indexes = {
        @Index(name = "idx_contrast_administration_order_item", columnList = "order_item_id"),
        @Index(name = "idx_contrast_administration_patient", columnList = "patient_id"),
        @Index(name = "idx_contrast_administration_administered_by", columnList = "administered_by"),
        @Index(name = "idx_contrast_administration_administered_at", columnList = "administered_at"),
        @Index(name = "idx_contrast_administration_batch", columnList = "batch_number")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContrastAdministration extends BaseEntity {

    /**
     * Order item reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private RadiologyOrderItem orderItem;

    /**
     * Patient reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Contrast name
     */
    @Column(name = "contrast_name", nullable = false, length = 200)
    private String contrastName;

    /**
     * Contrast type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "contrast_type", nullable = false, length = 50)
    private ContrastType contrastType;

    /**
     * Volume in ml
     */
    @Column(name = "volume_ml", nullable = false, precision = 10, scale = 2)
    private BigDecimal volumeMl;

    /**
     * Batch number
     */
    @Column(name = "batch_number", length = 100)
    private String batchNumber;

    /**
     * Administered by (user ID)
     */
    @Column(name = "administered_by", nullable = false)
    private UUID administeredBy;

    /**
     * Administered at
     */
    @Column(name = "administered_at", nullable = false)
    @Builder.Default
    private LocalDateTime administeredAt = LocalDateTime.now();

    /**
     * Whether reaction was observed
     */
    @Column(name = "reaction_observed")
    @Builder.Default
    private Boolean reactionObserved = false;

    /**
     * Reaction severity
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_severity", length = 50)
    private ReactionSeverity reactionSeverity;

    /**
     * Reaction description
     */
    @Column(name = "reaction_description", columnDefinition = "TEXT")
    private String reactionDescription;

    /**
     * Treatment given
     */
    @Column(name = "treatment_given", columnDefinition = "TEXT")
    private String treatmentGiven;
}
