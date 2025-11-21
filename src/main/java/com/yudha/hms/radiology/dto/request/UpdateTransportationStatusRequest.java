package com.yudha.hms.radiology.dto.request;

import com.yudha.hms.radiology.constant.TransportationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update Transportation Status Request DTO.
 *
 * Used for updating patient transportation status.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransportationStatusRequest {

    /**
     * Transportation status
     */
    @NotNull(message = "Status transportasi harus diisi")
    private TransportationStatus status;
}
