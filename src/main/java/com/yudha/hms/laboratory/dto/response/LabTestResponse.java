package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.SampleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Test Response DTO.
 *
 * Response for lab test information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabTestResponse {

    /**
     * Test ID
     */
    private UUID id;

    /**
     * Test code
     */
    private String testCode;

    /**
     * Test name
     */
    private String name;

    /**
     * Short name
     */
    private String shortName;

    /**
     * Category ID
     */
    private UUID categoryId;

    /**
     * Category name
     */
    private String categoryName;

    // ========== LOINC Coding ==========

    /**
     * LOINC code
     */
    private String loincCode;

    /**
     * LOINC display name
     */
    private String loincDisplayName;

    // ========== Sample Requirements ==========

    /**
     * Sample type
     */
    private SampleType sampleType;

    /**
     * Sample volume in ml
     */
    private BigDecimal sampleVolumeMl;

    /**
     * Sample volume unit
     */
    private String sampleVolumeUnit;

    /**
     * Sample container
     */
    private String sampleContainer;

    /**
     * Sample preservation
     */
    private String samplePreservation;

    /**
     * Fasting required
     */
    private Boolean fastingRequired;

    /**
     * Fasting duration in hours
     */
    private Integer fastingDurationHours;

    // ========== Processing Information ==========

    /**
     * Processing time in minutes
     */
    private Integer processingTimeMinutes;

    /**
     * Urgency available
     */
    private Boolean urgencyAvailable;

    /**
     * CITO processing time in minutes
     */
    private Integer citoProcessingTimeMinutes;

    // ========== Cost Information ==========

    /**
     * Base cost
     */
    private BigDecimal baseCost;

    /**
     * Urgent cost
     */
    private BigDecimal urgentCost;

    /**
     * BPJS tariff
     */
    private BigDecimal bpjsTariff;

    // ========== Test Configuration ==========

    /**
     * Test method
     */
    private String testMethod;

    /**
     * Test methodology
     */
    private String testMethodology;

    /**
     * Reference method
     */
    private String referenceMethod;

    /**
     * Requires approval
     */
    private Boolean requiresApproval;

    /**
     * Requires pathologist review
     */
    private Boolean requiresPathologistReview;

    // ========== Critical Values ==========

    /**
     * Has critical values
     */
    private Boolean hasCriticalValues;

    /**
     * Critical low value
     */
    private BigDecimal criticalLowValue;

    /**
     * Critical high value
     */
    private BigDecimal criticalHighValue;

    // ========== Quality Control ==========

    /**
     * QC required
     */
    private Boolean qcRequired;

    /**
     * QC frequency in hours
     */
    private Integer qcFrequencyHours;

    /**
     * Calibration required
     */
    private Boolean calibrationRequired;

    /**
     * Calibration frequency in days
     */
    private Integer calibrationFrequencyDays;

    // ========== Additional Information ==========

    /**
     * Clinical indication
     */
    private String clinicalIndication;

    /**
     * Preparation instructions
     */
    private String preparationInstructions;

    /**
     * Interpretation guide
     */
    private String interpretationGuide;

    /**
     * Notes
     */
    private String notes;

    /**
     * Number of parameters
     */
    private Long parameterCount;

    /**
     * Active status
     */
    private Boolean active;

    /**
     * Created timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Created by user ID
     */
    private String createdBy;

    /**
     * Updated timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by user ID
     */
    private String updatedBy;
}
