package com.yudha.hms.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

/**
 * Patient Basic DTO.
 *
 * Contains minimal patient information for search results.
 * Used when DataDepth = BASIC.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientBasicDto {

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
     * BPJS Number
     */
    private String bpjsNumber;

    /**
     * Full name
     */
    private String fullName;

    /**
     * Birth date
     */
    private LocalDate birthDate;

    /**
     * Age (calculated)
     */
    private Integer age;

    /**
     * Gender (MALE, FEMALE)
     */
    private String gender;

    /**
     * Primary phone number
     */
    private String phonePrimary;

    /**
     * Photo URL
     */
    private String photoUrl;

    /**
     * Active status
     */
    private Boolean isActive;

    /**
     * Deceased status
     */
    private Boolean isDeceased;

    /**
     * VIP status
     */
    private Boolean isVip;

    /**
     * BPJS active status
     */
    private Boolean bpjsActive;

    /**
     * BPJS class
     */
    private String bpjsClass;

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
     * Get display name with title
     */
    public String getDisplayName() {
        return fullName;
    }

    /**
     * Check if patient has BPJS
     */
    public boolean hasBpjs() {
        return bpjsNumber != null && !bpjsNumber.isEmpty()
            && Boolean.TRUE.equals(bpjsActive);
    }
}