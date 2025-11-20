package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR Composition Resource.
 *
 * Represents clinical documents such as discharge summaries, progress notes,
 * consultation reports, and other structured clinical documentation.
 *
 * Conforms to: https://fhir.kemkes.go.id/r4/StructureDefinition/Composition
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
public class Composition {

    @JsonProperty("resourceType")
    @Builder.Default
    private String resourceType = "Composition";

    @JsonProperty("id")
    private String id;

    @JsonProperty("meta")
    private Encounter.Meta meta;

    @JsonProperty("identifier")
    private Identifier identifier;

    @JsonProperty("status")
    private String status; // preliminary | final | amended | entered-in-error

    @JsonProperty("type")
    private CodeableConcept type; // Document type (LOINC codes)

    @JsonProperty("category")
    private List<CodeableConcept> category;

    @JsonProperty("subject")
    private Reference subject; // Patient

    @JsonProperty("encounter")
    private Reference encounter;

    @JsonProperty("date")
    private String date;

    @JsonProperty("author")
    private List<Reference> author; // Practitioner(s)

    @JsonProperty("title")
    private String title;

    @JsonProperty("confidentiality")
    private String confidentiality; // U | L | M | N | R | V

    @JsonProperty("attester")
    private List<Attester> attester;

    @JsonProperty("custodian")
    private Reference custodian; // Organization

    @JsonProperty("relatesTo")
    private List<RelatesTo> relatesTo;

    @JsonProperty("event")
    private List<Event> event;

    @JsonProperty("section")
    private List<Section> section;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Attester {
        @JsonProperty("mode")
        private String mode; // personal | professional | legal | official

        @JsonProperty("time")
        private String time;

        @JsonProperty("party")
        private Reference party;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RelatesTo {
        @JsonProperty("code")
        private String code; // replaces | transforms | signs | appends

        @JsonProperty("targetIdentifier")
        private Identifier targetIdentifier;

        @JsonProperty("targetReference")
        private Reference targetReference;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Event {
        @JsonProperty("code")
        private List<CodeableConcept> code;

        @JsonProperty("period")
        private Practitioner.Period period;

        @JsonProperty("detail")
        private List<Reference> detail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Section {
        @JsonProperty("title")
        private String title;

        @JsonProperty("code")
        private CodeableConcept code;

        @JsonProperty("author")
        private List<Reference> author;

        @JsonProperty("focus")
        private Reference focus;

        @JsonProperty("text")
        private Narrative text;

        @JsonProperty("mode")
        private String mode; // working | snapshot | changes

        @JsonProperty("orderedBy")
        private CodeableConcept orderedBy;

        @JsonProperty("entry")
        private List<Reference> entry;

        @JsonProperty("emptyReason")
        private CodeableConcept emptyReason;

        @JsonProperty("section")
        private List<Section> section; // Nested sections
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Narrative {
        @JsonProperty("status")
        private String status; // generated | extensions | additional | empty

        @JsonProperty("div")
        private String div; // XHTML content
    }

    /**
     * Factory method to create a discharge summary composition.
     */
    public static Composition createDischargeSummary(
        String compositionId,
        Reference patient,
        Reference encounter,
        String date,
        Reference author,
        String title,
        List<Section> sections
    ) {
        return Composition.builder()
            .resourceType("Composition")
            .id(compositionId)
            .identifier(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/composition")
                .value(compositionId)
                .build())
            .status("final")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("18842-5")
                    .display("Discharge summary")
                    .build()))
                .build())
            .category(List.of(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("LP173421-1")
                    .display("Report")
                    .build()))
                .build()))
            .subject(patient)
            .encounter(encounter)
            .date(date)
            .author(List.of(author))
            .title(title)
            .section(sections)
            .build();
    }

    /**
     * Factory method to create a progress note composition.
     */
    public static Composition createProgressNote(
        String compositionId,
        Reference patient,
        Reference encounter,
        String date,
        Reference author,
        List<Section> sections
    ) {
        return Composition.builder()
            .resourceType("Composition")
            .id(compositionId)
            .identifier(Identifier.builder()
                .system("http://sys-ids.kemkes.go.id/composition")
                .value(compositionId)
                .build())
            .status("final")
            .type(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("11506-3")
                    .display("Progress note")
                    .build()))
                .build())
            .subject(patient)
            .encounter(encounter)
            .date(date)
            .author(List.of(author))
            .title("Progress Note")
            .section(sections)
            .build();
    }

    /**
     * Helper to create a chief complaint section.
     */
    public static Section createChiefComplaintSection(String complaint) {
        return Section.builder()
            .title("Keluhan Utama")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("10154-3")
                    .display("Chief complaint")
                    .build()))
                .build())
            .text(Narrative.builder()
                .status("generated")
                .div("<div xmlns=\"http://www.w3.org/1999/xhtml\">" + complaint + "</div>")
                .build())
            .build();
    }

    /**
     * Helper to create a history of present illness section.
     */
    public static Section createHistorySection(String history) {
        return Section.builder()
            .title("Riwayat Penyakit Sekarang")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("10164-2")
                    .display("History of Present illness")
                    .build()))
                .build())
            .text(Narrative.builder()
                .status("generated")
                .div("<div xmlns=\"http://www.w3.org/1999/xhtml\">" + history + "</div>")
                .build())
            .build();
    }

    /**
     * Helper to create a diagnosis section with condition references.
     */
    public static Section createDiagnosisSection(List<Reference> conditions) {
        return Section.builder()
            .title("Diagnosis")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("29308-4")
                    .display("Diagnosis")
                    .build()))
                .build())
            .entry(conditions)
            .build();
    }

    /**
     * Helper to create a procedure section with procedure references.
     */
    public static Section createProcedureSection(List<Reference> procedures) {
        return Section.builder()
            .title("Tindakan/Prosedur")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("29554-3")
                    .display("Procedure")
                    .build()))
                .build())
            .entry(procedures)
            .build();
    }

    /**
     * Helper to create a plan of care section.
     */
    public static Section createPlanOfCareSection(String plan) {
        return Section.builder()
            .title("Rencana Tindak Lanjut")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("18776-5")
                    .display("Plan of care")
                    .build()))
                .build())
            .text(Narrative.builder()
                .status("generated")
                .div("<div xmlns=\"http://www.w3.org/1999/xhtml\">" + plan + "</div>")
                .build())
            .build();
    }

    /**
     * Helper to create a medications section.
     */
    public static Section createMedicationsSection(List<Reference> medications) {
        return Section.builder()
            .title("Obat-obatan")
            .code(CodeableConcept.builder()
                .coding(List.of(Coding.builder()
                    .system("http://loinc.org")
                    .code("10160-0")
                    .display("History of Medication use")
                    .build()))
                .build())
            .entry(medications)
            .build();
    }
}
