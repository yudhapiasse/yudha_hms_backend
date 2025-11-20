package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Checklist Item Entity.
 * Individual items in a pre-procedure checklist.
 *
 * @author HMS Development Team
 * @since 2025-01-20
 */
@Entity
@Table(name = "checklist_items", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_checklist_item_checklist", columnList = "checklist_id"),
        @Index(name = "idx_checklist_item_order", columnList = "checklist_id, item_order")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Individual checklist items")
public class ChecklistItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private PreProcedureChecklist checklist;

    @Column(name = "item_order")
    private Integer itemOrder;

    @Column(name = "item_text", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String itemText;

    @Column(name = "category", length = 50)
    private String category; // PATIENT_ID, CONSENT, ALLERGIES, NPO_STATUS, etc.

    @Column(name = "is_checked")
    @Builder.Default
    private Boolean isChecked = false;

    @Column(name = "is_required")
    @Builder.Default
    private Boolean isRequired = true;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "checked_by_id")
    private UUID checkedById;

    @Column(name = "checked_by_name", length = 200)
    private String checkedByName;

    @Column(name = "response_value", length = 500)
    private String responseValue; // For items requiring values (e.g., vital signs)

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public void check(UUID userId, String userName) {
        this.isChecked = true;
        this.checkedAt = LocalDateTime.now();
        this.checkedById = userId;
        this.checkedByName = userName;
    }
}
