package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Create Preparation Checklist Request DTO.
 *
 * Used for creating a new patient preparation checklist.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChecklistRequest {

    /**
     * Order ID
     */
    @NotNull(message = "ID order harus diisi")
    private UUID orderId;

    /**
     * Examination ID
     */
    @NotNull(message = "ID pemeriksaan harus diisi")
    private UUID examinationId;

    /**
     * Preparation instructions
     */
    @Size(max = 2000, message = "Instruksi persiapan maksimal 2000 karakter")
    private String preparationInstructions;
}
