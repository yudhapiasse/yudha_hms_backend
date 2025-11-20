package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Procedure Schedule Entity.
 *
 * Manages procedure scheduling with operating room integration.
 * Tracks reservations, resources, and scheduling conflicts.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "procedure_schedules", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_proc_schedule_procedure", columnList = "encounter_procedure_id"),
        @Index(name = "idx_proc_schedule_date", columnList = "scheduled_date, scheduled_start_time"),
        @Index(name = "idx_proc_schedule_room", columnList = "operating_room_id"),
        @Index(name = "idx_proc_schedule_surgeon", columnList = "primary_surgeon_id"),
        @Index(name = "idx_proc_schedule_status", columnList = "schedule_status"),
        @Index(name = "idx_proc_schedule_patient", columnList = "patient_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Procedure scheduling with operating room integration")
public class ProcedureSchedule extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== References ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_procedure_id")
    private EncounterProcedure encounterProcedure;

    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    @NotBlank(message = "Patient name is required")
    private String patientName;

    @Column(name = "encounter_id")
    private UUID encounterId;

    // ========== Procedure Details ==========
    @Column(name = "procedure_code", length = 10)
    private String procedureCode; // ICD-9-CM code

    @Column(name = "procedure_name", nullable = false, length = 300)
    @NotBlank(message = "Procedure name is required")
    private String procedureName;

    @Column(name = "procedure_type", length = 50)
    private String procedureType;

    @Column(name = "specialty", length = 100)
    private String specialty;

    // ========== Scheduling ==========
    @Column(name = "scheduled_date", nullable = false)
    @NotNull(message = "Scheduled date is required")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_start_time", nullable = false)
    @NotNull(message = "Scheduled start time is required")
    private LocalDateTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalDateTime scheduledEndTime;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    // ========== Operating Room ==========
    @Column(name = "operating_room_id")
    private UUID operatingRoomId;

    @Column(name = "operating_room_name", length = 100)
    private String operatingRoomName;

    @Column(name = "room_type", length = 50)
    private String roomType; // OR, PROCEDURE_ROOM, CATH_LAB, ENDOSCOPY_SUITE

    @Column(name = "room_reserved")
    @Builder.Default
    private Boolean roomReserved = false;

    @Column(name = "setup_time_minutes")
    private Integer setupTimeMinutes;

    @Column(name = "cleanup_time_minutes")
    private Integer cleanupTimeMinutes;

    // ========== Staff Scheduling ==========
    @Column(name = "primary_surgeon_id", nullable = false)
    @NotNull(message = "Primary surgeon is required")
    private UUID primarySurgeonId;

    @Column(name = "primary_surgeon_name", nullable = false, length = 200)
    @NotBlank(message = "Primary surgeon name is required")
    private String primarySurgeonName;

    @Column(name = "assisting_surgeons", columnDefinition = "TEXT")
    private String assistingSurgeons; // JSON array

    @Column(name = "anesthesiologist_id")
    private UUID anesthesiologistId;

    @Column(name = "anesthesiologist_name", length = 200)
    private String anesthesiologistName;

    @Column(name = "nursing_staff", columnDefinition = "TEXT")
    private String nursingStaff; // JSON array

    @Column(name = "scrub_nurse_id")
    private UUID scrubNurseId;

    @Column(name = "circulating_nurse_id")
    private UUID circulatingNurseId;

    // ========== Equipment and Resources ==========
    @Column(name = "required_equipment", columnDefinition = "TEXT")
    private String requiredEquipment; // JSON array

    @Column(name = "equipment_reserved")
    @Builder.Default
    private Boolean equipmentReserved = false;

    @Column(name = "special_equipment_notes", columnDefinition = "TEXT")
    private String specialEquipmentNotes;

    @Column(name = "required_blood_units")
    private Integer requiredBloodUnits;

    @Column(name = "blood_reserved")
    @Builder.Default
    private Boolean bloodReserved = false;

    // ========== Status ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = false, length = 30)
    @NotNull(message = "Schedule status is required")
    @Builder.Default
    private ScheduleStatus scheduleStatus = ScheduleStatus.PENDING;

    @Column(name = "priority", length = 20)
    @Builder.Default
    private String priority = "ROUTINE"; // EMERGENCY, URGENT, ROUTINE, ELECTIVE

    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;

    // ========== Pre-Procedure Preparation ==========
    @Column(name = "pre_op_assessment_completed")
    @Builder.Default
    private Boolean preOpAssessmentCompleted = false;

    @Column(name = "pre_op_clearance_obtained")
    @Builder.Default
    private Boolean preOpClearanceObtained = false;

    @Column(name = "consent_obtained")
    @Builder.Default
    private Boolean consentObtained = false;

    @Column(name = "consent_form_id")
    private UUID consentFormId;

    @Column(name = "npo_status", length = 50)
    private String npoStatus; // NPO (Nothing Per Oral) instructions

    @Column(name = "npo_since")
    private LocalDateTime npoSince;

    // ========== Notifications ==========
    @Column(name = "patient_notified")
    @Builder.Default
    private Boolean patientNotified = false;

    @Column(name = "patient_notification_date")
    private LocalDateTime patientNotificationDate;

    @Column(name = "surgeon_notified")
    @Builder.Default
    private Boolean surgeonNotified = false;

    @Column(name = "anesthesia_notified")
    @Builder.Default
    private Boolean anesthesiaNotified = false;

    // ========== Cancellation ==========
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by_id")
    private UUID cancelledById;

    @Column(name = "cancelled_by_name", length = 200)
    private String cancelledByName;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // ========== Rescheduling ==========
    @Column(name = "rescheduled_from_id")
    private UUID rescheduledFromId; // Link to previous schedule

    @Column(name = "reschedule_reason", columnDefinition = "TEXT")
    private String rescheduleReason;

    @Column(name = "reschedule_count")
    @Builder.Default
    private Integer rescheduleCount = 0;

    // ========== Notes ==========
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Enumerations ==========

    public enum ScheduleStatus {
        PENDING,        // Awaiting approval/confirmation
        CONFIRMED,      // Confirmed and resources reserved
        READY,          // Patient ready, all prep complete
        IN_PROGRESS,    // Procedure started
        COMPLETED,      // Procedure completed
        CANCELLED,      // Cancelled
        RESCHEDULED,    // Rescheduled to another time
        NO_SHOW,        // Patient didn't show up
        DELAYED         // Running behind schedule
    }

    // ========== Business Methods ==========

    /**
     * Calculate total reserved time including setup and cleanup.
     */
    public int getTotalReservedTimeMinutes() {
        int total = estimatedDurationMinutes != null ? estimatedDurationMinutes : 60;
        if (setupTimeMinutes != null) total += setupTimeMinutes;
        if (cleanupTimeMinutes != null) total += cleanupTimeMinutes;
        return total;
    }

    /**
     * Calculate actual duration if procedure completed.
     */
    public Integer getActualDurationMinutes() {
        if (actualStartTime != null && actualEndTime != null) {
            return (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
        return null;
    }

    /**
     * Check if all pre-procedure requirements are met.
     */
    public boolean isReadyForProcedure() {
        return scheduleStatus == ScheduleStatus.CONFIRMED &&
               preOpAssessmentCompleted &&
               preOpClearanceObtained &&
               consentObtained &&
               roomReserved &&
               (requiredBloodUnits == null || bloodReserved);
    }

    /**
     * Mark as confirmed.
     */
    public void confirm() {
        this.scheduleStatus = ScheduleStatus.CONFIRMED;
        this.roomReserved = true;
    }

    /**
     * Cancel the schedule.
     */
    public void cancel(UUID userId, String userName, String reason) {
        this.scheduleStatus = ScheduleStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledById = userId;
        this.cancelledByName = userName;
        this.cancellationReason = reason;
    }

    /**
     * Start the procedure.
     */
    public void start() {
        this.scheduleStatus = ScheduleStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
    }

    /**
     * Complete the procedure.
     */
    public void complete() {
        this.scheduleStatus = ScheduleStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
    }
}
