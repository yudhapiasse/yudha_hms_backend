package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR DocumentReference Resource.
 *
 * Used to index and track clinical documents such as PDFs, images, scanned documents,
 * clinical notes, discharge summaries, consent forms, etc.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/DocumentReference
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
public class DocumentReference {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "DocumentReference";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // current | superseded | entered-in-error

    @JsonProperty("docStatus")
    private String docStatus; // preliminary | final | amended | entered-in-error

    @JsonProperty("type")
    private CodeableConcept type; // LOINC document type code

    @JsonProperty("category")
    private List<CodeableConcept> category; // Clinical Note, Discharge Summary, etc.

    @JsonProperty("subject")
    private Reference subject; // Patient reference

    @JsonProperty("date")
    private String date; // When this document reference was created

    @JsonProperty("author")
    private List<Reference> author; // Who authored the document

    @JsonProperty("authenticator")
    private Reference authenticator; // Who authenticated the document

    @JsonProperty("custodian")
    private Reference custodian; // Organization managing the document

    @JsonProperty("relatesTo")
    private List<RelatesTo> relatesTo; // Relationships to other documents

    @JsonProperty("description")
    private String description;

    @JsonProperty("securityLabel")
    private List<CodeableConcept> securityLabel;

    @JsonProperty("content")
    private List<Content> content; // Document content (required)

    @JsonProperty("context")
    private Context context;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RelatesTo {
        @JsonProperty("code")
        private String code; // replaces | transforms | signs | appends

        @JsonProperty("target")
        private Reference target; // Target of the relationship
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Content {
        @JsonProperty("attachment")
        private Practitioner.Attachment attachment; // Required

        @JsonProperty("format")
        private Coding format; // Format/content type
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Context {
        @JsonProperty("encounter")
        private List<Reference> encounter;

        @JsonProperty("event")
        private List<CodeableConcept> event; // Main clinical acts documented

        @JsonProperty("period")
        private Practitioner.Period period; // Time of service documented

        @JsonProperty("facilityType")
        private CodeableConcept facilityType; // Kind of facility where documented

        @JsonProperty("practiceSetting")
        private CodeableConcept practiceSetting; // Additional practice setting

        @JsonProperty("sourcePatientInfo")
        private Reference sourcePatientInfo;

        @JsonProperty("related")
        private List<Reference> related; // Related identifiers or resources
    }

    /**
     * Factory method to create a clinical note document reference.
     */
    public static DocumentReference createClinicalNote(
        String documentId,
        Reference patient,
        Reference encounter,
        Reference author,
        String date,
        String documentUrl,
        String contentType
    ) {
        return DocumentReference.builder()
            .resourceType("DocumentReference")
            .id(documentId)
            .status("current")
            .docStatus("final")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("34133-9")
                    .display("Summary of episode note")
                    .build()))
                .build())
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("11488-4")
                    .display("Consultation note")
                    .build()))
                .build()))
            .subject(patient)
            .date(date)
            .author(List.of(author))
            .content(List.of(Content.builder()
                .attachment(Practitioner.Attachment.builder()
                    .contentType(contentType)
                    .url(documentUrl)
                    .build())
                .build()))
            .context(Context.builder()
                .encounter(List.of(encounter))
                .build())
            .build();
    }

    /**
     * Factory method to create a discharge summary document reference.
     */
    public static DocumentReference createDischargeSummary(
        String documentId,
        Reference patient,
        Reference encounter,
        Reference author,
        String date,
        String documentUrl
    ) {
        return DocumentReference.builder()
            .resourceType("DocumentReference")
            .id(documentId)
            .status("current")
            .docStatus("final")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("18842-5")
                    .display("Discharge summary")
                    .build()))
                .build())
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("18842-5")
                    .display("Discharge summary")
                    .build()))
                .build()))
            .subject(patient)
            .date(date)
            .author(List.of(author))
            .content(List.of(Content.builder()
                .attachment(Practitioner.Attachment.builder()
                    .contentType("application/pdf")
                    .url(documentUrl)
                    .build())
                .build()))
            .context(Context.builder()
                .encounter(List.of(encounter))
                .build())
            .build();
    }

    /**
     * Factory method to create a consent form document reference.
     */
    public static DocumentReference createConsentForm(
        String documentId,
        Reference patient,
        Reference authenticator,
        String date,
        String documentUrl
    ) {
        return DocumentReference.builder()
            .resourceType("DocumentReference")
            .id(documentId)
            .status("current")
            .docStatus("final")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("59284-0")
                    .display("Consent Document")
                    .build()))
                .build())
            .subject(patient)
            .date(date)
            .authenticator(authenticator)
            .content(List.of(Content.builder()
                .attachment(Practitioner.Attachment.builder()
                    .contentType("application/pdf")
                    .url(documentUrl)
                    .build())
                .build()))
            .build();
    }

    /**
     * Factory method to create an imaging result document reference.
     */
    public static DocumentReference createImagingResult(
        String documentId,
        Reference patient,
        Reference encounter,
        Reference radiologist,
        String date,
        String imageUrl,
        String contentType
    ) {
        return DocumentReference.builder()
            .resourceType("DocumentReference")
            .id(documentId)
            .status("current")
            .docStatus("final")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("18748-4")
                    .display("Diagnostic imaging study")
                    .build()))
                .build())
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("LP29684-5")
                    .display("Radiology")
                    .build()))
                .build()))
            .subject(patient)
            .date(date)
            .author(List.of(radiologist))
            .content(List.of(Content.builder()
                .attachment(Practitioner.Attachment.builder()
                    .contentType(contentType)
                    .url(imageUrl)
                    .build())
                .build()))
            .context(Context.builder()
                .encounter(List.of(encounter))
                .build())
            .build();
    }

    /**
     * Factory method to create a lab result document reference.
     */
    public static DocumentReference createLabResult(
        String documentId,
        Reference patient,
        Reference encounter,
        String date,
        String documentUrl
    ) {
        return DocumentReference.builder()
            .resourceType("DocumentReference")
            .id(documentId)
            .status("current")
            .docStatus("final")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("11502-2")
                    .display("Laboratory report")
                    .build()))
                .build())
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("LP7839-6")
                    .display("Pathology")
                    .build()))
                .build()))
            .subject(patient)
            .date(date)
            .content(List.of(Content.builder()
                .attachment(Practitioner.Attachment.builder()
                    .contentType("application/pdf")
                    .url(documentUrl)
                    .build())
                .build()))
            .context(Context.builder()
                .encounter(List.of(encounter))
                .build())
            .build();
    }
}
