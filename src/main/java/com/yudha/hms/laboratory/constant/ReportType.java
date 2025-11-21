package com.yudha.hms.laboratory.constant;

/**
 * Lab Report Type Enumeration.
 *
 * Types of laboratory reports.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
public enum ReportType {
    /**
     * Single test report
     */
    SINGLE_TEST("Single Test", "Single test report"),

    /**
     * Cumulative report for inpatients
     */
    CUMULATIVE("Cumulative", "Cumulative report for inpatients"),

    /**
     * Trend analysis report
     */
    TREND_ANALYSIS("Trend Analysis", "Graphical trend analysis report"),

    /**
     * Quality control report
     */
    QUALITY_CONTROL("Quality Control", "Quality control report"),

    /**
     * Test utilization statistics report
     */
    UTILIZATION("Utilization", "Test utilization statistics report"),

    /**
     * TAT monitoring report
     */
    TAT_MONITORING("TAT Monitoring", "Turnaround time monitoring report");

    private final String displayName;
    private final String description;

    ReportType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
