package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.constant.TariffType;
import com.yudha.hms.billing.entity.TariffCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Tariff Category Repository.
 *
 * Data access layer for TariffCategory entity with custom query methods.
 * Supports dynamic queries via JpaSpecificationExecutor for advanced search.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface TariffCategoryRepository extends JpaRepository<TariffCategory, UUID>, JpaSpecificationExecutor<TariffCategory> {

    /**
     * Find category by code
     *
     * @param code category code
     * @return optional category
     */
    Optional<TariffCategory> findByCode(String code);

    /**
     * Find all categories by tariff type
     *
     * @param tariffType tariff type
     * @return list of categories
     */
    List<TariffCategory> findByTariffType(TariffType tariffType);

    /**
     * Find all active categories
     *
     * @param active active status
     * @return list of categories
     */
    List<TariffCategory> findByActiveOrderByDisplayOrder(Boolean active);

    /**
     * Find categories by parent
     *
     * @param parent parent category
     * @return list of categories
     */
    List<TariffCategory> findByParentOrderByDisplayOrder(TariffCategory parent);

    /**
     * Check if code exists
     *
     * @param code category code
     * @return true if exists
     */
    boolean existsByCode(String code);
}
