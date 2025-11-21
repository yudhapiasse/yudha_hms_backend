package com.yudha.hms.radiology.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.radiology.dto.request.ReportingTemplateRequest;
import com.yudha.hms.radiology.dto.response.ApiResponse;
import com.yudha.hms.radiology.dto.response.ReportingTemplateResponse;
import com.yudha.hms.radiology.entity.RadiologyExamination;
import com.yudha.hms.radiology.entity.ReportingTemplate;
import com.yudha.hms.radiology.service.ReportingTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Reporting Template Controller.
 *
 * REST controller for managing radiology reporting templates.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/radiology/templates")
@RequiredArgsConstructor
@Slf4j
public class ReportingTemplateController {

    private final ReportingTemplateService templateService;
    private final ObjectMapper objectMapper;

    /**
     * Create new reporting template
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReportingTemplateResponse>> createTemplate(
            @Valid @RequestBody ReportingTemplateRequest request) {
        log.info("Creating reporting template: {}", request.getTemplateName());

        ReportingTemplate template = convertToEntity(request);
        ReportingTemplate savedTemplate = templateService.createTemplate(template);
        ReportingTemplateResponse response = toResponse(savedTemplate);

        log.info("Reporting template created successfully: {}", savedTemplate.getTemplateCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Template created successfully", response));
    }

    /**
     * Update existing reporting template
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportingTemplateResponse>> updateTemplate(
            @PathVariable UUID id,
            @Valid @RequestBody ReportingTemplateRequest request) {
        log.info("Updating reporting template ID: {}", id);

        ReportingTemplate templateUpdate = convertToEntity(request);
        ReportingTemplate template = templateService.updateTemplate(id, templateUpdate);
        ReportingTemplateResponse response = toResponse(template);

        log.info("Reporting template updated successfully: {}", template.getTemplateCode());

        return ResponseEntity.ok(ApiResponse.success("Template updated successfully", response));
    }

    /**
     * Get reporting template by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportingTemplateResponse>> getTemplateById(
            @PathVariable UUID id) {
        log.info("Fetching reporting template ID: {}", id);

        ReportingTemplate template = templateService.getTemplateById(id);
        ReportingTemplateResponse response = toResponse(template);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get all templates
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReportingTemplateResponse>>> getAllTemplates() {
        log.info("Fetching all active reporting templates");

        List<ReportingTemplate> templates = templateService.getAllActiveTemplates();
        List<ReportingTemplateResponse> responses = templates.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get templates by examination
     */
    @GetMapping("/examination/{examinationId}")
    public ResponseEntity<ApiResponse<List<ReportingTemplateResponse>>> getTemplatesByExamination(
            @PathVariable UUID examinationId) {
        log.info("Fetching templates for examination ID: {}", examinationId);

        List<ReportingTemplate> templates = templateService.getTemplatesByExamination(examinationId);
        List<ReportingTemplateResponse> responses = templates.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get default template for examination
     */
    @GetMapping("/examination/{examinationId}/default")
    public ResponseEntity<ApiResponse<ReportingTemplateResponse>> getDefaultTemplateForExamination(
            @PathVariable UUID examinationId) {
        log.info("Fetching default template for examination ID: {}", examinationId);

        ReportingTemplate template = templateService.getDefaultTemplateForExamination(examinationId);
        ReportingTemplateResponse response = toResponse(template);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Clone template
     */
    @PostMapping("/{id}/clone")
    public ResponseEntity<ApiResponse<ReportingTemplateResponse>> cloneTemplate(
            @PathVariable UUID id,
            @RequestParam String newCode,
            @RequestParam String newName) {
        log.info("Cloning template ID: {} to new code: {}", id, newCode);

        ReportingTemplate clonedTemplate = templateService.cloneTemplate(id, newCode, newName);
        ReportingTemplateResponse response = toResponse(clonedTemplate);

        log.info("Template cloned successfully: {}", newCode);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Template cloned successfully", response));
    }

    /**
     * Set template as default
     */
    @PatchMapping("/{id}/set-default")
    public ResponseEntity<ApiResponse<ReportingTemplateResponse>> setAsDefaultTemplate(
            @PathVariable UUID id) {
        log.info("Setting template as default ID: {}", id);

        ReportingTemplate template = templateService.setAsDefaultTemplate(id);
        ReportingTemplateResponse response = toResponse(template);

        log.info("Template set as default successfully: {}", template.getTemplateCode());

        return ResponseEntity.ok(ApiResponse.success("Template set as default successfully", response));
    }

    /**
     * Soft delete reporting template
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @PathVariable UUID id) {
        log.info("Deleting reporting template ID: {}", id);

        templateService.deleteTemplate(id, "SYSTEM");
        log.info("Reporting template deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Template deleted successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private ReportingTemplateResponse toResponse(ReportingTemplate template) {
        ReportingTemplateResponse response = new ReportingTemplateResponse();
        response.setId(template.getId());

        // Examination information
        if (template.getExamination() != null) {
            response.setExaminationId(template.getExamination().getId());
            response.setExaminationCode(template.getExamination().getExamCode());
            response.setExaminationName(template.getExamination().getExamName());
        }

        response.setTemplateName(template.getTemplateName());
        response.setTemplateCode(template.getTemplateCode());

        // Convert sections Map to JSON string
        if (template.getSections() != null) {
            try {
                response.setSections(objectMapper.writeValueAsString(template.getSections()));
            } catch (JsonProcessingException e) {
                log.error("Error converting sections to JSON", e);
                response.setSections("{}");
            }
        }

        response.setIsDefault(template.getIsDefault());
        response.setIsActive(template.getIsActive());

        // Audit fields
        response.setCreatedAt(template.getCreatedAt());
        response.setCreatedBy(template.getCreatedBy());
        response.setUpdatedAt(template.getUpdatedAt());
        response.setUpdatedBy(template.getUpdatedBy());

        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private ReportingTemplate convertToEntity(ReportingTemplateRequest request) {
        ReportingTemplate template = new ReportingTemplate();
        template.setTemplateName(request.getTemplateName());
        template.setTemplateCode(request.getTemplateCode());

        // Convert JSON string to Map for sections
        if (request.getSections() != null && !request.getSections().isEmpty()) {
            try {
                Map<String, String> sectionsMap = objectMapper.readValue(
                        request.getSections(),
                        new TypeReference<Map<String, String>>() {});
                template.setSections(sectionsMap);
            } catch (JsonProcessingException e) {
                log.error("Error parsing sections JSON", e);
                template.setSections(new HashMap<>());
            }
        } else {
            template.setSections(new HashMap<>());
        }

        template.setIsDefault(request.getIsDefault());
        template.setIsActive(request.getIsActive());

        // Set examination - need to create a minimal examination object with just the ID
        if (request.getExaminationId() != null) {
            RadiologyExamination examination = new RadiologyExamination();
            examination.setId(request.getExaminationId());
            template.setExamination(examination);
        }

        return template;
    }
}
