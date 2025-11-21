package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.EntryMethod;
import com.yudha.hms.laboratory.constant.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Lab Result Response DTO.
 *
 * Response for lab result with full details.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultResponse {

    /**
     * Result ID
     */
    private UUID id;

    /**
     * Result number
     */
    private String resultNumber;

    // ========== Order Reference ==========

    /**
     * Order ID
     */
    private UUID orderId;

    /**
     * Order number
     */
    private String orderNumber;

    /**
     * Order item ID
     */
    private UUID orderItemId;

    /**
     * Specimen ID
     */
    private UUID specimenId;

    /**
     * Specimen number
     */
    private String specimenNumber;

    // ========== Test Information ==========

    /**
     * Test ID
     */
    private UUID testId;

    /**
     * Test code
     */
    private String testCode;

    /**
     * Test name
     */
    private String testName;

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

    /**
     * Patient age
     */
    private Integer patientAge;

    /**
     * Patient gender
     */
    private String patientGender;

    // ========== Result Status ==========

    /**
     * Result status
     */
    private ResultStatus status;

    // ========== Entry Information ==========

    /**
     * Entered at
     */
    private LocalDateTime enteredAt;

    /**
     * Entered by user ID
     */
    private UUID enteredBy;

    /**
     * Entered by name
     */
    private String enteredByName;

    /**
     * Entry method
     */
    private EntryMethod entryMethod;

    // ========== Validation ==========

    /**
     * Validated at
     */
    private LocalDateTime validatedAt;

    /**
     * Validated by user ID
     */
    private UUID validatedBy;

    /**
     * Validated by name
     */
    private String validatedByName;

    /**
     * Validation notes
     */
    private String validationNotes;

    // ========== Pathologist Review ==========

    /**
     * Requires pathologist review
     */
    private Boolean requiresPathologistReview;

    /**
     * Reviewed by pathologist
     */
    private Boolean reviewedByPathologist;

    /**
     * Pathologist ID
     */
    private UUID pathologistId;

    /**
     * Pathologist name
     */
    private String pathologistName;

    /**
     * Pathologist reviewed at
     */
    private LocalDateTime pathologistReviewedAt;

    /**
     * Pathologist comments
     */
    private String pathologistComments;

    // ========== Result Parameters ==========

    /**
     * Parameter results
     */
    private List<LabResultParameterResponse> parameterResults;

    /**
     * Number of parameters
     */
    private Integer parameterCount;

    /**
     * Number of abnormal parameters
     */
    private Integer abnormalParameterCount;

    /**
     * Number of critical parameters
     */
    private Integer criticalParameterCount;

    // ========== Result Interpretation ==========

    /**
     * Overall interpretation
     */
    private String overallInterpretation;

    /**
     * Clinical significance
     */
    private String clinicalSignificance;

    /**
     * Recommendations
     */
    private String recommendations;

    // ========== Delta Check ==========

    /**
     * Delta check performed
     */
    private Boolean deltaCheckPerformed;

    /**
     * Delta check flagged
     */
    private Boolean deltaCheckFlagged;

    /**
     * Delta check notes
     */
    private String deltaCheckNotes;

    /**
     * Previous result ID
     */
    private UUID previousResultId;

    // ========== Panic/Critical Value ==========

    /**
     * Has panic values
     */
    private Boolean hasPanicValues;

    /**
     * Panic value notified
     */
    private Boolean panicValueNotified;

    /**
     * Panic value notified at
     */
    private LocalDateTime panicValueNotifiedAt;

    /**
     * Panic value notified to
     */
    private String panicValueNotifiedTo;

    // ========== Amendment ==========

    /**
     * Is amended
     */
    private Boolean isAmended;

    /**
     * Amended at
     */
    private LocalDateTime amendedAt;

    /**
     * Amended by user ID
     */
    private UUID amendedBy;

    /**
     * Amended by name
     */
    private String amendedByName;

    /**
     * Amendment reason
     */
    private String amendmentReason;

    /**
     * Original result ID
     */
    private UUID originalResultId;

    // ========== LIS Interface ==========

    /**
     * LIS result ID
     */
    private String lisResultId;

    /**
     * LIS imported at
     */
    private LocalDateTime lisImportedAt;

    // ========== QC Information ==========

    /**
     * QC result ID
     */
    private UUID qcResultId;

    /**
     * QC status
     */
    private String qcStatus;

    // ========== Report ==========

    /**
     * Report generated
     */
    private Boolean reportGenerated;

    /**
     * Report generated at
     */
    private LocalDateTime reportGeneratedAt;

    /**
     * Report sent to clinical
     */
    private Boolean reportSentToClinical;

    /**
     * Report sent at
     */
    private LocalDateTime reportSentAt;

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
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
