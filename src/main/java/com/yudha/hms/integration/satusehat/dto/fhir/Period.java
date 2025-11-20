package com.yudha.hms.integration.satusehat.dto.fhir;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FHIR Period Data Type.
 *
 * A time period defined by a start and end date/time.
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
public class Period {

    @JsonProperty("start")
    private String start; // YYYY-MM-DD or YYYY-MM-DDThh:mm:ss+zz:zz

    @JsonProperty("end")
    private String end; // YYYY-MM-DD or YYYY-MM-DDThh:mm:ss+zz:zz
}
