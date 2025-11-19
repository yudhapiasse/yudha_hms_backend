package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Outpatient Registration Entity.
 *
 * Represents patient registrations for outpatient polyclinics.
 * Supports both walk-in and appointment-based registrations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "outpatient_registration", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_registration_number", columnList = "registration_number"),
        @Index(name = "idx_registration_patient", columnList = "patient_id"),
        @Index(name = "idx_registration_date", columnList = "registration_date"),
        @Index(name = "idx_registration_polyclinic", columnList = "polyclinic_id"),
        @Index(name = "idx_registration_doctor", columnList = "doctor_id"),
        @Index(name = "idx_registration_status", columnList = "status"),
        @Index(name = "idx_registration_type", columnList = "registration_type"),
        @Index(name = "idx_registration_queue", columnList = "queue_number")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Outpatient registration records")
public class OutpatientRegistration extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Registration Number ==========
    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Registration number is required")
    private String registrationNumber; // REG-20250119-0001

    // ========== Patient Reference ==========
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Polyclinic and Doctor ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polyclinic_id", nullable = false)
    @NotNull(message = "Polyclinic is required")
    private Polyclinic polyclinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_schedule_id")
    private DoctorSchedule doctorSchedule;

    // ========== Registration Details ==========
    @Column(name = "registration_date", nullable = false)
    @NotNull(message = "Registration date is required")
    private LocalDate registrationDate;

    @Column(name = "registration_time", nullable = false)
    @NotNull(message = "Registration time is required")
    @Builder.Default
    private LocalDateTime registrationTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type", nullable = false, length = 20)
    @NotNull(message = "Registration type is required")
    private RegistrationType registrationType; // WALK_IN, APPOINTMENT

    // ========== Appointment Details (for APPOINTMENT type) ==========
    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "appointment_time")
    private LocalTime appointmentTime;

    @Column(name = "appointment_end_time")
    private LocalTime appointmentEndTime;

    // ========== Payment ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod; // CASH, BPJS, INSURANCE, DEBIT, CREDIT

    @Column(name = "is_bpjs")
    @Builder.Default
    private Boolean isBpjs = false;

    @Column(name = "bpjs_card_number", length = 50)
    private String bpjsCardNumber;

    @Column(name = "registration_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal registrationFee = BigDecimal.ZERO;

    @Column(name = "consultation_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal consultationFee = BigDecimal.ZERO;

    @Column(name = "total_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalFee = BigDecimal.ZERO;

    // ========== Status ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Status is required")
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.REGISTERED;

    // ========== Queue ==========
    @Column(name = "queue_number")
    private Integer queueNumber;

    @Column(name = "queue_code", length = 10)
    private String queueCode; // e.g., "A001", "B015"

    // ========== Visit Reason ==========
    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Referral ==========
    @Column(name = "referral_from", length = 100)
    private String referralFrom;

    @Column(name = "referral_letter_number", length = 50)
    private String referralLetterNumber;

    // ========== Timestamps ==========
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "consultation_start_time")
    private LocalDateTime consultationStartTime;

    @Column(name = "consultation_end_time")
    private LocalDateTime consultationEndTime;

    // ========== Cancellation ==========
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    // ========== Business Methods ==========

    /**
     * Check if this is an appointment registration.
     */
    public boolean isAppointment() {
        return registrationType == RegistrationType.APPOINTMENT;
    }

    /**
     * Check if this is a walk-in registration.
     */
    public boolean isWalkIn() {
        return registrationType == RegistrationType.WALK_IN;
    }

    /**
     * Check if patient is a BPJS patient.
     */
    public boolean isBpjsPatient() {
        return Boolean.TRUE.equals(isBpjs);
    }

    /**
     * Calculate total fee from registration and consultation fees.
     */
    public void calculateTotalFee() {
        BigDecimal reg = registrationFee != null ? registrationFee : BigDecimal.ZERO;
        BigDecimal cons = consultationFee != null ? consultationFee : BigDecimal.ZERO;
        this.totalFee = reg.add(cons);
    }

    /**
     * Mark patient as checked in.
     */
    public void checkIn() {
        this.checkInTime = LocalDateTime.now();
        this.status = RegistrationStatus.WAITING;
    }

    /**
     * Start consultation.
     */
    public void startConsultation() {
        this.consultationStartTime = LocalDateTime.now();
        this.status = RegistrationStatus.IN_CONSULTATION;
    }

    /**
     * Complete consultation.
     */
    public void completeConsultation() {
        this.consultationEndTime = LocalDateTime.now();
        this.status = RegistrationStatus.COMPLETED;
    }

    /**
     * Cancel registration.
     */
    public void cancel(String reason, String cancelledByUser) {
        this.status = RegistrationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancelledBy = cancelledByUser;
    }

    /**
     * Get estimated wait time in minutes.
     */
    public Integer getEstimatedWaitTimeMinutes() {
        if (checkInTime != null && consultationStartTime == null) {
            Duration duration = Duration.between(checkInTime, LocalDateTime.now());
            return (int) duration.toMinutes();
        }
        return null;
    }

    /**
     * Get consultation duration in minutes.
     */
    public Integer getConsultationDurationMinutes() {
        if (consultationStartTime != null && consultationEndTime != null) {
            Duration duration = Duration.between(consultationStartTime, consultationEndTime);
            return (int) duration.toMinutes();
        }
        return null;
    }

    /**
     * Check if registration can be cancelled.
     */
    public boolean canBeCancelled() {
        return status == RegistrationStatus.REGISTERED || status == RegistrationStatus.WAITING;
    }

    /**
     * Get display status.
     */
    public String getStatusDisplay() {
        return status.getDisplayName();
    }
}