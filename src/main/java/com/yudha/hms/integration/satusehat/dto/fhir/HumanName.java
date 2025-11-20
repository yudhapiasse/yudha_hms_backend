package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FHIR HumanName Data Type.
 *
 * A name of a human with text, parts and usage information.
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
public class HumanName {

    @JsonProperty("use")
    private String use; // usual | official | temp | nickname | anonymous | old | maiden

    @JsonProperty("text")
    private String text; // Full name as displayed

    @JsonProperty("family")
    private String family; // Family name (surname)

    @JsonProperty("given")
    private List<String> given; // Given names (first name, middle name)

    @JsonProperty("prefix")
    private List<String> prefix; // Parts that come before the name (Dr., Prof., etc.)

    @JsonProperty("suffix")
    private List<String> suffix; // Parts that come after the name (Jr., III, etc.)

    @JsonProperty("period")
    private Period period;

    /**
     * Create official name
     */
    public static HumanName createOfficial(String fullName, String familyName, List<String> givenNames) {
        return HumanName.builder()
            .use("official")
            .text(fullName != null ? fullName.toUpperCase() : null)
            .family(familyName != null ? familyName.toUpperCase() : null)
            .given(givenNames != null ? givenNames.stream()
                .map(String::toUpperCase)
                .toList() : null)
            .build();
    }
}
