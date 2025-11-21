package com.yudha.hms.radiology.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

/**
 * Reporting Template Entity.
 *
 * Report templates per examination
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Entity
@Table(name = "reporting_template", schema = "radiology_schema", indexes = {
        @Index(name = "idx_reporting_template_examination", columnList = "examination_id"),
        @Index(name = "idx_reporting_template_code", columnList = "template_code"),
        @Index(name = "idx_reporting_template_active", columnList = "is_active")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_reporting_template_exam_code", columnNames = {"examination_id", "template_code"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ReportingTemplate extends SoftDeletableEntity {

    /**
     * Examination reference
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "examination_id", nullable = false)
    private RadiologyExamination examination;

    /**
     * Template name
     */
    @Column(name = "template_name", nullable = false, length = 200)
    private String templateName;

    /**
     * Template code
     */
    @Column(name = "template_code", nullable = false, length = 50)
    private String templateCode;

    /**
     * Template sections (JSONB)
     * Format: {"findings": "template text", "impression": "template text", "recommendations": "template text"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sections", columnDefinition = "jsonb")
    private Map<String, String> sections;

    /**
     * Whether this is the default template
     */
    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Active status
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
