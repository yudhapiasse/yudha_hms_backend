package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.radiology.constant.reporting.ReportComplexity;
import com.yudha.hms.radiology.constant.reporting.ReportStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "radiology_report", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RadiologyReport extends SoftDeletableEntity {

    @Column(name = "report_number", length = 50, nullable = false, unique = true)
    private String reportNumber;

    @Column(name = "accession_number", length = 50, nullable = false)
    private String accessionNumber;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "study_id")
    private UUID studyId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "encounter_id")
    private UUID encounterId;

    @Column(name = "template_id")
    private UUID templateId;

    @Column(name = "examination_date", nullable = false)
    private LocalDate examinationDate;

    @Column(name = "examination_time")
    private LocalTime examinationTime;

    @Column(name = "modality_code", length = 10, nullable = false)
    private String modalityCode;

    @Column(name = "body_part", length = 100)
    private String bodyPart;

    @Column(name = "procedure_name", length = 200)
    private String procedureName;

    @Column(name = "clinical_indication", columnDefinition = "TEXT")
    private String clinicalIndication;

    @Column(name = "clinical_history", columnDefinition = "TEXT")
    private String clinicalHistory;

    @Column(name = "relevant_previous_imaging", columnDefinition = "TEXT")
    private String relevantPreviousImaging;

    @Column(name = "technique", columnDefinition = "TEXT")
    private String technique;

    @Type(JsonBinaryType.class)
    @Column(name = "findings", columnDefinition = "jsonb")
    private Object findings;

    @Column(name = "findings_text", columnDefinition = "TEXT")
    private String findingsText;

    @Column(name = "impression", columnDefinition = "TEXT")
    private String impression;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "comparison_text", columnDefinition = "TEXT")
    private String comparisonText;

    @Column(name = "compared_to_study_id")
    private UUID comparedToStudyId;

    @Column(name = "comparison_summary", columnDefinition = "TEXT")
    private String comparisonSummary;

    @Column(name = "has_critical_findings")
    private Boolean hasCriticalFindings = false;

    @Column(name = "critical_findings_text", columnDefinition = "TEXT")
    private String criticalFindingsText;

    @Column(name = "critical_findings_communicated")
    private Boolean criticalFindingsCommunicated = false;

    @Column(name = "critical_findings_communicated_at")
    private LocalDateTime criticalFindingsCommunicatedAt;

    @Column(name = "critical_findings_communicated_to", length = 200)
    private String criticalFindingsCommunicatedTo;

    @Column(name = "reported_by", nullable = false)
    private UUID reportedBy;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", length = 30, nullable = false)
    private ReportStatus reportStatus = ReportStatus.DRAFT;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "transcription_id")
    private UUID transcriptionId;

    @Column(name = "transcribed_from_audio")
    private Boolean transcribedFromAudio = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_complexity", length = 20)
    private ReportComplexity reportComplexity;

    @Column(name = "time_to_report_minutes")
    private Integer timeToReportMinutes;

    @Column(name = "auto_distributed")
    private Boolean autoDistributed = false;

    @Column(name = "distributed_at")
    private LocalDateTime distributedAt;

    @Column(name = "billing_code", length = 20)
    private String billingCode;

    @Column(name = "billing_status", length = 20)
    private String billingStatus;

    @Type(JsonBinaryType.class)
    @Column(name = "custom_fields", columnDefinition = "jsonb")
    private Object customFields;

    @Type(JsonBinaryType.class)
    @Column(name = "attachments", columnDefinition = "jsonb")
    private Object attachments;

    @Column(name = "cancelled")
    private Boolean cancelled = false;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private UUID cancelledBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
