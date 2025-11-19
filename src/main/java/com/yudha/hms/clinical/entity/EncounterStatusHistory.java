package com.yudha.hms.clinical.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Status History Entity.
 *
 * Audit trail of encounter status changes.
 * Tracks who changed the status, when, and why.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "encounter_status_history", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_status_history_encounter", columnList = "encounter_id"),
        @Index(name = "idx_status_history_date", columnList = "status_changed_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Audit trail of encounter status changes")
public class EncounterStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Relationship ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_id", nullable = false)
    @NotNull(message = "Encounter is required")
    private Encounter encounter;

    // ========== Status Change Details ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private EncounterStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    @NotNull(message = "To status is required")
    private EncounterStatus toStatus;

    // ========== Timing ==========
    @Column(name = "status_changed_at", nullable = false)
    @NotNull(message = "Status changed at is required")
    @Builder.Default
    private LocalDateTime statusChangedAt = LocalDateTime.now();

    // ========== User Information ==========
    @Column(name = "changed_by_id")
    private UUID changedById;

    @Column(name = "changed_by_name", length = 200)
    private String changedByName;

    // ========== Reason and Notes ==========
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Audit Fields ==========
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // ========== Business Methods ==========

    /**
     * Get the status transition description.
     */
    public String getTransitionDescription() {
        if (fromStatus == null) {
            return "Created with status: " + toStatus.getDisplayName();
        }
        return fromStatus.getDisplayName() + " → " + toStatus.getDisplayName();
    }

    /**
     * Get the status transition description in Indonesian.
     */
    public String getTransitionDescriptionIndonesian() {
        if (fromStatus == null) {
            return "Dibuat dengan status: " + toStatus.getIndonesianName();
        }
        return fromStatus.getIndonesianName() + " → " + toStatus.getIndonesianName();
    }
}
