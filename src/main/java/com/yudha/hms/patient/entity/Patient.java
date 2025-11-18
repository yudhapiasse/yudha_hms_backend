package com.yudha.hms.patient.entity;

import com.yudha.hms.shared.constant.Gender;
import com.yudha.hms.shared.constant.RegistrationSource;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Patient Entity for Indonesian Hospital Management System.
 *
 * Represents patient demographic and identification information with Indonesian-specific fields.
 *
 * Indonesian-specific features:
 * - NIK (Nomor Induk Kependudukan) - 16-digit national ID
 * - BPJS Kesehatan number and class
 * - Religion (required by Indonesian regulations)
 * - KTP vs Domicile addresses
 * - RT/RW (neighborhood identifiers)
 *
 * Compliance:
 * - Permenkes No. 24/2022 (E-Rekam Medis)
 * - UU PDP (Personal Data Protection)
 * - BPJS regulations
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Entity
@Table(name = "patient", schema = "patient_schema", indexes = {
    @Index(name = "idx_patient_mrn", columnList = "mrn", unique = true),
    @Index(name = "idx_patient_nik", columnList = "nik"),
    @Index(name = "idx_patient_bpjs", columnList = "bpjs_number"),
    @Index(name = "idx_patient_name", columnList = "full_name"),
    @Index(name = "idx_patient_birth_date", columnList = "birth_date"),
    @Index(name = "idx_patient_active", columnList = "is_active"),
    @Index(name = "idx_patient_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends SoftDeletableEntity {

    // ========================================================================
    // MEDICAL RECORD IDENTIFICATION
    // ========================================================================

    /**
     * Medical Record Number (MRN) - Unique patient identifier within hospital
     * Format: YYYYMM-XXXXX (e.g., 202501-00001)
     * Auto-generated using sequence
     */
    @Column(name = "mrn", length = 50, nullable = false, unique = true)
    @NotBlank(message = "Medical Record Number is required")
    @Size(max = 50, message = "MRN must not exceed 50 characters")
    private String mrn;

    // ========================================================================
    // INDONESIAN NATIONAL IDENTIFICATION
    // ========================================================================

    /**
     * NIK - Nomor Induk Kependudukan (Indonesian National ID Number)
     * 16-digit number issued by DUKCAPIL
     * Format: PPKKSSDDMMYYXXXX
     * - PP: Province code (2 digits)
     * - KK: City/Regency code (2 digits)
     * - SS: District code (2 digits)
     * - DDMMYY: Birth date (6 digits)
     * - XXXX: Sequential number (4 digits)
     */
    @Column(name = "nik", length = 16, unique = true)
    @Pattern(regexp = "^[0-9]{16}$", message = "NIK must be exactly 16 digits")
    private String nik;

    /**
     * NIK verification status
     * True if NIK has been verified with DUKCAPIL
     */
    @Column(name = "nik_verified")
    @Builder.Default
    private Boolean nikVerified = false;

    /**
     * NIK verification date
     * Timestamp when NIK was verified with DUKCAPIL
     */
    @Column(name = "nik_verification_date")
    private java.time.LocalDateTime nikVerificationDate;

    // ========================================================================
    // BPJS (NATIONAL HEALTH INSURANCE) INFORMATION
    // ========================================================================

    /**
     * BPJS Kesehatan card number
     * 13-digit number for BPJS participants
     * Format: XXXXXXXXXXXXX
     */
    @Column(name = "bpjs_number", length = 13, unique = true)
    @Pattern(regexp = "^[0-9]{13}$", message = "BPJS number must be exactly 13 digits")
    private String bpjsNumber;

    /**
     * BPJS active status
     * True if patient is an active BPJS participant
     */
    @Column(name = "bpjs_active")
    @Builder.Default
    private Boolean bpjsActive = false;

    /**
     * BPJS class/tier
     * Class 1, 2, or 3 (Kelas 1, 2, 3)
     * Determines room entitlement and coverage
     */
    @Column(name = "bpjs_class", length = 10)
    private String bpjsClass;

    /**
     * BPJS primary care provider code (Faskes Tingkat 1)
     * Code of the assigned primary care facility
     */
    @Column(name = "bpjs_provider_code", length = 20)
    private String bpjsProviderCode;

    // ========================================================================
    // PERSONAL INFORMATION
    // ========================================================================

    /**
     * Title (e.g., Tn., Ny., An., Dr., Prof.)
     * - Tn. (Tuan) - Mr.
     * - Ny. (Nyonya) - Mrs.
     * - Nn. (Nona) - Miss
     * - An. (Anak) - Child
     */
    @Column(name = "title", length = 10)
    private String title;

    /**
     * Full name of the patient
     * As registered in KTP or birth certificate
     */
    @Column(name = "full_name", length = 200, nullable = false)
    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    /**
     * Place of birth
     * City/regency where patient was born
     */
    @Column(name = "birth_place", length = 100)
    private String birthPlace;

    /**
     * Date of birth
     */
    @Column(name = "birth_date", nullable = false)
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    /**
     * Gender
     * MALE or FEMALE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10, nullable = false)
    @NotNull(message = "Gender is required")
    private Gender gender;

    // ========================================================================
    // INDONESIAN-SPECIFIC DEMOGRAPHIC FIELDS
    // ========================================================================

    /**
     * Religion (required by Indonesian regulations)
     * Reference to master_schema.religion table
     * Options: Islam, Kristen, Katolik, Hindu, Buddha, Konghucu, Kepercayaan
     */
    @Column(name = "religion_id")
    private java.util.UUID religionId;

    /**
     * Marital status
     * Reference to master_schema.marital_status table
     * Options: Belum Kawin, Kawin, Cerai Hidup, Cerai Mati
     */
    @Column(name = "marital_status_id")
    private java.util.UUID maritalStatusId;

    /**
     * Blood type
     * Reference to master_schema.blood_type table
     * Options: A+, A-, B+, B-, AB+, AB-, O+, O-, UNKNOWN
     */
    @Column(name = "blood_type_id")
    private java.util.UUID bloodTypeId;

    /**
     * Education level
     * Reference to master_schema.education_level table
     */
    @Column(name = "education_id")
    private java.util.UUID educationId;

    /**
     * Occupation
     * Reference to master_schema.occupation table
     */
    @Column(name = "occupation_id")
    private java.util.UUID occupationId;

    /**
     * Nationality
     * Default: Indonesian
     */
    @Column(name = "nationality", length = 50)
    @Builder.Default
    private String nationality = "Indonesian";

    /**
     * Citizenship status
     * WNI (Warga Negara Indonesia) or WNA (Warga Negara Asing)
     */
    @Column(name = "citizenship", length = 50)
    @Builder.Default
    private String citizenship = "WNI";

    /**
     * Mother's maiden name
     * Used for patient verification and security
     */
    @Column(name = "mother_maiden_name", length = 200)
    private String motherMaidenName;

    // ========================================================================
    // CONTACT INFORMATION
    // ========================================================================

    /**
     * Primary phone number
     * Mobile phone preferred for SMS/WhatsApp notifications
     */
    @Column(name = "phone_primary", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phonePrimary;

    /**
     * Secondary phone number
     * Alternative contact number
     */
    @Column(name = "phone_secondary", length = 20)
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneSecondary;

    /**
     * Email address
     */
    @Column(name = "email", length = 100)
    @Email(message = "Invalid email format")
    private String email;

    // ========================================================================
    // PATIENT PHOTO
    // ========================================================================

    /**
     * Patient photo URL/path
     * Reference to file storage (MinIO, S3, or local path)
     */
    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;

    // ========================================================================
    // REGISTRATION INFORMATION
    // ========================================================================

    /**
     * Registration date
     * Date when patient first registered with the hospital
     */
    @Column(name = "registration_date", nullable = false)
    @Builder.Default
    private java.time.LocalDateTime registrationDate = java.time.LocalDateTime.now();

    /**
     * Registration source
     * How the patient registered (walk-in, online, Mobile JKN, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_source", length = 50)
    private RegistrationSource registrationSource;

    // ========================================================================
    // STATUS FIELDS
    // ========================================================================

    /**
     * Active status
     * True if patient is active, false if inactive
     */
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Deceased status
     * True if patient has passed away
     */
    @Column(name = "is_deceased", nullable = false)
    @Builder.Default
    private Boolean isDeceased = false;

    /**
     * Deceased date
     * Date when patient passed away
     */
    @Column(name = "deceased_date")
    private java.time.LocalDateTime deceasedDate;

    /**
     * Deceased reason
     * Cause of death or reason for marking as deceased
     */
    @Column(name = "deceased_reason", columnDefinition = "TEXT")
    private String deceasedReason;

    // ========================================================================
    // VIP / SPECIAL STATUS
    // ========================================================================

    /**
     * VIP status
     * True for VIP patients (government officials, celebrities, etc.)
     */
    @Column(name = "is_vip")
    @Builder.Default
    private Boolean isVip = false;

    /**
     * VIP notes
     * Special instructions for VIP patient handling
     */
    @Column(name = "vip_notes", columnDefinition = "TEXT")
    private String vipNotes;

    // ========================================================================
    // GENERAL NOTES
    // ========================================================================

    /**
     * General notes
     * Any additional information about the patient
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // ========================================================================
    // RELATIONSHIPS
    // ========================================================================

    /**
     * Patient addresses (KTP and Domicile)
     * One-to-many relationship
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PatientAddress> addresses = new ArrayList<>();

    /**
     * Emergency contacts
     * One-to-many relationship
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();

    /**
     * Patient allergies
     * One-to-many relationship
     */
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PatientAllergy> allergies = new ArrayList<>();

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Add address to patient
     */
    public void addAddress(PatientAddress address) {
        addresses.add(address);
        address.setPatient(this);
    }

    /**
     * Remove address from patient
     */
    public void removeAddress(PatientAddress address) {
        addresses.remove(address);
        address.setPatient(null);
    }

    /**
     * Add emergency contact
     */
    public void addEmergencyContact(EmergencyContact contact) {
        emergencyContacts.add(contact);
        contact.setPatient(this);
    }

    /**
     * Remove emergency contact
     */
    public void removeEmergencyContact(EmergencyContact contact) {
        emergencyContacts.remove(contact);
        contact.setPatient(null);
    }

    /**
     * Add allergy
     */
    public void addAllergy(PatientAllergy allergy) {
        allergies.add(allergy);
        allergy.setPatient(this);
    }

    /**
     * Remove allergy
     */
    public void removeAllergy(PatientAllergy allergy) {
        allergies.remove(allergy);
        allergy.setPatient(null);
    }

    /**
     * Calculate age from birth date
     */
    @Transient
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Check if patient is pediatric (< 18 years old)
     */
    @Transient
    public boolean isPediatric() {
        return getAge() < 18;
    }

    /**
     * Check if patient is geriatric (>= 65 years old)
     */
    @Transient
    public boolean isGeriatric() {
        return getAge() >= 65;
    }

    /**
     * Get display name with title
     */
    @Transient
    public String getDisplayName() {
        return (title != null ? title + " " : "") + fullName;
    }
}