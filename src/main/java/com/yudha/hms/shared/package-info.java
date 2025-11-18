/**
 * Shared Module
 *
 * This module contains common utilities, configurations, constants, DTOs,
 * and exception handling used across all HMS modules.
 *
 * Sub-packages:
 *
 * 1. config/
 *    - Spring configuration classes
 *    - Security configuration (JWT, CORS, etc.)
 *    - Database configuration
 *    - Jackson configuration for JSON serialization
 *    - Async configuration
 *    - Cache configuration
 *    - WebSocket configuration
 *    - Audit configuration
 *
 * 2. constant/
 *    - Application constants
 *    - Indonesian-specific constants (provinces, cities, religions)
 *    - Medical constants (blood types, marital status)
 *    - BPJS constants (claim types, SEP types)
 *    - Status enumerations
 *    - Error codes
 *    - API endpoints
 *
 * 3. dto/
 *    - Common DTOs shared across modules
 *    - API response wrapper
 *    - Pagination request/response
 *    - Error response DTO
 *    - Success response DTO
 *    - Search criteria DTOs
 *
 * 4. exception/
 *    - Custom exception classes
 *    - Global exception handler (@ControllerAdvice)
 *    - Business exceptions (e.g., PatientNotFoundException)
 *    - Validation exceptions
 *    - Integration exceptions (BPJS, SATUSEHAT)
 *    - Database exceptions
 *
 * 5. util/
 *    - Utility classes for common operations
 *    - Date/time utilities (Indonesian timezone)
 *    - String utilities
 *    - NIK validation utilities
 *    - BPJS number validation
 *    - Medical record number generator
 *    - Barcode/QR code generators
 *    - Encryption/decryption utilities
 *    - PDF generation utilities
 *    - Excel generation utilities
 *    - Indonesian formatter (currency, date)
 *
 * Common Features:
 * - Centralized error handling
 * - Standardized API response format
 * - Reusable validation logic
 * - Indonesian-specific utilities
 * - Logging and audit trail support
 * - Security utilities (JWT, encryption)
 *
 * Design Principles:
 * - DRY (Don't Repeat Yourself)
 * - Single Responsibility Principle
 * - Stateless utilities
 * - Thread-safe implementations
 * - Well-documented public APIs
 *
 * Package Structure:
 * - config: Spring configuration classes
 * - constant: Application-wide constants and enums
 * - dto: Common data transfer objects
 * - exception: Custom exceptions and error handling
 * - util: Utility classes and helper methods
 *
 * @since 1.0.0
 * @version 1.0.0
 */
package com.yudha.hms.shared;