package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.NoteType;
import com.yudha.hms.clinical.entity.ProviderType;
import com.yudha.hms.clinical.entity.Shift;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for creating/updating progress notes.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressNoteRequest {

    @NotNull(message = "Note type is required")
    private NoteType noteType;

    private LocalDateTime noteDateTime;

    private Shift shift;

    // SOAP format
    private String subjective;

    private String objective;

    private String assessment;

    private String plan;

    // Additional information
    private String additionalNotes;

    private Boolean followUpRequired;

    private String followUpInstructions;

    private String criticalFindings;

    // Provider information
    @NotNull(message = "Provider ID is required")
    private UUID providerId;

    @NotBlank(message = "Provider name is required")
    private String providerName;

    private ProviderType providerType;

    private String providerSpecialty;

    // Cosign
    private Boolean requiresCosign;
}
