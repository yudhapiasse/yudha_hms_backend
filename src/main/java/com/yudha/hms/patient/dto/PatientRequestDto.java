package com.yudha.hms.patient.dto;

import com.yudha.hms.shared.constant.Gender;
import com.yudha.hms.shared.constant.RegistrationSource;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Patient Registration Request DTO.
 *
 * Used for creating new patient records.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequestDto {

    // ========================================================================
    // INDONESIAN IDENTIFICATION
    // ========================================================================

    /**
     * NIK - 16-digit Indonesian National ID
     * Optional for foreign patients
     */
    @Pattern(regexp = "^[0-9]{16}$", message = "NIK harus 16 digit angka")
    private String nik;

    /**
     * BPJS number - 13 digits
     * Optional, only for BPJS participants
     */
    @Pattern(regexp = "^[0-9]{13}$", message = "Nomor BPJS harus 13 digit angka")
    private String bpjsNumber;

    /**
     * BPJS class (Kelas 1, Kelas 2, Kelas 3)
     */
    private String bpjsClass;

    /**
     * BPJS provider code (Faskes Tingkat 1)
     */
    private String bpjsProviderCode;

    /**
     * BPJS active status
     * True if patient is an active BPJS participant
     */
    private Boolean bpjsActive;

    // ========================================================================
    // PERSONAL INFORMATION
    // ========================================================================

    /**
     * Title (Tn., Ny., An., etc.)
     */
    @Size(max = 10, message = "Gelar tidak boleh lebih dari 10 karakter")
    private String title;

    /**
     * Full name (required)
     */
    @NotBlank(message = "Nama lengkap wajib diisi")
    @Size(max = 200, message = "Nama lengkap tidak boleh lebih dari 200 karakter")
    private String fullName;

    /**
     * Place of birth
     */
    @Size(max = 100, message = "Tempat lahir tidak boleh lebih dari 100 karakter")
    private String birthPlace;

    /**
     * Date of birth (required)
     */
    @NotNull(message = "Tanggal lahir wajib diisi")
    @Past(message = "Tanggal lahir harus di masa lalu")
    private LocalDate birthDate;

    /**
     * Gender (required)
     */
    @NotNull(message = "Jenis kelamin wajib diisi")
    private Gender gender;

    // ========================================================================
    // INDONESIAN DEMOGRAPHIC FIELDS
    // ========================================================================

    /**
     * Religion ID (required by Indonesian law)
     */
    @NotNull(message = "Agama wajib diisi")
    private UUID religionId;

    /**
     * Marital status ID
     */
    private UUID maritalStatusId;

    /**
     * Blood type ID
     */
    private UUID bloodTypeId;

    /**
     * Education level ID
     */
    private UUID educationId;

    /**
     * Occupation ID
     */
    private UUID occupationId;

    /**
     * Nationality
     */
    @Size(max = 50, message = "Kewarganegaraan tidak boleh lebih dari 50 karakter")
    private String nationality;

    /**
     * Citizenship (WNI or WNA)
     */
    @Size(max = 50, message = "Status kewarganegaraan tidak boleh lebih dari 50 karakter")
    private String citizenship;

    /**
     * Mother's maiden name
     */
    @Size(max = 200, message = "Nama gadis ibu tidak boleh lebih dari 200 karakter")
    private String motherMaidenName;

    // ========================================================================
    // CONTACT INFORMATION
    // ========================================================================

    /**
     * Primary phone number (required)
     */
    @NotBlank(message = "Nomor telepon utama wajib diisi")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format nomor telepon tidak valid")
    private String phonePrimary;

    /**
     * Secondary phone number
     */
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Format nomor telepon tidak valid")
    private String phoneSecondary;

    /**
     * Email address
     */
    @Email(message = "Format email tidak valid")
    @Size(max = 100, message = "Email tidak boleh lebih dari 100 karakter")
    private String email;

    // ========================================================================
    // PHOTO
    // ========================================================================

    /**
     * Photo URL/path
     */
    private String photoUrl;

    // ========================================================================
    // REGISTRATION INFO
    // ========================================================================

    /**
     * Registration source
     */
    private RegistrationSource registrationSource;

    /**
     * VIP status
     * True for VIP patients (government officials, celebrities, etc.)
     */
    private Boolean isVip;

    /**
     * Notes
     */
    private String notes;

    // ========================================================================
    // RELATED DATA
    // ========================================================================

    /**
     * Addresses (KTP and/or Domicile)
     */
    private List<PatientAddressDto> addresses;

    /**
     * Emergency contacts
     */
    private List<EmergencyContactDto> emergencyContacts;

    /**
     * Allergies
     */
    private List<PatientAllergyDto> allergies;
}
