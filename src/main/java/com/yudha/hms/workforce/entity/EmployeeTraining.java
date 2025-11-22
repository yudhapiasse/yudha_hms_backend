package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.CompletionStatus;
import com.yudha.hms.workforce.constant.TrainingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_training", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTraining extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_type", length = 30, nullable = false)
    private TrainingType trainingType;

    @Column(name = "training_name", length = 200, nullable = false)
    private String trainingName;

    @Column(name = "provider", length = 200)
    private String provider;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "duration_hours", precision = 8, scale = 2)
    private BigDecimal durationHours;

    @Column(name = "location", length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", length = 20, nullable = false)
    private CompletionStatus completionStatus;

    @Column(name = "certificate_number", length = 100)
    private String certificateNumber;

    @Column(name = "certificate_expiry_date")
    private LocalDate certificateExpiryDate;

    @Column(name = "skp_credits", precision = 8, scale = 2)
    private BigDecimal skpCredits;

    @Column(name = "cost", precision = 15, scale = 2)
    private BigDecimal cost;

    @Column(name = "sponsored_by", length = 200)
    private String sponsoredBy;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
