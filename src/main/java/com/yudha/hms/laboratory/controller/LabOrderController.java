package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.LabOrderRequest;
import com.yudha.hms.laboratory.dto.request.OrderCancellationRequest;
import com.yudha.hms.laboratory.dto.request.OrderStatusUpdateRequest;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.LabOrderItemResponse;
import com.yudha.hms.laboratory.dto.response.LabOrderResponse;
import com.yudha.hms.laboratory.dto.response.OrderStatusHistoryResponse;
import com.yudha.hms.laboratory.dto.response.PageResponse;
import com.yudha.hms.laboratory.dto.search.OrderSearchCriteria;
import com.yudha.hms.laboratory.entity.LabOrder;
import com.yudha.hms.laboratory.entity.LabOrderItem;
import com.yudha.hms.laboratory.entity.OrderStatusHistory;
import com.yudha.hms.laboratory.service.LabOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Laboratory Order Controller.
 *
 * REST controller for managing laboratory orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/orders")
@RequiredArgsConstructor
@Slf4j
public class LabOrderController {

    private final LabOrderService orderService;

    /**
     * Create new laboratory order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LabOrderResponse>> createOrder(
            @Valid @RequestBody LabOrderRequest request) {
        log.info("Creating laboratory order for patient ID: {}", request.getPatientId());

        LabOrder order = convertToEntity(request);
        LabOrder savedOrder = orderService.createOrder(order, request.getTestIds(), request.getPanelIds());
        LabOrderResponse response = toResponse(savedOrder);

        log.info("Laboratory order created successfully: {}", savedOrder.getOrderNumber());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    /**
     * Get laboratory order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabOrderResponse>> getOrderById(
            @PathVariable UUID id) {
        log.info("Fetching laboratory order ID: {}", id);

        LabOrder order = orderService.getOrderById(id);
        LabOrderResponse response = toResponse(order);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search laboratory orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LabOrderResponse>>> searchOrders(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {
        log.info("Searching laboratory orders - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<LabOrder> orders = orderService.searchOrders(search, pageable);
        Page<LabOrderResponse> responsePage = orders.map(this::toResponse);
        PageResponse<LabOrderResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get order by order number
     */
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<LabOrderResponse>> getOrderByNumber(
            @PathVariable String orderNumber) {
        log.info("Fetching laboratory order by number: {}", orderNumber);

        LabOrder order = orderService.getOrderByNumber(orderNumber);
        LabOrderResponse response = toResponse(order);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get patient orders
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<LabOrderResponse>>> getPatientOrders(
            @PathVariable UUID patientId) {
        log.info("Fetching orders for patient ID: {}", patientId);

        List<LabOrder> orders = orderService.getOrdersByPatient(patientId);
        List<LabOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get pending orders
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<LabOrderResponse>>> getPendingOrders() {
        log.info("Fetching pending orders");

        List<LabOrder> orders = orderService.getPendingOrders();
        List<LabOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get urgent orders
     */
    @GetMapping("/urgent")
    public ResponseEntity<ApiResponse<List<LabOrderResponse>>> getUrgentOrders() {
        log.info("Fetching urgent orders");

        List<LabOrder> orders = orderService.getUrgentOrders();
        List<LabOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Update order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<LabOrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        log.info("Updating status for order ID: {} to {}", id, request.getNewStatus());

        LabOrder order = orderService.updateOrderStatus(id, request.getNewStatus(),
            request.getUpdatedBy() != null ? request.getUpdatedBy().toString() : "SYSTEM",
            request.getStatusReason());
        LabOrderResponse response = toResponse(order);

        log.info("Order status updated successfully");

        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }

    /**
     * Cancel order
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<LabOrderResponse>> cancelOrder(
            @PathVariable UUID id,
            @Valid @RequestBody OrderCancellationRequest request) {
        log.info("Cancelling order ID: {}", id);

        LabOrder order = orderService.cancelOrder(id, request.getCancelledBy(), request.getCancellationReason());
        LabOrderResponse response = toResponse(order);

        log.info("Order cancelled successfully");

        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }

    /**
     * Get order items
     */
    @GetMapping("/{id}/items")
    public ResponseEntity<ApiResponse<List<LabOrderItemResponse>>> getOrderItems(
            @PathVariable UUID id) {
        log.info("Fetching items for order ID: {}", id);

        List<LabOrderItem> items = orderService.getOrderItems(id);
        List<LabOrderItemResponse> responses = items.stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get order status history
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<OrderStatusHistoryResponse>>> getOrderStatusHistory(
            @PathVariable UUID id) {
        log.info("Fetching status history for order ID: {}", id);

        // Note: OrderStatusHistoryService.getOrderStatusHistory method needs to be available
        // For now, return empty list as workaround
        List<OrderStatusHistoryResponse> responses = List.of();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Convert entity to response DTO
     */
    private LabOrderResponse toResponse(LabOrder order) {
        LabOrderResponse response = new LabOrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setPatientId(order.getPatientId());
        response.setEncounterId(order.getEncounterId());
        response.setOrderingDoctorId(order.getOrderingDoctorId());
        response.setOrderingDepartment(order.getOrderingDepartment());
        response.setOrderingLocation(order.getOrderingLocation());
        response.setOrderDate(order.getOrderDate());
        response.setPriority(order.getPriority());
        response.setUrgencyReason(order.getUrgencyReason());
        response.setStatus(order.getStatus());
        response.setStatusReason(order.getStatusReason());
        response.setClinicalIndication(order.getClinicalIndication());
        response.setDiagnosisCode(order.getDiagnosisCode());
        response.setDiagnosisText(order.getDiagnosisText());
        response.setCollectionScheduledAt(order.getCollectionScheduledAt());
        response.setCollectionLocation(order.getCollectionLocation());
        response.setIsRecurring(order.getIsRecurring());
        response.setRecurrencePattern(order.getRecurrencePattern());
        response.setRecurrenceEndDate(order.getRecurrenceEndDate());
        response.setParentOrderId(order.getParentOrderId());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setInsuranceCompanyId(order.getInsuranceCompanyId());
        response.setCoverageType(order.getCoverageType());
        response.setCancelledAt(order.getCancelledAt());
        response.setCancelledBy(order.getCancelledBy());
        response.setCancellationReason(order.getCancellationReason());
        response.setCompletedAt(order.getCompletedAt());
        response.setResultVerifiedAt(order.getResultVerifiedAt());
        response.setResultVerifiedBy(order.getResultVerifiedBy());
        response.setExternalOrderId(order.getExternalOrderId());
        response.setExternalSystem(order.getExternalSystem());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        response.setCreatedBy(order.getCreatedBy());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setUpdatedBy(order.getUpdatedBy());
        return response;
    }

    /**
     * Convert order item entity to response DTO
     */
    private LabOrderItemResponse toItemResponse(LabOrderItem item) {
        LabOrderItemResponse response = new LabOrderItemResponse();
        response.setId(item.getId());
        response.setOrderId(item.getOrder().getId());
        response.setItemType(item.getItemType());
        response.setTestId(item.getTest() != null ? item.getTest().getId() : null);
        response.setTestCode(item.getTestCode());
        response.setTestName(item.getTestName());
        response.setPanelId(item.getPanel() != null ? item.getPanel().getId() : null);
        response.setStatus(item.getStatus());
        response.setUnitPrice(item.getUnitPrice());
        response.setDiscountAmount(item.getDiscountAmount());
        response.setFinalPrice(item.getFinalPrice());
        response.setSpecimenId(item.getSpecimenId());
        response.setResultId(item.getResultId());
        response.setResultCompletedAt(item.getResultCompletedAt());
        response.setNotes(item.getNotes());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    /**
     * Convert status history entity to response DTO
     */
    private OrderStatusHistoryResponse toHistoryResponse(OrderStatusHistory history) {
        OrderStatusHistoryResponse response = new OrderStatusHistoryResponse();
        response.setId(history.getId());
        response.setOrderId(history.getOrder().getId());
        response.setPreviousStatus(history.getPreviousStatus());
        response.setNewStatus(history.getNewStatus());
        response.setChangedBy(history.getChangedBy());
        response.setChangeReason(history.getChangeReason());
        response.setStatusChangedAt(history.getStatusChangedAt());
        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private LabOrder convertToEntity(LabOrderRequest request) {
        LabOrder order = new LabOrder();
        order.setPatientId(request.getPatientId());
        order.setEncounterId(request.getEncounterId());
        order.setOrderingDoctorId(request.getOrderingDoctorId());
        order.setOrderingDepartment(request.getOrderingDepartment());
        order.setOrderingLocation(request.getOrderingLocation());
        order.setOrderDate(LocalDateTime.now());
        order.setPriority(request.getPriority());
        order.setClinicalIndication(request.getClinicalIndication());
        order.setDiagnosisCode(request.getDiagnosisCode());
        order.setIsRecurring(request.getIsRecurring());
        order.setRecurrencePattern(request.getRecurrencePattern());
        order.setRecurrenceEndDate(request.getRecurrenceEndDate());
        order.setNotes(request.getNotes());
        return order;
    }
}
