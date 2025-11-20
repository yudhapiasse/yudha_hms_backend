package com.yudha.hms.integration.bpjs.dto.erekammedis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS eRekam Medis Diagnosis Submission.
 *
 * Diagnosis data submission using ICD-10 codes to BPJS electronic medical record system.
 * Complies with FHIR R4 Condition resource structure and SATUSEHAT interoperability.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisSubmission {

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
     * Primary diagnosis (Diagnosis utama).
     */
    @JsonProperty("primaryDiagnosis")
    private Diagnosis primaryDiagnosis;

    /**
     * Secondary diagnoses (Diagnosis sekunder).
     */
    @JsonProperty("secondaryDiagnoses")
    private List<Diagnosis> secondaryDiagnoses;

    /**
     * Encounter type: rawat-jalan, rawat-inap, emergency.
     */
    @JsonProperty("encounterType")
    private String encounterType;

    /**
     * Encounter date (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("encounterDate")
    private String encounterDate;

    /**
     * Diagnosing practitioner code.
     */
    @JsonProperty("practitionerCode")
    private String practitionerCode;

    /**
     * Diagnosing practitioner name.
     */
    @JsonProperty("practitionerName")
    private String practitionerName;

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
    public static class Diagnosis {
        /**
         * ICD-10 code.
         * Example: "A09" (Diarrhoea and gastroenteritis of presumed infectious origin)
         */
        @JsonProperty("icd10Code")
        private String icd10Code;

        /**
         * ICD-10 description (Indonesian).
         */
        @JsonProperty("icd10Description")
        private String icd10Description;

        /**
         * Clinical status: active, recurrence, relapse, inactive, remission, resolved.
         * FHIR R4 Condition.clinicalStatus
         */
        @JsonProperty("clinicalStatus")
        private String clinicalStatus;

        /**
         * Verification status: unconfirmed, provisional, differential, confirmed, refuted, entered-in-error.
         * FHIR R4 Condition.verificationStatus
         */
        @JsonProperty("verificationStatus")
        private String verificationStatus;

        /**
         * Severity: mild, moderate, severe.
         */
        @JsonProperty("severity")
        private String severity;

        /**
         * Onset date (yyyy-MM-dd).
         */
        @JsonProperty("onsetDate")
        private String onsetDate;

        /**
         * Recorded date (yyyy-MM-dd HH:mm:ss).
         */
        @JsonProperty("recordedDate")
        private String recordedDate;

        /**
         * Additional notes about the diagnosis.
         */
        @JsonProperty("notes")
        private String notes;

        /**
         * Diagnosis category: problem-list-item, encounter-diagnosis.
         */
        @JsonProperty("category")
        private String category;
    }
}
