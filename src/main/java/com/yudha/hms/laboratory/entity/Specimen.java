package com.yudha.hms.laboratory.entity;

import com.yudha.hms.laboratory.constant.QualityStatus;
import com.yudha.hms.laboratory.constant.SampleType;
import com.yudha.hms.laboratory.constant.SpecimenStatus;
import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Specimen Entity.
 *
 * Sample collection and tracking with barcode support.
 * Handles specimen lifecycle from collection to disposal,
 * including quality checks and pre-analytical validations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "specimen", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_specimen_number", columnList = "specimen_number", unique = true),
        @Index(name = "idx_specimen_barcode", columnList = "barcode", unique = true),
        @Index(name = "idx_specimen_order", columnList = "order_id"),
        @Index(name = "idx_specimen_order_item", columnList = "order_item_id"),
        @Index(name = "idx_specimen_status", columnList = "status"),
        @Index(name = "idx_specimen_quality", columnList = "quality_status"),
        @Index(name = "idx_specimen_collected_at", columnList = "collected_at"),
        @Index(name = "idx_specimen_received_at", columnList = "received_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Specimen extends BaseEntity {

    /**
     * Specimen number (unique identifier)
     */
    @Column(name = "specimen_number", nullable = false, unique = true, length = 50)
    private String specimenNumber;

    /**
     * Barcode (for scanning and tracking)
     */
    @Column(name = "barcode", unique = true, length = 100)
    private String barcode;

    // ========== Order Reference ==========

    /**
     * Order reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private LabOrder order;

    /**
     * Order item reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private LabOrderItem orderItem;

    // ========== Specimen Details ==========

    /**
     * Specimen type
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "specimen_type", nullable = false, length = 50)
    private SampleType specimenType;

    /**
     * Specimen source (e.g., "Right arm", "First morning urine")
     */
    @Column(name = "specimen_source", length = 200)
    private String specimenSource;

    /**
     * Volume in ml
     */
    @Column(name = "volume_ml", precision = 10, scale = 2)
    private BigDecimal volumeMl;

    /**
     * Container type (e.g., "EDTA tube", "Plain tube")
     */
    @Column(name = "container_type", length = 100)
    private String containerType;

    // ========== Collection Information ==========

    /**
     * Collection timestamp
     */
    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    /**
     * Collected by user ID
     */
    @Column(name = "collected_by")
    private UUID collectedBy;

    /**
     * Collection method
     */
    @Column(name = "collection_method", length = 200)
    private String collectionMethod;

    /**
     * Collection site
     */
    @Column(name = "collection_site", length = 200)
    private String collectionSite;

    // ========== Reception ==========

    /**
     * Received timestamp (at laboratory)
     */
    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    /**
     * Received by user ID
     */
    @Column(name = "received_by")
    private UUID receivedBy;

    // ========== Quality Checks ==========

    /**
     * Quality status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "quality_status", length = 50)
    @Builder.Default
    private QualityStatus qualityStatus = QualityStatus.ACCEPTABLE;

    /**
     * Quality notes
     */
    @Column(name = "quality_notes", columnDefinition = "TEXT")
    private String qualityNotes;

    /**
     * Rejection reason (if rejected)
     */
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // ========== Pre-analytical Validations ==========

    /**
     * Fasting status met (if required)
     */
    @Column(name = "fasting_status_met")
    private Boolean fastingStatusMet;

    /**
     * Volume adequate
     */
    @Column(name = "volume_adequate")
    private Boolean volumeAdequate;

    /**
     * Container appropriate
     */
    @Column(name = "container_appropriate")
    private Boolean containerAppropriate;

    /**
     * Labeling correct
     */
    @Column(name = "labeling_correct")
    private Boolean labelingCorrect;

    /**
     * Temperature appropriate during transport
     */
    @Column(name = "temperature_appropriate")
    private Boolean temperatureAppropriate;

    /**
     * Hemolysis detected (for blood samples)
     */
    @Column(name = "hemolysis_detected")
    private Boolean hemolysisDetected;

    /**
     * Lipemia detected (for blood samples)
     */
    @Column(name = "lipemia_detected")
    private Boolean lipemiaDetected;

    /**
     * Icterus detected (for blood samples)
     */
    @Column(name = "icterus_detected")
    private Boolean icterusDetected;

    // ========== Storage ==========

    /**
     * Storage location
     */
    @Column(name = "storage_location", length = 200)
    private String storageLocation;

    /**
     * Storage temperature (in Celsius)
     */
    @Column(name = "storage_temperature", precision = 5, scale = 2)
    private BigDecimal storageTemperature;

    /**
     * Stored timestamp
     */
    @Column(name = "stored_at")
    private LocalDateTime storedAt;

    // ========== Processing ==========

    /**
     * Processing started timestamp
     */
    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    /**
     * Processing completed timestamp
     */
    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;

    /**
     * Processed by user ID
     */
    @Column(name = "processed_by")
    private UUID processedBy;

    // ========== Status Tracking ==========

    /**
     * Specimen status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @Builder.Default
    private SpecimenStatus status = SpecimenStatus.PENDING;

    // ========== Disposal ==========

    /**
     * Disposed timestamp
     */
    @Column(name = "disposed_at")
    private LocalDateTime disposedAt;

    /**
     * Disposed by user ID
     */
    @Column(name = "disposed_by")
    private UUID disposedBy;

    /**
     * Disposal method
     */
    @Column(name = "disposal_method", length = 100)
    private String disposalMethod;

    // ========== Additional Information ==========

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========== Helper Methods ==========

    /**
     * Check if specimen is acceptable for testing
     */
    public boolean isAcceptable() {
        return qualityStatus == QualityStatus.ACCEPTABLE;
    }

    /**
     * Check if specimen is rejected
     */
    public boolean isRejected() {
        return qualityStatus == QualityStatus.REJECTED;
    }

    /**
     * Check if specimen quality is compromised
     */
    public boolean isCompromised() {
        return qualityStatus == QualityStatus.COMPROMISED;
    }

    /**
     * Check if specimen has been collected
     */
    public boolean isCollected() {
        return collectedAt != null;
    }

    /**
     * Check if specimen has been received by lab
     */
    public boolean isReceived() {
        return receivedAt != null;
    }

    /**
     * Check if specimen is in processing
     */
    public boolean isProcessing() {
        return status == SpecimenStatus.PROCESSING;
    }

    /**
     * Check if processing is complete
     */
    public boolean isProcessingComplete() {
        return status == SpecimenStatus.COMPLETED;
    }

    /**
     * Check if any pre-analytical issue is detected
     */
    public boolean hasPreAnalyticalIssues() {
        return Boolean.FALSE.equals(volumeAdequate) ||
               Boolean.FALSE.equals(containerAppropriate) ||
               Boolean.FALSE.equals(labelingCorrect) ||
               Boolean.FALSE.equals(temperatureAppropriate) ||
               Boolean.TRUE.equals(hemolysisDetected) ||
               Boolean.TRUE.equals(lipemiaDetected) ||
               Boolean.TRUE.equals(icterusDetected);
    }
}
