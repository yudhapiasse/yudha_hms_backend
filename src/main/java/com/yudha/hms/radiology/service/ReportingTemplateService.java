package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.entity.RadiologyExamination;
import com.yudha.hms.radiology.entity.ReportingTemplate;
import com.yudha.hms.radiology.repository.RadiologyExaminationRepository;
import com.yudha.hms.radiology.repository.ReportingTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for Reporting Template operations.
 *
 * Handles CRUD operations and business logic for radiology report templates.
 *
 * Features:
 * - CRUD operations with validation
 * - Get templates by examination
 * - Get default template for examination
 * - Clone template
 * - Set as default template
 * - Activate/deactivate template
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportingTemplateService {

    private final ReportingTemplateRepository templateRepository;
    private final RadiologyExaminationRepository examinationRepository;

    /**
     * Create a new reporting template.
     *
     * @param template Template to create
     * @return Created template
     * @throws IllegalArgumentException if template code exists or examination not found
     */
    public ReportingTemplate createTemplate(ReportingTemplate template) {
        log.info("Creating new reporting template: {}", template.getTemplateName());

        // Validate examination exists
        if (template.getExamination() == null || template.getExamination().getId() == null) {
            throw new IllegalArgumentException("Examination is required for template");
        }
        examinationRepository.findByIdAndDeletedAtIsNull(template.getExamination().getId())
                .orElseThrow(() -> new IllegalArgumentException("Examination not found: " + template.getExamination().getId()));

        // Validate template code uniqueness
        if (templateRepository.findByTemplateCodeAndDeletedAtIsNull(template.getTemplateCode()).isPresent()) {
            throw new IllegalArgumentException("Template code already exists: " + template.getTemplateCode());
        }

        // Set defaults
        if (template.getIsActive() == null) {
            template.setIsActive(true);
        }
        if (template.getIsDefault() == null) {
            template.setIsDefault(false);
        }
        if (template.getSections() == null) {
            template.setSections(new HashMap<>());
        }

        // If this is marked as default, unset other defaults for the same examination
        if (Boolean.TRUE.equals(template.getIsDefault())) {
            unsetDefaultTemplates(template.getExamination().getId());
        }

        ReportingTemplate saved = templateRepository.save(template);
        log.info("Template created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Update an existing reporting template.
     *
     * @param id ID of template to update
     * @param templateUpdate Updated template data
     * @return Updated template
     * @throws IllegalArgumentException if template not found or code exists
     */
    public ReportingTemplate updateTemplate(UUID id, ReportingTemplate templateUpdate) {
        log.info("Updating reporting template: {}", id);

        ReportingTemplate existing = templateRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with ID: " + id));

        // Check template code uniqueness if changed
        if (!existing.getTemplateCode().equals(templateUpdate.getTemplateCode())) {
            if (templateRepository.findByTemplateCodeAndDeletedAtIsNull(templateUpdate.getTemplateCode()).isPresent()) {
                throw new IllegalArgumentException("Template code already exists: " + templateUpdate.getTemplateCode());
            }
        }

        // Update fields
        existing.setTemplateCode(templateUpdate.getTemplateCode());
        existing.setTemplateName(templateUpdate.getTemplateName());
        existing.setSections(templateUpdate.getSections());
        existing.setIsActive(templateUpdate.getIsActive());

        // Update examination if changed
        if (templateUpdate.getExamination() != null &&
                !existing.getExamination().getId().equals(templateUpdate.getExamination().getId())) {
            RadiologyExamination newExamination = examinationRepository.findByIdAndDeletedAtIsNull(templateUpdate.getExamination().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Examination not found: " + templateUpdate.getExamination().getId()));
            existing.setExamination(newExamination);
        }

        // Handle default status change
        if (Boolean.TRUE.equals(templateUpdate.getIsDefault()) && !Boolean.TRUE.equals(existing.getIsDefault())) {
            unsetDefaultTemplates(existing.getExamination().getId());
            existing.setIsDefault(true);
        } else if (Boolean.FALSE.equals(templateUpdate.getIsDefault())) {
            existing.setIsDefault(false);
        }

        ReportingTemplate updated = templateRepository.save(existing);
        log.info("Template updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) a reporting template.
     *
     * @param id ID of template to delete
     * @param deletedBy User ID who performed deletion
     * @throws IllegalArgumentException if template not found
     */
    public void deleteTemplate(UUID id, String deletedBy) {
        log.info("Deleting reporting template: {}", id);

        ReportingTemplate template = templateRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with ID: " + id));

        // Soft delete
        template.setDeletedAt(LocalDateTime.now());
        template.setDeletedBy(deletedBy);
        templateRepository.save(template);

        log.info("Template deleted successfully: {}", id);
    }

    /**
     * Get template by ID.
     *
     * @param id Template ID
     * @return Template
     * @throws IllegalArgumentException if template not found
     */
    @Transactional(readOnly = true)
    public ReportingTemplate getTemplateById(UUID id) {
        return templateRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with ID: " + id));
    }

    /**
     * Get template by code.
     *
     * @param templateCode Template code
     * @return Template
     * @throws IllegalArgumentException if template not found
     */
    @Transactional(readOnly = true)
    public ReportingTemplate getTemplateByCode(String templateCode) {
        return templateRepository.findByTemplateCodeAndDeletedAtIsNull(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template not found with code: " + templateCode));
    }

    /**
     * Get all active templates.
     *
     * @return List of active templates
     */
    @Transactional(readOnly = true)
    public List<ReportingTemplate> getAllActiveTemplates() {
        return templateRepository.findByIsActiveTrueAndDeletedAtIsNull();
    }

    /**
     * Get templates by examination.
     *
     * @param examinationId Examination ID
     * @return List of active templates for the examination
     */
    @Transactional(readOnly = true)
    public List<ReportingTemplate> getTemplatesByExamination(UUID examinationId) {
        return templateRepository.findByExaminationIdAndIsActiveTrueAndDeletedAtIsNull(examinationId);
    }

    /**
     * Get default template for examination.
     *
     * @param examinationId Examination ID
     * @return Default template if exists
     */
    @Transactional(readOnly = true)
    public ReportingTemplate getDefaultTemplateForExamination(UUID examinationId) {
        return templateRepository.findDefaultTemplateByExamination(examinationId)
                .orElseThrow(() -> new IllegalArgumentException("No default template found for examination: " + examinationId));
    }

    /**
     * Clone an existing template.
     * Creates a new template with the same content but different code and name.
     *
     * @param templateId Template ID to clone
     * @param newCode New template code
     * @param newName New template name
     * @return Cloned template
     * @throws IllegalArgumentException if template not found or new code exists
     */
    public ReportingTemplate cloneTemplate(UUID templateId, String newCode, String newName) {
        log.info("Cloning template: {} to new code: {}", templateId, newCode);

        ReportingTemplate original = getTemplateById(templateId);

        // Validate new code doesn't exist
        if (templateRepository.findByTemplateCodeAndDeletedAtIsNull(newCode).isPresent()) {
            throw new IllegalArgumentException("Template code already exists: " + newCode);
        }

        // Clone the sections map
        Map<String, String> clonedSections = new HashMap<>();
        if (original.getSections() != null) {
            clonedSections.putAll(original.getSections());
        }

        // Create new template
        ReportingTemplate cloned = ReportingTemplate.builder()
                .examination(original.getExamination())
                .templateCode(newCode)
                .templateName(newName)
                .sections(clonedSections)
                .isDefault(false)  // Cloned templates are not default
                .isActive(true)
                .build();

        ReportingTemplate saved = templateRepository.save(cloned);
        log.info("Template cloned successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Set a template as the default for its examination.
     * Unsets any other default templates for the same examination.
     *
     * @param templateId Template ID
     * @return Updated template
     * @throws IllegalArgumentException if template not found
     */
    public ReportingTemplate setAsDefaultTemplate(UUID templateId) {
        log.info("Setting template as default: {}", templateId);

        ReportingTemplate template = getTemplateById(templateId);

        // Unset other defaults for the same examination
        unsetDefaultTemplates(template.getExamination().getId());

        // Set this as default
        template.setIsDefault(true);

        ReportingTemplate updated = templateRepository.save(template);
        log.info("Template set as default: {}", templateId);
        return updated;
    }

    /**
     * Activate a template.
     *
     * @param id Template ID
     * @return Updated template
     * @throws IllegalArgumentException if template not found
     */
    public ReportingTemplate activateTemplate(UUID id) {
        log.info("Activating template: {}", id);

        ReportingTemplate template = getTemplateById(id);
        template.setIsActive(true);

        ReportingTemplate updated = templateRepository.save(template);
        log.info("Template activated successfully: {}", id);
        return updated;
    }

    /**
     * Deactivate a template.
     * If this is the default template, it will also be unmarked as default.
     *
     * @param id Template ID
     * @return Updated template
     * @throws IllegalArgumentException if template not found
     */
    public ReportingTemplate deactivateTemplate(UUID id) {
        log.info("Deactivating template: {}", id);

        ReportingTemplate template = getTemplateById(id);
        template.setIsActive(false);

        // If this was the default, unset it
        if (Boolean.TRUE.equals(template.getIsDefault())) {
            template.setIsDefault(false);
            log.info("Template was default, unmarking as default: {}", id);
        }

        ReportingTemplate updated = templateRepository.save(template);
        log.info("Template deactivated successfully: {}", id);
        return updated;
    }

    /**
     * Count templates by examination.
     *
     * @param examinationId Examination ID
     * @return Count of active templates
     */
    @Transactional(readOnly = true)
    public long countTemplatesByExamination(UUID examinationId) {
        return templateRepository.countByExaminationIdAndIsActiveTrueAndDeletedAtIsNull(examinationId);
    }

    /**
     * Check if a template code exists.
     *
     * @param templateCode Template code
     * @return True if code exists
     */
    @Transactional(readOnly = true)
    public boolean templateCodeExists(String templateCode) {
        return templateRepository.findByTemplateCodeAndDeletedAtIsNull(templateCode).isPresent();
    }

    /**
     * Unset default status for all templates of an examination.
     * Helper method used when setting a new default template.
     *
     * @param examinationId Examination ID
     */
    private void unsetDefaultTemplates(UUID examinationId) {
        List<ReportingTemplate> templates = templateRepository.findByExaminationIdAndIsActiveTrueAndDeletedAtIsNull(examinationId);
        for (ReportingTemplate template : templates) {
            if (Boolean.TRUE.equals(template.getIsDefault())) {
                template.setIsDefault(false);
                templateRepository.save(template);
            }
        }
    }
}
