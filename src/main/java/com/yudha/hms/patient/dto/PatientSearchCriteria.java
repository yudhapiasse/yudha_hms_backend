package com.yudha.hms.patient.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Patient Search Criteria DTO.
 *
 * Used for searching patients with various filters and search terms.
 * Supports pagination, sorting, and advanced filters.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientSearchCriteria {

    // ============================================================================
    // Quick Search Fields (search any of these)
    // ============================================================================

    /**
     * Quick search term - searches across multiple fields:
     * - MRN (Medical Record Number)
     * - NIK (Indonesian National ID)
     * - BPJS Number
     * - Full Name
     * Uses PostgreSQL full-text search for name matching
     */
    private String searchTerm;

    // ============================================================================
    // Specific Search Fields
    // ============================================================================

    /**
     * Search by Medical Record Number (exact match)
     */
    private String mrn;

    /**
     * Search by NIK - Indonesian National ID (exact match)
     */
    private String nik;

    /**
     * Search by BPJS number (exact match)
     */
    private String bpjsNumber;

    /**
     * Search by patient name (full-text search, partial match)
     */
    private String name;

    /**
     * Search by phone number (partial match)
     */
    private String phone;

    /**
     * Search by email (partial match)
     */
    private String email;

    // ============================================================================
    // Advanced Filters
    // ============================================================================

    /**
     * Filter by gender (MALE, FEMALE)
     */
    private String gender;

    /**
     * Filter by birth date range - start date
     */
    private LocalDate birthDateFrom;

    /**
     * Filter by birth date range - end date
     */
    private LocalDate birthDateTo;

    /**
     * Filter by age range - minimum age
     */
    @Min(0)
    @Max(150)
    private Integer ageFrom;

    /**
     * Filter by age range - maximum age
     */
    @Min(0)
    @Max(150)
    private Integer ageTo;

    /**
     * Filter by registration date range - start date
     */
    private LocalDate registrationDateFrom;

    /**
     * Filter by registration date range - end date
     */
    private LocalDate registrationDateTo;

    /**
     * Filter by religion ID
     */
    private String religionId;

    /**
     * Filter by blood type ID
     */
    private String bloodTypeId;

    /**
     * Filter by marital status ID
     */
    private String maritalStatusId;

    /**
     * Filter by province ID
     */
    private String provinceId;

    /**
     * Filter by city ID
     */
    private String cityId;

    /**
     * Filter by district ID
     */
    private String districtId;

    /**
     * Filter by active status
     */
    private Boolean isActive;

    /**
     * Filter by deceased status
     */
    private Boolean isDeceased;

    /**
     * Filter by VIP status
     */
    private Boolean isVip;

    /**
     * Filter by BPJS active status
     */
    private Boolean bpjsActive;

    /**
     * Filter by BPJS class (Kelas 1, 2, 3)
     */
    private String bpjsClass;

    // ============================================================================
    // Pagination and Sorting
    // ============================================================================

    /**
     * Page number (0-indexed)
     * Default: 0
     */
    @Builder.Default
    @Min(0)
    private Integer page = 0;

    /**
     * Page size (number of records per page)
     * Default: 20
     */
    @Builder.Default
    @Min(1)
    @Max(100)
    private Integer size = 20;

    /**
     * Sort field
     * Supported values: mrn, fullName, birthDate, registrationDate, createdAt
     * Default: createdAt
     */
    @Builder.Default
    private String sortBy = "createdAt";

    /**
     * Sort direction (ASC or DESC)
     * Default: DESC
     */
    @Builder.Default
    private String sortDirection = "DESC";

    // ============================================================================
    // Data Depth Configuration
    // ============================================================================

    /**
     * Data depth level for response
     * - BASIC: Only core patient info (id, mrn, name, birth date, gender)
     * - DETAILED: Includes addresses and contact info
     * - COMPLETE: Includes everything (addresses, emergency contacts, allergies)
     * Default: DETAILED
     */
    @Builder.Default
    private DataDepth dataDepth = DataDepth.DETAILED;

    /**
     * Data depth enumeration
     */
    public enum DataDepth {
        BASIC,      // Minimal info
        DETAILED,   // Standard info with addresses
        COMPLETE    // All info including related entities
    }

    // ============================================================================
    // Utility Methods
    // ============================================================================

    /**
     * Check if quick search is active
     */
    public boolean hasQuickSearch() {
        return searchTerm != null && !searchTerm.trim().isEmpty();
    }

    /**
     * Check if specific field searches are active
     */
    public boolean hasSpecificSearches() {
        return mrn != null || nik != null || bpjsNumber != null
            || (name != null && !name.trim().isEmpty())
            || (phone != null && !phone.trim().isEmpty())
            || (email != null && !email.trim().isEmpty());
    }

    /**
     * Check if advanced filters are active
     */
    public boolean hasAdvancedFilters() {
        return gender != null || birthDateFrom != null || birthDateTo != null
            || ageFrom != null || ageTo != null
            || registrationDateFrom != null || registrationDateTo != null
            || religionId != null || bloodTypeId != null || maritalStatusId != null
            || provinceId != null || cityId != null || districtId != null
            || isActive != null || isDeceased != null || isVip != null
            || bpjsActive != null || bpjsClass != null;
    }
}