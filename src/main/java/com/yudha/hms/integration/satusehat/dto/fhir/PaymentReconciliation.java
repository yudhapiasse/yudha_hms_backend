package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR PaymentReconciliation Resource.
 *
 * Represents reconciliation of payments from insurers (like BPJS) against
 * submitted claims. Used for tracking payment receipts, matching payments
 * to claims, and managing financial reconciliation.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/PaymentReconciliation
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentReconciliation {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "PaymentReconciliation";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | cancelled | draft | entered-in-error

    @JsonProperty("period")
    private Practitioner.Period period;

    @JsonProperty("created")
    private String created;

    @JsonProperty("paymentIssuer")
    private Reference paymentIssuer; // BPJS Kesehatan

    @JsonProperty("request")
    private Reference request; // Claim

    @JsonProperty("requestor")
    private Reference requestor; // Hospital

    @JsonProperty("outcome")
    private String outcome; // queued | complete | error | partial

    @JsonProperty("disposition")
    private String disposition;

    @JsonProperty("paymentDate")
    private String paymentDate;

    @JsonProperty("paymentAmount")
    private Coverage.Money paymentAmount;

    @JsonProperty("paymentIdentifier")
    private Identifier paymentIdentifier;

    @JsonProperty("detail")
    private List<Detail> detail;

    @JsonProperty("formCode")
    private CodeableConcept formCode;

    @JsonProperty("processNote")
    private List<ProcessNote> processNote;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Detail {
        @JsonProperty("identifier")
        private Identifier identifier;

        @JsonProperty("predecessor")
        private Identifier predecessor;

        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("request")
        private Reference request;

        @JsonProperty("submitter")
        private Reference submitter;

        @JsonProperty("response")
        private Reference response;

        @JsonProperty("date")
        private String date;

        @JsonProperty("responsible")
        private Reference responsible;

        @JsonProperty("payee")
        private Reference payee;

        @JsonProperty("amount")
        private Coverage.Money amount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProcessNote {
        @JsonProperty("type")
        private String type; // display | print | printoper

        @JsonProperty("text")
        private String text;
    }

    /**
     * Factory method to create a BPJS payment reconciliation.
     */
    public static PaymentReconciliation createBPJSPaymentReconciliation(
        String reconciliationId,
        String periodStart,
        String periodEnd,
        String created,
        Reference bpjsOrganization,
        Reference claim,
        Reference hospital,
        String paymentDate,
        Double paymentAmount,
        String paymentIdentifierValue,
        Reference claimResponse,
        String detailId
    ) {
        return PaymentReconciliation.builder()
            .resourceType("PaymentReconciliation")
            .id(reconciliationId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/payment-reconciliation")
                .value(reconciliationId)
                .build()))
            .status("active")
            .period(Practitioner.Period.builder()
                .start(periodStart)
                .end(periodEnd)
                .build())
            .created(created)
            .paymentIssuer(bpjsOrganization)
            .request(claim)
            .requestor(hospital)
            .outcome("complete")
            .disposition("Payment processed successfully")
            .paymentDate(paymentDate)
            .paymentAmount(Coverage.Money.builder()
                .value(paymentAmount)
                .currency("IDR")
                .build())
            .paymentIdentifier(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/payment")
                .value(paymentIdentifierValue)
                .build())
            .detail(List.of(Detail.builder()
                .identifier(Identifier.builder()
                    .system("http://sys-ids.kemkes.go.id/payment-detail")
                    .value(detailId)
                    .build())
                .type(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/payment-type")
                        .code("payment")
                        .display("Payment")
                        .build()))
                    .build())
                .request(claim)
                .response(claimResponse)
                .submitter(hospital)
                .payee(hospital)
                .date(paymentDate)
                .amount(Coverage.Money.builder()
                    .value(paymentAmount)
                    .currency("IDR")
                    .build())
                .build()))
            .build();
    }
}
