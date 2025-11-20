package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Invoice Resource.
 *
 * Represents a consolidated bill for services provided to a patient during
 * an encounter. Aggregates multiple ChargeItems into a single invoice with
 * total amounts, discounts, and payment terms.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Invoice
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
public class Invoice {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Invoice";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // draft | issued | balanced | cancelled | entered-in-error

    @JsonProperty("cancelledReason")
    private String cancelledReason;

    @JsonProperty("type")
    private CodeableConcept type;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("recipient")
    private Reference recipient;

    @JsonProperty("date")
    private String date;

    @JsonProperty("participant")
    private List<Participant> participant;

    @JsonProperty("issuer")
    private Reference issuer; // Organization

    @JsonProperty("account")
    private Reference account;

    @JsonProperty("lineItem")
    private List<LineItem> lineItem;

    @JsonProperty("totalPriceComponent")
    private List<PriceComponent> totalPriceComponent;

    @JsonProperty("totalNet")
    private Coverage.Money totalNet;

    @JsonProperty("totalGross")
    private Coverage.Money totalGross;

    @JsonProperty("paymentTerms")
    private String paymentTerms;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Participant {
        @JsonProperty("role")
        private CodeableConcept role;

        @JsonProperty("actor")
        private Reference actor;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LineItem {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("chargeItemReference")
        private Reference chargeItemReference;

        @JsonProperty("chargeItemCodeableConcept")
        private CodeableConcept chargeItemCodeableConcept;

        @JsonProperty("priceComponent")
        private List<PriceComponent> priceComponent;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PriceComponent {
        @JsonProperty("type")
        private String type; // base | surcharge | deduction | discount | tax | informational

        @JsonProperty("code")
        private CodeableConcept code;

        @JsonProperty("factor")
        private Double factor;

        @JsonProperty("amount")
        private Coverage.Money amount;
    }

    /**
     * Factory method to create an inpatient invoice.
     */
    public static Invoice createInpatientInvoice(
        String invoiceId,
        Reference patient,
        Reference hospital,
        String invoiceDate,
        Reference account,
        List<LineItem> lineItems,
        Double totalGrossAmount,
        Double totalNetAmount
    ) {
        return Invoice.builder()
            .resourceType("Invoice")
            .id(invoiceId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/invoice")
                .value(invoiceId)
                .build()))
            .status("issued")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v2-0301")
                    .code("IP")
                    .display("Inpatient")
                    .build()))
                .build())
            .subject(patient)
            .date(invoiceDate)
            .participant(List.of(Participant.builder()
                .role(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
                        .code("PRF")
                        .display("Performer")
                        .build()))
                    .build())
                .actor(hospital)
                .build()))
            .issuer(hospital)
            .account(account)
            .lineItem(lineItems)
            .totalGross(Coverage.Money.builder()
                .value(totalGrossAmount)
                .currency("IDR")
                .build())
            .totalNet(Coverage.Money.builder()
                .value(totalNetAmount)
                .currency("IDR")
                .build())
            .build();
    }

    /**
     * Factory method to create an outpatient invoice.
     */
    public static Invoice createOutpatientInvoice(
        String invoiceId,
        Reference patient,
        Reference hospital,
        String invoiceDate,
        Reference account,
        List<LineItem> lineItems,
        Double totalAmount
    ) {
        return Invoice.builder()
            .resourceType("Invoice")
            .id(invoiceId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/invoice")
                .value(invoiceId)
                .build()))
            .status("issued")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v2-0301")
                    .code("OP")
                    .display("Outpatient")
                    .build()))
                .build())
            .subject(patient)
            .date(invoiceDate)
            .issuer(hospital)
            .account(account)
            .lineItem(lineItems)
            .totalGross(Coverage.Money.builder()
                .value(totalAmount)
                .currency("IDR")
                .build())
            .totalNet(Coverage.Money.builder()
                .value(totalAmount)
                .currency("IDR")
                .build())
            .build();
    }

    /**
     * Helper to create a line item from a charge.
     */
    public static LineItem createLineItem(
        Integer sequence,
        Reference chargeItemReference,
        String itemDescription,
        Double amount
    ) {
        return LineItem.builder()
            .sequence(sequence)
            .chargeItemReference(chargeItemReference)
            .priceComponent(List.of(PriceComponent.builder()
                .type("base")
                .code(CodeableConcept.builder()
                    .text(itemDescription)
                    .build())
                .amount(Coverage.Money.builder()
                    .value(amount)
                    .currency("IDR")
                    .build())
                .build()))
            .build();
    }
}
