package com.yudha.hms.radiology.service.reporting;

import com.yudha.hms.radiology.constant.reporting.ReportTemplateType;
import com.yudha.hms.radiology.entity.reporting.ReportTemplate;
import com.yudha.hms.radiology.repository.reporting.ReportTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportTemplateService {

    private final ReportTemplateRepository reportTemplateRepository;

    @Transactional
    public ReportTemplate createTemplate(ReportTemplate template) {
        log.info("Creating report template: {}", template.getTemplateCode());
        
        if (reportTemplateRepository.existsByTemplateCode(template.getTemplateCode())) {
            throw new IllegalArgumentException("Template code already exists: " + template.getTemplateCode());
        }
        
        return reportTemplateRepository.save(template);
    }

    @Transactional(readOnly = true)
    public ReportTemplate getTemplateById(UUID id) {
        return reportTemplateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + id));
    }

    @Transactional(readOnly = true)
    public ReportTemplate getTemplateByCode(String templateCode) {
        return reportTemplateRepository.findByTemplateCode(templateCode)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateCode));
    }

    @Transactional(readOnly = true)
    public List<ReportTemplate> getTemplatesByType(ReportTemplateType type) {
        return reportTemplateRepository.findByTemplateTypeAndIsActiveTrue(type);
    }

    @Transactional(readOnly = true)
    public List<ReportTemplate> getTemplatesByModality(String modalityCode) {
        return reportTemplateRepository.findByModalityCodeAndIsActiveTrue(modalityCode);
    }

    @Transactional(readOnly = true)
    public List<ReportTemplate> getTemplatesByModalityAndType(String modalityCode, ReportTemplateType type) {
        return reportTemplateRepository.findByModalityCodeAndTemplateTypeAndIsActiveTrue(modalityCode, type);
    }

    @Transactional(readOnly = true)
    public List<ReportTemplate> getAllActiveTemplates() {
        return reportTemplateRepository.findByIsActiveTrueOrderByTemplateNameAsc();
    }

    @Transactional
    public ReportTemplate updateTemplate(UUID id, ReportTemplate updatedTemplate) {
        log.info("Updating report template: {}", id);
        
        ReportTemplate existingTemplate = getTemplateById(id);
        
        existingTemplate.setTemplateName(updatedTemplate.getTemplateName());
        existingTemplate.setTemplateType(updatedTemplate.getTemplateType());
        existingTemplate.setModalityCode(updatedTemplate.getModalityCode());
        existingTemplate.setBodyPart(updatedTemplate.getBodyPart());
        existingTemplate.setTemplateStructure(updatedTemplate.getTemplateStructure());
        existingTemplate.setDefaultSections(updatedTemplate.getDefaultSections());
        existingTemplate.setMacros(updatedTemplate.getMacros());
        existingTemplate.setCommonFindings(updatedTemplate.getCommonFindings());
        existingTemplate.setIsActive(updatedTemplate.getIsActive());
        
        return reportTemplateRepository.save(existingTemplate);
    }

    @Transactional
    public void activateTemplate(UUID id) {
        log.info("Activating template: {}", id);
        ReportTemplate template = getTemplateById(id);
        template.setIsActive(true);
        reportTemplateRepository.save(template);
    }

    @Transactional
    public void deactivateTemplate(UUID id) {
        log.info("Deactivating template: {}", id);
        ReportTemplate template = getTemplateById(id);
        template.setIsActive(false);
        reportTemplateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(UUID id) {
        log.info("Soft deleting template: {}", id);
        ReportTemplate template = getTemplateById(id);
        template.setDeletedAt(LocalDateTime.now());
        reportTemplateRepository.save(template);
    }
}
