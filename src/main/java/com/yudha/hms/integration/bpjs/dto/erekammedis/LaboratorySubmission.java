package com.yudha.hms.integration.bpjs.dto.erekammedis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS eRekam Medis Laboratory Results Submission.
 *
 * Laboratory test results submission to BPJS electronic medical record system.
 * Complies with FHIR R4 Observation and DiagnosticReport resources, uses LOINC codes.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorySubmission {

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
     * Laboratory order number (hospital internal).
     */
    @JsonProperty("orderNumber")
    private String orderNumber;

    /**
     * DiagnosticReport identifier.
     */
    @JsonProperty("reportId")
    private String reportId;

    /**
     * Report category: hematology, chemistry, microbiology, immunology, pathology, etc.
     */
    @JsonProperty("category")
    private String category;

    /**
     * Report status: registered, partial, preliminary, final, amended, corrected, cancelled, entered-in-error.
     */
    @JsonProperty("status")
    private String status;

    /**
     * List of observations/test results.
     */
    @JsonProperty("observations")
    private List<Observation> observations;

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
     * Specimen collection date and time (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("specimenCollectedAt")
    private String specimenCollectedAt;

    /**
     * Result issued date and time (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("resultIssuedAt")
    private String resultIssuedAt;

    /**
     * Overall conclusion/interpretation.
     */
    @JsonProperty("conclusion")
    private String conclusion;

    /**
     * Organization/facility code.
     */
    @JsonProperty("organizationCode")
    private String organizationCode;

    /**
     * Laboratory name/location.
     */
    @JsonProperty("laboratoryName")
    private String laboratoryName;

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
    public static class Observation {
        /**
         * LOINC code for the test (if available).
         * Example: "718-7" for Hemoglobin
         */
        @JsonProperty("loincCode")
        private String loincCode;

        /**
         * Local test code (hospital laboratory code).
         */
        @JsonProperty("localCode")
        private String localCode;

        /**
         * Test name.
         */
        @JsonProperty("testName")
        private String testName;

        /**
         * Test result value.
         */
        @JsonProperty("value")
        private String value;

        /**
         * Unit of measurement (e.g., "g/dL", "mmol/L", "%").
         */
        @JsonProperty("unit")
        private String unit;

        /**
         * Reference range (normal values).
         */
        @JsonProperty("referenceRange")
        private ReferenceRange referenceRange;

        /**
         * Interpretation: normal, high, low, critical-high, critical-low.
         */
        @JsonProperty("interpretation")
        private String interpretation;

        /**
         * Status: registered, preliminary, final, amended, corrected, cancelled, entered-in-error.
         */
        @JsonProperty("status")
        private String status;

        /**
         * Observation date and time (yyyy-MM-dd HH:mm:ss).
         */
        @JsonProperty("observedAt")
        private String observedAt;

        /**
         * Specimen type (e.g., "whole blood", "serum", "urine").
         */
        @JsonProperty("specimenType")
        private String specimenType;

        /**
         * Method used for the test.
         */
        @JsonProperty("method")
        private String method;

        /**
         * Additional notes or comments.
         */
        @JsonProperty("notes")
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceRange {
        /**
         * Low value of reference range.
         */
        @JsonProperty("low")
        private Double low;

        /**
         * High value of reference range.
         */
        @JsonProperty("high")
        private Double high;

        /**
         * Text representation (e.g., "12-16 g/dL", "< 200 mg/dL").
         */
        @JsonProperty("text")
        private String text;

        /**
         * Age range applicability (if age-dependent).
         */
        @JsonProperty("ageRange")
        private String ageRange;

        /**
         * Gender applicability: male, female, all.
         */
        @JsonProperty("gender")
        private String gender;
    }
}
