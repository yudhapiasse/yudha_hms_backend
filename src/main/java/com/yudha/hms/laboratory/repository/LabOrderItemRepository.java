package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.entity.LabOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for LabOrderItem entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabOrderItemRepository extends JpaRepository<LabOrderItem, UUID> {

    /**
     * Find items by order
     */
    List<LabOrderItem> findByOrderId(UUID orderId);

    /**
     * Find items by order ordered by ID
     */
    List<LabOrderItem> findByOrderIdOrderByIdAsc(UUID orderId);

    /**
     * Find items by test
     */
    List<LabOrderItem> findByTestId(UUID testId);

    /**
     * Find items by panel
     */
    List<LabOrderItem> findByPanelId(UUID panelId);

    /**
     * Find items by status
     */
    @Query("SELECT i FROM LabOrderItem i WHERE i.status = :status AND i.order.deletedAt IS NULL")
    List<LabOrderItem> findByStatus(@Param("status") String status);

    /**
     * Find items without specimen
     */
    @Query("SELECT i FROM LabOrderItem i WHERE i.specimenId IS NULL AND i.order.status IN ('PENDING', 'SCHEDULED') AND i.order.deletedAt IS NULL")
    List<LabOrderItem> findItemsAwaitingSpecimen();

    /**
     * Find items without result
     */
    @Query("SELECT i FROM LabOrderItem i WHERE i.resultId IS NULL AND i.specimenId IS NOT NULL AND i.order.deletedAt IS NULL")
    List<LabOrderItem> findItemsAwaitingResult();

    /**
     * Count items by order
     */
    long countByOrderId(UUID orderId);
}
