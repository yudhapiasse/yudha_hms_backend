package com.yudha.hms.laboratory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Order Status History Response DTO.
 *
 * Response for order status change history.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryResponse {

    /**
     * History ID
     */
    private UUID id;

    /**
     * Order ID
     */
    private UUID orderId;

    /**
     * Order number
     */
    private String orderNumber;

    // ========== Status Change ==========

    /**
     * Previous status
     */
    private String previousStatus;

    /**
     * New status
     */
    private String newStatus;

    /**
     * Status changed at
     */
    private LocalDateTime statusChangedAt;

    /**
     * Changed by user ID
     */
    private String changedBy;

    /**
     * Changed by name
     */
    private String changedByName;

    /**
     * Change reason
     */
    private String changeReason;

    // ========== Notification ==========

    /**
     * Notification sent
     */
    private Boolean notificationSent;

    /**
     * Notification sent at
     */
    private LocalDateTime notificationSentAt;

    /**
     * Notification recipients
     */
    private List<String> notificationRecipients;

    /**
     * Created at
     */
    private LocalDateTime createdAt;
}
