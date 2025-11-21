package com.yudha.hms.laboratory.constant;

/**
 * Lab Report Status Enumeration.
 *
 * Status of laboratory reports.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ReportStatus {
    /**
     * Draft report
     */
    DRAFT("Draft", "Draft report"),

    /**
     * Final report
     */
    FINAL("Final", "Final report"),

    /**
     * Revised report
     */
    REVISED("Revised", "Revised report"),

    /**
     * Cancelled report
     */
    CANCELLED("Cancelled", "Cancelled report");

    private final String displayName;
    private final String description;

    ReportStatus(String displayName, String description) {
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
     * Check if report is final
     */
    public boolean isFinal() {
        return this == FINAL || this == REVISED;
    }
}
