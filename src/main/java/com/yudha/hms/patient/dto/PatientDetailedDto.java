package com.yudha.hms.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

/**
 * Patient Detailed DTO.
 *
 * Contains standard patient information including addresses.
 * Used when DataDepth = DETAILED.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDetailedDto {

    // ============================================================================
    // Core Information
    // ============================================================================

    /**
     * Patient ID
     */
    private UUID id;

    /**
     * Medical Record Number
     */
    private String mrn;

    /**
     * Indonesian National ID (NIK)
     */
    private String nik;

    /**
     * NIK verified status
     */
    private Boolean nikVerified;

    /**
     * BPJS Number
     */
    private String bpjsNumber;

    /**
     * BPJS active status
     */
    private Boolean bpjsActive;

    /**
     * BPJS class
     */
    private String bpjsClass;

    /**
     * BPJS provider code
     */
    private String bpjsProviderCode;

    // ============================================================================
    // Personal Information
    // ============================================================================

    /**
     * Title (Tn., Ny., An., etc.)
     */
    private String title;

    /**
     * Full name
     */
    private String fullName;

    /**
     * Birth place
     */
    private String birthPlace;

    /**
     * Birth date
     */
    private LocalDate birthDate;

    /**
     * Age (calculated)
     */
    private Integer age;

    /**
     * Gender
     */
    private String gender;

    /**
     * Religion ID
     */
    private UUID religionId;

    /**
     * Religion name
     */
    private String religionName;

    /**
     * Marital status ID
     */
    private UUID maritalStatusId;

    /**
     * Marital status name
     */
    private String maritalStatusName;

    /**
     * Blood type ID
     */
    private UUID bloodTypeId;

    /**
     * Blood type name
     */
    private String bloodTypeName;

    /**
     * Education level ID
     */
    private UUID educationId;

    /**
     * Education level name
     */
    private String educationName;

    /**
     * Occupation ID
     */
    private UUID occupationId;

    /**
     * Occupation name
     */
    private String occupationName;

    /**
     * Nationality
     */
    private String nationality;

    /**
     * Citizenship (WNI/WNA)
     */
    private String citizenship;

    /**
     * Mother's maiden name
     */
    private String motherMaidenName;

    // ============================================================================
    // Contact Information
    // ============================================================================

    /**
     * Primary phone
     */
    private String phonePrimary;

    /**
     * Secondary phone
     */
    private String phoneSecondary;

    /**
     * Email
     */
    private String email;

    /**
     * Photo URL
     */
    private String photoUrl;

    // ============================================================================
    // Addresses
    // ============================================================================

    /**
     * Patient addresses (KTP and Domicile)
     */
    private List<PatientAddressDto> addresses;

    // ============================================================================
    // Registration and Status
    // ============================================================================

    /**
     * Registration date
     */
    private LocalDateTime registrationDate;

    /**
     * Registration source
     */
    private String registrationSource;

    /**
     * Active status
     */
    private Boolean isActive;

    /**
     * Deceased status
     */
    private Boolean isDeceased;

    /**
     * Deceased date
     */
    private LocalDateTime deceasedDate;

    /**
     * VIP status
     */
    private Boolean isVip;

    /**
     * VIP notes
     */
    private String vipNotes;

    /**
     * General notes
     */
    private String notes;

    // ============================================================================
    // Utility Methods
    // ============================================================================

    /**
     * Calculate age from birth date
     */
    public Integer calculateAge() {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Get full display name with title
     */
    public String getDisplayName() {
        if (title != null && !title.isEmpty()) {
            return title + " " + fullName;
        }
        return fullName;
    }

    /**
     * Check if patient has BPJS
     */
    public boolean hasBpjs() {
        return bpjsNumber != null && !bpjsNumber.isEmpty()
            && Boolean.TRUE.equals(bpjsActive);
    }

    /**
     * Get primary address (KTP or first domicile)
     */
    public PatientAddressDto getPrimaryAddress() {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        return addresses.stream()
            .filter(addr -> Boolean.TRUE.equals(addr.getIsPrimary()))
            .findFirst()
            .orElse(addresses.get(0));
    }

    /**
     * Get KTP address
     */
    public PatientAddressDto getKtpAddress() {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        return addresses.stream()
            .filter(addr -> addr.getAddressType() != null &&
                "KTP".equalsIgnoreCase(addr.getAddressType().name()))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get domicile address
     */
    public PatientAddressDto getDomicileAddress() {
        if (addresses == null || addresses.isEmpty()) {
            return null;
        }
        return addresses.stream()
            .filter(addr -> addr.getAddressType() != null &&
                "DOMICILE".equalsIgnoreCase(addr.getAddressType().name()))
            .findFirst()
            .orElse(null);
    }
}