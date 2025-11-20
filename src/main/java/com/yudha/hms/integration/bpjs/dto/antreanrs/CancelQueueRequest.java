package com.yudha.hms.integration.bpjs.dto.antreanrs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BPJS Antrean RS Cancel Queue Request.
 *
 * Request to cancel a patient queue.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelQueueRequest {

    /**
     * Booking code to cancel.
     */
    @JsonProperty("kodebooking")
    private String kodebooking;

    /**
     * Cancellation reason.
     */
    @JsonProperty("keterangan")
    private String keterangan;
}
