# Laboratory Module - Compilation Errors Fix Guide

**Date:** 2025-11-21
**Purpose:** Step-by-step guide to fix all compilation errors

## Quick Summary

The Laboratory Module implementation has created comprehensive services with ~100 compilation errors due to:
1. Missing repository methods
2. Missing entity fields
3. Missing enum values
4. Type mismatches

**Fix Strategy:** Simplify services to match existing entity capabilities (Quick Path - 2-3 hours)

## Step-by-Step Fix Process

### Phase 1: Fix Repository Methods (30 minutes)

#### 1.1 LabOrderRepository
Add missing methods:
```java
// In LabOrderRepository.java
Optional<LabOrder> findByIdAndDeletedAtIsNull(UUID id);
Optional<LabOrder> findByOrderNumberAndDeletedAtIsNull(String orderNumber);
List<LabOrder> findByPatientIdAndDeletedAtIsNullOrderByOrderDateDesc(UUID patientId);
List<LabOrder> findByEncounterIdAndDeletedAtIsNullOrderByOrderDateDesc(UUID encounterId);
Page<LabOrder> findByStatusAndDeletedAtIsNullOrderByOrderDateDesc(OrderStatus status, Pageable pageable);
List<LabOrder> findByOrderDateBetweenAndDeletedAtIsNullOrderByOrderDateDesc(LocalDateTime startDate, LocalDateTime endDate);
long countByOrderNumberStartingWith(String prefix);
long countByStatusAndDeletedAtIsNull(OrderStatus status);

@Query("SELECT o FROM LabOrder o WHERE o.status = 'PENDING' AND o.deletedAt IS NULL ORDER BY o.orderDate ASC")
Page<LabOrder> findPendingOrders(Pageable pageable);

@Query("SELECT o FROM LabOrder o WHERE o.priority IN ('URGENT', 'CITO') AND o.status NOT IN ('COMPLETED', 'CANCELLED') AND o.deletedAt IS NULL ORDER BY o.orderDate ASC")
List<LabOrder> findUrgentOrders();

@Query("SELECT o FROM LabOrder o WHERE (LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%'))) AND o.deletedAt IS NULL")
Page<LabOrder> searchOrders(@Param("search") String search, Pageable pageable);
```

#### 1.2 LabOrderItemRepository
Add missing methods:
```java
// In LabOrderItemRepository.java
List<LabOrderItem> findByOrderIdOrderByIdAsc(UUID orderId);
```

#### 1.3 LabPanelRepository
Add missing methods:
```java
// In LabPanelRepository.java
Optional<LabPanel> findByIdAndDeletedAtIsNull(UUID id);
Optional<LabPanel> findByPanelCodeAndDeletedAtIsNull(String panelCode);
List<LabPanel> findByActiveTrueAndDeletedAtIsNullOrderByPanelNameAsc();
List<LabPanel> findByCategoryIdAndActiveTrueAndDeletedAtIsNullOrderByPanelNameAsc(UUID categoryId);
Page<LabPanel> findByDeletedAtIsNull(Pageable pageable);
long countByActiveTrueAndDeletedAtIsNull();

@Query("SELECT p FROM LabPanel p WHERE (LOWER(p.panelName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.panelCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND p.deletedAt IS NULL")
Page<LabPanel> searchPanels(@Param("search") String search, Pageable pageable);
```

#### 1.4 LabPanelItemRepository
Add missing methods:
```java
// In LabPanelItemRepository.java
List<LabPanelItem> findByPanelIdOrderBySequenceAsc(UUID panelId);
List<LabPanelItem> findByPanelIdAndIsMandatoryTrueOrderBySequenceAsc(UUID panelId);
Optional<LabPanelItem> findByPanelIdAndTestId(UUID panelId, UUID testId);
long countByPanelId(UUID panelId);
```

### Phase 2: Simplify LabOrderService (45 minutes)

Remove references to non-existent fields and simplify logic:

```java
// Remove these method calls (fields don't exist in LabOrder):
- order.setExpectedTatMinutes()
- order.setApprovedAt()
- order.setApprovedBy()
- order.setSpecimenCollectedAt()
- order.setSpecimenReceivedAt()
- order.setProcessingStartedAt()
- order.setRejectedAt()
- order.setRejectedBy()
- order.setRejectionReason()
- order.getClinicalInfo()
- order.getSpecialInstructions()
- order.getRecurringFrequency()
- order.getRecurringUntil()

// Remove these from LabOrderItem builder:
- .sampleType()
- .sampleVolume()
- .containerType()
- .processingTimeMinutes()

// Simplify createOrderItem method to:
private LabOrderItem createOrderItem(LabOrder order, LabTest test, LabPanel panel) {
    LabOrderItem item = LabOrderItem.builder()
            .order(order)
            .test(test)
            .panel(panel)
            .testCode(test.getTestCode())
            .testName(test.getName())
            .unitPrice(test.getBaseCost())
            .quantity(1)
            .totalPrice(test.getBaseCost())
            .status(OrderStatus.PENDING)
            .build();
    return labOrderItemRepository.save(item);
}

// Simplify updateOrderStatus - remove switch cases for non-existent fields:
public LabOrder updateOrderStatus(UUID orderId, OrderStatus newStatus, String changedBy, String reason) {
    log.info("Updating order status: {} to {}", orderId, newStatus);

    LabOrder order = labOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

    OrderStatus previousStatus = order.getStatus();
    order.setStatus(newStatus);

    if (newStatus == OrderStatus.COMPLETED) {
        order.setCompletedAt(LocalDateTime.now());
    } else if (newStatus == OrderStatus.CANCELLED) {
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelledBy(UUID.fromString(changedBy));
        order.setCancellationReason(reason);
    }

    LabOrder updated = labOrderRepository.save(order);
    orderStatusHistoryService.recordStatusChange(updated, previousStatus, newStatus, changedBy, reason);

    return updated;
}

// Simplify approveOrder:
public LabOrder approveOrder(UUID orderId, UUID approvedBy) {
    // Just change status, don't set approval fields
    return updateOrderStatus(orderId, OrderStatus.IN_PROGRESS, approvedBy.toString(), "Order approved");
}

// Remove rejectOrder method - not supported by current OrderStatus enum

// Simplify cancelOrder:
public LabOrder cancelOrder(UUID orderId, UUID cancelledBy, String reason) {
    log.info("Cancelling order: {}", orderId);
    LabOrder order = getOrderById(orderId);

    if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
        throw new IllegalStateException("Cannot cancel order in " + order.getStatus() + " status");
    }

    return updateOrderStatus(orderId, OrderStatus.CANCELLED, cancelledBy.toString(), reason);
}

// Simplify or remove createRecurringOrder - recurring fields don't exist

// Update validateStatusTransition to use only existing enum values:
private void validateStatusTransition(OrderStatus from, OrderStatus to) {
    // Simple validation with existing enum values only
    boolean valid = switch (from) {
        case PENDING -> to != OrderStatus.PENDING;
        case IN_PROGRESS -> to == OrderStatus.COMPLETED || to == OrderStatus.CANCELLED;
        case COMPLETED, CANCELLED -> false;
    };

    if (!valid) {
        throw new IllegalStateException("Invalid status transition: " + from + " -> " + to);
    }
}

// Fix getPendingOrders signature:
@Transactional(readOnly = true)
public Page<LabOrder> getPendingOrders(Pageable pageable) {
    return labOrderRepository.findPendingOrders(pageable);
}

// Remove getOverdueOrders - no TAT fields to check
// Remove searchOrders or fix the signature
```

### Phase 3: Fix LabPanelService (30 minutes)

```java
// Fix getPanelItems - can't use panel.getPanelItems()
@Transactional(readOnly = true)
public List<LabPanelItem> getPanelItems(UUID panelId) {
    return labPanelItemRepository.findByPanelIdOrderBySequenceAsc(panelId);
}

// Fix createOrderItemsFromPanel:
private void createOrderItemsFromPanel(LabOrder order, LabPanel panel) {
    List<LabPanelItem> items = labPanelItemRepository.findByPanelIdOrderBySequenceAsc(panel.getId());
    items.forEach(panelItem -> {
        createOrderItem(order, panelItem.getTest(), panel);
    });
}

// Fix countByPanelId cast:
int nextSequence = (int) (labPanelItemRepository.countByPanelId(panelId) + 1);
```

### Phase 4: Fix LabTestCategoryService (15 minutes)

```java
// The service looks mostly correct, just ensure all fields exist in entity
// Most issues are likely just repository methods which we've already fixed
```

### Phase 5: Fix LabTestService (15 minutes)

```java
// Remove these field setters if they don't exist:
// Check LabTest entity for actual field names

// The entity has:
- testMethodology (not methodology)
// No instrumentation field

// Update the service accordingly
```

### Phase 6: Fix SpecimenService (20 minutes)

```java
// Check Specimen entity for these fields:
// Add missing fields or remove service methods that reference them:
- hemolysisDetected
- lipemiaDetected
- icterusDetected

// Fix storage temperature type:
public Specimen storeSpecimen(String barcode, String storageLocation, Double storageTemperature) {
    Specimen specimen = getSpecimenByBarcode(barcode);
    specimen.setStorageLocation(storageLocation);
    specimen.setStorageTemperature(BigDecimal.valueOf(storageTemperature));
    // ...
}
```

## Quick Command Reference

### Check Compilation
```bash
mvn clean compile -DskipTests 2>&1 | grep "ERROR" | wc -l
```

### Find Specific Errors
```bash
mvn clean compile -DskipTests 2>&1 | grep "cannot find symbol" | sort | uniq
```

### Test After Each Fix
```bash
mvn clean compile -DskipTests
```

## Priority Order

1. **Start Here:** LabOrderRepository + LabOrderItemRepository (most errors)
2. **Then:** LabPanelRepository + LabPanelItemRepository
3. **Then:** Simplify LabOrderService
4. **Then:** Fix LabPanelService
5. **Then:** Fix SpecimenService
6. **Finally:** Fix remaining services

## Expected Timeline

- Phase 1 (Repositories): 30 minutes
- Phase 2 (LabOrderService): 45 minutes
- Phase 3 (LabPanelService): 30 minutes
- Phase 4 (LabTestCategoryService): 15 minutes
- Phase 5 (LabTestService): 15 minutes
- Phase 6 (SpecimenService): 20 minutes
- **Total:** ~2.5 hours

## Success Criteria

```bash
mvn clean compile -DskipTests
# Should show: BUILD SUCCESS
```

## After All Fixes

1. Run full compilation
2. Test application startup
3. Verify all 8 services are working
4. Move to implementing remaining services:
   - LabResultService
   - ResultValidationService
   - CriticalValueAlertService
   - LabReportService
   - TatMonitoringService
   - TestUtilizationService

---

**Generated by:** Claude Code
**Project:** HMS Backend - Laboratory Module
**Version:** 1.0.0
