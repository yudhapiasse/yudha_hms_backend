package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Location;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing Location FHIR resources in SATUSEHAT.
 *
 * Handles:
 * - Location CRUD operations
 * - Hierarchical location search
 * - Location type and status filtering
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final SatusehatHttpClient httpClient;
    private final SatusehatAuthService authService;

    // ========================================================================
    // LOCATION OPERATIONS
    // ========================================================================

    /**
     * Create a location in SATUSEHAT.
     */
    public Location createLocation(String organizationId, Location location, UUID userId) {
        log.info("Creating location in SATUSEHAT for organization: {}", organizationId);

        if (location.getName() == null || location.getName().isBlank()) {
            throw new SatusehatValidationException("Location name is required");
        }

        if (location.getManagingOrganization() == null) {
            throw new SatusehatValidationException("Managing organization is required");
        }

        var config = authService.getActiveConfig(organizationId);

        Location createdLocation = httpClient.post(
            "/Location",
            location,
            config,
            Location.class,
            userId
        );

        log.info("Location created successfully with ID: {}", createdLocation.getId());
        return createdLocation;
    }

    /**
     * Update a location in SATUSEHAT.
     */
    public Location updateLocation(
        String organizationId,
        String locationId,
        Location location,
        UUID userId
    ) {
        log.info("Updating location {} in SATUSEHAT", locationId);

        if (location.getId() == null) {
            location.setId(locationId);
        }

        var config = authService.getActiveConfig(organizationId);

        Location updatedLocation = httpClient.put(
            "/Location/" + locationId,
            location,
            config,
            Location.class,
            userId
        );

        log.info("Location {} updated successfully", locationId);
        return updatedLocation;
    }

    /**
     * Get location by ID.
     */
    public Location getLocationById(String organizationId, String locationId, UUID userId) {
        log.info("Retrieving location {} from SATUSEHAT", locationId);

        var config = authService.getActiveConfig(organizationId);

        Location location = httpClient.get(
            "/Location/" + locationId,
            config,
            Location.class,
            userId
        );

        return location;
    }

    /**
     * Search locations by name.
     */
    public ClinicalResourceService.SearchBundle<Location> searchLocationsByName(
        String organizationId,
        String name,
        UUID userId
    ) {
        log.info("Searching locations with name: {}", name);

        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return searchLocations(organizationId, params, userId);
    }

    /**
     * Search locations by organization.
     */
    public ClinicalResourceService.SearchBundle<Location> searchLocationsByOrganization(
        String organizationId,
        String orgResourceId,
        UUID userId
    ) {
        log.info("Searching locations for organization: {}", orgResourceId);

        Map<String, String> params = new HashMap<>();
        params.put("organization", "Organization/" + orgResourceId);

        return searchLocations(organizationId, params, userId);
    }

    /**
     * Search locations by type (e.g., polyclinic, ward, bed).
     */
    public ClinicalResourceService.SearchBundle<Location> searchLocationsByType(
        String organizationId,
        String typeCode,
        UUID userId
    ) {
        log.info("Searching locations with type: {}", typeCode);

        Map<String, String> params = new HashMap<>();
        params.put("type", typeCode);

        return searchLocations(organizationId, params, userId);
    }

    /**
     * Search locations by status (active, inactive, suspended).
     */
    public ClinicalResourceService.SearchBundle<Location> searchLocationsByStatus(
        String organizationId,
        String status,
        UUID userId
    ) {
        log.info("Searching locations with status: {}", status);

        Map<String, String> params = new HashMap<>();
        params.put("status", status);

        return searchLocations(organizationId, params, userId);
    }

    /**
     * Search child locations by parent location.
     * This enables hierarchical queries (e.g., find all beds in a ward).
     */
    public ClinicalResourceService.SearchBundle<Location> searchLocationsByParent(
        String organizationId,
        String parentLocationId,
        UUID userId
    ) {
        log.info("Searching child locations for parent: {}", parentLocationId);

        Map<String, String> params = new HashMap<>();
        params.put("partof", "Location/" + parentLocationId);

        return searchLocations(organizationId, params, userId);
    }

    /**
     * Search available beds (operational status = unoccupied).
     */
    public ClinicalResourceService.SearchBundle<Location> searchAvailableBeds(
        String organizationId,
        UUID userId
    ) {
        log.info("Searching available beds");

        Map<String, String> params = new HashMap<>();
        params.put("type", "BD"); // Bed type code
        params.put("operational-status", "U"); // Unoccupied

        return searchLocations(organizationId, params, userId);
    }

    /**
     * Search beds by ward.
     */
    public ClinicalResourceService.SearchBundle<Location> searchBedsByWard(
        String organizationId,
        String wardLocationId,
        UUID userId
    ) {
        log.info("Searching beds in ward: {}", wardLocationId);

        Map<String, String> params = new HashMap<>();
        params.put("type", "BD"); // Bed type code
        params.put("partof", "Location/" + wardLocationId);

        return searchLocations(organizationId, params, userId);
    }

    /**
     * Generic location search.
     */
    private ClinicalResourceService.SearchBundle<Location> searchLocations(
        String organizationId,
        Map<String, String> params,
        UUID userId
    ) {
        StringBuilder queryString = new StringBuilder("/Location?");
        params.forEach((key, value) -> {
            if (queryString.length() > 10) queryString.append("&");
            queryString.append(key).append("=").append(value);
        });

        var config = authService.getActiveConfig(organizationId);

        @SuppressWarnings("unchecked")
        ClinicalResourceService.SearchBundle<Location> bundle = httpClient.get(
            queryString.toString(),
            config,
            ClinicalResourceService.SearchBundle.class,
            userId
        );

        return bundle;
    }
}
