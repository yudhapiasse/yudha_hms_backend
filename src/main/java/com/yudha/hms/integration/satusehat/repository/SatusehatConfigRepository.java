package com.yudha.hms.integration.satusehat.repository;

import com.yudha.hms.integration.satusehat.entity.SatusehatConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for SATUSEHAT Configuration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface SatusehatConfigRepository extends JpaRepository<SatusehatConfig, UUID> {

    /**
     * Find configuration by organization ID
     */
    Optional<SatusehatConfig> findByOrganizationId(String organizationId);

    /**
     * Find active configuration by organization ID
     */
    Optional<SatusehatConfig> findByOrganizationIdAndIsActiveTrue(String organizationId);

    /**
     * Find configuration by organization ID and environment
     */
    Optional<SatusehatConfig> findByOrganizationIdAndEnvironment(
        String organizationId,
        SatusehatConfig.Environment environment
    );

    /**
     * Find active configuration by organization ID and environment
     */
    Optional<SatusehatConfig> findByOrganizationIdAndEnvironmentAndIsActiveTrue(
        String organizationId,
        SatusehatConfig.Environment environment
    );

    /**
     * Check if configuration exists for organization
     */
    boolean existsByOrganizationId(String organizationId);
}
