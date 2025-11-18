package com.yudha.hms.registration.entity;

/**
 * Status of inpatient admission.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public enum AdmissionStatus {
    ADMITTED("Admitted - Patient currently admitted"),
    IN_TREATMENT("In treatment - Receiving care"),
    DISCHARGED("Discharged - Patient left hospital"),
    TRANSFERRED("Transferred - Moved to another facility"),
    DECEASED("Deceased"),
    CANCELLED("Cancelled - Admission cancelled");

    private final String description;

    AdmissionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
