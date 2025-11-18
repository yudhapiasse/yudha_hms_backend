package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Inpatient admission entity.
 * Represents a patient's admission to the hospital for inpatient care.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "inpatient_admission", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_inpatient_admission_number", columnList = "admission_number"),
        @Index(name = "idx_inpatient_patient", columnList = "patient_id"),
        @Index(name = "idx_inpatient_admission_date", columnList = "admission_date"),
        @Index(name = "idx_inpatient_room", columnList = "room_id"),
        @Index(name = "idx_inpatient_bed", columnList = "bed_id"),
        @Index(name = "idx_inpatient_status", columnList = "status"),
        @Index(name = "idx_inpatient_admitting_doctor", columnList = "admitting_doctor_id"),
        @Index(name = "idx_inpatient_discharge_date", columnList = "discharge_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Inpatient admission records")
public class InpatientAdmission extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "admission_number", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Admission number is required")
    @Size(max = 50)
    private String admissionNumber;

    // Patient reference
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID is required")
    private UUID patientId;

    // Registration reference (if from outpatient/emergency)
    @Column(name = "outpatient_registration_id")
    private UUID outpatientRegistrationId;

    // Admission details
    @Column(name = "admission_date", nullable = false)
    @NotNull(message = "Admission date is required")
    @Builder.Default
    private LocalDateTime admissionDate = LocalDateTime.now();

    @Column(name = "admission_time", nullable = false)
    @NotNull
    @Builder.Default
    private LocalDateTime admissionTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "admission_type", nullable = false, length = 30)
    @NotNull(message = "Admission type is required")
    private AdmissionType admissionType;

    @Column(name = "admission_source", length = 30)
    @Size(max = 30)
    private String admissionSource; // OUTPATIENT, EMERGENCY, REFERRAL, DIRECT

    // Room and bed assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_admission_room"))
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", foreignKey = @ForeignKey(name = "fk_admission_bed"))
    private Bed bed;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_class", nullable = false, length = 20)
    @NotNull(message = "Room class is required")
    private RoomClass roomClass;

    // Medical team
    @Column(name = "admitting_doctor_id")
    private UUID admittingDoctorId;

    @Column(name = "admitting_doctor_name", length = 200)
    @Size(max = 200)
    private String admittingDoctorName;

    @Column(name = "attending_doctor_id")
    private UUID attendingDoctorId;

    @Column(name = "attending_doctor_name", length = 200)
    @Size(max = 200)
    private String attendingDoctorName;

    @Column(name = "referring_doctor_id")
    private UUID referringDoctorId;

    @Column(name = "referring_doctor_name", length = 200)
    @Size(max = 200)
    private String referringDoctorName;

    @Column(name = "referring_facility", length = 200)
    @Size(max = 200)
    private String referringFacility;

    // Clinical information
    @Column(name = "chief_complaint", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Chief complaint is required")
    private String chiefComplaint;

    @Column(name = "admission_diagnosis", columnDefinition = "TEXT")
    private String admissionDiagnosis;

    @Column(name = "secondary_diagnoses", columnDefinition = "TEXT")
    private String secondaryDiagnoses;

    // Estimated stay
    @Column(name = "estimated_length_of_stay_days")
    @Min(1)
    private Integer estimatedLengthOfStayDays;

    @Column(name = "estimated_discharge_date")
    private LocalDate estimatedDischargeDate;

    // Payment information
    @Column(name = "payment_method", nullable = false, length = 20)
    @NotBlank(message = "Payment method is required")
    @Size(max = 20)
    private String paymentMethod; // CASH, BPJS, INSURANCE, COMPANY

    @Column(name = "is_bpjs")
    @Builder.Default
    private Boolean isBpjs = false;

    @Column(name = "bpjs_card_number", length = 50)
    @Size(max = 50)
    private String bpjsCardNumber;

    @Column(name = "insurance_name", length = 100)
    @Size(max = 100)
    private String insuranceName;

    @Column(name = "insurance_number", length = 50)
    @Size(max = 50)
    private String insuranceNumber;

    @Column(name = "insurance_coverage_limit", precision = 12, scale = 2)
    private BigDecimal insuranceCoverageLimit;

    // Financial
    @Column(name = "room_rate_per_day", nullable = false, precision = 12, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal roomRatePerDay;

    @Column(name = "required_deposit", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal requiredDeposit = BigDecimal.ZERO;

    @Column(name = "deposit_paid", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal depositPaid = BigDecimal.ZERO;

    @Column(name = "deposit_paid_date")
    private LocalDateTime depositPaidDate;

    @Column(name = "deposit_receipt_number", length = 50)
    @Size(max = 50)
    private String depositReceiptNumber;

    // Status tracking
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @NotNull
    @Builder.Default
    private AdmissionStatus status = AdmissionStatus.ADMITTED;

    // Discharge information
    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Column(name = "discharge_time")
    private LocalDateTime dischargeTime;

    @Column(name = "discharge_type", length = 30)
    @Size(max = 30)
    private String dischargeType; // ROUTINE, AMA, TRANSFER, DECEASED

    @Column(name = "discharge_disposition", length = 50)
    @Size(max = 50)
    private String dischargeDisposition; // HOME, HOME_HEALTH, REHAB, NURSING_HOME, DECEASED

    @Column(name = "discharge_summary", columnDefinition = "TEXT")
    private String dischargeSummary;

    @Column(name = "discharge_instructions", columnDefinition = "TEXT")
    private String dischargeInstructions;

    // Length of stay calculation
    @Column(name = "actual_length_of_stay_days")
    private Integer actualLengthOfStayDays;

    // Emergency contact during admission
    @Column(name = "emergency_contact_name", length = 200)
    @Size(max = 200)
    private String emergencyContactName;

    @Column(name = "emergency_contact_relationship", length = 50)
    @Size(max = 50)
    private String emergencyContactRelationship;

    @Column(name = "emergency_contact_phone", length = 20)
    @Size(max = 20)
    private String emergencyContactPhone;

    // Patient belongings
    @Column(name = "belongings_stored")
    @Builder.Default
    private Boolean belongingsStored = false;

    @Column(name = "belongings_list", columnDefinition = "TEXT")
    private String belongingsList;

    // Special needs
    @Column(name = "requires_isolation")
    @Builder.Default
    private Boolean requiresIsolation = false;

    @Column(name = "isolation_type", length = 50)
    @Size(max = 50)
    private String isolationType; // AIRBORNE, DROPLET, CONTACT, PROTECTIVE

    @Column(name = "requires_interpreter")
    @Builder.Default
    private Boolean requiresInterpreter = false;

    @Column(name = "interpreter_language", length = 50)
    @Size(max = 50)
    private String interpreterLanguage;

    @Column(name = "has_allergies")
    @Builder.Default
    private Boolean hasAllergies = false;

    @Column(name = "allergy_notes", columnDefinition = "TEXT")
    private String allergyNotes;

    // Notes
    @Column(name = "admission_notes", columnDefinition = "TEXT")
    private String admissionNotes;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // Cancellation
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by", length = 100)
    @Size(max = 100)
    private String cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // Relationships
    @OneToMany(mappedBy = "admission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BedAssignment> bedAssignments = new ArrayList<>();

    @OneToMany(mappedBy = "admission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AdmissionDiagnosis> diagnoses = new ArrayList<>();

    /**
     * Check if admission is active (not discharged, transferred, or cancelled).
     *
     * @return true if admission is active
     */
    public boolean isActive() {
        return status == AdmissionStatus.ADMITTED || status == AdmissionStatus.IN_TREATMENT;
    }

    /**
     * Calculate deposit required based on estimated stay and room rate.
     *
     * @param daysOfDeposit number of days to calculate deposit for
     * @return calculated deposit amount
     */
    public BigDecimal calculateRequiredDeposit(int daysOfDeposit) {
        if (roomRatePerDay == null) {
            return BigDecimal.ZERO;
        }
        return roomRatePerDay.multiply(BigDecimal.valueOf(daysOfDeposit));
    }

    /**
     * Calculate actual length of stay.
     *
     * @return days between admission and discharge (or current date if not discharged)
     */
    public long calculateActualLengthOfStay() {
        LocalDateTime endDate = dischargeDate != null ? dischargeDate : LocalDateTime.now();
        return ChronoUnit.DAYS.between(admissionDate, endDate);
    }

    /**
     * Discharge the patient.
     *
     * @param dischargeType type of discharge
     * @param dischargeDisposition patient disposition
     */
    public void discharge(String dischargeType, String dischargeDisposition) {
        this.status = AdmissionStatus.DISCHARGED;
        this.dischargeDate = LocalDateTime.now();
        this.dischargeTime = LocalDateTime.now();
        this.dischargeType = dischargeType;
        this.dischargeDisposition = dischargeDisposition;
        this.actualLengthOfStayDays = (int) calculateActualLengthOfStay();

        // Release bed
        if (bed != null) {
            bed.release();
        }
        if (room != null) {
            room.releaseBed();
        }
    }

    /**
     * Cancel the admission.
     *
     * @param reason cancellation reason
     * @param cancelledBy user who cancelled
     */
    public void cancel(String reason, String cancelledBy) {
        this.status = AdmissionStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;

        // Release bed if assigned
        if (bed != null) {
            bed.release();
        }
        if (room != null) {
            room.releaseBed();
        }
    }

    /**
     * Add a diagnosis to the admission.
     *
     * @param diagnosis diagnosis to add
     */
    public void addDiagnosis(AdmissionDiagnosis diagnosis) {
        diagnoses.add(diagnosis);
        diagnosis.setAdmission(this);
    }

    /**
     * Add a bed assignment to the admission.
     *
     * @param assignment bed assignment to add
     */
    public void addBedAssignment(BedAssignment assignment) {
        bedAssignments.add(assignment);
        assignment.setAdmission(this);
    }
}
