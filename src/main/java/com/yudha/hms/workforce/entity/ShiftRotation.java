package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "shift_rotation", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRotation extends SoftDeletableEntity {

    @Column(name = "rotation_code", length = 50, nullable = false, unique = true)
    private String rotationCode;

    @Column(name = "rotation_name", length = 100, nullable = false)
    private String rotationName;

    @Column(name = "rotation_name_id", length = 100, nullable = false)
    private String rotationNameId;

    @Column(name = "pattern_description", columnDefinition = "TEXT")
    private String patternDescription;

    @Column(name = "cycle_length_days", nullable = false)
    private Integer cycleLengthDays;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
