package com.yudha.hms.laboratory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Order Status History Entity.
 *
 * Tracks laboratory order status changes and notifications.
 * Maintains audit trail of all status transitions with notification tracking.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "order_status_history", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_order_status_history_order", columnList = "order_id"),
        @Index(name = "idx_order_status_history_status", columnList = "new_status"),
        @Index(name = "idx_order_status_history_changed_at", columnList = "status_changed_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistory {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Order reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder order;

    // ========== Status Change ==========

    /**
     * Previous status
     */
    @Column(name = "previous_status", length = 50)
    private String previousStatus;

    /**
     * New status
     */
    @Column(name = "new_status", nullable = false, length = 50)
    private String newStatus;

    /**
     * Status changed timestamp
     */
    @Column(name = "status_changed_at", nullable = false)
    @Builder.Default
    private LocalDateTime statusChangedAt = LocalDateTime.now();

    /**
     * Changed by user ID
     */
    @Column(name = "changed_by", length = 100)
    private String changedBy;

    /**
     * Change reason
     */
    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    // ========== Notification ==========

    /**
     * Notification sent
     */
    @Column(name = "notification_sent")
    @Builder.Default
    private Boolean notificationSent = false;

    /**
     * Notification sent timestamp
     */
    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;

    /**
     * Notification recipients (emails, user IDs, etc.)
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "notification_recipients")
    private List<String> notificationRecipients;

    // ========== Audit Fields ==========

    /**
     * Created timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Version for optimistic locking
     */
    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ========== Helper Methods ==========

    /**
     * Check if notification has been sent
     */
    public boolean isNotificationSent() {
        return Boolean.TRUE.equals(notificationSent);
    }

    /**
     * Mark notification as sent
     */
    public void markNotificationSent() {
        this.notificationSent = true;
        this.notificationSentAt = LocalDateTime.now();
    }
}
