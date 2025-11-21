package com.yudha.hms.laboratory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * TAT Monitoring Entity.
 *
 * Turnaround Time (TAT) monitoring and statistics.
 * Tracks time from order to result for quality monitoring and performance improvement.
 * Calculates TAT at each stage: collection, reception, processing, result entry, validation, reporting.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "tat_monitoring", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_tat_monitoring_order", columnList = "order_id"),
        @Index(name = "idx_tat_monitoring_test", columnList = "test_id"),
        @Index(name = "idx_tat_monitoring_priority", columnList = "priority"),
        @Index(name = "idx_tat_monitoring_delayed", columnList = "delayed"),
        @Index(name = "idx_tat_monitoring_tat_met", columnList = "tat_met"),
        @Index(name = "idx_tat_monitoring_order_placed", columnList = "order_placed_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TatMonitoring {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Order ID
     */
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /**
     * Test ID
     */
    @Column(name = "test_id", nullable = false)
    private UUID testId;

    // ========== Test Information ==========

    /**
     * Test name
     */
    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    /**
     * Test category
     */
    @Column(name = "test_category", length = 100)
    private String testCategory;

    /**
     * Priority
     */
    @Column(name = "priority", nullable = false, length = 50)
    private String priority;

    // ========== Time Stamps ==========

    /**
     * Order placed timestamp
     */
    @Column(name = "order_placed_at", nullable = false)
    private LocalDateTime orderPlacedAt;

    /**
     * Sample collected timestamp
     */
    @Column(name = "sample_collected_at")
    private LocalDateTime sampleCollectedAt;

    /**
     * Sample received timestamp (at lab)
     */
    @Column(name = "sample_received_at")
    private LocalDateTime sampleReceivedAt;

    /**
     * Processing started timestamp
     */
    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    /**
     * Result entered timestamp
     */
    @Column(name = "result_entered_at")
    private LocalDateTime resultEnteredAt;

    /**
     * Result validated timestamp
     */
    @Column(name = "result_validated_at")
    private LocalDateTime resultValidatedAt;

    /**
     * Result reported timestamp (sent to clinical)
     */
    @Column(name = "result_reported_at")
    private LocalDateTime resultReportedAt;

    // ========== TAT Calculations (in minutes) ==========

    /**
     * Collection TAT (order to collection)
     */
    @Column(name = "collection_tat")
    private Integer collectionTat;

    /**
     * Reception TAT (collection to reception)
     */
    @Column(name = "reception_tat")
    private Integer receptionTat;

    /**
     * Processing TAT (reception to processing start)
     */
    @Column(name = "processing_tat")
    private Integer processingTat;

    /**
     * Result entry TAT (processing start to result entry)
     */
    @Column(name = "result_entry_tat")
    private Integer resultEntryTat;

    /**
     * Validation TAT (result entry to validation)
     */
    @Column(name = "validation_tat")
    private Integer validationTat;

    /**
     * Reporting TAT (validation to reporting)
     */
    @Column(name = "reporting_tat")
    private Integer reportingTat;

    /**
     * Total TAT (order to reporting)
     */
    @Column(name = "total_tat")
    private Integer totalTat;

    // ========== Expected vs Actual ==========

    /**
     * Expected TAT in minutes
     */
    @Column(name = "expected_tat_minutes")
    private Integer expectedTatMinutes;

    /**
     * TAT met (actual TAT <= expected TAT)
     */
    @Column(name = "tat_met")
    private Boolean tatMet;

    /**
     * TAT variance in minutes (positive = late, negative = early)
     */
    @Column(name = "tat_variance_minutes")
    private Integer tatVarianceMinutes;

    // ========== Delay Analysis ==========

    /**
     * Delayed
     */
    @Column(name = "delayed")
    @Builder.Default
    private Boolean delayed = false;

    /**
     * Delay reason
     */
    @Column(name = "delay_reason", length = 500)
    private String delayReason;

    /**
     * Delay category (SPECIMEN_COLLECTION, TRANSPORTATION, EQUIPMENT, REAGENT, STAFFING, OTHER)
     */
    @Column(name = "delay_category", length = 100)
    private String delayCategory;

    // ========== Quality Indicator ==========

    /**
     * Critical TAT (for critical/life-threatening tests)
     */
    @Column(name = "critical_tat")
    @Builder.Default
    private Boolean criticalTat = false;

    /**
     * Urgent TAT
     */
    @Column(name = "urgent_tat")
    @Builder.Default
    private Boolean urgentTat = false;

    // ========== Audit Fields ==========

    /**
     * Created timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Created by user
     */
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /**
     * Version for optimistic locking
     */
    @Version
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Long version = 0L;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ========== Helper Methods ==========

    /**
     * Calculate TAT between two timestamps
     */
    private Integer calculateTatMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return null;
        }
        return (int) java.time.Duration.between(start, end).toMinutes();
    }

    /**
     * Calculate all TAT metrics
     */
    public void calculateAllTat() {
        this.collectionTat = calculateTatMinutes(orderPlacedAt, sampleCollectedAt);
        this.receptionTat = calculateTatMinutes(sampleCollectedAt, sampleReceivedAt);
        this.processingTat = calculateTatMinutes(sampleReceivedAt, processingStartedAt);
        this.resultEntryTat = calculateTatMinutes(processingStartedAt, resultEnteredAt);
        this.validationTat = calculateTatMinutes(resultEnteredAt, resultValidatedAt);
        this.reportingTat = calculateTatMinutes(resultValidatedAt, resultReportedAt);
        this.totalTat = calculateTatMinutes(orderPlacedAt, resultReportedAt);

        // Calculate TAT met and variance
        if (totalTat != null && expectedTatMinutes != null) {
            this.tatMet = totalTat <= expectedTatMinutes;
            this.tatVarianceMinutes = totalTat - expectedTatMinutes;
            this.delayed = !tatMet;
        }
    }

    /**
     * Check if TAT was met
     */
    public boolean isTatMet() {
        return Boolean.TRUE.equals(tatMet);
    }

    /**
     * Check if delayed
     */
    public boolean isDelayed() {
        return Boolean.TRUE.equals(delayed);
    }
}
