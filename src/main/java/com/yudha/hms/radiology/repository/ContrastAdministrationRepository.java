package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.ContrastType;
import com.yudha.hms.radiology.constant.ReactionSeverity;
import com.yudha.hms.radiology.entity.ContrastAdministration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ContrastAdministration entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface ContrastAdministrationRepository extends JpaRepository<ContrastAdministration, UUID> {

    /**
     * Find by order item
     */
    Optional<ContrastAdministration> findByOrderItemId(UUID orderItemId);

    /**
     * Find by patient
     */
    List<ContrastAdministration> findByPatientIdOrderByAdministeredAtDesc(UUID patientId);

    /**
     * Find by contrast type
     */
    List<ContrastAdministration> findByContrastType(ContrastType contrastType);

    /**
     * Find by batch number
     */
    List<ContrastAdministration> findByBatchNumber(String batchNumber);

    /**
     * Find administrations with reactions
     */
    @Query("SELECT ca FROM ContrastAdministration ca WHERE ca.reactionObserved = true ORDER BY ca.administeredAt DESC")
    List<ContrastAdministration> findAdministrationsWithReactions();

    /**
     * Find by reaction severity
     */
    List<ContrastAdministration> findByReactionSeverity(ReactionSeverity reactionSeverity);

    /**
     * Find severe reactions
     */
    @Query("SELECT ca FROM ContrastAdministration ca WHERE ca.reactionObserved = true AND ca.reactionSeverity = 'SEVERE' ORDER BY ca.administeredAt DESC")
    List<ContrastAdministration> findSevereReactions();

    /**
     * Find by administered by
     */
    List<ContrastAdministration> findByAdministeredByOrderByAdministeredAtDesc(UUID administeredBy);

    /**
     * Find by date range
     */
    @Query("SELECT ca FROM ContrastAdministration ca WHERE ca.administeredAt BETWEEN :startDate AND :endDate ORDER BY ca.administeredAt DESC")
    Page<ContrastAdministration> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Find patient's contrast history
     */
    @Query("SELECT ca FROM ContrastAdministration ca WHERE ca.patient.id = :patientId ORDER BY ca.administeredAt DESC")
    List<ContrastAdministration> findPatientContrastHistory(@Param("patientId") UUID patientId);

    /**
     * Count administrations with reactions
     */
    long countByReactionObservedTrue();

    /**
     * Count by contrast type
     */
    long countByContrastType(ContrastType contrastType);
}
