package com.yudha.hms.clinical.entity;

/**
 * Referral Status Enum.
 *
 * Represents the status of a referral throughout its lifecycle.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
public enum ReferralStatus {
    DRAFT("Draft", "Draft", "Referral being prepared", 1),
    PENDING_SIGNATURE("Pending Signature", "Menunggu Tanda Tangan", "Waiting for doctor's signature", 2),
    SIGNED("Signed", "Ditandatangani", "Referral signed and ready to send", 3),
    SENT("Sent", "Terkirim", "Referral sent to receiving facility", 4),
    ACCEPTED("Accepted", "Diterima", "Receiving facility accepted the referral", 5),
    REJECTED("Rejected", "Ditolak", "Receiving facility rejected the referral", 6),
    PATIENT_TRANSFERRED("Patient Transferred", "Pasien Dipindahkan", "Patient has been transferred", 7),
    COMPLETED("Completed", "Selesai", "Referral process completed", 8),
    CANCELLED("Cancelled", "Dibatalkan", "Referral cancelled", 9);

    private final String displayName;
    private final String indonesianName;
    private final String description;
    private final int order;

    ReferralStatus(String displayName, String indonesianName, String description, int order) {
        this.displayName = displayName;
        this.indonesianName = indonesianName;
        this.description = description;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIndonesianName() {
        return indonesianName;
    }

    public String getDescription() {
        return description;
    }

    public int getOrder() {
        return order;
    }

    public boolean canBeCancelled() {
        return this == DRAFT || this == PENDING_SIGNATURE || this == SIGNED || this == SENT;
    }

    public boolean isPending() {
        return this == DRAFT || this == PENDING_SIGNATURE || this == SIGNED || this == SENT;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public boolean isTerminated() {
        return this == REJECTED || this == CANCELLED || this == COMPLETED;
    }

    public boolean isActive() {
        return !isTerminated();
    }

    public boolean canTransitionTo(ReferralStatus targetStatus) {
        return switch (this) {
            case DRAFT -> targetStatus == PENDING_SIGNATURE || targetStatus == CANCELLED;
            case PENDING_SIGNATURE -> targetStatus == SIGNED || targetStatus == CANCELLED;
            case SIGNED -> targetStatus == SENT || targetStatus == CANCELLED;
            case SENT -> targetStatus == ACCEPTED || targetStatus == REJECTED;
            case ACCEPTED -> targetStatus == PATIENT_TRANSFERRED || targetStatus == CANCELLED;
            case PATIENT_TRANSFERRED -> targetStatus == COMPLETED;
            default -> false;
        };
    }

    public String getTransitionErrorMessage(ReferralStatus targetStatus) {
        return String.format(
            "Cannot transition referral status from %s to %s",
            this.displayName, targetStatus.displayName
        );
    }
}
