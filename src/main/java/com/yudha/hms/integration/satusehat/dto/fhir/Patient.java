package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR R4 Patient Resource for SATUSEHAT.
 *
 * Represents demographic and administrative information about a patient.
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Patient
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
public class Patient {

    @JsonProperty("resourceType")
    private String resourceType = "Patient";

    @JsonProperty("id")
    private String id; // IHS Number from SATUSEHAT

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("name")
    private List<HumanName> name;

    @JsonProperty("telecom")
    private List<ContactPoint> telecom;

    @JsonProperty("gender")
    private String gender; // male | female | other | unknown

    @JsonProperty("birthDate")
    private String birthDate; // YYYY-MM-DD

    @JsonProperty("deceasedBoolean")
    private Boolean deceasedBoolean;

    @JsonProperty("address")
    private List<Address> address;

    @JsonProperty("maritalStatus")
    private CodeableConcept maritalStatus;

    @JsonProperty("multipleBirthBoolean")
    private Boolean multipleBirthBoolean;

    @JsonProperty("contact")
    private List<Contact> contact;

    @JsonProperty("communication")
    private List<Communication> communication;

    @JsonProperty("extension")
    private List<Extension> extension;

    /**
     * Meta information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Meta {
        @JsonProperty("profile")
        private List<String> profile;

        @JsonProperty("lastUpdated")
        private String lastUpdated;

        @JsonProperty("versionId")
        private String versionId;
    }

    /**
     * Contact information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Contact {
        @JsonProperty("relationship")
        private List<CodeableConcept> relationship;

        @JsonProperty("name")
        private HumanName name;

        @JsonProperty("telecom")
        private List<ContactPoint> telecom;

        @JsonProperty("address")
        private Address address;

        @JsonProperty("gender")
        private String gender;
    }

    /**
     * Communication preferences
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Communication {
        @JsonProperty("language")
        private CodeableConcept language;

        @JsonProperty("preferred")
        private Boolean preferred;
    }

    /**
     * Helper method to get NIK identifier
     */
    public String getNik() {
        if (identifier == null) return null;
        return identifier.stream()
            .filter(i -> "https://fhir.kemkes.go.id/id/nik".equals(i.getSystem()))
            .map(Identifier::getValue)
            .findFirst()
            .orElse(null);
    }

    /**
     * Helper method to get IHS Number identifier
     */
    public String getIhsNumber() {
        if (id != null) return id;
        if (identifier == null) return null;
        return identifier.stream()
            .filter(i -> "https://fhir.kemkes.go.id/id/ihs-number".equals(i.getSystem()))
            .map(Identifier::getValue)
            .findFirst()
            .orElse(null);
    }

    /**
     * Helper method to get Medical Record Number
     */
    public String getMedicalRecordNumber() {
        if (identifier == null) return null;
        return identifier.stream()
            .filter(i -> "https://fhir.kemkes.go.id/id/medical-record".equals(i.getSystem()))
            .map(Identifier::getValue)
            .findFirst()
            .orElse(null);
    }

    /**
     * Helper method to get official name
     */
    public String getOfficialName() {
        if (name == null || name.isEmpty()) return null;
        return name.stream()
            .filter(n -> "official".equals(n.getUse()))
            .map(HumanName::getText)
            .findFirst()
            .orElse(name.get(0).getText());
    }
}
