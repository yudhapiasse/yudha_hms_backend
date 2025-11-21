package com.yudha.hms.billing.service;

import com.yudha.hms.billing.dto.CreateInsuranceCompanyRequest;
import com.yudha.hms.billing.dto.InsuranceCompanyResponse;
import com.yudha.hms.billing.entity.InsuranceCompany;
import com.yudha.hms.billing.repository.InsuranceCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for insurance company operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceCompanyService {

    private final InsuranceCompanyRepository insuranceCompanyRepository;

    /**
     * Create new insurance company.
     *
     * @param request insurance company request
     * @param createdBy user creating
     * @return insurance company response
     */
    @Transactional
    public InsuranceCompanyResponse createInsuranceCompany(CreateInsuranceCompanyRequest request, String createdBy) {
        log.info("Creating insurance company: {}", request.getName());

        // Validate unique code
        if (insuranceCompanyRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Insurance company code already exists: " + request.getCode());
        }

        // Validate contract dates
        if (request.getContractEndDate().isBefore(request.getContractStartDate())) {
            throw new IllegalArgumentException("Contract end date must be after start date");
        }

        InsuranceCompany company = InsuranceCompany.builder()
                .code(request.getCode())
                .name(request.getName())
                .companyType(request.getCompanyType())
                .licenseNumber(request.getLicenseNumber())
                .taxId(request.getTaxId())
                .contactPerson(request.getContactPerson())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .contactFax(request.getContactFax())
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .website(request.getWebsite())
                .contractStartDate(request.getContractStartDate())
                .contractEndDate(request.getContractEndDate())
                .contractNumber(request.getContractNumber())
                .paymentTermsDays(request.getPaymentTermsDays())
                .creditLimit(request.getCreditLimit())
                .currentOutstanding(BigDecimal.ZERO)
                .defaultCoveragePercentage(request.getDefaultCoveragePercentage())
                .requiresPreAuthorization(request.getRequiresPreAuthorization())
                .claimSubmissionDeadlineDays(request.getClaimSubmissionDeadlineDays())
                .electronicClaimSupported(request.getElectronicClaimSupported())
                .claimSubmissionEmail(request.getClaimSubmissionEmail())
                .claimSubmissionPortal(request.getClaimSubmissionPortal())
                .bankName(request.getBankName())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankAccountHolder(request.getBankAccountHolder())
                .notes(request.getNotes())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        company.setCreatedBy(createdBy);
        company.setUpdatedBy(createdBy);

        company = insuranceCompanyRepository.save(company);

        log.info("Insurance company created: {}", company.getCode());
        return mapToResponse(company);
    }

    /**
     * Get insurance company by ID.
     *
     * @param companyId company ID
     * @return insurance company response
     */
    @Transactional(readOnly = true)
    public InsuranceCompanyResponse getInsuranceCompanyById(UUID companyId) {
        InsuranceCompany company = insuranceCompanyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Insurance company not found: " + companyId));
        return mapToResponse(company);
    }

    /**
     * Get insurance company by code.
     *
     * @param code company code
     * @return insurance company response
     */
    @Transactional(readOnly = true)
    public InsuranceCompanyResponse getInsuranceCompanyByCode(String code) {
        InsuranceCompany company = insuranceCompanyRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Insurance company not found: " + code));
        return mapToResponse(company);
    }

    /**
     * Get all active insurance companies.
     *
     * @return list of insurance companies
     */
    @Transactional(readOnly = true)
    public List<InsuranceCompanyResponse> getActiveInsuranceCompanies() {
        return insuranceCompanyRepository.findByActiveOrderByName(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get insurance companies with valid contracts.
     *
     * @return list of insurance companies
     */
    @Transactional(readOnly = true)
    public List<InsuranceCompanyResponse> getInsuranceCompaniesWithValidContracts() {
        return insuranceCompanyRepository.findWithValidContracts(LocalDate.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search insurance companies by name.
     *
     * @param name name pattern
     * @return list of insurance companies
     */
    @Transactional(readOnly = true)
    public List<InsuranceCompanyResponse> searchInsuranceCompanies(String name) {
        return insuranceCompanyRepository.searchByName(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update insurance company.
     *
     * @param companyId company ID
     * @param request update request
     * @param updatedBy user updating
     * @return updated insurance company
     */
    @Transactional
    public InsuranceCompanyResponse updateInsuranceCompany(UUID companyId, CreateInsuranceCompanyRequest request,
                                                           String updatedBy) {
        log.info("Updating insurance company: {}", companyId);

        InsuranceCompany company = insuranceCompanyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Insurance company not found: " + companyId));

        // Validate code change
        if (!company.getCode().equals(request.getCode())) {
            if (insuranceCompanyRepository.existsByCode(request.getCode())) {
                throw new IllegalArgumentException("Insurance company code already exists: " + request.getCode());
            }
        }

        // Update fields
        company.setCode(request.getCode());
        company.setName(request.getName());
        company.setCompanyType(request.getCompanyType());
        company.setLicenseNumber(request.getLicenseNumber());
        company.setTaxId(request.getTaxId());
        company.setContactPerson(request.getContactPerson());
        company.setContactPhone(request.getContactPhone());
        company.setContactEmail(request.getContactEmail());
        company.setContactFax(request.getContactFax());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setProvince(request.getProvince());
        company.setPostalCode(request.getPostalCode());
        company.setWebsite(request.getWebsite());
        company.setContractStartDate(request.getContractStartDate());
        company.setContractEndDate(request.getContractEndDate());
        company.setContractNumber(request.getContractNumber());
        company.setPaymentTermsDays(request.getPaymentTermsDays());
        company.setCreditLimit(request.getCreditLimit());
        company.setDefaultCoveragePercentage(request.getDefaultCoveragePercentage());
        company.setRequiresPreAuthorization(request.getRequiresPreAuthorization());
        company.setClaimSubmissionDeadlineDays(request.getClaimSubmissionDeadlineDays());
        company.setElectronicClaimSupported(request.getElectronicClaimSupported());
        company.setClaimSubmissionEmail(request.getClaimSubmissionEmail());
        company.setClaimSubmissionPortal(request.getClaimSubmissionPortal());
        company.setBankName(request.getBankName());
        company.setBankAccountNumber(request.getBankAccountNumber());
        company.setBankAccountHolder(request.getBankAccountHolder());
        company.setNotes(request.getNotes());
        company.setActive(request.getActive());
        company.setUpdatedBy(updatedBy);

        company = insuranceCompanyRepository.save(company);

        log.info("Insurance company updated: {}", company.getCode());
        return mapToResponse(company);
    }

    /**
     * Update insurance company outstanding amount.
     *
     * @param companyId company ID
     * @param amount amount to add (positive) or subtract (negative)
     * @param updatedBy user updating
     */
    @Transactional
    public void updateOutstandingAmount(UUID companyId, BigDecimal amount, String updatedBy) {
        InsuranceCompany company = insuranceCompanyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Insurance company not found: " + companyId));

        BigDecimal currentOutstanding = company.getCurrentOutstanding() != null ?
                company.getCurrentOutstanding() : BigDecimal.ZERO;

        company.setCurrentOutstanding(currentOutstanding.add(amount));
        company.setUpdatedBy(updatedBy);

        insuranceCompanyRepository.save(company);

        log.info("Updated outstanding for company {}: {} (new total: {})",
                company.getCode(), amount, company.getCurrentOutstanding());
    }

    /**
     * Deactivate insurance company.
     *
     * @param companyId company ID
     * @param updatedBy user updating
     */
    @Transactional
    public void deactivateInsuranceCompany(UUID companyId, String updatedBy) {
        log.info("Deactivating insurance company: {}", companyId);

        InsuranceCompany company = insuranceCompanyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Insurance company not found: " + companyId));

        company.setActive(false);
        company.setUpdatedBy(updatedBy);

        insuranceCompanyRepository.save(company);

        log.info("Insurance company deactivated: {}", company.getCode());
    }

    /**
     * Map insurance company entity to response DTO.
     *
     * @param company insurance company entity
     * @return insurance company response
     */
    private InsuranceCompanyResponse mapToResponse(InsuranceCompany company) {
        return InsuranceCompanyResponse.builder()
                .id(company.getId())
                .code(company.getCode())
                .name(company.getName())
                .companyType(company.getCompanyType())
                .licenseNumber(company.getLicenseNumber())
                .taxId(company.getTaxId())
                .contactPerson(company.getContactPerson())
                .contactPhone(company.getContactPhone())
                .contactEmail(company.getContactEmail())
                .contactFax(company.getContactFax())
                .address(company.getAddress())
                .city(company.getCity())
                .province(company.getProvince())
                .postalCode(company.getPostalCode())
                .website(company.getWebsite())
                .contractStartDate(company.getContractStartDate())
                .contractEndDate(company.getContractEndDate())
                .contractNumber(company.getContractNumber())
                .contractValid(company.isContractValid())
                .paymentTermsDays(company.getPaymentTermsDays())
                .creditLimit(company.getCreditLimit())
                .currentOutstanding(company.getCurrentOutstanding())
                .availableCredit(company.getAvailableCredit())
                .creditLimitExceeded(company.isCreditLimitExceeded())
                .defaultCoveragePercentage(company.getDefaultCoveragePercentage())
                .requiresPreAuthorization(company.getRequiresPreAuthorization())
                .claimSubmissionDeadlineDays(company.getClaimSubmissionDeadlineDays())
                .electronicClaimSupported(company.getElectronicClaimSupported())
                .claimSubmissionEmail(company.getClaimSubmissionEmail())
                .claimSubmissionPortal(company.getClaimSubmissionPortal())
                .bankName(company.getBankName())
                .bankAccountNumber(company.getBankAccountNumber())
                .bankAccountHolder(company.getBankAccountHolder())
                .notes(company.getNotes())
                .active(company.getActive())
                .createdAt(company.getCreatedAt())
                .createdBy(company.getCreatedBy())
                .updatedAt(company.getUpdatedAt())
                .updatedBy(company.getUpdatedBy())
                .build();
    }
}
