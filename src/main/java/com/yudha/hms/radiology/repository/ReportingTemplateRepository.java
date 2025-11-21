package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.entity.ReportingTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ReportingTemplate entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface ReportingTemplateRepository extends JpaRepository<ReportingTemplate, UUID> {

    /**
     * Find by ID and not deleted
     */
    Optional<ReportingTemplate> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Find by template code
     */
    Optional<ReportingTemplate> findByTemplateCodeAndDeletedAtIsNull(String templateCode);

    /**
     * Find by examination
     */
    List<ReportingTemplate> findByExaminationIdAndIsActiveTrueAndDeletedAtIsNull(UUID examinationId);

    /**
     * Find default template for examination
     */
    @Query("SELECT t FROM ReportingTemplate t WHERE t.examination.id = :examinationId AND t.isDefault = true AND t.isActive = true AND t.deletedAt IS NULL")
    Optional<ReportingTemplate> findDefaultTemplateByExamination(@Param("examinationId") UUID examinationId);

    /**
     * Find all active templates
     */
    List<ReportingTemplate> findByIsActiveTrueAndDeletedAtIsNull();

    /**
     * Count templates by examination
     */
    long countByExaminationIdAndIsActiveTrueAndDeletedAtIsNull(UUID examinationId);
}
