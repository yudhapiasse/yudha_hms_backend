package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.radiology.constant.reporting.AmendmentReason;
import com.yudha.hms.radiology.constant.reporting.AmendmentType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_amendment", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAmendment extends SoftDeletableEntity {

    @Column(name = "report_id", nullable = false)
    private UUID reportId;

    @Column(name = "amendment_number", nullable = false)
    private Integer amendmentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "amendment_reason", length = 50, nullable = false)
    private AmendmentReason amendmentReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "amendment_type", length = 30, nullable = false)
    private AmendmentType amendmentType;

    @Column(name = "original_findings", columnDefinition = "TEXT")
    private String originalFindings;

    @Column(name = "original_impression", columnDefinition = "TEXT")
    private String originalImpression;

    @Column(name = "amended_findings", columnDefinition = "TEXT")
    private String amendedFindings;

    @Column(name = "amended_impression", columnDefinition = "TEXT")
    private String amendedImpression;

    @Column(name = "amendment_notes", columnDefinition = "TEXT", nullable = false)
    private String amendmentNotes;

    @Column(name = "amended_by", nullable = false)
    private UUID amendedBy;

    @Column(name = "amended_at", nullable = false)
    private LocalDateTime amendedAt;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "referring_physician_notified")
    private Boolean referringPhysicianNotified = false;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "notification_method", length = 30)
    private String notificationMethod;

    @Column(name = "is_significant")
    private Boolean isSignificant = false;

    @Column(name = "significance_notes", columnDefinition = "TEXT")
    private String significanceNotes;
}
