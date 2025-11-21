package com.yudha.hms.laboratory.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Alert Acknowledgment Request DTO.
 *
 * Used for acknowledging critical value alerts.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertAcknowledgmentRequest {

    /**
     * Alert ID
     */
    @NotNull(message = "ID alert harus diisi")
    private UUID alertId;

    /**
     * Acknowledged by user ID
     */
    @NotNull(message = "ID user yang mengakui harus diisi")
    private UUID acknowledgedBy;

    /**
     * Acknowledgment notes
     */
    private String acknowledgmentNotes;

    /**
     * Action taken
     */
    private String actionTaken;

    /**
     * Mark as resolved
     */
    private Boolean markAsResolved;

    /**
     * Resolution notes
     */
    private String resolutionNotes;
}
