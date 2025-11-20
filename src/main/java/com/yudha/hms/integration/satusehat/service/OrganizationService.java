package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Organization;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing Organization FHIR resources in SATUSEHAT.
 *
 * Handles:
 * - Organization CRUD operations
 * - Organization search
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ========================================================================
    // ORGANIZATION OPERATIONS
    // ========================================================================

    /**
     * Create an organization in SATUSEHAT.
     */
    public Organization createOrganization(String organizationId, Organization organization, UUID userId) {
        log.info("Creating organization in SATUSEHAT for organization: {}", organizationId);

        if (organization.getName() == null || organization.getName().isBlank()) {
            throw new SatusehatValidationException("Organization name is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Organization createdOrganization = httpClient.post(
            "/Organization",
            organization,
            config,
            Organization.class,
            userId
        );

        log.info("Organization created successfully with ID: {}", createdOrganization.getId());
        return createdOrganization;
    }

    /**
     * Update an organization in SATUSEHAT.
     */
    public Organization updateOrganization(
        String organizationId,
        String orgResourceId,
        Organization organization,
        UUID userId
    ) {
        log.info("Updating organization {} in SATUSEHAT", orgResourceId);

        if (organization.getId() == null) {
            organization.setId(orgResourceId);
        }

        var config = authService.getActiveConfig(organizationId);

        Organization updatedOrganization = httpClient.put(
            "/Organization/" + orgResourceId,
            organization,
            config,
            Organization.class,
            userId
        );

        log.info("Organization {} updated successfully", orgResourceId);
        return updatedOrganization;
    }

    /**
     * Get organization by ID.
     */
    public Organization getOrganizationById(String organizationId, String orgResourceId, UUID userId) {
        log.info("Retrieving organization {} from SATUSEHAT", orgResourceId);

        var config = authService.getActiveConfig(organizationId);

        Organization organization = httpClient.get(
            "/Organization/" + orgResourceId,
            config,
            Organization.class,
            userId
        );

        return organization;
    }

    /**
     * Search organizations by name.
     */
    public ClinicalResourceService.SearchBundle<Organization> searchOrganizationsByName(
        String organizationId,
        String name,
        UUID userId
    ) {
        log.info("Searching organizations with name: {}", name);

        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return searchOrganizations(organizationId, params, userId);
    }

    /**
     * Search organizations by identifier.
     */
    public ClinicalResourceService.SearchBundle<Organization> searchOrganizationsByIdentifier(
        String organizationId,
        String identifierValue,
        UUID userId
    ) {
        log.info("Searching organizations with identifier: {}", identifierValue);

        Map<String, String> params = new HashMap<>();
        params.put("identifier", identifierValue);

        return searchOrganizations(organizationId, params, userId);
    }

    /**
     * Search organizations by type.
     */
    public ClinicalResourceService.SearchBundle<Organization> searchOrganizationsByType(
        String organizationId,
        String typeCode,
        UUID userId
    ) {
        log.info("Searching organizations with type: {}", typeCode);

        Map<String, String> params = new HashMap<>();
        params.put("type", typeCode);

        return searchOrganizations(organizationId, params, userId);
    }

    /**
     * Search active organizations.
     */
    public ClinicalResourceService.SearchBundle<Organization> searchActiveOrganizations(
        String organizationId,
        UUID userId
    ) {
        log.info("Searching active organizations");

        Map<String, String> params = new HashMap<>();
        params.put("active", "true");

        return searchOrganizations(organizationId, params, userId);
    }

    /**
     * Generic organization search.
     */
    private ClinicalResourceService.SearchBundle<Organization> searchOrganizations(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Organization?");
        params.forEach((key, value) -> {
            if (queryString.length() > 14) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Organization> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
