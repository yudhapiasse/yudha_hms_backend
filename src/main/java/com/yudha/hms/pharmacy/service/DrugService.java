package com.yudha.hms.pharmacy.service;

import com.yudha.hms.pharmacy.dto.*;
import com.yudha.hms.pharmacy.entity.Drug;
import com.yudha.hms.pharmacy.entity.DrugCategory;
import com.yudha.hms.pharmacy.entity.DrugInteraction;
import com.yudha.hms.pharmacy.entity.Supplier;
import com.yudha.hms.pharmacy.repository.DrugCategoryRepository;
import com.yudha.hms.pharmacy.repository.DrugInteractionRepository;
import com.yudha.hms.pharmacy.repository.DrugRepository;
import com.yudha.hms.pharmacy.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Drug Service.
 *
 * Business logic for drug master data management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DrugService {

    private final DrugRepository drugRepository;
    private final DrugCategoryRepository drugCategoryRepository;
    private final DrugInteractionRepository drugInteractionRepository;
    private final SupplierRepository supplierRepository;

    // ==================== Drug Operations ====================

    /**
     * Create new drug
     */
    @Transactional
    public DrugResponse createDrug(CreateDrugRequest request, String createdBy) {
        log.info("Creating drug with code: {}", request.getDrugCode());

        // Validate drug code uniqueness
        if (drugRepository.existsByDrugCode(request.getDrugCode())) {
            throw new IllegalArgumentException("Drug code already exists: " + request.getDrugCode());
        }

        // Validate barcode uniqueness if provided
        if (request.getBarcode() != null && drugRepository.existsByBarcode(request.getBarcode())) {
            throw new IllegalArgumentException("Barcode already exists: " + request.getBarcode());
        }

        // Validate category exists
        DrugCategory category = drugCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Drug category not found: " + request.getCategoryId()));

        // Validate supplier if provided
        Supplier supplier = null;
        if (request.getPrimarySupplierId() != null) {
            supplier = supplierRepository.findById(request.getPrimarySupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + request.getPrimarySupplierId()));
        }

        // Create drug entity
        Drug drug = Drug.builder()
                .drugCode(request.getDrugCode())
                .genericName(request.getGenericName())
                .brandName(request.getBrandName())
                .strength(request.getStrength())
                .dosageForm(request.getDosageForm())
                .routeOfAdministration(request.getRouteOfAdministration())
                .category(category)
                .unit(request.getUnit())
                .storageCondition(request.getStorageCondition())
                .storageTemperature(request.getStorageTemperature())
                .storageInstructions(request.getStorageInstructions())
                .formulariumStatus(request.getFormulariumStatus())
                .bpjsDrugCode(request.getBpjsDrugCode())
                .registrationNumber(request.getRegistrationNumber())
                .registrationExpiryDate(request.getRegistrationExpiryDate())
                .isNarcotic(request.getIsNarcotic())
                .isPsychotropic(request.getIsPsychotropic())
                .isHighAlert(request.getIsHighAlert())
                .requiresPrescription(request.getRequiresPrescription())
                .minimumStockLevel(request.getMinimumStockLevel())
                .maximumStockLevel(request.getMaximumStockLevel())
                .reorderQuantity(request.getReorderQuantity())
                .currentStock(request.getCurrentStock())
                .unitPrice(request.getUnitPrice())
                .bpjsUnitPrice(request.getBpjsUnitPrice())
                .primarySupplierId(request.getPrimarySupplierId())
                .primarySupplierName(supplier != null ? supplier.getName() : null)
                .barcode(request.getBarcode())
                .nationalDrugCode(request.getNationalDrugCode())
                .atcCode(request.getAtcCode())
                .manufacturer(request.getManufacturerName())
                .indications(request.getIndications())
                .contraindications(request.getContraindications())
                .sideEffects(request.getSideEffects())
                .dosageInstructions(request.getDosageInstructions())
                .warnings(request.getWarnings())
                .active(request.getActive())
                .build();

        drug.setCreatedBy(createdBy);
        Drug savedDrug = drugRepository.save(drug);

        log.info("Drug created successfully with ID: {}", savedDrug.getId());
        return mapToResponse(savedDrug);
    }

    /**
     * Get drug by ID
     */
    @Transactional(readOnly = true)
    public DrugResponse getDrugById(UUID id) {
        Drug drug = drugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found: " + id));
        return mapToResponse(drug);
    }

    /**
     * Get drug by code
     */
    @Transactional(readOnly = true)
    public DrugResponse getDrugByCode(String code) {
        Drug drug = drugRepository.findByDrugCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found with code: " + code));
        return mapToResponse(drug);
    }

    /**
     * Update drug
     */
    @Transactional
    public DrugResponse updateDrug(UUID id, UpdateDrugRequest request, String updatedBy) {
        log.info("Updating drug: {}", id);

        Drug drug = drugRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Drug not found: " + id));

        // Update fields if provided
        if (request.getBrandName() != null) {
            drug.setBrandName(request.getBrandName());
        }
        if (request.getStrength() != null) {
            drug.setStrength(request.getStrength());
        }
        if (request.getDosageForm() != null) {
            drug.setDosageForm(request.getDosageForm());
        }
        if (request.getRouteOfAdministration() != null) {
            drug.setRouteOfAdministration(request.getRouteOfAdministration());
        }
        if (request.getCategoryId() != null) {
            DrugCategory category = drugCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Drug category not found: " + request.getCategoryId()));
            drug.setCategory(category);
        }
        if (request.getUnit() != null) {
            drug.setUnit(request.getUnit());
        }
        if (request.getStorageCondition() != null) {
            drug.setStorageCondition(request.getStorageCondition());
        }
        if (request.getStorageTemperature() != null) {
            drug.setStorageTemperature(request.getStorageTemperature());
        }
        if (request.getStorageInstructions() != null) {
            drug.setStorageInstructions(request.getStorageInstructions());
        }
        if (request.getFormulariumStatus() != null) {
            drug.setFormulariumStatus(request.getFormulariumStatus());
        }
        if (request.getBpjsDrugCode() != null) {
            drug.setBpjsDrugCode(request.getBpjsDrugCode());
        }
        if (request.getRegistrationNumber() != null) {
            drug.setRegistrationNumber(request.getRegistrationNumber());
        }
        if (request.getRegistrationExpiryDate() != null) {
            drug.setRegistrationExpiryDate(request.getRegistrationExpiryDate());
        }
        if (request.getIsNarcotic() != null) {
            drug.setIsNarcotic(request.getIsNarcotic());
        }
        if (request.getIsPsychotropic() != null) {
            drug.setIsPsychotropic(request.getIsPsychotropic());
        }
        if (request.getIsHighAlert() != null) {
            drug.setIsHighAlert(request.getIsHighAlert());
        }
        if (request.getRequiresPrescription() != null) {
            drug.setRequiresPrescription(request.getRequiresPrescription());
        }
        if (request.getMinimumStockLevel() != null) {
            drug.setMinimumStockLevel(request.getMinimumStockLevel());
        }
        if (request.getMaximumStockLevel() != null) {
            drug.setMaximumStockLevel(request.getMaximumStockLevel());
        }
        if (request.getReorderQuantity() != null) {
            drug.setReorderQuantity(request.getReorderQuantity());
        }
        if (request.getCurrentStock() != null) {
            drug.setCurrentStock(request.getCurrentStock());
        }
        if (request.getUnitPrice() != null) {
            drug.setUnitPrice(request.getUnitPrice());
        }
        if (request.getBpjsUnitPrice() != null) {
            drug.setBpjsUnitPrice(request.getBpjsUnitPrice());
        }
        if (request.getPrimarySupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getPrimarySupplierId())
                    .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + request.getPrimarySupplierId()));
            drug.setPrimarySupplierId(request.getPrimarySupplierId());
            drug.setPrimarySupplierName(supplier.getName());
        }
        if (request.getBarcode() != null) {
            // Validate barcode uniqueness
            if (!request.getBarcode().equals(drug.getBarcode()) &&
                drugRepository.existsByBarcode(request.getBarcode())) {
                throw new IllegalArgumentException("Barcode already exists: " + request.getBarcode());
            }
            drug.setBarcode(request.getBarcode());
        }
        if (request.getNationalDrugCode() != null) {
            drug.setNationalDrugCode(request.getNationalDrugCode());
        }
        if (request.getAtcCode() != null) {
            drug.setAtcCode(request.getAtcCode());
        }
        if (request.getManufacturerName() != null) {
            drug.setManufacturer(request.getManufacturerName());
        }
        if (request.getIndications() != null) {
            drug.setIndications(request.getIndications());
        }
        if (request.getContraindications() != null) {
            drug.setContraindications(request.getContraindications());
        }
        if (request.getSideEffects() != null) {
            drug.setSideEffects(request.getSideEffects());
        }
        if (request.getDosageInstructions() != null) {
            drug.setDosageInstructions(request.getDosageInstructions());
        }
        if (request.getWarnings() != null) {
            drug.setWarnings(request.getWarnings());
        }
        if (request.getIsDiscontinued() != null) {
            drug.setIsDiscontinued(request.getIsDiscontinued());
        }
        if (request.getDiscontinuationDate() != null) {
            drug.setDiscontinuationDate(request.getDiscontinuationDate());
        }
        if (request.getActive() != null) {
            drug.setActive(request.getActive());
        }

        drug.setUpdatedBy(updatedBy);
        Drug updatedDrug = drugRepository.save(drug);

        log.info("Drug updated successfully: {}", id);
        return mapToResponse(updatedDrug);
    }

    /**
     * Search drugs
     */
    @Transactional(readOnly = true)
    public List<DrugResponse> searchDrugs(String searchTerm) {
        List<Drug> drugs = drugRepository.searchDrugs(searchTerm);
        return drugs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all active drugs
     */
    @Transactional(readOnly = true)
    public List<DrugResponse> getActiveDrugs() {
        List<Drug> drugs = drugRepository.findByActiveOrderByGenericName(true);
        return drugs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get low stock drugs
     */
    @Transactional(readOnly = true)
    public List<DrugResponse> getLowStockDrugs() {
        List<Drug> drugs = drugRepository.findLowStockDrugs();
        return drugs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get BPJS approved drugs
     */
    @Transactional(readOnly = true)
    public List<DrugResponse> getBpjsApprovedDrugs() {
        List<Drug> drugs = drugRepository.findBpjsApprovedDrugs();
        return drugs.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check drug interactions for a list of drugs
     */
    @Transactional(readOnly = true)
    public List<DrugInteractionResponse> checkDrugInteractions(List<UUID> drugIds) {
        List<DrugInteraction> interactions = drugInteractionRepository.findInteractionsByDrugList(drugIds);
        return interactions.stream()
                .map(this::mapToInteractionResponse)
                .collect(Collectors.toList());
    }

    // ==================== Drug Category Operations ====================

    /**
     * Create drug category
     */
    @Transactional
    public DrugCategoryResponse createDrugCategory(CreateDrugCategoryRequest request, String createdBy) {
        log.info("Creating drug category with code: {}", request.getCode());

        if (drugCategoryRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Drug category code already exists: " + request.getCode());
        }

        DrugCategory parent = null;
        Integer level = 0;

        if (request.getParentId() != null) {
            parent = drugCategoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found: " + request.getParentId()));
            level = parent.getLevel() + 1;
        }

        DrugCategory category = DrugCategory.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .parent(parent)
                .level(level)
                .displayOrder(request.getDisplayOrder())
                .active(request.getActive())
                .build();

        category.setCreatedBy(createdBy);
        DrugCategory savedCategory = drugCategoryRepository.save(category);

        log.info("Drug category created successfully with ID: {}", savedCategory.getId());
        return mapToCategoryResponse(savedCategory);
    }

    /**
     * Get all root categories
     */
    @Transactional(readOnly = true)
    public List<DrugCategoryResponse> getRootCategories() {
        List<DrugCategory> categories = drugCategoryRepository.findRootCategories();
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get subcategories
     */
    @Transactional(readOnly = true)
    public List<DrugCategoryResponse> getSubcategories(UUID parentId) {
        DrugCategory parent = drugCategoryRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent category not found: " + parentId));

        List<DrugCategory> categories = drugCategoryRepository.findByParentOrderByDisplayOrder(parent);
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    // ==================== Supplier Operations ====================

    /**
     * Create supplier
     */
    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request, String createdBy) {
        log.info("Creating supplier with code: {}", request.getCode());

        if (supplierRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Supplier code already exists: " + request.getCode());
        }

        Supplier supplier = Supplier.builder()
                .code(request.getCode())
                .name(request.getName())
                .legalName(request.getLegalName())
                .taxId(request.getTaxId())
                .licenseNumber(request.getLicenseNumber())
                .contactPerson(request.getContactPerson())
                .contactPhone(request.getContactPhone())
                .contactEmail(request.getContactEmail())
                .contactFax(request.getContactFax())
                .address(request.getAddress())
                .city(request.getCity())
                .province(request.getProvince())
                .postalCode(request.getPostalCode())
                .website(request.getWebsite())
                .paymentTermsDays(request.getPaymentTermsDays())
                .deliveryLeadTimeDays(request.getDeliveryLeadTimeDays())
                .minimumOrderValue(request.getMinimumOrderValue())
                .bankName(request.getBankName())
                .bankAccountNumber(request.getBankAccountNumber())
                .bankAccountHolder(request.getBankAccountHolder())
                .rating(request.getRating())
                .isPreferred(request.getIsPreferred())
                .notes(request.getNotes())
                .active(request.getActive())
                .build();

        supplier.setCreatedBy(createdBy);
        Supplier savedSupplier = supplierRepository.save(supplier);

        log.info("Supplier created successfully with ID: {}", savedSupplier.getId());
        return mapToSupplierResponse(savedSupplier);
    }

    /**
     * Get all active suppliers
     */
    @Transactional(readOnly = true)
    public List<SupplierResponse> getActiveSuppliers() {
        List<Supplier> suppliers = supplierRepository.findByActiveOrderByName(true);
        return suppliers.stream()
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search suppliers by name
     */
    @Transactional(readOnly = true)
    public List<SupplierResponse> searchSuppliers(String name) {
        List<Supplier> suppliers = supplierRepository.searchByName(name);
        return suppliers.stream()
                .map(this::mapToSupplierResponse)
                .collect(Collectors.toList());
    }

    // ==================== Mapping Methods ====================

    /**
     * Map Drug entity to DrugResponse
     */
    private DrugResponse mapToResponse(Drug drug) {
        return DrugResponse.builder()
                .id(drug.getId())
                .drugCode(drug.getDrugCode())
                .genericName(drug.getGenericName())
                .brandName(drug.getBrandName())
                .fullName(drug.getFullName())
                .strength(drug.getStrength())
                .dosageForm(drug.getDosageForm())
                .routeOfAdministration(drug.getRouteOfAdministration())
                .category(drug.getCategory() != null ? mapToCategoryResponse(drug.getCategory()) : null)
                .unit(drug.getUnit())
                .unitDescription(null)
                .storageCondition(drug.getStorageCondition())
                .storageTemperature(drug.getStorageTemperature())
                .storageInstructions(drug.getStorageInstructions())
                .requiresColdChain(drug.requiresColdChain())
                .formulariumStatus(drug.getFormulariumStatus())
                .bpjsDrugCode(drug.getBpjsDrugCode())
                .isBpjsCovered(drug.getFormulariumStatus() != null && drug.getFormulariumStatus().isBpjsCovered())
                .registrationNumber(drug.getRegistrationNumber())
                .registrationExpiryDate(drug.getRegistrationExpiryDate())
                .isRegistrationExpired(drug.isRegistrationExpired())
                .isNarcotic(drug.getIsNarcotic())
                .isPsychotropic(drug.getIsPsychotropic())
                .isHighAlert(drug.getIsHighAlert())
                .isControlledSubstance(drug.isControlledSubstance())
                .requiresPrescription(drug.getRequiresPrescription())
                .minimumStockLevel(drug.getMinimumStockLevel())
                .maximumStockLevel(drug.getMaximumStockLevel())
                .reorderQuantity(drug.getReorderQuantity())
                .currentStock(drug.getCurrentStock())
                .isLowStock(drug.isLowStock())
                .unitPrice(drug.getUnitPrice())
                .bpjsUnitPrice(drug.getBpjsUnitPrice())
                .primarySupplierId(drug.getPrimarySupplierId())
                .primarySupplierName(drug.getPrimarySupplierName())
                .barcode(drug.getBarcode())
                .nationalDrugCode(drug.getNationalDrugCode())
                .atcCode(drug.getAtcCode())
                .manufacturerName(drug.getManufacturer())
                .indications(drug.getIndications())
                .contraindications(drug.getContraindications())
                .sideEffects(drug.getSideEffects())
                .dosageInstructions(drug.getDosageInstructions())
                .warnings(drug.getWarnings())
                .pregnancyCategory(null)
                .lactationSafety(null)
                .isDiscontinued(drug.getIsDiscontinued())
                .discontinuationDate(drug.getDiscontinuationDate())
                .discontinuationReason(null)
                .active(drug.getActive())
                .createdAt(drug.getCreatedAt())
                .createdBy(drug.getCreatedBy())
                .updatedAt(drug.getUpdatedAt())
                .updatedBy(drug.getUpdatedBy())
                .build();
    }

    /**
     * Map DrugCategory entity to DrugCategoryResponse
     */
    private DrugCategoryResponse mapToCategoryResponse(DrugCategory category) {
        return DrugCategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .description(category.getDescription())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .fullPath(category.getFullPath())
                .level(category.getLevel())
                .displayOrder(category.getDisplayOrder())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .updatedAt(category.getUpdatedAt())
                .updatedBy(category.getUpdatedBy())
                .build();
    }

    /**
     * Map Supplier entity to SupplierResponse
     */
    private SupplierResponse mapToSupplierResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .name(supplier.getName())
                .legalName(supplier.getLegalName())
                .taxId(supplier.getTaxId())
                .licenseNumber(supplier.getLicenseNumber())
                .contactPerson(supplier.getContactPerson())
                .contactPhone(supplier.getContactPhone())
                .contactEmail(supplier.getContactEmail())
                .contactFax(supplier.getContactFax())
                .address(supplier.getAddress())
                .city(supplier.getCity())
                .province(supplier.getProvince())
                .postalCode(supplier.getPostalCode())
                .website(supplier.getWebsite())
                .paymentTermsDays(supplier.getPaymentTermsDays())
                .deliveryLeadTimeDays(supplier.getDeliveryLeadTimeDays())
                .minimumOrderValue(supplier.getMinimumOrderValue())
                .bankName(supplier.getBankName())
                .bankAccountNumber(supplier.getBankAccountNumber())
                .bankAccountHolder(supplier.getBankAccountHolder())
                .rating(supplier.getRating())
                .isPreferred(supplier.getIsPreferred())
                .notes(supplier.getNotes())
                .active(supplier.getActive())
                .createdAt(supplier.getCreatedAt())
                .createdBy(supplier.getCreatedBy())
                .updatedAt(supplier.getUpdatedAt())
                .updatedBy(supplier.getUpdatedBy())
                .build();
    }

    /**
     * Map DrugInteraction entity to DrugInteractionResponse
     */
    private DrugInteractionResponse mapToInteractionResponse(DrugInteraction interaction) {
        return DrugInteractionResponse.builder()
                .id(interaction.getId())
                .drug1Id(interaction.getDrug1().getId())
                .drug1Name(interaction.getDrug1().getFullName())
                .drug2Id(interaction.getDrug2().getId())
                .drug2Name(interaction.getDrug2().getFullName())
                .severity(interaction.getSeverity())
                .description(interaction.getDescription())
                .clinicalEffects(interaction.getClinicalEffects())
                .management(interaction.getManagement())
                .evidenceLevel(interaction.getEvidenceLevel())
                .references(interaction.getReference())
                .active(interaction.getActive())
                .createdAt(interaction.getCreatedAt())
                .createdBy(null)
                .updatedAt(interaction.getUpdatedAt())
                .updatedBy(null)
                .build();
    }
}
