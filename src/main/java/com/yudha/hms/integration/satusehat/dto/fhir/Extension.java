package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Extension Data Type.
 *
 * Optional Extension Element - found in all resources.
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
public class Extension {

    @JsonProperty("url")
    private String url; // identifies the meaning of the extension

    @JsonProperty("valueString")
    private String valueString;

    @JsonProperty("valueCode")
    private String valueCode;

    @JsonProperty("valueInteger")
    private Integer valueInteger;

    @JsonProperty("valueBoolean")
    private Boolean valueBoolean;

    @JsonProperty("valueCodeableConcept")
    private CodeableConcept valueCodeableConcept;

    @JsonProperty("extension")
    private List<Extension> extension; // Nested extensions

    /**
     * Create religion extension
     */
    public static Extension createReligion(String religionCode, String religionDisplay) {
        return Extension.builder()
            .url("https://fhir.kemkes.go.id/r4/StructureDefinition/patient-religion")
            .valueCodeableConcept(CodeableConcept.createReligion(religionCode, religionDisplay))
            .build();
    }

    /**
     * Create nationality extension
     */
    public static Extension createNationality(String nationalityCode) {
        return Extension.builder()
            .url("https://fhir.kemkes.go.id/r4/StructureDefinition/patient-nationality")
            .valueCode(nationalityCode)
            .build();
    }

    /**
     * Create blood type extension
     */
    public static Extension createBloodType(String bloodTypeCode, String bloodTypeDisplay) {
        return Extension.builder()
            .url("https://fhir.kemkes.go.id/r4/StructureDefinition/patient-bloodType")
            .valueCodeableConcept(CodeableConcept.fromCoding(Coding.builder()
                .system("https://fhir.kemkes.go.id/r4/CodeSystem/blood-type")
                .code(bloodTypeCode)
                .display(bloodTypeDisplay)
                .build()))
            .build();
    }
}
