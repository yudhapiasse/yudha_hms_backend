package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.entity.InsuranceCompany;
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
 * Insurance Company Repository.
 *
 * Data access layer for InsuranceCompany entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface InsuranceCompanyRepository extends JpaRepository<InsuranceCompany, UUID>,
        JpaSpecificationExecutor<InsuranceCompany> {

    /**
     * Find insurance company by code
     *
     * @param code company code
     * @return optional insurance company
     */
    Optional<InsuranceCompany> findByCode(String code);

    /**
     * Find active insurance companies
     *
     * @param active active status
     * @return list of insurance companies
     */
    List<InsuranceCompany> findByActiveOrderByName(Boolean active);

    /**
     * Find insurance companies by type
     *
     * @param companyType company type
     * @return list of insurance companies
     */
    List<InsuranceCompany> findByCompanyTypeOrderByName(String companyType);

    /**
     * Search insurance companies by name
     *
     * @param name name pattern
     * @return list of insurance companies
     */
    @Query("SELECT ic FROM InsuranceCompany ic WHERE LOWER(ic.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND ic.deletedAt IS NULL ORDER BY ic.name")
    List<InsuranceCompany> searchByName(@Param("name") String name);

    /**
     * Find insurance companies with valid contracts
     *
     * @param currentDate current date
     * @return list of insurance companies
     */
    @Query("SELECT ic FROM InsuranceCompany ic WHERE ic.active = true " +
           "AND ic.contractStartDate <= :currentDate " +
           "AND ic.contractEndDate >= :currentDate " +
           "AND ic.deletedAt IS NULL ORDER BY ic.name")
    List<InsuranceCompany> findWithValidContracts(@Param("currentDate") LocalDate currentDate);

    /**
     * Find insurance companies with expiring contracts
     *
     * @param startDate start date
     * @param endDate end date
     * @return list of insurance companies
     */
    @Query("SELECT ic FROM InsuranceCompany ic WHERE ic.active = true " +
           "AND ic.contractEndDate BETWEEN :startDate AND :endDate " +
           "AND ic.deletedAt IS NULL ORDER BY ic.contractEndDate")
    List<InsuranceCompany> findWithExpiringContracts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Check if company code exists
     *
     * @param code company code
     * @return true if exists
     */
    boolean existsByCode(String code);

    /**
     * Find insurance companies exceeding credit limit
     *
     * @return list of insurance companies
     */
    @Query("SELECT ic FROM InsuranceCompany ic WHERE ic.active = true " +
           "AND ic.creditLimit IS NOT NULL " +
           "AND ic.currentOutstanding > ic.creditLimit " +
           "AND ic.deletedAt IS NULL ORDER BY ic.name")
    List<InsuranceCompany> findExceedingCreditLimit();
}
