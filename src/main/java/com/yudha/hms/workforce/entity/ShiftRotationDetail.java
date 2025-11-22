package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "shift_rotation_detail", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRotationDetail extends SoftDeletableEntity {

    @Column(name = "rotation_id", nullable = false)
    private UUID rotationId;

    @Column(name = "day_sequence", nullable = false)
    private Integer daySequence;

    @Column(name = "shift_pattern_id", nullable = false)
    private UUID shiftPatternId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
