package com.yudha.hms.radiology.dto.response;

import com.yudha.hms.radiology.constant.PregnancyTestResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Patient Preparation Checklist Response DTO.
 *
 * Response for patient preparation checklist information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientPreparationChecklistResponse {

    /**
     * Checklist ID
     */
    private UUID id;

    /**
     * Order ID
     */
    private UUID orderId;

    /**
     * Order number
     */
    private String orderNumber;

    /**
     * Examination ID
     */
    private UUID examinationId;

    /**
     * Examination name
     */
    private String examinationName;

    // ========== Patient Information ==========

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Patient name
     */
    private String patientName;

    /**
     * Patient MRN
     */
    private String patientMrn;

    // ========== Preparation Instructions ==========

    /**
     * Preparation instructions
     */
    private String preparationInstructions;

    // ========== Fasting Verification ==========

    /**
     * Fasting required
     */
    private Boolean fastingRequired;

    /**
     * Fasting verified
     */
    private Boolean fastingVerified;

    /**
     * Fasting verified by
     */
    private UUID fastingVerifiedBy;

    /**
     * Fasting verified by name
     */
    private String fastingVerifiedByName;

    /**
     * Fasting verified at
     */
    private LocalDateTime fastingVerifiedAt;

    /**
     * Fasting hours required
     */
    private Integer fastingHoursRequired;

    // ========== Medication Hold ==========

    /**
     * Medication hold required
     */
    private Boolean medicationHoldRequired;

    /**
     * Medication hold verified
     */
    private Boolean medicationHoldVerified;

    /**
     * Medication hold verified by
     */
    private UUID medicationHoldVerifiedBy;

    /**
     * Medication hold verified by name
     */
    private String medicationHoldVerifiedByName;

    /**
     * Medication hold verified at
     */
    private LocalDateTime medicationHoldVerifiedAt;

    /**
     * Medication hold details
     */
    private String medicationHoldDetails;

    // ========== IV Access ==========

    /**
     * IV access required
     */
    private Boolean ivAccessRequired;

    /**
     * IV access verified
     */
    private Boolean ivAccessVerified;

    /**
     * IV access verified by
     */
    private UUID ivAccessVerifiedBy;

    /**
     * IV access verified by name
     */
    private String ivAccessVerifiedByName;

    /**
     * IV access verified at
     */
    private LocalDateTime ivAccessVerifiedAt;

    /**
     * IV gauge
     */
    private String ivGauge;

    // ========== Pregnancy Test ==========

    /**
     * Pregnancy test required
     */
    private Boolean pregnancyTestRequired;

    /**
     * Pregnancy test done
     */
    private Boolean pregnancyTestDone;

    /**
     * Pregnancy test result
     */
    private PregnancyTestResult pregnancyTestResult;

    /**
     * Pregnancy test date
     */
    private LocalDate pregnancyTestDate;

    // ========== Consent ==========

    /**
     * Consent obtained
     */
    private Boolean consentObtained;

    /**
     * Consent obtained by
     */
    private UUID consentObtainedBy;

    /**
     * Consent obtained by name
     */
    private String consentObtainedByName;

    /**
     * Consent obtained at
     */
    private LocalDateTime consentObtainedAt;

    /**
     * Consent form ID
     */
    private String consentFormId;

    // ========== Flexible Checklist ==========

    /**
     * Additional checklist items (JSONB)
     */
    private Map<String, Object> checklistItems;

    // ========== Completion Status ==========

    /**
     * All items completed
     */
    private Boolean allItemsCompleted;

    /**
     * Completed by
     */
    private UUID completedBy;

    /**
     * Completed by name
     */
    private String completedByName;

    /**
     * Completed at
     */
    private LocalDateTime completedAt;

    /**
     * Notes
     */
    private String notes;

    // ========== Audit Fields ==========

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;
}
