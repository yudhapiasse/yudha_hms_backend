package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.constant.OrderStatus;
import com.yudha.hms.laboratory.entity.LabOrder;
import com.yudha.hms.laboratory.entity.OrderStatusHistory;
import com.yudha.hms.laboratory.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Order Status History operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderStatusHistoryService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    /**
     * Record status change
     */
    public OrderStatusHistory recordStatusChange(LabOrder order, OrderStatus previousStatus, OrderStatus newStatus,
                                                   String changedBy, String reason) {
        log.info("Recording status change for order {}: {} -> {}", order.getOrderNumber(), previousStatus, newStatus);

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .previousStatus(previousStatus != null ? previousStatus.name() : null)
                .newStatus(newStatus.name())
                .statusChangedAt(LocalDateTime.now())
                .changedBy(changedBy)
                .changeReason(reason)
                .notificationSent(false)
                .build();

        OrderStatusHistory saved = orderStatusHistoryRepository.save(history);
        log.info("Status change recorded with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Mark notification as sent
     */
    public void markNotificationSent(UUID historyId, List<String> recipients) {
        log.info("Marking notification as sent for history: {}", historyId);

        OrderStatusHistory history = orderStatusHistoryRepository.findById(historyId)
                .orElseThrow(() -> new IllegalArgumentException("Status history not found: " + historyId));

        history.markNotificationSent();
        history.setNotificationRecipients(recipients);
        orderStatusHistoryRepository.save(history);

        log.info("Notification marked as sent");
    }

    /**
     * Get status history for order
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getOrderStatusHistory(UUID orderId) {
        return orderStatusHistoryRepository.findByOrderIdOrderByStatusChangedAtDesc(orderId);
    }

    /**
     * Get latest status for order
     */
    @Transactional(readOnly = true)
    public OrderStatusHistory getLatestStatus(UUID orderId) {
        List<OrderStatusHistory> history = getOrderStatusHistory(orderId);
        if (history.isEmpty()) {
            throw new IllegalStateException("No status history found for order: " + orderId);
        }
        return history.get(0);
    }

    /**
     * Get pending notifications
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getPendingNotifications() {
        return orderStatusHistoryRepository.findByNotificationSentFalseOrderByStatusChangedAtAsc();
    }

    /**
     * Get status changes by date range
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getStatusChangesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderStatusHistoryRepository.findByStatusChangedAtBetweenOrderByStatusChangedAtDesc(startDate, endDate);
    }

    /**
     * Get status changes by status
     */
    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getStatusChangesByStatus(String status) {
        return orderStatusHistoryRepository.findByNewStatusOrderByStatusChangedAtDesc(status);
    }
}
