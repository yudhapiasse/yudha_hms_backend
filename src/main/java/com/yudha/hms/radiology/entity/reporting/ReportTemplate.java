package com.yudha.hms.radiology.entity.reporting;

import com.yudha.hms.radiology.constant.reporting.ReportTemplateType;
import com.yudha.hms.shared.entity.SoftDeletableEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_template", schema = "radiology_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportTemplate extends SoftDeletableEntity {

    @Column(name = "template_code", length = 50, nullable = false, unique = true)
    private String templateCode;

    @Column(name = "template_name", length = 200, nullable = false)
    private String templateName;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_type", length = 50, nullable = false)
    private ReportTemplateType templateType;

    @Column(name = "modality_code", length = 10, nullable = false)
    private String modalityCode;

    @Column(name = "body_part", length = 100)
    private String bodyPart;

    @Column(name = "procedure_type", length = 100)
    private String procedureType;

    @Type(JsonBinaryType.class)
    @Column(name = "template_structure", columnDefinition = "jsonb", nullable = false)
    private Object templateStructure;

    @Type(JsonBinaryType.class)
    @Column(name = "default_sections", columnDefinition = "jsonb")
    private Object defaultSections;

    @Column(name = "required_fields", columnDefinition = "text[]")
    private String[] requiredFields;

    @Type(JsonBinaryType.class)
    @Column(name = "macros", columnDefinition = "jsonb")
    private Object macros;

    @Type(JsonBinaryType.class)
    @Column(name = "common_findings", columnDefinition = "jsonb")
    private Object commonFindings;

    @Column(name = "usage_count")
    private Integer usageCount = 0;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "version_number")
    private Integer versionNumber = 1;

    @Column(name = "parent_template_id")
    private UUID parentTemplateId;

    @Column(name = "created_by_radiologist")
    private UUID createdByRadiologist;

    @Column(name = "approved_by")
    private UUID approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
