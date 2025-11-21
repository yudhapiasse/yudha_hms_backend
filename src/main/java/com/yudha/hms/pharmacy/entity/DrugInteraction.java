package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.InteractionSeverity;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Drug Interaction Entity.
 *
 * Database of drug-drug interactions for clinical decision support.
 * Helps prevent prescribing contraindicated drug combinations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "drug_interaction", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_interaction_drug1", columnList = "drug1_id"),
        @Index(name = "idx_interaction_drug2", columnList = "drug2_id"),
        @Index(name = "idx_interaction_severity", columnList = "severity")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DrugInteraction extends BaseEntity {

    /**
     * First drug in interaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug1_id", nullable = false)
    private Drug drug1;

    /**
     * Second drug in interaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drug2_id", nullable = false)
    private Drug drug2;

    /**
     * Interaction severity
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 50)
    private InteractionSeverity severity;

    /**
     * Interaction description
     */
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    /**
     * Clinical effects
     */
    @Column(name = "clinical_effects", columnDefinition = "TEXT")
    private String clinicalEffects;

    /**
     * Management recommendation
     */
    @Column(name = "management", columnDefinition = "TEXT")
    private String management;

    /**
     * Evidence level (e.g., "Well-established", "Theoretical")
     */
    @Column(name = "evidence_level", length = 100)
    private String evidenceLevel;

    /**
     * Reference/source
     */
    @Column(name = "reference", columnDefinition = "TEXT")
    private String reference;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Check if involves given drug
     */
    public boolean involvesDrug(Drug drug) {
        return drug1.equals(drug) || drug2.equals(drug);
    }

    /**
     * Get the other drug in interaction
     */
    public Drug getOtherDrug(Drug drug) {
        if (drug1.equals(drug)) {
            return drug2;
        } else if (drug2.equals(drug)) {
            return drug1;
        }
        return null;
    }
}
