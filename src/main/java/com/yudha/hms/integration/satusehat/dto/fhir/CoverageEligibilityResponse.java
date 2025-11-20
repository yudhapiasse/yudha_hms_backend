package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR CoverageEligibilityResponse Resource.
 *
 * Represents the response for BPJS coverage eligibility verification - the actual SEP (Surat Eligibilitas Peserta).
 * This document confirms that the patient is eligible for BPJS-covered services.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/CoverageEligibilityResponse
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CoverageEligibilityResponse {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "CoverageEligibilityResponse";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier; // SEP number

    @JsonProperty("status")
    private String status; // active | cancelled | draft | entered-in-error

    @JsonProperty("purpose")
    private List<String> purpose; // auth-requirements | benefits | discovery | validation

    @JsonProperty("patient")
    private Reference patient;

    @JsonProperty("servicedDate")
    private String servicedDate;

    @JsonProperty("servicedPeriod")
    private Practitioner.Period servicedPeriod;

    @JsonProperty("created")
    private String created;

    @JsonProperty("request")
    private Reference request; // CoverageEligibilityRequest reference

    @JsonProperty("outcome")
    private String outcome; // queued | complete | error | partial

    @JsonProperty("disposition")
    private String disposition; // Human-readable result

    @JsonProperty("insurer")
    private Reference insurer; // BPJS Kesehatan

    @JsonProperty("insurance")
    private List<Insurance> insurance;

    @JsonProperty("preAuthRef")
    private String preAuthRef; // Pre-authorization reference number

    @JsonProperty("form")
    private CodeableConcept form;

    @JsonProperty("error")
    private List<Error> error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Insurance {
        @JsonProperty("coverage")
        private Reference coverage;

        @JsonProperty("inforce")
        private Boolean inforce; // Coverage is in force

        @JsonProperty("benefitPeriod")
        private Practitioner.Period benefitPeriod; // SEP validity period

        @JsonProperty("item")
        private List<Item> item;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
        @JsonProperty("category")
        private CodeableConcept category;

        @JsonProperty("productOrService")
        private CodeableConcept productOrService;

        @JsonProperty("modifier")
        private List<CodeableConcept> modifier;

        @JsonProperty("provider")
        private Reference provider;

        @JsonProperty("excluded")
        private Boolean excluded;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("network")
        private CodeableConcept network;

        @JsonProperty("unit")
        private CodeableConcept unit;

        @JsonProperty("term")
        private CodeableConcept term;

        @JsonProperty("benefit")
        private List<Benefit> benefit;

        @JsonProperty("authorizationRequired")
        private Boolean authorizationRequired;

        @JsonProperty("authorizationSupporting")
        private List<CodeableConcept> authorizationSupporting;

        @JsonProperty("authorizationUrl")
        private String authorizationUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Benefit {
        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("allowedUnsignedInt")
        private Integer allowedUnsignedInt;

        @JsonProperty("allowedString")
        private String allowedString;

        @JsonProperty("allowedMoney")
        private Coverage.Money allowedMoney;

        @JsonProperty("usedUnsignedInt")
        private Integer usedUnsignedInt;

        @JsonProperty("usedString")
        private String usedString;

        @JsonProperty("usedMoney")
        private Coverage.Money usedMoney;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        @JsonProperty("code")
        private CodeableConcept code;
    }

    /**
     * Factory method to create an approved SEP response.
     */
    public static CoverageEligibilityResponse createApprovedSEP(
        String responseId,
        String sepNumber,
        Reference request,
        Reference patient,
        String servicedDate,
        String created,
        Reference bpjsInsurer,
        Reference coverage,
        String benefitPeriodStart,
        String benefitPeriodEnd
    ) {
        return CoverageEligibilityResponse.builder()
            .resourceType("CoverageEligibilityResponse")
            .id(responseId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/sep")
                .value(sepNumber)
                .build()))
            .status("active")
            .purpose(List.of("benefits"))
            .patient(patient)
            .servicedDate(servicedDate)
            .created(created)
            .request(request)
            .outcome("complete")
            .disposition("Coverage approved - SEP issued")
            .insurer(bpjsInsurer)
            .insurance(List.of(Insurance.builder()
                .coverage(coverage)
                .inforce(true)
                .benefitPeriod(Practitioner.Period.builder()
                    .start(benefitPeriodStart)
                    .end(benefitPeriodEnd)
                    .build())
                .item(List.of(Item.builder()
                    .category(CodeableConcept.builder()
                        .coding(List.of(Coding.builder()
                            .system("http://terminology.hl7.org/CodeSystem/ex-benefitcategory")
                            .code("30")
                            .display("Health Benefit Plan Coverage")
                            .build()))
                        .build())
                    .benefit(List.of(Benefit.builder()
                        .type(CodeableConcept.builder()
                            .coding(List.of(Coding.builder()
                                .system("http://terminology.hl7.org/CodeSystem/benefit-type")
                                .code("benefit")
                                .display("Benefit")
                                .build()))
                            .build())
                        .allowedString("Covered")
                        .build()))
                    .build()))
                .build()))
            .build();
    }

    /**
     * Factory method to create a rejected SEP response.
     */
    public static CoverageEligibilityResponse createRejectedSEP(
        String responseId,
        Reference request,
        Reference patient,
        String servicedDate,
        Reference bpjsInsurer,
        String errorCode,
        String errorDisplay
    ) {
        return CoverageEligibilityResponse.builder()
            .resourceType("CoverageEligibilityResponse")
            .id(responseId)
            .status("active")
            .purpose(List.of("benefits"))
            .patient(patient)
            .servicedDate(servicedDate)
            .created(servicedDate + "T08:15:00+07:00")
            .request(request)
            .outcome("error")
            .disposition("Coverage denied")
            .insurer(bpjsInsurer)
            .error(List.of(Error.builder()
                .code(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/adjudication-error")
                        .code(errorCode)
                        .display(errorDisplay)
                        .build()))
                    .build())
                .build()))
            .build();
    }
}
