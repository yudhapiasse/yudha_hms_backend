package com.yudha.hms.integration.bpjs.dto.erekammedis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * BPJS eRekam Medis Medication Submission.
 *
 * Medication prescription and administration records submission to BPJS electronic medical record system.
 * Complies with FHIR R4 MedicationRequest and MedicationStatement resources.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationSubmission {

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
     * Prescription number (hospital internal).
     */
    @JsonProperty("prescriptionNumber")
    private String prescriptionNumber;

    /**
     * List of medications prescribed/administered.
     */
    @JsonProperty("medications")
    private List<Medication> medications;

    /**
     * Prescriber practitioner code.
     */
    @JsonProperty("prescriberCode")
    private String prescriberCode;

    /**
     * Prescriber practitioner name.
     */
    @JsonProperty("prescriberName")
    private String prescriberName;

    /**
     * Prescription date (yyyy-MM-dd HH:mm:ss).
     */
    @JsonProperty("prescriptionDate")
    private String prescriptionDate;

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
    public static class Medication {
        /**
         * Drug code (hospital formulary code or national code).
         */
        @JsonProperty("drugCode")
        private String drugCode;

        /**
         * Drug name (generic or branded).
         */
        @JsonProperty("drugName")
        private String drugName;

        /**
         * Generic name.
         */
        @JsonProperty("genericName")
        private String genericName;

        /**
         * Drug form: tablet, capsule, syrup, injection, cream, etc.
         */
        @JsonProperty("drugForm")
        private String drugForm;

        /**
         * Strength/concentration (e.g., "500mg", "5mg/ml").
         */
        @JsonProperty("strength")
        private String strength;

        /**
         * Route of administration: oral, iv, im, sc, topical, rectal, etc.
         */
        @JsonProperty("route")
        private String route;

        /**
         * Dosage instruction.
         */
        @JsonProperty("dosage")
        private Dosage dosage;

        /**
         * Quantity prescribed.
         */
        @JsonProperty("quantity")
        private Integer quantity;

        /**
         * Unit: tablet, capsule, bottle, vial, tube, etc.
         */
        @JsonProperty("unit")
        private String unit;

        /**
         * Duration of treatment in days.
         */
        @JsonProperty("durationDays")
        private Integer durationDays;

        /**
         * Indication/reason for prescription.
         */
        @JsonProperty("indication")
        private String indication;

        /**
         * Status: active, on-hold, cancelled, completed, entered-in-error, stopped, draft, unknown.
         */
        @JsonProperty("status")
        private String status;

        /**
         * Priority: routine, urgent, asap, stat.
         */
        @JsonProperty("priority")
        private String priority;

        /**
         * Whether this is a PRB (Program Rujuk Balik) drug.
         */
        @JsonProperty("isPrbDrug")
        private Boolean isPrbDrug;

        /**
         * Whether this is a formulary drug (DPHO).
         */
        @JsonProperty("isFormularyDrug")
        private Boolean isFormularyDrug;

        /**
         * Dispense information (if dispensed).
         */
        @JsonProperty("dispense")
        private Dispense dispense;

        /**
         * Additional instructions or notes.
         */
        @JsonProperty("notes")
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dosage {
        /**
         * Dose amount (numeric value).
         */
        @JsonProperty("doseAmount")
        private Double doseAmount;

        /**
         * Dose unit: tablet, ml, mg, etc.
         */
        @JsonProperty("doseUnit")
        private String doseUnit;

        /**
         * Frequency per day (e.g., 3 for TID).
         */
        @JsonProperty("frequencyPerDay")
        private Integer frequencyPerDay;

        /**
         * Timing text (e.g., "3x sehari", "setiap 8 jam", "sebelum makan").
         */
        @JsonProperty("timingText")
        private String timingText;

        /**
         * Additional dosage instructions.
         */
        @JsonProperty("instructions")
        private String instructions;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dispense {
        /**
         * Pharmacy number (No Apotik) from BPJS Apotek.
         */
        @JsonProperty("pharmacyNumber")
        private String pharmacyNumber;

        /**
         * Quantity dispensed.
         */
        @JsonProperty("quantityDispensed")
        private Integer quantityDispensed;

        /**
         * Dispense date (yyyy-MM-dd HH:mm:ss).
         */
        @JsonProperty("dispenseDate")
        private String dispenseDate;

        /**
         * Pharmacist who dispensed.
         */
        @JsonProperty("pharmacistName")
        private String pharmacistName;

        /**
         * Status: preparation, in-progress, on-hold, completed, entered-in-error, stopped, declined, unknown.
         */
        @JsonProperty("status")
        private String status;
    }
}
