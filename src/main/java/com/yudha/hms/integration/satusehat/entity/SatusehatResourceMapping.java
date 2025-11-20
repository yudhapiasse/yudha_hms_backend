package com.yudha.hms.integration.satusehat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * SATUSEHAT Resource Mapping Entity.
 *
 * Maps local HMS resources to SATUSEHAT FHIR resources.
 * Tracks submission status and maintains version control.
 *
 * Submission Status:
 * - PENDING: Not yet submitted to SATUSEHAT
 * - SUBMITTED: Successfully submitted and created
 * - UPDATED: Successfully updated
 * - FAILED: Submission or update failed
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Entity
@Table(name = "satusehat_resource_mappings",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_local_resource",
            columnNames = {"local_resource_type", "local_resource_id"}
        )
    },
    indexes = {
        @Index(name = "idx_satusehat_mapping_local",
               columnList = "local_resource_type, local_resource_id"),
        @Index(name = "idx_satusehat_mapping_remote",
               columnList = "satusehat_resource_type, satusehat_resource_id"),
        @Index(name = "idx_satusehat_mapping_status", columnList = "submission_status"),
        @Index(name = "idx_satusehat_mapping_identifier", columnList = "satusehat_identifier")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SatusehatResourceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "config_id")
    private SatusehatConfig config;

    // Local resource reference
    @Column(name = "local_resource_type", nullable = false, length = 100)
    private String localResourceType;

    @Column(name = "local_resource_id", nullable = false)
    private UUID localResourceId;

    // SATUSEHAT resource reference
    @Column(name = "satusehat_resource_type", nullable = false, length = 100)
    private String satusehatResourceType;

    @Column(name = "satusehat_resource_id", nullable = false, length = 100)
    private String satusehatResourceId;

    /**
     * Primary identifier in SATUSEHAT (IHS number, NIK, etc.)
     */
    @Column(name = "satusehat_identifier")
    private String satusehatIdentifier;

    /**
     * Submission status: PENDING, SUBMITTED, UPDATED, FAILED
     */
    @Column(name = "submission_status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private SubmissionStatus submissionStatus = SubmissionStatus.PENDING;

    /**
     * Last successful submission timestamp
     */
    @Column(name = "last_submitted_at")
    private LocalDateTime lastSubmittedAt;

    /**
     * Last error message (if any)
     */
    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    /**
     * FHIR resource version ID
     */
    @Column(name = "version_id", length = 100)
    private String versionId;

    /**
     * FHIR resource last updated timestamp
     */
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    /**
     * Cached FHIR resource snapshot (JSONB)
     */
    @Column(name = "resource_snapshot", columnDefinition = "jsonb")
    private String resourceSnapshot;

    // Audit fields
    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Submission status enumeration
     */
    public enum SubmissionStatus {
        PENDING,
        SUBMITTED,
        UPDATED,
        FAILED
    }

    /**
     * Mark as successfully submitted
     */
    public void markAsSubmitted(String satusehatResourceId, String versionId) {
        this.satusehatResourceId = satusehatResourceId;
        this.versionId = versionId;
        this.submissionStatus = SubmissionStatus.SUBMITTED;
        this.lastSubmittedAt = LocalDateTime.now();
        this.lastError = null;
    }

    /**
     * Mark as successfully updated
     */
    public void markAsUpdated(String versionId) {
        this.versionId = versionId;
        this.submissionStatus = SubmissionStatus.UPDATED;
        this.lastSubmittedAt = LocalDateTime.now();
        this.lastError = null;
    }

    /**
     * Mark as failed with error message
     */
    public void markAsFailed(String errorMessage) {
        this.submissionStatus = SubmissionStatus.FAILED;
        this.lastError = errorMessage;
    }
}
