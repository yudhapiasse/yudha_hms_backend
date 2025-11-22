package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.AccrualMethod;
import com.yudha.hms.workforce.constant.LeaveCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_type", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveType extends SoftDeletableEntity {

    @Column(name = "leave_code", length = 50, nullable = false, unique = true)
    private String leaveCode;

    @Column(name = "leave_name", length = 100, nullable = false)
    private String leaveName;

    @Column(name = "leave_name_id", length = 100, nullable = false)
    private String leaveNameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_category", length = 30, nullable = false)
    private LeaveCategory leaveCategory;

    @Column(name = "is_paid")
    private Boolean isPaid = true;

    @Column(name = "requires_approval")
    private Boolean requiresApproval = true;

    @Column(name = "max_days_per_year")
    private Integer maxDaysPerYear;

    @Column(name = "max_consecutive_days")
    private Integer maxConsecutiveDays;

    @Column(name = "requires_medical_certificate")
    private Boolean requiresMedicalCertificate = false;

    @Column(name = "medical_cert_after_days")
    private Integer medicalCertAfterDays;

    @Column(name = "requires_attachment")
    private Boolean requiresAttachment = false;

    @Column(name = "is_statutory")
    private Boolean isStatutory = false;

    @Column(name = "legal_reference", columnDefinition = "TEXT")
    private String legalReference;

    @Column(name = "can_carry_forward")
    private Boolean canCarryForward = false;

    @Column(name = "carry_forward_max_days")
    private Integer carryForwardMaxDays;

    @Column(name = "carry_forward_expiry_months")
    private Integer carryForwardExpiryMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "accrual_method", length = 30)
    private AccrualMethod accrualMethod;

    @Column(name = "accrual_rate", precision = 5, scale = 2)
    private BigDecimal accrualRate;

    @Column(name = "gender_specific", length = 10)
    private String genderSpecific;

    @Column(name = "min_service_months")
    private Integer minServiceMonths = 0;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
