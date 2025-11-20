package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR ImagingStudy Resource.
 *
 * Represents imaging studies such as X-ray, CT, MRI, ultrasound, and other
 * diagnostic imaging procedures with DICOM metadata.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/ImagingStudy
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
public class ImagingStudy {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "ImagingStudy";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // registered | available | cancelled | entered-in-error | unknown

    @JsonProperty("modality")
    private List<Coding> modality;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("started")
    private String started;

    @JsonProperty("basedOn")
    private List<Reference> basedOn; // ServiceRequest

    @JsonProperty("referrer")
    private Reference referrer;

    @JsonProperty("interpreter")
    private List<Reference> interpreter;

    @JsonProperty("endpoint")
    private List<Reference> endpoint;

    @JsonProperty("numberOfSeries")
    private Integer numberOfSeries;

    @JsonProperty("numberOfInstances")
    private Integer numberOfInstances;

    @JsonProperty("procedureReference")
    private Reference procedureReference;

    @JsonProperty("procedureCode")
    private List<CodeableConcept> procedureCode;

    @JsonProperty("location")
    private Reference location;

    @JsonProperty("reasonCode")
    private List<CodeableConcept> reasonCode;

    @JsonProperty("reasonReference")
    private List<Reference> reasonReference;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @JsonProperty("description")
    private String description;

    @JsonProperty("series")
    private List<Series> series;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Series {
        @JsonProperty("uid")
        private String uid; // DICOM Series Instance UID

        @JsonProperty("number")
        private Integer number;

        @JsonProperty("modality")
        private Coding modality; // DICOM modality code

        @JsonProperty("description")
        private String description;

        @JsonProperty("numberOfInstances")
        private Integer numberOfInstances;

        @JsonProperty("endpoint")
        private List<Reference> endpoint;

        @JsonProperty("bodySite")
        private Coding bodySite;

        @JsonProperty("laterality")
        private Coding laterality;

        @JsonProperty("specimen")
        private List<Reference> specimen;

        @JsonProperty("started")
        private String started;

        @JsonProperty("performer")
        private List<Performer> performer;

        @JsonProperty("instance")
        private List<Instance> instance;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Performer {
        @JsonProperty("function")
        private CodeableConcept function;

        @JsonProperty("actor")
        private Reference actor; // Practitioner
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Instance {
        @JsonProperty("uid")
        private String uid; // DICOM SOP Instance UID

        @JsonProperty("sopClass")
        private Coding sopClass;

        @JsonProperty("number")
        private Integer number;

        @JsonProperty("title")
        private String title;
    }

    /**
     * Factory method to create a CT imaging study.
     */
    public static ImagingStudy createCTStudy(
        String studyId,
        String accessionNumber,
        Reference patient,
        Reference encounter,
        String started,
        String bodyPartCode,
        String bodyPartDisplay,
        Integer numberOfSeries,
        Integer numberOfInstances,
        Reference location,
        List<CodeableConcept> reasonCodes,
        List<Series> series
    ) {
        return ImagingStudy.builder()
            .resourceType("ImagingStudy")
            .id(studyId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/accession")
                .value(accessionNumber)
                .build()))
            .status("available")
            .modality(List.of(Coding.builder()
                .system("http://dicom.nema.org/resources/ontology/DCM")
                .code("CT")
                .display("Computed Tomography")
                .build()))
            .subject(patient)
            .encounter(encounter)
            .started(started)
            .numberOfSeries(numberOfSeries)
            .numberOfInstances(numberOfInstances)
            .procedureCode(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("30746-2")
                    .display("CT " + bodyPartDisplay)
                    .build()))
                .build()))
            .location(location)
            .reasonCode(reasonCodes)
            .series(series)
            .build();
    }

    /**
     * Factory method to create an X-ray imaging study.
     */
    public static ImagingStudy createXRayStudy(
        String studyId,
        String accessionNumber,
        Reference patient,
        Reference encounter,
        String started,
        String bodyPartCode,
        String bodyPartDisplay,
        Integer numberOfSeries,
        Integer numberOfInstances,
        Reference location,
        List<Series> series
    ) {
        return ImagingStudy.builder()
            .resourceType("ImagingStudy")
            .id(studyId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/accession")
                .value(accessionNumber)
                .build()))
            .status("available")
            .modality(List.of(Coding.builder()
                .system("http://dicom.nema.org/resources/ontology/DCM")
                .code("DX")
                .display("Digital Radiography")
                .build()))
            .subject(patient)
            .encounter(encounter)
            .started(started)
            .numberOfSeries(numberOfSeries)
            .numberOfInstances(numberOfInstances)
            .procedureCode(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("36643-5")
                    .display(bodyPartDisplay + " X-ray")
                    .build()))
                .build()))
            .location(location)
            .series(series)
            .build();
    }

    /**
     * Factory method to create an MRI imaging study.
     */
    public static ImagingStudy createMRIStudy(
        String studyId,
        String accessionNumber,
        Reference patient,
        Reference encounter,
        String started,
        String bodyPartCode,
        String bodyPartDisplay,
        Integer numberOfSeries,
        Integer numberOfInstances,
        Reference location,
        List<CodeableConcept> reasonCodes,
        List<Series> series
    ) {
        return ImagingStudy.builder()
            .resourceType("ImagingStudy")
            .id(studyId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/accession")
                .value(accessionNumber)
                .build()))
            .status("available")
            .modality(List.of(Coding.builder()
                .system("http://dicom.nema.org/resources/ontology/DCM")
                .code("MR")
                .display("Magnetic Resonance")
                .build()))
            .subject(patient)
            .encounter(encounter)
            .started(started)
            .numberOfSeries(numberOfSeries)
            .numberOfInstances(numberOfInstances)
            .procedureCode(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("24558-9")
                    .display("MRI " + bodyPartDisplay)
                    .build()))
                .build()))
            .location(location)
            .reasonCode(reasonCodes)
            .series(series)
            .build();
    }

    /**
     * Factory method to create an ultrasound imaging study.
     */
    public static ImagingStudy createUltrasoundStudy(
        String studyId,
        String accessionNumber,
        Reference patient,
        Reference encounter,
        String started,
        String bodyPartCode,
        String bodyPartDisplay,
        Integer numberOfSeries,
        Integer numberOfInstances,
        Reference location,
        List<Series> series
    ) {
        return ImagingStudy.builder()
            .resourceType("ImagingStudy")
            .id(studyId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/accession")
                .value(accessionNumber)
                .build()))
            .status("available")
            .modality(List.of(Coding.builder()
                .system("http://dicom.nema.org/resources/ontology/DCM")
                .code("US")
                .display("Ultrasound")
                .build()))
            .subject(patient)
            .encounter(encounter)
            .started(started)
            .numberOfSeries(numberOfSeries)
            .numberOfInstances(numberOfInstances)
            .procedureCode(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("11525-3")
                    .display("US " + bodyPartDisplay)
                    .build()))
                .build()))
            .location(location)
            .series(series)
            .build();
    }

    /**
     * Helper to create a chest CT series with instances.
     */
    public static Series createChestCTSeries(
        String seriesUid,
        Integer seriesNumber,
        Integer numberOfInstances,
        String started,
        Reference performer
    ) {
        return Series.builder()
            .uid(seriesUid)
            .number(seriesNumber)
            .modality(Coding.builder()
                .system("http://dicom.nema.org/resources/ontology/DCM")
                .code("CT")
                .build())
            .description("Chest CT with Contrast")
            .numberOfInstances(numberOfInstances)
            .bodySite(Coding.builder()
                .system("http://snomed.info/sct")
                .code("51185008")
                .display("Thorax")
                .build())
            .started(started)
            .performer(List.of(Performer.builder()
                .function(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/v3-ParticipationType")
                        .code("PRF")
                        .display("Performer")
                        .build()))
                    .build())
                .actor(performer)
                .build()))
            .build();
    }

    /**
     * Helper to create a DICOM instance.
     */
    public static Instance createDicomInstance(
        String instanceUid,
        Integer instanceNumber,
        String sopClassUid
    ) {
        return Instance.builder()
            .uid(instanceUid)
            .sopClass(Coding.builder()
                .system("urn:ietf:rfc:3986")
                .code("urn:oid:" + sopClassUid)
                .build())
            .number(instanceNumber)
            .build();
    }
}
