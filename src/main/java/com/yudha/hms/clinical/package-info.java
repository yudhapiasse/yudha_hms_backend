/**
 * Clinical Module
 *
 * This module handles all clinical documentation and medical records including:
 * - SOAP notes (Subjective, Objective, Assessment, Plan)
 * - Medical diagnoses (ICD-10 coding)
 * - Medical procedures (ICD-9-CM coding)
 * - Clinical orders (medications, labs, radiology, etc.)
 * - Vital signs recording
 * - Clinical assessments
 * - Progress notes
 * - Discharge summaries
 * - Doctor's orders
 *
 * Indonesian Specific Features:
 * - ICD-10 Indonesian version coding
 * - INA-CBGs grouper integration
 * - Electronic Medical Record (E-RME) compliance
 * - Digital signature for medical documents
 * - Clinical data submission to SATUSEHAT (FHIR R4)
 * - RL (Reporting Line) data extraction
 *
 * Clinical Documentation Standards:
 * - SOAP format for medical records
 * - ICD-10 for diagnoses
 * - ICD-9-CM for procedures
 * - SNOMED CT for clinical terms
 * - LOINC for laboratory observations
 *
 * Package Structure:
 * - controller: REST API endpoints for clinical documentation
 * - service: Business logic for clinical workflows
 * - repository: Data access layer for clinical entities
 * - entity: JPA entities (ClinicalNote, Diagnosis, Procedure, VitalSigns, etc.)
 * - dto: Data Transfer Objects for API requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.clinical;
