package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "leave_balance", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "leave_type_id", nullable = false)
    private UUID leaveTypeId;

    @Column(name = "balance_year", nullable = false)
    private Integer balanceYear;

    @Column(name = "opening_balance", precision = 5, scale = 2)
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Column(name = "accrued_days", precision = 5, scale = 2)
    private BigDecimal accruedDays = BigDecimal.ZERO;

    @Column(name = "adjustment_days", precision = 5, scale = 2)
    private BigDecimal adjustmentDays = BigDecimal.ZERO;

    @Column(name = "taken_days", precision = 5, scale = 2)
    private BigDecimal takenDays = BigDecimal.ZERO;

    @Column(name = "pending_days", precision = 5, scale = 2)
    private BigDecimal pendingDays = BigDecimal.ZERO;

    @Column(name = "available_days", precision = 5, scale = 2)
    private BigDecimal availableDays = BigDecimal.ZERO;

    @Column(name = "carried_forward_from_previous", precision = 5, scale = 2)
    private BigDecimal carriedForwardFromPrevious = BigDecimal.ZERO;

    @Column(name = "carry_forward_expiry_date")
    private LocalDate carryForwardExpiryDate;

    @Column(name = "last_calculated_at")
    private LocalDateTime lastCalculatedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
