package com.yudha.hms.clinical.exception;

import com.yudha.hms.clinical.entity.EncounterStatus;

/**
 * Exception thrown when an invalid encounter status transition is attempted.
 *
 * This exception enforces the encounter status state machine rules.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public class InvalidStatusTransitionException extends RuntimeException {

    private final EncounterStatus fromStatus;
    private final EncounterStatus toStatus;

    public InvalidStatusTransitionException(EncounterStatus fromStatus, EncounterStatus toStatus) {
        super(fromStatus.getTransitionErrorMessageIndonesian(toStatus));
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public InvalidStatusTransitionException(EncounterStatus fromStatus, EncounterStatus toStatus, String message) {
        super(message);
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    public InvalidStatusTransitionException(String message) {
        super(message);
        this.fromStatus = null;
        this.toStatus = null;
    }

    public EncounterStatus getFromStatus() {
        return fromStatus;
    }

    public EncounterStatus getToStatus() {
        return toStatus;
    }

    /**
     * Get English error message.
     */
    public String getEnglishMessage() {
        if (fromStatus != null && toStatus != null) {
            return fromStatus.getTransitionErrorMessage(toStatus);
        }
        return getMessage();
    }

    /**
     * Get Indonesian error message.
     */
    public String getIndonesianMessage() {
        if (fromStatus != null && toStatus != null) {
            return fromStatus.getTransitionErrorMessageIndonesian(toStatus);
        }
        return getMessage();
    }
}
