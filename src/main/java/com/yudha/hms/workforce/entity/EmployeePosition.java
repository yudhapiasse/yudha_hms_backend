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

    @Column(name = "position_code", length = 20, nullable = false, unique = true)
    private String positionCode;

    @Column(name = "position_name", length = 100, nullable = false)
    private String positionName;

    @Column(name = "position_name_id", length = 100, nullable = false)
    private String positionNameId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "parent_position_id")
    private UUID parentPositionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_level", length = 20, nullable = false)
    private PositionLevel positionLevel;

    @Column(name = "requires_str", nullable = false)
    private Boolean requiresStr = false;

    @Column(name = "requires_sip", nullable = false)
    private Boolean requiresSip = false;

    @Column(name = "min_education_level", length = 20)
    private String minEducationLevel;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "job_description_id", columnDefinition = "TEXT")
    private String jobDescriptionId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
