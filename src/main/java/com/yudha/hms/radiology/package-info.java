/**
 * Radiology Module
 *
 * This module handles all radiology and medical imaging operations including:
 * - Radiology exam ordering
 * - Imaging modality scheduling
 * - PACS (Picture Archiving and Communication System) integration
 * - DICOM integration for medical imaging
 * - Radiology reports and interpretations
 * - Image viewing and storage
 * - Radiation dose tracking
 * - Contrast media management
 * - Quality assurance
 * - Radiology billing integration
 *
 * Indonesian Specific Features:
 * - Radiology result submission to SATUSEHAT
 * - Indonesian radiology procedure catalog
 * - External imaging center integration
 * - Radiation safety compliance (BAPETEN regulations)
 * - KARS accreditation requirements for radiology
 *
 * Imaging Modalities:
 * - X_RAY: Conventional radiography
 * - CT_SCAN: Computed Tomography
 * - MRI: Magnetic Resonance Imaging
 * - ULTRASOUND: Sonography/USG
 * - MAMMOGRAPHY: Breast imaging
 * - FLUOROSCOPY: Real-time X-ray imaging
 * - NUCLEAR_MEDICINE: PET, SPECT scans
 * - INTERVENTIONAL: Angiography, etc.
 *
 * DICOM Standards:
 * - DICOM Store (C-STORE): Image storage
 * - DICOM Query/Retrieve (C-FIND, C-MOVE): Image retrieval
 * - DICOM Worklist (MWL): Modality worklist
 * - DICOM SR: Structured reporting
 * - HL7 integration for order/result messaging
 *
 * Package Structure:
 * - controller: REST API endpoints for radiology operations
 * - service: Business logic for radiology workflows
 * - repository: Data access layer for radiology entities
 * - entity: JPA entities (RadiologyOrder, RadiologyReport, Imaging, etc.)
 * - dto: Data Transfer Objects for API requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.radiology;