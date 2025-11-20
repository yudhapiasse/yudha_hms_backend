package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.ClinicalNoteTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Clinical Note Template Repository.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface ClinicalNoteTemplateRepository extends JpaRepository<ClinicalNoteTemplate, UUID> {

    /**
     * Find template by template code.
     */
    Optional<ClinicalNoteTemplate> findByTemplateCode(String templateCode);

    /**
     * Check if template code exists.
     */
    boolean existsByTemplateCode(String templateCode);

    /**
     * Find active templates.
     */
    List<ClinicalNoteTemplate> findByIsActiveTrueOrderByTemplateNameAsc();

    /**
     * Find templates by type.
     */
    List<ClinicalNoteTemplate> findByTemplateTypeAndIsActiveTrueOrderByTemplateNameAsc(
        ClinicalNoteTemplate.TemplateType templateType
    );

    /**
     * Find templates by category.
     */
    List<ClinicalNoteTemplate> findByCategoryAndIsActiveTrueOrderByTemplateNameAsc(
        ClinicalNoteTemplate.TemplateCategory category
    );

    /**
     * Find templates by specialty.
     */
    List<ClinicalNoteTemplate> findBySpecialtyAndIsActiveTrueOrderByTemplateNameAsc(String specialty);

    /**
     * Find public templates.
     */
    List<ClinicalNoteTemplate> findByIsPublicTrueAndIsActiveTrueOrderByTemplateNameAsc();

    /**
     * Find templates by department.
     */
    List<ClinicalNoteTemplate> findByDepartmentIdAndIsActiveTrueOrderByTemplateNameAsc(UUID departmentId);

    /**
     * Find templates created by user.
     */
    List<ClinicalNoteTemplate> findByCreatedByIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find unapproved templates requiring approval.
     */
    @Query("SELECT t FROM ClinicalNoteTemplate t WHERE t.requiresApproval = true AND t.approved = false ORDER BY t.createdAt DESC")
    List<ClinicalNoteTemplate> findUnapprovedTemplates();

    /**
     * Find most used templates.
     */
    @Query("SELECT t FROM ClinicalNoteTemplate t WHERE t.isActive = true ORDER BY t.usageCount DESC")
    List<ClinicalNoteTemplate> findMostUsedTemplates();

    /**
     * Find templates ready for use (active, public or by department, approved if required).
     */
    @Query("SELECT t FROM ClinicalNoteTemplate t WHERE t.isActive = true " +
           "AND (t.isPublic = true OR t.departmentId = :departmentId) " +
           "AND (t.requiresApproval = false OR t.approved = true) " +
           "ORDER BY t.usageCount DESC, t.templateName ASC")
    List<ClinicalNoteTemplate> findReadyForUse(@Param("departmentId") UUID departmentId);

    /**
     * Search templates by name or description.
     */
    @Query("SELECT t FROM ClinicalNoteTemplate t WHERE t.isActive = true AND " +
           "(LOWER(t.templateName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY t.usageCount DESC")
    List<ClinicalNoteTemplate> searchTemplates(@Param("searchTerm") String searchTerm);

    /**
     * Find latest version of template.
     */
    @Query("SELECT t FROM ClinicalNoteTemplate t WHERE t.templateCode = :templateCode " +
           "AND t.isActive = true ORDER BY t.version DESC LIMIT 1")
    Optional<ClinicalNoteTemplate> findLatestVersionByCode(@Param("templateCode") String templateCode);

    /**
     * Count active templates.
     */
    long countByIsActiveTrue();

    /**
     * Count templates by specialty.
     */
    long countBySpecialtyAndIsActiveTrue(String specialty);
}
