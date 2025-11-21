package com.yudha.hms.laboratory.constant;

/**
 * Specimen Status Enumeration.
 *
 * Status of specimen throughout collection and processing workflow.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum SpecimenStatus {
    /**
     * Awaiting collection
     */
    PENDING("Pending", "Awaiting collection"),

    /**
     * Collected from patient
     */
    COLLECTED("Collected", "Collected from patient"),

    /**
     * Received by laboratory
     */
    RECEIVED("Received", "Received by laboratory"),

    /**
     * Processing in progress
     */
    PROCESSING("Processing", "Processing in progress"),

    /**
     * Processing completed
     */
    COMPLETED("Completed", "Processing completed"),

    /**
     * Rejected due to quality issues
     */
    REJECTED("Rejected", "Rejected due to quality issues"),

    /**
     * Discarded after testing
     */
    DISCARDED("Discarded", "Discarded after testing");

    private final String displayName;
    private final String description;

    SpecimenStatus(String displayName, String description) {
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
     * Check if specimen can be processed
     */
    public boolean canBeProcessed() {
        return this == RECEIVED || this == PROCESSING;
    }

    /**
     * Check if specimen is in acceptable state
     */
    public boolean isAcceptable() {
        return this != REJECTED && this != DISCARDED;
    }
}