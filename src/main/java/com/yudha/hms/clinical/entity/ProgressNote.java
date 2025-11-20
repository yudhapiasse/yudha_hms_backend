package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Progress Note Entity (SOAP Notes).
 *
 * Manages daily progress notes and clinical documentation for inpatient care.
 * Supports SOAP format (Subjective, Objective, Assessment, Plan).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "progress_note", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_progress_note_number", columnList = "note_number"),
        @Index(name = "idx_progress_note_encounter", columnList = "encounter_id"),
        @Index(name = "idx_progress_note_patient", columnList = "patient_id"),
        @Index(name = "idx_progress_note_date", columnList = "note_date_time"),
        @Index(name = "idx_progress_note_type", columnList = "note_type"),
        @Index(name = "idx_progress_note_provider", columnList = "provider_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("SOAP notes and progress documentation for inpatient care")
public class ProgressNote extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "note_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Note number is required")
    private String noteNumber; // PN-20251120-0001

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Note Metadata ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "note_type", nullable = false, length = 30)
    @NotNull(message = "Note type is required")
    private NoteType noteType; // SOAP, SHIFT_HANDOVER, CRITICAL_CARE, NURSING, PROCEDURE

    @Column(name = "note_date_time", nullable = false)
    @NotNull(message = "Note date/time is required")
    @Builder.Default
    private LocalDateTime noteDateTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "shift", length = 20)
    private Shift shift; // MORNING, AFTERNOON, NIGHT

    // ========== SOAP Format ==========
    @Column(name = "subjective", columnDefinition = "TEXT")
    private String subjective; // Patient complaints, symptoms

    @Column(name = "objective", columnDefinition = "TEXT")
    private String objective; // Vital signs, physical exam findings

    @Column(name = "assessment", columnDefinition = "TEXT")
    private String assessment; // Clinical impression, diagnosis

    @Column(name = "plan", columnDefinition = "TEXT")
    private String plan; // Treatment plan, interventions

    // ========== Additional Information ==========
    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_instructions", columnDefinition = "TEXT")
    private String followUpInstructions;

    @Column(name = "critical_findings", columnDefinition = "TEXT")
    private String criticalFindings;

    // ========== Provider Information ==========
    @Column(name = "provider_id", nullable = false)
    @NotNull(message = "Provider ID is required")
    private UUID providerId;

    @Column(name = "provider_name", nullable = false, length = 200)
    @NotBlank(message = "Provider name is required")
    private String providerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", length = 30)
    private ProviderType providerType; // DOCTOR, NURSE, SPECIALIST, RESIDENT

    @Column(name = "provider_specialty", length = 100)
    private String providerSpecialty;

    // ========== Cosign/Supervision ==========
    @Column(name = "requires_cosign")
    @Builder.Default
    private Boolean requiresCosign = false;

    @Column(name = "cosigned")
    @Builder.Default
    private Boolean cosigned = false;

    @Column(name = "cosigned_by_id")
    private UUID cosignedById;

    @Column(name = "cosigned_by_name", length = 200)
    private String cosignedByName;

    @Column(name = "cosigned_at")
    private LocalDateTime cosignedAt;

    // ========== Business Methods ==========

    /**
     * Check if note is complete (has all SOAP components).
     */
    public boolean isComplete() {
        return subjective != null && !subjective.isEmpty() &&
               objective != null && !objective.isEmpty() &&
               assessment != null && !assessment.isEmpty() &&
               plan != null && !plan.isEmpty();
    }

    /**
     * Check if note needs cosigning.
     */
    public boolean needsCosign() {
        return Boolean.TRUE.equals(requiresCosign) && !Boolean.TRUE.equals(cosigned);
    }

    /**
     * Cosign the note.
     */
    public void cosign(UUID doctorId, String doctorName) {
        this.cosigned = true;
        this.cosignedById = doctorId;
        this.cosignedByName = doctorName;
        this.cosignedAt = LocalDateTime.now();
    }

    /**
     * Check if note has critical findings.
     */
    public boolean hasCriticalFindings() {
        return criticalFindings != null && !criticalFindings.isEmpty();
    }

    /**
     * Check if note is from current shift.
     */
    public boolean isFromCurrentShift() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();

        if (shift == null) {
            return false;
        }

        return switch (shift) {
            case MORNING -> hour >= 7 && hour < 15;
            case AFTERNOON -> hour >= 15 && hour < 23;
            case NIGHT -> hour >= 23 || hour < 7;
        };
    }
}
