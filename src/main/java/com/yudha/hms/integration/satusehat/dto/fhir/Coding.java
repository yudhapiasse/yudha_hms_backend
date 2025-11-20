package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FHIR Coding Data Type.
 *
 * A reference to a code defined by a terminology system.
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
public class Coding {

    @JsonProperty("system")
    private String system; // Identity of the terminology system

    @JsonProperty("version")
    private String version; // Version of the system

    @JsonProperty("code")
    private String code; // Symbol in syntax defined by the system

    @JsonProperty("display")
    private String display; // Representation defined by the system

    @JsonProperty("userSelected")
    private Boolean userSelected; // If this coding was chosen directly by the user
}
