package com.yudha.hms.clinical.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Encounter Status Enum with State Machine validation.
 *
 * Allowed transitions:
 * - PLANNED -> ARRIVED, CANCELLED
 * - ARRIVED -> TRIAGED, CANCELLED
 * - TRIAGED -> IN_PROGRESS, CANCELLED
 * - IN_PROGRESS -> FINISHED
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Getter
public enum EncounterStatus {
    PLANNED("Planned", "Direncanakan", 1),
    ARRIVED("Arrived", "Tiba", 2),
    TRIAGED("Triaged", "Sudah Triase", 3),
    IN_PROGRESS("In Progress", "Sedang Berlangsung", 4),
    FINISHED("Finished", "Selesai", 5),
    CANCELLED("Cancelled", "Dibatalkan", 6);

    private final String displayName;
    private final String indonesianName;
    private final int order;

    EncounterStatus(String displayName, String indonesianName, int order) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.order = order;
    }

    /**
     * Check if status represents an active encounter.
     */
    public boolean isActive() {
        return this == PLANNED || this == ARRIVED || this == TRIAGED || this == IN_PROGRESS;
    }

    /**
     * Check if status represents a completed encounter.
     */
    public boolean isCompleted() {
        return this == FINISHED;
    }

    /**
     * Check if status represents a cancelled encounter.
     */
    public boolean isCancelled() {
        return this == CANCELLED;
    }

    /**
     * Check if this status can transition to the target status.
     *
     * @param targetStatus the target status to transition to
     * @return true if transition is allowed, false otherwise
     */
    public boolean canTransitionTo(EncounterStatus targetStatus) {
        if (targetStatus == null) {
            return false;
        }

        // Cannot transition from terminal states
        if (this == FINISHED || this == CANCELLED) {
            return false;
        }

        // Same status is not a valid transition
        if (this == targetStatus) {
            return false;
        }

        // Define allowed transitions using switch
        return switch (this) {
            case PLANNED -> targetStatus == ARRIVED || targetStatus == CANCELLED;
            case ARRIVED -> targetStatus == TRIAGED || targetStatus == CANCELLED;
            case TRIAGED -> targetStatus == IN_PROGRESS || targetStatus == CANCELLED;
            case IN_PROGRESS -> targetStatus == FINISHED;
            default -> false;
        };
    }

    /**
     * Get list of valid next statuses from current status.
     *
     * @return list of valid next statuses
     */
    public List<EncounterStatus> getAllowedTransitions() {
        return switch (this) {
            case PLANNED -> Arrays.asList(ARRIVED, CANCELLED);
            case ARRIVED -> Arrays.asList(TRIAGED, CANCELLED);
            case TRIAGED -> Arrays.asList(IN_PROGRESS, CANCELLED);
            case IN_PROGRESS -> List.of(FINISHED);
            case FINISHED, CANCELLED -> List.of(); // Terminal states - no transitions
        };
    }

    /**
     * Check if this status can be cancelled.
     *
     * @return true if cancellation is allowed
     */
    public boolean canBeCancelled() {
        return this == PLANNED || this == ARRIVED || this == TRIAGED;
    }

    /**
     * Check if encounter is in a terminal state.
     *
     * @return true if status is terminal (FINISHED or CANCELLED)
     */
    public boolean isTerminal() {
        return this == FINISHED || this == CANCELLED;
    }

    /**
     * Get user-friendly error message for invalid transition (English).
     *
     * @param targetStatus the attempted target status
     * @return error message
     */
    public String getTransitionErrorMessage(EncounterStatus targetStatus) {
        if (targetStatus == null) {
            return "Target status cannot be null";
        }

        if (this == targetStatus) {
            return String.format("Encounter is already in %s status", displayName);
        }

        if (isTerminal()) {
            return String.format("Cannot change status from %s (terminal state)", displayName);
        }

        List<EncounterStatus> allowed = getAllowedTransitions();
        if (allowed.isEmpty()) {
            return String.format("No transitions allowed from %s", displayName);
        }

        String allowedNames = String.join(", ",
            allowed.stream().map(EncounterStatus::getDisplayName).toList());

        return String.format("Cannot transition from %s to %s. Allowed transitions: %s",
            displayName, targetStatus.getDisplayName(), allowedNames);
    }

    /**
     * Get user-friendly error message for invalid transition (Indonesian).
     *
     * @param targetStatus the attempted target status
     * @return error message in Indonesian
     */
    public String getTransitionErrorMessageIndonesian(EncounterStatus targetStatus) {
        if (targetStatus == null) {
            return "Status tujuan tidak boleh kosong";
        }

        if (this == targetStatus) {
            return String.format("Encounter sudah dalam status %s", indonesianName);
        }

        if (isTerminal()) {
            return String.format("Tidak dapat mengubah status dari %s (status terminal)", indonesianName);
        }

        List<EncounterStatus> allowed = getAllowedTransitions();
        if (allowed.isEmpty()) {
            return String.format("Tidak ada transisi yang diizinkan dari %s", indonesianName);
        }

        String allowedNames = String.join(", ",
            allowed.stream().map(EncounterStatus::getIndonesianName).toList());

        return String.format("Tidak dapat mengubah status dari %s ke %s. Transisi yang diizinkan: %s",
            indonesianName, targetStatus.getIndonesianName(), allowedNames);
    }
}
