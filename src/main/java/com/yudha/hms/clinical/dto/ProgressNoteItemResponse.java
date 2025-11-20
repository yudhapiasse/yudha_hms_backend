package com.yudha.hms.clinical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Progress Note Item Response DTO.
 *
 * Lightweight representation of a progress note for integration responses.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressNoteItemResponse {

    private UUID id;
    private String noteNumber;
    private String noteType;
    private LocalDateTime noteDateTime;
    private String shift;
    private String providerName;
    private String providerType;
    private Boolean isComplete;
    private Boolean hasCriticalFindings;
    private String assessment; // Summary
    private String plan; // Summary
}
