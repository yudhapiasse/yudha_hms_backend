package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Location Resource.
 *
 * Represents a physical location where healthcare services are provided.
 * Supports hierarchical structure: Hospital → Building → Floor → Department → Room → Bed
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Location
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
public class Location {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Location";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | suspended | inactive

    @JsonProperty("name")
    private String name;

    @JsonProperty("alias")
    private List<String> alias;

    @JsonProperty("description")
    private String description;

    @JsonProperty("mode")
    private String mode; // instance | kind

    @JsonProperty("type")
    private List<CodeableConcept> type; // Service delivery location type

    @JsonProperty("telecom")
    private List<Organization.ContactPoint> telecom;

    @JsonProperty("address")
    private Organization.Address address;

    @JsonProperty("physicalType")
    private CodeableConcept physicalType; // building | room | bed | etc.

    @JsonProperty("position")
    private Position position; // Geographic coordinates

    @JsonProperty("managingOrganization")
    private Reference managingOrganization;

    @JsonProperty("partOf")
    private Reference partOf; // Parent location

    @JsonProperty("operationalStatus")
    private Coding operationalStatus; // Bed availability status

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Position {
        @JsonProperty("longitude")
        private Double longitude;

        @JsonProperty("latitude")
        private Double latitude;

        @JsonProperty("altitude")
        private Double altitude;
    }

    /**
     * Factory method to create a polyclinic/outpatient facility.
     */
    public static Location createPolyclinic(
        String locationId,
        String name,
        Reference organization,
        Reference parentLocation
    ) {
        return Location.builder()
            .resourceType("Location")
            .id(locationId)
            .status("active")
            .name(name)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("OF")
                    .display("Outpatient Facility")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("ro")
                    .display("Room")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(parentLocation)
            .build();
    }

    /**
     * Factory method to create an inpatient ward.
     */
    public static Location createWard(
        String locationId,
        String wardName,
        Reference organization,
        Reference parentLocation
    ) {
        return Location.builder()
            .resourceType("Location")
            .id(locationId)
            .status("active")
            .name(wardName)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("HU")
                    .display("Hospital Unit")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("wa")
                    .display("Ward")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(parentLocation)
            .build();
    }

    /**
     * Factory method to create a bed.
     */
    public static Location createBed(
        String bedId,
        String bedNumber,
        Reference organization,
        Reference wardLocation,
        String bedStatus
    ) {
        Coding operationalStatus = null;
        if (bedStatus != null) {
            operationalStatus = Coding.builder()
                .system("http://terminology.hl7.org/CodeSystem/v2-0116")
                .code(bedStatus) // O (Occupied), U (Unoccupied), K (Contaminated), I (Isolated)
                .build();
        }

        return Location.builder()
            .resourceType("Location")
            .id(bedId)
            .status("active")
            .name(bedNumber)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("BD")
                    .display("Bed")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("bd")
                    .display("Bed")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(wardLocation)
            .operationalStatus(operationalStatus)
            .build();
    }

    /**
     * Factory method to create an emergency department.
     */
    public static Location createEmergencyDepartment(
        String locationId,
        String name,
        Reference organization,
        Reference parentLocation
    ) {
        return Location.builder()
            .resourceType("Location")
            .id(locationId)
            .status("active")
            .name(name)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("ER")
                    .display("Emergency Room")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("ro")
                    .display("Room")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(parentLocation)
            .build();
    }

    /**
     * Factory method to create an operating theater.
     */
    public static Location createOperatingTheater(
        String locationId,
        String name,
        Reference organization,
        Reference parentLocation
    ) {
        return Location.builder()
            .resourceType("Location")
            .id(locationId)
            .status("active")
            .name(name)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("OR")
                    .display("Operating Room")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("ro")
                    .display("Room")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(parentLocation)
            .build();
    }

    /**
     * Factory method to create a laboratory.
     */
    public static Location createLaboratory(
        String locationId,
        String name,
        Reference organization,
        Reference parentLocation
    ) {
        return Location.builder()
            .resourceType("Location")
            .id(locationId)
            .status("active")
            .name(name)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("HOSP")
                    .display("Hospital")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("ro")
                    .display("Room")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(parentLocation)
            .build();
    }

    /**
     * Factory method to create a pharmacy.
     */
    public static Location createPharmacy(
        String locationId,
        String name,
        Reference organization,
        Reference parentLocation
    ) {
        return Location.builder()
            .resourceType("Location")
            .id(locationId)
            .status("active")
            .name(name)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("PHARM")
                    .display("Pharmacy")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("ro")
                    .display("Room")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(parentLocation)
            .build();
    }

    /**
     * Factory method to create a radiology department.
     */
    public static Location createRadiology(
        String locationId,
        String name,
        Reference organization,
        Reference parentLocation
    ) {
        return Location.builder()
            .resourceType("Location")
            .id(locationId)
            .status("active")
            .name(name)
            .mode("instance")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-RoleCode")
                    .code("RNEU")
                    .display("Neuroradiology Unit")
                    .build()))
                .build()))
            .physicalType(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/location-physical-type")
                    .code("ro")
                    .display("Room")
                    .build()))
                .build())
            .managingOrganization(organization)
            .partOf(parentLocation)
            .build();
    }
}
