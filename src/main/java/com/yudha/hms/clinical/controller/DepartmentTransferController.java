package com.yudha.hms.clinical.controller;

import com.yudha.hms.clinical.dto.ApprovalRequest;
import com.yudha.hms.clinical.dto.TransferRequest;
import com.yudha.hms.clinical.dto.TransferResponse;
import com.yudha.hms.clinical.entity.TransferType;
import com.yudha.hms.clinical.service.DepartmentTransferService;
import com.yudha.hms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Department Transfer Controller.
 *
 * REST API endpoints for department transfer management workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@RestController
@RequestMapping("/api/clinical/transfers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DepartmentTransferController {

    private final DepartmentTransferService transferService;

    // ========== Request Transfer ==========

    /**
     * Request a new department transfer.
     *
     * POST /api/clinical/transfers
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> requestTransfer(
        @Valid @RequestBody TransferRequest request
    ) {
        log.info("REST: Requesting transfer for encounter: {}", request.getEncounterId());

        TransferResponse response = transferService.requestTransfer(request);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(
                "Transfer berhasil diminta",
                response
            ));
    }

    // ========== Approval Workflow ==========

    /**
     * Approve or reject transfer request.
     *
     * POST /api/clinical/transfers/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<TransferResponse>> processApproval(
        @PathVariable UUID id,
        @Valid @RequestBody ApprovalRequest approvalRequest
    ) {
        log.info("REST: Processing approval for transfer: {}", id);

        TransferResponse response = transferService.processApproval(id, approvalRequest);

        String message = Boolean.TRUE.equals(approvalRequest.getApproved()) ?
            "Transfer disetujui" : "Transfer ditolak";

        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    // ========== Accept Transfer ==========

    /**
     * Accept transfer request by receiving department.
     *
     * POST /api/clinical/transfers/{id}/accept
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<TransferResponse>> acceptTransfer(
        @PathVariable UUID id,
        @RequestParam UUID acceptedById,
        @RequestParam String acceptedByName
    ) {
        log.info("REST: Accepting transfer: {} by: {}", id, acceptedByName);

        TransferResponse response = transferService.acceptTransfer(id, acceptedById, acceptedByName);

        return ResponseEntity.ok(ApiResponse.success(
            "Transfer diterima",
            response
        ));
    }

    // ========== Execute Transfer ==========

    /**
     * Start transfer execution (patient in transit).
     *
     * POST /api/clinical/transfers/{id}/start
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<ApiResponse<TransferResponse>> startTransfer(
        @PathVariable UUID id
    ) {
        log.info("REST: Starting transfer: {}", id);

        TransferResponse response = transferService.startTransfer(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Transfer dimulai - pasien dalam perjalanan",
            response
        ));
    }

    /**
     * Complete transfer execution.
     *
     * POST /api/clinical/transfers/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<TransferResponse>> completeTransfer(
        @PathVariable UUID id
    ) {
        log.info("REST: Completing transfer: {}", id);

        TransferResponse response = transferService.completeTransfer(id);

        return ResponseEntity.ok(ApiResponse.success(
            "Transfer selesai",
            response
        ));
    }

    // ========== Cancel Transfer ==========

    /**
     * Cancel transfer request.
     *
     * POST /api/clinical/transfers/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<TransferResponse>> cancelTransfer(
        @PathVariable UUID id,
        @RequestParam String reason,
        @RequestParam String cancelledBy
    ) {
        log.info("REST: Cancelling transfer: {} by: {}", id, cancelledBy);

        TransferResponse response = transferService.cancelTransfer(id, reason, cancelledBy);

        return ResponseEntity.ok(ApiResponse.success(
            "Transfer dibatalkan",
            response
        ));
    }

    // ========== Query Endpoints ==========

    /**
     * Get transfer by ID.
     *
     * GET /api/clinical/transfers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransferResponse>> getTransferById(
        @PathVariable UUID id
    ) {
        log.info("REST: Fetching transfer: {}", id);

        TransferResponse response = transferService.getTransferById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all transfers for an encounter.
     *
     * GET /api/clinical/transfers/encounter/{encounterId}
     */
    @GetMapping("/encounter/{encounterId}")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getTransfersByEncounter(
        @PathVariable UUID encounterId
    ) {
        log.info("REST: Fetching transfers for encounter: {}", encounterId);

        List<TransferResponse> transfers = transferService.getTransfersByEncounter(encounterId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d transfers", transfers.size()),
            transfers
        ));
    }

    /**
     * Get all pending transfers.
     *
     * GET /api/clinical/transfers/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getPendingTransfers() {
        log.info("REST: Fetching pending transfers");

        List<TransferResponse> transfers = transferService.getPendingTransfers();

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d pending transfers", transfers.size()),
            transfers
        ));
    }

    /**
     * Get transfers pending approval.
     *
     * GET /api/clinical/transfers/pending-approval
     */
    @GetMapping("/pending-approval")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getTransfersPendingApproval() {
        log.info("REST: Fetching transfers pending approval");

        List<TransferResponse> transfers = transferService.getTransfersPendingApproval();

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d transfers pending approval", transfers.size()),
            transfers
        ));
    }

    /**
     * Get active transfers for a department.
     *
     * GET /api/clinical/transfers/department/{departmentId}/active
     */
    @GetMapping("/department/{departmentId}/active")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getActiveDepartmentTransfers(
        @PathVariable UUID departmentId
    ) {
        log.info("REST: Fetching active transfers for department: {}", departmentId);

        List<TransferResponse> transfers =
            transferService.getActiveDepartmentTransfers(departmentId);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d active transfers", transfers.size()),
            transfers
        ));
    }

    /**
     * Get transfers by practitioner.
     *
     * GET /api/clinical/transfers/practitioner/{practitionerId}
     */
    @GetMapping("/practitioner/{practitionerId}")
    public ResponseEntity<ApiResponse<List<TransferResponse>>> getTransfersByPractitioner(
        @PathVariable UUID practitionerId,
        @RequestParam(defaultValue = "TRANSFERRING") String role
    ) {
        log.info("REST: Fetching transfers for practitioner: {} (role: {})",
            practitionerId, role);

        List<TransferResponse> transfers =
            transferService.getTransfersByPractitioner(practitionerId, role);

        return ResponseEntity.ok(ApiResponse.success(
            String.format("Found %d transfers", transfers.size()),
            transfers
        ));
    }

    // ========== Reference Data ==========

    /**
     * Get all transfer types with metadata.
     *
     * GET /api/clinical/transfers/types
     */
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTransferTypes() {
        log.info("REST: Fetching transfer types");

        List<Map<String, Object>> types = Arrays.stream(TransferType.values())
            .map(type -> {
                Map<String, Object> typeInfo = new HashMap<>();
                typeInfo.put("value", type.name());
                typeInfo.put("displayName", type.getDisplayName());
                typeInfo.put("indonesianName", type.getIndonesianName());
                typeInfo.put("description", type.getDescription());
                typeInfo.put("requiresApproval", type.requiresApproval());
                typeInfo.put("isUrgent", type.isUrgent());
                typeInfo.put("isStepUp", type.isStepUp());
                typeInfo.put("isStepDown", type.isStepDown());
                typeInfo.put("isICURelated", type.isICURelated());
                typeInfo.put("isExternal", type.isExternal());
                return typeInfo;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Transfer types retrieved successfully",
            types
        ));
    }

    /**
     * Get all transfer statuses.
     *
     * GET /api/clinical/transfers/statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTransferStatuses() {
        log.info("REST: Fetching transfer statuses");

        List<Map<String, Object>> statuses = Arrays.stream(
            com.yudha.hms.clinical.entity.TransferStatus.values()
        )
            .map(status -> {
                Map<String, Object> statusInfo = new HashMap<>();
                statusInfo.put("value", status.name());
                statusInfo.put("displayName", status.getDisplayName());
                statusInfo.put("indonesianName", status.getIndonesianName());
                statusInfo.put("order", status.getOrder());
                statusInfo.put("isActive", status.isActive());
                statusInfo.put("isCompleted", status.isCompleted());
                statusInfo.put("isTerminated", status.isTerminated());
                statusInfo.put("isPending", status.isPending());
                statusInfo.put("canBeCancelled", status.canBeCancelled());
                return statusInfo;
            })
            .toList();

        return ResponseEntity.ok(ApiResponse.success(
            "Transfer statuses retrieved successfully",
            statuses
        ));
    }
}
