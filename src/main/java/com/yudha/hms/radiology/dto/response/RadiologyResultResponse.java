package com.yudha.hms.radiology.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Radiology Result Response DTO.
 *
 * Response for radiology examination result information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologyResultResponse {

    /**
     * Result ID
     */
    private UUID id;

    /**
     * Result number
     */
    private String resultNumber;

    // ========== Order and Examination Information ==========

    /**
     * Order item ID
     */
    private UUID orderItemId;

    /**
     * Order number
     */
    private String orderNumber;

    /**
     * Examination ID
     */
    private UUID examinationId;

    /**
     * Examination code
     */
    private String examinationCode;

    /**
     * Examination name
     */
    private String examinationName;

    /**
     * Modality code
     */
    private String modalityCode;

    /**
     * Modality name
     */
    private String modalityName;

    // ========== Patient Information ==========

    /**
     * Patient ID
     */
    private UUID patientId;

    /**
     * Patient name
     */
    private String patientName;

    /**
     * Patient MRN
     */
    private String patientMrn;

    /**
     * Patient age
     */
    private Integer patientAge;

    /**
     * Patient gender
     */
    private String patientGender;

    // ========== Examination Execution ==========

    /**
     * Performed date
     */
    private LocalDateTime performedDate;

    /**
     * Performed by technician ID
     */
    private UUID performedByTechnicianId;

    /**
     * Technician name
     */
    private String technicianName;

    // ========== Report Content ==========

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

    // ========== Radiologist Information ==========

    /**
     * Radiologist ID
     */
    private UUID radiologistId;

    /**
     * Radiologist name
     */
    private String radiologistName;

    /**
     * Reported date
     */
    private LocalDateTime reportedDate;

    // ========== Status ==========

    /**
     * Is finalized
     */
    private Boolean isFinalized;

    /**
     * Finalized date
     */
    private LocalDateTime finalizedDate;

    /**
     * Is amended
     */
    private Boolean isAmended;

    /**
     * Amendment reason
     */
    private String amendmentReason;

    // ========== Images ==========

    /**
     * Image count
     */
    private Integer imageCount;

    /**
     * Images list
     */
    private List<RadiologyImageResponse> images;

    /**
     * DICOM study ID
     */
    private String dicomStudyId;

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
