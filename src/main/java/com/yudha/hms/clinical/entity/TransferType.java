package com.yudha.hms.clinical.entity;

import lombok.Getter;

/**
 * Transfer Type Enum.
 *
 * Defines types of department transfers based on clinical urgency and care level changes.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Getter
public enum TransferType {
    ROUTINE(
        "Routine Transfer",
        "Transfer Rutin",
        "Scheduled transfer between departments for continuing care",
        false,
        false
    ),
    EMERGENCY(
        "Emergency Transfer",
        "Transfer Darurat",
        "Urgent transfer due to clinical deterioration or emergency",
        true,
        true
    ),
    STEP_UP(
        "Step-Up Transfer",
        "Transfer Naik Tingkat",
        "Transfer to higher level of care (e.g., Ward to ICU)",
        true,
        true
    ),
    STEP_DOWN(
        "Step-Down Transfer",
        "Transfer Turun Tingkat",
        "Transfer to lower level of care (e.g., ICU to Ward)",
        false,
        false
    ),
    INTERNAL(
        "Internal Transfer",
        "Transfer Internal",
        "Transfer within same department to different location/bed",
        false,
        false
    ),
    EXTERNAL(
        "External Transfer",
        "Transfer Eksternal",
        "Transfer to another healthcare facility",
        true,
        true
    ),
    ICU_ADMISSION(
        "ICU Admission",
        "Masuk ICU",
        "Transfer to Intensive Care Unit",
        true,
        true
    ),
    ICU_DISCHARGE(
        "ICU Discharge",
        "Keluar ICU",
        "Discharge from Intensive Care Unit to ward",
        false,
        true
    ),
    OPERATING_ROOM(
        "Operating Room Transfer",
        "Transfer ke Kamar Operasi",
        "Transfer to operating room for surgery",
        true,
        false
    ),
    POST_OPERATIVE(
        "Post-Operative Transfer",
        "Transfer Pasca Operasi",
        "Transfer from recovery room after surgery",
        false,
        false
    );

    private final String displayName;
    private final String indonesianName;
    private final String description;
    private final boolean requiresApproval;
    private final boolean isUrgent;

    TransferType(String displayName, String indonesianName, String description,
                 boolean requiresApproval, boolean isUrgent) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
        this.requiresApproval = requiresApproval;
        this.isUrgent = isUrgent;
    }

    /**
     * Check if this transfer type requires supervisor/specialist approval.
     *
     * @return true if approval is required
     */
    public boolean requiresApproval() {
        return requiresApproval;
    }

    /**
     * Check if this transfer type is urgent.
     *
     * @return true if urgent
     */
    public boolean isUrgent() {
        return isUrgent;
    }

    /**
     * Check if this is a step-up transfer (to higher acuity).
     *
     * @return true if step-up
     */
    public boolean isStepUp() {
        return this == STEP_UP || this == ICU_ADMISSION;
    }

    /**
     * Check if this is a step-down transfer (to lower acuity).
     *
     * @return true if step-down
     */
    public boolean isStepDown() {
        return this == STEP_DOWN || this == ICU_DISCHARGE;
    }

    /**
     * Check if this transfer involves ICU.
     *
     * @return true if ICU related
     */
    public boolean isICURelated() {
        return this == ICU_ADMISSION || this == ICU_DISCHARGE || this == STEP_UP;
    }

    /**
     * Check if this is an external facility transfer.
     *
     * @return true if external
     */
    public boolean isExternal() {
        return this == EXTERNAL;
    }
}
