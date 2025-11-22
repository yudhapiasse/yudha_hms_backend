package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.BankFileFormat;
import com.yudha.hms.workforce.constant.TransferBatchStatus;
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
@Table(name = "bank_transfer_batch", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class BankTransferBatch extends SoftDeletableEntity {

    @Column(name = "payroll_period_id", nullable = false)
    private UUID payrollPeriodId;

    @Column(name = "batch_number", length = 50, nullable = false, unique = true)
    private String batchNumber;

    @Column(name = "batch_date", nullable = false)
    private LocalDate batchDate;

    @Column(name = "total_employees", nullable = false)
    private Integer totalEmployees;

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_format", length = 30, nullable = false)
    private BankFileFormat fileFormat;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    private TransferBatchStatus status = TransferBatchStatus.GENERATED;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "processed_by")
    private UUID processedBy;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_code", length = 20)
    private String bankCode;

    @Column(name = "company_account_number", length = 50)
    private String companyAccountNumber;

    @Column(name = "company_account_name", length = 200)
    private String companyAccountName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
