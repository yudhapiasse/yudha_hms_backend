package com.yudha.hms.integration.bpjs.dto.antreanrs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Antrean RS Update Queue Time Request.
 *
 * Request to update queue timestamps for different task IDs.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQueueRequest {

    /**
     * Booking code from add queue response.
     */
    @JsonProperty("kodebooking")
    private String kodebooking;

    /**
     * Task ID (1-7, 99).
     * @see TaskId
     */
    @JsonProperty("taskid")
    private Integer taskid;

    /**
     * Timestamp in milliseconds.
     */
    @JsonProperty("waktu")
    private Long waktu;

    /**
     * Prescription type (for task 5 with pharmacy):
     * "Tidak ada", "Racikan", "Non racikan".
     * Optional - only for hospitals with pharmacy queue.
     */
    @JsonProperty("jenisresep")
    private String jenisresep;
}
