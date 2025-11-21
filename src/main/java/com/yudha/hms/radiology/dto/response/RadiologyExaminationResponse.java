package com.yudha.hms.radiology.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Examination Response DTO.
 *
 * Response for radiology examination information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyExaminationResponse {

    /**
     * Examination ID
     */
    private UUID id;

    /**
     * Examination code
     */
    private String examCode;

    /**
     * Examination name
     */
    private String examName;

    /**
     * Short name
     */
    private String shortName;

    /**
     * Modality ID
     */
    private UUID modalityId;

    /**
     * Modality code
     */
    private String modalityCode;

    /**
     * Modality name
     */
    private String modalityName;

    // ========== Coding ==========

    /**
     * CPT code
     */
    private String cptCode;

    /**
     * ICD procedure code
     */
    private String icdProcedureCode;

    // ========== Preparation Requirements ==========

    /**
     * Preparation instructions
     */
    private String preparationInstructions;

    /**
     * Fasting required
     */
    private Boolean fastingRequired;

    /**
     * Fasting duration in hours
     */
    private Integer fastingDurationHours;

    // ========== Contrast Requirements ==========

    /**
     * Requires contrast
     */
    private Boolean requiresContrast;

    /**
     * Contrast type
     */
    private String contrastType;

    /**
     * Contrast volume in ml
     */
    private BigDecimal contrastVolumeMl;

    // ========== Timing Information ==========

    /**
     * Examination duration in minutes
     */
    private Integer examDurationMinutes;

    /**
     * Reporting time in minutes
     */
    private Integer reportingTimeMinutes;

    // ========== Cost Information ==========

    /**
     * Base cost
     */
    private BigDecimal baseCost;

    /**
     * Contrast cost
     */
    private BigDecimal contrastCost;

    /**
     * BPJS tariff
     */
    private BigDecimal bpjsTariff;

    // ========== Body Part and Positioning ==========

    /**
     * Body part examined
     */
    private String bodyPart;

    /**
     * Laterality applicable
     */
    private Boolean lateralityApplicable;

    /**
     * Positioning notes
     */
    private String positioningNotes;

    // ========== Clinical Information ==========

    /**
     * Clinical indication
     */
    private String clinicalIndication;

    /**
     * Interpretation guide
     */
    private String interpretationGuide;

    // ========== Approval ==========

    /**
     * Requires approval
     */
    private Boolean requiresApproval;

    /**
     * Active status
     */
    private Boolean isActive;

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
