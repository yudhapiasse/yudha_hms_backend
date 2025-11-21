package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for OrderStatusHistory entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, UUID> {

    /**
     * Find history by order
     */
    List<OrderStatusHistory> findByOrderIdOrderByStatusChangedAtAsc(UUID orderId);

    /**
     * Find latest status change for order
     */
    @Query("SELECT h FROM OrderStatusHistory h WHERE h.order.id = :orderId ORDER BY h.statusChangedAt DESC LIMIT 1")
    OrderStatusHistory findLatestStatusChange(@Param("orderId") UUID orderId);

    /**
     * Find status changes by new status
     */
    List<OrderStatusHistory> findByNewStatusOrderByStatusChangedAtDesc(String newStatus);

    /**
     * Find unnotified status changes
     */
    @Query("SELECT h FROM OrderStatusHistory h WHERE h.notificationSent = false ORDER BY h.statusChangedAt ASC")
    List<OrderStatusHistory> findUnnotifiedStatusChanges();

    /**
     * Find history by order - descending
     */
    List<OrderStatusHistory> findByOrderIdOrderByStatusChangedAtDesc(UUID orderId);

    /**
     * Find by notification sent false
     */
    List<OrderStatusHistory> findByNotificationSentFalseOrderByStatusChangedAtAsc();

    /**
     * Find by status changed between dates
     */
    List<OrderStatusHistory> findByStatusChangedAtBetweenOrderByStatusChangedAtDesc(LocalDateTime startDate, LocalDateTime endDate);
}
