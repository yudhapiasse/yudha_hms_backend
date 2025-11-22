package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.LicenseRenewalStatus;
import com.yudha.hms.workforce.constant.LicenseType;
import com.yudha.hms.workforce.entity.ProfessionalLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfessionalLicenseRepository extends JpaRepository<ProfessionalLicense, UUID> {

    List<ProfessionalLicense> findByEmployeeId(UUID employeeId);

    List<ProfessionalLicense> findByEmployeeIdAndLicenseType(UUID employeeId, LicenseType licenseType);

    Optional<ProfessionalLicense> findByLicenseNumber(String licenseNumber);

    List<ProfessionalLicense> findByLicenseType(LicenseType licenseType);

    List<ProfessionalLicense> findByRenewalStatus(LicenseRenewalStatus renewalStatus);

    @Query("SELECT l FROM ProfessionalLicense l WHERE l.expiryDate BETWEEN :startDate AND :endDate")
    List<ProfessionalLicense> findExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT l FROM ProfessionalLicense l WHERE l.expiryDate < :date")
    List<ProfessionalLicense> findExpiredLicenses(@Param("date") LocalDate date);

    @Query("SELECT l FROM ProfessionalLicense l WHERE l.renewalStatus = 'EXPIRING_SOON' AND l.renewalReminderSent = false")
    List<ProfessionalLicense> findLicensesNeedingReminder();

    @Query("SELECT COUNT(l) FROM ProfessionalLicense l WHERE l.employeeId = :employeeId AND l.licenseType = :type AND l.isExpired = false")
    Long countActiveLicensesByEmployeeAndType(@Param("employeeId") UUID employeeId, @Param("type") LicenseType type);
}
