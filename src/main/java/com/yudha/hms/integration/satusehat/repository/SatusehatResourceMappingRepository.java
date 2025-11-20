package com.yudha.hms.integration.satusehat.repository;

import com.yudha.hms.integration.satusehat.entity.SatusehatResourceMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for SATUSEHAT Resource Mappings.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface SatusehatResourceMappingRepository extends JpaRepository<SatusehatResourceMapping, UUID> {

    /**
     * Find mapping by local resource
     */
    Optional<SatusehatResourceMapping> findByLocalResourceTypeAndLocalResourceId(
        String localResourceType,
        UUID localResourceId
    );

    /**
     * Find mapping by SATUSEHAT resource
     */
    Optional<SatusehatResourceMapping> findBySatusehatResourceTypeAndSatusehatResourceId(
        String satusehatResourceType,
        String satusehatResourceId
    );

    /**
     * Find mapping by SATUSEHAT identifier (IHS, NIK, etc.)
     */
    Optional<SatusehatResourceMapping> findBySatusehatIdentifier(String satusehatIdentifier);

    /**
     * Find all mappings by submission status
     */
    List<SatusehatResourceMapping> findBySubmissionStatus(
        SatusehatResourceMapping.SubmissionStatus status
    );

    /**
     * Find pending submissions
     */
    @Query("SELECT m FROM SatusehatResourceMapping m WHERE m.submissionStatus = 'PENDING'")
    List<SatusehatResourceMapping> findPendingSubmissions();

    /**
     * Find failed submissions
     */
    @Query("SELECT m FROM SatusehatResourceMapping m WHERE m.submissionStatus = 'FAILED'")
    List<SatusehatResourceMapping> findFailedSubmissions();

    /**
     * Find mappings by local resource type
     */
    List<SatusehatResourceMapping> findByLocalResourceType(String localResourceType);

    /**
     * Find mappings by SATUSEHAT resource type
     */
    List<SatusehatResourceMapping> findBySatusehatResourceType(String satusehatResourceType);

    /**
     * Check if local resource is already mapped
     */
    boolean existsByLocalResourceTypeAndLocalResourceId(
        String localResourceType,
        UUID localResourceId
    );

    /**
     * Count mappings by submission status
     */
    long countBySubmissionStatus(SatusehatResourceMapping.SubmissionStatus status);

    /**
     * Find mappings by configuration
     */
    @Query("SELECT m FROM SatusehatResourceMapping m WHERE m.config.id = :configId")
    List<SatusehatResourceMapping> findByConfigId(@Param("configId") UUID configId);

    /**
     * Find mapping by resource type and local resource ID
     */
    Optional<SatusehatResourceMapping> findByResourceTypeAndLocalResourceId(
        String resourceType,
        UUID localResourceId
    );

    /**
     * Find mappings by resource type and submission status
     */
    List<SatusehatResourceMapping> findByResourceTypeAndSubmissionStatus(
        String resourceType,
        SatusehatResourceMapping.SubmissionStatus status
    );

    /**
     * Find mappings by organization, resource type, and submission status
     */
    List<SatusehatResourceMapping> findByOrganizationIdAndResourceTypeAndSubmissionStatus(
        String organizationId,
        String resourceType,
        SatusehatResourceMapping.SubmissionStatus status
    );
}
