package com.yudha.hms.clinical.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Transfer Status Enum with State Machine validation.
 *
 * Allowed transitions:
 * - REQUESTED -> PENDING_APPROVAL, ACCEPTED, REJECTED, CANCELLED
 * - PENDING_APPROVAL -> APPROVED, REJECTED, CANCELLED
 * - APPROVED -> ACCEPTED, CANCELLED
 * - ACCEPTED -> IN_TRANSIT, CANCELLED
 * - IN_TRANSIT -> COMPLETED
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum TransferStatus {
    REQUESTED("Requested", "Diminta", 1),
    PENDING_APPROVAL("Pending Approval", "Menunggu Persetujuan", 2),
    APPROVED("Approved", "Disetujui", 3),
    ACCEPTED("Accepted", "Diterima", 4),
    IN_TRANSIT("In Transit", "Dalam Perjalanan", 5),
    COMPLETED("Completed", "Selesai", 6),
    REJECTED("Rejected", "Ditolak", 7),
    CANCELLED("Cancelled", "Dibatalkan", 8);

    private final String displayName;
    private final String indonesianName;
    private final int order;

    TransferStatus(String displayName, String indonesianName, int order) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.order = order;
    }

    /**
     * Check if status represents an active transfer.
     */
    public boolean isActive() {
        return this == REQUESTED || this == PENDING_APPROVAL ||
               this == APPROVED || this == ACCEPTED || this == IN_TRANSIT;
    }

    /**
     * Check if status represents a completed transfer.
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Check if status represents a cancelled/rejected transfer.
     */
    public boolean isTerminated() {
        return this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if status is terminal (cannot transition further).
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if status is pending (not yet accepted).
     */
    public boolean isPending() {
        return this == REQUESTED || this == PENDING_APPROVAL;
    }

    /**
     * Check if this status can transition to the target status.
     *
     * @param targetStatus the target status to transition to
     * @return true if transition is allowed
     */
    public boolean canTransitionTo(TransferStatus targetStatus) {
        if (targetStatus == null || this == targetStatus) {
            return false;
        }

        if (isTerminal()) {
            return false;
        }

        return switch (this) {
            case REQUESTED -> targetStatus == PENDING_APPROVAL ||
                             targetStatus == ACCEPTED ||
                             targetStatus == REJECTED ||
                             targetStatus == CANCELLED;
            case PENDING_APPROVAL -> targetStatus == APPROVED ||
                                    targetStatus == REJECTED ||
                                    targetStatus == CANCELLED;
            case APPROVED -> targetStatus == ACCEPTED || targetStatus == CANCELLED;
            case ACCEPTED -> targetStatus == IN_TRANSIT || targetStatus == CANCELLED;
            case IN_TRANSIT -> targetStatus == COMPLETED;
            default -> false;
        };
    }

    /**
     * Get list of valid next statuses from current status.
     *
     * @return list of valid next statuses
     */
    public List<TransferStatus> getAllowedTransitions() {
        return switch (this) {
            case REQUESTED -> Arrays.asList(PENDING_APPROVAL, ACCEPTED, REJECTED, CANCELLED);
            case PENDING_APPROVAL -> Arrays.asList(APPROVED, REJECTED, CANCELLED);
            case APPROVED -> Arrays.asList(ACCEPTED, CANCELLED);
            case ACCEPTED -> Arrays.asList(IN_TRANSIT, CANCELLED);
            case IN_TRANSIT -> List.of(COMPLETED);
            case COMPLETED, REJECTED, CANCELLED -> List.of(); // Terminal states
        };
    }

    /**
     * Check if transfer can be cancelled from this status.
     *
     * @return true if cancellation is allowed
     */
    public boolean canBeCancelled() {
        return this == REQUESTED || this == PENDING_APPROVAL ||
               this == APPROVED || this == ACCEPTED;
    }

    /**
     * Get user-friendly error message for invalid transition (Indonesian).
     *
     * @param targetStatus the attempted target status
     * @return error message in Indonesian
     */
    public String getTransitionErrorMessage(TransferStatus targetStatus) {
        if (targetStatus == null) {
            return "Status tujuan tidak boleh kosong";
        }

        if (this == targetStatus) {
            return String.format("Transfer sudah dalam status %s", indonesianName);
        }

        if (isTerminal()) {
            return String.format("Tidak dapat mengubah status dari %s (status terminal)",
                indonesianName);
        }

        List<TransferStatus> allowed = getAllowedTransitions();
        if (allowed.isEmpty()) {
            return String.format("Tidak ada transisi yang diizinkan dari %s", indonesianName);
        }

        String allowedNames = String.join(", ",
            allowed.stream().map(TransferStatus::getIndonesianName).toList());

        return String.format("Tidak dapat mengubah status dari %s ke %s. " +
            "Transisi yang diizinkan: %s",
            indonesianName, targetStatus.getIndonesianName(), allowedNames);
    }
}
