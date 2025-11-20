package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Organization Resource.
 *
 * Represents a healthcare organization (hospital, clinic, etc.).
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Organization
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
public class Organization {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Organization";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("type")
    private List<CodeableConcept> type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("alias")
    private List<String> alias;

    @JsonProperty("telecom")
    private List<ContactPoint> telecom;

    @JsonProperty("address")
    private List<Address> address;

    @JsonProperty("partOf")
    private Reference partOf; // Parent organization

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContactPoint {
        @JsonProperty("system")
        private String system; // phone | fax | email | url

        @JsonProperty("value")
        private String value;

        @JsonProperty("use")
        private String use; // home | work | temp | old | mobile
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Address {
        @JsonProperty("use")
        private String use; // home | work | temp | old | billing

        @JsonProperty("type")
        private String type; // postal | physical | both

        @JsonProperty("line")
        private List<String> line;

        @JsonProperty("city")
        private String city;

        @JsonProperty("district")
        private String district;

        @JsonProperty("state")
        private String state;

        @JsonProperty("postalCode")
        private String postalCode;

        @JsonProperty("country")
        private String country;

        @JsonProperty("extension")
        private List<Extension> extension;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Extension {
            @JsonProperty("url")
            private String url;

            @JsonProperty("extension")
            private List<SubExtension> extension;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class SubExtension {
                @JsonProperty("url")
                private String url;

                @JsonProperty("valueCode")
                private String valueCode;
            }
        }
    }

    /**
     * Factory method to create a healthcare provider organization.
     */
    public static Organization createHealthcareProvider(
        String organizationId,
        String organizationName,
        String phone,
        String email
    ) {
        return Organization.builder()
            .resourceType("Organization")
            .id(organizationId)
            .active(true)
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/organization-type")
                    .code("prov")
                    .display("Healthcare Provider")
                    .build()))
                .build()))
            .name(organizationName)
            .telecom(List.of(
                ContactPoint.builder()
                    .system("phone")
                    .value(phone)
                    .use("work")
                    .build(),
                ContactPoint.builder()
                    .system("email")
                    .value(email)
                    .use("work")
                    .build()
            ))
            .build();
    }

    /**
     * Factory method to create organization with SATUSEHAT identifier.
     */
    public static Organization createWithIdentifier(
        String organizationId,
        String organizationName,
        String satusehatOrgId
    ) {
        return Organization.builder()
            .resourceType("Organization")
            .id(organizationId)
            .active(true)
            .identifier(List.of(Identifier.builder()
                .use("official")
                .system("http://sys-ids.kemkes.go.id/organization/" + satusehatOrgId)
                .value(satusehatOrgId)
                .build()))
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/organization-type")
                    .code("prov")
                    .display("Healthcare Provider")
                    .build()))
                .build()))
            .name(organizationName)
            .build();
    }
}
