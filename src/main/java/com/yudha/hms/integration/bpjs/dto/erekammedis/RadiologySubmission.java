package com.yudha.hms.integration.bpjs.dto.erekammedis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS eRekam Medis Radiology Results Submission.
 *
 * Radiology/imaging test results submission to BPJS electronic medical record system.
 * Complies with FHIR R4 ImagingStudy and DiagnosticReport resources.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadiologySubmission {

    /**
     * Encounter ID (visit/admission number).
     */
    @JsonProperty("encounterId")
    private String encounterId;

    /**
     * SEP number (if BPJS patient).
     */
    @JsonProperty("sepNumber")
    private String sepNumber;

    /**
     * Medical record number.
     */
    @JsonProperty("medicalRecordNumber")
    private String medicalRecordNumber;

    /**
     * BPJS card number (if BPJS patient).
     */
    @JsonProperty("bpjsCardNumber")
    private String bpjsCardNumber;

    /**
     * Radiology order number (hospital internal).
     */
    @JsonProperty("orderNumber")
    private String orderNumber;

    /**
     * ImagingStudy identifier.
     */
    @JsonProperty("studyId")
    private String studyId;

    /**
     * Accession number (PACS identifier).
     */
    @JsonProperty("accessionNumber")
    private String accessionNumber;

    /**
     * Modality: CR (Computed Radiography), CT (Computed Tomography), MR (Magnetic Resonance),
     * US (Ultrasound), XA (X-Ray Angiography), DX (Digital Radiography), etc.
     */
    @JsonProperty("modality")
    private String modality;

    /**
     * Body part examined.
     */
    @JsonProperty("bodyPart")
    private String bodyPart;

    /**
     * Study description (e.g., "Chest X-Ray PA", "CT Brain without contrast").
     */
    @JsonProperty("studyDescription")
    private String studyDescription;

    /**
     * Report status: registered, partial, preliminary, final, amended, corrected, cancelled, entered-in-error.
     */
    @JsonProperty("status")
    private String status;

    /**
     * List of series in this study.
     */
    @JsonProperty("series")
    private List<Series> series;

    /**
     * Radiologist findings/impression.
     */
    @JsonProperty("findings")
    private String findings;

    /**
     * Radiologist impression/conclusion.
     */
    @JsonProperty("impression")
    private String impression;

    /**
     * Recommendations.
     */
    @JsonProperty("recommendations")
    private String recommendations;

    /**
     * Ordering practitioner code.
     */
    @JsonProperty("orderingPractitionerCode")
    private String orderingPractitionerCode;

    /**
     * Ordering practitioner name.
     */
    @JsonProperty("orderingPractitionerName")
    private String orderingPractitionerName;

    /**
     * Interpreting radiologist code.
     */
    @JsonProperty("radiologistCode")
    private String radiologistCode;

    /**
     * Interpreting radiologist name.
     */
    @JsonProperty("radiologistName")
    private String radiologistName;

    /**
     * Study date and time (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("studyDateTime")
    private String studyDateTime;

    /**
     * Report date and time (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("reportDateTime")
    private String reportDateTime;

    /**
     * Organization/facility code.
     */
    @JsonProperty("organizationCode")
    private String organizationCode;

    /**
     * Radiology department/location.
     */
    @JsonProperty("radiologyDepartment")
    private String radiologyDepartment;

    /**
     * Submission timestamp (ISO 8601 format).
     */
    @JsonProperty("submittedAt")
    private String submittedAt;

    /**
     * Submitter user ID.
     */
    @JsonProperty("submittedBy")
    private String submittedBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Series {
        /**
         * Series instance UID (DICOM).
         */
        @JsonProperty("seriesUid")
        private String seriesUid;

        /**
         * Series number.
         */
        @JsonProperty("seriesNumber")
        private Integer seriesNumber;

        /**
         * Series description.
         */
        @JsonProperty("seriesDescription")
        private String seriesDescription;

        /**
         * Modality for this series.
         */
        @JsonProperty("modality")
        private String modality;

        /**
         * Body part examined in this series.
         */
        @JsonProperty("bodyPart")
        private String bodyPart;

        /**
         * Number of instances (images) in this series.
         */
        @JsonProperty("numberOfInstances")
        private Integer numberOfInstances;

        /**
         * Series date and time (yyyy-MM-dd HH:mm:ss).
         */
        @JsonProperty("seriesDateTime")
        private String seriesDateTime;

        /**
         * Performing physician/technician.
         */
        @JsonProperty("performingPhysician")
        private String performingPhysician;

        /**
         * List of instances/images.
         */
        @JsonProperty("instances")
        private List<Instance> instances;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Instance {
        /**
         * SOP Instance UID (DICOM).
         */
        @JsonProperty("sopInstanceUid")
        private String sopInstanceUid;

        /**
         * Instance number.
         */
        @JsonProperty("instanceNumber")
        private Integer instanceNumber;

        /**
         * SOP Class UID (DICOM image type).
         */
        @JsonProperty("sopClassUid")
        private String sopClassUid;

        /**
         * Image title/description.
         */
        @JsonProperty("title")
        private String title;

        /**
         * Image URL (if available via PACS/WADO).
         */
        @JsonProperty("imageUrl")
        private String imageUrl;

        /**
         * Thumbnail URL (if available).
         */
        @JsonProperty("thumbnailUrl")
        private String thumbnailUrl;
    }
}
