package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.SharePurpose;
import com.yudha.hms.radiology.entity.ImageShareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ImageShareLink entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Repository
public interface ImageShareLinkRepository extends JpaRepository<ImageShareLink, UUID> {

    Optional<ImageShareLink> findByShareToken(String shareToken);

    Optional<ImageShareLink> findByShareTokenAndIsActiveTrueAndRevokedFalse(String shareToken);

    List<ImageShareLink> findByStudyIdAndDeletedAtIsNull(UUID studyId);

    List<ImageShareLink> findByOrderIdAndDeletedAtIsNull(UUID orderId);

    @Query("SELECT s FROM ImageShareLink s WHERE s.isActive = true AND s.revoked = false AND s.expiresAt > :now AND s.deletedAt IS NULL")
    List<ImageShareLink> findActiveShareLinks(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM ImageShareLink s WHERE s.isActive = true AND s.revoked = false AND s.expiresAt <= :now AND s.deletedAt IS NULL")
    List<ImageShareLink> findExpiredShareLinks(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM ImageShareLink s WHERE s.study.id = :studyId AND s.isActive = true AND s.revoked = false AND s.expiresAt > CURRENT_TIMESTAMP AND s.deletedAt IS NULL")
    List<ImageShareLink> findActiveShareLinksByStudy(@Param("studyId") UUID studyId);

    @Query("SELECT s FROM ImageShareLink s WHERE s.recipientEmail = :email AND s.deletedAt IS NULL ORDER BY s.createdAt DESC")
    List<ImageShareLink> findByRecipientEmail(@Param("email") String email);

    List<ImageShareLink> findBySharePurposeAndDeletedAtIsNull(SharePurpose purpose);

    @Query("SELECT s FROM ImageShareLink s WHERE s.maxViews IS NOT NULL AND s.currentViews >= s.maxViews AND s.deletedAt IS NULL")
    List<ImageShareLink> findShareLinksReachedMaxViews();

    long countByStudyIdAndIsActiveTrueAndRevokedFalseAndDeletedAtIsNull(UUID studyId);
}
