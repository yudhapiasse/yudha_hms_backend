package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.LicenseRenewalStatus;
import com.yudha.hms.workforce.constant.LicenseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "professional_license", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalLicense extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", length = 50, nullable = false)
    private LicenseType licenseType;

    @Column(name = "license_number", length = 100, nullable = false)
    private String licenseNumber;

    @Column(name = "issued_by", length = 200, nullable = false)
    private String issuedBy;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "is_expired", nullable = false)
    private Boolean isExpired = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "renewal_status", length = 30)
    private LicenseRenewalStatus renewalStatus;

    @Column(name = "renewal_reminder_sent", nullable = false)
    private Boolean renewalReminderSent = false;

    @Column(name = "last_reminder_date")
    private LocalDate lastReminderDate;

    @Column(name = "profession", length = 100)
    private String profession;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "practice_location", length = 200)
    private String practiceLocation;

    @Column(name = "scope_of_practice", columnDefinition = "TEXT")
    private String scopeOfPractice;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
