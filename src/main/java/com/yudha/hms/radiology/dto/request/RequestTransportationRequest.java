package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request Transportation Request DTO.
 *
 * Used for requesting patient transportation for radiology examination.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransportationRequest {

    /**
     * Transportation notes
     */
    @Size(max = 1000, message = "Catatan transportasi maksimal 1000 karakter")
    private String notes;
}
