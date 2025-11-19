package com.yudha.hms.registration.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Doctor Entity.
 *
 * Represents medical practitioners with professional credentials and fees.
 * Includes STR (Surat Tanda Registrasi) and SIP (Surat Izin Praktik) information.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Entity
@Table(name = "doctor", schema = "registration_schema",
    indexes = {
        @Index(name = "idx_doctor_str", columnList = "str_number"),
        @Index(name = "idx_doctor_active", columnList = "is_active"),
        @Index(name = "idx_doctor_name", columnList = "full_name")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Doctor master data")
public class Doctor extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // ========== Employee Information ==========
    @Column(name = "employee_id", unique = true, length = 50)
    private String employeeId;

    // ========== Medical License ==========
    @Column(name = "str_number", unique = true, length = 50)
    private String strNumber; // Surat Tanda Registrasi (Medical License)

    @Column(name = "str_expiry_date")
    private LocalDate strExpiryDate;

    @Column(name = "sip_number", length = 50)
    private String sipNumber; // Surat Izin Praktik (Practice Permit)

    @Column(name = "sip_expiry_date")
    private LocalDate sipExpiryDate;

    // ========== Personal Information ==========
    @Column(name = "title", length = 50)
    private String title; // dr., dr. Sp.A, Prof. Dr., etc.

    @Column(name = "full_name", nullable = false, length = 200)
    @NotBlank(message = "Doctor full name is required")
    private String fullName;

    @Column(name = "specialization", length = 100)
    private String specialization; // Spesialis Anak, Spesialis Kandungan, etc.

    @Column(name = "sub_specialization", length = 100)
    private String subSpecialization;

    // ========== Contact ==========
    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    @Email(message = "Invalid email format")
    private String email;

    // ========== Professional Details ==========
    @Column(name = "medical_school", length = 200)
    private String medicalSchool;

    @Column(name = "graduation_year")
    @Min(1900)
    @Max(2100)
    private Integer graduationYear;

    @Column(name = "years_of_experience")
    @Min(0)
    private Integer yearsOfExperience;

    // ========== Consultation Fee ==========
    @Column(name = "base_consultation_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal baseConsultationFee = BigDecimal.ZERO;

    @Column(name = "bpjs_consultation_fee", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal bpjsConsultationFee = BigDecimal.ZERO;

    // ========== Status ==========
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_available_for_telemedicine")
    @Builder.Default
    private Boolean isAvailableForTelemedicine = false;

    // ========== Photo ==========
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    // ========== Business Methods ==========

    /**
     * Get display name with title.
     */
    public String getDisplayName() {
        if (title != null && !title.isEmpty()) {
            return title + " " + fullName;
        }
        return fullName;
    }

    /**
     * Get specialization display.
     */
    public String getSpecializationDisplay() {
        if (specialization != null && subSpecialization != null) {
            return specialization + " - " + subSpecialization;
        }
        return specialization != null ? specialization : "General Practitioner";
    }

    /**
     * Check if STR is valid (not expired).
     */
    public boolean isStrValid() {
        if (strNumber == null || strNumber.isEmpty()) {
            return false;
        }
        if (strExpiryDate == null) {
            return true; // No expiry date set
        }
        return !LocalDate.now().isAfter(strExpiryDate);
    }

    /**
     * Check if SIP is valid (not expired).
     */
    public boolean isSipValid() {
        if (sipNumber == null || sipNumber.isEmpty()) {
            return false;
        }
        if (sipExpiryDate == null) {
            return true; // No expiry date set
        }
        return !LocalDate.now().isAfter(sipExpiryDate);
    }

    /**
     * Check if doctor can practice (active, valid licenses).
     */
    public boolean canPractice() {
        return Boolean.TRUE.equals(isActive) && isStrValid();
    }

    /**
     * Get consultation fee based on payment method.
     */
    public BigDecimal getConsultationFee(boolean isBpjs) {
        return isBpjs ? bpjsConsultationFee : baseConsultationFee;
    }
}