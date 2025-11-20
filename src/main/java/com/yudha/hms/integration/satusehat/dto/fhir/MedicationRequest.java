package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR MedicationRequest Resource.
 *
 * Represents a prescription or medication order for a patient.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/MedicationRequest
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
public class MedicationRequest {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "MedicationRequest";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | on-hold | cancelled | completed | entered-in-error | stopped | draft | unknown

    @JsonProperty("intent")
    private String intent; // proposal | plan | order | original-order | reflex-order | filler-order | instance-order | option

    @JsonProperty("category")
    private List<CodeableConcept> category; // inpatient | outpatient | community | discharge

    @JsonProperty("priority")
    private String priority; // routine | urgent | asap | stat

    @JsonProperty("medicationReference")
    private Reference medicationReference; // Reference to Medication resource

    @JsonProperty("medicationCodeableConcept")
    private CodeableConcept medicationCodeableConcept; // Inline medication with KFA code

    @JsonProperty("subject")
    private Reference subject; // Patient reference (required)

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("authoredOn")
    private String authoredOn; // When the request was created

    @JsonProperty("requester")
    private Reference requester; // Practitioner who prescribed

    @JsonProperty("recorder")
    private Reference recorder; // Person who entered the order

    @JsonProperty("reasonCode")
    private List<CodeableConcept> reasonCode; // Why medication was prescribed

    @JsonProperty("reasonReference")
    private List<Reference> reasonReference; // Condition or Observation

    @JsonProperty("note")
    private List<Condition.Annotation> note;

    @JsonProperty("dosageInstruction")
    private List<DosageInstruction> dosageInstruction;

    @JsonProperty("dispenseRequest")
    private DispenseRequest dispenseRequest;

    @JsonProperty("substitution")
    private Substitution substitution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DosageInstruction {
        @JsonProperty("sequence")
        private Integer sequence;

        @JsonProperty("text")
        private String text; // Free text dosage instructions

        @JsonProperty("additionalInstruction")
        private List<CodeableConcept> additionalInstruction;

        @JsonProperty("patientInstruction")
        private String patientInstruction;

        @JsonProperty("timing")
        private Timing timing;

        @JsonProperty("asNeededBoolean")
        private Boolean asNeededBoolean;

        @JsonProperty("asNeededCodeableConcept")
        private CodeableConcept asNeededCodeableConcept;

        @JsonProperty("site")
        private CodeableConcept site; // Body site

        @JsonProperty("route")
        private CodeableConcept route; // oral | IV | IM | SC | topical

        @JsonProperty("method")
        private CodeableConcept method;

        @JsonProperty("doseAndRate")
        private List<DoseAndRate> doseAndRate;

        @JsonProperty("maxDosePerPeriod")
        private Medication.Ratio maxDosePerPeriod;

        @JsonProperty("maxDosePerAdministration")
        private Observation.Quantity maxDosePerAdministration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Timing {
        @JsonProperty("repeat")
        private Repeat repeat;

        @JsonProperty("code")
        private CodeableConcept code; // BID | TID | QID | etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Repeat {
        @JsonProperty("boundsDuration")
        private Duration boundsDuration;

        @JsonProperty("boundsRange")
        private Range boundsRange;

        @JsonProperty("boundsPeriod")
        private Practitioner.Period boundsPeriod;

        @JsonProperty("count")
        private Integer count;

        @JsonProperty("countMax")
        private Integer countMax;

        @JsonProperty("duration")
        private Double duration;

        @JsonProperty("durationMax")
        private Double durationMax;

        @JsonProperty("durationUnit")
        private String durationUnit; // s | min | h | d | wk | mo | a

        @JsonProperty("frequency")
        private Integer frequency; // e.g., 3 times

        @JsonProperty("frequencyMax")
        private Integer frequencyMax;

        @JsonProperty("period")
        private Double period; // e.g., per day

        @JsonProperty("periodMax")
        private Double periodMax;

        @JsonProperty("periodUnit")
        private String periodUnit; // s | min | h | d | wk | mo | a

        @JsonProperty("dayOfWeek")
        private List<String> dayOfWeek;

        @JsonProperty("timeOfDay")
        private List<String> timeOfDay;

        @JsonProperty("when")
        private List<String> when; // MORN | AFT | EVE | NIGHT | AC | PC
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Duration {
        @JsonProperty("value")
        private Double value;

        @JsonProperty("unit")
        private String unit;

        @JsonProperty("system")
        private String system;

        @JsonProperty("code")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Range {
        @JsonProperty("low")
        private Observation.Quantity low;

        @JsonProperty("high")
        private Observation.Quantity high;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DoseAndRate {
        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("doseRange")
        private Range doseRange;

        @JsonProperty("doseQuantity")
        private Observation.Quantity doseQuantity;

        @JsonProperty("rateRatio")
        private Medication.Ratio rateRatio;

        @JsonProperty("rateRange")
        private Range rateRange;

        @JsonProperty("rateQuantity")
        private Observation.Quantity rateQuantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DispenseRequest {
        @JsonProperty("initialFill")
        private InitialFill initialFill;

        @JsonProperty("dispenseInterval")
        private Duration dispenseInterval;

        @JsonProperty("validityPeriod")
        private Practitioner.Period validityPeriod;

        @JsonProperty("numberOfRepeatsAllowed")
        private Integer numberOfRepeatsAllowed;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("expectedSupplyDuration")
        private Duration expectedSupplyDuration;

        @JsonProperty("performer")
        private Reference performer; // Organization (Pharmacy)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class InitialFill {
        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("duration")
        private Duration duration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Substitution {
        @JsonProperty("allowedBoolean")
        private Boolean allowedBoolean;

        @JsonProperty("allowedCodeableConcept")
        private CodeableConcept allowedCodeableConcept;

        @JsonProperty("reason")
        private CodeableConcept reason;
    }

    /**
     * Factory method to create a basic oral medication prescription.
     */
    public static MedicationRequest createOralPrescription(
        String requestId,
        String kfaCode,
        String medicationName,
        Reference patient,
        Reference encounter,
        Reference practitioner,
        String authoredOn,
        String dosageText,
        Integer frequency,
        Double period,
        String periodUnit,
        Double doseValue,
        String doseUnit,
        Integer durationDays
    ) {
        return MedicationRequest.builder()
            .resourceType("MedicationRequest")
            .id(requestId)
            .status("active")
            .intent("order")
            .medicationCodeableConcept(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(kfaCode)
                    .display(medicationName)
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .authoredOn(authoredOn)
            .requester(practitioner)
            .dosageInstruction(List.of(DosageInstruction.builder()
                .sequence(1)
                .text(dosageText)
                .timing(Timing.builder()
                    .repeat(Repeat.builder()
                        .frequency(frequency)
                        .period(period)
                        .periodUnit(periodUnit)
                        .boundsDuration(Duration.builder()
                            .value((double) durationDays)
                            .unit("days")
                            .system("http://unitsofmeasure.org")
                            .code("d")
                            .build())
                        .build())
                    .build())
                .route(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code("26643006")
                        .display("Oral")
                        .build()))
                    .build())
                .doseAndRate(List.of(DoseAndRate.builder()
                    .doseQuantity(Observation.Quantity.builder()
                        .value(doseValue)
                        .unit(doseUnit)
                        .system("http://unitsofmeasure.org")
                        .code(doseUnit)
                        .build())
                    .build()))
                .build()))
            .build();
    }

    /**
     * Factory method to create a discharge prescription.
     */
    public static MedicationRequest createDischargePrescription(
        String requestId,
        Reference medication,
        Reference patient,
        Reference encounter,
        Reference practitioner,
        String dosageText,
        Integer quantityValue,
        String quantityUnit
    ) {
        return MedicationRequest.builder()
            .resourceType("MedicationRequest")
            .id(requestId)
            .status("active")
            .intent("order")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/medicationrequest-category")
                    .code("discharge")
                    .display("Discharge")
                    .build()))
                .build()))
            .medicationReference(medication)
            .subject(patient)
            .encounter(encounter)
            .requester(practitioner)
            .dosageInstruction(List.of(DosageInstruction.builder()
                .sequence(1)
                .text(dosageText)
                .build()))
            .dispenseRequest(DispenseRequest.builder()
                .quantity(Observation.Quantity.builder()
                    .value((double) quantityValue)
                    .unit(quantityUnit)
                    .build())
                .build())
            .build();
    }
}
