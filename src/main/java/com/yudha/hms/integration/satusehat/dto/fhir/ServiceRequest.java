package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR ServiceRequest Resource.
 *
 * Represents requests for diagnostic procedures, laboratory tests, imaging studies,
 * treatments, and referrals.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/ServiceRequest
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
public class ServiceRequest {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "ServiceRequest";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private List<Identifier> identifier;

    @JsonProperty("instantiatesCanonical")
    private List<String> instantiatesCanonical;

    @JsonProperty("instantiatesUri")
    private List<String> instantiatesUri;

    @JsonProperty("basedOn")
    private List<Reference> basedOn;

    @JsonProperty("replaces")
    private List<Reference> replaces;

    @JsonProperty("requisition")
    private Identifier requisition;

    @JsonProperty("status")
    private String status; // draft | active | on-hold | revoked | completed | entered-in-error | unknown

    @JsonProperty("intent")
    private String intent; // proposal | plan | directive | order | original-order | reflex-order | filler-order | instance-order | option

    @JsonProperty("category")
    private List<CodeableConcept> category;

    @JsonProperty("priority")
    private String priority; // routine | urgent | asap | stat

    @JsonProperty("doNotPerform")
    private Boolean doNotPerform;

    @JsonProperty("code")
    private CodeableConcept code;

    @JsonProperty("orderDetail")
    private List<CodeableConcept> orderDetail;

    @JsonProperty("quantityQuantity")
    private Observation.Quantity quantityQuantity;

    @JsonProperty("quantityRatio")
    private String quantityRatio;

    @JsonProperty("quantityRange")
    private String quantityRange;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("occurrenceDateTime")
    private String occurrenceDateTime;

    @JsonProperty("occurrencePeriod")
    private Practitioner.Period occurrencePeriod;

    @JsonProperty("occurrenceTiming")
    private String occurrenceTiming;

    @JsonProperty("asNeededBoolean")
    private Boolean asNeededBoolean;

    @JsonProperty("asNeededCodeableConcept")
    private CodeableConcept asNeededCodeableConcept;

    @JsonProperty("authoredOn")
    private String authoredOn;

    @JsonProperty("requester")
    private Reference requester; // Practitioner

    @JsonProperty("performerType")
    private CodeableConcept performerType;

    @JsonProperty("performer")
    private List<Reference> performer; // Practitioner or Organization

    @JsonProperty("locationCode")
    private List<CodeableConcept> locationCode;

    @JsonProperty("locationReference")
    private List<Reference> locationReference;

    @JsonProperty("reasonCode")
    private List<CodeableConcept> reasonCode;

    @JsonProperty("reasonReference")
    private List<Reference> reasonReference; // Condition, Observation

    @JsonProperty("insurance")
    private List<Reference> insurance;

    @JsonProperty("supportingInfo")
    private List<Reference> supportingInfo;

    @JsonProperty("specimen")
    private List<Reference> specimen;

    @JsonProperty("bodySite")
    private List<CodeableConcept> bodySite;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @JsonProperty("patientInstruction")
    private String patientInstruction;

    @JsonProperty("relevantHistory")
    private List<Reference> relevantHistory;

    /**
     * Factory method to create a laboratory service request.
     */
    public static ServiceRequest createLabRequest(
        String requestId,
        Reference patient,
        Reference encounter,
        String labTestCode,
        String labTestDisplay,
        String occurrenceDateTime,
        String authoredOn,
        Reference requester,
        Reference performer,
        String priority,
        List<Reference> specimens
    ) {
        return ServiceRequest.builder()
            .resourceType("ServiceRequest")
            .id(requestId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/servicerequest")
                .value(requestId)
                .build()))
            .status("active")
            .intent("order")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("108252007")
                    .display("Laboratory procedure")
                    .build()))
                .build()))
            .priority(priority != null ? priority : "routine")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code(labTestCode)
                    .display(labTestDisplay)
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .occurrenceDateTime(occurrenceDateTime)
            .authoredOn(authoredOn)
            .requester(requester)
            .performer(performer != null ? List.of(performer) : null)
            .specimen(specimens)
            .build();
    }

    /**
     * Factory method to create a radiology/imaging service request.
     */
    public static ServiceRequest createRadiologyRequest(
        String requestId,
        Reference patient,
        Reference encounter,
        String imagingCode,
        String imagingDisplay,
        String modalityCode,
        String occurrenceDateTime,
        String authoredOn,
        Reference requester,
        Reference performer,
        String priority,
        List<CodeableConcept> reasonCodes
    ) {
        return ServiceRequest.builder()
            .resourceType("ServiceRequest")
            .id(requestId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/servicerequest")
                .value(requestId)
                .build()))
            .status("active")
            .intent("order")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("363679005")
                    .display("Imaging")
                    .build()))
                .build()))
            .priority(priority != null ? priority : "routine")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code(imagingCode)
                    .display(imagingDisplay)
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .occurrenceDateTime(occurrenceDateTime)
            .authoredOn(authoredOn)
            .requester(requester)
            .performer(performer != null ? List.of(performer) : null)
            .reasonCode(reasonCodes)
            .build();
    }

    /**
     * Factory method to create a referral service request.
     */
    public static ServiceRequest createReferralRequest(
        String requestId,
        Reference patient,
        Reference encounter,
        String referralReason,
        String referralReasonCode,
        Reference requester,
        Reference performer,
        String priority,
        String notes
    ) {
        return ServiceRequest.builder()
            .resourceType("ServiceRequest")
            .id(requestId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/servicerequest")
                .value(requestId)
                .build()))
            .status("active")
            .intent("order")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("3457005")
                    .display("Patient referral")
                    .build()))
                .build()))
            .priority(priority != null ? priority : "routine")
            .code(CodeableConcept.builder()
                .text(referralReason)
                .build())
            .subject(patient)
            .encounter(encounter)
            .requester(requester)
            .performer(performer != null ? List.of(performer) : null)
            .reasonCode(referralReasonCode != null ? List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://hl7.org/fhir/sid/icd-10")
                    .code(referralReasonCode)
                    .display(referralReason)
                    .build()))
                .build()) : null)
            .note(notes != null ? List.of(AllergyIntolerance.Annotation.builder()
                .text(notes)
                .build()) : null)
            .build();
    }

    /**
     * Helper to create a lipid panel request.
     */
    public static ServiceRequest createLipidPanelRequest(
        String requestId,
        Reference patient,
        Reference encounter,
        String occurrenceDateTime,
        Reference requester,
        Reference labOrganization
    ) {
        return createLabRequest(
            requestId,
            patient,
            encounter,
            "24331-1",
            "Lipid panel",
            occurrenceDateTime,
            occurrenceDateTime,
            requester,
            labOrganization,
            "routine",
            null
        );
    }

    /**
     * Helper to create a chest X-ray request.
     */
    public static ServiceRequest createChestXRayRequest(
        String requestId,
        Reference patient,
        Reference encounter,
        String occurrenceDateTime,
        Reference requester,
        Reference radiologyDept,
        String reasonCode,
        String reasonDisplay
    ) {
        return createRadiologyRequest(
            requestId,
            patient,
            encounter,
            "36643-5",
            "Chest X-ray",
            "DX",
            occurrenceDateTime,
            occurrenceDateTime,
            requester,
            radiologyDept,
            "routine",
            reasonCode != null ? List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code(reasonCode)
                    .display(reasonDisplay)
                    .build()))
                .build()) : null
        );
    }

    /**
     * Helper to create a CT scan request.
     */
    public static ServiceRequest createCTScanRequest(
        String requestId,
        Reference patient,
        Reference encounter,
        String bodyPartCode,
        String bodyPartDisplay,
        String occurrenceDateTime,
        Reference requester,
        Reference radiologyDept,
        String priority
    ) {
        return createRadiologyRequest(
            requestId,
            patient,
            encounter,
            "30746-2",
            "CT " + bodyPartDisplay,
            "CT",
            occurrenceDateTime,
            occurrenceDateTime,
            requester,
            radiologyDept,
            priority,
            null
        );
    }
}
