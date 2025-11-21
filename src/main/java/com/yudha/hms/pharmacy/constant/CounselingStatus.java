package com.yudha.hms.pharmacy.constant;

import lombok.Getter;

/**
 * Patient Counseling Status Enum.
 *
 * Represents the status of patient counseling for medication.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Getter
public enum CounselingStatus {
    PENDING("Menunggu", "PENDING", "Menunggu konseling pasien"),
    IN_PROGRESS("Sedang Berlangsung", "PROGRESS", "Sedang melakukan konseling"),
    COMPLETED("Selesai", "DONE", "Konseling telah selesai"),
    DECLINED("Ditolak", "DECLINED", "Pasien menolak konseling"),
    NOT_REQUIRED("Tidak Diperlukan", "NOT_REQ", "Konseling tidak diperlukan"),
    RESCHEDULED("Dijadwal Ulang", "RESCHED", "Konseling dijadwal ulang");

    private final String displayName;
    private final String code;
    private final String description;

    CounselingStatus(String displayName, String code, String description) {
        this.displayName = displayName;
        this.code = code;
        this.description = description;
    }

    /**
     * Check if counseling is pending
     */
    public boolean isPending() {
        return this == PENDING || this == RESCHEDULED;
    }

    /**
     * Check if counseling is complete
     */
    public boolean isComplete() {
        return this == COMPLETED || this == DECLINED || this == NOT_REQUIRED;
    }

    /**
     * Check if counseling can be started
     */
    public boolean canBeStarted() {
        return this == PENDING || this == RESCHEDULED;
    }

    /**
     * Check if dispensing can proceed without counseling
     */
    public boolean allowsDispensing() {
        return this == COMPLETED || this == DECLINED || this == NOT_REQUIRED;
    }
}
