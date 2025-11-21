package com.yudha.hms.radiology.entity;

import com.yudha.hms.radiology.constant.SharePurpose;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Image Share Link Entity.
 *
 * Represents shareable links for external image access.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Entity
@Table(name = "image_share_link", schema = "radiology_schema", indexes = {
        @Index(name = "idx_share_link_study", columnList = "study_id"),
        @Index(name = "idx_share_link_token", columnList = "share_token"),
        @Index(name = "idx_share_link_expires", columnList = "expires_at"),
        @Index(name = "idx_share_link_active", columnList = "is_active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageShareLink extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private PacsStudy study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private RadiologyOrder order;

    // Share link details
    @Column(name = "share_token", nullable = false, unique = true, length = 100)
    private String shareToken;

    @Column(name = "share_url", length = 500)
    private String shareUrl;

    // Access control
    @Column(name = "password_protected")
    @Builder.Default
    private Boolean passwordProtected = false;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    // Expiration
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "max_views")
    private Integer maxViews;

    @Column(name = "current_views")
    @Builder.Default
    private Integer currentViews = 0;

    // Allowed features
    @Column(name = "allow_download")
    @Builder.Default
    private Boolean allowDownload = false;

    @Column(name = "allow_print")
    @Builder.Default
    private Boolean allowPrint = false;

    @Column(name = "allow_share")
    @Builder.Default
    private Boolean allowShare = false;

    // Recipient information
    @Column(name = "recipient_email", length = 200)
    private String recipientEmail;

    @Column(name = "recipient_name", length = 200)
    private String recipientName;

    @Column(name = "recipient_organization", length = 200)
    private String recipientOrganization;

    // Purpose
    @Enumerated(EnumType.STRING)
    @Column(name = "share_purpose", length = 50)
    private SharePurpose sharePurpose;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Status
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "revoked")
    @Builder.Default
    private Boolean revoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "revoked_by")
    private UUID revokedBy;

    @Column(name = "revoke_reason", columnDefinition = "TEXT")
    private String revokeReason;

    // Access tracking
    @Column(name = "first_accessed_at")
    private LocalDateTime firstAccessedAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "access_log", columnDefinition = "jsonb")
    private Map<String, Object> accessLog;
}
