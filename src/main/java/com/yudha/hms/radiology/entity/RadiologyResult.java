package com.yudha.hms.radiology.entity;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Result Entity.
 *
 * Examination results/reports
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "radiology_result", schema = "radiology_schema", indexes = {
        @Index(name = "idx_radiology_result_number", columnList = "result_number", unique = true),
        @Index(name = "idx_radiology_result_order_item", columnList = "order_item_id"),
        @Index(name = "idx_radiology_result_examination", columnList = "examination_id"),
        @Index(name = "idx_radiology_result_patient", columnList = "patient_id"),
        @Index(name = "idx_radiology_result_performed_date", columnList = "performed_date"),
        @Index(name = "idx_radiology_result_technician", columnList = "performed_by_technician_id"),
        @Index(name = "idx_radiology_result_radiologist", columnList = "radiologist_id"),
        @Index(name = "idx_radiology_result_finalized", columnList = "is_finalized"),
        @Index(name = "idx_radiology_result_dicom_study", columnList = "dicom_study_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RadiologyResult extends SoftDeletableEntity {

    /**
     * Result number (unique identifier)
     */
    @Column(name = "result_number", nullable = false, unique = true, length = 50)
    private String resultNumber;

    /**
     * Order item reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private RadiologyOrderItem orderItem;

    /**
     * Examination reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false)
    private RadiologyExamination examination;

    /**
     * Patient reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Performed date
     */
    @Column(name = "performed_date")
    private LocalDateTime performedDate;

    /**
     * Performed by technician ID
     */
    @Column(name = "performed_by_technician_id")
    private UUID performedByTechnicianId;

    /**
     * Findings
     */
    @Column(name = "findings", columnDefinition = "TEXT")
    private String findings;

    /**
     * Impression
     */
    @Column(name = "impression", columnDefinition = "TEXT")
    private String impression;

    /**
     * Recommendations
     */
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    /**
     * Radiologist ID
     */
    @Column(name = "radiologist_id")
    private UUID radiologistId;

    /**
     * Reported date
     */
    @Column(name = "reported_date")
    private LocalDateTime reportedDate;

    /**
     * Whether result is finalized
     */
    @Column(name = "is_finalized")
    @Builder.Default
    private Boolean isFinalized = false;

    /**
     * Finalized date
     */
    @Column(name = "finalized_date")
    private LocalDateTime finalizedDate;

    /**
     * Whether result is amended
     */
    @Column(name = "is_amended")
    @Builder.Default
    private Boolean isAmended = false;

    /**
     * Amendment reason
     */
    @Column(name = "amendment_reason", columnDefinition = "TEXT")
    private String amendmentReason;

    /**
     * Image count
     */
    @Column(name = "image_count")
    @Builder.Default
    private Integer imageCount = 0;

    /**
     * DICOM study ID
     */
    @Column(name = "dicom_study_id", length = 100)
    private String dicomStudyId;
}
