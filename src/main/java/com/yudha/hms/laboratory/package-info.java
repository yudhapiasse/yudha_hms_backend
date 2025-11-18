/**
 * Laboratory Module
 *
 * This module handles all laboratory operations including:
 * - Laboratory test ordering
 * - Specimen collection and tracking
 * - Result entry and validation
 * - Quality control management
 * - Laboratory Information System (LIS) integration
 * - Critical value alerts
 * - Reference range management
 * - Laboratory reports and interpretations
 * - Turnaround time tracking
 * - Laboratory billing integration
 *
 * Indonesian Specific Features:
 * - LOINC coding for lab tests
 * - Laboratory result submission to SATUSEHAT
 * - Indonesian lab test catalog
 * - External lab integration (Prodia, Parahita, etc.)
 * - Lab quality control reporting
 * - Accreditation compliance (KARS, ISO 15189)
 *
 * Laboratory Test Categories:
 * - HEMATOLOGY: Blood cell counts, coagulation
 * - CLINICAL_CHEMISTRY: Blood chemistry, enzymes
 * - IMMUNOLOGY: Antibodies, antigens
 * - MICROBIOLOGY: Culture, sensitivity testing
 * - MOLECULAR: PCR, genetic testing
 * - PATHOLOGY: Histopathology, cytology
 * - URINALYSIS: Urine analysis
 * - BLOOD_BANK: Blood typing, cross-matching
 *
 * Specimen Types:
 * - BLOOD: Whole blood, serum, plasma
 * - URINE: Urine samples
 * - STOOL: Fecal samples
 * - TISSUE: Biopsy specimens
 * - SWAB: Culture swabs
 * - BODY_FLUID: CSF, pleural fluid, etc.
 *
 * Package Structure:
 * - controller: REST API endpoints for laboratory operations
 * - service: Business logic for laboratory workflows
 * - repository: Data access layer for laboratory entities
 * - entity: JPA entities (LabOrder, LabResult, Specimen, Test, etc.)
 * - dto: Data Transfer Objects for API requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.laboratory;