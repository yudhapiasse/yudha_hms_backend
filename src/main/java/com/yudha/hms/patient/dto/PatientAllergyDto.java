package com.yudha.hms.patient.dto;

import com.yudha.hms.shared.constant.AllergenType;
import com.yudha.hms.shared.constant.AllergySeverity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Patient Allergy DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientAllergyDto {

    /**
     * Allergen type
     */
    @NotNull(message = "Tipe alergen wajib diisi")
    private AllergenType allergenType;

    /**
     * Allergen name
     */
    @NotBlank(message = "Nama alergen wajib diisi")
    @Size(max = 200, message = "Nama alergen tidak boleh lebih dari 200 karakter")
    private String allergenName;

    /**
     * Reaction description
     */
    private String reaction;

    /**
     * Severity
     */
    private AllergySeverity severity;

    /**
     * Notes
     */
    private String notes;
}
