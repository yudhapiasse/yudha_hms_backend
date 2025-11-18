package com.yudha.hms.patient.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Emergency Contact Entity.
 *
 * Stores emergency contact information for patients.
 * Multiple contacts can be stored with priority ordering.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "emergency_contact", schema = "patient_schema", indexes = {
    @Index(name = "idx_emergency_contact_patient", columnList = "patient_id"),
    @Index(name = "idx_emergency_contact_priority", columnList = "priority")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContact extends AuditableEntity {

    /**
     * Patient reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    private Patient patient;

    /**
     * Full name of emergency contact
     */
    @Column(name = "full_name", length = 200, nullable = false)
    @NotBlank(message = "Contact name is required")
    @Size(max = 200, message = "Contact name must not exceed 200 characters")
    private String fullName;

    /**
     * Relationship to patient
     * E.g., PARENT, SPOUSE, SIBLING, CHILD, FRIEND, OTHER
     */
    @Column(name = "relationship", length = 50, nullable = false)
    @NotBlank(message = "Relationship is required")
    private String relationship;

    /**
     * Primary phone number
     * Required for emergency contact
     */
    @Column(name = "phone_primary", length = 20, nullable = false)
    @NotBlank(message = "Primary phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phonePrimary;

    /**
     * Secondary phone number
     * Optional alternative contact number
     */
    @Column(name = "phone_secondary", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneSecondary;

    /**
     * Email address
     */
    @Column(name = "email", length = 100)
    @Email(message = "Invalid email format")
    private String email;

    /**
     * Address
     * Full address of emergency contact
     */
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /**
     * Priority
     * 1 = Primary contact (should be called first)
     * 2 = Secondary contact
     * 3 = Tertiary contact
     * etc.
     */
    @Column(name = "priority")
    @Builder.Default
    @Min(value = 1, message = "Priority must be at least 1")
    private Integer priority = 1;

    /**
     * Notes
     * Additional information about the emergency contact
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Check if this is the primary contact
     */
    @Transient
    public boolean isPrimaryContact() {
        return priority != null && priority == 1;
    }
}