package com.yudha.hms.integration.bpjs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Participant Eligibility Check Request.
 *
 * Used for checking patient eligibility in BPJS system.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpjsParticipantRequest {

    /**
     * BPJS card number (Nomor Kartu).
     */
    @JsonProperty("noKartu")
    private String noKartu;

    /**
     * Service date in YYYY-MM-DD format.
     */
    @JsonProperty("tglSEP")
    private String tglSEP;
}
