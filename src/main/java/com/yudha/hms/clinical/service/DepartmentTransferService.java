package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.ApprovalRequest;
import com.yudha.hms.clinical.dto.TransferRequest;
import com.yudha.hms.clinical.dto.TransferResponse;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.exception.EncounterBusinessRuleException;
import com.yudha.hms.clinical.exception.InvalidStatusTransitionException;
import com.yudha.hms.clinical.repository.DepartmentTransferRepository;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Department Transfer Service.
 *
 * Manages the complete department transfer workflow including:
 * - Request transfer with clinical summary
 * - Approval workflow for ICU/special care transfers
 * - Execute transfer (update encounter, locations, care team)
 * - Notifications and billing impact
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DepartmentTransferService {

    private final DepartmentTransferRepository transferRepository;
    private final EncounterRepository encounterRepository;

    // ========== Request Transfer ==========

    /**
     * Request a new department transfer.
     *
     * Workflow:
     * 1. Validate encounter exists and is active
     * 2. Check for existing active transfer
     * 3. Validate clinical summary (handover notes)
     * 4. Check destination bed availability (TODO: integrate with bed management)
     * 5. Create transfer request
     * 6. If requires approval → PENDING_APPROVAL, else → REQUESTED
     *
     * @param request transfer request details
     * @return created transfer response
     */
    public TransferResponse requestTransfer(TransferRequest request) {
        log.info("Requesting transfer for encounter: {}", request.getEncounterId());

        // Step 1: Validate encounter
        Encounter encounter = findEncounterById(request.getEncounterId());
        validateEncounterForTransfer(encounter);

        // Step 2: Check for existing active transfer
        if (hasActiveTransfer(encounter.getId())) {
            throw new ValidationException(
                "Encounter sudah memiliki transfer aktif. " +
                "Selesaikan atau batalkan transfer yang ada terlebih dahulu."
            );
        }

        // Step 3: Validate clinical summary for critical transfers
        validateClinicalSummary(request);

        // Step 4: Check destination bed availability (TODO: integrate with bed management)
        // validateBedAvailability(request.getToLocationId());

        // Step 5: Create transfer
        DepartmentTransfer transfer = buildTransferFromRequest(encounter, request);

        // Step 6: Determine initial status based on approval requirement
        if (transfer.needsApproval()) {
            transfer.requestApproval();
            log.info("Transfer requires approval, status set to PENDING_APPROVAL");
        }

        transfer = transferRepository.save(transfer);
        log.info("Transfer created: {}", transfer.getTransferNumber());

        // TODO: Send notification to destination department
        // notifyDestinationDepartment(transfer);

        return mapToResponse(transfer);
    }

    // ========== Approval Workflow ==========

    /**
     * Approve or reject transfer request.
     *
     * @param transferId transfer ID
     * @param approvalRequest approval decision and notes
     * @return updated transfer
     */
    public TransferResponse processApproval(UUID transferId, ApprovalRequest approvalRequest) {
        log.info("Processing approval for transfer: {}", transferId);

        DepartmentTransfer transfer = findTransferById(transferId);

        // Validate transfer is in correct status
        if (transfer.getTransferStatus() != TransferStatus.PENDING_APPROVAL) {
            throw new ValidationException(
                "Transfer tidak dalam status menunggu persetujuan. " +
                "Status saat ini: " + transfer.getTransferStatus().getIndonesianName()
            );
        }

        if (Boolean.TRUE.equals(approvalRequest.getApproved())) {
            // Approve transfer
            transfer.approve(
                approvalRequest.getApprovedById(),
                approvalRequest.getApprovedByName(),
                approvalRequest.getApprovalNotes()
            );
            log.info("Transfer approved by: {}", approvalRequest.getApprovedByName());
        } else {
            // Reject transfer
            if (approvalRequest.getRejectionReason() == null ||
                approvalRequest.getRejectionReason().trim().isEmpty()) {
                throw new ValidationException("Alasan penolakan wajib diisi");
            }
            transfer.reject(approvalRequest.getRejectionReason());
            log.info("Transfer rejected: {}", approvalRequest.getRejectionReason());
        }

        transfer = transferRepository.save(transfer);

        // TODO: Notify requesting department of approval/rejection
        // notifyRequestingDepartment(transfer);

        return mapToResponse(transfer);
    }

    // ========== Accept Transfer ==========

    /**
     * Accept transfer request by receiving department.
     *
     * @param transferId transfer ID
     * @param acceptedById ID of person accepting
     * @param acceptedByName name of person accepting
     * @return updated transfer
     */
    public TransferResponse acceptTransfer(UUID transferId, UUID acceptedById, String acceptedByName) {
        log.info("Accepting transfer: {}", transferId);

        DepartmentTransfer transfer = findTransferById(transferId);

        // Validate transfer can be accepted
        if (!transfer.getTransferStatus().canTransitionTo(TransferStatus.ACCEPTED)) {
            throw new InvalidStatusTransitionException(
                transfer.getTransferStatus().getTransitionErrorMessage(TransferStatus.ACCEPTED)
            );
        }

        // TODO: Final bed availability check
        // if (!isBedAvailable(transfer.getToLocationId())) {
        //     throw new ValidationException("Tempat tidur tujuan tidak tersedia");
        // }

        transfer.accept(acceptedById, acceptedByName);
        transfer = transferRepository.save(transfer);

        log.info("Transfer accepted by: {}", acceptedByName);

        return mapToResponse(transfer);
    }

    // ========== Execute Transfer ==========

    /**
     * Start transfer execution (patient in transit).
     *
     * @param transferId transfer ID
     * @return updated transfer
     */
    public TransferResponse startTransfer(UUID transferId) {
        log.info("Starting transfer execution: {}", transferId);

        DepartmentTransfer transfer = findTransferById(transferId);

        transfer.startTransfer();
        transfer = transferRepository.save(transfer);

        log.info("Transfer in transit");

        // TODO: Update patient location status if needed
        // updatePatientLocationStatus(transfer.getPatientId(), "IN_TRANSIT");

        return mapToResponse(transfer);
    }

    /**
     * Complete transfer execution.
     *
     * This is the critical step that:
     * 1. Updates encounter department and location
     * 2. Releases source bed
     * 3. Occupies destination bed
     * 4. Updates care team assignments
     * 5. Triggers billing impact calculation
     *
     * @param transferId transfer ID
     * @return updated transfer
     */
    public TransferResponse completeTransfer(UUID transferId) {
        log.info("Completing transfer: {}", transferId);

        DepartmentTransfer transfer = findTransferById(transferId);
        Encounter encounter = transfer.getEncounter();

        // Step 1: Update encounter location
        updateEncounterLocation(encounter, transfer);

        // Step 2: Release source bed (TODO: integrate with bed management)
        // releaseBed(transfer.getFromLocationId());

        // Step 3: Occupy destination bed (TODO: integrate with bed management)
        // occupyBed(transfer.getToLocationId(), encounter.getPatientId());

        // Step 4: Update care team (TODO: integrate with care team management)
        // updateCareTeam(encounter, transfer);

        // Step 5: Complete transfer
        transfer.complete();
        transfer = transferRepository.save(transfer);

        // Step 6: Save updated encounter
        encounterRepository.save(encounter);

        log.info("Transfer completed successfully");

        // TODO: Trigger billing impact calculation
        // calculateBillingImpact(transfer);

        // TODO: Notify both departments
        // notifyTransferCompletion(transfer);

        return mapToResponse(transfer);
    }

    // ========== Cancel Transfer ==========

    /**
     * Cancel transfer request.
     *
     * @param transferId transfer ID
     * @param reason cancellation reason
     * @param cancelledBy user who cancelled
     * @return updated transfer
     */
    public TransferResponse cancelTransfer(UUID transferId, String reason, String cancelledBy) {
        log.info("Cancelling transfer: {}", transferId);

        DepartmentTransfer transfer = findTransferById(transferId);

        if (reason == null || reason.trim().isEmpty()) {
            throw new ValidationException("Alasan pembatalan wajib diisi");
        }

        transfer.cancel(reason, cancelledBy);
        transfer = transferRepository.save(transfer);

        log.info("Transfer cancelled by: {}", cancelledBy);

        // TODO: Notify both departments
        // notifyTransferCancellation(transfer);

        return mapToResponse(transfer);
    }

    // ========== Query Methods ==========

    /**
     * Get transfer by ID.
     */
    public TransferResponse getTransferById(UUID id) {
        DepartmentTransfer transfer = findTransferById(id);
        return mapToResponse(transfer);
    }

    /**
     * Get all transfers for an encounter.
     */
    public List<TransferResponse> getTransfersByEncounter(UUID encounterId) {
        return transferRepository.findByEncounterIdOrderByTransferRequestedAtDesc(encounterId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get all pending transfers.
     */
    public List<TransferResponse> getPendingTransfers() {
        return transferRepository.findPendingTransfers()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get transfers pending approval.
     */
    public List<TransferResponse> getTransfersPendingApproval() {
        return transferRepository.findTransfersPendingApproval()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get active transfers for a department.
     */
    public List<TransferResponse> getActiveDepartmentTransfers(UUID departmentId) {
        return transferRepository.findActiveDepartmentTransfers(departmentId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get transfers by practitioner.
     */
    public List<TransferResponse> getTransfersByPractitioner(UUID practitionerId, String role) {
        List<DepartmentTransfer> transfers;

        if ("TRANSFERRING".equalsIgnoreCase(role)) {
            transfers = transferRepository.findByTransferringPractitionerIdOrderByTransferRequestedAtDesc(practitionerId);
        } else if ("RECEIVING".equalsIgnoreCase(role)) {
            transfers = transferRepository.findByReceivingPractitionerIdOrderByTransferRequestedAtDesc(practitionerId);
        } else {
            throw new ValidationException("Invalid practitioner role: " + role);
        }

        return transfers.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    // ========== Helper Methods ==========

    /**
     * Find transfer by ID or throw exception.
     */
    private DepartmentTransfer findTransferById(UUID id) {
        return transferRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Transfer", "ID", id));
    }

    /**
     * Find encounter by ID or throw exception.
     */
    private Encounter findEncounterById(UUID id) {
        return encounterRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Encounter", "ID", id));
    }

    /**
     * Validate encounter can be transferred.
     */
    private void validateEncounterForTransfer(Encounter encounter) {
        // Check encounter is active
        if (!encounter.isActive()) {
            throw new EncounterBusinessRuleException(
                "ENCOUNTER_NOT_ACTIVE",
                "Encounter tidak dalam status aktif. Status: " + encounter.getStatus().getIndonesianName()
            );
        }

        // Check encounter type (only inpatient and emergency can be transferred)
        if (encounter.getEncounterType() == EncounterType.OUTPATIENT) {
            throw new EncounterBusinessRuleException(
                "INVALID_ENCOUNTER_TYPE",
                "Pasien rawat jalan tidak dapat ditransfer antar departemen"
            );
        }
    }

    /**
     * Check if encounter has active transfer.
     */
    private boolean hasActiveTransfer(UUID encounterId) {
        return transferRepository.findActiveTransferForEncounter(encounterId).isPresent();
    }

    /**
     * Validate clinical summary requirements.
     */
    private void validateClinicalSummary(TransferRequest request) {
        // For critical/ICU transfers, handover summary is mandatory
        if (request.getTransferType().isICURelated() ||
            request.getTransferType() == TransferType.EMERGENCY ||
            request.getTransferType() == TransferType.STEP_UP) {

            if (request.getHandoverSummary() == null ||
                request.getHandoverSummary().trim().isEmpty()) {
                throw new ValidationException(
                    "Ringkasan klinis (handover summary) wajib diisi untuk transfer ICU/darurat/step-up"
                );
            }
        }
    }

    /**
     * Build transfer entity from request.
     */
    private DepartmentTransfer buildTransferFromRequest(Encounter encounter, TransferRequest request) {
        String transferNumber = generateTransferNumber();

        return DepartmentTransfer.builder()
            .transferNumber(transferNumber)
            .encounter(encounter)
            .patientId(encounter.getPatientId())
            .transferType(request.getTransferType())
            // Source (from current encounter location)
            .fromDepartmentId(encounter.getDepartmentId())
            .fromDepartment(encounter.getCurrentDepartment() != null ?
                encounter.getCurrentDepartment() : "Unknown")
            .fromLocationId(encounter.getLocationId())
            .fromLocation(encounter.getCurrentLocation())
            // Destination
            .toDepartmentId(request.getToDepartmentId())
            .toDepartment(request.getToDepartment())
            .toLocationId(request.getToLocationId())
            .toLocation(request.getToLocation())
            // Request details
            .reasonForTransfer(request.getReasonForTransfer())
            .urgency(request.getUrgency() != null ? request.getUrgency() : "ROUTINE")
            .transferringPractitionerId(request.getTransferringPractitionerId())
            .transferringPractitionerName(request.getTransferringPractitionerName())
            // Handover notes
            .handoverSummary(request.getHandoverSummary())
            .currentCondition(request.getCurrentCondition())
            .activeMedications(request.getActiveMedications())
            .specialInstructions(request.getSpecialInstructions())
            // Transport
            .requiresTransport(request.getRequiresTransport())
            .requiresEquipment(request.getRequiresEquipment())
            .modeOfTransport(request.getModeOfTransport())
            .build();
    }

    /**
     * Update encounter location after transfer completion.
     */
    private void updateEncounterLocation(Encounter encounter, DepartmentTransfer transfer) {
        encounter.setDepartmentId(transfer.getToDepartmentId());
        encounter.setCurrentDepartment(transfer.getToDepartment());
        encounter.setLocationId(transfer.getToLocationId());
        encounter.setCurrentLocation(transfer.getToLocation());

        // Update attending doctor if receiving practitioner is specified
        if (transfer.getReceivingPractitionerId() != null) {
            encounter.setAttendingDoctorId(transfer.getReceivingPractitionerId());
            encounter.setAttendingDoctorName(transfer.getReceivingPractitionerName());
        }

        log.info("Updated encounter location: {} -> {}",
            transfer.getFromDepartment(), transfer.getToDepartment());
    }

    /**
     * Map entity to response DTO.
     */
    private TransferResponse mapToResponse(DepartmentTransfer transfer) {
        Long approvalDuration = null;
        if (transfer.getApprovedAt() != null && transfer.getTransferRequestedAt() != null) {
            approvalDuration = Duration.between(
                transfer.getTransferRequestedAt(),
                transfer.getApprovedAt()
            ).toMinutes();
        }

        Long totalDuration = null;
        if (transfer.getTransferCompletedAt() != null && transfer.getTransferRequestedAt() != null) {
            totalDuration = Duration.between(
                transfer.getTransferRequestedAt(),
                transfer.getTransferCompletedAt()
            ).toMinutes();
        }

        return TransferResponse.builder()
            .id(transfer.getId())
            .transferNumber(transfer.getTransferNumber())
            .encounterId(transfer.getEncounter().getId())
            .patientId(transfer.getPatientId())
            // Transfer details
            .transferType(transfer.getTransferType())
            .transferTypeDisplay(transfer.getTransferType().getDisplayName())
            .transferTypeIndonesian(transfer.getTransferType().getIndonesianName())
            .fromDepartmentId(transfer.getFromDepartmentId())
            .fromDepartment(transfer.getFromDepartment())
            .fromLocationId(transfer.getFromLocationId())
            .fromLocation(transfer.getFromLocation())
            .toDepartmentId(transfer.getToDepartmentId())
            .toDepartment(transfer.getToDepartment())
            .toLocationId(transfer.getToLocationId())
            .toLocation(transfer.getToLocation())
            // Status
            .transferStatus(transfer.getTransferStatus())
            .transferStatusDisplay(transfer.getTransferStatus().getDisplayName())
            .transferStatusIndonesian(transfer.getTransferStatus().getIndonesianName())
            // Timeline
            .transferRequestedAt(transfer.getTransferRequestedAt())
            .approvedAt(transfer.getApprovedAt())
            .transferAcceptedAt(transfer.getTransferAcceptedAt())
            .transferCompletedAt(transfer.getTransferCompletedAt())
            .cancelledAt(transfer.getCancelledAt())
            .approvalDurationMinutes(approvalDuration)
            .totalTransferDurationMinutes(totalDuration)
            // Request details
            .requestedById(transfer.getRequestedById())
            .requestedByName(transfer.getRequestedByName())
            .reasonForTransfer(transfer.getReasonForTransfer())
            .urgency(transfer.getUrgency())
            // Practitioners
            .transferringPractitionerId(transfer.getTransferringPractitionerId())
            .transferringPractitionerName(transfer.getTransferringPractitionerName())
            // Approval
            .requiresApproval(transfer.getRequiresApproval())
            .approvedById(transfer.getApprovedById())
            .approvedByName(transfer.getApprovedByName())
            .approvalNotes(transfer.getApprovalNotes())
            // Acceptance
            .acceptedById(transfer.getAcceptedById())
            .acceptedByName(transfer.getAcceptedByName())
            .rejectionReason(transfer.getRejectionReason())
            // Receiving team
            .receivingPractitionerId(transfer.getReceivingPractitionerId())
            .receivingPractitionerName(transfer.getReceivingPractitionerName())
            .receivingDoctorId(transfer.getReceivingDoctorId())
            .receivingDoctorName(transfer.getReceivingDoctorName())
            .receivingNurseId(transfer.getReceivingNurseId())
            .receivingNurseName(transfer.getReceivingNurseName())
            // Handover
            .handoverSummary(transfer.getHandoverSummary())
            .currentCondition(transfer.getCurrentCondition())
            .activeMedications(transfer.getActiveMedications())
            .specialInstructions(transfer.getSpecialInstructions())
            // Transport
            .requiresTransport(transfer.getRequiresTransport())
            .requiresEquipment(transfer.getRequiresEquipment())
            .modeOfTransport(transfer.getModeOfTransport())
            // Cancellation
            .cancelledBy(transfer.getCancelledBy())
            .cancellationReason(transfer.getCancellationReason())
            // Computed flags
            .isActive(transfer.isActive())
            .isCompleted(transfer.isCompleted())
            .isPending(transfer.isPending())
            .canBeCancelled(transfer.canBeCancelled())
            .needsApproval(transfer.needsApproval())
            // Audit
            .createdAt(transfer.getCreatedAt())
            .updatedAt(transfer.getUpdatedAt())
            .createdBy(transfer.getCreatedBy())
            .updatedBy(transfer.getUpdatedBy())
            .build();
    }

    /**
     * Generate unique transfer number.
     */
    private String generateTransferNumber() {
        String prefix = "TRF";
        String datePart = java.time.LocalDate.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        );

        // Get count of today's transfers (simple sequence)
        long count = transferRepository.count() + 1;

        return String.format("%s-%s-%04d", prefix, datePart, count);
    }
}
