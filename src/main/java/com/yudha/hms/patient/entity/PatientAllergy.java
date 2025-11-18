package com.yudha.hms.patient.entity;

import com.yudha.hms.shared.constant.AllergenType;
import com.yudha.hms.shared.constant.AllergySeverity;
import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Patient Allergy Entity.
 *
 * Stores patient allergy information for safety and clinical decision support.
 * Critical for preventing adverse drug reactions and allergic events.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "patient_allergy", schema = "patient_schema", indexes = {
    @Index(name = "idx_patient_allergy_patient", columnList = "patient_id"),
    @Index(name = "idx_patient_allergy_type", columnList = "allergen_type"),
    @Index(name = "idx_patient_allergy_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientAllergy extends AuditableEntity {

    /**
     * Patient reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    /**
     * Allergen type
     * DRUG, FOOD, ENVIRONMENTAL, etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "allergen_type", length = 50, nullable = false)
    @NotNull(message = "Allergen type is required")
    private AllergenType allergenType;

    /**
     * Allergen name
     * Specific name of the allergen
     * E.g., "Penicillin", "Peanuts", "Latex gloves"
     */
    @Column(name = "allergen_name", length = 200, nullable = false)
    @NotBlank(message = "Allergen name is required")
    @Size(max = 200, message = "Allergen name must not exceed 200 characters")
    private String allergenName;

    /**
     * Reaction description
     * Description of the allergic reaction
     * E.g., "Rash and itching", "Difficulty breathing", "Anaphylaxis"
     */
    @Column(name = "reaction", columnDefinition = "TEXT")
    private String reaction;

    /**
     * Severity
     * MILD, MODERATE, SEVERE, LIFE_THREATENING
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 20)
    private AllergySeverity severity;

    /**
     * Verified by
     * Name/ID of healthcare provider who verified the allergy
     */
    @Column(name = "verified_by", length = 100)
    private String verifiedBy;

    /**
     * Verification date
     * When the allergy was verified by a healthcare provider
     */
    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    /**
     * Notes
     * Additional information about the allergy
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Active status
     * True if allergy is still active, false if patient has outgrown it
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Check if allergy is verified
     */
    @Transient
    public boolean isVerified() {
        return verifiedBy != null && verifiedDate != null;
    }

    /**
     * Check if allergy is severe or life-threatening
     */
    @Transient
    public boolean isCritical() {
        return severity == AllergySeverity.SEVERE ||
               severity == AllergySeverity.LIFE_THREATENING;
    }
}