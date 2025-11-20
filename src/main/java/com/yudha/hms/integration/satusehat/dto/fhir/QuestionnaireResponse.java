package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR QuestionnaireResponse Resource.
 *
 * Represents a structured set of questions and answers, used for capturing
 * patient-reported outcomes, assessments, screening forms, and survey responses.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/QuestionnaireResponse
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
public class QuestionnaireResponse {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "QuestionnaireResponse";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private Identifier identifier;

    @JsonProperty("basedOn")
    private List<Reference> basedOn;

    @JsonProperty("partOf")
    private List<Reference> partOf;

    @JsonProperty("questionnaire")
    private String questionnaire; // Canonical URL

    @JsonProperty("status")
    private String status; // in-progress | completed | amended | entered-in-error | stopped

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("authored")
    private String authored;

    @JsonProperty("author")
    private Reference author; // Practitioner, Patient, RelatedPerson

    @JsonProperty("source")
    private Reference source;

    @JsonProperty("item")
    private List<Item> item;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Item {
        @JsonProperty("linkId")
        private String linkId;

        @JsonProperty("definition")
        private String definition;

        @JsonProperty("text")
        private String text;

        @JsonProperty("answer")
        private List<Answer> answer;

        @JsonProperty("item")
        private List<Item> item; // Nested items
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Answer {
        @JsonProperty("valueBoolean")
        private Boolean valueBoolean;

        @JsonProperty("valueDecimal")
        private Double valueDecimal;

        @JsonProperty("valueInteger")
        private Integer valueInteger;

        @JsonProperty("valueDate")
        private String valueDate;

        @JsonProperty("valueDateTime")
        private String valueDateTime;

        @JsonProperty("valueTime")
        private String valueTime;

        @JsonProperty("valueString")
        private String valueString;

        @JsonProperty("valueUri")
        private String valueUri;

        @JsonProperty("valueAttachment")
        private String valueAttachment;

        @JsonProperty("valueCoding")
        private Coding valueCoding;

        @JsonProperty("valueQuantity")
        private Observation.Quantity valueQuantity;

        @JsonProperty("valueReference")
        private Reference valueReference;

        @JsonProperty("item")
        private List<Item> item; // Nested items for answer groups
    }

    /**
     * Factory method to create a pain assessment questionnaire response.
     */
    public static QuestionnaireResponse createPainAssessment(
        String responseId,
        Reference patient,
        Reference encounter,
        Reference author,
        String authored,
        Integer painIntensity,
        String painLocation,
        String painDuration
    ) {
        return QuestionnaireResponse.builder()
            .resourceType("QuestionnaireResponse")
            .id(responseId)
            .identifier(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/questionnaire-response")
                .value(responseId)
                .build())
            .questionnaire("http://fhir.kemkes.go.id/Questionnaire/pain-assessment")
            .status("completed")
            .subject(patient)
            .encounter(encounter)
            .authored(authored)
            .author(author)
            .item(List.of(
                Item.builder()
                    .linkId("1")
                    .text("Pain intensity scale (0-10)")
                    .answer(List.of(Answer.builder()
                        .valueInteger(painIntensity)
                        .build()))
                    .build(),
                Item.builder()
                    .linkId("2")
                    .text("Pain location")
                    .answer(List.of(Answer.builder()
                        .valueString(painLocation)
                        .build()))
                    .build(),
                Item.builder()
                    .linkId("3")
                    .text("Pain duration")
                    .answer(List.of(Answer.builder()
                        .valueString(painDuration)
                        .build()))
                    .build()
            ))
            .build();
    }

    /**
     * Factory method to create a patient satisfaction survey response.
     */
    public static QuestionnaireResponse createSatisfactionSurvey(
        String responseId,
        Reference patient,
        Reference encounter,
        String authored,
        Integer overallSatisfaction,
        String comments
    ) {
        return QuestionnaireResponse.builder()
            .resourceType("QuestionnaireResponse")
            .id(responseId)
            .identifier(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/questionnaire-response")
                .value(responseId)
                .build())
            .questionnaire("http://fhir.kemkes.go.id/Questionnaire/patient-satisfaction")
            .status("completed")
            .subject(patient)
            .encounter(encounter)
            .authored(authored)
            .author(patient)
            .item(List.of(
                Item.builder()
                    .linkId("1")
                    .text("Overall satisfaction (1-5)")
                    .answer(List.of(Answer.builder()
                        .valueInteger(overallSatisfaction)
                        .build()))
                    .build(),
                Item.builder()
                    .linkId("2")
                    .text("Additional comments")
                    .answer(List.of(Answer.builder()
                        .valueString(comments)
                        .build()))
                    .build()
            ))
            .build();
    }

    /**
     * Helper to create an item with integer answer.
     */
    public static Item createIntegerItem(
        String linkId,
        String text,
        Integer value
    ) {
        return Item.builder()
            .linkId(linkId)
            .text(text)
            .answer(List.of(Answer.builder()
                .valueInteger(value)
                .build()))
            .build();
    }

    /**
     * Helper to create an item with string answer.
     */
    public static Item createStringItem(
        String linkId,
        String text,
        String value
    ) {
        return Item.builder()
            .linkId(linkId)
            .text(text)
            .answer(List.of(Answer.builder()
                .valueString(value)
                .build()))
            .build();
    }

    /**
     * Helper to create an item with boolean answer.
     */
    public static Item createBooleanItem(
        String linkId,
        String text,
        Boolean value
    ) {
        return Item.builder()
            .linkId(linkId)
            .text(text)
            .answer(List.of(Answer.builder()
                .valueBoolean(value)
                .build()))
            .build();
    }
}
