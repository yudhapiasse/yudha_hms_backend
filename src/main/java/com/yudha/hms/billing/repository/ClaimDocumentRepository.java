package com.yudha.hms.billing.repository;

import com.yudha.hms.billing.constant.DocumentType;
import com.yudha.hms.billing.entity.ClaimDocument;
import com.yudha.hms.billing.entity.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Claim Document Repository.
 *
 * Data access layer for ClaimDocument entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, UUID>,
        JpaSpecificationExecutor<ClaimDocument> {

    /**
     * Find documents by claim
     *
     * @param claim insurance claim
     * @return list of documents
     */
    List<ClaimDocument> findByClaimOrderByUploadDate(InsuranceClaim claim);

    /**
     * Find documents by claim and document type
     *
     * @param claim insurance claim
     * @param documentType document type
     * @return list of documents
     */
    List<ClaimDocument> findByClaimAndDocumentType(InsuranceClaim claim, DocumentType documentType);

    /**
     * Find verified documents by claim
     *
     * @param claim insurance claim
     * @param verified verification status
     * @return list of documents
     */
    List<ClaimDocument> findByClaimAndVerified(InsuranceClaim claim, Boolean verified);

    /**
     * Count documents by claim
     *
     * @param claim insurance claim
     * @return document count
     */
    long countByClaim(InsuranceClaim claim);

    /**
     * Count verified documents by claim
     *
     * @param claim insurance claim
     * @param verified verification status
     * @return document count
     */
    long countByClaimAndVerified(InsuranceClaim claim, Boolean verified);

    /**
     * Find documents by document type
     *
     * @param documentType document type
     * @return list of documents
     */
    List<ClaimDocument> findByDocumentTypeOrderByUploadDateDesc(DocumentType documentType);

    /**
     * Find unverified documents
     *
     * @return list of documents
     */
    @Query("SELECT d FROM ClaimDocument d WHERE d.verified = false OR d.verified IS NULL " +
           "ORDER BY d.uploadDate")
    List<ClaimDocument> findUnverifiedDocuments();

    /**
     * Check if claim has mandatory documents
     *
     * @param claimId claim ID
     * @return true if has all mandatory documents
     */
    @Query("SELECT COUNT(DISTINCT d.documentType) FROM ClaimDocument d " +
           "WHERE d.claim.id = :claimId " +
           "AND d.documentType IN ('CLAIM_FORM', 'INVOICE', 'RECEIPT', 'INSURANCE_CARD', 'ID_CARD')")
    long countMandatoryDocuments(@Param("claimId") UUID claimId);
}
