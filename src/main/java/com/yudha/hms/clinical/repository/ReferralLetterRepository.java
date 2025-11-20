package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.entity.ReferralLetter;
import com.yudha.hms.clinical.entity.ReferralStatus;
import com.yudha.hms.clinical.entity.ReferralType;
import com.yudha.hms.clinical.entity.ReferralUrgency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Referral Letter Repository.
 *
 * Data access layer for referral letter operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Repository
public interface ReferralLetterRepository extends JpaRepository<ReferralLetter, UUID> {

    /**
     * Find referral by referral number.
     */
    Optional<ReferralLetter> findByReferralNumber(String referralNumber);

    /**
     * Check if referral number exists.
     */
    boolean existsByReferralNumber(String referralNumber);

    /**
     * Find referral by encounter ID.
     */
    Optional<ReferralLetter> findByEncounterId(UUID encounterId);

    /**
     * Find all referrals by patient ID.
     */
    List<ReferralLetter> findByPatientIdOrderByReferralCreatedAtDesc(UUID patientId);

    /**
     * Find referrals by status.
     */
    List<ReferralLetter> findByReferralStatusOrderByReferralCreatedAtDesc(ReferralStatus status);

    /**
     * Find referrals by type.
     */
    List<ReferralLetter> findByReferralTypeOrderByReferralCreatedAtDesc(ReferralType type);

    /**
     * Find referrals by urgency level.
     */
    List<ReferralLetter> findByUrgencyOrderByReferralCreatedAtDesc(ReferralUrgency urgency);

    /**
     * Find BPJS referrals.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.isBpjsReferral = true ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findBpjsReferrals();

    /**
     * Find referrals by referring doctor.
     */
    List<ReferralLetter> findByReferringDoctorIdOrderByReferralCreatedAtDesc(UUID referringDoctorId);

    /**
     * Find referrals by referred facility.
     */
    List<ReferralLetter> findByReferredToFacilityOrderByReferralCreatedAtDesc(String referredToFacility);

    /**
     * Find unsigned referrals.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.signed = false ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findUnsignedReferrals();

    /**
     * Find referrals without generated documents.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.documentGenerated = false ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findWithoutGeneratedDocuments();

    /**
     * Find pending VClaim submissions.
     * Returns BPJS referrals that are signed but not yet submitted to VClaim.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.isBpjsReferral = true " +
           "AND rl.signed = true " +
           "AND rl.bpjsVclaimSubmitted = false " +
           "ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findPendingVClaimSubmissions();

    /**
     * Find pending PCare submissions.
     * Returns signed referrals not yet submitted to PCare.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.signed = true " +
           "AND rl.pcareSubmitted = false " +
           "ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findPendingPCareSubmissions();

    /**
     * Find pending SATUSEHAT submissions.
     * Returns signed referrals not yet submitted to SATUSEHAT.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.signed = true " +
           "AND rl.satusehatSubmitted = false " +
           "ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findPendingSatusehatSubmissions();

    /**
     * Find expired referrals.
     * Returns referrals where validUntil date has passed.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.validUntil IS NOT NULL " +
           "AND rl.validUntil < :currentDate " +
           "AND rl.referralStatus NOT IN ('COMPLETED', 'CANCELLED', 'REJECTED') " +
           "ORDER BY rl.validUntil ASC")
    List<ReferralLetter> findExpiredReferrals(@Param("currentDate") LocalDate currentDate);

    /**
     * Find referrals by date range.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.referralDate BETWEEN :startDate AND :endDate " +
           "ORDER BY rl.referralDate DESC")
    List<ReferralLetter> findByReferralDateBetween(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Find urgent and emergency referrals.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.urgency IN ('URGENT', 'EMERGENCY') " +
           "AND rl.referralStatus IN ('DRAFT', 'PENDING_SIGNATURE', 'SIGNED', 'SENT') " +
           "ORDER BY rl.urgency ASC, rl.referralCreatedAt ASC")
    List<ReferralLetter> findUrgentAndEmergencyReferrals();

    /**
     * Find pending referrals requiring action.
     * Returns referrals in draft, pending signature, or signed status.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.referralStatus IN ('DRAFT', 'PENDING_SIGNATURE', 'SIGNED') " +
           "ORDER BY rl.urgency ASC, rl.referralCreatedAt ASC")
    List<ReferralLetter> findPendingReferrals();

    /**
     * Find accepted referrals by facility.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.referredToFacility = :facility " +
           "AND rl.referralStatus = 'ACCEPTED' " +
           "ORDER BY rl.acceptanceDate DESC")
    List<ReferralLetter> findAcceptedReferralsByFacility(@Param("facility") String facility);

    /**
     * Count referrals by status and date range.
     */
    @Query("SELECT COUNT(rl) FROM ReferralLetter rl WHERE rl.referralStatus = :status " +
           "AND rl.referralDate BETWEEN :startDate AND :endDate")
    long countByStatusAndDateRange(
        @Param("status") ReferralStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count referrals by type and date range.
     */
    @Query("SELECT COUNT(rl) FROM ReferralLetter rl WHERE rl.referralType = :type " +
           "AND rl.referralDate BETWEEN :startDate AND :endDate")
    long countByTypeAndDateRange(
        @Param("type") ReferralType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Count BPJS referrals pending VClaim submission.
     */
    @Query("SELECT COUNT(rl) FROM ReferralLetter rl WHERE rl.isBpjsReferral = true " +
           "AND rl.signed = true " +
           "AND rl.bpjsVclaimSubmitted = false")
    long countPendingVClaimSubmissions();

    /**
     * Find referrals by patient and type.
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.patientId = :patientId " +
           "AND rl.referralType = :type " +
           "ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findByPatientIdAndReferralType(
        @Param("patientId") UUID patientId,
        @Param("type") ReferralType type
    );

    /**
     * Find active referrals by patient.
     * Returns referrals not in terminal states (completed, cancelled, rejected).
     */
    @Query("SELECT rl FROM ReferralLetter rl WHERE rl.patientId = :patientId " +
           "AND rl.referralStatus NOT IN ('COMPLETED', 'CANCELLED', 'REJECTED') " +
           "ORDER BY rl.referralCreatedAt DESC")
    List<ReferralLetter> findActiveReferralsByPatientId(@Param("patientId") UUID patientId);

    /**
     * Find referrals by BPJS SEP number.
     */
    Optional<ReferralLetter> findByBpjsSepNumber(String bpjsSepNumber);

    /**
     * Check if encounter has referral.
     */
    boolean existsByEncounterId(UUID encounterId);
}
