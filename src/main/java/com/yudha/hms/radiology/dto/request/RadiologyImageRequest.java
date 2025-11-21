package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Image Request DTO.
 *
 * Used for uploading and managing radiology images.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyImageRequest {

    /**
     * Result ID
     */
    @NotNull(message = "ID hasil harus diisi")
    private UUID resultId;

    // ========== DICOM Information ==========

    /**
     * DICOM Study UID
     */
    @NotBlank(message = "DICOM Study UID harus diisi")
    @Size(max = 100, message = "DICOM Study UID maksimal 100 karakter")
    private String dicomStudyUid;

    /**
     * DICOM Series UID
     */
    @Size(max = 100, message = "DICOM Series UID maksimal 100 karakter")
    private String dicomSeriesUid;

    /**
     * DICOM Instance UID
     */
    @Size(max = 100, message = "DICOM Instance UID maksimal 100 karakter")
    private String dicomInstanceUid;

    /**
     * Modality (from DICOM)
     */
    @Size(max = 50, message = "Modalitas maksimal 50 karakter")
    private String modality;

    /**
     * Body part examined
     */
    @Size(max = 100, message = "Bagian tubuh maksimal 100 karakter")
    private String bodyPartExamined;

    /**
     * Image type
     */
    @Size(max = 50, message = "Tipe gambar maksimal 50 karakter")
    private String imageType;

    // ========== File Information ==========

    /**
     * File path in storage
     */
    @Size(max = 500, message = "Path file maksimal 500 karakter")
    private String filePath;

    /**
     * File size in bytes
     */
    private Long fileSizeBytes;

    /**
     * Acquisition date and time
     */
    private LocalDateTime acquisitionDate;

    // ========== Positioning ==========

    /**
     * View position (AP, PA, LAT, etc.)
     */
    @Size(max = 50, message = "Posisi tampilan maksimal 50 karakter")
    private String viewPosition;

    /**
     * Is key image
     */
    @Builder.Default
    private Boolean isKeyImage = false;

    /**
     * Notes
     */
    private String notes;
}
