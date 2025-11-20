package com.yudha.hms.integration.bpjs.dto.vclaim;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Rujukan (Referral) Request DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RujukanRequest {

    /**
     * Nomor rujukan or nomor kartu depending on search type.
     */
    private String identifier;

    /**
     * Tanggal rujukan (yyyy-MM-dd).
     */
    private String tanggal;

    /**
     * Jenis rujukan: 1=PCare, 2=RS.
     */
    private String jenis;
}
