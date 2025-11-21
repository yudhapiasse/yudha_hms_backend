package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.entity.Prescription;
import com.yudha.hms.pharmacy.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Prescription Item Repository.
 *
 * Data access layer for PrescriptionItem entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, UUID> {

    /**
     * Find items by prescription
     */
    List<PrescriptionItem> findByPrescriptionOrderByLineNumber(Prescription prescription);

    /**
     * Find items by prescription ID
     */
    List<PrescriptionItem> findByPrescription_IdOrderByLineNumber(UUID prescriptionId);

    /**
     * Find items by drug
     */
    List<PrescriptionItem> findByDrug_IdOrderByCreatedAtDesc(UUID drugId);

    /**
     * Find controlled drug items
     */
    @Query("SELECT i FROM PrescriptionItem i WHERE i.isControlled = true " +
           "ORDER BY i.createdAt DESC")
    List<PrescriptionItem> findControlledDrugItems();

    /**
     * Find high alert medication items
     */
    @Query("SELECT i FROM PrescriptionItem i WHERE i.isHighAlert = true " +
           "ORDER BY i.createdAt DESC")
    List<PrescriptionItem> findHighAlertItems();

    /**
     * Find items with interactions
     */
    @Query("SELECT i FROM PrescriptionItem i WHERE i.interactionWarnings IS NOT NULL " +
           "ORDER BY i.createdAt DESC")
    List<PrescriptionItem> findItemsWithInteractions();

    /**
     * Find substituted items
     */
    @Query("SELECT i FROM PrescriptionItem i WHERE i.substitutedDrugId IS NOT NULL " +
           "ORDER BY i.createdAt DESC")
    List<PrescriptionItem> findSubstitutedItems();

    /**
     * Find partially dispensed items
     */
    @Query("SELECT i FROM PrescriptionItem i WHERE i.quantityDispensed IS NOT NULL " +
           "AND i.quantityDispensed < i.quantityToDispense " +
           "ORDER BY i.createdAt DESC")
    List<PrescriptionItem> findPartiallyDispensedItems();

    /**
     * Count items by drug
     */
    long countByDrug_Id(UUID drugId);

    /**
     * Find items by patient (through prescription)
     */
    @Query("SELECT i FROM PrescriptionItem i WHERE i.prescription.patientId = :patientId " +
           "ORDER BY i.createdAt DESC")
    List<PrescriptionItem> findByPatientId(@Param("patientId") UUID patientId);
}
