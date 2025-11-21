package com.yudha.hms.radiology.dto.response;

import com.yudha.hms.radiology.constant.ContrastType;
import com.yudha.hms.radiology.constant.ReactionSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrast Administration Response DTO.
 *
 * Response for contrast administration information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContrastAdministrationResponse {

    /**
     * Contrast administration ID
     */
    private UUID id;

    /**
     * Order item ID
     */
    private UUID orderItemId;

    /**
     * Order number
     */
    private String orderNumber;

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

    // ========== Contrast Information ==========

    /**
     * Contrast name
     */
    private String contrastName;

    /**
     * Contrast type
     */
    private ContrastType contrastType;

    /**
     * Volume in ml
     */
    private BigDecimal volumeMl;

    /**
     * Batch number
     */
    private String batchNumber;

    /**
     * Administered by
     */
    private UUID administeredBy;

    /**
     * Administered by name
     */
    private String administeredByName;

    /**
     * Administered date
     */
    private LocalDateTime administeredDate;

    // ========== Reaction Monitoring ==========

    /**
     * Reaction observed
     */
    private Boolean reactionObserved;

    /**
     * Reaction severity
     */
    private ReactionSeverity reactionSeverity;

    /**
     * Reaction description
     */
    private String reactionDescription;

    /**
     * Treatment given
     */
    private String treatmentGiven;

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
