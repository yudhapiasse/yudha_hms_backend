package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR AllergyIntolerance Resource.
 *
 * Records allergies and intolerances to medications, foods, environmental factors,
 * and other substances that may cause adverse reactions.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/AllergyIntolerance
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
public class AllergyIntolerance {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "AllergyIntolerance";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("clinicalStatus")
    private CodeableConcept clinicalStatus; // active | inactive | resolved

    @JsonProperty("verificationStatus")
    private CodeableConcept verificationStatus; // unconfirmed | confirmed | refuted | entered-in-error

    @JsonProperty("type")
    private String type; // allergy | intolerance

    @JsonProperty("category")
    private List<String> category; // food | medication | environment | biologic

    @JsonProperty("criticality")
    private String criticality; // low | high | unable-to-assess

    @JsonProperty("code")
    private CodeableConcept code; // Substance/product causing allergy

    @JsonProperty("patient")
    private Reference patient;

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("onsetDateTime")
    private String onsetDateTime;

    @JsonProperty("onsetAge")
    private Observation.Quantity onsetAge;

    @JsonProperty("onsetPeriod")
    private Practitioner.Period onsetPeriod;

    @JsonProperty("onsetRange")
    private String onsetRange;

    @JsonProperty("onsetString")
    private String onsetString;

    @JsonProperty("recordedDate")
    private String recordedDate;

    @JsonProperty("recorder")
    private Reference recorder; // Practitioner who recorded

    @JsonProperty("asserter")
    private Reference asserter; // Source of the information

    @JsonProperty("lastOccurrence")
    private String lastOccurrence;

    @JsonProperty("note")
    private List<Annotation> note;

    @JsonProperty("reaction")
    private List<Reaction> reaction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Annotation {
        @JsonProperty("authorReference")
        private Reference authorReference;

        @JsonProperty("authorString")
        private String authorString;

        @JsonProperty("time")
        private String time;

        @JsonProperty("text")
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Reaction {
        @JsonProperty("substance")
        private CodeableConcept substance;

        @JsonProperty("manifestation")
        private List<CodeableConcept> manifestation; // Symptoms

        @JsonProperty("description")
        private String description;

        @JsonProperty("onset")
        private String onset;

        @JsonProperty("severity")
        private String severity; // mild | moderate | severe

        @JsonProperty("exposureRoute")
        private CodeableConcept exposureRoute;

        @JsonProperty("note")
        private List<Annotation> note;
    }

    /**
     * Factory method to create a medication allergy.
     */
    public static AllergyIntolerance createMedicationAllergy(
        String allergyId,
        Reference patient,
        Reference encounter,
        String medicationCode,
        String medicationDisplay,
        String criticality,
        List<Reaction> reactions
    ) {
        return AllergyIntolerance.builder()
            .resourceType("AllergyIntolerance")
            .id(allergyId)
            .clinicalStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical")
                    .code("active")
                    .display("Active")
                    .build()))
                .build())
            .verificationStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/allergyintolerance-verification")
                    .code("confirmed")
                    .display("Confirmed")
                    .build()))
                .build())
            .type("allergy")
            .category(List.of("medication"))
            .criticality(criticality)
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code(medicationCode)
                    .display(medicationDisplay)
                    .build()))
                .build())
            .patient(patient)
            .encounter(encounter)
            .reaction(reactions)
            .build();
    }

    /**
     * Factory method to create a food allergy.
     */
    public static AllergyIntolerance createFoodAllergy(
        String allergyId,
        Reference patient,
        String foodCode,
        String foodDisplay,
        String criticality,
        List<Reaction> reactions
    ) {
        return AllergyIntolerance.builder()
            .resourceType("AllergyIntolerance")
            .id(allergyId)
            .clinicalStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical")
                    .code("active")
                    .display("Active")
                    .build()))
                .build())
            .verificationStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/allergyintolerance-verification")
                    .code("confirmed")
                    .display("Confirmed")
                    .build()))
                .build())
            .type("allergy")
            .category(List.of("food"))
            .criticality(criticality)
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code(foodCode)
                    .display(foodDisplay)
                    .build()))
                .build())
            .patient(patient)
            .reaction(reactions)
            .build();
    }

    /**
     * Helper to create a reaction with manifestation.
     */
    public static Reaction createReaction(
        String substanceCode,
        String substanceDisplay,
        String manifestationCode,
        String manifestationDisplay,
        String severity
    ) {
        return Reaction.builder()
            .substance(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code(substanceCode)
                    .display(substanceDisplay)
                    .build()))
                .build())
            .manifestation(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code(manifestationCode)
                    .display(manifestationDisplay)
                    .build()))
                .build()))
            .severity(severity)
            .build();
    }
}
