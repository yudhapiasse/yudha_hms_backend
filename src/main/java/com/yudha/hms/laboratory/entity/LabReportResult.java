package com.yudha.hms.laboratory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lab Report Result Entity.
 *
 * Results included in a laboratory report.
 * Links reports to individual results with display configuration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "lab_report_result", schema = "laboratory_schema", indexes = {
        @Index(name = "idx_lab_report_result_report", columnList = "report_id"),
        @Index(name = "idx_lab_report_result_result", columnList = "result_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabReportResult {

    /**
     * Primary key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    /**
     * Report reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private LabReport report;

    /**
     * Result reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private LabResult result;

    // ========== Display Configuration ==========

    /**
     * Display order in report
     */
    @Column(name = "display_order")
    private Integer displayOrder;

    /**
     * Include parameters in report
     */
    @Column(name = "include_parameters")
    @Builder.Default
    private Boolean includeParameters = true;

    /**
     * Include interpretation in report
     */
    @Column(name = "include_interpretation")
    @Builder.Default
    private Boolean includeInterpretation = true;

    /**
     * Include reference ranges in report
     */
    @Column(name = "include_reference_ranges")
    @Builder.Default
    private Boolean includeReferenceRanges = true;

    // ========== Grouping ==========

    /**
     * Section name (for grouping results)
     */
    @Column(name = "section_name", length = 200)
    private String sectionName;

    /**
     * Section order
     */
    @Column(name = "section_order")
    private Integer sectionOrder;

    // ========== Audit Fields ==========

    /**
     * Created timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

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
}
