package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.FormulariumStatus;
import com.yudha.hms.pharmacy.entity.Drug;
import com.yudha.hms.pharmacy.entity.DrugCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Drug Repository.
 *
 * Data access layer for Drug entity with comprehensive query methods.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface DrugRepository extends JpaRepository<Drug, UUID>, JpaSpecificationExecutor<Drug> {

    /**
     * Find drug by code
     */
    Optional<Drug> findByDrugCode(String drugCode);

    /**
     * Find drug by barcode
     */
    Optional<Drug> findByBarcode(String barcode);

    /**
     * Find drugs by generic name
     */
    List<Drug> findByGenericNameContainingIgnoreCaseOrderByGenericName(String genericName);

    /**
     * Find drugs by brand name
     */
    List<Drug> findByBrandNameContainingIgnoreCaseOrderByBrandName(String brandName);

    /**
     * Find drugs by category
     */
    List<Drug> findByCategoryOrderByGenericName(DrugCategory category);

    /**
     * Find active drugs
     */
    List<Drug> findByActiveOrderByGenericName(Boolean active);

    /**
     * Find drugs by formularium status
     */
    List<Drug> findByFormulariumStatusOrderByGenericName(FormulariumStatus status);

    /**
     * Find BPJS approved drugs
     */
    @Query("SELECT d FROM Drug d WHERE d.formulariumStatus IN ('APPROVED', 'RESTRICTED') " +
           "AND d.active = true AND d.deletedAt IS NULL ORDER BY d.genericName")
    List<Drug> findBpjsApprovedDrugs();

    /**
     * Find narcotic drugs
     */
    List<Drug> findByIsNarcoticOrderByGenericName(Boolean isNarcotic);

    /**
     * Find psychotropic drugs
     */
    List<Drug> findByIsPsychotropicOrderByGenericName(Boolean isPsychotropic);

    /**
     * Find high alert medications
     */
    List<Drug> findByIsHighAlertOrderByGenericName(Boolean isHighAlert);

    /**
     * Find drugs with low stock
     */
    @Query("SELECT d FROM Drug d WHERE d.currentStock < d.minimumStockLevel " +
           "AND d.active = true AND d.deletedAt IS NULL ORDER BY d.genericName")
    List<Drug> findLowStockDrugs();

    /**
     * Find drugs by supplier
     */
    List<Drug> findByPrimarySupplierIdOrderByGenericName(UUID supplierId);

    /**
     * Search drugs (generic name or brand name)
     */
    @Query("SELECT d FROM Drug d WHERE " +
           "(LOWER(d.genericName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(d.brandName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND d.active = true AND d.deletedAt IS NULL ORDER BY d.genericName")
    List<Drug> searchDrugs(@Param("searchTerm") String searchTerm);

    /**
     * Find drugs with expired registration
     */
    @Query("SELECT d FROM Drug d WHERE d.registrationExpiryDate < CURRENT_DATE " +
           "AND d.active = true AND d.deletedAt IS NULL ORDER BY d.registrationExpiryDate")
    List<Drug> findDrugsWithExpiredRegistration();

    /**
     * Find discontinued drugs
     */
    List<Drug> findByIsDiscontinuedOrderByDiscontinuationDate(Boolean isDiscontinued);

    /**
     * Check if drug code exists
     */
    boolean existsByDrugCode(String drugCode);

    /**
     * Check if barcode exists
     */
    boolean existsByBarcode(String barcode);

    /**
     * Count drugs by category
     */
    long countByCategory(DrugCategory category);

    /**
     * Count drugs by formularium status
     */
    long countByFormulariumStatus(FormulariumStatus status);
}
