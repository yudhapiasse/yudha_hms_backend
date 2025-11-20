package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR MedicationDispense Resource.
 *
 * Represents the event of a pharmacy dispensing medication to a patient.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/MedicationDispense
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
public class MedicationDispense {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "MedicationDispense";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // preparation | in-progress | cancelled | on-hold | completed | entered-in-error | stopped | declined | unknown

    @JsonProperty("category")
    private CodeableConcept category; // inpatient | outpatient | community | discharge

    @JsonProperty("medicationReference")
    private Reference medicationReference;

    @JsonProperty("medicationCodeableConcept")
    private CodeableConcept medicationCodeableConcept;

    @JsonProperty("subject")
    private Reference subject; // Patient reference (required)

    @JsonProperty("context")
    private Reference context; // Encounter reference

    @JsonProperty("supportingInformation")
    private List<Reference> supportingInformation;

    @JsonProperty("performer")
    private List<Performer> performer;

    @JsonProperty("location")
    private Reference location; // Pharmacy location

    @JsonProperty("authorizingPrescription")
    private List<Reference> authorizingPrescription; // MedicationRequest references

    @JsonProperty("type")
    private CodeableConcept type; // First Fill | Refill | Partial Fill

    @JsonProperty("quantity")
    private Observation.Quantity quantity; // Amount dispensed

    @JsonProperty("daysSupply")
    private Observation.Quantity daysSupply;

    @JsonProperty("whenPrepared")
    private String whenPrepared; // When product was packaged

    @JsonProperty("whenHandedOver")
    private String whenHandedOver; // When product was given out

    @JsonProperty("destination")
    private Reference destination; // Where medication sent

    @JsonProperty("receiver")
    private List<Reference> receiver; // Who collected the medication

    @JsonProperty("note")
    private List<Condition.Annotation> note;

    @JsonProperty("dosageInstruction")
    private List<MedicationRequest.DosageInstruction> dosageInstruction;

    @JsonProperty("substitution")
    private Substitution substitution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Performer {
        @JsonProperty("function")
        private CodeableConcept function;

        @JsonProperty("actor")
        private Reference actor; // Practitioner or Organization
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Substitution {
        @JsonProperty("wasSubstituted")
        private Boolean wasSubstituted;

        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("reason")
        private List<CodeableConcept> reason;

        @JsonProperty("responsibleParty")
        private List<Reference> responsibleParty;
    }

    /**
     * Factory method to create a completed dispense event.
     */
    public static MedicationDispense createCompletedDispense(
        String dispenseId,
        Reference medicationRequest,
        Reference medication,
        Reference patient,
        Reference encounter,
        Reference pharmacist,
        Reference pharmacyLocation,
        Double quantityValue,
        String quantityUnit,
        String whenHandedOver
    ) {
        return MedicationDispense.builder()
            .resourceType("MedicationDispense")
            .id(dispenseId)
            .status("completed")
            .medicationReference(medication)
            .subject(patient)
            .context(encounter)
            .performer(List.of(Performer.builder()
                .actor(pharmacist)
                .function(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/medicationdispense-performer-function")
                        .code("packager")
                        .display("Packager")
                        .build()))
                    .build())
                .build()))
            .location(pharmacyLocation)
            .authorizingPrescription(List.of(medicationRequest))
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                    .code("FF")
                    .display("First Fill")
                    .build()))
                .build())
            .quantity(Observation.Quantity.builder()
                .value(quantityValue)
                .unit(quantityUnit)
                .build())
            .whenHandedOver(whenHandedOver)
            .build();
    }

    /**
     * Factory method to create an outpatient dispense.
     */
    public static MedicationDispense createOutpatientDispense(
        String dispenseId,
        String kfaCode,
        String medicationName,
        Reference patient,
        Reference medicationRequest,
        Reference pharmacist,
        Double quantityValue,
        String quantityUnit,
        Integer daysSupply,
        String whenHandedOver
    ) {
        return MedicationDispense.builder()
            .resourceType("MedicationDispense")
            .id(dispenseId)
            .status("completed")
            .category(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/medicationdispense-category")
                    .code("outpatient")
                    .display("Outpatient")
                    .build()))
                .build())
            .medicationCodeableConcept(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(kfaCode)
                    .display(medicationName)
                    .build()))
                .build())
            .subject(patient)
            .authorizingPrescription(List.of(medicationRequest))
            .performer(List.of(Performer.builder()
                .actor(pharmacist)
                .build()))
            .quantity(Observation.Quantity.builder()
                .value(quantityValue)
                .unit(quantityUnit)
                .build())
            .daysSupply(Observation.Quantity.builder()
                .value((double) daysSupply)
                .unit("days")
                .system("http://unitsofmeasure.org")
                .code("d")
                .build())
            .whenHandedOver(whenHandedOver)
            .build();
    }

    /**
     * Factory method to create a discharge dispense.
     */
    public static MedicationDispense createDischargeDispense(
        String dispenseId,
        Reference medication,
        Reference patient,
        Reference encounter,
        Reference medicationRequest,
        Reference pharmacist,
        Double quantityValue,
        String quantityUnit,
        String whenHandedOver
    ) {
        return MedicationDispense.builder()
            .resourceType("MedicationDispense")
            .id(dispenseId)
            .status("completed")
            .category(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/medicationdispense-category")
                    .code("discharge")
                    .display("Discharge")
                    .build()))
                .build())
            .medicationReference(medication)
            .subject(patient)
            .context(encounter)
            .authorizingPrescription(List.of(medicationRequest))
            .performer(List.of(Performer.builder()
                .actor(pharmacist)
                .build()))
            .quantity(Observation.Quantity.builder()
                .value(quantityValue)
                .unit(quantityUnit)
                .build())
            .whenHandedOver(whenHandedOver)
            .build();
    }
}
