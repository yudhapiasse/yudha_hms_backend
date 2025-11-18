package com.yudha.hms.shared.exception;

import com.yudha.hms.shared.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler.
 *
 * Centralized exception handling for all REST controllers.
 * Catches exceptions and returns consistent ErrorResponse format with:
 * - HTTP status codes
 * - Indonesian error messages
 * - Field-level validation errors
 * - Request path information
 * - Debug information (in non-production environments)
 *
 * Exception Mappings:
 * - DuplicateResourceException → 409 CONFLICT
 * - ResourceNotFoundException → 404 NOT FOUND
 * - ValidationException → 400 BAD REQUEST
 * - MethodArgumentNotValidException → 400 BAD REQUEST
 * - IllegalArgumentException → 400 BAD REQUEST
 * - General Exception → 500 INTERNAL SERVER ERROR
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle duplicate resource exceptions.
     *
     * Thrown when attempting to create a resource that already exists
     * (e.g., duplicate NIK, BPJS number, MRN).
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 409 CONFLICT
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        log.warn("Duplicate resource detected: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("DUPLICATE_RESOURCE")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    /**
     * Handle resource not found exceptions.
     *
     * Thrown when a requested resource does not exist
     * (e.g., patient not found by ID, MRN, or NIK).
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn("Resource not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("RESOURCE_NOT_FOUND")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    /**
     * Handle business validation exceptions.
     *
     * Thrown when business rules are violated
     * (e.g., NIK format invalid, BPJS number invalid, cross-field validation).
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 400 BAD REQUEST and field errors
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        log.warn("Validation failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message(ex.getMessage())
                .errors(ex.getErrors())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Handle Jakarta Bean Validation exceptions.
     *
     * Thrown when @Valid fails on controller method arguments
     * (e.g., @NotNull, @NotBlank, @Pattern violations).
     *
     * Extracts field-level errors from BindingResult.
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 400 BAD REQUEST and field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        log.warn("Method argument validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_ERROR")
                .message("Validasi gagal pada " + fieldErrors.size() + " field")
                .errors(fieldErrors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Handle illegal argument exceptions.
     *
     * Thrown when method arguments are invalid
     * (e.g., invalid enum values, null required parameters).
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 400 BAD REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_ARGUMENT")
                .message("Parameter tidak valid: " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Handle null pointer exceptions.
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(
            NullPointerException ex,
            HttpServletRequest request) {

        log.error("Null pointer exception occurred", ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("Terjadi kesalahan internal server")
                .path(request.getRequestURI())
                .debugInfo("NullPointerException: " + ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    /**
     * Handle all other uncaught exceptions.
     *
     * Catch-all handler for unexpected exceptions.
     * Returns generic error message to client while logging full details.
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected exception occurred", ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("Terjadi kesalahan yang tidak terduga. Silakan hubungi administrator.")
                .path(request.getRequestURI())
                .debugInfo(ex.getClass().getSimpleName() + ": " + ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    /**
     * Handle data integrity violation exceptions.
     *
     * Thrown when database constraints are violated
     * (e.g., unique constraint, foreign key constraint, not null constraint).
     *
     * Note: This requires spring-boot-starter-data-jpa which includes
     * org.springframework.dao.DataIntegrityViolationException
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 409 CONFLICT
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex,
            HttpServletRequest request) {

        log.error("Data integrity violation", ex);

        String message = "Terjadi pelanggaran integritas data";
        String debugInfo = ex.getMessage();

        // Extract more meaningful message from exception
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("unique constraint") ||
                ex.getMessage().contains("duplicate key")) {
                message = "Data sudah ada dalam sistem";
            } else if (ex.getMessage().contains("foreign key constraint")) {
                message = "Data terkait dengan data lain dan tidak dapat diubah";
            } else if (ex.getMessage().contains("not-null constraint")) {
                message = "Field wajib tidak boleh kosong";
            }
        }

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("DATA_INTEGRITY_VIOLATION")
                .message(message)
                .path(request.getRequestURI())
                .debugInfo(debugInfo)
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    /**
     * Handle optimistic locking failures.
     *
     * Thrown when concurrent updates conflict (version mismatch).
     *
     * @param ex the exception
     * @param request the HTTP request
     * @return error response with 409 CONFLICT
     */
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex,
            HttpServletRequest request) {

        log.warn("Optimistic locking failure: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("CONCURRENT_UPDATE_CONFLICT")
                .message("Data telah diubah oleh pengguna lain. Silakan muat ulang dan coba lagi.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}
