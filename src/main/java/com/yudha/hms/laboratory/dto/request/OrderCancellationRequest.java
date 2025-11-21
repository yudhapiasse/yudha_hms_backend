package com.yudha.hms.laboratory.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Order Cancellation Request DTO.
 *
 * Used for cancelling lab orders.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancellationRequest {

    /**
     * Order ID
     */
    @NotNull(message = "ID order harus diisi")
    private UUID orderId;

    /**
     * Cancellation reason
     */
    @NotBlank(message = "Alasan pembatalan harus diisi")
    private String cancellationReason;

    /**
     * Cancelled by user ID
     */
    @NotNull(message = "ID user pembatal harus diisi")
    private UUID cancelledBy;

    /**
     * Additional notes
     */
    private String notes;
}
