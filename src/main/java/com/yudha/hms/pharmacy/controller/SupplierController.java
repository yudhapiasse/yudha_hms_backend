package com.yudha.hms.pharmacy.controller;

import com.yudha.hms.pharmacy.dto.CreateSupplierRequest;
import com.yudha.hms.pharmacy.dto.SupplierResponse;
import com.yudha.hms.pharmacy.service.DrugService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Supplier Controller.
 *
 * REST API endpoints for supplier management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/v1/pharmacy/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final DrugService drugService;

    /**
     * Create new supplier
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PHARMACY_ADMIN', 'ADMIN')")
    public ResponseEntity<SupplierResponse> createSupplier(
            @Valid @RequestBody CreateSupplierRequest request,
            @RequestAttribute("currentUserId") String userId) {
        SupplierResponse response = drugService.createSupplier(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all active suppliers
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'ADMIN')")
    public ResponseEntity<List<SupplierResponse>> getActiveSuppliers() {
        List<SupplierResponse> responses = drugService.getActiveSuppliers();
        return ResponseEntity.ok(responses);
    }

    /**
     * Search suppliers by name
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'ADMIN')")
    public ResponseEntity<List<SupplierResponse>> searchSuppliers(@RequestParam String name) {
        List<SupplierResponse> responses = drugService.searchSuppliers(name);
        return ResponseEntity.ok(responses);
    }
}
