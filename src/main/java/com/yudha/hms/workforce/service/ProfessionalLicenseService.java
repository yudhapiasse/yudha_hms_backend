package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.LicenseRenewalStatus;
import com.yudha.hms.workforce.constant.LicenseType;
import com.yudha.hms.workforce.entity.ProfessionalLicense;
import com.yudha.hms.workforce.repository.ProfessionalLicenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfessionalLicenseService {

    private final ProfessionalLicenseRepository professionalLicenseRepository;

    @Transactional(readOnly = true)
    public ProfessionalLicense getLicenseById(UUID id) {
        return professionalLicenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("License not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public ProfessionalLicense getLicenseByNumber(String licenseNumber) {
        return professionalLicenseRepository.findByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new RuntimeException("License not found with number: " + licenseNumber));
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getLicensesByEmployee(UUID employeeId) {
        return professionalLicenseRepository.findByEmployeeIdAndIsActiveTrue(employeeId);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getLicensesByEmployeeAndType(UUID employeeId, LicenseType licenseType) {
        return professionalLicenseRepository.findByEmployeeIdAndLicenseTypeAndIsActiveTrue(employeeId, licenseType);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getLicensesByType(LicenseType licenseType) {
        return professionalLicenseRepository.findByLicenseTypeAndIsActiveTrue(licenseType);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getLicensesByRenewalStatus(LicenseRenewalStatus renewalStatus) {
        return professionalLicenseRepository.findByRenewalStatus(renewalStatus);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getLicensesExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return professionalLicenseRepository.findExpiringBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getLicensesExpiringSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return professionalLicenseRepository.findExpiringBetween(today, futureDate);
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getExpiredLicenses() {
        return professionalLicenseRepository.findExpiredLicenses(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<ProfessionalLicense> getLicensesNeedingReminder() {
        return professionalLicenseRepository.findLicensesNeedingReminder();
    }

    @Transactional
    public ProfessionalLicense createLicense(ProfessionalLicense license) {
        license.setIsActive(true);
        license.setRenewalReminderSent(false);
        updateRenewalStatus(license);
        return professionalLicenseRepository.save(license);
    }

    @Transactional
    public ProfessionalLicense updateLicense(UUID id, ProfessionalLicense licenseDetails) {
        ProfessionalLicense license = getLicenseById(id);

        license.setLicenseType(licenseDetails.getLicenseType());
        license.setLicenseNumber(licenseDetails.getLicenseNumber());
        license.setIssuedBy(licenseDetails.getIssuedBy());
        license.setIssueDate(licenseDetails.getIssueDate());
        license.setExpiryDate(licenseDetails.getExpiryDate());
        license.setSpecialization(licenseDetails.getSpecialization());
        license.setScopeOfPractice(licenseDetails.getScopeOfPractice());
        license.setFacilityName(licenseDetails.getFacilityName());
        license.setFacilityAddress(licenseDetails.getFacilityAddress());
        license.setDocumentUrl(licenseDetails.getDocumentUrl());
        license.setNotes(licenseDetails.getNotes());

        updateRenewalStatus(license);

        return professionalLicenseRepository.save(license);
    }

    @Transactional
    public void renewLicense(UUID id, LocalDate newIssueDate, LocalDate newExpiryDate) {
        ProfessionalLicense license = getLicenseById(id);
        license.setIssueDate(newIssueDate);
        license.setExpiryDate(newExpiryDate);
        license.setRenewalReminderSent(false);
        updateRenewalStatus(license);
        professionalLicenseRepository.save(license);
    }

    @Transactional
    public void markReminderSent(UUID id) {
        ProfessionalLicense license = getLicenseById(id);
        license.setRenewalReminderSent(true);
        professionalLicenseRepository.save(license);
    }

    @Transactional
    public void deactivateLicense(UUID id) {
        ProfessionalLicense license = getLicenseById(id);
        license.setIsActive(false);
        professionalLicenseRepository.save(license);
    }

    @Transactional
    public void activateLicense(UUID id) {
        ProfessionalLicense license = getLicenseById(id);
        license.setIsActive(true);
        professionalLicenseRepository.save(license);
    }

    @Transactional
    public void deleteLicense(UUID id) {
        professionalLicenseRepository.deleteById(id);
    }

    private void updateRenewalStatus(ProfessionalLicense license) {
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = license.getExpiryDate();

        if (expiryDate.isBefore(today)) {
            license.setRenewalStatus(LicenseRenewalStatus.EXPIRED);
        } else if (expiryDate.isBefore(today.plusDays(90))) {
            license.setRenewalStatus(LicenseRenewalStatus.EXPIRING_SOON);
        } else {
            license.setRenewalStatus(LicenseRenewalStatus.VALID);
        }
    }
}
