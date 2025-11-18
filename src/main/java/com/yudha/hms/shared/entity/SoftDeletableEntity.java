package com.yudha.hms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Soft Deletable Entity class with soft delete support.
 * Extends AuditableEntity and adds soft delete functionality.
 *
 * Features:
 * - Inherits all fields from AuditableEntity (id, timestamps, audit fields, version)
 * - Adds deletedAt (timestamp when deleted)
 * - Adds deletedBy (who deleted the record)
 * - Automatic soft delete via @SQLDelete annotation
 * - Automatic filtering of deleted records via @Where clause
 *
 * Benefits of Soft Delete:
 * - Maintains data integrity and audit trail
 * - Allows recovery of deleted records
 * - Required for regulatory compliance (medical records must be kept)
 * - Prevents accidental data loss
 *
 * Usage:
 * <pre>
 * {@code
 * @Entity
 * @Table(name = "patient", schema = "patient_schema")
 * public class Patient extends SoftDeletableEntity {
 *     // Entity fields
 * }
 *
 * // Soft delete a patient
 * patient.softDelete("admin");
 * patientRepository.save(patient);
 *
 * // Or use repository.delete() - it will soft delete automatically
 * patientRepository.delete(patient);
 * }
 * </pre>
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@MappedSuperclass
@SQLDelete(sql = "UPDATE {h-schema}{h-table} SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
public abstract class SoftDeletableEntity extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Timestamp when the entity was soft deleted
     * NULL means the entity is active (not deleted)
     * Non-null means the entity has been soft deleted
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * User who soft deleted this entity
     * Stores username or user ID
     */
    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    /**
     * Check if entity is soft deleted
     *
     * @return true if entity is deleted
     */
    @Transient
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Check if entity is active (not deleted)
     *
     * @return true if entity is active
     */
    @Transient
    public boolean isActive() {
        return this.deletedAt == null;
    }

    /**
     * Soft delete this entity
     * Sets deletedAt to current timestamp and records who deleted it
     *
     * Note: You must still call repository.save() or repository.delete()
     * to persist the soft delete
     *
     * @param deletedBy username or user ID who is deleting
     */
    public void softDelete(String deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Restore a soft-deleted entity
     * Clears deletedAt and deletedBy fields
     *
     * Note: You must call repository.save() to persist the restoration
     */
    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }

    /**
     * Pre-remove callback
     * This is called when repository.delete() is invoked
     * We override it to perform soft delete instead of hard delete
     */
    @PreRemove
    protected void onDelete() {
        // This will trigger the @SQLDelete query
        // The query will set deleted_at to current timestamp
        // No additional action needed here
    }

    /**
     * String representation including soft delete info
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format(
            "%s{id=%s, createdBy='%s', updatedBy='%s', version=%d, deleted=%b}",
            getClass().getSimpleName(),
            getId(),
            getCreatedBy(),
            getUpdatedBy(),
            getVersion() != null ? getVersion() : 0,
            isDeleted()
        );
    }
}