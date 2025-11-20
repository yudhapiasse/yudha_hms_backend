package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FHIR Reference data type.
 * 
 * Represents a reference from one resource to another.
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
public class Reference {
    
    @JsonProperty("reference")
    private String reference; // e.g., "Patient/100000030009"
    
    @JsonProperty("type")
    private String type; // Resource type (Patient, Practitioner, etc.)
    
    @JsonProperty("identifier")
    private Identifier identifier;
    
    @JsonProperty("display")
    private String display; // Human-readable description
    
    // Factory methods
    
    public static Reference createPatientReference(String ihsNumber, String patientName) {
        return Reference.builder()
            .reference("Patient/" + ihsNumber)
            .type("Patient")
            .display(patientName)
            .build();
    }
    
    public static Reference createPractitionerReference(String practitionerId, String practitionerName) {
        return Reference.builder()
            .reference("Practitioner/" + practitionerId)
            .type("Practitioner")
            .display(practitionerName)
            .build();
    }
    
    public static Reference createOrganizationReference(String organizationId, String organizationName) {
        return Reference.builder()
            .reference("Organization/" + organizationId)
            .type("Organization")
            .display(organizationName)
            .build();
    }
    
    public static Reference createLocationReference(String locationId, String locationName) {
        return Reference.builder()
            .reference("Location/" + locationId)
            .type("Location")
            .display(locationName)
            .build();
    }
    
    public static Reference createConditionReference(String conditionId, String conditionDisplay) {
        return Reference.builder()
            .reference("Condition/" + conditionId)
            .type("Condition")
            .display(conditionDisplay)
            .build();
    }
}
