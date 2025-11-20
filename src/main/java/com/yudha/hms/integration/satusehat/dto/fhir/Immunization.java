package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Immunization Resource.
 *
 * Records vaccine administration events including vaccine type, dose, site,
 * route, performer, and protocol information.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Immunization
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
public class Immunization {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Immunization";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // completed | entered-in-error | not-done

    @JsonProperty("statusReason")
    private CodeableConcept statusReason;

    @JsonProperty("vaccineCode")
    private CodeableConcept vaccineCode;

    @JsonProperty("patient")
    private Reference patient;

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("occurrenceDateTime")
    private String occurrenceDateTime;

    @JsonProperty("occurrenceString")
    private String occurrenceString;

    @JsonProperty("recorded")
    private String recorded;

    @JsonProperty("primarySource")
    private Boolean primarySource;

    @JsonProperty("reportOrigin")
    private CodeableConcept reportOrigin;

    @JsonProperty("location")
    private Reference location;

    @JsonProperty("manufacturer")
    private Reference manufacturer;

    @JsonProperty("lotNumber")
    private String lotNumber;

    @JsonProperty("expirationDate")
    private String expirationDate;

    @JsonProperty("site")
    private CodeableConcept site; // Body site

    @JsonProperty("route")
    private CodeableConcept route; // Administration route

    @JsonProperty("doseQuantity")
    private Observation.Quantity doseQuantity;

    @JsonProperty("performer")
    private List<Performer> performer;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @JsonProperty("reasonCode")
    private List<CodeableConcept> reasonCode;

    @JsonProperty("reasonReference")
    private List<Reference> reasonReference;

    @JsonProperty("isSubpotent")
    private Boolean isSubpotent;

    @JsonProperty("subpotentReason")
    private List<CodeableConcept> subpotentReason;

    @JsonProperty("education")
    private List<Education> education;

    @JsonProperty("programEligibility")
    private List<CodeableConcept> programEligibility;

    @JsonProperty("fundingSource")
    private CodeableConcept fundingSource;

    @JsonProperty("reaction")
    private List<Reaction> reaction;

    @JsonProperty("protocolApplied")
    private List<ProtocolApplied> protocolApplied;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Education {
        @JsonProperty("documentType")
        private String documentType;

        @JsonProperty("reference")
        private String reference;

        @JsonProperty("publicationDate")
        private String publicationDate;

        @JsonProperty("presentationDate")
        private String presentationDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Reaction {
        @JsonProperty("date")
        private String date;

        @JsonProperty("detail")
        private Reference detail; // Observation

        @JsonProperty("reported")
        private Boolean reported;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProtocolApplied {
        @JsonProperty("series")
        private String series;

        @JsonProperty("authority")
        private Reference authority;

        @JsonProperty("targetDisease")
        private List<CodeableConcept> targetDisease;

        @JsonProperty("doseNumberPositiveInt")
        private Integer doseNumberPositiveInt;

        @JsonProperty("doseNumberString")
        private String doseNumberString;

        @JsonProperty("seriesDosesPositiveInt")
        private Integer seriesDosesPositiveInt;

        @JsonProperty("seriesDosesString")
        private String seriesDosesString;
    }

    /**
     * Factory method to create an immunization record.
     */
    public static Immunization createImmunization(
        String immunizationId,
        Reference patient,
        Reference encounter,
        String vaccineCode,
        String vaccineDisplay,
        String occurrenceDateTime,
        Reference location,
        Reference performer,
        String lotNumber,
        String expirationDate,
        Integer doseNumber,
        Integer seriesDoses
    ) {
        return Immunization.builder()
            .resourceType("Immunization")
            .id(immunizationId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/immunization")
                .value(immunizationId)
                .build()))
            .status("completed")
            .vaccineCode(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code(vaccineCode)
                    .display(vaccineDisplay)
                    .build()))
                .build())
            .patient(patient)
            .encounter(encounter)
            .occurrenceDateTime(occurrenceDateTime)
            .recorded(occurrenceDateTime)
            .primarySource(true)
            .location(location)
            .lotNumber(lotNumber)
            .expirationDate(expirationDate)
            .performer(List.of(Performer.builder()
                .function(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/v2-0443")
                        .code("AP")
                        .display("Administering Provider")
                        .build()))
                    .build())
                .actor(performer)
                .build()))
            .protocolApplied(List.of(ProtocolApplied.builder()
                .doseNumberPositiveInt(doseNumber)
                .seriesDosesPositiveInt(seriesDoses)
                .build()))
            .build();
    }

    /**
     * Factory method to create COVID-19 vaccine record.
     */
    public static Immunization createCOVID19Vaccine(
        String immunizationId,
        Reference patient,
        Reference encounter,
        String occurrenceDateTime,
        Reference location,
        Reference performer,
        String lotNumber,
        String expirationDate,
        Integer doseNumber
    ) {
        return Immunization.builder()
            .resourceType("Immunization")
            .id(immunizationId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/immunization")
                .value(immunizationId)
                .build()))
            .status("completed")
            .vaccineCode(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://sys-ids.kemkes.go.id/kfa")
                    .code("VCN001")
                    .display("COVID-19 Vaccine")
                    .build()))
                .build())
            .patient(patient)
            .encounter(encounter)
            .occurrenceDateTime(occurrenceDateTime)
            .recorded(occurrenceDateTime)
            .primarySource(true)
            .location(location)
            .lotNumber(lotNumber)
            .expirationDate(expirationDate)
            .site(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("72098002")
                    .display("Entire left upper arm")
                    .build()))
                .build())
            .route(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("78421000")
                    .display("Intramuscular route")
                    .build()))
                .build())
            .doseQuantity(Observation.Quantity.builder()
                .value(0.5)
                .unit("mL")
                .system("http://unitsofmeasure.org")
                .code("mL")
                .build())
            .performer(List.of(Performer.builder()
                .function(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/v2-0443")
                        .code("AP")
                        .display("Administering Provider")
                        .build()))
                    .build())
                .actor(performer)
                .build()))
            .protocolApplied(List.of(ProtocolApplied.builder()
                .doseNumberPositiveInt(doseNumber)
                .seriesDosesPositiveInt(2)
                .build()))
            .build();
    }
}
