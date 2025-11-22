package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.CalculationMethod;
import com.yudha.hms.workforce.constant.PayrollComponentType;
import com.yudha.hms.workforce.constant.PercentageBase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "payroll_component", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class PayrollComponent extends SoftDeletableEntity {

    @Column(name = "component_code", length = 50, nullable = false, unique = true)
    private String componentCode;

    @Column(name = "component_name", length = 200, nullable = false)
    private String componentName;

    @Column(name = "component_name_id", length = 200, nullable = false)
    private String componentNameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", length = 30, nullable = false)
    private PayrollComponentType componentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_method", length = 30, nullable = false)
    private CalculationMethod calculationMethod;

    @Column(name = "is_taxable", nullable = false)
    private Boolean isTaxable = true;

    @Column(name = "is_bpjs_kesehatan_included", nullable = false)
    private Boolean isBpjsKesehatanIncluded = true;

    @Column(name = "is_bpjs_tk_included", nullable = false)
    private Boolean isBpjsTkIncluded = true;

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = true;

    @Column(name = "is_prorated", nullable = false)
    private Boolean isProrated = false;

    @Column(name = "default_amount", precision = 15, scale = 2)
    private BigDecimal defaultAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "percentage_base", length = 30)
    private PercentageBase percentageBase;

    @Column(name = "percentage_value", precision = 5, scale = 2)
    private BigDecimal percentageValue;

    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder = 100;

    @Column(name = "gl_account_code", length = 50)
    private String glAccountCode;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
