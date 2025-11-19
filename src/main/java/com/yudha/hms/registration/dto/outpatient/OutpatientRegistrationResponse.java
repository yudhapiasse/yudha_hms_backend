package com.yudha.hms.registration.dto.outpatient;

import com.yudha.hms.registration.entity.PaymentMethod;
import com.yudha.hms.registration.entity.RegistrationStatus;
import com.yudha.hms.registration.entity.RegistrationType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for outpatient registration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutpatientRegistrationResponse {

    // ========== Registration Identity ==========
    private UUID id;
    private String registrationNumber;

    // ========== Patient Information ==========
    private UUID patientId;
    private String patientName;
    private String patientMrn;

    // ========== Polyclinic and Doctor ==========
    private UUID polyclinicId;
    private String polyclinicCode;
    private String polyclinicName;
    private String polyclinicLocation;

    private UUID doctorId;
    private String doctorName;
    private String doctorTitle;
    private String doctorSpecialization;

    // ========== Registration Details ==========
    private LocalDate registrationDate;
    private LocalDateTime registrationTime;
    private RegistrationType registrationType;
    private String registrationTypeDisplay;

    // ========== Appointment Details ==========
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private LocalTime appointmentEndTime;

    // ========== Queue Information ==========
    private Integer queueNumber;
    private String queueCode;
    private Integer estimatedWaitTimeMinutes;
    private String queueStatus; // "Current", "Waiting", "Completed"

    // ========== Payment ==========
    private PaymentMethod paymentMethod;
    private String paymentMethodDisplay;
    private Boolean isBpjs;
    private String bpjsCardNumber;
    private BigDecimal registrationFee;
    private BigDecimal consultationFee;
    private BigDecimal totalFee;

    // ========== Status ==========
    private RegistrationStatus status;
    private String statusDisplay;

    // ========== Visit Reason ==========
    private String chiefComplaint;
    private String notes;

    // ========== Referral ==========
    private String referralFrom;
    private String referralLetterNumber;

    // ========== Timestamps ==========
    private LocalDateTime checkInTime;
    private LocalDateTime consultationStartTime;
    private LocalDateTime consultationEndTime;

    // ========== Cancellation ==========
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String cancelledBy;

    // ========== Audit ==========
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // ========== Helper Methods ==========

    /**
     * Get formatted queue display.
     */
    public String getQueueDisplay() {
        if (queueCode != null) {
            return queueCode;
        }
        if (queueNumber != null) {
            return String.format("Queue #%d", queueNumber);
        }
        return "No queue assigned";
    }

    /**
     * Get estimated wait time display.
     */
    public String getWaitTimeDisplay() {
        if (estimatedWaitTimeMinutes == null) {
            return "N/A";
        }
        if (estimatedWaitTimeMinutes < 60) {
            return estimatedWaitTimeMinutes + " minutes";
        }
        int hours = estimatedWaitTimeMinutes / 60;
        int minutes = estimatedWaitTimeMinutes % 60;
        return String.format("%d hour%s %d minute%s",
            hours, hours > 1 ? "s" : "",
            minutes, minutes > 1 ? "s" : "");
    }

    /**
     * Get fee display.
     */
    public String getTotalFeeDisplay() {
        if (totalFee == null || totalFee.compareTo(BigDecimal.ZERO) == 0) {
            return "Free (BPJS)";
        }
        return String.format("Rp %,.2f", totalFee);
    }
}