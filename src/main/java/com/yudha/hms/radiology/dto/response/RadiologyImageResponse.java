package com.yudha.hms.radiology.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Image Response DTO.
 *
 * Response for radiology image information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyImageResponse {

    /**
     * Image ID
     */
    private UUID id;

    /**
     * Result ID
     */
    private UUID resultId;

    /**
     * Result number
     */
    private String resultNumber;

    // ========== DICOM Information ==========

    /**
     * DICOM Study UID
     */
    private String dicomStudyUid;

    /**
     * DICOM Series UID
     */
    private String dicomSeriesUid;

    /**
     * DICOM Instance UID
     */
    private String dicomInstanceUid;

    /**
     * Modality
     */
    private String modality;

    /**
     * Body part examined
     */
    private String bodyPartExamined;

    /**
     * Image type
     */
    private String imageType;

    // ========== File Information ==========

    /**
     * File path
     */
    private String filePath;

    /**
     * File URL (for display)
     */
    private String fileUrl;

    /**
     * Thumbnail URL
     */
    private String thumbnailUrl;

    /**
     * File size in bytes
     */
    private Long fileSizeBytes;

    /**
     * File size formatted (e.g., "2.5 MB")
     */
    private String fileSizeFormatted;

    /**
     * Acquisition date
     */
    private LocalDateTime acquisitionDate;

    // ========== Positioning ==========

    /**
     * View position
     */
    private String viewPosition;

    /**
     * Is key image
     */
    private Boolean isKeyImage;

    /**
     * Notes
     */
    private String notes;

    // ========== Audit Fields ==========

    /**
     * Created at
     */
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
