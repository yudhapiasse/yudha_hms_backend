package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Radiology Result Request DTO.
 *
 * Used for creating and updating radiology examination results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyResultRequest {

    /**
     * Order item ID
     */
    @NotNull(message = "ID item pemeriksaan harus diisi")
    private UUID orderItemId;

    /**
     * Examination ID
     */
    @NotNull(message = "ID pemeriksaan harus diisi")
    private UUID examinationId;

    /**
     * Patient ID
     */
    @NotNull(message = "ID pasien harus diisi")
    private UUID patientId;

    /**
     * Performed date and time
     */
    private LocalDateTime performedDate;

    /**
     * Performed by technician ID
     */
    private UUID performedByTechnicianId;

    /**
     * Findings
     */
    private String findings;

    /**
     * Impression
     */
    private String impression;

    /**
     * Recommendations
     */
    private String recommendations;

    /**
     * Radiologist ID
     */
    private UUID radiologistId;

    /**
     * DICOM Study ID
     */
    private String dicomStudyId;

    /**
     * Number of images
     */
    private Integer imageCount;

    /**
     * Additional notes
     */
    private String notes;
}
