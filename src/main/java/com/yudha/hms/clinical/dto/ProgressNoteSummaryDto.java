package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.NoteType;
import com.yudha.hms.clinical.entity.Shift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Summary DTO for progress notes (for lists).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressNoteSummaryDto {

    private UUID id;

    private String noteNumber;

    private NoteType noteType;

    private String noteTypeDisplay;

    private LocalDateTime noteDateTime;

    private Shift shift;

    private String providerName;

    private String providerType;

    private Boolean hasCriticalFindings;

    private Boolean needsCosign;

    private String assessment; // Brief preview

    private LocalDateTime createdAt;
}
