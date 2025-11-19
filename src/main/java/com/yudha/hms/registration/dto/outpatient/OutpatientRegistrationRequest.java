package com.yudha.hms.registration.dto.outpatient;

import com.yudha.hms.registration.entity.PaymentMethod;
import com.yudha.hms.registration.entity.RegistrationType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Request DTO for outpatient registration.
 * Supports both walk-in and appointment registrations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutpatientRegistrationRequest {

    // ========== Patient Information ==========
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Polyclinic and Doctor ==========
    @NotNull(message = "Polyclinic ID is required")
    private UUID polyclinicId;

    @NotNull(message = "Doctor ID is required")
    private UUID doctorId;

    // ========== Registration Type ==========
    @NotNull(message = "Registration type is required")
    private RegistrationType registrationType; // WALK_IN or APPOINTMENT

    // ========== Appointment Details (required for APPOINTMENT type) ==========
    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    // ========== Payment Information ==========
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Builder.Default
    private Boolean isBpjs = false;

    private String bpjsCardNumber;

    // ========== Visit Reason ==========
    @NotBlank(message = "Chief complaint is required")
    @Size(min = 10, max = 1000, message = "Chief complaint must be between 10 and 1000 characters")
    private String chiefComplaint;

    private String notes;

    // ========== Referral Information ==========
    private String referralFrom;

    private String referralLetterNumber;

    // ========== Validation Methods ==========

    /**
     * Validate appointment details are provided for APPOINTMENT type.
     */
    public boolean isAppointmentDataValid() {
        if (registrationType == RegistrationType.APPOINTMENT) {
            return appointmentDate != null && appointmentTime != null;
        }
        return true;
    }

    /**
     * Validate BPJS card number is provided if isBpjs is true.
     */
    public boolean isBpjsDataValid() {
        if (Boolean.TRUE.equals(isBpjs)) {
            return bpjsCardNumber != null && !bpjsCardNumber.trim().isEmpty();
        }
        return true;
    }
}