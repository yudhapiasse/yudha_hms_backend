package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.entity.PackageDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Package Deal Repository.
 *
 * Data access layer for PackageDeal entity with custom query methods.
 * Supports dynamic queries via JpaSpecificationExecutor for advanced search.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface PackageDealRepository extends JpaRepository<PackageDeal, UUID>, JpaSpecificationExecutor<PackageDeal> {

    /**
     * Find package by code
     *
     * @param code package code
     * @return optional package
     */
    Optional<PackageDeal> findByCode(String code);

    /**
     * Find all active packages
     *
     * @param active active status
     * @return list of packages
     */
    List<PackageDeal> findByActive(Boolean active);

    /**
     * Find currently valid packages
     * Valid means: active and effectiveDate <= today <= expiryDate
     *
     * @param currentDate current date
     * @return list of valid packages
     */
    @Query("SELECT p FROM PackageDeal p WHERE p.active = true " +
           "AND (p.effectiveDate IS NULL OR p.effectiveDate <= :currentDate) " +
           "AND (p.expiryDate IS NULL OR p.expiryDate >= :currentDate)")
    List<PackageDeal> findCurrentlyValidPackages(@Param("currentDate") LocalDate currentDate);

    /**
     * Check if code exists
     *
     * @param code package code
     * @return true if exists
     */
    boolean existsByCode(String code);

    /**
     * Search packages by name (case-insensitive, partial match)
     *
     * @param name name to search
     * @return list of packages
     */
    @Query("SELECT p FROM PackageDeal p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PackageDeal> searchByName(@Param("name") String name);
}
