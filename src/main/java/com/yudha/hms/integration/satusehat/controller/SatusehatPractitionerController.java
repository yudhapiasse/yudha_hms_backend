package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.Practitioner;
import com.yudha.hms.integration.satusehat.dto.fhir.PractitionerRole;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import com.yudha.hms.integration.satusehat.service.PractitionerResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API controller for SATUSEHAT practitioner resource operations.
 *
 * Provides endpoints for:
 * - Practitioner (healthcare professional) management
 * - PractitionerRole (roles/positions at organizations) management
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/practitioner")
@RequiredArgsConstructor
public class SatusehatPractitionerController {

    private final PractitionerResourceService practitionerResourceService;

    // ========================================================================
    // PRACTITIONER ENDPOINTS
    // ========================================================================

    /**
     * Create a practitioner in SATUSEHAT.
     *
     * POST /api/v1/satusehat/practitioner
     */
    @PostMapping
    public ResponseEntity<Practitioner> createPractitioner(
        @RequestBody Practitioner practitioner,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create practitioner request for organization: {}", organizationId);

        try {
            Practitioner createdPractitioner = practitionerResourceService.createPractitioner(
                organizationId,
                practitioner,
                userId
            );

            return ResponseEntity.ok(createdPractitioner);

        } catch (Exception e) {
            log.error("Failed to create practitioner: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a practitioner in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/practitioner/{practitionerId}
     */
    @PutMapping("/{practitionerId}")
    public ResponseEntity<Practitioner> updatePractitioner(
        @PathVariable String practitionerId,
        @RequestBody Practitioner practitioner,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update practitioner request for ID: {}", practitionerId);

        try {
            Practitioner updatedPractitioner = practitionerResourceService.updatePractitioner(
                organizationId,
                practitionerId,
                practitioner,
                userId
            );

            return ResponseEntity.ok(updatedPractitioner);

        } catch (Exception e) {
            log.error("Failed to update practitioner: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get practitioner by ID.
     *
     * GET /api/v1/satusehat/practitioner/{practitionerId}
     */
    @GetMapping("/{practitionerId}")
    public ResponseEntity<Practitioner> getPractitioner(
        @PathVariable String practitionerId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get practitioner request for ID: {}", practitionerId);

        try {
            Practitioner practitioner = practitionerResourceService.getPractitionerById(
                organizationId,
                practitionerId,
                userId
            );

            return ResponseEntity.ok(practitioner);

        } catch (Exception e) {
            log.error("Failed to get practitioner: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search practitioners by name.
     *
     * GET /api/v1/satusehat/practitioner/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Practitioner>> searchPractitionersByName(
        @PathVariable String name,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search practitioners by name: {}", name);

        try {
            ClinicalResourceService.SearchBundle<Practitioner> bundle =
                practitionerResourceService.searchPractitionersByName(organizationId, name, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search practitioners: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search practitioners by identifier (NIK, SIP, etc.).
     *
     * GET /api/v1/satusehat/practitioner/identifier/{identifierValue}
     */
    @GetMapping("/identifier/{identifierValue}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Practitioner>> searchPractitionersByIdentifier(
        @PathVariable String identifierValue,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search practitioners by identifier: {}", identifierValue);

        try {
            ClinicalResourceService.SearchBundle<Practitioner> bundle =
                practitionerResourceService.searchPractitionersByIdentifier(organizationId, identifierValue, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search practitioners: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search active practitioners.
     *
     * GET /api/v1/satusehat/practitioner/active
     */
    @GetMapping("/active")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Practitioner>> searchActivePractitioners(
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search active practitioners");

        try {
            ClinicalResourceService.SearchBundle<Practitioner> bundle =
                practitionerResourceService.searchActivePractitioners(organizationId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search active practitioners: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // PRACTITIONER ROLE ENDPOINTS
    // ========================================================================

    /**
     * Create a practitioner role in SATUSEHAT.
     *
     * POST /api/v1/satusehat/practitioner/role
     */
    @PostMapping("/role")
    public ResponseEntity<PractitionerRole> createPractitionerRole(
        @RequestBody PractitionerRole practitionerRole,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create practitioner role request for organization: {}", organizationId);

        try {
            PractitionerRole createdRole = practitionerResourceService.createPractitionerRole(
                organizationId,
                practitionerRole,
                userId
            );

            return ResponseEntity.ok(createdRole);

        } catch (Exception e) {
            log.error("Failed to create practitioner role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a practitioner role in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/practitioner/role/{roleId}
     */
    @PutMapping("/role/{roleId}")
    public ResponseEntity<PractitionerRole> updatePractitionerRole(
        @PathVariable String roleId,
        @RequestBody PractitionerRole practitionerRole,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update practitioner role request for ID: {}", roleId);

        try {
            PractitionerRole updatedRole = practitionerResourceService.updatePractitionerRole(
                organizationId,
                roleId,
                practitionerRole,
                userId
            );

            return ResponseEntity.ok(updatedRole);

        } catch (Exception e) {
            log.error("Failed to update practitioner role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get practitioner role by ID.
     *
     * GET /api/v1/satusehat/practitioner/role/{roleId}
     */
    @GetMapping("/role/{roleId}")
    public ResponseEntity<PractitionerRole> getPractitionerRole(
        @PathVariable String roleId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get practitioner role request for ID: {}", roleId);

        try {
            PractitionerRole role = practitionerResourceService.getPractitionerRoleById(
                organizationId,
                roleId,
                userId
            );

            return ResponseEntity.ok(role);

        } catch (Exception e) {
            log.error("Failed to get practitioner role: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search practitioner roles by practitioner.
     *
     * GET /api/v1/satusehat/practitioner/role/practitioner/{practitionerId}
     */
    @GetMapping("/role/practitioner/{practitionerId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PractitionerRole>> searchRolesByPractitioner(
        @PathVariable String practitionerId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search roles for practitioner: {}", practitionerId);

        try {
            ClinicalResourceService.SearchBundle<PractitionerRole> bundle =
                practitionerResourceService.searchRolesByPractitioner(organizationId, practitionerId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search practitioner roles by organization.
     *
     * GET /api/v1/satusehat/practitioner/role/organization/{orgResourceId}
     */
    @GetMapping("/role/organization/{orgResourceId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PractitionerRole>> searchRolesByOrganization(
        @PathVariable String orgResourceId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search roles for organization: {}", orgResourceId);

        try {
            ClinicalResourceService.SearchBundle<PractitionerRole> bundle =
                practitionerResourceService.searchRolesByOrganization(organizationId, orgResourceId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search practitioner roles by specialty.
     *
     * GET /api/v1/satusehat/practitioner/role/specialty/{specialtyCode}
     */
    @GetMapping("/role/specialty/{specialtyCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PractitionerRole>> searchRolesBySpecialty(
        @PathVariable String specialtyCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search roles by specialty: {}", specialtyCode);

        try {
            ClinicalResourceService.SearchBundle<PractitionerRole> bundle =
                practitionerResourceService.searchRolesBySpecialty(organizationId, specialtyCode, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search practitioner roles by location.
     *
     * GET /api/v1/satusehat/practitioner/role/location/{locationId}
     */
    @GetMapping("/role/location/{locationId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PractitionerRole>> searchRolesByLocation(
        @PathVariable String locationId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search roles for location: {}", locationId);

        try {
            ClinicalResourceService.SearchBundle<PractitionerRole> bundle =
                practitionerResourceService.searchRolesByLocation(organizationId, locationId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search practitioner roles by role code.
     *
     * GET /api/v1/satusehat/practitioner/role/code/{roleCode}
     */
    @GetMapping("/role/code/{roleCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PractitionerRole>> searchRolesByRoleCode(
        @PathVariable String roleCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search roles by code: {}", roleCode);

        try {
            ClinicalResourceService.SearchBundle<PractitionerRole> bundle =
                practitionerResourceService.searchRolesByRoleCode(organizationId, roleCode, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search active practitioner roles.
     *
     * GET /api/v1/satusehat/practitioner/role/active
     */
    @GetMapping("/role/active")
    public ResponseEntity<ClinicalResourceService.SearchBundle<PractitionerRole>> searchActiveRoles(
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search active practitioner roles");

        try {
            ClinicalResourceService.SearchBundle<PractitionerRole> bundle =
                practitionerResourceService.searchActiveRoles(organizationId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search active roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
