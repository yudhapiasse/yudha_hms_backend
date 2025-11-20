package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR CoverageEligibilityRequest Resource.
 *
 * Represents a request for BPJS coverage eligibility verification (SEP - Surat Eligibilitas Peserta).
 * This is required before providing BPJS-covered healthcare services in Indonesia.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/CoverageEligibilityRequest
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
public class CoverageEligibilityRequest {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "CoverageEligibilityRequest";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | cancelled | draft | entered-in-error

    @JsonProperty("priority")
    private CodeableConcept priority;

    @JsonProperty("purpose")
    private List<String> purpose; // auth-requirements | benefits | discovery | validation

    @JsonProperty("patient")
    private Reference patient; // Patient reference (required)

    @JsonProperty("servicedDate")
    private String servicedDate;

    @JsonProperty("servicedPeriod")
    private Practitioner.Period servicedPeriod;

    @JsonProperty("created")
    private String created;

    @JsonProperty("enterer")
    private Reference enterer; // Practitioner who entered the request

    @JsonProperty("provider")
    private Reference provider; // Hospital/organization

    @JsonProperty("insurer")
    private Reference insurer; // BPJS Kesehatan

    @JsonProperty("facility")
    private Reference facility; // Hospital location

    @JsonProperty("supportingInfo")
    private List<SupportingInfo> supportingInfo;

    @JsonProperty("insurance")
    private List<Insurance> insurance;

    @JsonProperty("item")
    private List<Item> item;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SupportingInfo {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("information")
        private Reference information;

        @JsonProperty("appliesToAll")
        private Boolean appliesToAll;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Insurance {
        @JsonProperty("focal")
        private Boolean focal;

        @JsonProperty("coverage")
        private Reference coverage; // Coverage resource reference
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
        @JsonProperty("supportingInfoSequence")
        private List<Integer> supportingInfoSequence;

        @JsonProperty("category")
        private CodeableConcept category;

        @JsonProperty("productOrService")
        private CodeableConcept productOrService;

        @JsonProperty("modifier")
        private List<CodeableConcept> modifier;

        @JsonProperty("provider")
        private Reference provider;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("unitPrice")
        private Coverage.Money unitPrice;

        @JsonProperty("facility")
        private Reference facility;

        @JsonProperty("diagnosis")
        private List<Diagnosis> diagnosis;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Diagnosis {
        @JsonProperty("diagnosisCodeableConcept")
        private CodeableConcept diagnosisCodeableConcept;

        @JsonProperty("diagnosisReference")
        private Reference diagnosisReference;
    }

    /**
     * Factory method to create a basic SEP request for BPJS.
     */
    public static CoverageEligibilityRequest createSEPRequest(
        String requestId,
        Reference patient,
        String servicedDate,
        String created,
        Reference enterer,
        Reference provider,
        Reference bpjsInsurer,
        Reference facility,
        Reference coverage
    ) {
        return CoverageEligibilityRequest.builder()
            .resourceType("CoverageEligibilityRequest")
            .id(requestId)
            .status("active")
            .purpose(List.of("benefits"))
            .patient(patient)
            .servicedDate(servicedDate)
            .created(created)
            .enterer(enterer)
            .provider(provider)
            .insurer(bpjsInsurer)
            .facility(facility)
            .insurance(List.of(Insurance.builder()
                .focal(true)
                .coverage(coverage)
                .build()))
            .build();
    }

    /**
     * Factory method to create SEP request with diagnosis.
     */
    public static CoverageEligibilityRequest createSEPRequestWithDiagnosis(
        String requestId,
        Reference patient,
        String servicedDate,
        Reference enterer,
        Reference provider,
        Reference bpjsInsurer,
        Reference facility,
        Reference coverage,
        String icd10Code,
        String diagnosisDisplay
    ) {
        return CoverageEligibilityRequest.builder()
            .resourceType("CoverageEligibilityRequest")
            .id(requestId)
            .status("active")
            .purpose(List.of("benefits"))
            .patient(patient)
            .servicedDate(servicedDate)
            .created(servicedDate + "T08:00:00+07:00")
            .enterer(enterer)
            .provider(provider)
            .insurer(bpjsInsurer)
            .facility(facility)
            .insurance(List.of(Insurance.builder()
                .focal(true)
                .coverage(coverage)
                .build()))
            .item(List.of(Item.builder()
                .category(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/ex-benefitcategory")
                        .code("30")
                        .display("Health Benefit Plan Coverage")
                        .build()))
                    .build())
                .diagnosis(List.of(Diagnosis.builder()
                    .diagnosisCodeableConcept(CodeableConcept.builder()
                        .coding(List.of(Coding.builder()
                            .system("http://hl7.org/fhir/sid/icd-10")
                            .code(icd10Code)
                            .display(diagnosisDisplay)
                            .build()))
                        .build())
                    .build()))
                .build()))
            .build();
    }
}
