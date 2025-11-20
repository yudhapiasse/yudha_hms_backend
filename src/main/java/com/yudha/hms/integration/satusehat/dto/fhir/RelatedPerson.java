package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR RelatedPerson Resource.
 *
 * Represents a person who is related to a patient, such as family members,
 * caregivers, emergency contacts, or guardians. Used for managing patient
 * relationships and contact information.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/RelatedPerson
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
public class RelatedPerson {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "RelatedPerson";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("patient")
    private Reference patient;

    @JsonProperty("relationship")
    private List<CodeableConcept> relationship;

    @JsonProperty("name")
    private List<HumanName> name;

    @JsonProperty("telecom")
    private List<ContactPoint> telecom;

    @JsonProperty("gender")
    private String gender; // male | female | other | unknown

    @JsonProperty("birthDate")
    private String birthDate;

    @JsonProperty("address")
    private List<Address> address;

    @JsonProperty("photo")
    private List<String> photo;

    @JsonProperty("period")
    private Practitioner.Period period;

    @JsonProperty("communication")
    private List<Communication> communication;

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
     * Factory method to create a family member (mother).
     */
    public static RelatedPerson createMother(
        String relatedPersonId,
        String nik,
        Reference patient,
        String name,
        String phoneNumber,
        String birthDate,
        String addressText,
        String city,
        String state,
        String postalCode
    ) {
        return RelatedPerson.builder()
            .resourceType("RelatedPerson")
            .id(relatedPersonId)
            .identifier(List.of(Identifier.builder()
                .use("official")
                .system("https://fhir.kemkes.go.id/id/nik")
                .value(nik)
                .build()))
            .active(true)
            .patient(patient)
            .relationship(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("MTH")
                    .display("Mother")
                    .build()))
                .build()))
            .name(List.of(HumanName.builder()
                .use("official")
                .text(name)
                .build()))
            .telecom(List.of(ContactPoint.builder()
                .system("phone")
                .value(phoneNumber)
                .use("mobile")
                .build()))
            .gender("female")
            .birthDate(birthDate)
            .address(List.of(Address.builder()
                .use("home")
                .text(addressText)
                .city(city)
                .state(state)
                .postalCode(postalCode)
                .country("ID")
                .build()))
            .build();
    }

    /**
     * Factory method to create an emergency contact.
     */
    public static RelatedPerson createEmergencyContact(
        String relatedPersonId,
        String nik,
        Reference patient,
        String relationshipCode,
        String relationshipDisplay,
        String name,
        String phoneNumber,
        String gender
    ) {
        return RelatedPerson.builder()
            .resourceType("RelatedPerson")
            .id(relatedPersonId)
            .identifier(List.of(Identifier.builder()
                .use("official")
                .system("https://fhir.kemkes.go.id/id/nik")
                .value(nik)
                .build()))
            .active(true)
            .patient(patient)
            .relationship(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code(relationshipCode)
                    .display(relationshipDisplay)
                    .build()))
                .build()))
            .name(List.of(HumanName.builder()
                .use("official")
                .text(name)
                .build()))
            .telecom(List.of(ContactPoint.builder()
                .system("phone")
                .value(phoneNumber)
                .use("mobile")
                .build()))
            .gender(gender)
            .build();
    }

    /**
     * Factory method to create a caregiver.
     */
    public static RelatedPerson createCaregiver(
        String relatedPersonId,
        String nik,
        Reference patient,
        String name,
        String phoneNumber,
        String gender,
        String startDate
    ) {
        return RelatedPerson.builder()
            .resourceType("RelatedPerson")
            .id(relatedPersonId)
            .identifier(List.of(Identifier.builder()
                .use("official")
                .system("https://fhir.kemkes.go.id/id/nik")
                .value(nik)
                .build()))
            .active(true)
            .patient(patient)
            .relationship(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("CAREGIVER")
                    .display("Caregiver")
                    .build()))
                .build()))
            .name(List.of(HumanName.builder()
                .use("official")
                .text(name)
                .build()))
            .telecom(List.of(ContactPoint.builder()
                .system("phone")
                .value(phoneNumber)
                .use("mobile")
                .build()))
            .gender(gender)
            .period(Practitioner.Period.builder()
                .start(startDate)
                .build())
            .build();
    }

    /**
     * Helper to create a spouse relationship.
     */
    public static RelatedPerson createSpouse(
        String relatedPersonId,
        String nik,
        Reference patient,
        String name,
        String phoneNumber,
        String gender
    ) {
        return createEmergencyContact(
            relatedPersonId,
            nik,
            patient,
            "SPS",
            "Spouse",
            name,
            phoneNumber,
            gender
        );
    }

    /**
     * Helper to create a child relationship.
     */
    public static RelatedPerson createChild(
        String relatedPersonId,
        String nik,
        Reference patient,
        String name,
        String phoneNumber,
        String gender,
        String birthDate
    ) {
        RelatedPerson child = createEmergencyContact(
            relatedPersonId,
            nik,
            patient,
            "CHILD",
            "Child",
            name,
            phoneNumber,
            gender
        );
        child.setBirthDate(birthDate);
        return child;
    }
}
