package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR PractitionerRole Resource.
 *
 * A specific set of Roles/Locations/specialties/services that a practitioner may perform
 * at an organization for a period of time.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/PractitionerRole
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
public class PractitionerRole {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "PractitionerRole";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("period")
    private Practitioner.Period period; // Period during which this role is valid

    @JsonProperty("practitioner")
    private Reference practitioner; // Practitioner reference (required)

    @JsonProperty("organization")
    private Reference organization; // Organization where role is performed

    @JsonProperty("code")
    private List<CodeableConcept> code; // Roles (e.g., doctor, nurse)

    @JsonProperty("specialty")
    private List<CodeableConcept> specialty; // Specific specialty (e.g., cardiology, pediatrics)

    @JsonProperty("location")
    private List<Reference> location; // Locations where role is performed

    @JsonProperty("healthcareService")
    private List<Reference> healthcareService;

    @JsonProperty("telecom")
    private List<Organization.ContactPoint> telecom;

    @JsonProperty("availableTime")
    private List<AvailableTime> availableTime;

    @JsonProperty("notAvailable")
    private List<NotAvailable> notAvailable;

    @JsonProperty("availabilityExceptions")
    private String availabilityExceptions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AvailableTime {
        @JsonProperty("daysOfWeek")
        private List<String> daysOfWeek; // mon | tue | wed | thu | fri | sat | sun

        @JsonProperty("allDay")
        private Boolean allDay;

        @JsonProperty("availableStartTime")
        private String availableStartTime; // HH:MM:SS

        @JsonProperty("availableEndTime")
        private String availableEndTime; // HH:MM:SS
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NotAvailable {
        @JsonProperty("description")
        private String description;

        @JsonProperty("during")
        private Practitioner.Period during;
    }

    /**
     * Factory method to create a basic practitioner role.
     */
    public static PractitionerRole createBasicRole(
        String roleId,
        Reference practitioner,
        Reference organization,
        String roleCode,
        String roleDisplay
    ) {
        return PractitionerRole.builder()
            .resourceType("PractitionerRole")
            .id(roleId)
            .active(true)
            .practitioner(practitioner)
            .organization(organization)
            .code(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/practitioner-role")
                    .code(roleCode)
                    .display(roleDisplay)
                    .build()))
                .build()))
            .build();
    }

    /**
     * Factory method to create a doctor role with specialty.
     */
    public static PractitionerRole createDoctorRole(
        String roleId,
        Reference practitioner,
        Reference organization,
        String specialtyCode,
        String specialtyDisplay,
        List<Reference> locations
    ) {
        return PractitionerRole.builder()
            .resourceType("PractitionerRole")
            .id(roleId)
            .active(true)
            .practitioner(practitioner)
            .organization(organization)
            .code(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/practitioner-role")
                    .code("doctor")
                    .display("Doctor")
                    .build()))
                .build()))
            .specialty(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code(specialtyCode)
                    .display(specialtyDisplay)
                    .build()))
                .build()))
            .location(locations)
            .build();
    }

    /**
     * Factory method to create a nurse role.
     */
    public static PractitionerRole createNurseRole(
        String roleId,
        Reference practitioner,
        Reference organization,
        List<Reference> locations
    ) {
        return PractitionerRole.builder()
            .resourceType("PractitionerRole")
            .id(roleId)
            .active(true)
            .practitioner(practitioner)
            .organization(organization)
            .code(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/practitioner-role")
                    .code("nurse")
                    .display("Nurse")
                    .build()))
                .build()))
            .location(locations)
            .build();
    }

    /**
     * Factory method to create a role with availability schedule.
     */
    public static PractitionerRole createRoleWithSchedule(
        String roleId,
        Reference practitioner,
        Reference organization,
        String roleCode,
        String roleDisplay,
        List<AvailableTime> schedule
    ) {
        return PractitionerRole.builder()
            .resourceType("PractitionerRole")
            .id(roleId)
            .active(true)
            .practitioner(practitioner)
            .organization(organization)
            .code(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/practitioner-role")
                    .code(roleCode)
                    .display(roleDisplay)
                    .build()))
                .build()))
            .availableTime(schedule)
            .build();
    }

    /**
     * Helper method to create weekday availability (Monday-Friday, 8am-5pm).
     */
    public static AvailableTime createWeekdayAvailability(String startTime, String endTime) {
        return AvailableTime.builder()
            .daysOfWeek(List.of("mon", "tue", "wed", "thu", "fri"))
            .availableStartTime(startTime)
            .availableEndTime(endTime)
            .build();
    }

    /**
     * Helper method to create weekend availability.
     */
    public static AvailableTime createWeekendAvailability(String startTime, String endTime) {
        return AvailableTime.builder()
            .daysOfWeek(List.of("sat", "sun"))
            .availableStartTime(startTime)
            .availableEndTime(endTime)
            .build();
    }
}
