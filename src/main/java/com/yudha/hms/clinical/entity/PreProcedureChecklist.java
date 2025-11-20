package com.yudha.hms.clinical.entity;

import com.yudha.hms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pre-Procedure Checklist Entity.
 * Tracks pre-procedure verification and safety checks.
 *
 * @author HMS Development Team
 * @since 2025-01-20
 */
@Entity
@Table(name = "pre_procedure_checklists", schema = "clinical_schema",
    indexes = {
        @Index(name = "idx_pre_checklist_procedure", columnList = "encounter_procedure_id"),
        @Index(name = "idx_pre_checklist_schedule", columnList = "procedure_schedule_id"),
        @Index(name = "idx_pre_checklist_status", columnList = "completion_status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Comment("Pre-procedure safety checklists")
public class PreProcedureChecklist extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encounter_procedure_id")
    private EncounterProcedure encounterProcedure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedure_schedule_id")
    private ProcedureSchedule procedureSchedule;

    @Column(name = "patient_id", nullable = false)
    @NotNull
    private UUID patientId;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChecklistItem> items = new ArrayList<>();

    @Column(name = "completion_status", length = 20)
    @Builder.Default
    private String completionStatus = "INCOMPLETE"; // INCOMPLETE, PARTIAL, COMPLETE

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by_id")
    private UUID completedById;

    @Column(name = "completed_by_name", length = 200)
    private String completedByName;

    @Column(name = "verified_by_id")
    private UUID verifiedById;

    @Column(name = "verified_by_name", length = 200)
    private String verifiedByName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public void addItem(ChecklistItem item) {
        items.add(item);
        item.setChecklist(this);
    }

    public boolean isComplete() {
        return items.stream().allMatch(item -> item.getIsChecked());
    }

    public void complete(UUID userId, String userName) {
        this.completionStatus = "COMPLETE";
        this.completedAt = LocalDateTime.now();
        this.completedById = userId;
        this.completedByName = userName;
    }
}
