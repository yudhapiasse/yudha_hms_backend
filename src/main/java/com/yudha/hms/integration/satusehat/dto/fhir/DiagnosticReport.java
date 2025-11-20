package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR DiagnosticReport Resource.
 *
 * Represents the findings and interpretation of diagnostic tests performed on patients,
 * such as laboratory results, imaging reports, pathology reports, etc.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/DiagnosticReport
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
public class DiagnosticReport {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "DiagnosticReport";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("basedOn")
    private List<Reference> basedOn; // ServiceRequest references

    @JsonProperty("status")
    private String status; // registered | partial | preliminary | final | amended | corrected | appended | cancelled | entered-in-error | unknown

    @JsonProperty("category")
    private List<CodeableConcept> category; // LAB | RAD | etc.

    @JsonProperty("code")
    private CodeableConcept code; // LOINC code for the report type

    @JsonProperty("subject")
    private Reference subject; // Patient reference (required)

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("effectiveDateTime")
    private String effectiveDateTime; // Time of specimen collection

    @JsonProperty("effectivePeriod")
    private Practitioner.Period effectivePeriod;

    @JsonProperty("issued")
    private String issued; // When the report was issued

    @JsonProperty("performer")
    private List<Reference> performer; // Practitioner/Organization who performed

    @JsonProperty("resultsInterpreter")
    private List<Reference> resultsInterpreter; // Practitioner who interpreted

    @JsonProperty("specimen")
    private List<Reference> specimen;

    @JsonProperty("result")
    private List<Reference> result; // Observation references

    @JsonProperty("imagingStudy")
    private List<Reference> imagingStudy;

    @JsonProperty("media")
    private List<Media> media;

    @JsonProperty("conclusion")
    private String conclusion; // Clinical interpretation/summary

    @JsonProperty("conclusionCode")
    private List<CodeableConcept> conclusionCode; // Coded conclusions

    @JsonProperty("presentedForm")
    private List<Practitioner.Attachment> presentedForm; // Entire report as attachment (PDF, etc.)

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Media {
        @JsonProperty("comment")
        private String comment;

        @JsonProperty("link")
        private Reference link; // Reference to the image/media
    }

    /**
     * Factory method to create a laboratory report.
     */
    public static DiagnosticReport createLabReport(
        String reportId,
        Reference patient,
        Reference encounter,
        String effectiveDateTime,
        String issued,
        List<Reference> results,
        String conclusion
    ) {
        return DiagnosticReport.builder()
            .resourceType("DiagnosticReport")
            .id(reportId)
            .status("final")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v2-0074")
                    .code("LAB")
                    .display("Laboratory")
                    .build()))
                .build()))
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("11502-2")
                    .display("Laboratory report")
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .effectiveDateTime(effectiveDateTime)
            .issued(issued)
            .result(results)
            .conclusion(conclusion)
            .build();
    }

    /**
     * Factory method to create a radiology report.
     */
    public static DiagnosticReport createRadiologyReport(
        String reportId,
        Reference patient,
        Reference encounter,
        String effectiveDateTime,
        String issued,
        Reference radiologist,
        String conclusion,
        List<Practitioner.Attachment> presentedForm
    ) {
        return DiagnosticReport.builder()
            .resourceType("DiagnosticReport")
            .id(reportId)
            .status("final")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v2-0074")
                    .code("RAD")
                    .display("Radiology")
                    .build()))
                .build()))
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("18748-4")
                    .display("Diagnostic imaging study")
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .effectiveDateTime(effectiveDateTime)
            .issued(issued)
            .resultsInterpreter(List.of(radiologist))
            .conclusion(conclusion)
            .presentedForm(presentedForm)
            .build();
    }

    /**
     * Factory method to create a pathology report.
     */
    public static DiagnosticReport createPathologyReport(
        String reportId,
        Reference patient,
        Reference encounter,
        Reference specimen,
        String effectiveDateTime,
        String issued,
        List<Reference> results,
        String conclusion
    ) {
        return DiagnosticReport.builder()
            .resourceType("DiagnosticReport")
            .id(reportId)
            .status("final")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v2-0074")
                    .code("PAT")
                    .display("Pathology")
                    .build()))
                .build()))
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("60567-5")
                    .display("Pathology report")
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .specimen(List.of(specimen))
            .effectiveDateTime(effectiveDateTime)
            .issued(issued)
            .result(results)
            .conclusion(conclusion)
            .build();
    }

    /**
     * Factory method to create a preliminary report.
     */
    public static DiagnosticReport createPreliminaryReport(
        String reportId,
        String category,
        String categoryDisplay,
        Reference patient,
        Reference encounter,
        String effectiveDateTime
    ) {
        return DiagnosticReport.builder()
            .resourceType("DiagnosticReport")
            .id(reportId)
            .status("preliminary")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v2-0074")
                    .code(category)
                    .display(categoryDisplay)
                    .build()))
                .build()))
            .subject(patient)
            .encounter(encounter)
            .effectiveDateTime(effectiveDateTime)
            .build();
    }
}
