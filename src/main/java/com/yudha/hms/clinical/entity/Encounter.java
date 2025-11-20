package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Encounter Entity.
 *
 * Central entity for visit/encounter management linking all registration types
 * to clinical workflows, billing, and pharmacy.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "encounter", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_encounter_number", columnList = "encounter_number"),
        @Index(name = "idx_encounter_patient", columnList = "patient_id"),
        @Index(name = "idx_encounter_type", columnList = "encounter_type"),
        @Index(name = "idx_encounter_status", columnList = "status"),
        @Index(name = "idx_encounter_start", columnList = "encounter_start"),
        @Index(name = "idx_encounter_outpatient", columnList = "outpatient_registration_id"),
        @Index(name = "idx_encounter_inpatient", columnList = "inpatient_admission_id"),
        @Index(name = "idx_encounter_emergency", columnList = "emergency_registration_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Central encounter/visit management")
public class Encounter extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Encounter Identity ==========
    @Column(name = "encounter_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Encounter number is required")
    private String encounterNumber; // ENC-20250119-0001

    // ========== Patient Reference ==========
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // ========== Encounter Type and Class ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "encounter_type", nullable = false, length = 20)
    @NotNull(message = "Encounter type is required")
    private EncounterType encounterType; // OUTPATIENT, INPATIENT, EMERGENCY

    @Enumerated(EnumType.STRING)
    @Column(name = "encounter_class", nullable = false, length = 20)
    @NotNull(message = "Encounter class is required")
    private EncounterClass encounterClass; // AMBULATORY, INPATIENT, EMERGENCY, VIRTUAL

    // ========== Registration References (one will be populated) ==========
    @Column(name = "outpatient_registration_id")
    private UUID outpatientRegistrationId;

    @Column(name = "inpatient_admission_id")
    private UUID inpatientAdmissionId;

    @Column(name = "emergency_registration_id")
    private UUID emergencyRegistrationId;

    // ========== Timing ==========
    @Column(name = "encounter_start", nullable = false)
    @NotNull(message = "Encounter start time is required")
    @Builder.Default
    private LocalDateTime encounterStart = LocalDateTime.now();

    @Column(name = "encounter_end")
    private LocalDateTime encounterEnd;

    // ========== Status ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull(message = "Status is required")
    @Builder.Default
    private EncounterStatus status = EncounterStatus.PLANNED;

    // ========== Department/Location ==========
    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "location_id")
    private UUID locationId;

    @Column(name = "current_department", length = 100)
    private String currentDepartment;

    @Column(name = "current_location", length = 200)
    private String currentLocation;

    @Column(name = "admitting_department", length = 100)
    private String admittingDepartment;

    // ========== Care Team ==========
    @Column(name = "practitioner_id")
    private UUID practitionerId; // Attending/primary doctor

    @Column(name = "referring_practitioner_id")
    private UUID referringPractitionerId;

    @Column(name = "attending_doctor_id")
    private UUID attendingDoctorId;

    @Column(name = "attending_doctor_name", length = 200)
    private String attendingDoctorName;

    @Column(name = "primary_nurse_id")
    private UUID primaryNurseId;

    @Column(name = "primary_nurse_name", length = 200)
    private String primaryNurseName;

    // ========== Priority ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority; // ROUTINE, URGENT, EMERGENCY, STAT

    // ========== Service Type ==========
    @Column(name = "service_type", length = 50)
    private String serviceType; // GENERAL_MEDICINE, SURGERY, PEDIATRICS, etc.

    // ========== Reason for Visit ==========
    @Column(name = "reason_for_visit", columnDefinition = "TEXT")
    private String reasonForVisit;

    @Column(name = "chief_complaint", columnDefinition = "TEXT")
    private String chiefComplaint;

    // ========== Discharge Information ==========
    @Column(name = "discharge_disposition", length = 50)
    private String dischargeDisposition; // HOME, TRANSFER, ADMITTED, AMA

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Column(name = "discharge_summary_id")
    private UUID dischargeSummaryId;

    // ========== Referral Information ==========
    @Column(name = "referred_from", length = 200)
    private String referredFrom;

    @Column(name = "referred_to", length = 200)
    private String referredTo;

    @Column(name = "referral_id")
    private UUID referralId;

    // ========== Length of Stay ==========
    @Column(name = "length_of_stay_hours")
    private Integer lengthOfStayHours;

    @Column(name = "length_of_stay_days")
    private Integer lengthOfStayDays;

    // ========== BPJS/Insurance ==========
    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", length = 30)
    private InsuranceType insuranceType; // BPJS, PRIVATE_INSURANCE, SELF_PAY

    @Column(name = "insurance_number", length = 100)
    private String insuranceNumber;

    @Column(name = "is_bpjs")
    @Builder.Default
    private Boolean isBpjs = false;

    @Column(name = "sep_number", length = 50)
    private String sepNumber; // BPJS Surat Eligibilitas Peserta

    @Column(name = "sep_date")
    private java.time.LocalDate sepDate;

    @Column(name = "insurance_provider", length = 200)
    private String insuranceProvider;

    // ========== SATUSEHAT Integration ==========
    @Column(name = "satusehat_encounter_id", length = 100)
    private String satusehatEncounterId;

    @Column(name = "satusehat_synced")
    @Builder.Default
    private Boolean satusehatSynced = false;

    @Column(name = "satusehat_synced_at")
    private LocalDateTime satusehatSyncedAt;

    @Column(name = "satusehat_submitted")
    @Builder.Default
    private Boolean satusehatSubmitted = false;

    @Column(name = "satusehat_submission_date")
    private LocalDateTime satusehatSubmissionDate;

    // ========== Billing ==========
    @Column(name = "billing_status", length = 20)
    private String billingStatus; // PENDING, BILLED, PAID, CANCELLED

    @Column(name = "total_charges", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalCharges = BigDecimal.ZERO;

    // ========== Notes ==========
    @Column(name = "encounter_notes", columnDefinition = "TEXT")
    private String encounterNotes;

    // ========== Cancellation ==========
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by", length = 100)
    private String cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // ========== Relationships ==========
    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DepartmentTransfer> transfers = new ArrayList<>();

    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EncounterParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EncounterDiagnosis> diagnoses = new ArrayList<>();

    @OneToMany(mappedBy = "encounter", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EncounterStatusHistory> statusHistory = new ArrayList<>();

    // ========== Business Methods ==========

    /**
     * Change encounter status with state machine validation and business rules enforcement.
     *
     * This method:
     * 1. Validates state transition is allowed
     * 2. Enforces business rules (diagnosis required, billing check)
     * 3. Creates status history entry
     * 4. Updates relevant timestamps
     *
     * @param newStatus the target status
     * @param reason reason for status change
     * @param changedBy user making the change
     * @throws com.yudha.hms.clinical.exception.InvalidStatusTransitionException if transition is invalid
     * @throws com.yudha.hms.clinical.exception.EncounterBusinessRuleException if business rule is violated
     */
    public void changeStatus(EncounterStatus newStatus, String reason, String changedBy) {
        changeStatus(newStatus, reason, changedBy, null);
    }

    /**
     * Change encounter status with state machine validation and business rules enforcement.
     *
     * @param newStatus the target status
     * @param reason reason for status change
     * @param changedBy user making the change
     * @param changedById ID of user making the change
     * @throws com.yudha.hms.clinical.exception.InvalidStatusTransitionException if transition is invalid
     * @throws com.yudha.hms.clinical.exception.EncounterBusinessRuleException if business rule is violated
     */
    public void changeStatus(EncounterStatus newStatus, String reason, String changedBy, UUID changedById) {
        // Validate state transition
        if (!this.status.canTransitionTo(newStatus)) {
            throw new com.yudha.hms.clinical.exception.InvalidStatusTransitionException(
                this.status, newStatus
            );
        }

        // Enforce business rules
        validateBusinessRules(newStatus);

        // Store old status for history
        EncounterStatus oldStatus = this.status;

        // Update status
        this.status = newStatus;

        // Update timestamps based on new status
        updateTimestampsForStatus(newStatus);

        // Create status history entry
        EncounterStatusHistory historyEntry = EncounterStatusHistory.builder()
            .encounter(this)
            .fromStatus(oldStatus)
            .toStatus(newStatus)
            .reason(reason)
            .changedByName(changedBy)
            .changedById(changedById)
            .statusChangedAt(LocalDateTime.now())
            .build();

        addStatusHistory(historyEntry);
    }

    /**
     * Validate business rules before status change.
     *
     * @param newStatus the target status
     * @throws com.yudha.hms.clinical.exception.EncounterBusinessRuleException if business rule is violated
     */
    private void validateBusinessRules(EncounterStatus newStatus) {
        // Business Rules for FINISHING encounter
        if (newStatus == EncounterStatus.FINISHED) {
            // Rule 1: Cannot finish without at least one diagnosis
            if (diagnoses == null || diagnoses.isEmpty()) {
                throw new com.yudha.hms.clinical.exception.EncounterBusinessRuleException(
                    "DIAGNOSIS_REQUIRED",
                    "Encounter tidak dapat diselesaikan tanpa diagnosis. " +
                    "Minimal satu diagnosis harus ditambahkan sebelum menyelesaikan encounter."
                );
            }

            // Rule 2: Must have attending practitioner assigned
            if (attendingDoctorId == null && practitionerId == null) {
                throw new com.yudha.hms.clinical.exception.EncounterBusinessRuleException(
                    "PRACTITIONER_REQUIRED",
                    "Encounter harus memiliki dokter yang bertugas (attending practitioner) " +
                    "sebelum dapat diselesaikan."
                );
            }

            // Rule 3: BPJS encounters require SEP number
            if (Boolean.TRUE.equals(isBpjs)) {
                if (sepNumber == null || sepNumber.trim().isEmpty()) {
                    throw new com.yudha.hms.clinical.exception.EncounterBusinessRuleException(
                        "SEP_REQUIRED",
                        "Encounter BPJS wajib memiliki nomor SEP sebelum dapat diselesaikan."
                    );
                }
            }
        }

        // Business Rules for CANCELLING encounter
        if (newStatus == EncounterStatus.CANCELLED) {
            // Rule 1: Cannot cancel if there are linked billing transactions
            if (billingStatus != null &&
                (billingStatus.equals("BILLED") || billingStatus.equals("PAID"))) {
                throw new com.yudha.hms.clinical.exception.EncounterBusinessRuleException(
                    "BILLING_LINKED",
                    "Encounter tidak dapat dibatalkan karena sudah memiliki transaksi billing. " +
                    "Status billing: " + billingStatus
                );
            }

            // Rule 2: Check if there are any charges
            if (totalCharges != null && totalCharges.compareTo(BigDecimal.ZERO) > 0) {
                throw new com.yudha.hms.clinical.exception.EncounterBusinessRuleException(
                    "CHARGES_EXISTS",
                    "Encounter tidak dapat dibatalkan karena sudah memiliki tagihan sebesar Rp " +
                    totalCharges + ". Batalkan atau hapus tagihan terlebih dahulu."
                );
            }
        }
    }

    /**
     * Update relevant timestamps when status changes.
     *
     * @param newStatus the new status
     */
    private void updateTimestampsForStatus(EncounterStatus newStatus) {
        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case FINISHED -> {
                this.encounterEnd = now;
                calculateLengthOfStay();
            }
            case CANCELLED -> {
                this.cancelledAt = now;
                if (this.encounterEnd == null) {
                    this.encounterEnd = now;
                    calculateLengthOfStay();
                }
            }
            case IN_PROGRESS -> {
                if (this.encounterStart == null) {
                    this.encounterStart = now;
                }
            }
        }
    }

    /**
     * Start the encounter (change status to IN_PROGRESS).
     * Convenience method that uses the state machine.
     *
     * @param startedBy user starting the encounter
     * @deprecated Use changeStatus(EncounterStatus.IN_PROGRESS, reason, changedBy) instead
     */
    @Deprecated
    public void startEncounter(String startedBy) {
        changeStatus(EncounterStatus.IN_PROGRESS, "Encounter dimulai", startedBy);
    }

    /**
     * Mark patient as arrived.
     * Convenience method that uses the state machine.
     *
     * @param arrivedBy user marking arrival
     * @deprecated Use changeStatus(EncounterStatus.ARRIVED, reason, changedBy) instead
     */
    @Deprecated
    public void markAsArrived(String arrivedBy) {
        changeStatus(EncounterStatus.ARRIVED, "Pasien tiba", arrivedBy);
    }

    /**
     * Mark patient as triaged.
     * Convenience method that uses the state machine.
     *
     * @param triagedBy user marking triage
     * @deprecated Use changeStatus(EncounterStatus.TRIAGED, reason, changedBy) instead
     */
    @Deprecated
    public void markAsTriaged(String triagedBy) {
        changeStatus(EncounterStatus.TRIAGED, "Pasien sudah ditriage", triagedBy);
    }

    /**
     * Finish the encounter.
     * Convenience method that uses the state machine.
     *
     * @param finishedBy user finishing the encounter
     * @deprecated Use changeStatus(EncounterStatus.FINISHED, reason, changedBy) instead
     */
    @Deprecated
    public void finishEncounter(String finishedBy) {
        changeStatus(EncounterStatus.FINISHED, "Encounter selesai", finishedBy);
    }

    /**
     * Cancel the encounter.
     * Convenience method that uses the state machine.
     *
     * @param reason reason for cancellation
     * @param cancelledByUser user cancelling the encounter
     * @deprecated Use changeStatus(EncounterStatus.CANCELLED, reason, changedBy) instead
     */
    @Deprecated
    public void cancelEncounter(String reason, String cancelledByUser) {
        this.cancellationReason = reason;
        this.cancelledBy = cancelledByUser;
        changeStatus(EncounterStatus.CANCELLED, reason, cancelledByUser);
    }

    /**
     * Add a participant to the encounter.
     */
    public void addParticipant(EncounterParticipant participant) {
        participants.add(participant);
        participant.setEncounter(this);
    }

    /**
     * Add a diagnosis to the encounter.
     */
    public void addDiagnosis(EncounterDiagnosis diagnosis) {
        diagnoses.add(diagnosis);
        diagnosis.setEncounter(this);
    }

    /**
     * Add status history entry.
     */
    public void addStatusHistory(EncounterStatusHistory history) {
        statusHistory.add(history);
        history.setEncounter(this);
    }

    /**
     * Calculate length of stay.
     */
    public void calculateLengthOfStay() {
        if (encounterStart != null && encounterEnd != null) {
            Duration duration = Duration.between(encounterStart, encounterEnd);
            this.lengthOfStayHours = (int) duration.toHours();
            this.lengthOfStayDays = (int) duration.toDays();
        }
    }

    /**
     * Check if encounter is active.
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * Check if encounter is completed.
     */
    public boolean isCompleted() {
        return status.isCompleted();
    }

    /**
     * Get encounter duration in hours.
     */
    public Long getDurationHours() {
        LocalDateTime end = encounterEnd != null ? encounterEnd : LocalDateTime.now();
        if (encounterStart != null) {
            return Duration.between(encounterStart, end).toHours();
        }
        return 0L;
    }

    /**
     * Check if encounter can be cancelled (uses state machine).
     */
    public boolean canBeCancelled() {
        return status.canBeCancelled();
    }

    /**
     * Check if status transition is allowed.
     *
     * @param targetStatus the target status
     * @return true if transition is allowed
     */
    public boolean canTransitionTo(EncounterStatus targetStatus) {
        return status.canTransitionTo(targetStatus);
    }

    /**
     * Get list of allowed status transitions from current status.
     *
     * @return list of allowed next statuses
     */
    public List<EncounterStatus> getAllowedStatusTransitions() {
        return status.getAllowedTransitions();
    }
}
