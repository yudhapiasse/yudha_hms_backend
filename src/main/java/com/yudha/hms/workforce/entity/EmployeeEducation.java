package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.EducationLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "employee_education", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEducation extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "education_level", length = 30, nullable = false)
    private EducationLevel educationLevel;

    @Column(name = "institution_name", length = 200, nullable = false)
    private String institutionName;

    @Column(name = "major_field_of_study", length = 200)
    private String majorFieldOfStudy;

    @Column(name = "degree_title", length = 100)
    private String degreeTitle;

    @Column(name = "start_year")
    private Integer startYear;

    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(name = "gpa", precision = 4, scale = 2)
    private BigDecimal gpa;

    @Column(name = "gpa_scale", precision = 4, scale = 2)
    private BigDecimal gpaScale;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "certificate_number", length = 100)
    private String certificateNumber;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @Column(name = "is_highest_education", nullable = false)
    private Boolean isHighestEducation = false;

    @Column(name = "verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
