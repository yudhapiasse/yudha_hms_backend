package com.yudha.hms.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Base Entity class for all HMS entities.
 * Provides common fields: UUID primary key, timestamps.
 *
 * Features:
 * - UUID as primary key (generated automatically)
 * - Created and updated timestamps
 * - Proper equals/hashCode based on ID
 * - Serializable for caching support
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key (UUID)
     * Generated automatically using uuid2 strategy (better performance than random UUID)
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    private UUID id;

    /**
     * Timestamp when the entity was created
     * Set automatically on insert
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last updated
     * Updated automatically on every update
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Pre-persist callback to set creation and update timestamps
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Pre-update callback to update the updated timestamp
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Equals method based on ID
     * Returns true only if IDs are equal and not null
     *
     * @param o object to compare
     * @return true if objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    /**
     * HashCode based on ID
     * Returns constant value if ID is null (before persistence)
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 31;
    }

    /**
     * String representation of the entity
     * Shows class name and ID
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return String.format("%s{id=%s}", getClass().getSimpleName(), id);
    }

    /**
     * Check if entity is new (not yet persisted)
     *
     * @return true if entity is new
     */
    @Transient
    public boolean isNew() {
        return this.id == null;
    }
}
