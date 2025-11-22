package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.PayrollComponentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payroll_item", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class PayrollItem extends SoftDeletableEntity {

    @Column(name = "employee_payroll_id", nullable = false)
    private UUID employeePayrollId;

    @Column(name = "payroll_component_id", nullable = false)
    private UUID payrollComponentId;

    @Column(name = "component_code", length = 50, nullable = false)
    private String componentCode;

    @Column(name = "component_name", length = 200, nullable = false)
    private String componentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", length = 30, nullable = false)
    private PayrollComponentType componentType;

    @Column(name = "calculation_base", precision = 15, scale = 2)
    private BigDecimal calculationBase;

    @Column(name = "rate", precision = 8, scale = 2)
    private BigDecimal rate;

    @Column(name = "quantity", precision = 8, scale = 2)
    private BigDecimal quantity = BigDecimal.ONE;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "is_taxable", nullable = false)
    private Boolean isTaxable = true;

    @Column(name = "is_bpjs_included", nullable = false)
    private Boolean isBpjsIncluded = true;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
