package com.yudha.hms.registration.entity;

/**
 * Type of inpatient admission.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public enum AdmissionType {
    ELECTIVE("Elective - Scheduled admission"),
    EMERGENCY("Emergency admission"),
    TRANSFER("Transfer from another facility"),
    OBSERVATION("Observation/short stay");

    private final String description;

    AdmissionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
