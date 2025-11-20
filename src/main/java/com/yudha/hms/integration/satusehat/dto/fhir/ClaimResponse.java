package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR ClaimResponse Resource.
 *
 * Represents the adjudication result from BPJS for a submitted claim.
 * Shows which items are approved, amounts covered, and payment details.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/ClaimResponse
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
public class ClaimResponse {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "ClaimResponse";

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

    @JsonProperty("created")
    private String created;

    @JsonProperty("insurer")
    private Reference insurer; // BPJS Kesehatan

    @JsonProperty("requestor")
    private Reference requestor; // Hospital

    @JsonProperty("request")
    private Reference request; // Original Claim reference

    @JsonProperty("outcome")
    private String outcome; // queued | complete | error | partial

    @JsonProperty("disposition")
    private String disposition; // Human-readable description

    @JsonProperty("preAuthRef")
    private String preAuthRef; // SEP number

    @JsonProperty("preAuthPeriod")
    private Practitioner.Period preAuthPeriod;

    @JsonProperty("payeeType")
    private CodeableConcept payeeType;

    @JsonProperty("item")
    private List<Item> item;

    @JsonProperty("addItem")
    private List<AddItem> addItem;

    @JsonProperty("adjudication")
    private List<Adjudication> adjudication;

    @JsonProperty("total")
    private List<Total> total;

    @JsonProperty("payment")
    private Payment payment;

    @JsonProperty("fundsReserve")
    private CodeableConcept fundsReserve;

    @JsonProperty("formCode")
    private CodeableConcept formCode;

    @JsonProperty("form")
    private Practitioner.Attachment form;

    @JsonProperty("processNote")
    private List<ProcessNote> processNote;

    @JsonProperty("communicationRequest")
    private List<Reference> communicationRequest;

    @JsonProperty("insurance")
    private List<Insurance> insurance;

    @JsonProperty("error")
    private List<Error> error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
        @JsonProperty("itemSequence")
        private Integer itemSequence;

        @JsonProperty("noteNumber")
        private List<Integer> noteNumber;

        @JsonProperty("adjudication")
        private List<Adjudication> adjudication;

        @JsonProperty("detail")
        private List<Detail> detail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Detail {
        @JsonProperty("detailSequence")
        private Integer detailSequence;

        @JsonProperty("noteNumber")
        private List<Integer> noteNumber;

        @JsonProperty("adjudication")
        private List<Adjudication> adjudication;

        @JsonProperty("subDetail")
        private List<SubDetail> subDetail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SubDetail {
        @JsonProperty("subDetailSequence")
        private Integer subDetailSequence;

        @JsonProperty("noteNumber")
        private List<Integer> noteNumber;

        @JsonProperty("adjudication")
        private List<Adjudication> adjudication;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddItem {
        @JsonProperty("itemSequence")
        private List<Integer> itemSequence;

        @JsonProperty("detailSequence")
        private List<Integer> detailSequence;

        @JsonProperty("subdetailSequence")
        private List<Integer> subdetailSequence;

        @JsonProperty("provider")
        private List<Reference> provider;

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

        @JsonProperty("bodySite")
        private CodeableConcept bodySite;

        @JsonProperty("subSite")
        private List<CodeableConcept> subSite;

        @JsonProperty("noteNumber")
        private List<Integer> noteNumber;

        @JsonProperty("adjudication")
        private List<Adjudication> adjudication;

        @JsonProperty("detail")
        private List<AddItemDetail> detail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddItemDetail {
        @JsonProperty("productOrService")
        private CodeableConcept productOrService;

        @JsonProperty("modifier")
        private List<CodeableConcept> modifier;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("unitPrice")
        private Coverage.Money unitPrice;

        @JsonProperty("factor")
        private Double factor;

        @JsonProperty("net")
        private Coverage.Money net;

        @JsonProperty("noteNumber")
        private List<Integer> noteNumber;

        @JsonProperty("adjudication")
        private List<Adjudication> adjudication;

        @JsonProperty("subDetail")
        private List<AddItemSubDetail> subDetail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddItemSubDetail {
        @JsonProperty("productOrService")
        private CodeableConcept productOrService;

        @JsonProperty("modifier")
        private List<CodeableConcept> modifier;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("unitPrice")
        private Coverage.Money unitPrice;

        @JsonProperty("factor")
        private Double factor;

        @JsonProperty("net")
        private Coverage.Money net;

        @JsonProperty("noteNumber")
        private List<Integer> noteNumber;

        @JsonProperty("adjudication")
        private List<Adjudication> adjudication;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Adjudication {
        @JsonProperty("category")
        private CodeableConcept category; // eligible | benefit | deductible | copay

        @JsonProperty("reason")
        private CodeableConcept reason;

        @JsonProperty("amount")
        private Coverage.Money amount;

        @JsonProperty("value")
        private Double value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Total {
        @JsonProperty("category")
        private CodeableConcept category;

        @JsonProperty("amount")
        private Coverage.Money amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Payment {
        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("adjustment")
        private Coverage.Money adjustment;

        @JsonProperty("adjustmentReason")
        private CodeableConcept adjustmentReason;

        @JsonProperty("date")
        private String date;

        @JsonProperty("amount")
        private Coverage.Money amount;

        @JsonProperty("identifier")
        private Identifier identifier;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProcessNote {
        @JsonProperty("number")
        private Integer number;

        @JsonProperty("type")
        private String type; // display | print | printoper

        @JsonProperty("text")
        private String text;

        @JsonProperty("language")
        private CodeableConcept language;
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

        @JsonProperty("coverage")
        private Reference coverage;

        @JsonProperty("businessArrangement")
        private String businessArrangement;

        @JsonProperty("claimResponse")
        private Reference claimResponse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Error {
        @JsonProperty("itemSequence")
        private Integer itemSequence;

        @JsonProperty("detailSequence")
        private Integer detailSequence;

        @JsonProperty("subDetailSequence")
        private Integer subDetailSequence;

        @JsonProperty("code")
        private CodeableConcept code;
    }

    /**
     * Factory method to create an approved claim response.
     */
    public static ClaimResponse createApprovedResponse(
        String responseId,
        Reference claim,
        Reference patient,
        String created,
        Reference bpjsInsurer,
        Reference hospital,
        Double approvedAmount
    ) {
        return ClaimResponse.builder()
            .resourceType("ClaimResponse")
            .id(responseId)
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/claim-type")
                    .code("institutional")
                    .build()))
                .build())
            .use("claim")
            .patient(patient)
            .created(created)
            .insurer(bpjsInsurer)
            .requestor(hospital)
            .request(claim)
            .outcome("complete")
            .disposition("Claim approved for payment")
            .total(List.of(Total.builder()
                .category(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/adjudication")
                        .code("benefit")
                        .display("Benefit Amount")
                        .build()))
                    .build())
                .amount(Coverage.Money.builder()
                    .value(approvedAmount)
                    .currency("IDR")
                    .build())
                .build()))
            .payment(Payment.builder()
                .type(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/ex-paymenttype")
                        .code("complete")
                        .display("Complete")
                        .build()))
                    .build())
                .amount(Coverage.Money.builder()
                    .value(approvedAmount)
                    .currency("IDR")
                    .build())
                .build())
            .build();
    }
}
