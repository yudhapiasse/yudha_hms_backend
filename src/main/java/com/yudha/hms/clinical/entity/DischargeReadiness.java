package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Discharge Readiness Entity.
 *
 * Tracks the discharge readiness assessment criteria to ensure
 * patient is ready for safe discharge.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Entity
@Table(name = "discharge_readiness", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_readiness_encounter", columnList = "encounter_id"),
        @Index(name = "idx_readiness_patient", columnList = "patient_id")
    }
)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DischargeReadiness extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== References ==========

    @Column(name = "encounter_id", nullable = false)
    private UUID encounterId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    // ========== Readiness Criteria ==========

    @Column(name = "medical_stability_met")
    @Builder.Default
    private Boolean medicalStabilityMet = false;

    @Column(name = "medical_stability_notes", columnDefinition = "TEXT")
    private String medicalStabilityNotes;

    @Column(name = "medical_stability_assessed_at")
    private LocalDateTime medicalStabilityAssessedAt;

    @Column(name = "medical_stability_assessed_by", length = 200)
    private String medicalStabilityAssessedBy;

    @Column(name = "home_care_arranged")
    @Builder.Default
    private Boolean homeCareArranged = false;

    @Column(name = "home_care_notes", columnDefinition = "TEXT")
    private String homeCareNotes;

    @Column(name = "caregiver_name", length = 200)
    private String caregiverName;

    @Column(name = "caregiver_contact", length = 100)
    private String caregiverContact;

    @Column(name = "medications_reconciled")
    @Builder.Default
    private Boolean medicationsReconciled = false;

    @Column(name = "medication_reconciliation_notes", columnDefinition = "TEXT")
    private String medicationReconciliationNotes;

    @Column(name = "medication_reconciled_at")
    private LocalDateTime medicationReconciledAt;

    @Column(name = "medication_reconciled_by", length = 200)
    private String medicationReconciledBy;

    @Column(name = "follow_up_scheduled")
    @Builder.Default
    private Boolean followUpScheduled = false;

    @Column(name = "follow_up_appointment_date")
    private LocalDateTime followUpAppointmentDate;

    @Column(name = "follow_up_provider", length = 200)
    private String followUpProvider;

    @Column(name = "follow_up_department", length = 100)
    private String followUpDepartment;

    // ========== Patient Education ==========

    @Column(name = "patient_education_completed")
    @Builder.Default
    private Boolean patientEducationCompleted = false;

    @Column(name = "patient_education_topics", columnDefinition = "TEXT")
    private String patientEducationTopics;

    @Column(name = "patient_understanding_verified")
    @Builder.Default
    private Boolean patientUnderstandingVerified = false;

    // ========== Equipment and Supplies ==========

    @Column(name = "dme_ordered")
    @Builder.Default
    private Boolean dmeOrdered = false; // Durable Medical Equipment

    @Column(name = "dme_description", columnDefinition = "TEXT")
    private String dmeDescription;

    @Column(name = "medical_supplies_provided")
    @Builder.Default
    private Boolean medicalSuppliesProvided = false;

    @Column(name = "medical_supplies_list", columnDefinition = "TEXT")
    private String medicalSuppliesList;

    // ========== Discharge Barriers ==========

    @Column(name = "has_discharge_barriers")
    @Builder.Default
    private Boolean hasDischargeBarriers = false;

    @Column(name = "discharge_barriers", columnDefinition = "TEXT")
    private String dischargeBarriers;

    @Column(name = "barriers_resolved")
    @Builder.Default
    private Boolean barriersResolved = false;

    // ========== Overall Assessment ==========

    @Column(name = "ready_for_discharge")
    @Builder.Default
    private Boolean readyForDischarge = false;

    @Column(name = "readiness_assessed_at")
    private LocalDateTime readinessAssessedAt;

    @Column(name = "readiness_assessed_by_id")
    private UUID readinessAssessedById;

    @Column(name = "readiness_assessed_by_name", length = 200)
    private String readinessAssessedByName;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    // ========== Business Methods ==========

    public void assessMedicalStability(boolean met, String notes, String assessedBy) {
        this.medicalStabilityMet = met;
        this.medicalStabilityNotes = notes;
        this.medicalStabilityAssessedAt = LocalDateTime.now();
        this.medicalStabilityAssessedBy = assessedBy;
        updateReadinessStatus();
    }

    public void reconcileMedications(String notes, String reconciledBy) {
        this.medicationsReconciled = true;
        this.medicationReconciliationNotes = notes;
        this.medicationReconciledAt = LocalDateTime.now();
        this.medicationReconciledBy = reconciledBy;
        updateReadinessStatus();
    }

    public void scheduleFollowUp(LocalDateTime appointmentDate, String provider, String department) {
        this.followUpScheduled = true;
        this.followUpAppointmentDate = appointmentDate;
        this.followUpProvider = provider;
        this.followUpDepartment = department;
        updateReadinessStatus();
    }

    public void completePatientEducation(String topics) {
        this.patientEducationCompleted = true;
        this.patientEducationTopics = topics;
        this.patientUnderstandingVerified = true;
        updateReadinessStatus();
    }

    public void markReadyForDischarge(UUID assessedById, String assessedByName) {
        this.readyForDischarge = true;
        this.readinessAssessedAt = LocalDateTime.now();
        this.readinessAssessedById = assessedById;
        this.readinessAssessedByName = assessedByName;
    }

    private void updateReadinessStatus() {
        boolean allCriteriaMet = Boolean.TRUE.equals(medicalStabilityMet) &&
                                Boolean.TRUE.equals(homeCareArranged) &&
                                Boolean.TRUE.equals(medicationsReconciled) &&
                                Boolean.TRUE.equals(followUpScheduled) &&
                                (!Boolean.TRUE.equals(hasDischargeBarriers) ||
                                 Boolean.TRUE.equals(barriersResolved));

        if (allCriteriaMet && !Boolean.TRUE.equals(readyForDischarge)) {
            this.readyForDischarge = allCriteriaMet;
            this.readinessAssessedAt = LocalDateTime.now();
        }
    }

    public double calculateReadinessPercentage() {
        int totalCriteria = 5;
        int metCriteria = 0;

        if (Boolean.TRUE.equals(medicalStabilityMet)) metCriteria++;
        if (Boolean.TRUE.equals(homeCareArranged)) metCriteria++;
        if (Boolean.TRUE.equals(medicationsReconciled)) metCriteria++;
        if (Boolean.TRUE.equals(followUpScheduled)) metCriteria++;
        if (!Boolean.TRUE.equals(hasDischargeBarriers) || Boolean.TRUE.equals(barriersResolved)) {
            metCriteria++;
        }

        return (metCriteria * 100.0) / totalCriteria;
    }
}
