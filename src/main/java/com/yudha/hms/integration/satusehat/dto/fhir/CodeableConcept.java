package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR CodeableConcept Data Type.
 *
 * A concept that may be defined by a formal reference to a terminology or ontology
 * or may be provided by text.
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
public class CodeableConcept {

    @JsonProperty("coding")
    private List<Coding> coding;

    @JsonProperty("text")
    private String text;

    /**
     * Create from single coding
     */
    public static CodeableConcept fromCoding(Coding coding) {
        return CodeableConcept.builder()
            .coding(List.of(coding))
            .text(coding.getDisplay())
            .build();
    }

    /**
     * Create marital status
     */
    public static CodeableConcept createMaritalStatus(String code, String display) {
        return fromCoding(Coding.builder()
            .system("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
            .code(code)
            .display(display)
            .build());
    }

    /**
     * Create religion
     */
    public static CodeableConcept createReligion(String code, String display) {
        return fromCoding(Coding.builder()
            .system("https://fhir.kemkes.go.id/r4/CodeSystem/patient-religion")
            .code(code)
            .display(display)
            .build());
    }

    /**
     * Create language
     */
    public static CodeableConcept createLanguage(String code, String display) {
        return fromCoding(Coding.builder()
            .system("urn:ietf:bcp:47")
            .code(code)
            .display(display)
            .build());
    }

    /**
     * Create relationship
     */
    public static CodeableConcept createRelationship(String code, String display) {
        return fromCoding(Coding.builder()
            .system("http://terminology.hl7.org/CodeSystem/v2-0131")
            .code(code)
            .display(display)
            .build());
    }
}
