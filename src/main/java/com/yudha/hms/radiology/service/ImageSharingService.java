package com.yudha.hms.radiology.service;

import com.yudha.hms.radiology.constant.SharePurpose;
import com.yudha.hms.radiology.entity.ImageShareLink;
import com.yudha.hms.radiology.entity.PacsStudy;
import com.yudha.hms.radiology.entity.RadiologyOrder;
import com.yudha.hms.radiology.repository.ImageShareLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for Image Sharing operations.
 *
 * Handles secure external image sharing via token-based links.
 * Provides access control, expiration, view limits, and audit logging.
 *
 * Features:
 * - Create shareable link with token generation
 * - Password protection for links
 * - Expiration management
 * - View limit enforcement
 * - Access tracking and logging
 * - Revoke share links
 * - Query active/expired links
 * - Link validation
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageSharingService {

    private final ImageShareLinkRepository shareLinkRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a shareable link for a study.
     *
     * @param study PACS study
     * @param order Radiology order
     * @param expiresAt Expiration date/time
     * @param password Optional password
     * @param maxViews Maximum views allowed
     * @param allowDownload Allow downloads
     * @param allowPrint Allow printing
     * @param recipientEmail Recipient email
     * @param recipientName Recipient name
     * @param sharePurpose Purpose of sharing
     * @return Created share link
     */
    public ImageShareLink createShareLink(PacsStudy study, RadiologyOrder order,
                                         LocalDateTime expiresAt, String password,
                                         Integer maxViews, Boolean allowDownload,
                                         Boolean allowPrint, String recipientEmail,
                                         String recipientName, SharePurpose sharePurpose) {
        log.info("Creating share link for study: {}", study.getStudyInstanceUid());

        // Generate unique share token
        String shareToken = generateShareToken();

        // Build share link
        ImageShareLink.ImageShareLinkBuilder builder = ImageShareLink.builder()
                .study(study)
                .order(order)
                .shareToken(shareToken)
                .shareUrl(buildShareUrl(shareToken))
                .expiresAt(expiresAt)
                .maxViews(maxViews)
                .currentViews(0)
                .allowDownload(allowDownload != null ? allowDownload : false)
                .allowPrint(allowPrint != null ? allowPrint : false)
                .allowShare(false)
                .recipientEmail(recipientEmail)
                .recipientName(recipientName)
                .sharePurpose(sharePurpose)
                .isActive(true)
                .revoked(false);

        // Handle password protection
        if (password != null && !password.isEmpty()) {
            builder.passwordProtected(true);
            builder.passwordHash(passwordEncoder.encode(password));
        } else {
            builder.passwordProtected(false);
        }

        ImageShareLink shareLink = builder.build();
        ImageShareLink saved = shareLinkRepository.save(shareLink);

        log.info("Share link created successfully: {}", saved.getShareToken());
        return saved;
    }

    /**
     * Generate a unique share token.
     *
     * @return Generated token
     */
    private String generateShareToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Build share URL from token.
     *
     * @param token Share token
     * @return Share URL
     */
    private String buildShareUrl(String token) {
        // In production, this would use actual domain
        return "https://pacs.hospital.com/share/" + token;
    }

    /**
     * Get share link by ID.
     *
     * @param id Share link ID
     * @return Share link
     */
    @Transactional(readOnly = true)
    public ImageShareLink getShareLinkById(UUID id) {
        return shareLinkRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Share link not found: " + id));
    }

    /**
     * Get share link by token.
     *
     * @param token Share token
     * @return Share link
     */
    @Transactional(readOnly = true)
    public ImageShareLink getShareLinkByToken(String token) {
        return shareLinkRepository.findByShareToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Share link not found: " + token));
    }

    /**
     * Get active share link by token (not expired, not revoked).
     *
     * @param token Share token
     * @return Active share link
     */
    @Transactional(readOnly = true)
    public ImageShareLink getActiveShareLinkByToken(String token) {
        return shareLinkRepository.findByShareTokenAndIsActiveTrueAndRevokedFalse(token)
                .orElseThrow(() -> new IllegalArgumentException("Active share link not found: " + token));
    }

    /**
     * Get share links by study ID.
     *
     * @param studyId Study ID
     * @return List of share links
     */
    @Transactional(readOnly = true)
    public List<ImageShareLink> getShareLinksByStudy(UUID studyId) {
        return shareLinkRepository.findByStudyIdAndDeletedAtIsNull(studyId);
    }

    /**
     * Get active share links by study ID.
     *
     * @param studyId Study ID
     * @return List of active share links
     */
    @Transactional(readOnly = true)
    public List<ImageShareLink> getActiveShareLinksByStudy(UUID studyId) {
        return shareLinkRepository.findActiveShareLinksByStudy(studyId);
    }

    /**
     * Get all active share links.
     *
     * @return List of active share links
     */
    @Transactional(readOnly = true)
    public List<ImageShareLink> getActiveShareLinks() {
        return shareLinkRepository.findActiveShareLinks(LocalDateTime.now());
    }

    /**
     * Get expired share links.
     *
     * @return List of expired share links
     */
    @Transactional(readOnly = true)
    public List<ImageShareLink> getExpiredShareLinks() {
        return shareLinkRepository.findExpiredShareLinks(LocalDateTime.now());
    }

    /**
     * Get share links by recipient email.
     *
     * @param email Recipient email
     * @return List of share links
     */
    @Transactional(readOnly = true)
    public List<ImageShareLink> getShareLinksByRecipientEmail(String email) {
        return shareLinkRepository.findByRecipientEmail(email);
    }

    /**
     * Validate share link access.
     *
     * @param token Share token
     * @param password Optional password
     * @return True if access is valid
     */
    public boolean validateShareLinkAccess(String token, String password) {
        try {
            ImageShareLink shareLink = getActiveShareLinkByToken(token);

            // Check if expired
            if (shareLink.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("Share link expired: {}", token);
                return false;
            }

            // Check if revoked
            if (shareLink.getRevoked()) {
                log.warn("Share link revoked: {}", token);
                return false;
            }

            // Check max views
            if (shareLink.getMaxViews() != null &&
                    shareLink.getCurrentViews() >= shareLink.getMaxViews()) {
                log.warn("Share link reached max views: {}", token);
                return false;
            }

            // Check password
            if (shareLink.getPasswordProtected()) {
                if (password == null || !passwordEncoder.matches(password, shareLink.getPasswordHash())) {
                    log.warn("Invalid password for share link: {}", token);
                    return false;
                }
            }

            return true;
        } catch (IllegalArgumentException e) {
            log.warn("Share link not found: {}", token);
            return false;
        }
    }

    /**
     * Track share link access.
     *
     * @param token Share token
     * @param ipAddress IP address
     * @param userAgent User agent
     * @return Updated share link
     */
    public ImageShareLink trackAccess(String token, String ipAddress, String userAgent) {
        log.info("Tracking access for share link: {}", token);

        ImageShareLink shareLink = getShareLinkByToken(token);

        // Update view count
        shareLink.setCurrentViews(shareLink.getCurrentViews() + 1);

        // Update access timestamps
        if (shareLink.getFirstAccessedAt() == null) {
            shareLink.setFirstAccessedAt(LocalDateTime.now());
        }
        shareLink.setLastAccessedAt(LocalDateTime.now());

        // Add to access log
        Map<String, Object> accessLog = shareLink.getAccessLog();
        if (accessLog == null) {
            accessLog = new HashMap<>();
            shareLink.setAccessLog(accessLog);
        }

        Map<String, Object> accessEntry = new HashMap<>();
        accessEntry.put("timestamp", LocalDateTime.now().toString());
        accessEntry.put("ipAddress", ipAddress);
        accessEntry.put("userAgent", userAgent);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> accesses = (List<Map<String, Object>>) accessLog.get("accesses");
        if (accesses == null) {
            accesses = new java.util.ArrayList<>();
            accessLog.put("accesses", accesses);
        }
        accesses.add(accessEntry);

        return shareLinkRepository.save(shareLink);
    }

    /**
     * Revoke share link.
     *
     * @param id Share link ID
     * @param revokedBy User ID who revoked
     * @param reason Revocation reason
     * @return Updated share link
     */
    public ImageShareLink revokeShareLink(UUID id, UUID revokedBy, String reason) {
        log.info("Revoking share link: {}", id);

        ImageShareLink shareLink = getShareLinkById(id);
        shareLink.setRevoked(true);
        shareLink.setRevokedAt(LocalDateTime.now());
        shareLink.setRevokedBy(revokedBy);
        shareLink.setRevokeReason(reason);
        shareLink.setIsActive(false);

        return shareLinkRepository.save(shareLink);
    }

    /**
     * Extend share link expiration.
     *
     * @param id Share link ID
     * @param newExpiresAt New expiration date/time
     * @return Updated share link
     */
    public ImageShareLink extendExpiration(UUID id, LocalDateTime newExpiresAt) {
        log.info("Extending expiration for share link: {}", id);

        ImageShareLink shareLink = getShareLinkById(id);
        shareLink.setExpiresAt(newExpiresAt);

        return shareLinkRepository.save(shareLink);
    }

    /**
     * Update share link permissions.
     *
     * @param id Share link ID
     * @param allowDownload Allow downloads
     * @param allowPrint Allow printing
     * @return Updated share link
     */
    public ImageShareLink updatePermissions(UUID id, Boolean allowDownload, Boolean allowPrint) {
        log.info("Updating permissions for share link: {}", id);

        ImageShareLink shareLink = getShareLinkById(id);
        if (allowDownload != null) {
            shareLink.setAllowDownload(allowDownload);
        }
        if (allowPrint != null) {
            shareLink.setAllowPrint(allowPrint);
        }

        return shareLinkRepository.save(shareLink);
    }

    /**
     * Deactivate expired share links.
     *
     * @return Number of links deactivated
     */
    public int deactivateExpiredLinks() {
        log.info("Deactivating expired share links");

        List<ImageShareLink> expiredLinks = getExpiredShareLinks();
        for (ImageShareLink link : expiredLinks) {
            link.setIsActive(false);
        }

        shareLinkRepository.saveAll(expiredLinks);
        log.info("Deactivated {} expired share links", expiredLinks.size());
        return expiredLinks.size();
    }

    /**
     * Get count of active share links for study.
     *
     * @param studyId Study ID
     * @return Count of active links
     */
    @Transactional(readOnly = true)
    public long getActiveShareLinkCount(UUID studyId) {
        return shareLinkRepository.countByStudyIdAndIsActiveTrueAndRevokedFalseAndDeletedAtIsNull(studyId);
    }

    /**
     * Soft delete share link.
     *
     * @param id Share link ID
     */
    public void deleteShareLink(UUID id) {
        log.info("Deleting share link: {}", id);

        ImageShareLink shareLink = getShareLinkById(id);
        shareLink.setDeletedAt(LocalDateTime.now());
        shareLinkRepository.save(shareLink);
    }
}
