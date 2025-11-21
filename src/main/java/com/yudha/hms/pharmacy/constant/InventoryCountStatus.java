package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Inventory Count Status enumeration.
 *
 * Status of physical inventory counting sessions (stock opname).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum InventoryCountStatus {
    PLANNING("Perencanaan", "PLAN", "Sedang dalam tahap perencanaan"),
    SCHEDULED("Dijadwalkan", "SCHED", "Sudah dijadwalkan"),
    IN_PROGRESS("Sedang Berlangsung", "PROGRESS", "Sedang dilakukan penghitungan"),
    COMPLETED("Selesai", "COMPLETE", "Penghitungan selesai"),
    UNDER_REVIEW("Dalam Review", "REVIEW", "Sedang direview"),
    APPROVED("Disetujui", "APPROVED", "Disetujui dan penyesuaian dibuat"),
    REJECTED("Ditolak", "REJECTED", "Ditolak, perlu penghitungan ulang"),
    CANCELLED("Dibatalkan", "CANCELLED", "Dibatalkan");

    private final String displayName;
    private final String code;
    private final String description;

    InventoryCountStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    public static InventoryCountStatus fromCode(String code) {
        for (InventoryCountStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown inventory count status code: " + code);
    }

    /**
     * Check if count can be edited
     */
    public boolean isEditable() {
        return this == PLANNING || this == SCHEDULED;
    }

    /**
     * Check if count can be started
     */
    public boolean canBeStarted() {
        return this == SCHEDULED;
    }

    /**
     * Check if items can be counted
     */
    public boolean canCountItems() {
        return this == IN_PROGRESS;
    }

    /**
     * Check if count can be completed
     */
    public boolean canBeCompleted() {
        return this == IN_PROGRESS;
    }

    /**
     * Check if count can be reviewed
     */
    public boolean canBeReviewed() {
        return this == COMPLETED;
    }

    /**
     * Check if count can be approved
     */
    public boolean canBeApproved() {
        return this == UNDER_REVIEW;
    }

    /**
     * Check if count is in final state
     */
    public boolean isFinal() {
        return this == APPROVED || this == REJECTED || this == CANCELLED;
    }

    /**
     * Check if count generates adjustments
     */
    public boolean generatesAdjustments() {
        return this == APPROVED;
    }

    /**
     * Check if count is active
     */
    public boolean isActive() {
        return !isFinal();
    }
}
