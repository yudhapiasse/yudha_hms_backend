package com.yudha.hms.integration.bpjs.dto.aplicares;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Aplicares Delete Room Request.
 *
 * Request to delete a room from BPJS bed availability system.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteRoomRequest {

    /**
     * Room class code.
     */
    @JsonProperty("kodekelas")
    private String kodekelas;

    /**
     * Hospital room code.
     */
    @JsonProperty("koderuang")
    private String koderuang;
}
