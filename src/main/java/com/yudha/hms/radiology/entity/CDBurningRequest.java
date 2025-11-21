package com.yudha.hms.radiology.entity;

import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.radiology.constant.CDRequestStatus;
import com.yudha.hms.radiology.constant.CDRequestType;
import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CD Burning Request Entity.
 *
 * Represents CD/DVD burning requests for patient images.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Entity
@Table(name = "cd_burning_request", schema = "radiology_schema", indexes = {
        @Index(name = "idx_cd_burning_request_number", columnList = "request_number"),
        @Index(name = "idx_cd_burning_patient", columnList = "patient_id"),
        @Index(name = "idx_cd_burning_status", columnList = "status"),
        @Index(name = "idx_cd_burning_requested_by", columnList = "requested_by")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CDBurningRequest extends SoftDeletableEntity {

    // Request details
    @Column(name = "request_number", nullable = false, unique = true, length = 50)
    private String requestNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", length = 20)
    @Builder.Default
    private CDRequestType requestType = CDRequestType.PATIENT_CD;

    // Studies to burn (stored as array in database)
    @Column(name = "study_ids", nullable = false, columnDefinition = "UUID[]")
    private UUID[] studyIds;

    @Column(name = "order_ids", columnDefinition = "UUID[]")
    private UUID[] orderIds;

    // Patient information
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "patient_name", length = 200)
    private String patientName;

    // CD details
    @Column(name = "cd_label", length = 200)
    private String cdLabel;

    @Column(name = "include_viewer")
    @Builder.Default
    private Boolean includeViewer = true;

    @Column(name = "viewer_type", length = 50)
    private String viewerType;

    @Column(name = "include_reports")
    @Builder.Default
    private Boolean includeReports = true;

    // Format
    @Column(name = "output_format", length = 20)
    @Builder.Default
    private String outputFormat = "DICOM";

    @Column(name = "anonymize")
    @Builder.Default
    private Boolean anonymize = false;

    @Column(name = "compress")
    @Builder.Default
    private Boolean compress = false;

    // Request information
    @Column(name = "requested_by", nullable = false)
    private UUID requestedBy;

    @Column(name = "requested_for", length = 200)
    private String requestedFor;

    @Column(name = "request_reason", columnDefinition = "TEXT")
    private String requestReason;

    // Status tracking
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private CDRequestStatus status = CDRequestStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    @Builder.Default
    private OrderPriority priority = OrderPriority.ROUTINE;

    // Processing
    @Column(name = "assigned_to")
    private UUID assignedTo;

    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;

    // Completion
    @Column(name = "cd_count")
    @Builder.Default
    private Integer cdCount = 1;

    @Column(name = "burned_at")
    private LocalDateTime burnedAt;

    @Column(name = "burned_by")
    private UUID burnedBy;

    // Delivery
    @Column(name = "delivery_method", length = 50)
    private String deliveryMethod;

    @Column(name = "delivered_to", length = 200)
    private String deliveredTo;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "delivery_notes", columnDefinition = "TEXT")
    private String deliveryNotes;

    // Error handling
    @Column(name = "failed")
    @Builder.Default
    private Boolean failed = false;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "retry_count")
    @Builder.Default
    private Integer retryCount = 0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
