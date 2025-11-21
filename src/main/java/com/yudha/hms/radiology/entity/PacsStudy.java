package com.yudha.hms.radiology.entity;

import com.yudha.hms.radiology.constant.QAStatus;
import com.yudha.hms.radiology.constant.StudyStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

/**
 * PACS Study Entity.
 *
 * Represents radiology studies stored in PACS.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Entity
@Table(name = "pacs_study", schema = "radiology_schema", indexes = {
        @Index(name = "idx_pacs_study_order", columnList = "order_id"),
        @Index(name = "idx_pacs_study_uid", columnList = "study_instance_uid"),
        @Index(name = "idx_pacs_study_accession", columnList = "accession_number"),
        @Index(name = "idx_pacs_study_patient", columnList = "patient_id"),
        @Index(name = "idx_pacs_study_date", columnList = "study_date"),
        @Index(name = "idx_pacs_study_status", columnList = "study_status"),
        @Index(name = "idx_pacs_study_archived", columnList = "archived")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PacsStudy extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private RadiologyOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worklist_id")
    private DicomWorklist worklist;

    // DICOM identifiers
    @Column(name = "study_instance_uid", nullable = false, unique = true, length = 128)
    private String studyInstanceUid;

    @Column(name = "accession_number", nullable = false, length = 50)
    private String accessionNumber;

    @Column(name = "study_id", length = 50)
    private String studyId;

    // Patient information
    @Column(name = "patient_id", nullable = false, length = 50)
    private String patientId;

    @Column(name = "patient_name", length = 200)
    private String patientName;

    @Column(name = "patient_birth_date")
    private LocalDate patientBirthDate;

    @Column(name = "patient_sex", length = 1)
    private String patientSex;

    @Column(name = "patient_age", length = 10)
    private String patientAge;

    // Study details
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    @Column(name = "study_time")
    private LocalTime studyTime;

    @Column(name = "study_description", columnDefinition = "TEXT")
    private String studyDescription;

    @Column(name = "modality_code", nullable = false, length = 10)
    private String modalityCode;

    @Column(name = "body_part_examined", length = 100)
    private String bodyPartExamined;

    // Study metadata
    @Column(name = "number_of_series")
    @Builder.Default
    private Integer numberOfSeries = 0;

    @Column(name = "number_of_instances")
    @Builder.Default
    private Integer numberOfInstances = 0;

    @Column(name = "study_size_mb", precision = 12, scale = 2)
    private BigDecimal studySizeMb;

    // Referring physician
    @Column(name = "referring_physician_name", length = 200)
    private String referringPhysicianName;

    @Column(name = "referring_physician_id", length = 50)
    private String referringPhysicianId;

    // Performing physician
    @Column(name = "performing_physician_name", length = 200)
    private String performingPhysicianName;

    // Study status
    @Enumerated(EnumType.STRING)
    @Column(name = "study_status", length = 20)
    @Builder.Default
    private StudyStatus studyStatus = StudyStatus.IN_PROGRESS;

    @Column(name = "acquisition_complete")
    @Builder.Default
    private Boolean acquisitionComplete = false;

    @Column(name = "acquisition_completed_at")
    private LocalDateTime acquisitionCompletedAt;

    // Quality assurance
    @Enumerated(EnumType.STRING)
    @Column(name = "qa_status", length = 20)
    private QAStatus qaStatus;

    @Column(name = "qa_performed_by")
    private UUID qaPerformedBy;

    @Column(name = "qa_performed_at")
    private LocalDateTime qaPerformedAt;

    @Column(name = "qa_notes", columnDefinition = "TEXT")
    private String qaNotes;

    // PACS storage
    @Column(name = "pacs_location", length = 500)
    private String pacsLocation;

    @Column(name = "archived")
    @Builder.Default
    private Boolean archived = false;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "archival_rule_id")
    private UUID archivalRuleId;

    // Cloud storage
    @Column(name = "cloud_pacs_synced")
    @Builder.Default
    private Boolean cloudPacsSynced = false;

    @Column(name = "cloud_pacs_synced_at")
    private LocalDateTime cloudPacsSyncedAt;

    @Column(name = "cloud_pacs_url", length = 500)
    private String cloudPacsUrl;

    // Access and sharing
    @Column(name = "viewable_externally")
    @Builder.Default
    private Boolean viewableExternally = false;

    @Column(name = "share_expires_at")
    private LocalDateTime shareExpiresAt;

    // Additional metadata
    @Column(name = "institution_name", length = 200)
    private String institutionName;

    @Column(name = "station_name", length = 100)
    private String stationName;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "manufacturer_model", length = 100)
    private String manufacturerModel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_tags", columnDefinition = "jsonb")
    private Map<String, Object> customTags;
}
