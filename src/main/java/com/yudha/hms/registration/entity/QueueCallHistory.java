package com.yudha.hms.registration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Queue Call History Entity.
 * Tracks all queue calls for audit and analytics purposes.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "queue_call_history", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_queue_call_history_registration", columnList = "outpatient_registration_id"),
        @Index(name = "idx_queue_call_history_called_at", columnList = "called_at"),
        @Index(name = "idx_queue_call_history_polyclinic", columnList = "polyclinic_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("History of all queue calls for audit and analytics")
public class QueueCallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outpatient_registration_id", nullable = false)
    @NotNull(message = "Outpatient registration is required")
    private OutpatientRegistration outpatientRegistration;

    @Column(name = "queue_number", nullable = false)
    @NotNull(message = "Queue number is required")
    private Integer queueNumber;

    @Column(name = "queue_code", nullable = false, length = 20)
    @NotNull(message = "Queue code is required")
    private String queueCode;

    @Column(name = "called_at", nullable = false)
    @NotNull(message = "Called at time is required")
    @Builder.Default
    private LocalDateTime calledAt = LocalDateTime.now();

    @Column(name = "called_by_id")
    private UUID calledById;

    @Column(name = "called_by_name", nullable = false, length = 100)
    @NotNull(message = "Called by name is required")
    private String calledByName;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false, length = 20)
    @NotNull(message = "Call type is required")
    @Builder.Default
    private QueueCallType callType = QueueCallType.NORMAL;

    @Column(name = "polyclinic_id", nullable = false)
    @NotNull(message = "Polyclinic ID is required")
    private UUID polyclinicId;

    @Column(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    @Column(name = "consultation_room", length = 100)
    private String consultationRoom;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_status", length = 20)
    private QueueResponseStatus responseStatus;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Audit ==========
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // ========== Business Methods ==========

    /**
     * Mark as responded.
     */
    public void markAsResponded() {
        this.responseStatus = QueueResponseStatus.RESPONDED;
        this.respondedAt = LocalDateTime.now();
    }

    /**
     * Mark as no response.
     */
    public void markAsNoResponse() {
        this.responseStatus = QueueResponseStatus.NO_RESPONSE;
    }

    /**
     * Mark as skipped.
     */
    public void markAsSkipped() {
        this.responseStatus = QueueResponseStatus.SKIPPED;
    }

    /**
     * Check if patient responded.
     */
    public boolean hasResponded() {
        return responseStatus == QueueResponseStatus.RESPONDED;
    }

    /**
     * Get response time in seconds.
     */
    public Long getResponseTimeSeconds() {
        if (respondedAt != null && calledAt != null) {
            return java.time.Duration.between(calledAt, respondedAt).getSeconds();
        }
        return null;
    }
}
