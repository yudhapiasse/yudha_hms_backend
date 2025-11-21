package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.entity.PatientPreparationChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PatientPreparationChecklist entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Repository
public interface PatientPreparationChecklistRepository extends JpaRepository<PatientPreparationChecklist, UUID> {

    /**
     * Find checklist by order ID
     */
    Optional<PatientPreparationChecklist> findByOrderId(UUID orderId);

    /**
     * Find checklists by examination ID
     */
    List<PatientPreparationChecklist> findByExaminationId(UUID examinationId);

    /**
     * Find incomplete checklists
     */
    @Query("SELECT c FROM PatientPreparationChecklist c WHERE c.allItemsCompleted = false ORDER BY c.createdAt ASC")
    List<PatientPreparationChecklist> findIncompleteChecklists();

    /**
     * Find checklists awaiting fasting verification
     */
    @Query("SELECT c FROM PatientPreparationChecklist c WHERE c.fastingRequired = true AND c.fastingVerified = false ORDER BY c.createdAt ASC")
    List<PatientPreparationChecklist> findAwaitingFastingVerification();

    /**
     * Find checklists awaiting consent
     */
    @Query("SELECT c FROM PatientPreparationChecklist c WHERE c.consentObtained = false ORDER BY c.createdAt ASC")
    List<PatientPreparationChecklist> findAwaitingConsent();

    /**
     * Find checklists awaiting pregnancy test
     */
    @Query("SELECT c FROM PatientPreparationChecklist c WHERE c.pregnancyTestRequired = true AND c.pregnancyTestDone = false ORDER BY c.createdAt ASC")
    List<PatientPreparationChecklist> findAwaitingPregnancyTest();

    /**
     * Find checklists awaiting IV access
     */
    @Query("SELECT c FROM PatientPreparationChecklist c WHERE c.ivAccessRequired = true AND c.ivAccessVerified = false ORDER BY c.createdAt ASC")
    List<PatientPreparationChecklist> findAwaitingIVAccess();

    /**
     * Check if order has checklist
     */
    boolean existsByOrderId(UUID orderId);

    /**
     * Count incomplete checklists
     */
    long countByAllItemsCompletedFalse();

    /**
     * Find checklists by order IDs (for batch loading)
     */
    List<PatientPreparationChecklist> findByOrderIdIn(List<UUID> orderIds);
}
