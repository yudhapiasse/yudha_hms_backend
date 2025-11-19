package com.yudha.hms.registration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Queue Sequence Entity.
 *
 * Tracks queue number generation per polyclinic per day.
 * Ensures unique sequential queue numbers with daily reset.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "queue_sequence", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_queue_sequence_polyclinic", columnList = "polyclinic_id"),
        @Index(name = "idx_queue_sequence_date", columnList = "queue_date")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_queue_sequence", columnNames = {"polyclinic_id", "queue_date"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Tracks queue numbers per polyclinic per day")
public class QueueSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Polyclinic Reference ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polyclinic_id", nullable = false)
    @NotNull(message = "Polyclinic is required")
    private Polyclinic polyclinic;

    // ========== Queue Date ==========
    @Column(name = "queue_date", nullable = false)
    @NotNull(message = "Queue date is required")
    private LocalDate queueDate;

    // ========== Sequence Counter ==========
    @Column(name = "last_queue_number")
    @Builder.Default
    private Integer lastQueueNumber = 0;

    // ========== Queue Prefix ==========
    @Column(name = "prefix", length = 5)
    private String prefix; // e.g., "A", "B", "UM" (Umum), "AN" (Anak)

    // ========== Audit Fields ==========
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ========== Business Methods ==========

    /**
     * Get next queue number and increment counter.
     */
    public synchronized String getNextQueueNumber() {
        this.lastQueueNumber++;
        this.updatedAt = LocalDateTime.now();
        return formatQueueCode(this.lastQueueNumber);
    }

    /**
     * Format queue code with prefix and number.
     */
    public String formatQueueCode(int number) {
        String queuePrefix = (prefix != null && !prefix.isEmpty()) ? prefix : "Q";
        return String.format("%s%03d", queuePrefix, number);
    }

    /**
     * Get current queue code without incrementing.
     */
    public String getCurrentQueueCode() {
        return formatQueueCode(this.lastQueueNumber);
    }

    /**
     * Reset queue counter (for testing or manual reset).
     */
    public void reset() {
        this.lastQueueNumber = 0;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if this sequence is for today.
     */
    public boolean isForToday() {
        return queueDate.equals(LocalDate.now());
    }
}