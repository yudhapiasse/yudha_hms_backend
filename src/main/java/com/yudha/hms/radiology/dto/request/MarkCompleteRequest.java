package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Mark Complete Request DTO.
 *
 * Used for marking preparation checklist as complete.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkCompleteRequest {

    /**
     * Completed by (user ID)
     */
    @NotNull(message = "ID petugas harus diisi")
    private UUID completedBy;
}
