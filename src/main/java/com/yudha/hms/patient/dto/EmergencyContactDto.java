package com.yudha.hms.patient.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Emergency Contact DTO.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContactDto {

    /**
     * Full name
     */
    @NotBlank(message = "Nama kontak darurat wajib diisi")
    @Size(max = 200, message = "Nama tidak boleh lebih dari 200 karakter")
    private String fullName;

    /**
     * Relationship to patient
     */
    @NotBlank(message = "Hubungan dengan pasien wajib diisi")
    @Size(max = 50, message = "Hubungan tidak boleh lebih dari 50 karakter")
    private String relationship;

    /**
     * Primary phone
     */
    @NotBlank(message = "Nomor telepon utama wajib diisi")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format nomor telepon tidak valid")
    private String phonePrimary;

    /**
     * Secondary phone
     */
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format nomor telepon tidak valid")
    private String phoneSecondary;

    /**
     * Email
     */
    @Email(message = "Format email tidak valid")
    private String email;

    /**
     * Address
     */
    private String address;

    /**
     * Priority (1 = primary)
     */
    @Min(value = 1, message = "Prioritas minimal 1")
    private Integer priority;

    /**
     * Notes
     */
    private String notes;
}
