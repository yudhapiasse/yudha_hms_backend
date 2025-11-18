/**
 * SATUSEHAT Integration Module
 *
 * This module handles integration with SATUSEHAT (Platform Data dan Informasi Kesehatan Indonesia)
 * Ministry of Health's national health data exchange platform.
 *
 * SATUSEHAT Overview:
 * - National health data interoperability platform
 * - Based on FHIR R4 (Fast Healthcare Interoperability Resources)
 * - Mandatory for all healthcare facilities in Indonesia (Permenkes 24/2022)
 * - Replaces previous SIRS (Sistem Informasi Rumah Sakit)
 *
 * Main Integration Points:
 * - OAuth 2.0 authentication
 * - FHIR R4 REST API
 * - Patient resource management
 * - Encounter (Kunjungan) resource
 * - Observation (Lab results, vital signs)
 * - Medication (Prescription, dispensing)
 * - Condition (Diagnosis)
 * - Procedure
 * - Organization (Healthcare facility)
 * - Practitioner (Healthcare workers)
 * - Location (Departments, rooms)
 *
 * FHIR R4 Resources Implemented:
 * - Patient: NIK-based patient master index
 * - Encounter: Patient visits/admissions
 * - Observation: Vital signs, lab results, clinical observations
 * - Condition: Diagnoses (ICD-10)
 * - Procedure: Medical/surgical procedures (ICD-9-CM)
 * - Medication: Prescriptions and dispensing
 * - MedicationRequest: e-Prescribing
 * - MedicationDispense: Drug dispensing records
 * - Organization: Hospital/clinic information
 * - Practitioner: Doctor/nurse/staff information
 * - Location: Departments, wards, rooms
 * - ServiceRequest: Lab/radiology orders
 * - DiagnosticReport: Lab/radiology results
 *
 * Data Submission Requirements:
 * - All patient encounters must be reported
 * - Lab results submission (within 24 hours)
 * - Radiology results submission
 * - Prescription and dispensing data
 * - Clinical diagnoses and procedures
 * - Vital signs and clinical observations
 *
 * Indonesian Regulatory Compliance:
 * - Permenkes 24/2022 (E-Rekam Medis)
 * - Permenkes 20/2019 (SATUSEHAT)
 * - Data privacy and security (UU PDP)
 * - NIK as patient identifier
 *
 * Authentication:
 * - OAuth 2.0 client credentials flow
 * - Organization ID (satusehat_orgid)
 * - Client ID and Client Secret
 * - Access token management and refresh
 *
 * Package Structure:
 * - controller: REST API endpoints for SATUSEHAT operations
 * - service: Business logic for FHIR resource creation and submission
 * - repository: Data access layer for SATUSEHAT sync tracking
 * - entity: JPA entities for tracking submission status
 * - dto: Data Transfer Objects for FHIR resources
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.integration.satusehat;