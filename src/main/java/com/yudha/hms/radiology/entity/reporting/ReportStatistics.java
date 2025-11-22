package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_statistics", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportStatistics extends SoftDeletableEntity {

    @Column(name = "statistics_date", nullable = false)
    private LocalDate statisticsDate;

    @Column(name = "period_type", length = 20, nullable = false)
    private String periodType;

    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    @Column(name = "radiologist_id")
    private UUID radiologistId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "modality_code", length = 10)
    private String modalityCode;

    @Column(name = "total_reports")
    private Integer totalReports = 0;

    @Column(name = "preliminary_reports")
    private Integer preliminaryReports = 0;

    @Column(name = "final_reports")
    private Integer finalReports = 0;

    @Column(name = "amended_reports")
    private Integer amendedReports = 0;

    @Column(name = "avg_reporting_time_minutes", precision = 10, scale = 2)
    private BigDecimal avgReportingTimeMinutes;

    @Column(name = "median_reporting_time_minutes", precision = 10, scale = 2)
    private BigDecimal medianReportingTimeMinutes;

    @Column(name = "reports_within_24_hours")
    private Integer reportsWithin24Hours = 0;

    @Column(name = "reports_over_24_hours")
    private Integer reportsOver24Hours = 0;

    @Column(name = "critical_findings_count")
    private Integer criticalFindingsCount = 0;

    @Column(name = "critical_findings_notified")
    private Integer criticalFindingsNotified = 0;

    @Column(name = "avg_notification_time_minutes", precision = 10, scale = 2)
    private BigDecimal avgNotificationTimeMinutes;

    @Column(name = "amendment_rate", precision = 5, scale = 2)
    private BigDecimal amendmentRate;

    @Column(name = "addendum_count")
    private Integer addendumCount = 0;

    @Column(name = "correction_count")
    private Integer correctionCount = 0;

    @Column(name = "simple_reports")
    private Integer simpleReports = 0;

    @Column(name = "moderate_reports")
    private Integer moderateReports = 0;

    @Column(name = "complex_reports")
    private Integer complexReports = 0;

    @Column(name = "transcribed_reports")
    private Integer transcribedReports = 0;

    @Column(name = "transcription_success_rate", precision = 5, scale = 2)
    private BigDecimal transcriptionSuccessRate;

    @Column(name = "reports_distributed")
    private Integer reportsDistributed = 0;

    @Column(name = "avg_distribution_time_minutes", precision = 10, scale = 2)
    private BigDecimal avgDistributionTimeMinutes;

    @Column(name = "failed_distributions")
    private Integer failedDistributions = 0;

    @Column(name = "reports_with_comparison")
    private Integer reportsWithComparison = 0;

    @Type(JsonBinaryType.class)
    @Column(name = "template_usage", columnDefinition = "jsonb")
    private Object templateUsage;

    @Column(name = "computed_at", nullable = false)
    private LocalDateTime computedAt;

    @Column(name = "computed_by", length = 100)
    private String computedBy;
}
