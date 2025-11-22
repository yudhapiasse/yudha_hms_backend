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

    @Column(name = "specialization", length = 200)
    private String specialization;

    @Column(name = "scope_of_practice", columnDefinition = "TEXT")
    private String scopeOfPractice;

    @Column(name = "facility_name", length = 200)
    private String facilityName;

    @Column(name = "facility_address", columnDefinition = "TEXT")
    private String facilityAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "renewal_status", length = 20)
    private LicenseRenewalStatus renewalStatus;

    @Column(name = "renewal_reminder_sent")
    private Boolean renewalReminderSent = false;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
