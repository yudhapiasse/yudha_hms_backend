package com.yudha.hms.clinical.dto;

import com.yudha.hms.clinical.entity.ClinicalStatus;
import com.yudha.hms.clinical.entity.DiagnosisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encounter Diagnosis DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncounterDiagnosisDto {

    private UUID id;

    private UUID diagnosisId;

    @NotBlank(message = "Kode diagnosis wajib diisi")
    private String diagnosisCode;

    @NotBlank(message = "Teks diagnosis wajib diisi")
    private String diagnosisText;

    @NotNull(message = "Tipe diagnosis wajib diisi")
    private DiagnosisType diagnosisType;

    @NotNull(message = "Status klinis wajib diisi")
    private ClinicalStatus clinicalStatus;

    @Positive(message = "Rank harus bernilai positif")
    private Integer rank;

    private String verificationStatus;

    private LocalDate onsetDate;

    private LocalDateTime recordedDate;

    private String severity;

    private UUID diagnosedById;

    private String diagnosedByName;

    private String clinicalNotes;
}
