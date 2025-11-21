package com.yudha.hms.laboratory.repository;

import com.yudha.hms.laboratory.constant.OrderPriority;
import com.yudha.hms.laboratory.constant.OrderStatus;
import com.yudha.hms.laboratory.entity.LabOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LabOrder entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<LabOrder> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by order number
     */
    Optional<LabOrder> findByOrderNumberAndDeletedAtIsNull(String orderNumber);

    /**
     * Find by patient (paginated)
     */
    Page<LabOrder> findByPatientIdAndDeletedAtIsNullOrderByOrderDateDesc(UUID patientId, Pageable pageable);

    /**
     * Find by patient (list)
     */
    List<LabOrder> findByPatientIdAndDeletedAtIsNullOrderByOrderDateDesc(UUID patientId);

    /**
     * Find by encounter
     */
    List<LabOrder> findByEncounterIdAndDeletedAtIsNullOrderByOrderDateDesc(UUID encounterId);

    /**
     * Find by status
     */
    Page<LabOrder> findByStatusAndDeletedAtIsNullOrderByOrderDateDesc(OrderStatus status, Pageable pageable);

    /**
     * Find by priority
     */
    Page<LabOrder> findByPriorityAndDeletedAtIsNullOrderByOrderDateDesc(OrderPriority priority, Pageable pageable);

    /**
     * Find pending orders
     */
    @Query("SELECT o FROM LabOrder o WHERE o.status IN ('PENDING', 'SCHEDULED') AND o.deletedAt IS NULL ORDER BY o.priority DESC, o.orderDate ASC")
    Page<LabOrder> findPendingOrders(Pageable pageable);

    /**
     * Find urgent orders
     */
    @Query("SELECT o FROM LabOrder o WHERE o.priority IN ('URGENT', 'CITO') AND o.status NOT IN ('COMPLETED', 'CANCELLED') AND o.deletedAt IS NULL ORDER BY o.orderDate ASC")
    List<LabOrder> findUrgentOrders();

    /**
     * Find orders by date range (paginated)
     */
    @Query("SELECT o FROM LabOrder o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.deletedAt IS NULL ORDER BY o.orderDate DESC")
    Page<LabOrder> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find orders by date range (list)
     */
    List<LabOrder> findByOrderDateBetweenAndDeletedAtIsNullOrderByOrderDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recurring orders
     */
    List<LabOrder> findByIsRecurringAndDeletedAtIsNull(Boolean isRecurring);

    /**
     * Find orders by ordering doctor
     */
    Page<LabOrder> findByOrderingDoctorIdAndDeletedAtIsNullOrderByOrderDateDesc(UUID doctorId, Pageable pageable);

    /**
     * Count orders by status
     */
    long countByStatusAndDeletedAtIsNull(OrderStatus status);

    /**
     * Count orders by patient and status
     */
    long countByPatientIdAndStatusAndDeletedAtIsNull(UUID patientId, OrderStatus status);

    /**
     * Count orders by order number prefix
     */
    long countByOrderNumberStartingWith(String prefix);

    /**
     * Search orders by order number
     */
    @Query("SELECT o FROM LabOrder o WHERE (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%'))) AND o.deletedAt IS NULL")
    Page<LabOrder> searchOrders(@Param("search") String search, Pageable pageable);
}
