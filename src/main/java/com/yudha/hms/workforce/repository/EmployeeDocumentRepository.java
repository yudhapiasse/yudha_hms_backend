package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.DocumentType;
import com.yudha.hms.workforce.entity.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, UUID> {

    List<EmployeeDocument> findByEmployeeId(UUID employeeId);

    List<EmployeeDocument> findByEmployeeIdAndDocumentType(UUID employeeId, DocumentType documentType);

    Optional<EmployeeDocument> findByDocumentNumber(String documentNumber);

    List<EmployeeDocument> findByDocumentType(DocumentType documentType);

    List<EmployeeDocument> findByEmployeeIdAndIsVerifiedTrue(UUID employeeId);

    List<EmployeeDocument> findByIsVerifiedFalse();

    @Query("SELECT d FROM EmployeeDocument d WHERE d.employeeId = :employeeId AND d.documentType IN :types")
    List<EmployeeDocument> findByEmployeeAndTypes(@Param("employeeId") UUID employeeId, @Param("types") List<DocumentType> types);

    @Query("SELECT d FROM EmployeeDocument d WHERE d.expiryDate BETWEEN :startDate AND :endDate")
    List<EmployeeDocument> findExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT d FROM EmployeeDocument d WHERE d.expiryDate < :date")
    List<EmployeeDocument> findExpiredDocuments(@Param("date") LocalDate date);

    @Query("SELECT COUNT(d) FROM EmployeeDocument d WHERE d.employeeId = :employeeId AND d.isVerified = true")
    Long countVerifiedDocuments(@Param("employeeId") UUID employeeId);
}
