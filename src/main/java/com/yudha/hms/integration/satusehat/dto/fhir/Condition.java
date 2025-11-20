package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Condition Resource (Diagnosis).
 * 
 * Represents a clinical condition, problem, diagnosis, or other event.
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Condition
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
public class Condition {
    
    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Condition";
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("meta")
    private Encounter.Meta meta;
    
    @JsonProperty("clinicalStatus")
    private CodeableConcept clinicalStatus; // active | recurrence | relapse | inactive | remission | resolved
    
    @JsonProperty("verificationStatus")
    private CodeableConcept verificationStatus; // unconfirmed | provisional | differential | confirmed
    
    @JsonProperty("category")
    private List<CodeableConcept> category;
    
    @JsonProperty("severity")
    private CodeableConcept severity;
    
    @JsonProperty("code")
    private CodeableConcept code; // ICD-10 code
    
    @JsonProperty("subject")
    private Reference subject; // Patient reference (required)
    
    @JsonProperty("encounter")
    private Reference encounter; // Encounter reference
    
    @JsonProperty("onsetDateTime")
    private String onsetDateTime;
    
    @JsonProperty("recordedDate")
    private String recordedDate;
    
    @JsonProperty("recorder")
    private Reference recorder; // Practitioner reference
    
    @JsonProperty("note")
    private List<Annotation> note;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Annotation {
        @JsonProperty("authorReference")
        private Reference authorReference;
        
        @JsonProperty("time")
        private String time;
        
        @JsonProperty("text")
        private String text;
    }
    
    // Factory method for encounter diagnosis
    public static Condition createEncounterDiagnosis(
        String icd10Code, 
        String display,
        Reference patient,
        Reference encounter
    ) {
        return Condition.builder()
            .resourceType("Condition")
            .clinicalStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/condition-clinical")
                    .code("active")
                    .display("Active")
                    .build()))
                .build())
            .verificationStatus(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/condition-ver-status")
                    .code("confirmed")
                    .display("Confirmed")
                    .build()))
                .build())
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/condition-category")
                    .code("encounter-diagnosis")
                    .display("Encounter Diagnosis")
                    .build()))
                .build()))
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://hl7.org/fhir/sid/icd-10")
                    .code(icd10Code)
                    .display(display)
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .build();
    }
}
