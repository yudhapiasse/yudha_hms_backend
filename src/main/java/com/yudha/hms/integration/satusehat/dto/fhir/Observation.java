package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Observation Resource.
 * 
 * Represents measurements and simple assertions (vital signs, lab results, etc.).
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Observation
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
public class Observation {
    
    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Observation";
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("meta")
    private Encounter.Meta meta;
    
    @JsonProperty("status")
    private String status; // registered | preliminary | final | amended
    
    @JsonProperty("category")
    private List<CodeableConcept> category;
    
    @JsonProperty("code")
    private CodeableConcept code; // LOINC code
    
    @JsonProperty("subject")
    private Reference subject; // Patient reference
    
    @JsonProperty("encounter")
    private Reference encounter;
    
    @JsonProperty("effectiveDateTime")
    private String effectiveDateTime;
    
    @JsonProperty("issued")
    private String issued;
    
    @JsonProperty("performer")
    private List<Reference> performer;
    
    @JsonProperty("valueQuantity")
    private Quantity valueQuantity;
    
    @JsonProperty("valueString")
    private String valueString;
    
    @JsonProperty("valueCodeableConcept")
    private CodeableConcept valueCodeableConcept;
    
    @JsonProperty("component")
    private List<Component> component;
    
    @JsonProperty("referenceRange")
    private List<ReferenceRange> referenceRange;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Quantity {
        @JsonProperty("value")
        private Double value;
        
        @JsonProperty("unit")
        private String unit;
        
        @JsonProperty("system")
        private String system;
        
        @JsonProperty("code")
        private String code;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Component {
        @JsonProperty("code")
        private CodeableConcept code;
        
        @JsonProperty("valueQuantity")
        private Quantity valueQuantity;
        
        @JsonProperty("valueString")
        private String valueString;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReferenceRange {
        @JsonProperty("low")
        private Quantity low;
        
        @JsonProperty("high")
        private Quantity high;
        
        @JsonProperty("text")
        private String text;
    }
    
    // Factory method for vital signs
    public static Observation createVitalSign(
        String loincCode,
        String display,
        Double value,
        String unit,
        Reference patient,
        Reference encounter
    ) {
        return Observation.builder()
            .resourceType("Observation")
            .status("final")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/observation-category")
                    .code("vital-signs")
                    .display("Vital Signs")
                    .build()))
                .build()))
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code(loincCode)
                    .display(display)
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .valueQuantity(Quantity.builder()
                .value(value)
                .unit(unit)
                .system("http://unitsofmeasure.org")
                .build())
            .build();
    }
}
