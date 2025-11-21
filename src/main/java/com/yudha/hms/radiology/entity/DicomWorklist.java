package com.yudha.hms.radiology.entity;

import com.yudha.hms.radiology.constant.WorklistStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

/**
 * DICOM Worklist Entity.
 *
 * Represents DICOM Modality Worklist (MWL) entries for exam scheduling.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Entity
@Table(name = "dicom_worklist", schema = "radiology_schema", indexes = {
        @Index(name = "idx_dicom_worklist_order", columnList = "order_id"),
        @Index(name = "idx_dicom_worklist_order_item", columnList = "order_item_id"),
        @Index(name = "idx_dicom_worklist_accession", columnList = "accession_number"),
        @Index(name = "idx_dicom_worklist_status", columnList = "worklist_status"),
        @Index(name = "idx_dicom_worklist_scheduled_date", columnList = "scheduled_procedure_step_start_date"),
        @Index(name = "idx_dicom_worklist_modality", columnList = "modality_code")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DicomWorklist extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private RadiologyOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private RadiologyOrderItem orderItem;

    // Patient demographics for DICOM MWL
    @Column(name = "patient_id", nullable = false, length = 50)
    private String patientId;

    @Column(name = "patient_name", nullable = false, length = 200)
    private String patientName;

    @Column(name = "patient_birth_date")
    private LocalDate patientBirthDate;

    @Column(name = "patient_sex", length = 1)
    private String patientSex;

    // Study information
    @Column(name = "accession_number", nullable = false, unique = true, length = 50)
    private String accessionNumber;

    @Column(name = "study_instance_uid", length = 128)
    private String studyInstanceUid;

    @Column(name = "requested_procedure_id", length = 50)
    private String requestedProcedureId;

    @Column(name = "scheduled_procedure_step_id", length = 50)
    private String scheduledProcedureStepId;

    // Modality information
    @Column(name = "modality_code", nullable = false, length = 10)
    private String modalityCode;

    @Column(name = "scheduled_station_ae_title", length = 50)
    private String scheduledStationAeTitle;

    @Column(name = "scheduled_station_name", length = 100)
    private String scheduledStationName;

    // Scheduling
    @Column(name = "scheduled_procedure_step_start_date", nullable = false)
    private LocalDate scheduledProcedureStepStartDate;

    @Column(name = "scheduled_procedure_step_start_time", nullable = false)
    private LocalTime scheduledProcedureStepStartTime;

    @Column(name = "scheduled_procedure_step_end_date")
    private LocalDate scheduledProcedureStepEndDate;

    @Column(name = "scheduled_procedure_step_end_time")
    private LocalTime scheduledProcedureStepEndTime;

    // Procedure details
    @Column(name = "requested_procedure_description", columnDefinition = "TEXT")
    private String requestedProcedureDescription;

    @Column(name = "scheduled_procedure_step_description", columnDefinition = "TEXT")
    private String scheduledProcedureStepDescription;

    @Column(name = "scheduled_procedure_step_status", length = 20)
    @Builder.Default
    private String scheduledProcedureStepStatus = "SCHEDULED";

    // Referring physician
    @Column(name = "referring_physician_name", length = 200)
    private String referringPhysicianName;

    @Column(name = "referring_physician_id", length = 50)
    private String referringPhysicianId;

    // Performing physician
    @Column(name = "scheduled_performing_physician_name", length = 200)
    private String scheduledPerformingPhysicianName;

    // Study details
    @Column(name = "study_description", columnDefinition = "TEXT")
    private String studyDescription;

    @Column(name = "body_part_examined", length = 100)
    private String bodyPartExamined;

    @Column(name = "laterality", length = 10)
    private String laterality;

    // Worklist status
    @Enumerated(EnumType.STRING)
    @Column(name = "worklist_status", length = 20)
    @Builder.Default
    private WorklistStatus worklistStatus = WorklistStatus.PENDING;

    @Column(name = "sent_to_modality")
    @Builder.Default
    private Boolean sentToModality = false;

    @Column(name = "sent_to_modality_at")
    private LocalDateTime sentToModalityAt;

    @Column(name = "acknowledged_by_modality")
    @Builder.Default
    private Boolean acknowledgedByModality = false;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    // Completion tracking
    @Column(name = "procedure_started_at")
    private LocalDateTime procedureStartedAt;

    @Column(name = "procedure_completed_at")
    private LocalDateTime procedureCompletedAt;

    // Actual procedure step tracking
    @Column(name = "actual_procedure_step_start_date")
    private LocalDate actualProcedureStepStartDate;

    @Column(name = "actual_procedure_step_start_time")
    private LocalTime actualProcedureStepStartTime;

    @Column(name = "actual_procedure_step_end_date")
    private LocalDate actualProcedureStepEndDate;

    @Column(name = "actual_procedure_step_end_time")
    private LocalTime actualProcedureStepEndTime;

    // Cancellation
    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Additional information
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "custom_tags", columnDefinition = "jsonb")
    private Map<String, Object> customTags;
}
