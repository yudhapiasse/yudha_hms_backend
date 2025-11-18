/**
 * Patient Management Module
 *
 * This module handles all patient-related operations including:
 * - Patient registration and demographic data
 * - NIK (National ID) validation for Indonesian citizens
 * - BPJS number validation
 * - Medical record number (MRN) generation and management
 * - Patient search and retrieval
 * - Patient photo management
 * - Emergency contact information
 * - Patient address (KTP vs Domicile)
 * - Patient barcode/QR code generation
 *
 * Indonesian Specific Features:
 * - NIK (16 digits) validation and storage
 * - Religion (required field for Indonesian context)
 * - KTP address vs Domicile address
 * - Integration with DUKCAPIL for NIK verification
 *
 * Package Structure:
 * - controller: REST API endpoints for patient operations
 * - service: Business logic for patient management
 * - repository: Data access layer for patient entities
 * - entity: JPA entities (Patient, EmergencyContact, etc.)
 * - dto: Data Transfer Objects for API requests/responses
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.patient;