package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR EpisodeOfCare Resource.
 *
 * Represents an association between a patient and an organization/healthcare provider
 * during which time encounters may occur. Used for managing care episodes, chronic
 * disease management, and care coordination.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/EpisodeOfCare
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
public class EpisodeOfCare {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "EpisodeOfCare";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // planned | waitlist | active | onhold | finished | cancelled | entered-in-error

    @JsonProperty("statusHistory")
    private List<StatusHistory> statusHistory;

    @JsonProperty("type")
    private List<CodeableConcept> type;

    @JsonProperty("diagnosis")
    private List<Diagnosis> diagnosis;

    @JsonProperty("patient")
    private Reference patient;

    @JsonProperty("managingOrganization")
    private Reference managingOrganization;

    @JsonProperty("period")
    private Practitioner.Period period;

    @JsonProperty("referralRequest")
    private List<Reference> referralRequest;

    @JsonProperty("careManager")
    private Reference careManager; // Practitioner

    @JsonProperty("team")
    private List<Reference> team; // CareTeam

    @JsonProperty("account")
    private List<Reference> account;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StatusHistory {
        @JsonProperty("status")
        private String status;

        @JsonProperty("period")
        private Practitioner.Period period;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Diagnosis {
        @JsonProperty("condition")
        private Reference condition;

        @JsonProperty("role")
        private CodeableConcept role;

        @JsonProperty("rank")
        private Integer rank;
    }

    /**
     * Factory method to create an episode of care for chronic disease management.
     */
    public static EpisodeOfCare createChronicCareEpisode(
        String episodeId,
        Reference patient,
        Reference managingOrganization,
        Reference careManager,
        String startDate,
        String typeCode,
        String typeDisplay,
        List<Diagnosis> diagnoses
    ) {
        return EpisodeOfCare.builder()
            .resourceType("EpisodeOfCare")
            .id(episodeId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/episode")
                .value(episodeId)
                .build()))
            .status("active")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/episodeofcare-type")
                    .code(typeCode)
                    .display(typeDisplay)
                    .build()))
                .build()))
            .patient(patient)
            .managingOrganization(managingOrganization)
            .period(Practitioner.Period.builder()
                .start(startDate)
                .build())
            .careManager(careManager)
            .diagnosis(diagnoses)
            .build();
    }

    /**
     * Factory method to create a home and community care episode.
     */
    public static EpisodeOfCare createHomeCareEpisode(
        String episodeId,
        Reference patient,
        Reference managingOrganization,
        Reference careManager,
        String startDate,
        Reference chiefComplaintCondition
    ) {
        return EpisodeOfCare.builder()
            .resourceType("EpisodeOfCare")
            .id(episodeId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/episode")
                .value(episodeId)
                .build()))
            .status("active")
            .type(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/episodeofcare-type")
                    .code("hacc")
                    .display("Home and Community Care")
                    .build()))
                .build()))
            .patient(patient)
            .managingOrganization(managingOrganization)
            .period(Practitioner.Period.builder()
                .start(startDate)
                .build())
            .careManager(careManager)
            .diagnosis(List.of(Diagnosis.builder()
                .condition(chiefComplaintCondition)
                .role(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://terminology.hl7.org/CodeSystem/diagnosis-role")
                        .code("CC")
                        .display("Chief complaint")
                        .build()))
                    .build())
                .rank(1)
                .build()))
            .build();
    }

    /**
     * Helper to create a diagnosis entry.
     */
    public static Diagnosis createDiagnosis(
        Reference condition,
        String roleCode,
        String roleDisplay,
        Integer rank
    ) {
        return Diagnosis.builder()
            .condition(condition)
            .role(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/diagnosis-role")
                    .code(roleCode)
                    .display(roleDisplay)
                    .build()))
                .build())
            .rank(rank)
            .build();
    }
}
