package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Specimen Resource.
 *
 * Represents a sample collected from a patient for laboratory analysis,
 * including blood, urine, tissue, and other biological materials.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Specimen
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
public class Specimen {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Specimen";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("accessionIdentifier")
    private Identifier accessionIdentifier;

    @JsonProperty("status")
    private String status; // available | unavailable | unsatisfactory | entered-in-error

    @JsonProperty("type")
    private CodeableConcept type;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("receivedTime")
    private String receivedTime;

    @JsonProperty("parent")
    private List<Reference> parent;

    @JsonProperty("request")
    private List<Reference> request; // ServiceRequest

    @JsonProperty("collection")
    private Collection collection;

    @JsonProperty("processing")
    private List<Processing> processing;

    @JsonProperty("container")
    private List<Container> container;

    @JsonProperty("condition")
    private List<CodeableConcept> condition;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Collection {
        @JsonProperty("collector")
        private Reference collector; // Practitioner

        @JsonProperty("collectedDateTime")
        private String collectedDateTime;

        @JsonProperty("collectedPeriod")
        private Practitioner.Period collectedPeriod;

        @JsonProperty("duration")
        private String duration;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("method")
        private CodeableConcept method;

        @JsonProperty("bodySite")
        private CodeableConcept bodySite;

        @JsonProperty("fastingStatusCodeableConcept")
        private CodeableConcept fastingStatusCodeableConcept;

        @JsonProperty("fastingStatusDuration")
        private String fastingStatusDuration;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Processing {
        @JsonProperty("description")
        private String description;

        @JsonProperty("procedure")
        private CodeableConcept procedure;

        @JsonProperty("additive")
        private List<Reference> additive;

        @JsonProperty("timeDateTime")
        private String timeDateTime;

        @JsonProperty("timePeriod")
        private Practitioner.Period timePeriod;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Container {
        @JsonProperty("identifier")
        private List<Identifier> identifier;

        @JsonProperty("description")
        private String description;

        @JsonProperty("type")
        private CodeableConcept type;

        @JsonProperty("capacity")
        private Observation.Quantity capacity;

        @JsonProperty("specimenQuantity")
        private Observation.Quantity specimenQuantity;

        @JsonProperty("additiveCodeableConcept")
        private CodeableConcept additiveCodeableConcept;

        @JsonProperty("additiveReference")
        private Reference additiveReference;
    }

    /**
     * Factory method to create a blood specimen.
     */
    public static Specimen createBloodSpecimen(
        String specimenId,
        Reference patient,
        List<Reference> serviceRequests,
        String collectedDateTime,
        String receivedTime,
        Reference collector,
        Double quantityValue,
        String containerId,
        String containerType
    ) {
        return Specimen.builder()
            .resourceType("Specimen")
            .id(specimenId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/specimen")
                .value(specimenId)
                .build()))
            .status("available")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("119297000")
                    .display("Blood specimen")
                    .build()))
                .build())
            .subject(patient)
            .receivedTime(receivedTime)
            .request(serviceRequests)
            .collection(Collection.builder()
                .collector(collector)
                .collectedDateTime(collectedDateTime)
                .quantity(Observation.Quantity.builder()
                    .value(quantityValue)
                    .unit("mL")
                    .system("http://unitsofmeasure.org")
                    .code("mL")
                    .build())
                .method(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code("82078001")
                        .display("Venipuncture")
                        .build()))
                    .build())
                .bodySite(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code("368208006")
                        .display("Left arm")
                        .build()))
                    .build())
                .build())
            .container(List.of(Container.builder()
                .identifier(List.of(Identifier.builder()
                    .value(containerId)
                    .build()))
                .type(CodeableConcept.builder()
                    .text(containerType != null ? containerType : "Yellow top tube (SST)")
                    .build())
                .build()))
            .build();
    }

    /**
     * Factory method to create a urine specimen.
     */
    public static Specimen createUrineSpecimen(
        String specimenId,
        Reference patient,
        List<Reference> serviceRequests,
        String collectedDateTime,
        String receivedTime,
        Reference collector,
        Double quantityValue
    ) {
        return Specimen.builder()
            .resourceType("Specimen")
            .id(specimenId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/specimen")
                .value(specimenId)
                .build()))
            .status("available")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("122575003")
                    .display("Urine specimen")
                    .build()))
                .build())
            .subject(patient)
            .receivedTime(receivedTime)
            .request(serviceRequests)
            .collection(Collection.builder()
                .collector(collector)
                .collectedDateTime(collectedDateTime)
                .quantity(Observation.Quantity.builder()
                    .value(quantityValue)
                    .unit("mL")
                    .system("http://unitsofmeasure.org")
                    .code("mL")
                    .build())
                .method(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code("73416001")
                        .display("Urine specimen collection, clean catch")
                        .build()))
                    .build())
                .build())
            .container(List.of(Container.builder()
                .type(CodeableConcept.builder()
                    .text("Sterile urine container")
                    .build())
                .build()))
            .build();
    }

    /**
     * Factory method to create a tissue specimen.
     */
    public static Specimen createTissueSpecimen(
        String specimenId,
        Reference patient,
        List<Reference> serviceRequests,
        String collectedDateTime,
        String receivedTime,
        Reference collector,
        String bodySiteCode,
        String bodySiteDisplay,
        String collectionMethodCode,
        String collectionMethodDisplay
    ) {
        return Specimen.builder()
            .resourceType("Specimen")
            .id(specimenId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/specimen")
                .value(specimenId)
                .build()))
            .status("available")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("119376003")
                    .display("Tissue specimen")
                    .build()))
                .build())
            .subject(patient)
            .receivedTime(receivedTime)
            .request(serviceRequests)
            .collection(Collection.builder()
                .collector(collector)
                .collectedDateTime(collectedDateTime)
                .method(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code(collectionMethodCode)
                        .display(collectionMethodDisplay)
                        .build()))
                    .build())
                .bodySite(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code(bodySiteCode)
                        .display(bodySiteDisplay)
                        .build()))
                    .build())
                .build())
            .container(List.of(Container.builder()
                .type(CodeableConcept.builder()
                    .text("Formalin container")
                    .build())
                .build()))
            .build();
    }

    /**
     * Helper to create a specimen for lipid panel test.
     */
    public static Specimen createLipidPanelSpecimen(
        String specimenId,
        Reference patient,
        Reference serviceRequest,
        String collectedDateTime,
        Reference collector
    ) {
        return createBloodSpecimen(
            specimenId,
            patient,
            List.of(serviceRequest),
            collectedDateTime,
            collectedDateTime,
            collector,
            5.0,
            "TUBE-" + specimenId,
            "Yellow top tube (SST)"
        );
    }
}
