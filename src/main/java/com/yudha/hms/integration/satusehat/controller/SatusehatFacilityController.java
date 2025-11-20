package com.yudha.hms.integration.satusehat.controller;

import com.yudha.hms.integration.satusehat.dto.fhir.Location;
import com.yudha.hms.integration.satusehat.dto.fhir.Organization;
import com.yudha.hms.integration.satusehat.service.ClinicalResourceService;
import com.yudha.hms.integration.satusehat.service.LocationService;
import com.yudha.hms.integration.satusehat.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API controller for SATUSEHAT facility resource operations.
 *
 * Provides endpoints for:
 * - Organization management
 * - Location management (hierarchical structure)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/satusehat/facility")
@RequiredArgsConstructor
public class SatusehatFacilityController {

    private final OrganizationService organizationService;
    private final LocationService locationService;

    // ========================================================================
    // ORGANIZATION ENDPOINTS
    // ========================================================================

    /**
     * Create an organization in SATUSEHAT.
     *
     * POST /api/v1/satusehat/facility/organization
     */
    @PostMapping("/organization")
    public ResponseEntity<Organization> createOrganization(
        @RequestBody Organization organization,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create organization request for organization: {}", organizationId);

        try {
            Organization createdOrganization = organizationService.createOrganization(
                organizationId,
                organization,
                userId
            );

            return ResponseEntity.ok(createdOrganization);

        } catch (Exception e) {
            log.error("Failed to create organization: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update an organization in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/facility/organization/{orgResourceId}
     */
    @PutMapping("/organization/{orgResourceId}")
    public ResponseEntity<Organization> updateOrganization(
        @PathVariable String orgResourceId,
        @RequestBody Organization organization,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update organization request for ID: {}", orgResourceId);

        try {
            Organization updatedOrganization = organizationService.updateOrganization(
                organizationId,
                orgResourceId,
                organization,
                userId
            );

            return ResponseEntity.ok(updatedOrganization);

        } catch (Exception e) {
            log.error("Failed to update organization: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get organization by ID.
     *
     * GET /api/v1/satusehat/facility/organization/{orgResourceId}
     */
    @GetMapping("/organization/{orgResourceId}")
    public ResponseEntity<Organization> getOrganization(
        @PathVariable String orgResourceId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get organization request for ID: {}", orgResourceId);

        try {
            Organization organization = organizationService.getOrganizationById(
                organizationId,
                orgResourceId,
                userId
            );

            return ResponseEntity.ok(organization);

        } catch (Exception e) {
            log.error("Failed to get organization: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search organizations by name.
     *
     * GET /api/v1/satusehat/facility/organization/name/{name}
     */
    @GetMapping("/organization/name/{name}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Organization>> searchOrganizationsByName(
        @PathVariable String name,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search organizations by name: {}", name);

        try {
            ClinicalResourceService.SearchBundle<Organization> bundle =
                organizationService.searchOrganizationsByName(organizationId, name, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search organizations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search active organizations.
     *
     * GET /api/v1/satusehat/facility/organization/active
     */
    @GetMapping("/organization/active")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Organization>> searchActiveOrganizations(
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search active organizations");

        try {
            ClinicalResourceService.SearchBundle<Organization> bundle =
                organizationService.searchActiveOrganizations(organizationId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search active organizations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================================================
    // LOCATION ENDPOINTS
    // ========================================================================

    /**
     * Create a location in SATUSEHAT.
     *
     * POST /api/v1/satusehat/facility/location
     */
    @PostMapping("/location")
    public ResponseEntity<Location> createLocation(
        @RequestBody Location location,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Create location request for organization: {}", organizationId);

        try {
            Location createdLocation = locationService.createLocation(
                organizationId,
                location,
                userId
            );

            return ResponseEntity.ok(createdLocation);

        } catch (Exception e) {
            log.error("Failed to create location: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update a location in SATUSEHAT.
     *
     * PUT /api/v1/satusehat/facility/location/{locationId}
     */
    @PutMapping("/location/{locationId}")
    public ResponseEntity<Location> updateLocation(
        @PathVariable String locationId,
        @RequestBody Location location,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Update location request for ID: {}", locationId);

        try {
            Location updatedLocation = locationService.updateLocation(
                organizationId,
                locationId,
                location,
                userId
            );

            return ResponseEntity.ok(updatedLocation);

        } catch (Exception e) {
            log.error("Failed to update location: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get location by ID.
     *
     * GET /api/v1/satusehat/facility/location/{locationId}
     */
    @GetMapping("/location/{locationId}")
    public ResponseEntity<Location> getLocation(
        @PathVariable String locationId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Get location request for ID: {}", locationId);

        try {
            Location location = locationService.getLocationById(
                organizationId,
                locationId,
                userId
            );

            return ResponseEntity.ok(location);

        } catch (Exception e) {
            log.error("Failed to get location: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search locations by organization.
     *
     * GET /api/v1/satusehat/facility/location/organization/{orgResourceId}
     */
    @GetMapping("/location/organization/{orgResourceId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Location>> searchLocationsByOrganization(
        @PathVariable String orgResourceId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search locations for organization: {}", orgResourceId);

        try {
            ClinicalResourceService.SearchBundle<Location> bundle =
                locationService.searchLocationsByOrganization(organizationId, orgResourceId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search locations by type.
     *
     * GET /api/v1/satusehat/facility/location/type/{typeCode}
     */
    @GetMapping("/location/type/{typeCode}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Location>> searchLocationsByType(
        @PathVariable String typeCode,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search locations by type: {}", typeCode);

        try {
            ClinicalResourceService.SearchBundle<Location> bundle =
                locationService.searchLocationsByType(organizationId, typeCode, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search locations by status.
     *
     * GET /api/v1/satusehat/facility/location/status/{status}
     */
    @GetMapping("/location/status/{status}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Location>> searchLocationsByStatus(
        @PathVariable String status,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search locations by status: {}", status);

        try {
            ClinicalResourceService.SearchBundle<Location> bundle =
                locationService.searchLocationsByStatus(organizationId, status, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search child locations by parent location.
     *
     * GET /api/v1/satusehat/facility/location/parent/{parentLocationId}
     */
    @GetMapping("/location/parent/{parentLocationId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Location>> searchLocationsByParent(
        @PathVariable String parentLocationId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search child locations for parent: {}", parentLocationId);

        try {
            ClinicalResourceService.SearchBundle<Location> bundle =
                locationService.searchLocationsByParent(organizationId, parentLocationId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search child locations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search available beds.
     *
     * GET /api/v1/satusehat/facility/location/beds/available
     */
    @GetMapping("/location/beds/available")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Location>> searchAvailableBeds(
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search available beds");

        try {
            ClinicalResourceService.SearchBundle<Location> bundle =
                locationService.searchAvailableBeds(organizationId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search available beds: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search beds by ward.
     *
     * GET /api/v1/satusehat/facility/location/beds/ward/{wardLocationId}
     */
    @GetMapping("/location/beds/ward/{wardLocationId}")
    public ResponseEntity<ClinicalResourceService.SearchBundle<Location>> searchBedsByWard(
        @PathVariable String wardLocationId,
        @RequestParam String organizationId,
        @RequestParam UUID userId
    ) {
        log.info("Search beds in ward: {}", wardLocationId);

        try {
            ClinicalResourceService.SearchBundle<Location> bundle =
                locationService.searchBedsByWard(organizationId, wardLocationId, userId);

            return ResponseEntity.ok(bundle);

        } catch (Exception e) {
            log.error("Failed to search beds: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
