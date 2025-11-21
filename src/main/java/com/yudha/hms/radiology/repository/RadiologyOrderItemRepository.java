package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.OrderStatus;
import com.yudha.hms.radiology.entity.RadiologyOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RadiologyOrderItem entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface RadiologyOrderItemRepository extends JpaRepository<RadiologyOrderItem, UUID> {

    /**
     * Find by order
     */
    List<RadiologyOrderItem> findByOrderId(UUID orderId);

    /**
     * Find by examination
     */
    List<RadiologyOrderItem> findByExaminationId(UUID examinationId);

    /**
     * Find by status
     */
    List<RadiologyOrderItem> findByStatus(OrderStatus status);

    /**
     * Find by result ID
     */
    Optional<RadiologyOrderItem> findByResultId(UUID resultId);

    /**
     * Find pending items for examination
     */
    @Query("SELECT oi FROM RadiologyOrderItem oi WHERE oi.examination.id = :examinationId AND oi.status = 'PENDING'")
    List<RadiologyOrderItem> findPendingItemsByExamination(@Param("examinationId") UUID examinationId);

    /**
     * Count items by status
     */
    long countByStatus(OrderStatus status);

    /**
     * Count items by examination
     */
    long countByExaminationId(UUID examinationId);
}
