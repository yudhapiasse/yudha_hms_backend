package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.EncounterStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Status History DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterStatusHistoryDto {

    private UUID id;

    private EncounterStatus fromStatus;

    private EncounterStatus toStatus;

    private LocalDateTime statusChangedAt;

    private UUID changedById;

    private String changedByName;

    private String reason;

    private String notes;

    private String transitionDescription;

    private String transitionDescriptionIndonesian;
}
