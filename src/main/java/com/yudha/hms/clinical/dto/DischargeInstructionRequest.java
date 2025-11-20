package com.yudha.hms.clinical.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Discharge Instruction Request DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DischargeInstructionRequest {

    @NotBlank(message = "Instruction category is required")
    private String instructionCategory; // WOUND_CARE, DIET, ACTIVITY, PHYSICAL_THERAPY, MEDICATION, GENERAL

    @NotBlank(message = "Instruction title is required")
    private String instructionTitle;

    @NotBlank(message = "Instruction details are required")
    private String instructionDetails;

    private String instructionFrequency;
    private String instructionDuration;

    private String doInstructions;
    private String dontInstructions;
    private String whenToCallDoctor;

    private Boolean hasVideoTutorial;
    private String videoUrl;
    private Boolean hasPrintedMaterial;
    private String printedMaterialUrl;
    private String diagramUrl;

    private Boolean isCriticalInstruction;
    private Integer displayOrder;
    private String additionalNotes;
}
