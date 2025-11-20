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

    @Enumerated(EnumType.STRING)
    @Column(name = "queue_status", length = 20)
    @Builder.Default
    private QueueStatus queueStatus = QueueStatus.WAITING;

    @Column(name = "queue_called_at")
    private LocalDateTime queueCalledAt;

    @Column(name = "queue_called_by", length = 100)
    private String queueCalledBy;

    @Column(name = "queue_serving_started_at")
    private LocalDateTime queueServingStartedAt;

    @Column(name = "queue_serving_ended_at")
    private LocalDateTime queueServingEndedAt;

    @Column(name = "queue_skipped_at")
    private LocalDateTime queueSkippedAt;

    @Column(name = "queue_skip_reason", columnDefinition = "TEXT")
    private String queueSkipReason;

    // ========== Encounter Link ==========
    @Column(name = "encounter_id")
    private UUID encounterId;

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

    // ========== Queue Management Methods ==========

    /**
     * Call patient from queue.
     */
    public void callQueue(String calledBy) {
        if (queueStatus != null && !queueStatus.canBeCalled()) {
            throw new IllegalStateException("Cannot call patient in current queue status: " + queueStatus);
        }
        this.queueStatus = QueueStatus.CALLED;
        this.queueCalledAt = LocalDateTime.now();
        this.queueCalledBy = calledBy;
    }

    /**
     * Start serving patient.
     */
    public void startServing() {
        if (queueStatus != null && !queueStatus.canStartServing()) {
            throw new IllegalStateException("Cannot start serving patient in current queue status: " + queueStatus);
        }
        this.queueStatus = QueueStatus.SERVING;
        this.queueServingStartedAt = LocalDateTime.now();
        startConsultation();
    }

    /**
     * Complete queue service.
     */
    public void completeQueue() {
        this.queueStatus = QueueStatus.COMPLETED;
        this.queueServingEndedAt = LocalDateTime.now();
        completeConsultation();
    }

    /**
     * Skip patient (not present when called).
     */
    public void skipQueue(String reason) {
        this.queueStatus = QueueStatus.SKIPPED;
        this.queueSkippedAt = LocalDateTime.now();
        this.queueSkipReason = reason;
    }

    /**
     * Cancel queue.
     */
    public void cancelQueue(String reason, String cancelledByUser) {
        this.queueStatus = QueueStatus.CANCELLED;
        cancel(reason, cancelledByUser);
    }

    /**
     * Get queue wait time in minutes.
     */
    public Integer getQueueWaitTimeMinutes() {
        if (checkInTime != null && queueServingStartedAt != null) {
            Duration duration = Duration.between(checkInTime, queueServingStartedAt);
            return (int) duration.toMinutes();
        }
        if (checkInTime != null && queueServingStartedAt == null) {
            Duration duration = Duration.between(checkInTime, LocalDateTime.now());
            return (int) duration.toMinutes();
        }
        return null;
    }

    /**
     * Get queue service time in minutes.
     */
    public Integer getQueueServiceTimeMinutes() {
        if (queueServingStartedAt != null && queueServingEndedAt != null) {
            Duration duration = Duration.between(queueServingStartedAt, queueServingEndedAt);
            return (int) duration.toMinutes();
        }
        if (queueServingStartedAt != null && queueServingEndedAt == null) {
            Duration duration = Duration.between(queueServingStartedAt, LocalDateTime.now());
            return (int) duration.toMinutes();
        }
        return null;
    }

    /**
     * Check if queue is active.
     */
    public boolean isQueueActive() {
        return queueStatus != null && queueStatus.isActive();
    }

    /**
     * Check if queue is completed.
     */
    public boolean isQueueCompleted() {
        return queueStatus != null && queueStatus.isCompleted();
    }
}