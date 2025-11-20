package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR ClinicalImpression Resource.
 *
 * Represents a clinical assessment and conclusion reached about a patient's condition.
 * Includes investigations, findings, summary, and prognosis.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/ClinicalImpression
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
public class ClinicalImpression {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "ClinicalImpression";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // in-progress | completed | entered-in-error

    @JsonProperty("statusReason")
    private CodeableConcept statusReason;

    @JsonProperty("code")
    private CodeableConcept code;

    @JsonProperty("description")
    private String description;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("effectiveDateTime")
    private String effectiveDateTime;

    @JsonProperty("effectivePeriod")
    private Practitioner.Period effectivePeriod;

    @JsonProperty("date")
    private String date;

    @JsonProperty("assessor")
    private Reference assessor; // Practitioner conducting assessment

    @JsonProperty("previous")
    private Reference previous; // Reference to previous assessment

    @JsonProperty("problem")
    private List<Reference> problem; // Condition references

    @JsonProperty("investigation")
    private List<Investigation> investigation;

    @JsonProperty("protocol")
    private List<String> protocol;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("finding")
    private List<Finding> finding;

    @JsonProperty("prognosisCodeableConcept")
    private List<CodeableConcept> prognosisCodeableConcept;

    @JsonProperty("prognosisReference")
    private List<Reference> prognosisReference;

    @JsonProperty("supportingInfo")
    private List<Reference> supportingInfo;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Investigation {
        @JsonProperty("code")
        private CodeableConcept code;

        @JsonProperty("item")
        private List<Reference> item; // Observation, QuestionnaireResponse, etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Finding {
        @JsonProperty("itemCodeableConcept")
        private CodeableConcept itemCodeableConcept;

        @JsonProperty("itemReference")
        private Reference itemReference; // Condition or Observation

        @JsonProperty("basis")
        private String basis;
    }

    /**
     * Factory method to create a clinical impression.
     */
    public static ClinicalImpression createClinicalImpression(
        String impressionId,
        Reference patient,
        Reference encounter,
        String date,
        Reference assessor,
        List<Reference> problems,
        String summary,
        List<Finding> findings,
        List<CodeableConcept> prognosis
    ) {
        return ClinicalImpression.builder()
            .resourceType("ClinicalImpression")
            .id(impressionId)
            .status("completed")
            .subject(patient)
            .encounter(encounter)
            .effectiveDateTime(date)
            .date(date)
            .assessor(assessor)
            .problem(problems)
            .summary(summary)
            .finding(findings)
            .prognosisCodeableConcept(prognosis)
            .build();
    }

    /**
     * Helper to create a physical examination investigation.
     */
    public static Investigation createPhysicalExamInvestigation(List<Reference> observations) {
        return Investigation.builder()
            .code(CodeableConcept.builder()
                .text("Physical Examination")
                .build())
            .item(observations)
            .build();
    }

    /**
     * Helper to create a laboratory investigation.
     */
    public static Investigation createLabInvestigation(List<Reference> labResults) {
        return Investigation.builder()
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("15220000")
                    .display("Laboratory test")
                    .build()))
                .build())
            .item(labResults)
            .build();
    }

    /**
     * Helper to create a finding.
     */
    public static Finding createFinding(
        String code,
        String display,
        String basis
    ) {
        return Finding.builder()
            .itemCodeableConcept(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code(code)
                    .display(display)
                    .build()))
                .build())
            .basis(basis)
            .build();
    }

    /**
     * Helper to create prognosis codeable concept.
     */
    public static CodeableConcept createPrognosisCode(String code, String display) {
        return CodeableConcept.builder()
            .coding(List.of(Coding.builder()
                .system("http://snomed.info/sct")
                .code(code)
                .display(display)
                .build()))
            .build();
    }
}
