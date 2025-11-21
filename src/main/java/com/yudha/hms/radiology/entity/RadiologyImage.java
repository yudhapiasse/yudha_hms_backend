package com.yudha.hms.radiology.entity;

import com.yudha.hms.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Radiology Image Entity.
 *
 * DICOM images/studies
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "radiology_image", schema = "radiology_schema", indexes = {
        @Index(name = "idx_radiology_image_result", columnList = "result_id"),
        @Index(name = "idx_radiology_image_dicom_study", columnList = "dicom_study_uid"),
        @Index(name = "idx_radiology_image_dicom_series", columnList = "dicom_series_uid"),
        @Index(name = "idx_radiology_image_dicom_instance", columnList = "dicom_instance_uid")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RadiologyImage extends BaseEntity {

    /**
     * Result reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private RadiologyResult result;

    /**
     * Image number
     */
    @Column(name = "image_number", nullable = false)
    private Integer imageNumber;

    /**
     * DICOM study UID
     */
    @Column(name = "dicom_study_uid", length = 100)
    private String dicomStudyUid;

    /**
     * DICOM series UID
     */
    @Column(name = "dicom_series_uid", length = 100)
    private String dicomSeriesUid;

    /**
     * DICOM instance UID
     */
    @Column(name = "dicom_instance_uid", length = 100)
    private String dicomInstanceUid;

    /**
     * Modality
     */
    @Column(name = "modality", length = 50)
    private String modality;

    /**
     * Body part examined
     */
    @Column(name = "body_part_examined", length = 200)
    private String bodyPartExamined;

    /**
     * Image type
     */
    @Column(name = "image_type", length = 100)
    private String imageType;

    /**
     * File path
     */
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * File size in bytes
     */
    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    /**
     * Acquisition date
     */
    @Column(name = "acquisition_date")
    private LocalDateTime acquisitionDate;

    /**
     * View position
     */
    @Column(name = "view_position", length = 100)
    private String viewPosition;

    /**
     * Whether this is a key image
     */
    @Column(name = "is_key_image")
    @Builder.Default
    private Boolean isKeyImage = false;

    /**
     * Notes
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
