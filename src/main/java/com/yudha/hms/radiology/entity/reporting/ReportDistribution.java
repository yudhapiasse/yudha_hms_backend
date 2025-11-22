package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.radiology.constant.reporting.DistributionMethod;
import com.yudha.hms.radiology.constant.reporting.DistributionStatus;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_distribution", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDistribution extends SoftDeletableEntity {

    @Column(name = "report_id", nullable = false)
    private UUID reportId;

    @Column(name = "recipient_type", length = 50, nullable = false)
    private String recipientType;

    @Column(name = "recipient_id")
    private UUID recipientId;

    @Column(name = "recipient_name", length = 200, nullable = false)
    private String recipientName;

    @Column(name = "recipient_email", length = 200)
    private String recipientEmail;

    @Column(name = "recipient_fax", length = 50)
    private String recipientFax;

    @Enumerated(EnumType.STRING)
    @Column(name = "distribution_method", length = 30, nullable = false)
    private DistributionMethod distributionMethod;

    @Column(name = "distribution_format", length = 20)
    private String distributionFormat;

    @Column(name = "distribution_priority", length = 20)
    private String distributionPriority = "NORMAL";

    @Enumerated(EnumType.STRING)
    @Column(name = "distribution_status", length = 30, nullable = false)
    private DistributionStatus distributionStatus = DistributionStatus.PENDING;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "delivery_confirmed")
    private Boolean deliveryConfirmed = false;

    @Column(name = "delivery_confirmation_method", length = 30)
    private String deliveryConfirmationMethod;

    @Column(name = "read_receipt")
    private Boolean readReceipt = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "failed")
    private Boolean failed = false;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "last_retry_at")
    private LocalDateTime lastRetryAt;

    @Column(name = "max_retries")
    private Integer maxRetries = 3;

    @Column(name = "include_images")
    private Boolean includeImages = false;

    @Column(name = "include_measurements")
    private Boolean includeMeasurements = true;

    @Column(name = "include_comparisons")
    private Boolean includeComparisons = true;

    @Column(name = "watermark")
    private Boolean watermark = false;

    @Column(name = "encrypted")
    private Boolean encrypted = false;

    @Column(name = "password_protected")
    private Boolean passwordProtected = false;

    @Column(name = "access_code", length = 50)
    private String accessCode;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "portal_access_link", length = 500)
    private String portalAccessLink;

    @Column(name = "portal_accessed")
    private Boolean portalAccessed = false;

    @Column(name = "portal_accessed_at")
    private LocalDateTime portalAccessedAt;

    @Column(name = "auto_distribution")
    private Boolean autoDistribution = false;

    @Column(name = "distribution_rule_id")
    private UUID distributionRuleId;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
