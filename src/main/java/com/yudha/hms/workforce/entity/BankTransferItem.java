package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bank_transfer_item", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class BankTransferItem extends SoftDeletableEntity {

    @Column(name = "bank_transfer_batch_id", nullable = false)
    private UUID bankTransferBatchId;

    @Column(name = "employee_payroll_id", nullable = false)
    private UUID employeePayrollId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @Column(name = "employee_number", length = 50)
    private String employeeNumber;

    @Column(name = "employee_name", length = 200, nullable = false)
    private String employeeName;

    @Column(name = "bank_code", length = 20)
    private String bankCode;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "account_number", length = 50, nullable = false)
    private String accountNumber;

    @Column(name = "account_holder_name", length = 200, nullable = false)
    private String accountHolderName;

    @Column(name = "transfer_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal transferAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transfer_date")
    private LocalDate transferDate;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
}
