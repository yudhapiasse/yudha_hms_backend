package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.constant.TariffType;
import com.yudha.hms.billing.entity.Tariff;
import com.yudha.hms.billing.entity.TariffCategory;
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
 * Tariff Repository.
 *
 * Data access layer for Tariff entity with custom query methods.
 * Supports dynamic queries via JpaSpecificationExecutor for advanced search.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface TariffRepository extends JpaRepository<Tariff, UUID>, JpaSpecificationExecutor<Tariff> {

    /**
     * Find tariff by code
     *
     * @param code tariff code
     * @return optional tariff
     */
    Optional<Tariff> findByCode(String code);

    /**
     * Find all tariffs by type
     *
     * @param tariffType tariff type
     * @return list of tariffs
     */
    List<Tariff> findByTariffType(TariffType tariffType);

    /**
     * Find all tariffs by category
     *
     * @param category tariff category
     * @return list of tariffs
     */
    List<Tariff> findByCategory(TariffCategory category);

    /**
     * Find all active tariffs
     *
     * @param active active status
     * @return list of tariffs
     */
    List<Tariff> findByActive(Boolean active);

    /**
     * Find active tariffs by type
     *
     * @param tariffType tariff type
     * @param active active status
     * @return list of tariffs
     */
    List<Tariff> findByTariffTypeAndActive(TariffType tariffType, Boolean active);

    /**
     * Find currently valid tariffs
     * Valid means: active and effectiveDate <= today <= expiryDate
     *
     * @param currentDate current date
     * @return list of valid tariffs
     */
    @Query("SELECT t FROM Tariff t WHERE t.active = true " +
           "AND (t.effectiveDate IS NULL OR t.effectiveDate <= :currentDate) " +
           "AND (t.expiryDate IS NULL OR t.expiryDate >= :currentDate)")
    List<Tariff> findCurrentlyValidTariffs(@Param("currentDate") LocalDate currentDate);

    /**
     * Check if code exists
     *
     * @param code tariff code
     * @return true if exists
     */
    boolean existsByCode(String code);

    /**
     * Search tariffs by name (case-insensitive, partial match)
     *
     * @param name name to search
     * @return list of tariffs
     */
    @Query("SELECT t FROM Tariff t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tariff> searchByName(@Param("name") String name);
}
