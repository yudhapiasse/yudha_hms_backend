package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Practitioner Resource.
 *
 * Represents a person who is directly or indirectly involved in the provisioning of healthcare.
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Practitioner
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
public class Practitioner {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Practitioner";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("name")
    private List<HumanName> name;

    @JsonProperty("telecom")
    private List<Organization.ContactPoint> telecom;

    @JsonProperty("address")
    private List<Organization.Address> address;

    @JsonProperty("gender")
    private String gender; // male | female | other | unknown

    @JsonProperty("birthDate")
    private String birthDate; // YYYY-MM-DD

    @JsonProperty("photo")
    private List<Attachment> photo;

    @JsonProperty("qualification")
    private List<Qualification> qualification;

    @JsonProperty("communication")
    private List<CodeableConcept> communication;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class HumanName {
        @JsonProperty("use")
        private String use; // official | usual | nickname | anonymous | old | maiden

        @JsonProperty("text")
        private String text; // Full name as displayed

        @JsonProperty("family")
        private String family; // Family/last name

        @JsonProperty("given")
        private List<String> given; // Given/first names

        @JsonProperty("prefix")
        private List<String> prefix; // Dr., Prof., etc.

        @JsonProperty("suffix")
        private List<String> suffix; // Jr., Sr., etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Attachment {
        @JsonProperty("contentType")
        private String contentType; // image/jpeg, image/png, etc.

        @JsonProperty("url")
        private String url;

        @JsonProperty("data")
        private String data; // Base64 encoded

        @JsonProperty("title")
        private String title;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Qualification {
        @JsonProperty("identifier")
        private List<Identifier> identifier;

        @JsonProperty("code")
        private CodeableConcept code; // Coded representation of the qualification

        @JsonProperty("period")
        private Period period;

        @JsonProperty("issuer")
        private Reference issuer; // Organization that issued the credential
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Period {
        @JsonProperty("start")
        private String start; // Start date

        @JsonProperty("end")
        private String end; // End date
    }

    /**
     * Factory method to create a practitioner with NIK identifier.
     */
    public static Practitioner createWithNIK(
        String practitionerId,
        String nik,
        String fullName,
        String gender,
        String birthDate
    ) {
        return Practitioner.builder()
            .resourceType("Practitioner")
            .id(practitionerId)
            .active(true)
            .identifier(List.of(Identifier.builder()
                .use("official")
                .system("https://fhir.kemkes.go.id/id/nik")
                .value(nik)
                .build()))
            .name(List.of(HumanName.builder()
                .use("official")
                .text(fullName)
                .build()))
            .gender(gender)
            .birthDate(birthDate)
            .build();
    }

    /**
     * Factory method to create a practitioner with SIP (Surat Izin Praktik).
     */
    public static Practitioner createWithSIP(
        String practitionerId,
        String sipNumber,
        String fullName,
        String prefix
    ) {
        return Practitioner.builder()
            .resourceType("Practitioner")
            .id(practitionerId)
            .active(true)
            .identifier(List.of(Identifier.builder()
                .use("official")
                .system("https://fhir.kemkes.go.id/id/sip")
                .value(sipNumber)
                .build()))
            .name(List.of(HumanName.builder()
                .use("official")
                .text(fullName)
                .prefix(prefix != null ? List.of(prefix) : null)
                .build()))
            .build();
    }

    /**
     * Factory method to create a complete practitioner with qualifications.
     */
    public static Practitioner createWithQualification(
        String practitionerId,
        String nik,
        String fullName,
        String gender,
        String birthDate,
        String qualificationCode,
        String qualificationDisplay
    ) {
        return Practitioner.builder()
            .resourceType("Practitioner")
            .id(practitionerId)
            .active(true)
            .identifier(List.of(Identifier.builder()
                .use("official")
                .system("https://fhir.kemkes.go.id/id/nik")
                .value(nik)
                .build()))
            .name(List.of(HumanName.builder()
                .use("official")
                .text(fullName)
                .build()))
            .gender(gender)
            .birthDate(birthDate)
            .qualification(List.of(Qualification.builder()
                .code(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.kemkes.go.id/CodeSystem/practitioner-qualification")
                        .code(qualificationCode)
                        .display(qualificationDisplay)
                        .build()))
                    .build())
                .build()))
            .build();
    }
}
