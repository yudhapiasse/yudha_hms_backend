package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Practitioner;
import com.yudha.hms.integration.satusehat.dto.fhir.PractitionerRole;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing Practitioner and PractitionerRole FHIR resources in SATUSEHAT.
 *
 * Handles:
 * - Practitioner CRUD operations and search
 * - PractitionerRole CRUD operations and search
 * - Practitioner-Organization relationships
 * - Specialty and location assignments
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PractitionerResourceService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ========================================================================
    // PRACTITIONER OPERATIONS
    // ========================================================================

    /**
     * Create a practitioner in SATUSEHAT.
     */
    public Practitioner createPractitioner(String organizationId, Practitioner practitioner, UUID userId) {
        log.info("Creating practitioner in SATUSEHAT for organization: {}", organizationId);

        if (practitioner.getName() == null || practitioner.getName().isEmpty()) {
            throw new SatusehatValidationException("Practitioner name is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Practitioner createdPractitioner = httpClient.post(
            "/Practitioner",
            practitioner,
            config,
            Practitioner.class,
            userId
        );

        log.info("Practitioner created successfully with ID: {}", createdPractitioner.getId());
        return createdPractitioner;
    }

    /**
     * Update a practitioner in SATUSEHAT.
     */
    public Practitioner updatePractitioner(
        String organizationId,
        String practitionerId,
        Practitioner practitioner,
        UUID userId
    ) {
        log.info("Updating practitioner {} in SATUSEHAT", practitionerId);

        if (practitioner.getId() == null) {
            practitioner.setId(practitionerId);
        }

        var config = authService.getActiveConfig(organizationId);

        Practitioner updatedPractitioner = httpClient.put(
            "/Practitioner/" + practitionerId,
            practitioner,
            config,
            Practitioner.class,
            userId
        );

        log.info("Practitioner {} updated successfully", practitionerId);
        return updatedPractitioner;
    }

    /**
     * Get practitioner by ID.
     */
    public Practitioner getPractitionerById(String organizationId, String practitionerId, UUID userId) {
        log.info("Retrieving practitioner {} from SATUSEHAT", practitionerId);

        var config = authService.getActiveConfig(organizationId);

        Practitioner practitioner = httpClient.get(
            "/Practitioner/" + practitionerId,
            config,
            Practitioner.class,
            userId
        );

        return practitioner;
    }

    /**
     * Search practitioners by name.
     */
    public ClinicalResourceService.SearchBundle<Practitioner> searchPractitionersByName(
        String organizationId,
        String name,
        UUID userId
    ) {
        log.info("Searching practitioners with name: {}", name);

        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return searchPractitioners(organizationId, params, userId);
    }

    /**
     * Search practitioners by identifier (NIK, SIP, etc.).
     */
    public ClinicalResourceService.SearchBundle<Practitioner> searchPractitionersByIdentifier(
        String organizationId,
        String identifierValue,
        UUID userId
    ) {
        log.info("Searching practitioners with identifier: {}", identifierValue);

        Map<String, String> params = new HashMap<>();
        params.put("identifier", identifierValue);

        return searchPractitioners(organizationId, params, userId);
    }

    /**
     * Search active practitioners.
     */
    public ClinicalResourceService.SearchBundle<Practitioner> searchActivePractitioners(
        String organizationId,
        UUID userId
    ) {
        log.info("Searching active practitioners");

        Map<String, String> params = new HashMap<>();
        params.put("active", "true");

        return searchPractitioners(organizationId, params, userId);
    }

    /**
     * Generic practitioner search.
     */
    private ClinicalResourceService.SearchBundle<Practitioner> searchPractitioners(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Practitioner?");
        params.forEach((key, value) -> {
            if (queryString.length() > 14) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Practitioner> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }

    // ========================================================================
    // PRACTITIONER ROLE OPERATIONS
    // ========================================================================

    /**
     * Create a practitioner role in SATUSEHAT.
     */
    public PractitionerRole createPractitionerRole(
        String organizationId,
        PractitionerRole practitionerRole,
        UUID userId
    ) {
        log.info("Creating practitioner role in SATUSEHAT for organization: {}", organizationId);

        if (practitionerRole.getPractitioner() == null) {
            throw new SatusehatValidationException("Practitioner reference is required");
        }

        if (practitionerRole.getOrganization() == null) {
            throw new SatusehatValidationException("Organization reference is required");
        }

        var config = authService.getActiveConfig(organizationId);

        PractitionerRole createdRole = httpClient.post(
            "/PractitionerRole",
            practitionerRole,
            config,
            PractitionerRole.class,
            userId
        );

        log.info("Practitioner role created successfully with ID: {}", createdRole.getId());
        return createdRole;
    }

    /**
     * Update a practitioner role in SATUSEHAT.
     */
    public PractitionerRole updatePractitionerRole(
        String organizationId,
        String roleId,
        PractitionerRole practitionerRole,
        UUID userId
    ) {
        log.info("Updating practitioner role {} in SATUSEHAT", roleId);

        if (practitionerRole.getId() == null) {
            practitionerRole.setId(roleId);
        }

        var config = authService.getActiveConfig(organizationId);

        PractitionerRole updatedRole = httpClient.put(
            "/PractitionerRole/" + roleId,
            practitionerRole,
            config,
            PractitionerRole.class,
            userId
        );

        log.info("Practitioner role {} updated successfully", roleId);
        return updatedRole;
    }

    /**
     * Get practitioner role by ID.
     */
    public PractitionerRole getPractitionerRoleById(String organizationId, String roleId, UUID userId) {
        log.info("Retrieving practitioner role {} from SATUSEHAT", roleId);

        var config = authService.getActiveConfig(organizationId);

        PractitionerRole role = httpClient.get(
            "/PractitionerRole/" + roleId,
            config,
            PractitionerRole.class,
            userId
        );

        return role;
    }

    /**
     * Search practitioner roles by practitioner.
     */
    public ClinicalResourceService.SearchBundle<PractitionerRole> searchRolesByPractitioner(
        String organizationId,
        String practitionerId,
        UUID userId
    ) {
        log.info("Searching roles for practitioner: {}", practitionerId);

        Map<String, String> params = new HashMap<>();
        params.put("practitioner", "Practitioner/" + practitionerId);

        return searchPractitionerRoles(organizationId, params, userId);
    }

    /**
     * Search practitioner roles by organization.
     */
    public ClinicalResourceService.SearchBundle<PractitionerRole> searchRolesByOrganization(
        String organizationId,
        String orgResourceId,
        UUID userId
    ) {
        log.info("Searching roles for organization: {}", orgResourceId);

        Map<String, String> params = new HashMap<>();
        params.put("organization", "Organization/" + orgResourceId);

        return searchPractitionerRoles(organizationId, params, userId);
    }

    /**
     * Search practitioner roles by specialty.
     */
    public ClinicalResourceService.SearchBundle<PractitionerRole> searchRolesBySpecialty(
        String organizationId,
        String specialtyCode,
        UUID userId
    ) {
        log.info("Searching roles with specialty: {}", specialtyCode);

        Map<String, String> params = new HashMap<>();
        params.put("specialty", specialtyCode);

        return searchPractitionerRoles(organizationId, params, userId);
    }

    /**
     * Search practitioner roles by location.
     */
    public ClinicalResourceService.SearchBundle<PractitionerRole> searchRolesByLocation(
        String organizationId,
        String locationId,
        UUID userId
    ) {
        log.info("Searching roles for location: {}", locationId);

        Map<String, String> params = new HashMap<>();
        params.put("location", "Location/" + locationId);

        return searchPractitionerRoles(organizationId, params, userId);
    }

    /**
     * Search practitioner roles by role code.
     */
    public ClinicalResourceService.SearchBundle<PractitionerRole> searchRolesByRoleCode(
        String organizationId,
        String roleCode,
        UUID userId
    ) {
        log.info("Searching roles with code: {}", roleCode);

        Map<String, String> params = new HashMap<>();
        params.put("role", roleCode);

        return searchPractitionerRoles(organizationId, params, userId);
    }

    /**
     * Search active practitioner roles.
     */
    public ClinicalResourceService.SearchBundle<PractitionerRole> searchActiveRoles(
        String organizationId,
        UUID userId
    ) {
        log.info("Searching active practitioner roles");

        Map<String, String> params = new HashMap<>();
        params.put("active", "true");

        return searchPractitionerRoles(organizationId, params, userId);
    }

    /**
     * Generic practitioner role search.
     */
    private ClinicalResourceService.SearchBundle<PractitionerRole> searchPractitionerRoles(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/PractitionerRole?");
        params.forEach((key, value) -> {
            if (queryString.length() > 18) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<PractitionerRole> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
