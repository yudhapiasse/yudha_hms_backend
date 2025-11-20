package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR ChargeItem Resource.
 *
 * Represents a charge for a service, procedure, medication, or other billable
 * item provided to a patient. Used for tracking individual billing items before
 * they are aggregated into invoices.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/ChargeItem
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
public class ChargeItem {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "ChargeItem";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("definitionUri")
    private List<String> definitionUri;

    @JsonProperty("definitionCanonical")
    private List<String> definitionCanonical;

    @JsonProperty("status")
    private String status; // planned | billable | not-billable | aborted | billed | entered-in-error | unknown

    @JsonProperty("partOf")
    private List<Reference> partOf;

    @JsonProperty("code")
    private CodeableConcept code;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("context")
    private Reference context; // Encounter

    @JsonProperty("occurrenceDateTime")
    private String occurrenceDateTime;

    @JsonProperty("occurrencePeriod")
    private Practitioner.Period occurrencePeriod;

    @JsonProperty("occurrenceTiming")
    private String occurrenceTiming;

    @JsonProperty("performer")
    private List<Performer> performer;

    @JsonProperty("performingOrganization")
    private Reference performingOrganization;

    @JsonProperty("requestingOrganization")
    private Reference requestingOrganization;

    @JsonProperty("costCenter")
    private Reference costCenter;

    @JsonProperty("quantity")
    private Observation.Quantity quantity;

    @JsonProperty("bodysite")
    private List<CodeableConcept> bodysite;

    @JsonProperty("factorOverride")
    private Double factorOverride;

    @JsonProperty("priceOverride")
    private Coverage.Money priceOverride;

    @JsonProperty("overrideReason")
    private String overrideReason;

    @JsonProperty("enterer")
    private Reference enterer;

    @JsonProperty("enteredDate")
    private String enteredDate;

    @JsonProperty("reason")
    private List<CodeableConcept> reason;

    @JsonProperty("service")
    private List<Reference> service;

    @JsonProperty("productReference")
    private Reference productReference;

    @JsonProperty("productCodeableConcept")
    private CodeableConcept productCodeableConcept;

    @JsonProperty("account")
    private List<Reference> account;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @JsonProperty("supportingInformation")
    private List<Reference> supportingInformation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Performer {
        @JsonProperty("function")
        private CodeableConcept function;

        @JsonProperty("actor")
        private Reference actor;
    }

    /**
     * Factory method to create a procedure charge.
     */
    public static ChargeItem createProcedureCharge(
        String chargeId,
        String tariffCode,
        String procedureName,
        Reference patient,
        Reference encounter,
        Reference practitioner,
        Reference organization,
        String occurrenceDateTime,
        Double unitPrice,
        Reference account
    ) {
        return ChargeItem.builder()
            .resourceType("ChargeItem")
            .id(chargeId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/charge")
                .value(chargeId)
                .build()))
            .status("billable")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/tariff")
                    .code(tariffCode)
                    .display(procedureName)
                    .build()))
                .build())
            .subject(patient)
            .context(encounter)
            .occurrenceDateTime(occurrenceDateTime)
            .performer(List.of(Performer.builder()
                .function(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
                        .code("PRF")
                        .display("Performer")
                        .build()))
                    .build())
                .actor(practitioner)
                .build()))
            .performingOrganization(organization)
            .quantity(Observation.Quantity.builder()
                .value(1.0)
                .build())
            .priceOverride(Coverage.Money.builder()
                .value(unitPrice)
                .currency("IDR")
                .build())
            .enteredDate(occurrenceDateTime)
            .account(List.of(account))
            .build();
    }

    /**
     * Factory method to create a room charge.
     */
    public static ChargeItem createRoomCharge(
        String chargeId,
        String roomClassCode,
        String roomClassName,
        Reference patient,
        Reference encounter,
        Reference organization,
        String startDate,
        String endDate,
        Integer numberOfDays,
        Double dailyRate,
        Reference account
    ) {
        return ChargeItem.builder()
            .resourceType("ChargeItem")
            .id(chargeId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/charge")
                .value(chargeId)
                .build()))
            .status("billable")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/tariff")
                    .code(roomClassCode)
                    .display(roomClassName)
                    .build()))
                .build())
            .subject(patient)
            .context(encounter)
            .occurrencePeriod(Practitioner.Period.builder()
                .start(startDate)
                .end(endDate)
                .build())
            .performingOrganization(organization)
            .quantity(Observation.Quantity.builder()
                .value(numberOfDays.doubleValue())
                .unit("days")
                .build())
            .priceOverride(Coverage.Money.builder()
                .value(dailyRate * numberOfDays)
                .currency("IDR")
                .build())
            .enteredDate(endDate)
            .account(List.of(account))
            .build();
    }

    /**
     * Factory method to create a medication charge.
     */
    public static ChargeItem createMedicationCharge(
        String chargeId,
        String medicationCode,
        String medicationName,
        Reference patient,
        Reference encounter,
        Reference organization,
        String occurrenceDateTime,
        Integer quantity,
        Double unitPrice,
        Reference account
    ) {
        return ChargeItem.builder()
            .resourceType("ChargeItem")
            .id(chargeId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/charge")
                .value(chargeId)
                .build()))
            .status("billable")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/medication")
                    .code(medicationCode)
                    .display(medicationName)
                    .build()))
                .build())
            .subject(patient)
            .context(encounter)
            .occurrenceDateTime(occurrenceDateTime)
            .performingOrganization(organization)
            .quantity(Observation.Quantity.builder()
                .value(quantity.doubleValue())
                .build())
            .priceOverride(Coverage.Money.builder()
                .value(unitPrice * quantity)
                .currency("IDR")
                .build())
            .enteredDate(occurrenceDateTime)
            .account(List.of(account))
            .build();
    }
}
