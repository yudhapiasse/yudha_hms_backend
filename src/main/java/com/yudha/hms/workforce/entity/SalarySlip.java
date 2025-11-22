package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "salary_slip", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class SalarySlip extends SoftDeletableEntity {

    @Column(name = "employee_payroll_id", nullable = false)
    private UUID employeePayrollId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "payroll_period_id", nullable = false)
    private UUID payrollPeriodId;

    @Column(name = "slip_number", length = 50, nullable = false, unique = true)
    private String slipNumber;

    @Column(name = "slip_date", nullable = false)
    private LocalDate slipDate;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "file_type", length = 20)
    private String fileType = "PDF";

    @Column(name = "is_sent", nullable = false)
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "sent_via", length = 30)
    private String sentVia;

    @Column(name = "sent_to", length = 200)
    private String sentTo;

    @Column(name = "is_downloaded", nullable = false)
    private Boolean isDownloaded = false;

    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "last_downloaded_at")
    private LocalDateTime lastDownloadedAt;
}
