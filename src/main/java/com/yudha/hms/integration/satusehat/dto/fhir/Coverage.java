package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Coverage Resource.
 *
 * Represents insurance coverage information, particularly BPJS (Indonesia's national health insurance).
 * Includes BPJS card number, class (1, 2, 3), and coverage period.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Coverage
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
public class Coverage {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Coverage";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | cancelled | draft | entered-in-error

    @JsonProperty("type")
    private CodeableConcept type; // Public Healthcare, Private, etc.

    @JsonProperty("policyHolder")
    private Reference policyHolder;

    @JsonProperty("subscriber")
    private Reference subscriber; // Patient who owns the card

    @JsonProperty("subscriberId")
    private String subscriberId; // BPJS card number

    @JsonProperty("beneficiary")
    private Reference beneficiary; // Patient receiving coverage

    @JsonProperty("dependent")
    private String dependent; // Dependent number

    @JsonProperty("relationship")
    private CodeableConcept relationship; // self | spouse | child | parent

    @JsonProperty("period")
    private Practitioner.Period period; // Coverage validity period

    @JsonProperty("payor")
    private List<Reference> payor; // BPJS Kesehatan organization

    @JsonProperty("class")
    private List<CoverageClass> coverageClass; // BPJS Class

    @JsonProperty("order")
    private Integer order;

    @JsonProperty("network")
    private String network;

    @JsonProperty("costToBeneficiary")
    private List<CostToBeneficiary> costToBeneficiary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CoverageClass {
        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("value")
        private String value; // "1", "2", or "3" for BPJS class

        @JsonProperty("name")
        private String name; // "Kelas 1", "Kelas 2", "Kelas 3"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CostToBeneficiary {
        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("valueQuantity")
        private Observation.Quantity valueQuantity;

        @JsonProperty("valueMoney")
        private Money valueMoney;

        @JsonProperty("exception")
        private List<Exception> exception;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Money {
        @JsonProperty("value")
        private Double value;

        @JsonProperty("currency")
        private String currency; // IDR
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Exception {
        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("period")
        private Practitioner.Period period;
    }

    /**
     * Factory method to create BPJS coverage.
     */
    public static Coverage createBPJSCoverage(
        String coverageId,
        String bpjsCardNumber,
        Reference patient,
        String bpjsClass,
        String className,
        String periodStart,
        String periodEnd,
        Reference bpjsOrganization
    ) {
        return Coverage.builder()
            .resourceType("Coverage")
            .id(coverageId)
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                    .code("PUBLICPOL")
                    .display("Public Healthcare")
                    .build()))
                .build())
            .subscriber(patient)
            .subscriberId(bpjsCardNumber)
            .beneficiary(patient)
            .relationship(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                    .code("self")
                    .display("Self")
                    .build()))
                .build())
            .period(Practitioner.Period.builder()
                .start(periodStart)
                .end(periodEnd)
                .build())
            .payor(List.of(bpjsOrganization))
            .coverageClass(List.of(CoverageClass.builder()
                .type(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/coverage-class")
                        .code("class")
                        .display("Class")
                        .build()))
                    .build())
                .value(bpjsClass)
                .name(className)
                .build()))
            .build();
    }

    /**
     * Factory method to create BPJS coverage for dependent (family member).
     */
    public static Coverage createBPJSDependentCoverage(
        String coverageId,
        String bpjsCardNumber,
        Reference subscriber,
        Reference beneficiary,
        String dependentNumber,
        String relationshipCode,
        String relationshipDisplay,
        String bpjsClass,
        String periodStart,
        String periodEnd,
        Reference bpjsOrganization
    ) {
        return Coverage.builder()
            .resourceType("Coverage")
            .id(coverageId)
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                    .code("PUBLICPOL")
                    .display("Public Healthcare")
                    .build()))
                .build())
            .subscriber(subscriber)
            .subscriberId(bpjsCardNumber)
            .beneficiary(beneficiary)
            .dependent(dependentNumber)
            .relationship(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                    .code(relationshipCode)
                    .display(relationshipDisplay)
                    .build()))
                .build())
            .period(Practitioner.Period.builder()
                .start(periodStart)
                .end(periodEnd)
                .build())
            .payor(List.of(bpjsOrganization))
            .coverageClass(List.of(CoverageClass.builder()
                .type(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/coverage-class")
                        .code("class")
                        .display("Class")
                        .build()))
                    .build())
                .value(bpjsClass)
                .name("Kelas " + bpjsClass)
                .build()))
            .build();
    }

    /**
     * Factory method to create private insurance coverage.
     */
    public static Coverage createPrivateInsuranceCoverage(
        String coverageId,
        String policyNumber,
        Reference patient,
        Reference insuranceOrganization,
        String periodStart,
        String periodEnd
    ) {
        return Coverage.builder()
            .resourceType("Coverage")
            .id(coverageId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/coverage")
                .value(coverageId)
                .build()))
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                    .code("PRIVATEPOL")
                    .display("Private Healthcare")
                    .build()))
                .build())
            .subscriber(patient)
            .subscriberId(policyNumber)
            .beneficiary(patient)
            .relationship(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                    .code("self")
                    .display("Self")
                    .build()))
                .build())
            .period(Practitioner.Period.builder()
                .start(periodStart)
                .end(periodEnd)
                .build())
            .payor(List.of(insuranceOrganization))
            .build();
    }

    /**
     * Factory method to create company-sponsored coverage.
     */
    public static Coverage createCompanyCoverage(
        String coverageId,
        String employeeId,
        Reference patient,
        Reference policyHolder,
        Reference companyOrganization,
        String periodStart,
        String periodEnd
    ) {
        return Coverage.builder()
            .resourceType("Coverage")
            .id(coverageId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/coverage")
                .value(coverageId)
                .build()))
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                    .code("EHCPOL")
                    .display("Extended Healthcare")
                    .build()))
                .build())
            .policyHolder(policyHolder)
            .subscriber(patient)
            .subscriberId(employeeId)
            .beneficiary(patient)
            .relationship(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/subscriber-relationship")
                    .code("self")
                    .display("Self")
                    .build()))
                .build())
            .period(Practitioner.Period.builder()
                .start(periodStart)
                .end(periodEnd)
                .build())
            .payor(List.of(companyOrganization))
            .build();
    }
}
