package com.yudha.hms.integration.bpjs.dto.erekammedis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * BPJS eRekam Medis Patient Demographic Submission.
 *
 * Patient demographic data submission to BPJS electronic medical record system.
 * Complies with FHIR R4 Patient resource structure and SATUSEHAT interoperability.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDemographicSubmission {

    /**
     * Medical record number (hospital internal).
     */
    @JsonProperty("medicalRecordNumber")
    private String medicalRecordNumber;

    /**
     * BPJS card number (No Kartu BPJS).
     */
    @JsonProperty("bpjsCardNumber")
    private String bpjsCardNumber;

    /**
     * NIK (Nomor Induk Kependudukan) - National ID.
     */
    @JsonProperty("nik")
    private String nik;

    /**
     * IHS Number (Indonesia Health Service Number) from SATUSEHAT.
     */
    @JsonProperty("ihsNumber")
    private String ihsNumber;

    /**
     * Patient full name.
     */
    @JsonProperty("fullName")
    private String fullName;

    /**
     * Birth date (yyyy-MM-dd).
     */
    @JsonProperty("birthDate")
    private LocalDate birthDate;

    /**
     * Gender: male, female, other, unknown (FHIR R4 AdministrativeGender).
     */
    @JsonProperty("gender")
    private String gender;

    /**
     * Marital status: S (Single), M (Married), D (Divorced), W (Widowed).
     */
    @JsonProperty("maritalStatus")
    private String maritalStatus;

    /**
     * Blood type: A, B, AB, O.
     */
    @JsonProperty("bloodType")
    private String bloodType;

    /**
     * Rhesus factor: + or -.
     */
    @JsonProperty("rhesus")
    private String rhesus;

    /**
     * Religion.
     */
    @JsonProperty("religion")
    private String religion;

    /**
     * Education level.
     */
    @JsonProperty("education")
    private String education;

    /**
     * Occupation.
     */
    @JsonProperty("occupation")
    private String occupation;

    /**
     * Mother's name (for patient identification).
     */
    @JsonProperty("motherName")
    private String motherName;

    /**
     * Phone number.
     */
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    /**
     * Email address.
     */
    @JsonProperty("email")
    private String email;

    /**
     * Complete address.
     */
    @JsonProperty("address")
    private Address address;

    /**
     * Emergency contact.
     */
    @JsonProperty("emergencyContact")
    private EmergencyContact emergencyContact;

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
    public static class Address {
        /**
         * Street address line 1.
         */
        @JsonProperty("line1")
        private String line1;

        /**
         * Street address line 2.
         */
        @JsonProperty("line2")
        private String line2;

        /**
         * Village/Kelurahan.
         */
        @JsonProperty("village")
        private String village;

        /**
         * Subdistrict/Kecamatan.
         */
        @JsonProperty("subdistrict")
        private String subdistrict;

        /**
         * City/Kabupaten.
         */
        @JsonProperty("city")
        private String city;

        /**
         * Province.
         */
        @JsonProperty("province")
        private String province;

        /**
         * Postal code.
         */
        @JsonProperty("postalCode")
        private String postalCode;

        /**
         * RT (Rukun Tetangga).
         */
        @JsonProperty("rt")
        private String rt;

        /**
         * RW (Rukun Warga).
         */
        @JsonProperty("rw")
        private String rw;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmergencyContact {
        /**
         * Contact name.
         */
        @JsonProperty("name")
        private String name;

        /**
         * Relationship to patient.
         */
        @JsonProperty("relationship")
        private String relationship;

        /**
         * Contact phone number.
         */
        @JsonProperty("phoneNumber")
        private String phoneNumber;

        /**
         * Contact address.
         */
        @JsonProperty("address")
        private String address;
    }
}
