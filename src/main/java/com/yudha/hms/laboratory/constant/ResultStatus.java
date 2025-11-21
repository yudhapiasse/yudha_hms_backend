package com.yudha.hms.laboratory.constant;

/**
 * Lab Result Status Enumeration.
 *
 * Status of laboratory results throughout the workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ResultStatus {
    /**
     * Result entry pending
     */
    PENDING("Pending", "Result entry pending"),

    /**
     * Preliminary results available (not yet validated)
     */
    PRELIMINARY("Preliminary", "Preliminary results available (not yet validated)"),

    /**
     * Final validated results
     */
    FINAL("Final", "Final validated results"),

    /**
     * Result has been amended
     */
    AMENDED("Amended", "Result has been amended"),

    /**
     * Result has been corrected
     */
    CORRECTED("Corrected", "Result has been corrected"),

    /**
     * Result cancelled
     */
    CANCELLED("Cancelled", "Result cancelled"),

    /**
     * Result entered in error
     */
    ENTERED_IN_ERROR("Entered in Error", "Result entered in error");

    private final String displayName;
    private final String description;

    ResultStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if result is final and reportable
     */
    public boolean isFinal() {
        return this == FINAL || this == AMENDED || this == CORRECTED;
    }

    /**
     * Check if result can be amended
     */
    public boolean canBeAmended() {
        return this == FINAL || this == PRELIMINARY;
    }
}
