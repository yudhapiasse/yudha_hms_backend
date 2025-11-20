package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Medication Resource.
 *
 * Represents a medication product or substance.
 * Uses KFA (Kode Farmasi dan Alkes) codes for Indonesian pharmaceutical products.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Medication
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
public class Medication {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Medication";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("code")
    private CodeableConcept code; // KFA code for the medication

    @JsonProperty("status")
    private String status; // active | inactive | entered-in-error

    @JsonProperty("manufacturer")
    private Reference manufacturer; // Organization that manufactures

    @JsonProperty("form")
    private CodeableConcept form; // powder | tablets | capsule | solution | suspension | injection

    @JsonProperty("amount")
    private Ratio amount;

    @JsonProperty("ingredient")
    private List<Ingredient> ingredient;

    @JsonProperty("batch")
    private Batch batch;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Ingredient {
        @JsonProperty("itemCodeableConcept")
        private CodeableConcept itemCodeableConcept;

        @JsonProperty("itemReference")
        private Reference itemReference;

        @JsonProperty("isActive")
        private Boolean isActive;

        @JsonProperty("strength")
        private Ratio strength;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Batch {
        @JsonProperty("lotNumber")
        private String lotNumber;

        @JsonProperty("expirationDate")
        private String expirationDate; // YYYY-MM-DD
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Ratio {
        @JsonProperty("numerator")
        private Observation.Quantity numerator;

        @JsonProperty("denominator")
        private Observation.Quantity denominator;
    }

    /**
     * Factory method to create a medication with KFA code.
     */
    public static Medication createWithKFA(
        String medicationId,
        String kfaCode,
        String medicationName,
        String formCode,
        String formDisplay
    ) {
        return Medication.builder()
            .resourceType("Medication")
            .id(medicationId)
            .status("active")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(kfaCode)
                    .display(medicationName)
                    .build()))
                .build())
            .form(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.kemkes.go.id/CodeSystem/medication-form")
                    .code(formCode)
                    .display(formDisplay)
                    .build()))
                .build())
            .build();
    }

    /**
     * Factory method to create a tablet medication.
     */
    public static Medication createTablet(
        String medicationId,
        String kfaCode,
        String medicationName,
        Double strengthValue,
        String strengthUnit
    ) {
        return Medication.builder()
            .resourceType("Medication")
            .id(medicationId)
            .status("active")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(kfaCode)
                    .display(medicationName)
                    .build()))
                .build())
            .form(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.kemkes.go.id/CodeSystem/medication-form")
                    .code("TAB")
                    .display("Tablet")
                    .build()))
                .build())
            .ingredient(List.of(Ingredient.builder()
                .isActive(true)
                .strength(Ratio.builder()
                    .numerator(Observation.Quantity.builder()
                        .value(strengthValue)
                        .unit(strengthUnit)
                        .system("http://unitsofmeasure.org")
                        .code(strengthUnit)
                        .build())
                    .denominator(Observation.Quantity.builder()
                        .value(1.0)
                        .unit("TAB")
                        .system("http://terminology.hl7.org/CodeSystem/v3-orderableDrugForm")
                        .code("TAB")
                        .build())
                    .build())
                .build()))
            .build();
    }

    /**
     * Factory method to create an injection medication.
     */
    public static Medication createInjection(
        String medicationId,
        String kfaCode,
        String medicationName,
        Double volumeValue,
        String volumeUnit
    ) {
        return Medication.builder()
            .resourceType("Medication")
            .id(medicationId)
            .status("active")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(kfaCode)
                    .display(medicationName)
                    .build()))
                .build())
            .form(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.kemkes.go.id/CodeSystem/medication-form")
                    .code("INJ")
                    .display("Injection")
                    .build()))
                .build())
            .amount(Ratio.builder()
                .numerator(Observation.Quantity.builder()
                    .value(volumeValue)
                    .unit(volumeUnit)
                    .system("http://unitsofmeasure.org")
                    .code(volumeUnit)
                    .build())
                .build())
            .build();
    }

    /**
     * Factory method to create a capsule medication.
     */
    public static Medication createCapsule(
        String medicationId,
        String kfaCode,
        String medicationName,
        Double strengthValue,
        String strengthUnit
    ) {
        return Medication.builder()
            .resourceType("Medication")
            .id(medicationId)
            .status("active")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(kfaCode)
                    .display(medicationName)
                    .build()))
                .build())
            .form(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.kemkes.go.id/CodeSystem/medication-form")
                    .code("CAP")
                    .display("Capsule")
                    .build()))
                .build())
            .ingredient(List.of(Ingredient.builder()
                .isActive(true)
                .strength(Ratio.builder()
                    .numerator(Observation.Quantity.builder()
                        .value(strengthValue)
                        .unit(strengthUnit)
                        .system("http://unitsofmeasure.org")
                        .code(strengthUnit)
                        .build())
                    .denominator(Observation.Quantity.builder()
                        .value(1.0)
                        .unit("CAP")
                        .build())
                    .build())
                .build()))
            .build();
    }

    /**
     * Factory method to create a syrup/solution medication.
     */
    public static Medication createSyrup(
        String medicationId,
        String kfaCode,
        String medicationName,
        Double bottleVolume,
        String volumeUnit
    ) {
        return Medication.builder()
            .resourceType("Medication")
            .id(medicationId)
            .status("active")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(kfaCode)
                    .display(medicationName)
                    .build()))
                .build())
            .form(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.kemkes.go.id/CodeSystem/medication-form")
                    .code("SYR")
                    .display("Syrup")
                    .build()))
                .build())
            .amount(Ratio.builder()
                .numerator(Observation.Quantity.builder()
                    .value(bottleVolume)
                    .unit(volumeUnit)
                    .system("http://unitsofmeasure.org")
                    .code(volumeUnit)
                    .build())
                .build())
            .build();
    }
}
