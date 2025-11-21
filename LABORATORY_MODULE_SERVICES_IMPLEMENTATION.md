# Laboratory Module - Services Implementation

**Date:** 2025-11-21
**Status:** IN PROGRESS
**Phase:** Service Layer Implementation

## Overview

Comprehensive implementation of the Laboratory Module service layer, providing business logic for test management, order processing, specimen tracking, results validation, and reporting.

## Implemented Services

### Phase 1: Test Master Services (COMPLETED)

#### 1. LabTestCategoryService
**File:** `src/main/java/com/yudha/hms/laboratory/service/LabTestCategoryService.java`

**Features:**
- Create, update, delete (soft delete) test categories
- Hierarchical category management with parent-child relationships
- Circular reference prevention
- Category activation/deactivation
- Search and pagination support
- Reordering capabilities
- Validation of unique codes
- Check for active tests before deletion

**Key Methods:**
```java
createCategory(LabTestCategory category)
updateCategory(UUID id, LabTestCategory categoryUpdate)
deleteCategory(UUID id, String deletedBy)
getCategoryById(UUID id)
getAllActiveCategories()
getRootCategories()
getChildCategories(UUID parentId)
searchCategories(String search, Pageable pageable)
reorderCategories(List<UUID> categoryIds)
```

#### 2. LabTestService
**File:** `src/main/java/com/yudha/hms/laboratory/service/LabTestService.java`

**Features:**
- Complete test catalog management
- Test creation with parameters
- LOINC code validation and uniqueness
- Category assignment and validation
- Price management (base cost, BPJS tariff)
- Sample type and volume specifications
- Processing time (TAT) configuration
- Test activation/deactivation
- Search by code, name, LOINC, category, sample type
- Comprehensive test metadata management

**Key Methods:**
```java
createTest(LabTest test)
createTestWithParameters(LabTest test, List<LabTestParameter> parameters)
updateTest(UUID id, LabTest testUpdate)
deleteTest(UUID id, String deletedBy)
getTestByCode(String testCode)
getTestByLoincCode(String loincCode)
getAllActiveTests()
getTestsByCategory(UUID categoryId)
getTestsBySampleType(String sampleType)
searchTests(String search, Pageable pageable)
updateTestPricing(UUID id, BigDecimal baseCost, BigDecimal bpjsTariff)
getTestParameters(UUID testId)
```

#### 3. LabTestParameterService
**File:** `src/main/java/com/yudha/hms/laboratory/service/LabTestParameterService.java`

**Features:**
- Test parameter CRUD operations
- Normal range configuration (numeric ranges)
- Age/gender-specific ranges (JSONB support)
- Critical and panic value definitions
- Delta check configuration
- Calculated parameter support
- Allowed values for option-based parameters
- Parameter display order management
- Value validation against ranges

**Key Methods:**
```java
createParameter(LabTestParameter parameter)
updateParameter(UUID id, LabTestParameter parameterUpdate)
deleteParameter(UUID id, String deletedBy)
getParametersByTest(UUID testId)
getMandatoryParametersByTest(UUID testId)
getParametersWithDeltaCheck(UUID testId)
updateNormalRanges(UUID id, BigDecimal low, BigDecimal high, String text)
updateCriticalValues(UUID id, BigDecimal criticalLow, BigDecimal criticalHigh, BigDecimal panicLow, BigDecimal panicHigh)
updateDeltaCheckConfig(UUID id, Boolean enabled, BigDecimal percentage, BigDecimal absolute)
isValueNormal(UUID parameterId, BigDecimal value)
isValueCritical(UUID parameterId, BigDecimal value)
isValuePanic(UUID parameterId, BigDecimal value)
```

#### 4. LabPanelService
**File:** `src/main/java/com/yudha/hms/laboratory/service/LabPanelService.java`

**Features:**
- Test panel (package) management
- Panel creation with multiple tests
- Dynamic test addition/removal
- Package pricing and discount calculation
- Test sequence management within panels
- Mandatory vs optional test configuration
- Report inclusion settings
- Automatic cost calculation
- Discount percentage tracking

**Key Methods:**
```java
createPanel(LabPanel panel)
createPanelWithTests(LabPanel panel, List<UUID> testIds)
updatePanel(UUID id, LabPanel panelUpdate)
addTestToPanel(UUID panelId, UUID testId, boolean isMandatory)
removeTestFromPanel(UUID panelId, UUID testId)
getPanelItems(UUID panelId)
updatePanelPricing(UUID id, BigDecimal packagePrice, BigDecimal bpjsPackageTariff, BigDecimal discountPercentage)
calculatePanelCost(UUID panelId)
calculatePanelDiscount(UUID panelId)
reorderPanelItems(UUID panelId, List<UUID> itemIds)
```

### Phase 2: Order Management Services (COMPLETED)

#### 5. LabOrderService
**File:** `src/main/java/com/yudha/hms/laboratory/service/LabOrderService.java`

**Features:**
- Electronic lab order creation
- Order number generation (format: LO + YYYYMMDD + sequence)
- Multi-test and panel ordering
- Priority-based ordering (ROUTINE, URGENT, CITO)
- Status workflow management with validation
- Order approval/rejection workflow
- Cancellation with reason tracking
- Recurring order support (daily, weekly, monthly)
- Expected TAT calculation based on priority
- Order item management
- Total cost calculation
- Overdue order detection

**Key Methods:**
```java
createOrder(LabOrder order, List<UUID> testIds, List<UUID> panelIds)
updateOrderStatus(UUID orderId, OrderStatus newStatus, String changedBy, String reason)
approveOrder(UUID orderId, UUID approvedBy)
rejectOrder(UUID orderId, UUID rejectedBy, String reason)
cancelOrder(UUID orderId, UUID cancelledBy, String reason)
createRecurringOrder(UUID parentOrderId, LocalDate scheduledDate)
getOrderByNumber(String orderNumber)
getOrdersByPatient(UUID patientId)
getOrdersByEncounter(UUID encounterId)
getPendingOrders()
getUrgentOrders()
getOverdueOrders(LocalDateTime cutoffTime)
calculateOrderTotal(UUID orderId)
```

**Status Workflow:**
```
PENDING -> APPROVED -> SPECIMEN_COLLECTED -> SPECIMEN_RECEIVED -> IN_PROGRESS -> COMPLETED
         ↓
      REJECTED
         ↓
     CANCELLED
```

#### 6. OrderStatusHistoryService
**File:** `src/main/java/com/yudha/hms/laboratory/service/OrderStatusHistoryService.java`

**Features:**
- Automatic status change tracking
- Audit trail for all transitions
- Notification management
- Status history retrieval
- Pending notification tracking
- Date range queries
- Status-based queries

**Key Methods:**
```java
recordStatusChange(LabOrder order, OrderStatus previousStatus, OrderStatus newStatus, String changedBy, String reason)
markNotificationSent(UUID historyId, List<String> recipients)
getOrderStatusHistory(UUID orderId)
getLatestStatus(UUID orderId)
getPendingNotifications()
getStatusChangesByDateRange(LocalDateTime startDate, LocalDateTime endDate)
```

#### 7. SpecimenService
**File:** `src/main/java/com/yudha/hms/laboratory/service/SpecimenService.java`

**Features:**
- Specimen lifecycle management
- Automatic barcode generation
- Collection tracking with collector and timestamp
- Lab reception workflow
- Quality control checks (hemolysis, lipemia, icterus detection)
- Specimen rejection with reason tracking
- Processing workflow
- Storage location and temperature tracking
- Disposal management
- Quality status monitoring
- Pending specimen queries

**Key Methods:**
```java
createSpecimen(Specimen specimen)
collectSpecimen(UUID orderItemId, UUID collectedBy, LocalDateTime collectedAt)
receiveSpecimen(String barcode, UUID receivedBy, String labLocation)
performQualityCheck(String barcode, QualityStatus qualityStatus, Boolean hemolysis, Boolean lipemia, Boolean icterus, String qualityNotes)
rejectSpecimen(String barcode, String rejectionReason)
processSpecimen(String barcode)
completeSpecimenProcessing(String barcode)
storeSpecimen(String barcode, String storageLocation, Double storageTemperature)
disposeSpecimen(String barcode, UUID disposedBy, String disposalMethod)
getSpecimenByBarcode(String barcode)
getSpecimensByOrder(UUID orderId)
getPendingSpecimens()
getRejectedSpecimens(LocalDateTime startDate, LocalDateTime endDate)
getSpecimensWithQualityIssues()
```

### Phase 3: Utility Services (COMPLETED)

#### 8. BarcodeGenerationService
**File:** `src/main/java/com/yudha/hms/laboratory/service/BarcodeGenerationService.java`

**Features:**
- Specimen barcode generation (format: SP + YYYYMMDD + 6-digit random)
- Order barcode generation (format: LO + YYYYMMDD + 5-digit sequence)
- UUID-based unique identifiers
- Custom prefix barcodes
- Barcode format validation
- Luhn algorithm check digit generation and verification
- Barcode integrity checking

**Key Methods:**
```java
generateSpecimenBarcode()
generateOrderBarcode(long sequenceNumber)
generateUniqueBarcode()
generateBarcodeWithPrefix(String prefix, int length)
validateBarcodeFormat(String barcode)
generateCheckDigit(String barcode)
generateBarcodeWithCheckDigit(String baseBarcode)
verifyBarcodeCheckDigit(String barcodeWithCheckDigit)
```

**Barcode Formats:**
- Specimen: `SP20250121012345` (16 characters)
- Order: `LO202501210001` (15 characters)
- Unique: `A1B2C3D4E5F6` (12 characters)

## Service Dependencies

```
LabTestCategoryService
    └── LabTestCategoryRepository

LabTestService
    ├── LabTestRepository
    ├── LabTestCategoryRepository
    └── LabTestParameterRepository

LabTestParameterService
    ├── LabTestParameterRepository
    └── LabTestRepository

LabPanelService
    ├── LabPanelRepository
    ├── LabPanelItemRepository
    └── LabTestRepository

LabOrderService
    ├── LabOrderRepository
    ├── LabOrderItemRepository
    ├── LabTestRepository
    ├── LabPanelRepository
    └── OrderStatusHistoryService

OrderStatusHistoryService
    └── OrderStatusHistoryRepository

SpecimenService
    ├── SpecimenRepository
    ├── LabOrderRepository
    ├── LabOrderItemRepository
    └── BarcodeGenerationService

BarcodeGenerationService
    └── (no dependencies - utility service)
```

## Services Still To Implement

### Phase 4: Result Services (PENDING)

1. **LabResultService**
   - Result entry (manual, LIS interface, imported)
   - Delta check algorithm implementation
   - Automatic abnormal flagging
   - Previous result comparison
   - Result validation workflow initiation
   - Result amendment
   - Patient result history

2. **ResultValidationService**
   - Multi-step validation workflow
   - Technician validation
   - Senior technician review
   - Pathologist approval
   - Clinical reviewer workflow
   - Validation rejection with repeat test request
   - Digital signature management
   - Issue tracking and corrective actions

3. **CriticalValueAlertService**
   - Automatic panic value detection
   - Critical value alert creation
   - Notification generation
   - Acknowledgment tracking
   - Alert escalation
   - Communication log
   - Alert reporting

### Phase 5: Reporting Services (PENDING)

1. **LabReportService**
   - Report generation (single test, cumulative, trend)
   - PDF generation with letterhead
   - Digital signature
   - Report finalization
   - Distribution tracking (print, email, access)
   - Report templates
   - Clinical interpretation
   - Report revision

2. **TatMonitoringService**
   - TAT calculation at each stage
   - TAT compliance tracking
   - Bottleneck identification
   - TAT reporting and analytics
   - Performance metrics

3. **TestUtilizationService**
   - Test utilization tracking
   - Usage statistics
   - Trend analysis
   - Department-wise utilization
   - Cost analysis
   - Utilization reporting

### Phase 6: Advanced Features (PENDING)

1. **DeltaCheckService**
   - Delta check algorithm implementation
   - Percentage change calculation
   - Absolute change calculation
   - Historical result comparison
   - Delta check violation detection
   - Technologist notification

2. **NotificationService**
   - Critical value notifications
   - Status change notifications
   - Email notification
   - SMS notification (future)
   - In-app notification
   - Notification templates
   - Delivery tracking

3. **PdfReportGenerationService**
   - PDF rendering with iText or Apache PDFBox
   - Template-based report generation
   - Letterhead inclusion
   - Digital signature embedding
   - Result tables and charts
   - Previous result comparison tables
   - QR code generation for verification

## Implementation Statistics

### Completed
- **Services:** 8 services
- **Lines of Code:** ~2,500 lines
- **Methods:** 150+ public methods
- **Features:** 50+ business features

### Test Master Services
- 4 services
- Full CRUD operations
- Search and pagination
- Soft delete
- Validation
- Price management
- Category hierarchy
- Parameter configuration

### Order Management Services
- 3 services
- Order workflow
- Status tracking
- Specimen lifecycle
- Barcode generation
- Quality control
- Recurring orders
- TAT calculation

### Utility Services
- 1 service
- Barcode generation
- Check digit validation
- Multiple barcode formats

## Next Steps

1. **Complete Result Services**
   - Implement LabResultService with delta check
   - Implement ResultValidationService with workflow
   - Implement CriticalValueAlertService with notifications

2. **Complete Reporting Services**
   - Implement LabReportService with PDF generation
   - Implement TatMonitoringService
   - Implement TestUtilizationService

3. **Create DTOs**
   - Request DTOs for all create/update operations
   - Response DTOs for all read operations
   - Search/Filter DTOs
   - Validation annotations

4. **Create REST Controllers**
   - Test Master Controllers
   - Order Management Controllers
   - Result Controllers
   - Report Controllers
   - Exception handling
   - API documentation

5. **Testing**
   - Unit tests for all services
   - Integration tests
   - End-to-end testing

## Key Design Patterns Used

1. **Service Layer Pattern** - Business logic separated from controllers
2. **Repository Pattern** - Data access abstraction
3. **Transaction Management** - @Transactional annotations
4. **Soft Delete Pattern** - Data preservation with audit trail
5. **Builder Pattern** - Entity construction
6. **Validation Pattern** - Business rule validation
7. **Status Machine Pattern** - Order status workflow
8. **Factory Pattern** - Entity creation with defaults

## Error Handling

All services implement comprehensive error handling:
- `IllegalArgumentException` for invalid inputs
- `IllegalStateException` for invalid state transitions
- Entity not found exceptions
- Validation exceptions
- Logging at INFO and DEBUG levels

## Transaction Management

- `@Transactional` for write operations
- `@Transactional(readOnly = true)` for read operations
- Rollback on exceptions
- Optimistic locking with version fields

## Logging

All services use SLF4J logging:
- INFO level for business operations
- DEBUG level for barcode generation
- Error tracking for failures
- Operation tracing with IDs

---

**Status:** Service layer implementation in progress - Test Master and Order Management complete
**Next:** Result Services, Reporting Services, DTOs, and REST Controllers
**Generated by:** Claude Code
**Project:** HMS Backend - Phase 10: Laboratory Module
**Version:** 1.0.0
