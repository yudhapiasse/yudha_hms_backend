package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.radiology.constant.OrderStatus;
import com.yudha.hms.radiology.entity.*;
import com.yudha.hms.radiology.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for Radiology Order operations.
 *
 * Handles CRUD operations and business logic for radiology orders.
 * Most complex service with auto-numbering, scheduling, and workflow management.
 *
 * Features:
 * - Create order with auto-numbering (RO + YYYYMMDD + 6-digit sequence)
 * - Add examination items to order
 * - Schedule order (assign room, date, time)
 * - Update order status workflow (PENDING → SCHEDULED → IN_PROGRESS → COMPLETED)
 * - Cancel order with reason
 * - Get pending, urgent/emergency orders
 * - Get orders by patient
 * - Get scheduled orders for room/date
 * - Calculate total cost
 * - Validation: valid examinations, room availability, scheduling conflicts
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RadiologyOrderService {

    private final RadiologyOrderRepository orderRepository;
    private final RadiologyOrderItemRepository orderItemRepository;
    private final RadiologyExaminationRepository examinationRepository;
    private final RadiologyRoomRepository roomRepository;

    /**
     * Create a new radiology order with examination items.
     *
     * @param order Order to create
     * @param examinationIds List of examination IDs
     * @return Created order
     * @throws IllegalArgumentException if examinations not found
     */
    public RadiologyOrder createOrder(RadiologyOrder order, List<UUID> examinationIds) {
        log.info("Creating new radiology order for patient: {}", order.getPatient().getId());

        // Generate order number
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);

        // Set initial status
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // Set order date if not provided
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }

        // Set default priority if not provided
        if (order.getPriority() == null) {
            order.setPriority(OrderPriority.ROUTINE);
        }

        // Save order first
        RadiologyOrder saved = orderRepository.save(order);

        // Create order items for examinations
        if (examinationIds != null && !examinationIds.isEmpty()) {
            for (UUID examinationId : examinationIds) {
                RadiologyExamination examination = examinationRepository.findByIdAndDeletedAtIsNull(examinationId)
                        .orElseThrow(() -> new IllegalArgumentException("Examination not found: " + examinationId));
                createOrderItem(saved, examination);
            }
        }

        log.info("Order created successfully: {}", saved.getOrderNumber());
        return saved;
    }

    /**
     * Add an examination item to an existing order.
     *
     * @param orderId Order ID
     * @param examinationId Examination ID
     * @return Created order item
     * @throws IllegalArgumentException if order or examination not found
     */
    public RadiologyOrderItem addExaminationToOrder(UUID orderId, UUID examinationId) {
        log.info("Adding examination {} to order {}", examinationId, orderId);

        RadiologyOrder order = getOrderById(orderId);
        RadiologyExamination examination = examinationRepository.findByIdAndDeletedAtIsNull(examinationId)
                .orElseThrow(() -> new IllegalArgumentException("Examination not found: " + examinationId));

        // Check if order can be modified
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to order in status: " + order.getStatus());
        }

        RadiologyOrderItem item = createOrderItem(order, examination);
        log.info("Examination added to order successfully");
        return item;
    }

    /**
     * Schedule an order (assign room, date, and time).
     *
     * @param orderId Order ID
     * @param roomId Room ID
     * @param scheduledDate Scheduled date
     * @param scheduledTime Scheduled time
     * @return Updated order
     * @throws IllegalArgumentException if order or room not found
     * @throws IllegalStateException if order cannot be scheduled or room not available
     */
    public RadiologyOrder scheduleOrder(UUID orderId, UUID roomId, LocalDate scheduledDate, LocalTime scheduledTime) {
        log.info("Scheduling order {} for room {} on {} at {}", orderId, roomId, scheduledDate, scheduledTime);

        RadiologyOrder order = getOrderById(orderId);

        // Validate order status
        if (!order.getStatus().canBeScheduled()) {
            throw new IllegalStateException("Order cannot be scheduled in status: " + order.getStatus());
        }

        // Validate room exists
        RadiologyRoom room = roomRepository.findByIdAndDeletedAtIsNull(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        // Check room is operational and available
        if (!room.getIsOperational() || !room.getIsAvailable()) {
            throw new IllegalStateException("Room is not available for scheduling: " + room.getRoomCode());
        }

        // Check for scheduling conflicts (basic check - can be enhanced)
        List<RadiologyOrder> existingOrders = orderRepository.findByScheduledDateAndRoomIdAndDeletedAtIsNull(scheduledDate, roomId);
        for (RadiologyOrder existing : existingOrders) {
            if (existing.getScheduledTime() != null && scheduledTime != null) {
                // Check if times are too close (within 30 minutes)
                long minutesDiff = Math.abs(java.time.Duration.between(existing.getScheduledTime(), scheduledTime).toMinutes());
                if (minutesDiff < 30) {
                    throw new IllegalStateException("Scheduling conflict: Room is already booked at " +
                            existing.getScheduledTime() + " (too close to requested time)");
                }
            }
        }

        // Update order
        order.setRoom(room);
        order.setScheduledDate(scheduledDate);
        order.setScheduledTime(scheduledTime);
        order.setStatus(OrderStatus.SCHEDULED);

        RadiologyOrder updated = orderRepository.save(order);
        log.info("Order scheduled successfully: {}", orderId);
        return updated;
    }

    /**
     * Update order status.
     *
     * @param orderId Order ID
     * @param newStatus New status
     * @param changedBy User ID who changed status
     * @param notes Optional notes
     * @return Updated order
     * @throws IllegalArgumentException if order not found
     * @throws IllegalStateException if status transition is invalid
     */
    public RadiologyOrder updateOrderStatus(UUID orderId, OrderStatus newStatus, UUID changedBy, String notes) {
        log.info("Updating order status: {} to {}", orderId, newStatus);

        RadiologyOrder order = getOrderById(orderId);
        OrderStatus previousStatus = order.getStatus();

        // Validate status transition
        validateStatusTransition(previousStatus, newStatus);

        // Update status
        order.setStatus(newStatus);

        // Add notes if provided
        if (notes != null && !notes.isEmpty()) {
            String existingNotes = order.getNotes();
            String updatedNotes = existingNotes != null
                    ? existingNotes + "\n" + LocalDateTime.now() + " [" + previousStatus + " → " + newStatus + "]: " + notes
                    : LocalDateTime.now() + " [" + previousStatus + " → " + newStatus + "]: " + notes;
            order.setNotes(updatedNotes);
        }

        RadiologyOrder updated = orderRepository.save(order);
        log.info("Order status updated successfully: {} → {}", previousStatus, newStatus);
        return updated;
    }

    /**
     * Cancel an order.
     *
     * @param orderId Order ID
     * @param cancelledBy User ID who cancelled
     * @param reason Cancellation reason
     * @return Updated order
     * @throws IllegalArgumentException if order not found
     * @throws IllegalStateException if order cannot be cancelled
     */
    public RadiologyOrder cancelOrder(UUID orderId, UUID cancelledBy, String reason) {
        log.info("Cancelling order: {} - Reason: {}", orderId, reason);

        RadiologyOrder order = getOrderById(orderId);

        // Validate order can be cancelled
        if (!order.getStatus().canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled in status: " + order.getStatus());
        }

        // Update order
        order.setStatus(OrderStatus.CANCELLED);
        String noteText = "Order cancelled by user " + cancelledBy + ": " + reason;
        String existingNotes = order.getNotes();
        order.setNotes(existingNotes != null ? existingNotes + "\n" + LocalDateTime.now() + " - " + noteText : noteText);

        // Update all order items to cancelled
        List<RadiologyOrderItem> items = orderItemRepository.findByOrderId(orderId);
        for (RadiologyOrderItem item : items) {
            item.setStatus(OrderStatus.CANCELLED);
            orderItemRepository.save(item);
        }

        RadiologyOrder updated = orderRepository.save(order);
        log.info("Order cancelled successfully: {}", orderId);
        return updated;
    }

    /**
     * Get order by ID.
     *
     * @param id Order ID
     * @return Order
     * @throws IllegalArgumentException if order not found
     */
    @Transactional(readOnly = true)
    public RadiologyOrder getOrderById(UUID id) {
        return orderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
    }

    /**
     * Get order by number.
     *
     * @param orderNumber Order number
     * @return Order
     * @throws IllegalArgumentException if order not found
     */
    @Transactional(readOnly = true)
    public RadiologyOrder getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumberAndDeletedAtIsNull(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));
    }

    /**
     * Get orders by patient.
     *
     * @param patientId Patient ID
     * @param pageable Pagination parameters
     * @return Page of orders
     */
    @Transactional(readOnly = true)
    public Page<RadiologyOrder> getOrdersByPatient(UUID patientId, Pageable pageable) {
        return orderRepository.findByPatientIdAndDeletedAtIsNull(patientId, pageable);
    }

    /**
     * Get orders by encounter.
     *
     * @param encounterId Encounter ID
     * @return List of orders
     */
    @Transactional(readOnly = true)
    public List<RadiologyOrder> getOrdersByEncounter(UUID encounterId) {
        return orderRepository.findByEncounterIdAndDeletedAtIsNull(encounterId);
    }

    /**
     * Get orders by status.
     *
     * @param status Order status
     * @param pageable Pagination parameters
     * @return Page of orders
     */
    @Transactional(readOnly = true)
    public Page<RadiologyOrder> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusAndDeletedAtIsNull(status, pageable);
    }

    /**
     * Get pending orders.
     *
     * @return List of pending orders
     */
    @Transactional(readOnly = true)
    public List<RadiologyOrder> getPendingOrders() {
        return orderRepository.findByStatusAndDeletedAtIsNull(OrderStatus.PENDING);
    }

    /**
     * Get urgent/emergency orders.
     *
     * @return List of urgent orders
     */
    @Transactional(readOnly = true)
    public List<RadiologyOrder> getUrgentOrders() {
        return orderRepository.findUrgentOrders();
    }

    /**
     * Get scheduled orders for a room and date.
     *
     * @param roomId Room ID
     * @param date Target date
     * @return List of scheduled orders
     */
    @Transactional(readOnly = true)
    public List<RadiologyOrder> getScheduledOrdersForRoom(UUID roomId, LocalDate date) {
        return orderRepository.findByScheduledDateAndRoomIdAndDeletedAtIsNull(date, roomId);
    }

    /**
     * Get scheduled orders for a date.
     *
     * @param date Target date
     * @return List of scheduled orders
     */
    @Transactional(readOnly = true)
    public List<RadiologyOrder> getScheduledOrdersForDate(LocalDate date) {
        return orderRepository.findByScheduledDateAndDeletedAtIsNull(date);
    }

    /**
     * Get orders by ordering doctor.
     *
     * @param doctorId Doctor ID
     * @param pageable Pagination parameters
     * @return Page of orders
     */
    @Transactional(readOnly = true)
    public Page<RadiologyOrder> getOrdersByDoctor(UUID doctorId, Pageable pageable) {
        return orderRepository.findByOrderingDoctorIdAndDeletedAtIsNull(doctorId, pageable);
    }

    /**
     * Get orders by priority.
     *
     * @param priority Order priority
     * @param pageable Pagination parameters
     * @return Page of orders
     */
    @Transactional(readOnly = true)
    public Page<RadiologyOrder> getOrdersByPriority(OrderPriority priority, Pageable pageable) {
        return orderRepository.findByPriorityAndDeletedAtIsNull(priority, pageable);
    }

    /**
     * Get orders by date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of orders
     */
    @Transactional(readOnly = true)
    public Page<RadiologyOrder> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Search orders.
     *
     * @param search Search term
     * @param pageable Pagination parameters
     * @return Page of matching orders
     */
    @Transactional(readOnly = true)
    public Page<RadiologyOrder> searchOrders(String search, Pageable pageable) {
        return orderRepository.searchOrders(search, pageable);
    }

    /**
     * Get order items.
     *
     * @param orderId Order ID
     * @return List of order items
     */
    @Transactional(readOnly = true)
    public List<RadiologyOrderItem> getOrderItems(UUID orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    /**
     * Calculate order total cost.
     *
     * @param orderId Order ID
     * @return Total cost
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderTotal(UUID orderId) {
        List<RadiologyOrderItem> items = getOrderItems(orderId);
        return items.stream()
                .map(RadiologyOrderItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Count orders by status.
     *
     * @param status Order status
     * @return Count of orders
     */
    @Transactional(readOnly = true)
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatusAndDeletedAtIsNull(status);
    }

    /**
     * Count orders by priority.
     *
     * @param priority Order priority
     * @return Count of orders
     */
    @Transactional(readOnly = true)
    public long countOrdersByPriority(OrderPriority priority) {
        return orderRepository.countByPriorityAndDeletedAtIsNull(priority);
    }

    /**
     * Count orders by date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return Count of orders
     */
    @Transactional(readOnly = true)
    public long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countByDateRange(startDate, endDate);
    }

    // Private helper methods

    /**
     * Create an order item for an examination.
     */
    private RadiologyOrderItem createOrderItem(RadiologyOrder order, RadiologyExamination examination) {
        BigDecimal unitPrice = examination.getBaseCost();
        BigDecimal finalPrice = unitPrice;  // Can be adjusted for quantity/discounts

        RadiologyOrderItem item = RadiologyOrderItem.builder()
                .order(order)
                .examination(examination)
                .examCode(examination.getExamCode())
                .examName(examination.getExamName())
                .quantity(1)
                .unitPrice(unitPrice)
                .discountAmount(BigDecimal.ZERO)
                .finalPrice(finalPrice)
                .status(OrderStatus.PENDING)
                .build();

        return orderItemRepository.save(item);
    }

    /**
     * Generate unique order number.
     * Format: RO + YYYYMMDD + 6-digit sequence
     */
    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "RO" + datePart;

        // Count orders with this prefix
        long count = orderRepository.countByStatusAndDeletedAtIsNull(OrderStatus.PENDING);
        // In production, use a proper sequence generator
        String sequence = String.format("%06d", (count % 1000000) + 1);

        return prefix + sequence;
    }

    /**
     * Validate status transition.
     */
    private void validateStatusTransition(OrderStatus from, OrderStatus to) {
        boolean valid = switch (from) {
            case PENDING -> to == OrderStatus.SCHEDULED || to == OrderStatus.IN_PROGRESS || to == OrderStatus.CANCELLED;
            case SCHEDULED -> to == OrderStatus.IN_PROGRESS || to == OrderStatus.CANCELLED;
            case IN_PROGRESS -> to == OrderStatus.COMPLETED || to == OrderStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;  // Terminal states
        };

        if (!valid) {
            throw new IllegalStateException("Invalid status transition: " + from + " → " + to);
        }
    }
}
