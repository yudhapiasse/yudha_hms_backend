package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR PaymentNotice Resource.
 *
 * Represents a notification of payment made or to be made by an insurer to a
 * healthcare provider. Used for notifying hospitals about BPJS payments and
 * tracking payment status updates.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/PaymentNotice
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
public class PaymentNotice {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "PaymentNotice";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | cancelled | draft | entered-in-error

    @JsonProperty("request")
    private Reference request; // Claim

    @JsonProperty("response")
    private Reference response; // ClaimResponse

    @JsonProperty("created")
    private String created;

    @JsonProperty("provider")
    private Reference provider; // Hospital

    @JsonProperty("payment")
    private Reference payment; // PaymentReconciliation

    @JsonProperty("paymentDate")
    private String paymentDate;

    @JsonProperty("payee")
    private Reference payee; // Hospital

    @JsonProperty("recipient")
    private Reference recipient; // Hospital

    @JsonProperty("amount")
    private Coverage.Money amount;

    @JsonProperty("paymentStatus")
    private CodeableConcept paymentStatus;

    /**
     * Factory method to create a BPJS payment notice.
     */
    public static PaymentNotice createBPJSPaymentNotice(
        String noticeId,
        Reference claim,
        Reference claimResponse,
        String created,
        Reference hospital,
        Reference paymentReconciliation,
        String paymentDate,
        Double paymentAmount,
        String paymentStatusCode,
        String paymentStatusDisplay
    ) {
        return PaymentNotice.builder()
            .resourceType("PaymentNotice")
            .id(noticeId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/payment-notice")
                .value(noticeId)
                .build()))
            .status("active")
            .request(claim)
            .response(claimResponse)
            .created(created)
            .provider(hospital)
            .payment(paymentReconciliation)
            .paymentDate(paymentDate)
            .payee(hospital)
            .recipient(hospital)
            .amount(Coverage.Money.builder()
                .value(paymentAmount)
                .currency("IDR")
                .build())
            .paymentStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/paymentstatus")
                    .code(paymentStatusCode)
                    .display(paymentStatusDisplay)
                    .build()))
                .build())
            .build();
    }

    /**
     * Factory method to create a payment pending notice.
     */
    public static PaymentNotice createPaymentPendingNotice(
        String noticeId,
        Reference claim,
        Reference claimResponse,
        String created,
        Reference hospital,
        Double expectedAmount
    ) {
        return PaymentNotice.builder()
            .resourceType("PaymentNotice")
            .id(noticeId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/payment-notice")
                .value(noticeId)
                .build()))
            .status("active")
            .request(claim)
            .response(claimResponse)
            .created(created)
            .provider(hospital)
            .payee(hospital)
            .recipient(hospital)
            .amount(Coverage.Money.builder()
                .value(expectedAmount)
                .currency("IDR")
                .build())
            .paymentStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/paymentstatus")
                    .code("cleared")
                    .display("Cleared")
                    .build()))
                .build())
            .build();
    }
}
