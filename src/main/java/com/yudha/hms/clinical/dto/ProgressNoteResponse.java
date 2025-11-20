package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.NoteType;
import com.yudha.hms.clinical.entity.ProviderType;
import com.yudha.hms.clinical.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for progress notes.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressNoteResponse {

    private UUID id;

    private String noteNumber;

    private UUID encounterId;

    private UUID patientId;

    // Note metadata
    private NoteType noteType;

    private String noteTypeDisplay;

    private LocalDateTime noteDateTime;

    private Shift shift;

    private String shiftDisplay;

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
    private UUID providerId;

    private String providerName;

    private ProviderType providerType;

    private String providerTypeDisplay;

    private String providerSpecialty;

    // Cosign information
    private Boolean requiresCosign;

    private Boolean cosigned;

    private UUID cosignedById;

    private String cosignedByName;

    private LocalDateTime cosignedAt;

    // Audit information
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    // Computed fields
    private Boolean isComplete;

    private Boolean needsCosign;

    private Boolean hasCriticalFindings;
}
