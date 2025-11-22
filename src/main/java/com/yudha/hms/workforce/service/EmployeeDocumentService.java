package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.DocumentType;
import com.yudha.hms.workforce.entity.EmployeeDocument;
import com.yudha.hms.workforce.repository.EmployeeDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeDocumentService {

    private final EmployeeDocumentRepository employeeDocumentRepository;

    @Transactional(readOnly = true)
    public EmployeeDocument getDocumentById(UUID id) {
        return employeeDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public EmployeeDocument getDocumentByNumber(String documentNumber) {
        return employeeDocumentRepository.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new RuntimeException("Document not found with number: " + documentNumber));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocument> getDocumentsByEmployee(UUID employeeId) {
        return employeeDocumentRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocument> getDocumentsByType(UUID employeeId, DocumentType documentType) {
        return employeeDocumentRepository.findByEmployeeIdAndDocumentType(employeeId, documentType);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocument> getVerifiedDocuments(UUID employeeId) {
        return employeeDocumentRepository.findByEmployeeIdAndIsVerifiedTrue(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocument> getUnverifiedDocuments() {
        return employeeDocumentRepository.findByIsVerifiedFalse();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocument> getDocumentsExpiringSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return employeeDocumentRepository.findExpiringBetween(today, futureDate);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDocument> getExpiredDocuments() {
        return employeeDocumentRepository.findExpiredDocuments(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Long countVerifiedDocuments(UUID employeeId) {
        return employeeDocumentRepository.countVerifiedDocuments(employeeId);
    }

    @Transactional
    public EmployeeDocument createDocument(EmployeeDocument document) {
        document.setIsVerified(false);
        return employeeDocumentRepository.save(document);
    }

    @Transactional
    public EmployeeDocument updateDocument(UUID id, EmployeeDocument documentDetails) {
        EmployeeDocument document = getDocumentById(id);

        document.setDocumentType(documentDetails.getDocumentType());
        document.setDocumentNumber(documentDetails.getDocumentNumber());
        document.setDocumentName(documentDetails.getDocumentName());
        document.setDescription(documentDetails.getDescription());
        document.setIssueDate(documentDetails.getIssueDate());
        document.setExpiryDate(documentDetails.getExpiryDate());
        document.setFileUrl(documentDetails.getFileUrl());
        document.setFileName(documentDetails.getFileName());
        document.setFileSizeBytes(documentDetails.getFileSizeBytes());
        document.setFileType(documentDetails.getFileType());
        document.setNotes(documentDetails.getNotes());

        return employeeDocumentRepository.save(document);
    }

    @Transactional
    public void verifyDocument(UUID id, UUID verifiedBy) {
        EmployeeDocument document = getDocumentById(id);
        document.setIsVerified(true);
        document.setVerifiedBy(verifiedBy);
        employeeDocumentRepository.save(document);
    }

    @Transactional
    public void unverifyDocument(UUID id) {
        EmployeeDocument document = getDocumentById(id);
        document.setIsVerified(false);
        document.setVerifiedBy(null);
        employeeDocumentRepository.save(document);
    }

    @Transactional
    public void deleteDocument(UUID id) {
        employeeDocumentRepository.deleteById(id);
    }
}
