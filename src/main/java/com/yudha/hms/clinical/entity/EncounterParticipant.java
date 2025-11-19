package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Participant Entity.
 *
 * Represents practitioners participating in an encounter (care team).
 * Many-to-many relationship between encounters and practitioners.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "encounter_participants", schema = "clinical_schema",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_encounter_participant",
            columnNames = {"encounter_id", "practitioner_id", "participant_type"})
    },
    indexes = {
        @Index(name = "idx_encounter_participants_encounter", columnList = "encounter_id"),
        @Index(name = "idx_encounter_participants_practitioner", columnList = "practitioner_id"),
        @Index(name = "idx_encounter_participants_type", columnList = "participant_type")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Practitioners participating in an encounter (care team)")
public class EncounterParticipant extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Relationships ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    @Column(name = "practitioner_id", nullable = false)
    @NotNull(message = "Practitioner ID is required")
    private UUID practitionerId;

    // ========== Participant Details ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "participant_type", nullable = false, length = 30)
    @NotNull(message = "Participant type is required")
    private ParticipantType participantType; // PRIMARY, SECONDARY, CONSULTANT, ANESTHESIOLOGIST

    @Column(name = "participant_name", length = 200)
    private String participantName;

    @Column(name = "participant_role", length = 100)
    private String participantRole; // Additional role description

    // ========== Period of Participation ==========
    @Column(name = "period_start", nullable = false)
    @NotNull(message = "Period start is required")
    @Builder.Default
    private LocalDateTime periodStart = LocalDateTime.now();

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    // ========== Additional Information ==========
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Business Methods ==========

    /**
     * End the participation period.
     */
    public void endParticipation() {
        this.periodEnd = LocalDateTime.now();
    }

    /**
     * Check if participation is currently active.
     */
    public boolean isActive() {
        return periodEnd == null || periodEnd.isAfter(LocalDateTime.now());
    }

    /**
     * Check if this participant is the primary care provider.
     */
    public boolean isPrimary() {
        return participantType == ParticipantType.PRIMARY;
    }
}
