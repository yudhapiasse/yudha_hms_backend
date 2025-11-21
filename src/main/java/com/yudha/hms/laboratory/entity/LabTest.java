package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.SampleType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Lab Test Entity.
 *
 * Laboratory test catalog with comprehensive test configuration including:
 * - LOINC coding for international standard
 * - Sample requirements
 * - Processing time (TAT)
 * - Cost information
 * - Critical values
 * - Quality control requirements
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_test", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_test_code", columnList = "test_code", unique = true),
        @Index(name = "idx_lab_test_name", columnList = "name"),
        @Index(name = "idx_lab_test_category", columnList = "category_id"),
        @Index(name = "idx_lab_test_loinc", columnList = "loinc_code"),
        @Index(name = "idx_lab_test_sample_type", columnList = "sample_type"),
        @Index(name = "idx_lab_test_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LabTest extends SoftDeletableEntity {

    /**
     * Test code (internal hospital code)
     */
    @Column(name = "test_code", nullable = false, unique = true, length = 50)
    private String testCode;

    /**
     * Test name
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Short name
     */
    @Column(name = "short_name", length = 100)
    private String shortName;

    /**
     * Test category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private LabTestCategory category;

    // ========== LOINC Coding ==========

    /**
     * LOINC code for international standard
     */
    @Column(name = "loinc_code", length = 20)
    private String loincCode;

    /**
     * LOINC display name
     */
    @Column(name = "loinc_display_name", length = 500)
    private String loincDisplayName;

    // ========== Sample Requirements ==========

    /**
     * Sample type required
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "sample_type", nullable = false, length = 50)
    private SampleType sampleType;

    /**
     * Sample volume in ml
     */
    @Column(name = "sample_volume_ml", precision = 10, scale = 2)
    private BigDecimal sampleVolumeMl;

    /**
     * Sample volume unit
     */
    @Column(name = "sample_volume_unit", length = 20)
    private String sampleVolumeUnit;

    /**
     * Sample container type
     */
    @Column(name = "sample_container", length = 100)
    private String sampleContainer;

    /**
     * Sample preservation requirements
     */
    @Column(name = "sample_preservation", length = 200)
    private String samplePreservation;

    /**
     * Fasting required
     */
    @Column(name = "fasting_required")
    @Builder.Default
    private Boolean fastingRequired = false;

    /**
     * Fasting duration in hours
     */
    @Column(name = "fasting_duration_hours")
    private Integer fastingDurationHours;

    // ========== Processing Information ==========

    /**
     * Processing time in minutes (TAT - Turnaround Time)
     */
    @Column(name = "processing_time_minutes")
    private Integer processingTimeMinutes;

    /**
     * Urgency available (can be marked as urgent/cito)
     */
    @Column(name = "urgency_available")
    @Builder.Default
    private Boolean urgencyAvailable = true;

    /**
     * CITO processing time in minutes
     */
    @Column(name = "cito_processing_time_minutes")
    private Integer citoProcessingTimeMinutes;

    // ========== Cost Information ==========

    /**
     * Base cost
     */
    @Column(name = "base_cost", precision = 15, scale = 2, nullable = false)
    private BigDecimal baseCost;

    /**
     * Urgent cost (additional or total for urgent tests)
     */
    @Column(name = "urgent_cost", precision = 15, scale = 2)
    private BigDecimal urgentCost;

    /**
     * BPJS tariff (for BPJS patients)
     */
    @Column(name = "bpjs_tariff", precision = 15, scale = 2)
    private BigDecimal bpjsTariff;

    // ========== Test Configuration ==========

    /**
     * Test method
     */
    @Column(name = "test_method", length = 200)
    private String testMethod;

    /**
     * Test methodology (detailed)
     */
    @Column(name = "test_methodology", columnDefinition = "TEXT")
    private String testMethodology;

    /**
     * Reference method
     */
    @Column(name = "reference_method", length = 200)
    private String referenceMethod;

    /**
     * Requires approval before processing
     */
    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;

    /**
     * Requires pathologist review
     */
    @Column(name = "requires_pathologist_review")
    @Builder.Default
    private Boolean requiresPathologistReview = false;

    // ========== Critical Values ==========

    /**
     * Has critical values defined
     */
    @Column(name = "has_critical_values")
    @Builder.Default
    private Boolean hasCriticalValues = false;

    /**
     * Critical low value
     */
    @Column(name = "critical_low_value", precision = 15, scale = 4)
    private BigDecimal criticalLowValue;

    /**
     * Critical high value
     */
    @Column(name = "critical_high_value", precision = 15, scale = 4)
    private BigDecimal criticalHighValue;

    // ========== Quality Control ==========

    /**
     * QC required
     */
    @Column(name = "qc_required")
    @Builder.Default
    private Boolean qcRequired = true;

    /**
     * QC frequency in hours
     */
    @Column(name = "qc_frequency_hours")
    private Integer qcFrequencyHours;

    /**
     * Calibration required
     */
    @Column(name = "calibration_required")
    @Builder.Default
    private Boolean calibrationRequired = false;

    /**
     * Calibration frequency in days
     */
    @Column(name = "calibration_frequency_days")
    private Integer calibrationFrequencyDays;

    // ========== Additional Information ==========

    /**
     * Clinical indication
     */
    @Column(name = "clinical_indication", columnDefinition = "TEXT")
    private String clinicalIndication;

    /**
     * Preparation instructions for patient
     */
    @Column(name = "preparation_instructions", columnDefinition = "TEXT")
    private String preparationInstructions;

    /**
     * Interpretation guide
     */
    @Column(name = "interpretation_guide", columnDefinition = "TEXT")
    private String interpretationGuide;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Active status
     */
    @Column(name = "active", nullable = false)
    @Builder.Default
    private Boolean active = true;

    // ========== Helper Methods ==========

    /**
     * Check if test requires fasting
     */
    public boolean requiresFasting() {
        return Boolean.TRUE.equals(fastingRequired);
    }

    /**
     * Check if urgent processing is available
     */
    public boolean isUrgencyAvailable() {
        return Boolean.TRUE.equals(urgencyAvailable);
    }

    /**
     * Get processing time based on priority
     */
    public Integer getProcessingTime(boolean isUrgent) {
        if (isUrgent && citoProcessingTimeMinutes != null) {
            return citoProcessingTimeMinutes;
        }
        return processingTimeMinutes;
    }

    /**
     * Get cost based on priority
     */
    public BigDecimal getCost(boolean isUrgent) {
        if (isUrgent && urgentCost != null) {
            return urgentCost;
        }
        return baseCost;
    }
}
