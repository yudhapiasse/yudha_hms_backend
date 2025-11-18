package com.yudha.hms.patient.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yudha.hms.shared.constant.Gender;
import com.yudha.hms.shared.constant.RegistrationSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Patient Response DTO.
 *
 * Returned for patient queries.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponseDto {

    /**
     * Patient ID
     */
    private UUID id;

    /**
     * Medical Record Number
     */
    private String mrn;

    /**
     * NIK
     */
    private String nik;

    /**
     * NIK verified status
     */
    private Boolean nikVerified;

    /**
     * BPJS number
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
     * Title
     */
    private String title;

    /**
     * Full name
     */
    private String fullName;

    /**
     * Display name (with title)
     */
    private String displayName;

    /**
     * Place of birth
     */
    private String birthPlace;

    /**
     * Date of birth
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /**
     * Age (calculated)
     */
    private Integer age;

    /**
     * Gender
     */
    private Gender gender;

    /**
     * Religion ID
     */
    private UUID religionId;

    /**
     * Religion name (denormalized for convenience)
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

    /**
     * Registration date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationDate;

    /**
     * Registration source
     */
    private RegistrationSource registrationSource;

    /**
     * Active status
     */
    private Boolean isActive;

    /**
     * VIP status
     */
    private Boolean isVip;

    /**
     * Addresses
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

    /**
     * Created at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Created by
     */
    private String createdBy;

    /**
     * Updated at
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Updated by
     */
    private String updatedBy;
}
