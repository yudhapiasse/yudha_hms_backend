package com.yudha.hms.radiology.entity;

import com.yudha.hms.radiology.constant.PregnancyTestResult;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Patient Preparation Checklist Entity.
 *
 * Tracks patient preparation requirements and completion before radiology examination.
 * Ensures patient safety through systematic verification of preparation steps.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Entity
@Table(name = "patient_preparation_checklist", schema = "radiology_schema", indexes = {
        @Index(name = "idx_prep_checklist_order", columnList = "order_id"),
        @Index(name = "idx_prep_checklist_examination", columnList = "examination_id"),
        @Index(name = "idx_prep_checklist_completed", columnList = "all_items_completed"),
        @Index(name = "idx_prep_checklist_consent", columnList = "consent_obtained")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PatientPreparationChecklist extends BaseEntity {

    /**
     * Radiology order reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private RadiologyOrder order;

    /**
     * Examination reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false)
    private RadiologyExamination examination;

    // ========== Preparation Instructions ==========

    /**
     * General preparation instructions
     */
    @Column(name = "preparation_instructions", columnDefinition = "TEXT")
    private String preparationInstructions;

    // ========== Fasting Verification ==========

    /**
     * Is fasting required for this examination?
     */
    @Column(name = "fasting_required")
    @Builder.Default
    private Boolean fastingRequired = false;

    /**
     * Has fasting been verified?
     */
    @Column(name = "fasting_verified")
    @Builder.Default
    private Boolean fastingVerified = false;

    /**
     * Who verified fasting status
     */
    @Column(name = "fasting_verified_by")
    private UUID fastingVerifiedBy;

    /**
     * When was fasting verified
     */
    @Column(name = "fasting_verified_at")
    private LocalDateTime fastingVerifiedAt;

    /**
     * Required fasting duration in hours
     */
    @Column(name = "fasting_hours_required")
    private Integer fastingHoursRequired;

    // ========== Medication Hold Verification ==========

    /**
     * Is medication hold required?
     */
    @Column(name = "medication_hold_required")
    @Builder.Default
    private Boolean medicationHoldRequired = false;

    /**
     * Has medication hold been verified?
     */
    @Column(name = "medication_hold_verified")
    @Builder.Default
    private Boolean medicationHoldVerified = false;

    /**
     * Who verified medication hold
     */
    @Column(name = "medication_hold_verified_by")
    private UUID medicationHoldVerifiedBy;

    /**
     * When was medication hold verified
     */
    @Column(name = "medication_hold_verified_at")
    private LocalDateTime medicationHoldVerifiedAt;

    /**
     * Details of medications to hold
     */
    @Column(name = "medication_hold_details", columnDefinition = "TEXT")
    private String medicationHoldDetails;

    // ========== IV Access Verification (for contrast) ==========

    /**
     * Is IV access required?
     */
    @Column(name = "iv_access_required")
    @Builder.Default
    private Boolean ivAccessRequired = false;

    /**
     * Has IV access been verified/established?
     */
    @Column(name = "iv_access_verified")
    @Builder.Default
    private Boolean ivAccessVerified = false;

    /**
     * Who verified IV access
     */
    @Column(name = "iv_access_verified_by")
    private UUID ivAccessVerifiedBy;

    /**
     * When was IV access verified
     */
    @Column(name = "iv_access_verified_at")
    private LocalDateTime ivAccessVerifiedAt;

    /**
     * IV catheter gauge (e.g., "18G", "20G")
     */
    @Column(name = "iv_gauge", length = 20)
    private String ivGauge;

    // ========== Pregnancy Test Verification ==========

    /**
     * Is pregnancy test required? (for females of childbearing age with radiation)
     */
    @Column(name = "pregnancy_test_required")
    @Builder.Default
    private Boolean pregnancyTestRequired = false;

    /**
     * Has pregnancy test been done?
     */
    @Column(name = "pregnancy_test_done")
    @Builder.Default
    private Boolean pregnancyTestDone = false;

    /**
     * Pregnancy test result
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "pregnancy_test_result", length = 20)
    private PregnancyTestResult pregnancyTestResult;

    /**
     * Date of pregnancy test
     */
    @Column(name = "pregnancy_test_date")
    private LocalDate pregnancyTestDate;

    // ========== Consent Verification ==========

    /**
     * Has informed consent been obtained?
     */
    @Column(name = "consent_obtained")
    @Builder.Default
    private Boolean consentObtained = false;

    /**
     * Who obtained consent
     */
    @Column(name = "consent_obtained_by")
    private UUID consentObtainedBy;

    /**
     * When was consent obtained
     */
    @Column(name = "consent_obtained_at")
    private LocalDateTime consentObtainedAt;

    /**
     * Consent form identifier/number
     */
    @Column(name = "consent_form_id", length = 100)
    private String consentFormId;

    // ========== Flexible Checklist Items ==========

    /**
     * Additional flexible checklist items as JSON
     * Structure: {"item_key": {"completed": true/false, "completedBy": "UUID", "completedAt": "timestamp", "notes": "text"}}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "checklist_items", columnDefinition = "jsonb")
    private Map<String, Object> checklistItems;

    // ========== Overall Completion ==========

    /**
     * Have all checklist items been completed?
     */
    @Column(name = "all_items_completed")
    @Builder.Default
    private Boolean allItemsCompleted = false;

    /**
     * Who marked all items as completed
     */
    @Column(name = "completed_by")
    private UUID completedBy;

    /**
     * When were all items marked complete
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ========== Notes ==========

    /**
     * Additional notes about preparation
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
