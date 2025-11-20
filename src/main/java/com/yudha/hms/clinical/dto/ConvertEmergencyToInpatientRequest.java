package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Convert Emergency to Inpatient Request DTO.
 *
 * Request to convert an emergency encounter to an inpatient encounter.
 * Creates a new inpatient encounter linked to the original emergency encounter.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConvertEmergencyToInpatientRequest {

    @NotNull(message = "Emergency encounter ID is required")
    private UUID emergencyEncounterId;

    @NotNull(message = "Bed ID is required for inpatient admission")
    private UUID bedId;

    @NotNull(message = "Ward ID is required")
    private UUID wardId;

    private UUID roomId;

    @NotBlank(message = "Admission reason is required")
    private String admissionReason;

    @NotBlank(message = "Admission diagnosis is required")
    private String admissionDiagnosis;

    private String clinicalSummary;

    private String specialInstructions;

    @NotNull(message = "Attending physician ID is required")
    private UUID attendingPhysicianId;

    private String notes;
}
