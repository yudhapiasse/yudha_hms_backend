package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Claim Resource.
 *
 * Represents an insurance claim submission to BPJS for reimbursement of provided healthcare services.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Claim
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
public class Claim {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Claim";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | cancelled | draft | entered-in-error

    @JsonProperty("type")
    private CodeableConcept type; // institutional | oral | pharmacy | professional | vision

    @JsonProperty("use")
    private String use; // claim | preauthorization | predetermination

    @JsonProperty("patient")
    private Reference patient;

    @JsonProperty("billablePeriod")
    private Practitioner.Period billablePeriod;

    @JsonProperty("created")
    private String created;

    @JsonProperty("enterer")
    private Reference enterer;

    @JsonProperty("insurer")
    private Reference insurer; // BPJS Kesehatan

    @JsonProperty("provider")
    private Reference provider; // Hospital

    @JsonProperty("priority")
    private CodeableConcept priority;

    @JsonProperty("prescription")
    private Reference prescription;

    @JsonProperty("originalPrescription")
    private Reference originalPrescription;

    @JsonProperty("payee")
    private Payee payee;

    @JsonProperty("referral")
    private Reference referral;

    @JsonProperty("facility")
    private Reference facility;

    @JsonProperty("careTeam")
    private List<CareTeam> careTeam;

    @JsonProperty("supportingInfo")
    private List<SupportingInfo> supportingInfo; // SEP reference

    @JsonProperty("diagnosis")
    private List<Diagnosis> diagnosis;

    @JsonProperty("procedure")
    private List<Procedure> procedure;

    @JsonProperty("insurance")
    private List<Insurance> insurance;

    @JsonProperty("accident")
    private Accident accident;

    @JsonProperty("item")
    private List<Item> item;

    @JsonProperty("total")
    private Coverage.Money total;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payee {
        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("party")
        private Reference party;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CareTeam {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("provider")
        private Reference provider;

        @JsonProperty("responsible")
        private Boolean responsible;

        @JsonProperty("role")
        private CodeableConcept role;

        @JsonProperty("qualification")
        private CodeableConcept qualification;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SupportingInfo {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("category")
        private CodeableConcept category;

        @JsonProperty("code")
        private CodeableConcept code;

        @JsonProperty("timingDate")
        private String timingDate;

        @JsonProperty("timingPeriod")
        private Practitioner.Period timingPeriod;

        @JsonProperty("valueBoolean")
        private Boolean valueBoolean;

        @JsonProperty("valueString")
        private String valueString;

        @JsonProperty("valueQuantity")
        private Observation.Quantity valueQuantity;

        @JsonProperty("valueAttachment")
        private Practitioner.Attachment valueAttachment;

        @JsonProperty("valueReference")
        private Reference valueReference;

        @JsonProperty("reason")
        private CodeableConcept reason;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Diagnosis {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("diagnosisCodeableConcept")
        private CodeableConcept diagnosisCodeableConcept;

        @JsonProperty("diagnosisReference")
        private Reference diagnosisReference;

        @JsonProperty("type")
        private List<CodeableConcept> type; // principal | admitting | discharge

        @JsonProperty("onAdmission")
        private CodeableConcept onAdmission;

        @JsonProperty("packageCode")
        private CodeableConcept packageCode;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Procedure {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("type")
        private List<CodeableConcept> type;

        @JsonProperty("date")
        private String date;

        @JsonProperty("procedureCodeableConcept")
        private CodeableConcept procedureCodeableConcept;

        @JsonProperty("procedureReference")
        private Reference procedureReference;

        @JsonProperty("udi")
        private List<Reference> udi;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Insurance {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("focal")
        private Boolean focal;

        @JsonProperty("identifier")
        private Identifier identifier;

        @JsonProperty("coverage")
        private Reference coverage;

        @JsonProperty("businessArrangement")
        private String businessArrangement;

        @JsonProperty("preAuthRef")
        private List<String> preAuthRef; // SEP number

        @JsonProperty("claimResponse")
        private Reference claimResponse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Accident {
        @JsonProperty("date")
        private String date;

        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("locationAddress")
        private Organization.Address locationAddress;

        @JsonProperty("locationReference")
        private Reference locationReference;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("careTeamSequence")
        private List<Integer> careTeamSequence;

        @JsonProperty("diagnosisSequence")
        private List<Integer> diagnosisSequence;

        @JsonProperty("procedureSequence")
        private List<Integer> procedureSequence;

        @JsonProperty("informationSequence")
        private List<Integer> informationSequence;

        @JsonProperty("revenue")
        private CodeableConcept revenue;

        @JsonProperty("category")
        private CodeableConcept category;

        @JsonProperty("productOrService")
        private CodeableConcept productOrService;

        @JsonProperty("modifier")
        private List<CodeableConcept> modifier;

        @JsonProperty("programCode")
        private List<CodeableConcept> programCode;

        @JsonProperty("servicedDate")
        private String servicedDate;

        @JsonProperty("servicedPeriod")
        private Practitioner.Period servicedPeriod;

        @JsonProperty("locationCodeableConcept")
        private CodeableConcept locationCodeableConcept;

        @JsonProperty("locationReference")
        private Reference locationReference;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("unitPrice")
        private Coverage.Money unitPrice;

        @JsonProperty("factor")
        private Double factor;

        @JsonProperty("net")
        private Coverage.Money net;

        @JsonProperty("udi")
        private List<Reference> udi;

        @JsonProperty("bodySite")
        private CodeableConcept bodySite;

        @JsonProperty("subSite")
        private List<CodeableConcept> subSite;

        @JsonProperty("encounter")
        private List<Reference> encounter;

        @JsonProperty("detail")
        private List<Detail> detail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Detail {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("revenue")
        private CodeableConcept revenue;

        @JsonProperty("category")
        private CodeableConcept category;

        @JsonProperty("productOrService")
        private CodeableConcept productOrService;

        @JsonProperty("modifier")
        private List<CodeableConcept> modifier;

        @JsonProperty("programCode")
        private List<CodeableConcept> programCode;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("unitPrice")
        private Coverage.Money unitPrice;

        @JsonProperty("factor")
        private Double factor;

        @JsonProperty("net")
        private Coverage.Money net;

        @JsonProperty("udi")
        private List<Reference> udi;

        @JsonProperty("subDetail")
        private List<SubDetail> subDetail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubDetail {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("revenue")
        private CodeableConcept revenue;

        @JsonProperty("category")
        private CodeableConcept category;

        @JsonProperty("productOrService")
        private CodeableConcept productOrService;

        @JsonProperty("modifier")
        private List<CodeableConcept> modifier;

        @JsonProperty("programCode")
        private List<CodeableConcept> programCode;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("unitPrice")
        private Coverage.Money unitPrice;

        @JsonProperty("factor")
        private Double factor;

        @JsonProperty("net")
        private Coverage.Money net;

        @JsonProperty("udi")
        private List<Reference> udi;
    }

    /**
     * Factory method to create a basic BPJS claim.
     */
    public static Claim createBPJSClaim(
        String claimId,
        Reference patient,
        String billableStart,
        String billableEnd,
        String created,
        Reference enterer,
        Reference bpjsInsurer,
        Reference provider,
        Reference coverage,
        String sepNumber,
        List<Diagnosis> diagnoses,
        List<Item> items,
        Double totalAmount
    ) {
        return Claim.builder()
            .resourceType("Claim")
            .id(claimId)
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/claim-type")
                    .code("institutional")
                    .display("Institutional")
                    .build()))
                .build())
            .use("claim")
            .patient(patient)
            .billablePeriod(Practitioner.Period.builder()
                .start(billableStart)
                .end(billableEnd)
                .build())
            .created(created)
            .enterer(enterer)
            .insurer(bpjsInsurer)
            .provider(provider)
            .priority(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/processpriority")
                    .code("normal")
                    .build()))
                .build())
            .supportingInfo(List.of(SupportingInfo.builder()
                .sequence(1)
                .category(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/claiminformationcategory")
                        .code("info")
                        .display("Information")
                        .build()))
                    .build())
                .valueString(sepNumber)
                .build()))
            .diagnosis(diagnoses)
            .insurance(List.of(Insurance.builder()
                .sequence(1)
                .focal(true)
                .coverage(coverage)
                .preAuthRef(List.of(sepNumber))
                .build()))
            .item(items)
            .total(Coverage.Money.builder()
                .value(totalAmount)
                .currency("IDR")
                .build())
            .build();
    }
}
