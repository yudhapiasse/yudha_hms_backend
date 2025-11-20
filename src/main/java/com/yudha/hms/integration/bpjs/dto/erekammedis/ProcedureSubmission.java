package com.yudha.hms.integration.bpjs.dto.erekammedis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS eRekam Medis Procedure Submission.
 *
 * Procedure/action data submission using ICD-9-CM codes to BPJS electronic medical record system.
 * Complies with FHIR R4 Procedure resource structure and SATUSEHAT interoperability.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureSubmission {

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
     * List of procedures performed.
     */
    @JsonProperty("procedures")
    private List<Procedure> procedures;

    /**
     * Encounter type: rawat-jalan, rawat-inap, emergency, surgery.
     */
    @JsonProperty("encounterType")
    private String encounterType;

    /**
     * Organization/facility code.
     */
    @JsonProperty("organizationCode")
    private String organizationCode;

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
    public static class Procedure {
        /**
         * ICD-9-CM procedure code.
         * Example: "99.04" (Transfusion of packed cells)
         */
        @JsonProperty("icd9Code")
        private String icd9Code;

        /**
         * ICD-9-CM procedure description (Indonesian).
         */
        @JsonProperty("icd9Description")
        private String icd9Description;

        /**
         * Status: preparation, in-progress, not-done, on-hold, stopped, completed, entered-in-error, unknown.
         * FHIR R4 Procedure.status
         */
        @JsonProperty("status")
        private String status;

        /**
         * Status reason (if not-done or stopped).
         */
        @JsonProperty("statusReason")
        private String statusReason;

        /**
         * Category: diagnostic, therapeutic, surgical, nursing, educational.
         */
        @JsonProperty("category")
        private String category;

        /**
         * Procedure performed date and time (yyyy-MM-dd HH:mm:ss).
         */
        @JsonProperty("performedDateTime")
        private String performedDateTime;

        /**
         * Procedure duration in minutes.
         */
        @JsonProperty("durationMinutes")
        private Integer durationMinutes;

        /**
         * Primary performer (surgeon/practitioner).
         */
        @JsonProperty("performer")
        private Performer performer;

        /**
         * Additional performers (assistants, nurses, anesthesiologists).
         */
        @JsonProperty("additionalPerformers")
        private List<Performer> additionalPerformers;

        /**
         * Location where procedure was performed.
         */
        @JsonProperty("location")
        private String location;

        /**
         * Outcome: successful, unsuccessful, partially-successful.
         */
        @JsonProperty("outcome")
        private String outcome;

        /**
         * Complications or adverse events.
         */
        @JsonProperty("complications")
        private String complications;

        /**
         * Follow-up instructions.
         */
        @JsonProperty("followUp")
        private String followUp;

        /**
         * Additional notes about the procedure.
         */
        @JsonProperty("notes")
        private String notes;

        /**
         * Body site where procedure was performed.
         */
        @JsonProperty("bodySite")
        private String bodySite;

        /**
         * Reason for procedure (diagnosis or indication).
         */
        @JsonProperty("reason")
        private String reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Performer {
        /**
         * Practitioner code.
         */
        @JsonProperty("practitionerCode")
        private String practitionerCode;

        /**
         * Practitioner name.
         */
        @JsonProperty("practitionerName")
        private String practitionerName;

        /**
         * Role: surgeon, assistant-surgeon, anesthesiologist, nurse, technician.
         */
        @JsonProperty("role")
        private String role;

        /**
         * Specialty.
         */
        @JsonProperty("specialty")
        private String specialty;
    }
}
