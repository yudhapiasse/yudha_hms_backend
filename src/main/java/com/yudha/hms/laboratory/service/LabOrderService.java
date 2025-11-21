package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.constant.OrderPriority;
import com.yudha.hms.laboratory.constant.OrderStatus;
import com.yudha.hms.laboratory.entity.LabOrder;
import com.yudha.hms.laboratory.entity.LabOrderItem;
import com.yudha.hms.laboratory.entity.LabPanel;
import com.yudha.hms.laboratory.entity.LabTest;
import com.yudha.hms.laboratory.repository.LabOrderRepository;
import com.yudha.hms.laboratory.repository.LabOrderItemRepository;
import com.yudha.hms.laboratory.repository.LabTestRepository;
import com.yudha.hms.laboratory.repository.LabPanelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for Laboratory Order operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LabOrderService {

    private final LabOrderRepository labOrderRepository;
    private final LabOrderItemRepository labOrderItemRepository;
    private final LabTestRepository labTestRepository;
    private final LabPanelRepository labPanelRepository;
    private final com.yudha.hms.laboratory.repository.LabPanelItemRepository labPanelItemRepository;
    private final OrderStatusHistoryService orderStatusHistoryService;

    /**
     * Create new lab order
     */
    public LabOrder createOrder(LabOrder order, List<UUID> testIds, List<UUID> panelIds) {
        log.info("Creating new lab order for patient: {}", order.getPatientId());

        // Generate order number
        String orderNumber = generateOrderNumber();
        order.setOrderNumber(orderNumber);

        // Set initial status
        order.setStatus(OrderStatus.PENDING);

        // TODO: Expected TAT requires additional entity field
        // order.setExpectedTatMinutes(calculateExpectedTat(order.getPriority()));

        LabOrder saved = labOrderRepository.save(order);

        // Create order items for individual tests
        if (testIds != null && !testIds.isEmpty()) {
            for (UUID testId : testIds) {
                LabTest test = labTestRepository.findByIdAndDeletedAtIsNull(testId)
                        .orElseThrow(() -> new IllegalArgumentException("Test not found: " + testId));
                createOrderItem(saved, test, null);
            }
        }

        // Create order items for panels
        if (panelIds != null && !panelIds.isEmpty()) {
            for (UUID panelId : panelIds) {
                LabPanel panel = labPanelRepository.findByIdAndDeletedAtIsNull(panelId)
                        .orElseThrow(() -> new IllegalArgumentException("Panel not found: " + panelId));
                // Panel creates multiple order items (one per test in panel)
                createOrderItemsFromPanel(saved, panel);
            }
        }

        // Record status history
        orderStatusHistoryService.recordStatusChange(saved, null, OrderStatus.PENDING,
                order.getOrderingDoctorId().toString(), "Order created");

        log.info("Lab order created successfully: {}", saved.getOrderNumber());
        return saved;
    }

    /**
     * Create order item from test
     */
    private LabOrderItem createOrderItem(LabOrder order, LabTest test, LabPanel panel) {
        LabOrderItem item = LabOrderItem.builder()
                .order(order)
                .test(test)
                .panel(panel)
                .itemType(panel == null ? "TEST" : "PANEL")
                .testCode(test.getTestCode())
                .testName(test.getName())
                // TODO: LabOrderItem does not have these fields - they belong to LabTest
                // .sampleType(test.getSampleType())
                // .sampleVolume(test.getSampleVolumeMl())
                // .sampleVolumeUnit(test.getSampleVolumeUnit())
                // .containerType(test.getSampleContainer())
                // .processingTimeMinutes(test.getProcessingTimeMinutes())
                .unitPrice(test.getBaseCost())
                // .quantity(1) // LabOrderItem does not have quantity field
                .finalPrice(test.getBaseCost())
                .status("PENDING")
                .build();

        return labOrderItemRepository.save(item);
    }

    /**
     * Create order items from panel
     */
    private void createOrderItemsFromPanel(LabOrder order, LabPanel panel) {
        // Get all tests in panel and create order items
        // LabPanel entity does not have getPanelItems() - need to query repository
        labPanelItemRepository.findByPanelId(panel.getId()).forEach(panelItem -> {
            createOrderItem(order, panelItem.getTest(), panel);
        });
    }

    /**
     * Update order status
     */
    public LabOrder updateOrderStatus(UUID orderId, OrderStatus newStatus, String changedBy, String reason) {
        log.info("Updating order status: {} to {}", orderId, newStatus);

        LabOrder order = labOrderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        OrderStatus previousStatus = order.getStatus();

        // Validate status transition
        validateStatusTransition(previousStatus, newStatus);

        // Update status
        order.setStatus(newStatus);

        // Update timestamps based on status
        switch (newStatus) {
            // TODO: APPROVED, SPECIMEN_COLLECTED, SPECIMEN_RECEIVED, AMENDED statuses don't exist in OrderStatus enum
            // case APPROVED:
            //     order.setApprovedAt(LocalDateTime.now());
            //     order.setApprovedBy(UUID.fromString(changedBy));
            //     break;
            // case SPECIMEN_COLLECTED:
            //     if (order.getSpecimenCollectedAt() == null) {
            //         order.setSpecimenCollectedAt(LocalDateTime.now());
            //     }
            //     break;
            // case SPECIMEN_RECEIVED:
            //     if (order.getSpecimenReceivedAt() == null) {
            //         order.setSpecimenReceivedAt(LocalDateTime.now());
            //     }
            //     break;
            case IN_PROGRESS:
                // TODO: LabOrder does not have processingStartedAt field
                // if (order.getProcessingStartedAt() == null) {
                //     order.setProcessingStartedAt(LocalDateTime.now());
                // }
                break;
            case COMPLETED:
                order.setCompletedAt(LocalDateTime.now());
                break;
            case CANCELLED:
                order.setCancelledAt(LocalDateTime.now());
                order.setCancelledBy(changedBy); // cancelledBy is String, not UUID
                order.setCancellationReason(reason);
                break;
        }

        LabOrder updated = labOrderRepository.save(order);

        // Record status history
        orderStatusHistoryService.recordStatusChange(updated, previousStatus, newStatus, changedBy, reason);

        log.info("Order status updated successfully: {} -> {}", previousStatus, newStatus);
        return updated;
    }

    // TODO: APPROVED and REJECTED statuses don't exist in OrderStatus enum
    // /**
    //  * Approve order
    //  */
    // public LabOrder approveOrder(UUID orderId, UUID approvedBy) {
    //     return updateOrderStatus(orderId, OrderStatus.APPROVED, approvedBy.toString(), "Order approved");
    // }

    // /**
    //  * Reject order
    //  */
    // public LabOrder rejectOrder(UUID orderId, UUID rejectedBy, String reason) {
    //     LabOrder order = updateOrderStatus(orderId, OrderStatus.REJECTED, rejectedBy.toString(), reason);
    //     order.setRejectedAt(LocalDateTime.now());
    //     order.setRejectedBy(rejectedBy);
    //     order.setRejectionReason(reason);
    //     return labOrderRepository.save(order);
    // }

    /**
     * Cancel order
     */
    public LabOrder cancelOrder(UUID orderId, UUID cancelledBy, String reason) {
        log.info("Cancelling order: {}", orderId);

        // Check if order can be cancelled
        LabOrder order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in " + order.getStatus() + " status");
        }

        return updateOrderStatus(orderId, OrderStatus.CANCELLED, cancelledBy.toString(), reason);
    }

    /**
     * Create recurring order
     */
    public LabOrder createRecurringOrder(UUID parentOrderId, LocalDate scheduledDate) {
        log.info("Creating recurring order from parent: {}", parentOrderId);

        LabOrder parentOrder = getOrderById(parentOrderId);

        if (!Boolean.TRUE.equals(parentOrder.getIsRecurring())) {
            throw new IllegalArgumentException("Parent order is not marked as recurring");
        }

        // Clone parent order
        LabOrder recurringOrder = LabOrder.builder()
                .patientId(parentOrder.getPatientId())
                .encounterId(parentOrder.getEncounterId())
                .orderingDoctorId(parentOrder.getOrderingDoctorId())
                .orderingDepartment(parentOrder.getOrderingDepartment())
                .orderingLocation(parentOrder.getOrderingLocation())
                .orderDate(LocalDateTime.now())
                .priority(parentOrder.getPriority())
                .clinicalIndication(parentOrder.getClinicalIndication()) // Use clinicalIndication, not clinicalInfo
                // TODO: LabOrder does not have specialInstructions field
                // .specialInstructions(parentOrder.getSpecialInstructions())
                .isRecurring(true)
                .parentOrderId(parentOrderId)
                .recurrencePattern(parentOrder.getRecurrencePattern()) // Use recurrencePattern, not recurringFrequency
                .recurrenceEndDate(parentOrder.getRecurrenceEndDate()) // Use recurrenceEndDate, not recurringUntil
                // TODO: LabOrder does not have nextRecurrenceDate field
                // .nextRecurrenceDate(calculateNextRecurrenceDate(scheduledDate, parentOrder.getRecurrencePattern()))
                .build();

        // Get test IDs from parent order
        List<UUID> testIds = labOrderItemRepository.findByOrderIdOrderByIdAsc(parentOrderId).stream()
                .filter(item -> item.getPanel() == null)
                .map(item -> item.getTest().getId())
                .distinct()
                .toList();

        // Get panel IDs from parent order
        List<UUID> panelIds = labOrderItemRepository.findByOrderIdOrderByIdAsc(parentOrderId).stream()
                .filter(item -> item.getPanel() != null)
                .map(item -> item.getPanel().getId())
                .distinct()
                .toList();

        return createOrder(recurringOrder, testIds, panelIds);
    }

    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public LabOrder getOrderById(UUID id) {
        return labOrderRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    /**
     * Get order by number
     */
    @Transactional(readOnly = true)
    public LabOrder getOrderByNumber(String orderNumber) {
        return labOrderRepository.findByOrderNumberAndDeletedAtIsNull(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));
    }

    /**
     * Get orders by patient
     */
    @Transactional(readOnly = true)
    public List<LabOrder> getOrdersByPatient(UUID patientId) {
        return labOrderRepository.findByPatientIdAndDeletedAtIsNullOrderByOrderDateDesc(patientId);
    }

    /**
     * Get orders by encounter
     */
    @Transactional(readOnly = true)
    public List<LabOrder> getOrdersByEncounter(UUID encounterId) {
        return labOrderRepository.findByEncounterIdAndDeletedAtIsNullOrderByOrderDateDesc(encounterId);
    }

    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public Page<LabOrder> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return labOrderRepository.findByStatusAndDeletedAtIsNullOrderByOrderDateDesc(status, pageable);
    }

    /**
     * Get pending orders
     */
    @Transactional(readOnly = true)
    public List<LabOrder> getPendingOrders() {
        // Simplified implementation using available methods
        return labOrderRepository.findByStatusAndDeletedAtIsNullOrderByOrderDateDesc(OrderStatus.PENDING, org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    /**
     * Get urgent orders
     */
    @Transactional(readOnly = true)
    public List<LabOrder> getUrgentOrders() {
        return labOrderRepository.findUrgentOrders();
    }

    /**
     * Get overdue orders
     */
    @Transactional(readOnly = true)
    public List<LabOrder> getOverdueOrders(LocalDateTime cutoffTime) {
        // TODO: Implement custom repository query for overdue orders
        // For now, return all pending orders as a simplified implementation
        return labOrderRepository.findByStatusAndDeletedAtIsNullOrderByOrderDateDesc(
            OrderStatus.PENDING,
            org.springframework.data.domain.PageRequest.of(0, 100)
        ).getContent();
    }

    /**
     * Search orders
     */
    @Transactional(readOnly = true)
    public Page<LabOrder> searchOrders(String search, Pageable pageable) {
        return labOrderRepository.searchOrders(search, pageable);
    }

    /**
     * Get order items
     */
    @Transactional(readOnly = true)
    public List<LabOrderItem> getOrderItems(UUID orderId) {
        return labOrderItemRepository.findByOrderIdOrderByIdAsc(orderId);
    }

    /**
     * Calculate order total
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateOrderTotal(UUID orderId) {
        List<LabOrderItem> items = getOrderItems(orderId);
        return items.stream()
                .map(LabOrderItem::getFinalPrice) // Use getFinalPrice, not getTotalPrice
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Count orders by status
     */
    @Transactional(readOnly = true)
    public long countOrdersByStatus(OrderStatus status) {
        return labOrderRepository.countByStatusAndDeletedAtIsNull(status);
    }

    /**
     * Get orders by date range
     */
    @Transactional(readOnly = true)
    public List<LabOrder> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return labOrderRepository.findByOrderDateBetweenAndDeletedAtIsNullOrderByOrderDateDesc(startDate, endDate);
    }

    // Helper methods

    private String generateOrderNumber() {
        String prefix = "LO";
        String datePart = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = labOrderRepository.countByOrderNumberStartingWith(prefix + datePart);
        String sequence = String.format("%05d", count + 1);
        return prefix + datePart + sequence;
    }

    // TODO: OrderPriority does not have getExpectedTatMinutes method
    // private Integer calculateExpectedTat(OrderPriority priority) {
    //     return priority.getExpectedTatMinutes();
    // }

    private void validateStatusTransition(OrderStatus from, OrderStatus to) {
        // Define valid transitions - simplified to match available OrderStatus values
        // Available statuses: PENDING, SCHEDULED, COLLECTED, RECEIVED, IN_PROGRESS, COMPLETED, CANCELLED
        boolean valid = switch (from) {
            case PENDING -> to == OrderStatus.SCHEDULED || to == OrderStatus.COLLECTED || to == OrderStatus.CANCELLED;
            case SCHEDULED -> to == OrderStatus.COLLECTED || to == OrderStatus.CANCELLED;
            case COLLECTED -> to == OrderStatus.RECEIVED || to == OrderStatus.CANCELLED;
            case RECEIVED -> to == OrderStatus.IN_PROGRESS || to == OrderStatus.CANCELLED;
            case IN_PROGRESS -> to == OrderStatus.COMPLETED || to == OrderStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false; // Terminal states
        };

        if (!valid) {
            throw new IllegalStateException("Invalid status transition: " + from + " -> " + to);
        }
    }

    private LocalDate calculateNextRecurrenceDate(LocalDate currentDate, String frequency) {
        return switch (frequency.toUpperCase()) {
            case "DAILY" -> currentDate.plusDays(1);
            case "WEEKLY" -> currentDate.plusWeeks(1);
            case "MONTHLY" -> currentDate.plusMonths(1);
            default -> currentDate.plusDays(1);
        };
    }
}
