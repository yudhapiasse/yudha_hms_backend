package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.radiology.constant.reporting.ComparisonChange;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_comparison", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportComparison extends SoftDeletableEntity {

    @Column(name = "current_report_id", nullable = false)
    private UUID currentReportId;

    @Column(name = "prior_report_id", nullable = false)
    private UUID priorReportId;

    @Column(name = "prior_study_id")
    private UUID priorStudyId;

    @Column(name = "comparison_date", nullable = false)
    private LocalDate comparisonDate;

    @Column(name = "time_interval_days")
    private Integer timeIntervalDays;

    @Column(name = "comparison_category", length = 50)
    private String comparisonCategory;

    @Column(name = "comparison_type", length = 30)
    private String comparisonType;

    @Type(JsonBinaryType.class)
    @Column(name = "new_findings", columnDefinition = "jsonb")
    private Object newFindings;

    @Type(JsonBinaryType.class)
    @Column(name = "resolved_findings", columnDefinition = "jsonb")
    private Object resolvedFindings;

    @Type(JsonBinaryType.class)
    @Column(name = "stable_findings", columnDefinition = "jsonb")
    private Object stableFindings;

    @Type(JsonBinaryType.class)
    @Column(name = "progressed_findings", columnDefinition = "jsonb")
    private Object progressedFindings;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_change", length = 30)
    private ComparisonChange overallChange;

    @Column(name = "change_summary", columnDefinition = "TEXT")
    private String changeSummary;

    @Column(name = "clinical_significance", columnDefinition = "TEXT")
    private String clinicalSignificance;

    @Column(name = "significant_changes", columnDefinition = "text[]")
    private String[] significantChanges;

    @Type(JsonBinaryType.class)
    @Column(name = "measurements_comparison", columnDefinition = "jsonb")
    private Object measurementsComparison;

    @Column(name = "follow_up_recommendations", columnDefinition = "TEXT")
    private String followUpRecommendations;

    @Column(name = "recommended_interval_days")
    private Integer recommendedIntervalDays;

    @Column(name = "compared_by", nullable = false)
    private UUID comparedBy;

    @Column(name = "compared_at", nullable = false)
    private LocalDateTime comparedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
