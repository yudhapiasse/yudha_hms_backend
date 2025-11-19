package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.ParticipantType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Participant DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterParticipantDto {

    private UUID id;

    @NotNull(message = "Practitioner ID wajib diisi")
    private UUID practitionerId;

    @NotNull(message = "Tipe partisipan wajib diisi")
    private ParticipantType participantType;

    private String participantName;

    private String participantRole;

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private String notes;
}
