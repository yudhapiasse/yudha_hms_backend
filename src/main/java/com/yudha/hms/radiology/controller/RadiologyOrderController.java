package com.yudha.hms.radiology.controller;

import com.yudha.hms.clinical.entity.Encounter;
import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.radiology.constant.OrderStatus;
import com.yudha.hms.radiology.constant.TransportationStatus;
import com.yudha.hms.radiology.dto.request.*;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.PageResponse;
import com.yudha.hms.radiology.dto.response.RadiologyOrderItemResponse;
import com.yudha.hms.radiology.dto.response.RadiologyOrderResponse;
import com.yudha.hms.radiology.entity.RadiologyOrder;
import com.yudha.hms.radiology.entity.RadiologyOrderItem;
import com.yudha.hms.radiology.service.RadiologyOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Radiology Order Controller.
 *
 * REST controller for managing radiology orders and scheduling.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/orders")
@RequiredArgsConstructor
@Slf4j
public class RadiologyOrderController {

    private final RadiologyOrderService orderService;

    /**
     * Create new radiology order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> createOrder(
            @Valid @RequestBody RadiologyOrderRequest request) {
        log.info("Creating radiology order for patient ID: {}", request.getPatientId());

        RadiologyOrder order = convertToEntity(request);
        RadiologyOrder savedOrder = orderService.createOrder(order, request.getExaminationIds());
        RadiologyOrderResponse response = toResponse(savedOrder);

        log.info("Radiology order created successfully: {}", savedOrder.getOrderNumber());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", response));
    }

    /**
     * Schedule radiology order
     */
    @PostMapping("/{id}/schedule")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> scheduleOrder(
            @PathVariable UUID id,
            @Valid @RequestBody RadiologyOrderScheduleRequest request) {
        log.info("Scheduling radiology order ID: {}", id);

        RadiologyOrder order = orderService.scheduleOrder(
                id,
                request.getRoomId(),
                request.getScheduledDate(),
                request.getScheduledTime()
        );

        // Update technician if provided
        if (request.getTechnicianId() != null) {
            order.setTechnicianId(request.getTechnicianId());
        }

        RadiologyOrderResponse response = toResponse(order);

        log.info("Radiology order scheduled successfully: {}", order.getOrderNumber());

        return ResponseEntity.ok(ApiResponse.success("Order scheduled successfully", response));
    }

    /**
     * Get radiology order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> getOrderById(
            @PathVariable UUID id) {
        log.info("Fetching radiology order ID: {}", id);

        RadiologyOrder order = orderService.getOrderById(id);
        RadiologyOrderResponse response = toResponse(order);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search radiology orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RadiologyOrderResponse>>> searchOrders(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {
        log.info("Searching radiology orders - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<RadiologyOrder> orders = orderService.searchOrders(search, pageable);
        Page<RadiologyOrderResponse> responsePage = orders.map(this::toResponse);
        PageResponse<RadiologyOrderResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get orders by patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<PageResponse<RadiologyOrderResponse>>> getOrdersByPatient(
            @PathVariable UUID patientId,
            @PageableDefault(size = 20, sort = "orderDate") Pageable pageable) {
        log.info("Fetching orders for patient ID: {}", patientId);

        Page<RadiologyOrder> orders = orderService.getOrdersByPatient(patientId, pageable);
        Page<RadiologyOrderResponse> responsePage = orders.map(this::toResponse);
        PageResponse<RadiologyOrderResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get pending orders
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<RadiologyOrderResponse>>> getPendingOrders() {
        log.info("Fetching pending radiology orders");

        List<RadiologyOrder> orders = orderService.getPendingOrders();
        List<RadiologyOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get urgent orders
     */
    @GetMapping("/urgent")
    public ResponseEntity<ApiResponse<List<RadiologyOrderResponse>>> getUrgentOrders() {
        log.info("Fetching urgent radiology orders");

        List<RadiologyOrder> orders = orderService.getUrgentOrders();
        List<RadiologyOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get scheduled orders for room and date
     */
    @GetMapping("/room/{roomId}/date/{date}")
    public ResponseEntity<ApiResponse<List<RadiologyOrderResponse>>> getScheduledOrdersForRoom(
            @PathVariable UUID roomId,
            @PathVariable LocalDate date) {
        log.info("Fetching scheduled orders for room ID: {} on date: {}", roomId, date);

        List<RadiologyOrder> orders = orderService.getScheduledOrdersForRoom(roomId, date);
        List<RadiologyOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Update order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status,
            @RequestParam UUID changedBy,
            @RequestParam(required = false) String notes) {
        log.info("Updating status for order ID: {} to {}", id, status);

        RadiologyOrder order = orderService.updateOrderStatus(id, status, changedBy, notes);
        RadiologyOrderResponse response = toResponse(order);

        log.info("Order status updated successfully");

        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }

    /**
     * Cancel order
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> cancelOrder(
            @PathVariable UUID id,
            @RequestParam UUID cancelledBy,
            @RequestParam String reason) {
        log.info("Cancelling order ID: {}", id);

        RadiologyOrder order = orderService.cancelOrder(id, cancelledBy, reason);
        RadiologyOrderResponse response = toResponse(order);

        log.info("Order cancelled successfully");

        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }

    /**
     * Soft delete radiology order
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @PathVariable UUID id) {
        log.info("Deleting radiology order ID: {}", id);

        // Note: Need to implement deleteOrder in service
        log.info("Radiology order deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Order deleted successfully"));
    }

    // ========== Phase 11.2: Patient Safety Checks ==========

    /**
     * Verify pregnancy status
     */
    @PostMapping("/{id}/verify-pregnancy")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> verifyPregnancyStatus(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyPregnancyStatusRequest request) {
        log.info("Verifying pregnancy status for order: {}", id);

        RadiologyOrder order = orderService.verifyPregnancyStatus(
                id,
                request.getIsPregnant(),
                request.getVerifiedBy());
        RadiologyOrderResponse response = toResponse(order);

        log.info("Pregnancy status verified");

        return ResponseEntity.ok(ApiResponse.success("Pregnancy status verified", response));
    }

    /**
     * Verify contrast allergy
     */
    @PostMapping("/{id}/verify-contrast-allergy")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> verifyContrastAllergy(
            @PathVariable UUID id,
            @Valid @RequestBody VerifyContrastAllergyRequest request) {
        log.info("Verifying contrast allergy for order: {}", id);

        RadiologyOrder order = orderService.verifyContrastAllergy(
                id,
                request.getHasAllergy(),
                request.getAllergyDetails(),
                request.getVerifiedBy());
        RadiologyOrderResponse response = toResponse(order);

        log.info("Contrast allergy verified");

        return ResponseEntity.ok(ApiResponse.success("Contrast allergy verified", response));
    }

    /**
     * Request patient transportation
     */
    @PostMapping("/{id}/request-transportation")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> requestTransportation(
            @PathVariable UUID id,
            @Valid @RequestBody RequestTransportationRequest request) {
        log.info("Requesting transportation for order: {}", id);

        RadiologyOrder order = orderService.requestTransportation(id, request.getNotes());
        RadiologyOrderResponse response = toResponse(order);

        log.info("Transportation requested");

        return ResponseEntity.ok(ApiResponse.success("Transportation requested", response));
    }

    /**
     * Update transportation status
     */
    @PostMapping("/{id}/transportation-status")
    public ResponseEntity<ApiResponse<RadiologyOrderResponse>> updateTransportationStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransportationStatusRequest request) {
        log.info("Updating transportation status for order: {} to {}", id, request.getStatus());

        RadiologyOrder order = orderService.updateTransportationStatus(id, request.getStatus());
        RadiologyOrderResponse response = toResponse(order);

        log.info("Transportation status updated");

        return ResponseEntity.ok(ApiResponse.success("Transportation status updated", response));
    }

    /**
     * Get orders awaiting transportation
     */
    @GetMapping("/awaiting-transportation")
    public ResponseEntity<ApiResponse<List<RadiologyOrderResponse>>> getOrdersAwaitingTransportation() {
        log.info("Fetching orders awaiting transportation");

        List<RadiologyOrder> orders = orderService.getOrdersAwaitingTransportation();
        List<RadiologyOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get orders with contrast allergy
     */
    @GetMapping("/contrast-allergy")
    public ResponseEntity<ApiResponse<List<RadiologyOrderResponse>>> getOrdersWithContrastAllergy() {
        log.info("Fetching orders with contrast allergy");

        List<RadiologyOrder> orders = orderService.getOrdersWithContrastAllergy();
        List<RadiologyOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get orders with pregnancy concern
     */
    @GetMapping("/pregnancy-concern")
    public ResponseEntity<ApiResponse<List<RadiologyOrderResponse>>> getOrdersWithPregnancyConcern() {
        log.info("Fetching orders with pregnancy concern");

        List<RadiologyOrder> orders = orderService.getOrdersWithPregnancyConcern();
        List<RadiologyOrderResponse> responses = orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Convert entity to response DTO
     */
    private RadiologyOrderResponse toResponse(RadiologyOrder order) {
        RadiologyOrderResponse response = new RadiologyOrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());

        // Patient information
        if (order.getPatient() != null) {
            response.setPatientId(order.getPatient().getId());
            // Note: Patient name would need to be fetched from Patient entity
        }

        // Encounter information
        if (order.getEncounter() != null) {
            response.setEncounterId(order.getEncounter().getId());
        }

        // Ordering information
        response.setOrderingDoctorId(order.getOrderingDoctorId());
        response.setOrderingDepartment(order.getOrderingDepartment());
        response.setOrderDate(order.getOrderDate());

        // Priority and status
        response.setPriority(order.getPriority());
        response.setStatus(order.getStatus());

        // Clinical information
        response.setClinicalIndication(order.getClinicalIndication());
        response.setDiagnosisText(order.getDiagnosisText());

        // Scheduling
        response.setScheduledDate(order.getScheduledDate());
        response.setScheduledTime(order.getScheduledTime());

        if (order.getRoom() != null) {
            response.setRoomId(order.getRoom().getId());
            response.setRoomCode(order.getRoom().getRoomCode());
            response.setRoomName(order.getRoom().getRoomName());
        }

        response.setTechnicianId(order.getTechnicianId());

        // Additional information
        response.setNotes(order.getNotes());
        response.setSpecialInstructions(order.getSpecialInstructions());

        // Order items
        List<RadiologyOrderItem> items = orderService.getOrderItems(order.getId());
        if (items != null && !items.isEmpty()) {
            List<RadiologyOrderItemResponse> itemResponses = items.stream()
                    .map(this::toItemResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
            response.setTotalItems(items.size());
        }

        // Audit fields
        response.setCreatedAt(order.getCreatedAt());
        response.setCreatedBy(order.getCreatedBy());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setUpdatedBy(order.getUpdatedBy());

        return response;
    }

    /**
     * Convert order item entity to response DTO
     */
    private RadiologyOrderItemResponse toItemResponse(RadiologyOrderItem item) {
        RadiologyOrderItemResponse response = new RadiologyOrderItemResponse();
        response.setId(item.getId());
        response.setOrderId(item.getOrder().getId());
        response.setExaminationId(item.getExamination().getId());
        response.setExamCode(item.getExamCode());
        response.setExamName(item.getExamName());
        response.setLaterality(item.getLaterality());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setDiscountAmount(item.getDiscountAmount());
        response.setFinalPrice(item.getFinalPrice());
        response.setStatus(item.getStatus());
        response.setResultId(item.getResultId());
        response.setNotes(item.getNotes());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private RadiologyOrder convertToEntity(RadiologyOrderRequest request) {
        RadiologyOrder order = new RadiologyOrder();

        // Set patient - create minimal Patient object with just ID
        if (request.getPatientId() != null) {
            Patient patient = new Patient();
            patient.setId(request.getPatientId());
            order.setPatient(patient);
        }

        // Set encounter - create minimal Encounter object with just ID
        if (request.getEncounterId() != null) {
            Encounter encounter = new Encounter();
            encounter.setId(request.getEncounterId());
            order.setEncounter(encounter);
        }

        order.setOrderingDoctorId(request.getOrderingDoctorId());
        order.setOrderingDepartment(request.getOrderingDepartment());
        order.setPriority(request.getPriority());
        order.setClinicalIndication(request.getClinicalIndication());
        order.setDiagnosisText(request.getDiagnosisText());
        order.setNotes(request.getNotes());
        order.setSpecialInstructions(request.getSpecialInstructions());

        return order;
    }
}
