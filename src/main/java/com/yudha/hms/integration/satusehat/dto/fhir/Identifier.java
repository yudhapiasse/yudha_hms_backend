package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FHIR Identifier Data Type.
 *
 * An identifier intended for computation.
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
public class Identifier {

    @JsonProperty("use")
    private String use; // usual | official | temp | secondary

    @JsonProperty("system")
    private String system; // The namespace for the identifier value

    @JsonProperty("value")
    private String value; // The value that is unique

    @JsonProperty("type")
    private CodeableConcept type;

    @JsonProperty("period")
    private Period period;

    /**
     * Create NIK identifier
     */
    public static Identifier createNik(String nikValue) {
        return Identifier.builder()
            .use("official")
            .system("https://fhir.kemkes.go.id/id/nik")
            .value(nikValue)
            .build();
    }

    /**
     * Create IHS Number identifier
     */
    public static Identifier createIhsNumber(String ihsNumber) {
        return Identifier.builder()
            .use("official")
            .system("https://fhir.kemkes.go.id/id/ihs-number")
            .value(ihsNumber)
            .build();
    }

    /**
     * Create Medical Record Number identifier
     */
    public static Identifier createMedicalRecordNumber(String mrNumber) {
        return Identifier.builder()
            .use("usual")
            .system("https://fhir.kemkes.go.id/id/medical-record")
            .value(mrNumber)
            .build();
    }
}
