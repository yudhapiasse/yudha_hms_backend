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
    @Column(name = "training_type", length = 50, nullable = false)
    private TrainingType trainingType;

    @Column(name = "training_name", length = 200, nullable = false)
    private String trainingName;

    @Column(name = "training_provider", length = 200)
    private String trainingProvider;

    @Column(name = "training_category", length = 100)
    private String trainingCategory;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "issues_certificate", nullable = false)
    private Boolean issuesCertificate = false;

    @Column(name = "certificate_number", length = 100)
    private String certificateNumber;

    @Column(name = "certificate_url", length = 500)
    private String certificateUrl;

    @Column(name = "certificate_expiry_date")
    private LocalDate certificateExpiryDate;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = false;

    @Column(name = "is_regulatory_required", nullable = false)
    private Boolean isRegulatoryRequired = false;

    @Column(name = "training_cost", precision = 15, scale = 2)
    private BigDecimal trainingCost;

    @Column(name = "currency", length = 10)
    private String currency = "IDR";

    @Column(name = "paid_by", length = 50)
    private String paidBy;

    @Column(name = "attended", nullable = false)
    private Boolean attended = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status", length = 30)
    private CompletionStatus completionStatus;

    @Column(name = "evaluation_score", precision = 5, scale = 2)
    private BigDecimal evaluationScore;

    @Column(name = "pass_fail_status", length = 20)
    private String passFailStatus;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
