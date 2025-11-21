# Radiology Module - Schema Design Documentation

## Overview
Phase 11: Radiology Module - Complete database schema and Java implementation for radiology examination management including modalities, examinations, orders, results, DICOM images, contrast administration, and equipment maintenance.

**Version:** 1.0.0
**Date:** 2025-01-21
**Author:** HMS Development Team

---

## Table of Contents
1. [Database Schema](#database-schema)
2. [Enumeration Constants](#enumeration-constants)
3. [Entity Relationships](#entity-relationships)
4. [Repository Methods](#repository-methods)
5. [Key Features](#key-features)
6. [Design Patterns](#design-patterns)

---

## Database Schema

### Schema: `radiology_schema`

All radiology module tables are organized under the `radiology_schema` namespace.

### Tables Summary

| Table Name | Description | Records |
|------------|-------------|---------|
| radiology_modality | Imaging modality master (X-Ray, CT, MRI, etc.) | Master data |
| radiology_examination | Examination catalog with CPT codes | Master data |
| radiology_room | Imaging rooms and equipment locations | Master data |
| reporting_template | Report templates per examination | Master data |
| radiology_order | Radiology examination orders | Transactional |
| radiology_order_item | Order line items with laterality | Transactional |
| radiology_result | Examination results and reports | Transactional |
| radiology_image | DICOM images and studies | Transactional |
| contrast_administration | Contrast media tracking | Transactional |
| equipment_maintenance | Equipment maintenance log | Transactional |

---

## Detailed Table Specifications

### 1. radiology_modality
**Purpose:** Master data for imaging modalities

**Columns:**
- `id` (UUID) - Primary key
- `code` (VARCHAR 50) - Unique modality code (XRAY, CT_SCAN, MRI, etc.)
- `name` (VARCHAR 200) - Modality name
- `description` (TEXT) - Detailed description
- `requires_radiation` (BOOLEAN) - Whether modality uses ionizing radiation
- `average_duration_minutes` (INTEGER) - Average examination duration
- `is_active` (BOOLEAN) - Active status
- `display_order` (INTEGER) - UI display order
- `icon` (VARCHAR 100) - Icon name for UI
- `color` (VARCHAR 50) - Color code for UI
- Audit fields: created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version

**Indexes:**
- Unique: code
- Standard: name, is_active

**Initial Data:** 8 modalities (XRAY, CT_SCAN, MRI, ULTRASOUND, MAMMOGRAPHY, FLUOROSCOPY, DEXA, ANGIOGRAPHY)

---

### 2. radiology_examination
**Purpose:** Examination catalog (similar to laboratory tests)

**Columns:**
- `id` (UUID) - Primary key
- `exam_code` (VARCHAR 50) - Unique examination code
- `exam_name` (VARCHAR 200) - Examination name
- `short_name` (VARCHAR 100) - Short name for display
- `modality_id` (UUID) - FK to radiology_modality
- `cpt_code` (VARCHAR 20) - CPT code for procedure
- `icd_procedure_code` (VARCHAR 20) - ICD procedure code
- `preparation_instructions` (TEXT) - Patient preparation instructions
- `fasting_required` (BOOLEAN) - Whether fasting is required
- `fasting_duration_hours` (INTEGER) - Fasting duration if required
- `requires_contrast` (BOOLEAN) - Whether contrast media is required
- `contrast_type` (VARCHAR 50) - Type of contrast (IODINE_BASED, GADOLINIUM_BASED, etc.)
- `contrast_volume_ml` (DECIMAL 10,2) - Contrast volume
- `exam_duration_minutes` (INTEGER) - Examination duration
- `reporting_time_minutes` (INTEGER) - Average reporting time
- `base_cost` (DECIMAL 15,2) - Base examination cost
- `contrast_cost` (DECIMAL 15,2) - Additional contrast cost
- `bpjs_tariff` (DECIMAL 15,2) - BPJS reimbursement tariff
- `body_part` (VARCHAR 200) - Body part examined
- `laterality_applicable` (BOOLEAN) - Whether laterality applies
- `positioning_notes` (TEXT) - Patient positioning instructions
- `clinical_indication` (TEXT) - Clinical indications for examination
- `interpretation_guide` (TEXT) - Interpretation guidelines
- `is_active` (BOOLEAN) - Active status
- `requires_approval` (BOOLEAN) - Whether approval is required
- Audit fields: created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version

**Indexes:**
- Unique: exam_code
- Standard: exam_name, modality_id, cpt_code, body_part, is_active

**Constraints:**
- fasting_duration_hours >= 0
- contrast_volume_ml > 0
- exam_duration_minutes > 0
- reporting_time_minutes > 0
- base_cost >= 0
- contrast_cost >= 0

---

### 3. radiology_room
**Purpose:** Imaging rooms and equipment locations

**Columns:**
- `id` (UUID) - Primary key
- `room_code` (VARCHAR 50) - Unique room code
- `room_name` (VARCHAR 200) - Room name
- `location` (VARCHAR 200) - Location description
- `floor` (VARCHAR 50) - Floor level
- `modality_id` (UUID) - FK to radiology_modality
- `equipment_name` (VARCHAR 200) - Equipment name
- `equipment_model` (VARCHAR 200) - Equipment model
- `manufacturer` (VARCHAR 200) - Equipment manufacturer
- `installation_date` (DATE) - Installation date
- `last_calibration_date` (DATE) - Last calibration date
- `next_calibration_date` (DATE) - Next calibration due date
- `is_operational` (BOOLEAN) - Whether equipment is operational
- `is_available` (BOOLEAN) - Whether available for booking
- `max_bookings_per_day` (INTEGER) - Maximum bookings per day
- `notes` (TEXT) - Additional notes
- Audit fields: created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version

**Indexes:**
- Unique: room_code
- Standard: room_name, modality_id, is_operational, is_available

**Constraints:**
- max_bookings_per_day > 0

---

### 4. reporting_template
**Purpose:** Report templates per examination

**Columns:**
- `id` (UUID) - Primary key
- `examination_id` (UUID) - FK to radiology_examination
- `template_name` (VARCHAR 200) - Template name
- `template_code` (VARCHAR 50) - Template code
- `sections` (JSONB) - Template sections (findings, impression, recommendations)
- `is_default` (BOOLEAN) - Whether this is the default template
- `is_active` (BOOLEAN) - Active status
- Audit fields: created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version

**JSONB Structure:**
```json
{
  "findings": "Template text for findings section",
  "impression": "Template text for impression section",
  "recommendations": "Template text for recommendations section"
}
```

**Indexes:**
- Standard: examination_id, template_code, is_default, is_active
- Unique constraint: (examination_id, template_code)

---

### 5. radiology_order
**Purpose:** Radiology examination orders

**Columns:**
- `id` (UUID) - Primary key
- `order_number` (VARCHAR 50) - Unique order number
- `patient_id` (UUID) - FK to patient_schema.patient
- `encounter_id` (UUID) - FK to clinical_schema.encounter
- `ordering_doctor_id` (UUID) - Ordering physician ID
- `ordering_department` (VARCHAR 100) - Ordering department
- `order_date` (TIMESTAMP) - Order date and time
- `priority` (VARCHAR 50) - Order priority (ROUTINE, URGENT, EMERGENCY)
- `clinical_indication` (TEXT) - Clinical indication
- `diagnosis_text` (VARCHAR 500) - Diagnosis description
- `status` (VARCHAR 50) - Order status (PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
- `scheduled_date` (DATE) - Scheduled examination date
- `scheduled_time` (TIME) - Scheduled examination time
- `room_id` (UUID) - FK to radiology_room
- `technician_id` (UUID) - Assigned technician ID
- `notes` (TEXT) - Order notes
- `special_instructions` (TEXT) - Special instructions
- Audit fields: created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version

**Indexes:**
- Unique: order_number
- Standard: patient_id, encounter_id, ordering_doctor_id, status, priority, order_date, scheduled_date, room_id

---

### 6. radiology_order_item
**Purpose:** Individual examinations in orders

**Columns:**
- `id` (UUID) - Primary key
- `order_id` (UUID) - FK to radiology_order
- `examination_id` (UUID) - FK to radiology_examination
- `exam_code` (VARCHAR 50) - Denormalized exam code
- `exam_name` (VARCHAR 200) - Denormalized exam name
- `laterality` (VARCHAR 50) - Laterality (LEFT, RIGHT, BILATERAL, NOT_APPLICABLE)
- `quantity` (INTEGER) - Quantity (default 1)
- `unit_price` (DECIMAL 15,2) - Unit price
- `discount_amount` (DECIMAL 15,2) - Discount amount
- `final_price` (DECIMAL 15,2) - Final price
- `status` (VARCHAR 50) - Item status
- `result_id` (UUID) - FK to radiology_result (nullable)
- `notes` (TEXT) - Item notes
- Audit fields: created_at, created_by, updated_at, updated_by, version

**Indexes:**
- Standard: order_id, examination_id, status, result_id

**Constraints:**
- quantity > 0
- unit_price >= 0
- discount_amount >= 0
- final_price >= 0

---

### 7. radiology_result
**Purpose:** Examination results and reports

**Columns:**
- `id` (UUID) - Primary key
- `result_number` (VARCHAR 50) - Unique result number
- `order_item_id` (UUID) - FK to radiology_order_item
- `examination_id` (UUID) - FK to radiology_examination
- `patient_id` (UUID) - FK to patient_schema.patient
- `performed_date` (TIMESTAMP) - Examination performed date
- `performed_by_technician_id` (UUID) - Technician who performed exam
- `findings` (TEXT) - Examination findings
- `impression` (TEXT) - Radiologist's impression
- `recommendations` (TEXT) - Recommendations
- `radiologist_id` (UUID) - Reporting radiologist ID
- `reported_date` (TIMESTAMP) - Report date
- `is_finalized` (BOOLEAN) - Whether result is finalized
- `finalized_date` (TIMESTAMP) - Finalization date
- `is_amended` (BOOLEAN) - Whether result was amended
- `amendment_reason` (TEXT) - Reason for amendment
- `image_count` (INTEGER) - Number of images
- `dicom_study_id` (VARCHAR 100) - DICOM study UID
- Audit fields: created_at, created_by, updated_at, updated_by, deleted_at, deleted_by, version

**Indexes:**
- Unique: result_number
- Standard: order_item_id, examination_id, patient_id, performed_date, performed_by_technician_id, radiologist_id, is_finalized, dicom_study_id

**Constraints:**
- image_count >= 0

---

### 8. radiology_image
**Purpose:** DICOM images and studies

**Columns:**
- `id` (UUID) - Primary key
- `result_id` (UUID) - FK to radiology_result
- `image_number` (INTEGER) - Image sequence number
- `dicom_study_uid` (VARCHAR 100) - DICOM study UID
- `dicom_series_uid` (VARCHAR 100) - DICOM series UID
- `dicom_instance_uid` (VARCHAR 100) - DICOM instance UID
- `modality` (VARCHAR 50) - Imaging modality
- `body_part_examined` (VARCHAR 200) - Body part examined
- `image_type` (VARCHAR 100) - Image type
- `file_path` (VARCHAR 500) - File storage path
- `file_size_bytes` (BIGINT) - File size
- `acquisition_date` (TIMESTAMP) - Image acquisition date
- `view_position` (VARCHAR 100) - View/projection position
- `is_key_image` (BOOLEAN) - Whether this is a key image
- `notes` (TEXT) - Image notes
- Audit fields: created_at, created_by, updated_at, updated_by, version

**Indexes:**
- Standard: result_id, dicom_study_uid, dicom_series_uid, dicom_instance_uid
- Filtered: is_key_image (where is_key_image = true)

**Constraints:**
- file_size_bytes > 0

---

### 9. contrast_administration
**Purpose:** Contrast media tracking and reaction monitoring

**Columns:**
- `id` (UUID) - Primary key
- `order_item_id` (UUID) - FK to radiology_order_item
- `patient_id` (UUID) - FK to patient_schema.patient
- `contrast_name` (VARCHAR 200) - Contrast media name
- `contrast_type` (VARCHAR 50) - Contrast type (IODINE_BASED, GADOLINIUM_BASED, BARIUM_BASED)
- `volume_ml` (DECIMAL 10,2) - Volume administered
- `batch_number` (VARCHAR 100) - Batch number
- `administered_by` (UUID) - Administrator user ID
- `administered_at` (TIMESTAMP) - Administration time
- `reaction_observed` (BOOLEAN) - Whether reaction was observed
- `reaction_severity` (VARCHAR 50) - Severity (NONE, MILD, MODERATE, SEVERE)
- `reaction_description` (TEXT) - Reaction description
- `treatment_given` (TEXT) - Treatment provided
- Audit fields: created_at, created_by, updated_at, updated_by, version

**Indexes:**
- Standard: order_item_id, patient_id, administered_by, administered_at, batch_number
- Filtered: reaction_observed (where reaction_observed = true)

**Constraints:**
- volume_ml > 0

---

### 10. equipment_maintenance
**Purpose:** Equipment maintenance scheduling and tracking

**Columns:**
- `id` (UUID) - Primary key
- `room_id` (UUID) - FK to radiology_room
- `maintenance_type` (VARCHAR 50) - Type (PREVENTIVE, CORRECTIVE, CALIBRATION)
- `scheduled_date` (DATE) - Scheduled date
- `performed_date` (DATE) - Actual performed date
- `performed_by` (VARCHAR 200) - Technician/engineer name
- `vendor_name` (VARCHAR 200) - Vendor name
- `findings` (TEXT) - Maintenance findings
- `actions_taken` (TEXT) - Actions performed
- `next_maintenance_date` (DATE) - Next scheduled maintenance
- `cost` (DECIMAL 15,2) - Maintenance cost
- Audit fields: created_at, created_by, updated_at, updated_by, version

**Indexes:**
- Standard: room_id, maintenance_type, scheduled_date, performed_date, next_maintenance_date

**Constraints:**
- cost >= 0

---

## Enumeration Constants

### 1. ModalityType
Imaging modality types with Indonesian language support.

**Values:**
- `XRAY` - X-Ray / Radiografi konvensional
- `CT_SCAN` - CT Scan / Tomografi Terkomputasi
- `MRI` - MRI / Pencitraan Resonansi Magnetik
- `ULTRASOUND` - Ultrasound / Ultrasonografi
- `MAMMOGRAPHY` - Mammography / Pencitraan payudara
- `FLUOROSCOPY` - Fluoroscopy / Pencitraan sinar-X real-time
- `DEXA` - DEXA Scan / Pengukuran kepadatan tulang
- `ANGIOGRAPHY` - Angiography / Pencitraan pembuluh darah
- `NUCLEAR_MEDICINE` - Nuclear Medicine / Kedokteran Nuklir
- `PET_SCAN` - PET Scan / Tomografi Emisi Positron

**Methods:**
- `usesRadiation()` - Check if modality uses ionizing radiation

---

### 2. OrderPriority
Order priority levels.

**Values:**
- `ROUTINE` - Routine / Rutin (24 hours expected)
- `URGENT` - Urgent / Mendesak (4 hours expected)
- `EMERGENCY` - Emergency / Darurat (1 hour expected)

**Methods:**
- `getExpectedCompletionHours()` - Get expected completion time

---

### 3. OrderStatus
Order workflow status.

**Values:**
- `PENDING` - Pending / Menunggu
- `SCHEDULED` - Scheduled / Terjadwal
- `IN_PROGRESS` - In Progress / Dalam Proses
- `COMPLETED` - Completed / Selesai
- `CANCELLED` - Cancelled / Dibatalkan

**Methods:**
- `isTerminal()` - Check if status is terminal
- `canBeCancelled()` - Check if order can be cancelled
- `canBeScheduled()` - Check if order can be scheduled

---

### 4. Laterality
Body side specification.

**Values:**
- `LEFT` - Left / Kiri
- `RIGHT` - Right / Kanan
- `BILATERAL` - Bilateral / Bilateral (both sides)
- `NOT_APPLICABLE` - Not Applicable / Tidak Berlaku

**Methods:**
- `isSingleSide()` - Check if laterality specifies a single side

---

### 5. ContrastType
Contrast media types.

**Values:**
- `NONE` - None / Tidak Ada
- `IODINE_BASED` - Iodine-Based / Berbasis Iodin (for CT, angiography)
- `GADOLINIUM_BASED` - Gadolinium-Based / Berbasis Gadolinium (for MRI)
- `BARIUM_BASED` - Barium-Based / Berbasis Barium (for GI studies)
- `MICROBUBBLE` - Microbubble / Gelembung Mikro (for ultrasound)

**Methods:**
- `isContrastRequired()` - Check if contrast is required
- `requiresRenalFunctionCheck()` - Check if renal function test needed

---

### 6. ReactionSeverity
Contrast reaction severity levels.

**Values:**
- `NONE` - None / Tidak Ada
- `MILD` - Mild / Ringan (self-limiting)
- `MODERATE` - Moderate / Sedang (requires intervention)
- `SEVERE` - Severe / Berat (life-threatening)

**Methods:**
- `requiresEmergencyIntervention()` - Check if emergency intervention needed
- `requiresMedicalAttention()` - Check if medical attention needed

---

### 7. MaintenanceType
Equipment maintenance types.

**Values:**
- `PREVENTIVE` - Preventive / Pencegahan (scheduled)
- `CORRECTIVE` - Corrective / Perbaikan (fixing failures)
- `CALIBRATION` - Calibration / Kalibrasi

**Methods:**
- `isScheduled()` - Check if maintenance is scheduled
- `isUnplanned()` - Check if maintenance is unplanned

---

## Entity Relationships

### Entity Relationship Diagram (Textual)

```
radiology_modality (1) ←→ (N) radiology_examination
radiology_modality (1) ←→ (N) radiology_room

radiology_examination (1) ←→ (N) reporting_template
radiology_examination (1) ←→ (N) radiology_order_item

radiology_room (1) ←→ (N) radiology_order
radiology_room (1) ←→ (N) equipment_maintenance

patient (1) ←→ (N) radiology_order
patient (1) ←→ (N) radiology_result
patient (1) ←→ (N) contrast_administration

encounter (1) ←→ (N) radiology_order

radiology_order (1) ←→ (N) radiology_order_item

radiology_order_item (1) ←→ (1) radiology_result
radiology_order_item (1) ←→ (1) contrast_administration

radiology_result (1) ←→ (N) radiology_image
```

---

## Repository Methods

### Custom Query Methods Summary

Each repository extends `JpaRepository<Entity, UUID>` and includes:

#### Common Methods (All Repositories)
- `findByIdAndDeletedAtIsNull(UUID id)` - Find by ID excluding soft deleted
- Various search and filter methods with pagination support
- Count methods for statistics

#### RadiologyModalityRepository
- `findByCodeAndDeletedAtIsNull(String code)`
- `findByIsActiveTrueAndDeletedAtIsNull()`
- `findByRequiresRadiationTrueAndIsActiveTrueAndDeletedAtIsNull()`
- `searchModalities(String search, Pageable pageable)`

#### RadiologyExaminationRepository
- `findByExamCodeAndDeletedAtIsNull(String examCode)`
- `findByCptCodeAndDeletedAtIsNull(String cptCode)`
- `findByModalityIdAndIsActiveTrueAndDeletedAtIsNull(UUID modalityId)`
- `findByRequiresContrastTrueAndIsActiveTrueAndDeletedAtIsNull()`
- `searchExaminations(String search, Pageable pageable)`

#### RadiologyRoomRepository
- `findByRoomCodeAndDeletedAtIsNull(String roomCode)`
- `findByModalityIdAndIsOperationalTrueAndIsAvailableTrueAndDeletedAtIsNull(UUID modalityId)`
- `findRoomsNeedingCalibration(LocalDate date)`
- `searchRooms(String search, Pageable pageable)`

#### RadiologyOrderRepository
- `findByOrderNumberAndDeletedAtIsNull(String orderNumber)`
- `findByPatientIdAndDeletedAtIsNull(UUID patientId, Pageable pageable)`
- `findByStatusAndDeletedAtIsNull(OrderStatus status, Pageable pageable)`
- `findUrgentOrders()` - Returns urgent/emergency orders
- `findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)`
- `searchOrders(String search, Pageable pageable)`

#### RadiologyResultRepository
- `findByResultNumberAndDeletedAtIsNull(String resultNumber)`
- `findByOrderItemIdAndDeletedAtIsNull(UUID orderItemId)`
- `findPendingResults()` - Non-finalized results
- `findResultsAwaitingRadiologist()` - Results needing radiologist review
- `findByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)`
- `searchResults(String search, Pageable pageable)`

#### ContrastAdministrationRepository
- `findByPatientIdOrderByAdministeredAtDesc(UUID patientId)`
- `findAdministrationsWithReactions()`
- `findSevereReactions()`
- `findPatientContrastHistory(UUID patientId)`

#### EquipmentMaintenanceRepository
- `findByRoomIdOrderByScheduledDateDesc(UUID roomId)`
- `findPendingMaintenance(LocalDate date)`
- `findOverdueMaintenance(LocalDate date)`
- `findUpcomingMaintenance(LocalDate startDate, LocalDate endDate)`
- `findMaintenanceHistory(UUID roomId)`
- `findCalibrationHistory(UUID roomId)`

---

## Key Features

### 1. Modality Management
- Support for 10 different imaging modalities
- Radiation exposure tracking
- Average duration tracking
- UI customization (icon, color, display order)

### 2. Examination Catalog
- Comprehensive examination catalog with CPT and ICD codes
- Patient preparation instructions
- Contrast media requirements and volumes
- Cost management (base cost, contrast cost, BPJS tariff)
- Body part and laterality tracking
- Clinical indication and interpretation guides

### 3. Room and Equipment Management
- Equipment tracking with model and manufacturer
- Calibration scheduling and alerts
- Operational status tracking
- Booking capacity management

### 4. Order Management
- Priority-based ordering (ROUTINE, URGENT, EMERGENCY)
- Scheduling with room assignment
- Laterality support for applicable examinations
- Status workflow (PENDING → SCHEDULED → IN_PROGRESS → COMPLETED)
- Integration with patient and encounter

### 5. Result and Reporting
- Structured reporting (findings, impression, recommendations)
- DICOM integration with study/series/instance UIDs
- Image management with key image marking
- Radiologist workflow with finalization
- Amendment tracking

### 6. Contrast Administration
- Contrast media tracking with batch numbers
- Reaction monitoring with severity levels
- Patient contrast history
- Renal function check requirements

### 7. Equipment Maintenance
- Preventive maintenance scheduling
- Corrective maintenance tracking
- Calibration management
- Vendor tracking and cost monitoring

---

## Design Patterns

### 1. Soft Delete Pattern
All master data tables use soft delete:
- `deleted_at` (TIMESTAMP) - Null for active records
- `deleted_by` (VARCHAR 100) - User who deleted the record
- Queries include `WHERE deleted_at IS NULL` filter

### 2. Audit Trail
All tables include audit fields:
- `created_at` (TIMESTAMP) - Record creation timestamp
- `created_by` (VARCHAR 100) - Creator user identifier
- `updated_at` (TIMESTAMP) - Last update timestamp
- `updated_by` (VARCHAR 100) - Last updater user identifier
- Automatic update via database trigger: `update_updated_at_column()`

### 3. Optimistic Locking
All entities include:
- `version` (BIGINT) - Version number for optimistic locking
- JPA automatically handles concurrent update detection

### 4. UUID Primary Keys
- All tables use UUID as primary key
- Generated using PostgreSQL `gen_random_uuid()`
- Benefits: distributed system compatibility, no sequential guessing

### 5. Denormalization for Performance
Order items denormalize frequently accessed data:
- `exam_code` - From examination
- `exam_name` - From examination
- Reduces JOIN operations during order processing

### 6. JSONB for Flexibility
Reporting templates use JSONB:
- Flexible structure for template sections
- Indexable and queryable
- Future-proof for template customization

### 7. Proper Indexing
Strategic indexes for:
- Unique constraints (codes, numbers)
- Foreign keys
- Status and date filters
- Search columns (name, code)
- Filtered indexes for specific queries (is_key_image, reaction_observed)

---

## Integration Points

### 1. Patient Module
- FK: `patient_id` in radiology_order, radiology_result, contrast_administration
- Access to: Patient demographics, medical record number

### 2. Clinical Module
- FK: `encounter_id` in radiology_order
- Access to: Encounter context, ordering physician

### 3. DICOM/PACS Systems
- DICOM UIDs stored in radiology_image
- Study, Series, Instance identifiers
- File path references to PACS storage

### 4. Billing Module (Future)
- Cost information in radiology_examination
- BPJS tariff support
- Order item pricing

---

## Future Enhancements

### Planned Features
1. **DICOM Worklist Integration**
   - Automatic worklist push to modalities
   - Status updates from PACS

2. **Template Engine**
   - Advanced template rendering
   - Variable substitution
   - Macros and shortcuts

3. **Quality Assurance**
   - Image quality metrics
   - Technician performance tracking
   - Equipment downtime analysis

4. **SATUSEHAT Integration**
   - Result submission to Indonesian national health system
   - IHS code mapping

5. **Advanced Analytics**
   - Turnaround time (TAT) monitoring
   - Utilization statistics
   - Revenue analysis

---

## Files Created

### Database Migration
- `/src/main/resources/db/migration/V35__create_radiology_module_tables.sql`

### Enum Constants (7 files)
- `/src/main/java/com/yudha/hms/radiology/constant/ModalityType.java`
- `/src/main/java/com/yudha/hms/radiology/constant/OrderPriority.java`
- `/src/main/java/com/yudha/hms/radiology/constant/OrderStatus.java`
- `/src/main/java/com/yudha/hms/radiology/constant/Laterality.java`
- `/src/main/java/com/yudha/hms/radiology/constant/ContrastType.java`
- `/src/main/java/com/yudha/hms/radiology/constant/ReactionSeverity.java`
- `/src/main/java/com/yudha/hms/radiology/constant/MaintenanceType.java`

### Entity Classes (10 files)
- `/src/main/java/com/yudha/hms/radiology/entity/RadiologyModality.java`
- `/src/main/java/com/yudha/hms/radiology/entity/RadiologyExamination.java`
- `/src/main/java/com/yudha/hms/radiology/entity/RadiologyRoom.java`
- `/src/main/java/com/yudha/hms/radiology/entity/ReportingTemplate.java`
- `/src/main/java/com/yudha/hms/radiology/entity/RadiologyOrder.java`
- `/src/main/java/com/yudha/hms/radiology/entity/RadiologyOrderItem.java`
- `/src/main/java/com/yudha/hms/radiology/entity/RadiologyResult.java`
- `/src/main/java/com/yudha/hms/radiology/entity/RadiologyImage.java`
- `/src/main/java/com/yudha/hms/radiology/entity/ContrastAdministration.java`
- `/src/main/java/com/yudha/hms/radiology/entity/EquipmentMaintenance.java`

### Repository Interfaces (10 files)
- `/src/main/java/com/yudha/hms/radiology/repository/RadiologyModalityRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/RadiologyExaminationRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/RadiologyRoomRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/ReportingTemplateRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/RadiologyOrderRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/RadiologyOrderItemRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/RadiologyResultRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/RadiologyImageRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/ContrastAdministrationRepository.java`
- `/src/main/java/com/yudha/hms/radiology/repository/EquipmentMaintenanceRepository.java`

### Documentation
- `/src/main/java/com/yudha/hms/radiology/package-info.java`
- `/RADIOLOGY_MODULE_SCHEMA_DESIGN.md` (this file)

---

## Summary

**Total Files Created:** 29
- 1 Database migration script
- 7 Enum constant classes
- 10 Entity classes
- 10 Repository interfaces
- 1 Package documentation
- 1 Schema design documentation

**Total Tables Created:** 10
- 4 Master data tables
- 6 Transactional tables

**Initial Data:** 8 radiology modalities

All files follow the same pattern as the Laboratory Module with:
- PostgreSQL database schema with UUID primary keys
- Soft delete pattern for master data
- Comprehensive audit trail
- Proper indexing for performance
- Indonesian language support in enumerations
- Integration with patient and clinical modules
- DICOM/PACS integration support
- Contrast administration tracking
- Equipment maintenance management

---

**End of Documentation**
