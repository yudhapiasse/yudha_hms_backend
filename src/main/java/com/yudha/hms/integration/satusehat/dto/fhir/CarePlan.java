package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR CarePlan Resource.
 *
 * Represents a care plan for managing patient conditions, coordinating care activities,
 * setting goals, and tracking progress. Used for chronic disease management, treatment
 * protocols, and care coordination.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/CarePlan
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
public class CarePlan {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "CarePlan";

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

    @JsonProperty("partOf")
    private List<Reference> partOf;

    @JsonProperty("status")
    private String status; // draft | active | on-hold | revoked | completed | entered-in-error | unknown

    @JsonProperty("intent")
    private String intent; // proposal | plan | order | option

    @JsonProperty("category")
    private List<CodeableConcept> category;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("period")
    private Practitioner.Period period;

    @JsonProperty("created")
    private String created;

    @JsonProperty("author")
    private Reference author; // Practitioner

    @JsonProperty("contributor")
    private List<Reference> contributor;

    @JsonProperty("careTeam")
    private List<Reference> careTeam;

    @JsonProperty("addresses")
    private List<Reference> addresses; // Conditions

    @JsonProperty("supportingInfo")
    private List<Reference> supportingInfo;

    @JsonProperty("goal")
    private List<Reference> goal;

    @JsonProperty("activity")
    private List<Activity> activity;

    @JsonProperty("note")
    private List<AllergyIntolerance.Annotation> note;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Activity {
        @JsonProperty("outcomeCodeableConcept")
        private List<CodeableConcept> outcomeCodeableConcept;

        @JsonProperty("outcomeReference")
        private List<Reference> outcomeReference;

        @JsonProperty("progress")
        private List<AllergyIntolerance.Annotation> progress;

        @JsonProperty("reference")
        private Reference reference;

        @JsonProperty("detail")
        private ActivityDetail detail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ActivityDetail {
        @JsonProperty("kind")
        private String kind; // Appointment | CommunicationRequest | DeviceRequest | MedicationRequest | NutritionOrder | Task | ServiceRequest | VisionPrescription

        @JsonProperty("instantiatesCanonical")
        private List<String> instantiatesCanonical;

        @JsonProperty("instantiatesUri")
        private List<String> instantiatesUri;

        @JsonProperty("code")
        private CodeableConcept code;

        @JsonProperty("reasonCode")
        private List<CodeableConcept> reasonCode;

        @JsonProperty("reasonReference")
        private List<Reference> reasonReference;

        @JsonProperty("goal")
        private List<Reference> goal;

        @JsonProperty("status")
        private String status; // not-started | scheduled | in-progress | on-hold | completed | cancelled | stopped | unknown | entered-in-error

        @JsonProperty("statusReason")
        private CodeableConcept statusReason;

        @JsonProperty("doNotPerform")
        private Boolean doNotPerform;

        @JsonProperty("scheduledTiming")
        private Timing scheduledTiming;

        @JsonProperty("scheduledPeriod")
        private Practitioner.Period scheduledPeriod;

        @JsonProperty("scheduledString")
        private String scheduledString;

        @JsonProperty("location")
        private Reference location;

        @JsonProperty("performer")
        private List<Reference> performer;

        @JsonProperty("productCodeableConcept")
        private CodeableConcept productCodeableConcept;

        @JsonProperty("productReference")
        private Reference productReference;

        @JsonProperty("dailyAmount")
        private Observation.Quantity dailyAmount;

        @JsonProperty("quantity")
        private Observation.Quantity quantity;

        @JsonProperty("description")
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Timing {
        @JsonProperty("event")
        private List<String> event;

        @JsonProperty("repeat")
        private TimingRepeat repeat;

        @JsonProperty("code")
        private CodeableConcept code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TimingRepeat {
        @JsonProperty("boundsDuration")
        private String boundsDuration;

        @JsonProperty("boundsRange")
        private String boundsRange;

        @JsonProperty("boundsPeriod")
        private Practitioner.Period boundsPeriod;

        @JsonProperty("count")
        private Integer count;

        @JsonProperty("countMax")
        private Integer countMax;

        @JsonProperty("duration")
        private Double duration;

        @JsonProperty("durationMax")
        private Double durationMax;

        @JsonProperty("durationUnit")
        private String durationUnit; // s | min | h | d | wk | mo | a

        @JsonProperty("frequency")
        private Integer frequency;

        @JsonProperty("frequencyMax")
        private Integer frequencyMax;

        @JsonProperty("period")
        private Double period;

        @JsonProperty("periodMax")
        private Double periodMax;

        @JsonProperty("periodUnit")
        private String periodUnit; // s | min | h | d | wk | mo | a

        @JsonProperty("dayOfWeek")
        private List<String> dayOfWeek;

        @JsonProperty("timeOfDay")
        private List<String> timeOfDay;

        @JsonProperty("when")
        private List<String> when;

        @JsonProperty("offset")
        private Integer offset;
    }

    /**
     * Factory method to create a diabetes management care plan.
     */
    public static CarePlan createDiabetesCarePlan(
        String carePlanId,
        Reference patient,
        Reference encounter,
        Reference author,
        String startDate,
        String endDate,
        List<Reference> conditions,
        List<Reference> goals,
        List<Activity> activities
    ) {
        return CarePlan.builder()
            .resourceType("CarePlan")
            .id(carePlanId)
            .identifier(List.of(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/careplan")
                .value(carePlanId)
                .build()))
            .status("active")
            .intent("plan")
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://snomed.info/sct")
                    .code("735321000")
                    .display("Diabetes mellitus care plan")
                    .build()))
                .build()))
            .title("Diabetes Management Plan")
            .description("Comprehensive diabetes management including medication, diet, and exercise")
            .subject(patient)
            .encounter(encounter)
            .period(Practitioner.Period.builder()
                .start(startDate)
                .end(endDate)
                .build())
            .created(startDate)
            .author(author)
            .addresses(conditions)
            .goal(goals)
            .activity(activities)
            .build();
    }

    /**
     * Helper to create a medication activity.
     */
    public static Activity createMedicationActivity(
        String medicationText,
        Integer frequency,
        Integer period,
        String periodUnit
    ) {
        return Activity.builder()
            .detail(ActivityDetail.builder()
                .kind("MedicationRequest")
                .code(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code("33633005")
                        .display("Prescription of drug")
                        .build()))
                    .text(medicationText)
                    .build())
                .status("in-progress")
                .scheduledTiming(Timing.builder()
                    .repeat(TimingRepeat.builder()
                        .frequency(frequency)
                        .period(period.doubleValue())
                        .periodUnit(periodUnit)
                        .build())
                    .build())
                .build())
            .build();
    }

    /**
     * Helper to create a monitoring activity.
     */
    public static Activity createMonitoringActivity(
        String monitoringText,
        String scheduledStart,
        String scheduledEnd
    ) {
        return Activity.builder()
            .detail(ActivityDetail.builder()
                .kind("ServiceRequest")
                .code(CodeableConcept.builder()
                    .text(monitoringText)
                    .build())
                .status("scheduled")
                .scheduledPeriod(Practitioner.Period.builder()
                    .start(scheduledStart)
                    .end(scheduledEnd)
                    .build())
                .build())
            .build();
    }

    /**
     * Helper to create a diet activity.
     */
    public static Activity createDietActivity(
        String dietDescription
    ) {
        return Activity.builder()
            .detail(ActivityDetail.builder()
                .kind("NutritionOrder")
                .code(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code("182922004")
                        .display("Dietary regime")
                        .build()))
                    .text(dietDescription)
                    .build())
                .status("in-progress")
                .build())
            .build();
    }

    /**
     * Helper to create an exercise activity.
     */
    public static Activity createExerciseActivity(
        String exerciseDescription,
        Integer frequency,
        String periodUnit
    ) {
        return Activity.builder()
            .detail(ActivityDetail.builder()
                .kind("ServiceRequest")
                .code(CodeableConcept.builder()
                    .coding(List.of(Coding.builder()
                        .system("http://snomed.info/sct")
                        .code("229065009")
                        .display("Exercise therapy")
                        .build()))
                    .text(exerciseDescription)
                    .build())
                .status("in-progress")
                .scheduledTiming(Timing.builder()
                    .repeat(TimingRepeat.builder()
                        .frequency(frequency)
                        .period(1.0)
                        .periodUnit(periodUnit)
                        .build())
                    .build())
                .build())
            .build();
    }
}
