package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.DischargeInstruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Discharge Instruction Repository.
 *
 * Data access layer for discharge instruction operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface DischargeInstructionRepository extends JpaRepository<DischargeInstruction, UUID> {

    @Query("SELECT di FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId ORDER BY di.displayOrder, di.instructionCategory")
    List<DischargeInstruction> findByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT di FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId AND di.instructionCategory = :category ORDER BY di.displayOrder")
    List<DischargeInstruction> findByDischargeSummaryIdAndCategory(
        @Param("summaryId") UUID summaryId,
        @Param("category") String category
    );

    @Query("SELECT di FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId AND di.isCriticalInstruction = true ORDER BY di.displayOrder")
    List<DischargeInstruction> findCriticalInstructionsByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT di FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId AND di.patientEducated = false")
    List<DischargeInstruction> findPendingEducationByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT di FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId AND di.patientDemonstratesUnderstanding = false")
    List<DischargeInstruction> findWithPoorUnderstandingByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT di FROM DischargeInstruction di WHERE di.instructionCategory = :category")
    List<DischargeInstruction> findByCategory(@Param("category") String category);

    @Query("SELECT COUNT(di) FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId")
    long countByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT COUNT(di) FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId AND di.isCriticalInstruction = true")
    long countCriticalByDischargeSummaryId(@Param("summaryId") UUID summaryId);

    @Query("SELECT COUNT(di) FROM DischargeInstruction di WHERE di.dischargeSummary.id = :summaryId AND di.patientEducated = false")
    long countPendingEducationByDischargeSummaryId(@Param("summaryId") UUID summaryId);
}
