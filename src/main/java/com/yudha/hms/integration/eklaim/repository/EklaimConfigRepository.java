package com.yudha.hms.integration.eklaim.repository;

import com.yudha.hms.integration.eklaim.entity.EklaimConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for E-Klaim Configuration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Repository
public interface EklaimConfigRepository extends JpaRepository<EklaimConfig, UUID> {

    /**
     * Find configuration by hospital code
     */
    Optional<EklaimConfig> findByHospitalCode(String hospitalCode);

    /**
     * Find active configuration by hospital code
     */
    Optional<EklaimConfig> findByHospitalCodeAndIsActiveTrue(String hospitalCode);

    /**
     * Find all active configurations
     */
    List<EklaimConfig> findByIsActiveTrue();

    /**
     * Find production configurations
     */
    List<EklaimConfig> findByIsProductionTrue();

    /**
     * Check if hospital code exists
     */
    boolean existsByHospitalCode(String hospitalCode);
}
