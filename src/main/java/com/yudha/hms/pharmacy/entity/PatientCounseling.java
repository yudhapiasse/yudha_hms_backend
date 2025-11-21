package com.yudha.hms.pharmacy.entity;

import com.yudha.hms.pharmacy.constant.CounselingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Patient Counseling Entity.
 *
 * Records patient counseling sessions for medication education and compliance.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "patient_counseling", schema = "pharmacy_schema", indexes = {
        @Index(name = "idx_counseling_dispensing", columnList = "dispensing_id"),
        @Index(name = "idx_counseling_patient", columnList = "patient_id"),
        @Index(name = "idx_counseling_status", columnList = "status"),
        @Index(name = "idx_counseling_date", columnList = "counseling_date"),
        @Index(name = "idx_counseling_pharmacist", columnList = "pharmacist_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCounseling {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispensing_id", nullable = false)
    private Dispensing dispensing;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "patient_name", length = 200)
    private String patientName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private CounselingStatus status = CounselingStatus.PENDING;

    @Column(name = "counseling_date")
    private LocalDateTime counselingDate;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "pharmacist_id")
    private UUID pharmacistId;

    @Column(name = "pharmacist_name", length = 200)
    private String pharmacistName;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "counseling_location", length = 100)
    private String counselingLocation;

    // Counseling content
    @Column(name = "drug_information_provided")
    private Boolean drugInformationProvided = false;

    @Column(name = "dosage_instructions_explained")
    private Boolean dosageInstructionsExplained = false;

    @Column(name = "side_effects_discussed")
    private Boolean sideEffectsDiscussed = false;

    @Column(name = "interactions_discussed")
    private Boolean interactionsDiscussed = false;

    @Column(name = "storage_instructions_given")
    private Boolean storageInstructionsGiven = false;

    @Column(name = "adherence_counseling_provided")
    private Boolean adherenceCounselingProvided = false;

    @Column(name = "lifestyle_modifications_discussed")
    private Boolean lifestyleModificationsDiscussed = false;

    // Patient understanding and agreement
    @Column(name = "patient_understands")
    private Boolean patientUnderstands;

    @Column(name = "patient_has_questions")
    private Boolean patientHasQuestions;

    @Column(name = "patient_questions", columnDefinition = "TEXT")
    private String patientQuestions;

    @Column(name = "patient_concerns", columnDefinition = "TEXT")
    private String patientConcerns;

    @Column(name = "patient_signature_obtained")
    private Boolean patientSignatureObtained = false;

    @Column(name = "patient_consent_given")
    private Boolean patientConsentGiven = false;

    // Written materials provided
    @Column(name = "written_materials_provided")
    private Boolean writtenMaterialsProvided = false;

    @Column(name = "materials_provided", columnDefinition = "TEXT")
    private String materialsProvided;

    @Column(name = "follow_up_required")
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;

    @Column(name = "follow_up_reason", columnDefinition = "TEXT")
    private String followUpReason;

    @Column(name = "counseling_notes", columnDefinition = "TEXT")
    private String counselingNotes;

    @Column(name = "decline_reason", columnDefinition = "TEXT")
    private String declineReason;

    @Column(name = "completed_by_id")
    private UUID completedById;

    @Column(name = "completed_by_name", length = 200)
    private String completedByName;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "active")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    // Business methods

    /**
     * Start counseling session
     */
    public void startCounseling(UUID pharmacistId, String pharmacistName) {
        if (!status.canBeStarted()) {
            throw new IllegalStateException("Cannot start counseling in current status: " + status);
        }
        this.status = CounselingStatus.IN_PROGRESS;
        this.pharmacistId = pharmacistId;
        this.pharmacistName = pharmacistName;
        this.counselingDate = LocalDateTime.now();
    }

    /**
     * Complete counseling session
     */
    public void complete(UUID completedBy, String completedByName, Integer duration) {
        if (status != CounselingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete counseling in current status: " + status);
        }
        this.status = CounselingStatus.COMPLETED;
        this.completedById = completedBy;
        this.completedByName = completedByName;
        this.completedAt = LocalDateTime.now();
        this.durationMinutes = duration;
    }

    /**
     * Decline counseling
     */
    public void decline(String reason) {
        if (!status.canBeStarted()) {
            throw new IllegalStateException("Cannot decline counseling in current status: " + status);
        }
        this.status = CounselingStatus.DECLINED;
        this.declineReason = reason;
    }

    /**
     * Reschedule counseling
     */
    public void reschedule(LocalDateTime newDate, String reason) {
        this.status = CounselingStatus.RESCHEDULED;
        this.scheduledDate = newDate;
        if (this.counselingNotes == null) {
            this.counselingNotes = "Rescheduled: " + reason;
        } else {
            this.counselingNotes += "\nRescheduled: " + reason;
        }
    }

    /**
     * Check if counseling is comprehensive
     */
    public boolean isComprehensive() {
        return drugInformationProvided && dosageInstructionsExplained
                && sideEffectsDiscussed && interactionsDiscussed
                && storageInstructionsGiven;
    }

    /**
     * Schedule follow-up
     */
    public void scheduleFollowUp(LocalDateTime followUpDate, String reason) {
        this.followUpRequired = true;
        this.followUpDate = followUpDate;
        this.followUpReason = reason;
    }
}
