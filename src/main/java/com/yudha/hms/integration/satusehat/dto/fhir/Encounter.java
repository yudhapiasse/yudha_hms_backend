package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Encounter Resource.
 * 
 * Represents an interaction between a patient and healthcare provider(s).
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Encounter
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
public class Encounter {
    
    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Encounter";
    
    @JsonProperty("id")
    private String id; // SATUSEHAT Encounter ID
    
    @JsonProperty("meta")
    private Meta meta;
    
    @JsonProperty("identifier")
    private List<Identifier> identifier;
    
    @JsonProperty("status")
    private String status; // planned | arrived | triaged | in-progress | onleave | finished | cancelled
    
    @JsonProperty("statusHistory")
    private List<StatusHistory> statusHistory;
    
    @JsonProperty("class")
    private Coding encounterClass; // AMB | IMP | EMER
    
    @JsonProperty("type")
    private List<CodeableConcept> type;
    
    @JsonProperty("serviceType")
    private CodeableConcept serviceType;
    
    @JsonProperty("priority")
    private CodeableConcept priority;
    
    @JsonProperty("subject")
    private Reference subject; // Patient reference (required)
    
    @JsonProperty("participant")
    private List<Participant> participant;
    
    @JsonProperty("period")
    private Period period;
    
    @JsonProperty("reasonCode")
    private List<CodeableConcept> reasonCode;
    
    @JsonProperty("diagnosis")
    private List<Diagnosis> diagnosis;
    
    @JsonProperty("hospitalization")
    private Hospitalization hospitalization;
    
    @JsonProperty("location")
    private List<EncounterLocation> location;
    
    @JsonProperty("serviceProvider")
    private Reference serviceProvider; // Organization reference
    
    // Nested classes
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Meta {
        @JsonProperty("profile")
        private List<String> profile;
        
        @JsonProperty("versionId")
        private String versionId;
        
        @JsonProperty("lastUpdated")
        private String lastUpdated;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatusHistory {
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("period")
        private Period period;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Participant {
        @JsonProperty("type")
        private List<CodeableConcept> type;
        
        @JsonProperty("period")
        private Period period;
        
        @JsonProperty("individual")
        private Reference individual; // Practitioner reference
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Diagnosis {
        @JsonProperty("condition")
        private Reference condition; // Condition reference
        
        @JsonProperty("use")
        private CodeableConcept use; // DD=Discharge, AD=Admission, CC=Chief Complaint
        
        @JsonProperty("rank")
        private Integer rank;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Hospitalization {
        @JsonProperty("admitSource")
        private CodeableConcept admitSource;
        
        @JsonProperty("reAdmission")
        private CodeableConcept reAdmission;
        
        @JsonProperty("dietPreference")
        private List<CodeableConcept> dietPreference;
        
        @JsonProperty("dischargeDisposition")
        private CodeableConcept dischargeDisposition;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EncounterLocation {
        @JsonProperty("location")
        private Reference location; // Location reference
        
        @JsonProperty("status")
        private String status; // planned | active | reserved | completed
        
        @JsonProperty("period")
        private Period period;
    }
    
    // Factory methods for common encounter types
    
    public static Encounter createOutpatient(String encounterId, Reference patient, Reference organization) {
        return Encounter.builder()
            .resourceType("Encounter")
            .id(encounterId)
            .meta(Meta.builder()
                .profile(List.of("https://fhir.kemkes.go.id/r4/StructureDefinition/Encounter"))
                .build())
            .status("in-progress")
            .encounterClass(Coding.builder()
                .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                .code("AMB")
                .display("ambulatory")
                .build())
            .subject(patient)
            .serviceProvider(organization)
            .build();
    }
    
    public static Encounter createInpatient(String encounterId, Reference patient, Reference organization) {
        return Encounter.builder()
            .resourceType("Encounter")
            .id(encounterId)
            .meta(Meta.builder()
                .profile(List.of("https://fhir.kemkes.go.id/r4/StructureDefinition/Encounter"))
                .build())
            .status("in-progress")
            .encounterClass(Coding.builder()
                .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                .code("IMP")
                .display("inpatient encounter")
                .build())
            .subject(patient)
            .serviceProvider(organization)
            .build();
    }
    
    public static Encounter createEmergency(String encounterId, Reference patient, Reference organization) {
        return Encounter.builder()
            .resourceType("Encounter")
            .id(encounterId)
            .meta(Meta.builder()
                .profile(List.of("https://fhir.kemkes.go.id/r4/StructureDefinition/Encounter"))
                .build())
            .status("in-progress")
            .encounterClass(Coding.builder()
                .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                .code("EMER")
                .display("emergency")
                .build())
            .priority(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActPriority")
                    .code("EM")
                    .display("emergency")
                    .build()))
                .build())
            .subject(patient)
            .serviceProvider(organization)
            .build();
    }
}
