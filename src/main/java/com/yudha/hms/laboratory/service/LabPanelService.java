package com.yudha.hms.laboratory.service;

import com.yudha.hms.laboratory.entity.LabPanel;
import com.yudha.hms.laboratory.entity.LabPanelItem;
import com.yudha.hms.laboratory.entity.LabTest;
import com.yudha.hms.laboratory.repository.LabPanelRepository;
import com.yudha.hms.laboratory.repository.LabPanelItemRepository;
import com.yudha.hms.laboratory.repository.LabTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for Laboratory Panel (Test Package) operations.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LabPanelService {

    private final LabPanelRepository labPanelRepository;
    private final LabPanelItemRepository labPanelItemRepository;
    private final LabTestRepository labTestRepository;

    /**
     * Create new lab panel
     */
    public LabPanel createPanel(LabPanel panel) {
        log.info("Creating new lab panel: {}", panel.getName()); // Use getName(), not getPanelName()

        // Validate unique code
        if (labPanelRepository.findByPanelCodeAndDeletedAtIsNull(panel.getPanelCode()).isPresent()) {
            throw new IllegalArgumentException("Panel code already exists: " + panel.getPanelCode());
        }

        LabPanel saved = labPanelRepository.save(panel);
        log.info("Lab panel created successfully with ID: {}", saved.getId());
        return saved;
    }

    /**
     * Create panel with tests
     */
    public LabPanel createPanelWithTests(LabPanel panel, List<UUID> testIds) {
        log.info("Creating lab panel with {} tests: {}", testIds.size(), panel.getName()); // Use getName(), not getPanelName()

        // Create the panel first
        LabPanel savedPanel = createPanel(panel);

        // Add tests to panel
        int sequence = 1;
        for (UUID testId : testIds) {
            LabTest test = labTestRepository.findByIdAndDeletedAtIsNull(testId)
                    .orElseThrow(() -> new IllegalArgumentException("Test not found: " + testId));

            LabPanelItem item = LabPanelItem.builder()
                    .panel(savedPanel)
                    .test(test)
                    .displayOrder(sequence++) // Use displayOrder, not sequence
                    // TODO: LabPanelItem does not have testCode and testName fields
                    // .testCode(test.getTestCode())
                    // .testName(test.getName())
                    .isMandatory(true)
                    // TODO: LabPanelItem does not have includeInReport field
                    // .includeInReport(true)
                    .build();

            labPanelItemRepository.save(item);
        }

        // Calculate package price (sum of all test costs)
        BigDecimal totalCost = calculatePanelCost(savedPanel.getId());
        savedPanel.setPackagePrice(totalCost);
        savedPanel = labPanelRepository.save(savedPanel);

        log.info("Lab panel created with tests successfully: {}", savedPanel.getId());
        return savedPanel;
    }

    /**
     * Update existing lab panel
     */
    public LabPanel updatePanel(UUID id, LabPanel panelUpdate) {
        log.info("Updating lab panel: {}", id);

        LabPanel existing = labPanelRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Panel not found: " + id));

        // Check panel code uniqueness
        if (!existing.getPanelCode().equals(panelUpdate.getPanelCode())) {
            if (labPanelRepository.findByPanelCodeAndDeletedAtIsNull(panelUpdate.getPanelCode()).isPresent()) {
                throw new IllegalArgumentException("Panel code already exists: " + panelUpdate.getPanelCode());
            }
        }

        // Update fields
        existing.setPanelCode(panelUpdate.getPanelCode());
        existing.setName(panelUpdate.getName()); // Use setName(), not setPanelName()
        existing.setDescription(panelUpdate.getDescription());
        existing.setPackagePrice(panelUpdate.getPackagePrice());
        existing.setBpjsPackageTariff(panelUpdate.getBpjsPackageTariff());
        existing.setDiscountPercentage(panelUpdate.getDiscountPercentage());
        // TODO: LabPanel does not have validityDays field
        // existing.setValidityDays(panelUpdate.getValidityDays());
        existing.setNotes(panelUpdate.getNotes());
        existing.setActive(panelUpdate.getActive());

        LabPanel updated = labPanelRepository.save(existing);
        log.info("Lab panel updated successfully: {}", id);
        return updated;
    }

    /**
     * Delete (soft delete) lab panel
     */
    public void deletePanel(UUID id, String deletedBy) {
        log.info("Deleting lab panel: {}", id);

        LabPanel panel = labPanelRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Panel not found: " + id));

        // Soft delete
        panel.setDeletedAt(LocalDateTime.now());
        panel.setDeletedBy(deletedBy);
        labPanelRepository.save(panel);

        log.info("Lab panel deleted successfully: {}", id);
    }

    /**
     * Get panel by ID
     */
    @Transactional(readOnly = true)
    public LabPanel getPanelById(UUID id) {
        return labPanelRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Panel not found: " + id));
    }

    /**
     * Get panel by code
     */
    @Transactional(readOnly = true)
    public LabPanel getPanelByCode(String panelCode) {
        return labPanelRepository.findByPanelCodeAndDeletedAtIsNull(panelCode)
                .orElseThrow(() -> new IllegalArgumentException("Panel not found with code: " + panelCode));
    }

    /**
     * Get all active panels
     */
    @Transactional(readOnly = true)
    public List<LabPanel> getAllActivePanels() {
        return labPanelRepository.findByActiveTrueAndDeletedAtIsNullOrderByNameAsc();
    }

    /**
     * Get panels by category
     */
    @Transactional(readOnly = true)
    public List<LabPanel> getPanelsByCategory(UUID categoryId) {
        return labPanelRepository.findByCategoryIdAndActiveTrueAndDeletedAtIsNullOrderByNameAsc(categoryId);
    }

    /**
     * Search panels
     */
    @Transactional(readOnly = true)
    public Page<LabPanel> searchPanels(String search, Pageable pageable) {
        return labPanelRepository.searchPanels(search, pageable);
    }

    /**
     * Get all panels with pagination
     */
    @Transactional(readOnly = true)
    public Page<LabPanel> getAllPanels(Pageable pageable) {
        return labPanelRepository.findByDeletedAtIsNull(pageable);
    }

    /**
     * Activate panel
     */
    public void activatePanel(UUID id) {
        log.info("Activating lab panel: {}", id);
        LabPanel panel = getPanelById(id);
        panel.setActive(true);
        labPanelRepository.save(panel);
        log.info("Lab panel activated: {}", id);
    }

    /**
     * Deactivate panel
     */
    public void deactivatePanel(UUID id) {
        log.info("Deactivating lab panel: {}", id);
        LabPanel panel = getPanelById(id);
        panel.setActive(false);
        labPanelRepository.save(panel);
        log.info("Lab panel deactivated: {}", id);
    }

    /**
     * Add test to panel
     */
    public LabPanelItem addTestToPanel(UUID panelId, UUID testId, boolean isMandatory) {
        log.info("Adding test {} to panel {}", testId, panelId);

        LabPanel panel = getPanelById(panelId);
        LabTest test = labTestRepository.findByIdAndDeletedAtIsNull(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + testId));

        // Check if test already exists in panel
        if (labPanelItemRepository.findByPanelIdAndTestId(panelId, testId).isPresent()) {
            throw new IllegalArgumentException("Test already exists in this panel");
        }

        // Get next sequence number
        int nextSequence = (int) (labPanelItemRepository.countByPanelId(panelId) + 1);

        LabPanelItem item = LabPanelItem.builder()
                .panel(panel)
                .test(test)
                .displayOrder(nextSequence) // Use displayOrder, not sequence
                // TODO: LabPanelItem does not have testCode and testName fields
                // .testCode(test.getTestCode())
                // .testName(test.getName())
                .isMandatory(isMandatory)
                // TODO: LabPanelItem does not have includeInReport field
                // .includeInReport(true)
                .build();

        LabPanelItem saved = labPanelItemRepository.save(item);

        // Recalculate package price
        BigDecimal totalCost = calculatePanelCost(panelId);
        panel.setPackagePrice(totalCost);
        labPanelRepository.save(panel);

        log.info("Test added to panel successfully");
        return saved;
    }

    /**
     * Remove test from panel
     */
    public void removeTestFromPanel(UUID panelId, UUID testId) {
        log.info("Removing test {} from panel {}", testId, panelId);

        LabPanelItem item = labPanelItemRepository.findByPanelIdAndTestId(panelId, testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found in panel"));

        labPanelItemRepository.delete(item);

        // Recalculate package price
        LabPanel panel = getPanelById(panelId);
        BigDecimal totalCost = calculatePanelCost(panelId);
        panel.setPackagePrice(totalCost);
        labPanelRepository.save(panel);

        log.info("Test removed from panel successfully");
    }

    /**
     * Get panel items (tests in panel)
     */
    @Transactional(readOnly = true)
    public List<LabPanelItem> getPanelItems(UUID panelId) {
        // TODO: Repository method may not exist - implement in repository if needed
        return labPanelItemRepository.findByPanelId(panelId);
    }

    /**
     * Get mandatory tests in panel
     */
    @Transactional(readOnly = true)
    public List<LabPanelItem> getMandatoryPanelItems(UUID panelId) {
        return labPanelItemRepository.findByPanelIdAndIsMandatoryTrueOrderByDisplayOrder(panelId);
    }

    /**
     * Update panel pricing
     */
    public LabPanel updatePanelPricing(UUID id, BigDecimal packagePrice, BigDecimal bpjsPackageTariff,
                                        BigDecimal discountPercentage) {
        log.info("Updating panel pricing for: {}", id);
        LabPanel panel = getPanelById(id);
        panel.setPackagePrice(packagePrice);
        panel.setBpjsPackageTariff(bpjsPackageTariff);
        panel.setDiscountPercentage(discountPercentage);
        LabPanel updated = labPanelRepository.save(panel);
        log.info("Panel pricing updated: {}", id);
        return updated;
    }

    /**
     * Calculate panel cost (sum of all test costs)
     */
    @Transactional(readOnly = true)
    public BigDecimal calculatePanelCost(UUID panelId) {
        List<LabPanelItem> items = getPanelItems(panelId);
        BigDecimal totalCost = BigDecimal.ZERO;

        for (LabPanelItem item : items) {
            LabTest test = item.getTest();
            if (test.getBaseCost() != null) {
                totalCost = totalCost.add(test.getBaseCost());
            }
        }

        return totalCost;
    }

    /**
     * Calculate panel discount (package price vs individual tests)
     */
    @Transactional(readOnly = true)
    public BigDecimal calculatePanelDiscount(UUID panelId) {
        LabPanel panel = getPanelById(panelId);
        BigDecimal individualCost = calculatePanelCost(panelId);

        if (panel.getPackagePrice() != null && individualCost.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = individualCost.subtract(panel.getPackagePrice());
            return discount.divide(individualCost, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        }

        return BigDecimal.ZERO;
    }

    /**
     * Reorder panel items
     */
    public void reorderPanelItems(UUID panelId, List<UUID> itemIds) {
        log.info("Reordering {} items in panel {}", itemIds.size(), panelId);

        for (int i = 0; i < itemIds.size(); i++) {
            UUID itemId = itemIds.get(i);
            LabPanelItem item = labPanelItemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Panel item not found: " + itemId));
            item.setDisplayOrder(i + 1); // Use setDisplayOrder, not setSequence
            labPanelItemRepository.save(item);
        }

        log.info("Panel items reordered successfully");
    }

    /**
     * Count active panels
     */
    @Transactional(readOnly = true)
    public long countActivePanels() {
        return labPanelRepository.countByActiveTrueAndDeletedAtIsNull();
    }
}
