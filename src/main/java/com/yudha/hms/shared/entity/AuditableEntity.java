package com.yudha.hms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Auditable Entity class with audit fields.
 * Extends BaseEntity and adds user tracking and optimistic locking.
 *
 * Features:
 * - Inherits UUID, createdAt, updatedAt from BaseEntity
 * - Adds createdBy (who created the record)
 * - Adds updatedBy (who last updated the record)
 * - Optimistic locking with @Version
 * - Automatic population via Spring Data JPA Auditing
 *
 * Usage:
 * Entities that need full audit trail should extend this class.
 *
 * Example:
 * <pre>
 * {@code
 * @Entity
 * @Table(name = "patient", schema = "patient_schema")
 * public class Patient extends AuditableEntity {
 *     // Entity fields
 * }
 * }
 * </pre>
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AuditableEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * User who created this entity
     * Populated automatically by Spring Data JPA Auditing
     * Stores username or user ID
     */
    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    /**
     * User who last updated this entity
     * Updated automatically by Spring Data JPA Auditing
     * Stores username or user ID
     */
    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    /**
     * Version field for optimistic locking
     * Prevents lost updates in concurrent transactions
     *
     * How it works:
     * 1. Entity is loaded with version = 1
     * 2. User A and User B load the same entity
     * 3. User A updates and saves (version incremented to 2)
     * 4. User B tries to save with version = 1
     * 5. OptimisticLockException is thrown for User B
     * 6. User B must reload and retry
     *
     * This ensures data integrity in multi-user environment
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * String representation including audit info
     *
     * @return string representation with audit fields
     */
    @Override
    public String toString() {
        return String.format(
            "%s{id=%s, createdBy='%s', updatedBy='%s', version=%d}",
            getClass().getSimpleName(),
            getId(),
            createdBy,
            updatedBy,
            version != null ? version : 0
        );
    }
}