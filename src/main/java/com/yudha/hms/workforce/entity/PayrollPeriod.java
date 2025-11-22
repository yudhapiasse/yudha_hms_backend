package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.PayrollPeriodStatus;
import com.yudha.hms.workforce.constant.ThrType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payroll_period", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class PayrollPeriod extends SoftDeletableEntity {

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "period_code", length = 20, nullable = false, unique = true)
    private String periodCode;

    @Column(name = "period_name", length = 100, nullable = false)
    private String periodName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "cut_off_date", nullable = false)
    private LocalDate cutOffDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private PayrollPeriodStatus status = PayrollPeriodStatus.DRAFT;

    @Column(name = "is_thr_period", nullable = false)
    private Boolean isThrPeriod = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "thr_type", length = 30)
    private ThrType thrType;

    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
