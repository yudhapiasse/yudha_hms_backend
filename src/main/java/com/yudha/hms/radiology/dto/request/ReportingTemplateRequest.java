package com.yudha.hms.radiology.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Reporting Template Request DTO.
 *
 * Used for creating and updating reporting templates.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportingTemplateRequest {

    /**
     * Examination ID
     */
    @NotNull(message = "ID pemeriksaan harus diisi")
    private UUID examinationId;

    /**
     * Template name
     */
    @NotBlank(message = "Nama template harus diisi")
    @Size(max = 200, message = "Nama template maksimal 200 karakter")
    private String templateName;

    /**
     * Template code
     */
    @Size(max = 50, message = "Kode template maksimal 50 karakter")
    private String templateCode;

    /**
     * Template sections (JSON structure)
     * Will be stored as JSONB in database
     */
    private String sections;

    /**
     * Is default template for this examination
     */
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Active status
     */
    @Builder.Default
    private Boolean isActive = true;
}
