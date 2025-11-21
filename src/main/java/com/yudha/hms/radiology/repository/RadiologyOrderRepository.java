package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.radiology.constant.OrderStatus;
import com.yudha.hms.radiology.constant.TransportationStatus;
import com.yudha.hms.radiology.entity.RadiologyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RadiologyOrder entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface RadiologyOrderRepository extends JpaRepository<RadiologyOrder, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<RadiologyOrder> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by order number
     */
    Optional<RadiologyOrder> findByOrderNumberAndDeletedAtIsNull(String orderNumber);

    /**
     * Find by patient
     */
    Page<RadiologyOrder> findByPatientIdAndDeletedAtIsNull(UUID patientId, Pageable pageable);

    /**
     * Find by encounter
     */
    List<RadiologyOrder> findByEncounterIdAndDeletedAtIsNull(UUID encounterId);

    /**
     * Find by status
     */
    Page<RadiologyOrder> findByStatusAndDeletedAtIsNull(OrderStatus status, Pageable pageable);

    /**
     * Find by priority
     */
    Page<RadiologyOrder> findByPriorityAndDeletedAtIsNull(OrderPriority priority, Pageable pageable);

    /**
     * Find by ordering doctor
     */
    Page<RadiologyOrder> findByOrderingDoctorIdAndDeletedAtIsNull(UUID orderingDoctorId, Pageable pageable);

    /**
     * Find by scheduled date
     */
    List<RadiologyOrder> findByScheduledDateAndDeletedAtIsNull(LocalDate scheduledDate);

    /**
     * Find by scheduled date and room
     */
    List<RadiologyOrder> findByScheduledDateAndRoomIdAndDeletedAtIsNull(LocalDate scheduledDate, UUID roomId);

    /**
     * Find by date range
     */
    @Query("SELECT o FROM RadiologyOrder o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.deletedAt IS NULL")
    Page<RadiologyOrder> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find pending orders
     */
    List<RadiologyOrder> findByStatusAndDeletedAtIsNull(OrderStatus status);

    /**
     * Find urgent/emergency orders
     */
    @Query("SELECT o FROM RadiologyOrder o WHERE o.priority IN ('URGENT', 'EMERGENCY') AND o.status NOT IN ('COMPLETED', 'CANCELLED') AND o.deletedAt IS NULL ORDER BY o.priority DESC, o.orderDate ASC")
    List<RadiologyOrder> findUrgentOrders();

    /**
     * Find orders by technician
     */
    List<RadiologyOrder> findByTechnicianIdAndStatusAndDeletedAtIsNull(UUID technicianId, OrderStatus status);

    /**
     * Search orders by order number or patient info
     */
    @Query("SELECT o FROM RadiologyOrder o WHERE (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.patient.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.patient.mrn) LIKE LOWER(CONCAT('%', :search, '%'))) AND o.deletedAt IS NULL")
    Page<RadiologyOrder> searchOrders(@Param("search") String search, Pageable pageable);

    /**
     * Count orders by status
     */
    long countByStatusAndDeletedAtIsNull(OrderStatus status);

    /**
     * Count orders by priority
     */
    long countByPriorityAndDeletedAtIsNull(OrderPriority priority);

    /**
     * Count orders by date range
     */
    @Query("SELECT COUNT(o) FROM RadiologyOrder o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.deletedAt IS NULL")
    long countByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ========== Phase 11.2: Patient Safety and Transportation ==========

    /**
     * Find orders requiring transportation with specific status
     */
    List<RadiologyOrder> findByRequiresTransportationTrueAndTransportationStatusAndDeletedAtIsNull(TransportationStatus status);

    /**
     * Find orders with contrast allergy
     */
    List<RadiologyOrder> findByHasContrastAllergyTrueAndDeletedAtIsNull();

    /**
     * Find orders with pregnancy concern
     */
    List<RadiologyOrder> findByIsPregnantTrueAndDeletedAtIsNull();
}
