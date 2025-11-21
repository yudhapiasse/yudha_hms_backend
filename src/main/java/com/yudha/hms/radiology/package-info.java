/**
 * Radiology Module - Phase 11.
 *
 * <p>This package contains the complete implementation of the Radiology Module for the Hospital Management System.
 * The module handles all aspects of radiology examination management, from order creation to result reporting.</p>
 *
 * <h2>Module Structure</h2>
 * <ul>
 *   <li><b>constant</b> - Enumerations for radiology-specific constants (modality types, order status, priorities, etc.)</li>
 *   <li><b>entity</b> - JPA entities representing radiology data models</li>
 *   <li><b>repository</b> - Spring Data JPA repositories for database operations</li>
 * </ul>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li><b>11.1 Radiology Examination Master</b>
 *     <ul>
 *       <li>Modality management (X-Ray, CT, MRI, USG, Mammography, etc.)</li>
 *       <li>Examination catalog with CPT codes and ICD procedure codes</li>
 *       <li>Preparation instructions and contrast requirements</li>
 *       <li>Room and equipment tracking with maintenance scheduling</li>
 *       <li>Reporting templates with JSONB sections</li>
 *     </ul>
 *   </li>
 *   <li><b>11.2 Order Management</b>
 *     <ul>
 *       <li>Radiology order creation with priority levels (ROUTINE, URGENT, EMERGENCY)</li>
 *       <li>Scheduling and room assignment</li>
 *       <li>Order item tracking with laterality support (LEFT, RIGHT, BILATERAL)</li>
 *       <li>Status workflow management</li>
 *     </ul>
 *   </li>
 *   <li><b>11.3 Result Entry and Reporting</b>
 *     <ul>
 *       <li>Examination result documentation (findings, impression, recommendations)</li>
 *       <li>DICOM image management with study/series/instance UIDs</li>
 *       <li>Radiologist reporting workflow with finalization</li>
 *       <li>Result amendment tracking</li>
 *     </ul>
 *   </li>
 *   <li><b>11.4 Contrast Administration and Equipment Maintenance</b>
 *     <ul>
 *       <li>Contrast media tracking and reaction monitoring</li>
 *       <li>Equipment maintenance scheduling (preventive, corrective, calibration)</li>
 *       <li>Calibration tracking and alerts</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>Imaging Modalities</h2>
 * <ul>
 *   <li>XRAY - Conventional radiography</li>
 *   <li>CT_SCAN - Computed Tomography</li>
 *   <li>MRI - Magnetic Resonance Imaging</li>
 *   <li>ULTRASOUND - Ultrasonography/USG</li>
 *   <li>MAMMOGRAPHY - Breast imaging</li>
 *   <li>FLUOROSCOPY - Real-time X-ray imaging</li>
 *   <li>DEXA - Bone density measurement</li>
 *   <li>ANGIOGRAPHY - Vascular imaging</li>
 *   <li>NUCLEAR_MEDICINE - Nuclear medicine imaging</li>
 *   <li>PET_SCAN - Positron Emission Tomography</li>
 * </ul>
 *
 * <h2>Database Schema</h2>
 * <p>The module uses the <code>radiology_schema</code> in PostgreSQL with the following tables:</p>
 * <ul>
 *   <li><b>radiology_modality</b> - Imaging modality master data</li>
 *   <li><b>radiology_examination</b> - Examination catalog</li>
 *   <li><b>radiology_room</b> - Imaging rooms and equipment</li>
 *   <li><b>reporting_template</b> - Report templates with JSONB sections</li>
 *   <li><b>radiology_order</b> - Examination orders</li>
 *   <li><b>radiology_order_item</b> - Order line items</li>
 *   <li><b>radiology_result</b> - Examination results and reports</li>
 *   <li><b>radiology_image</b> - DICOM images and studies</li>
 *   <li><b>contrast_administration</b> - Contrast media tracking</li>
 *   <li><b>equipment_maintenance</b> - Equipment maintenance log</li>
 * </ul>
 *
 * <h2>Integration Points</h2>
 * <ul>
 *   <li>Patient Module - Patient demographics and medical records</li>
 *   <li>Clinical Module - Encounters and clinical orders</li>
 *   <li>Billing Module - Examination costs and billing</li>
 *   <li>DICOM/PACS - Medical imaging systems</li>
 * </ul>
 *
 * <h2>Design Patterns</h2>
 * <ul>
 *   <li>Soft Delete Pattern - All master data uses soft delete (deleted_at timestamp)</li>
 *   <li>Audit Trail - Created/updated timestamps and user tracking</li>
 *   <li>Optimistic Locking - Version field for concurrent access</li>
 *   <li>UUID Primary Keys - For distributed system compatibility</li>
 *   <li>Indonesian Language Support - All enums include Indonesian translations</li>
 * </ul>
 *
 * <h2>Indonesian Specific Features</h2>
 * <ul>
 *   <li>BPJS tariff support for examinations</li>
 *   <li>Indonesian language labels in enumerations</li>
 *   <li>Integration with SATUSEHAT for radiology results (future)</li>
 *   <li>KARS accreditation requirements support</li>
 * </ul>
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
package com.yudha.hms.radiology;