package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Account Resource.
 *
 * Represents a financial account for tracking costs and charges related to
 * patient care. Used for managing billing accounts, deposits, and financial
 * coverage during encounters.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Account
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
public class Account {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Account";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("status")
    private String status; // active | inactive | entered-in-error | on-hold | unknown

    @JsonProperty("type")
    private CodeableConcept type;

    @JsonProperty("name")
    private String name;

    @JsonProperty("subject")
    private List<Reference> subject;

    @JsonProperty("servicePeriod")
    private Practitioner.Period servicePeriod;

    @JsonProperty("coverage")
    private List<AccountCoverage> coverage;

    @JsonProperty("owner")
    private Reference owner; // Organization

    @JsonProperty("description")
    private String description;

    @JsonProperty("guarantor")
    private List<Guarantor> guarantor;

    @JsonProperty("partOf")
    private Reference partOf;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AccountCoverage {
        @JsonProperty("coverage")
        private Reference coverage;

        @JsonProperty("priority")
        private Integer priority;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Guarantor {
        @JsonProperty("party")
        private Reference party;

        @JsonProperty("onHold")
        private Boolean onHold;

        @JsonProperty("period")
        private Practitioner.Period period;
    }

    /**
     * Factory method to create an inpatient billing account.
     */
    public static Account createInpatientAccount(
        String accountId,
        String accountName,
        Reference patient,
        String servicePeriodStart,
        String servicePeriodEnd,
        Reference coverage,
        Integer coveragePriority,
        Reference hospital,
        String description
    ) {
        return Account.builder()
            .resourceType("Account")
            .id(accountId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/account")
                .value(accountId)
                .build()))
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                    .code("PBILLACCT")
                    .display("Patient Billing Account")
                    .build()))
                .build())
            .name(accountName)
            .subject(List.of(patient))
            .servicePeriod(Practitioner.Period.builder()
                .start(servicePeriodStart)
                .end(servicePeriodEnd)
                .build())
            .coverage(List.of(AccountCoverage.builder()
                .coverage(coverage)
                .priority(coveragePriority)
                .build()))
            .owner(hospital)
            .description(description)
            .build();
    }

    /**
     * Factory method to create an outpatient billing account.
     */
    public static Account createOutpatientAccount(
        String accountId,
        String accountName,
        Reference patient,
        String serviceDate,
        Reference coverage,
        Reference hospital,
        String description
    ) {
        return Account.builder()
            .resourceType("Account")
            .id(accountId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/account")
                .value(accountId)
                .build()))
            .status("active")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://terminology.hl7.org/CodeSystem/v3-ActCode")
                    .code("PBILLACCT")
                    .display("Patient Billing Account")
                    .build()))
                .build())
            .name(accountName)
            .subject(List.of(patient))
            .servicePeriod(Practitioner.Period.builder()
                .start(serviceDate)
                .build())
            .coverage(List.of(AccountCoverage.builder()
                .coverage(coverage)
                .priority(1)
                .build()))
            .owner(hospital)
            .description(description)
            .build();
    }
}
