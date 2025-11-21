package com.yudha.hms.laboratory.dto.request;

import com.yudha.hms.laboratory.constant.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Order Status Update Request DTO.
 *
 * Used for updating lab order status.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequest {

    /**
     * Order ID
     */
    @NotNull(message = "ID order harus diisi")
    private UUID orderId;

    /**
     * New status
     */
    @NotNull(message = "Status baru harus dipilih")
    private OrderStatus newStatus;

    /**
     * Status reason/notes
     */
    private String statusReason;

    /**
     * Updated by user ID
     */
    private UUID updatedBy;
}
