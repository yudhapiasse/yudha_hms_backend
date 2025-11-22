package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.EducationLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Column(name = "education_level", length = 20, nullable = false)
    private EducationLevel educationLevel;

    @Column(name = "institution_name", length = 200, nullable = false)
    private String institutionName;

    @Column(name = "field_of_study", length = 200)
    private String fieldOfStudy;

    @Column(name = "major", length = 200)
    private String major;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "graduation_date")
    private LocalDate graduationDate;

    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa;

    @Column(name = "gpa_scale", precision = 3, scale = 2)
    private BigDecimal gpaScale;

    @Column(name = "certificate_number", length = 100)
    private String certificateNumber;

    @Column(name = "accreditation", length = 10)
    private String accreditation;

    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "transcript_url", length = 500)
    private String transcriptUrl;

    @Column(name = "is_highest_education", nullable = false)
    private Boolean isHighestEducation = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
