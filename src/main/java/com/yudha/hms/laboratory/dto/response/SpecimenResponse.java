package com.yudha.hms.laboratory.dto.response;

import com.yudha.hms.laboratory.constant.QualityStatus;
import com.yudha.hms.laboratory.constant.SampleType;
import com.yudha.hms.laboratory.constant.SpecimenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Specimen Response DTO.
 *
 * Response for specimen information with full details.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecimenResponse {

    /**
     * Specimen ID
     */
    private UUID id;

    /**
     * Specimen number
     */
    private String specimenNumber;

    /**
     * Barcode
     */
    private String barcode;

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

    // ========== Specimen Details ==========

    /**
     * Specimen type
     */
    private SampleType specimenType;

    /**
     * Specimen source
     */
    private String specimenSource;

    /**
     * Volume in ml
     */
    private BigDecimal volumeMl;

    /**
     * Container type
     */
    private String containerType;

    // ========== Collection Information ==========

    /**
     * Collected at
     */
    private LocalDateTime collectedAt;

    /**
     * Collected by user ID
     */
    private UUID collectedBy;

    /**
     * Collected by name
     */
    private String collectedByName;

    /**
     * Collection method
     */
    private String collectionMethod;

    /**
     * Collection site
     */
    private String collectionSite;

    // ========== Reception ==========

    /**
     * Received at
     */
    private LocalDateTime receivedAt;

    /**
     * Received by user ID
     */
    private UUID receivedBy;

    /**
     * Received by name
     */
    private String receivedByName;

    // ========== Quality Checks ==========

    /**
     * Quality status
     */
    private QualityStatus qualityStatus;

    /**
     * Quality notes
     */
    private String qualityNotes;

    /**
     * Rejection reason
     */
    private String rejectionReason;

    // ========== Pre-analytical Validations ==========

    /**
     * Fasting status met
     */
    private Boolean fastingStatusMet;

    /**
     * Volume adequate
     */
    private Boolean volumeAdequate;

    /**
     * Container appropriate
     */
    private Boolean containerAppropriate;

    /**
     * Labeling correct
     */
    private Boolean labelingCorrect;

    /**
     * Temperature appropriate
     */
    private Boolean temperatureAppropriate;

    /**
     * Hemolysis detected
     */
    private Boolean hemolysisDetected;

    /**
     * Lipemia detected
     */
    private Boolean lipemiaDetected;

    /**
     * Icterus detected
     */
    private Boolean icterusDetected;

    /**
     * Has pre-analytical issues
     */
    private Boolean hasPreAnalyticalIssues;

    // ========== Storage ==========

    /**
     * Storage location
     */
    private String storageLocation;

    /**
     * Storage temperature
     */
    private BigDecimal storageTemperature;

    /**
     * Stored at
     */
    private LocalDateTime storedAt;

    // ========== Processing ==========

    /**
     * Processing started at
     */
    private LocalDateTime processingStartedAt;

    /**
     * Processing completed at
     */
    private LocalDateTime processingCompletedAt;

    /**
     * Processed by user ID
     */
    private UUID processedBy;

    /**
     * Processed by name
     */
    private String processedByName;

    // ========== Status Tracking ==========

    /**
     * Specimen status
     */
    private SpecimenStatus status;

    // ========== Disposal ==========

    /**
     * Disposed at
     */
    private LocalDateTime disposedAt;

    /**
     * Disposed by user ID
     */
    private UUID disposedBy;

    /**
     * Disposed by name
     */
    private String disposedByName;

    /**
     * Disposal method
     */
    private String disposalMethod;

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
