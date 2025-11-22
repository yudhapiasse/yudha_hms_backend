package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.PositionLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "employee_position", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeePosition extends SoftDeletableEntity {

    @Column(name = "position_code", length = 50, nullable = false, unique = true)
    private String positionCode;

    @Column(name = "position_name", length = 200, nullable = false)
    private String positionName;

    @Column(name = "position_name_id", length = 200, nullable = false)
    private String positionNameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_level", length = 30)
    private PositionLevel positionLevel;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "parent_position_id")
    private UUID parentPositionId;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "qualifications", columnDefinition = "TEXT")
    private String qualifications;

    @Column(name = "requires_medical_license", nullable = false)
    private Boolean requiresMedicalLicense = false;

    @Column(name = "required_license_type", length = 50)
    private String requiredLicenseType;

    @Column(name = "requires_certification", nullable = false)
    private Boolean requiresCertification = false;

    @Column(name = "required_certifications")
    private String[] requiredCertifications;

    @Column(name = "min_experience_years")
    private Integer minExperienceYears;

    @Column(name = "min_education_level", length = 30)
    private String minEducationLevel;

    @Column(name = "active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
