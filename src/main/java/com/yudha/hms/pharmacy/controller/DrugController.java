package com.yudha.hms.pharmacy.controller;

import com.yudha.hms.pharmacy.dto.*;
import com.yudha.hms.pharmacy.service.DrugService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Drug Controller.
 *
 * REST API endpoints for drug master data management.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/v1/pharmacy/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugService drugService;

    /**
     * Create new drug
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'ADMIN')")
    public ResponseEntity<DrugResponse> createDrug(
            @Valid @RequestBody CreateDrugRequest request,
            @RequestAttribute("currentUserId") String userId) {
        DrugResponse response = drugService.createDrug(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get drug by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<DrugResponse> getDrugById(@PathVariable UUID id) {
        DrugResponse response = drugService.getDrugById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get drug by code
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<DrugResponse> getDrugByCode(@PathVariable String code) {
        DrugResponse response = drugService.getDrugByCode(code);
        return ResponseEntity.ok(response);
    }

    /**
     * Update drug
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'ADMIN')")
    public ResponseEntity<DrugResponse> updateDrug(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDrugRequest request,
            @RequestAttribute("currentUserId") String userId) {
        DrugResponse response = drugService.updateDrug(id, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Search drugs
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<DrugResponse>> searchDrugs(@RequestParam String term) {
        List<DrugResponse> responses = drugService.searchDrugs(term);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all active drugs
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<List<DrugResponse>> getActiveDrugs() {
        List<DrugResponse> responses = drugService.getActiveDrugs();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get low stock drugs
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'ADMIN')")
    public ResponseEntity<List<DrugResponse>> getLowStockDrugs() {
        List<DrugResponse> responses = drugService.getLowStockDrugs();
        return ResponseEntity.ok(responses);
    }

    /**
     * Get BPJS approved drugs
     */
    @GetMapping("/bpjs-approved")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<DrugResponse>> getBpjsApprovedDrugs() {
        List<DrugResponse> responses = drugService.getBpjsApprovedDrugs();
        return ResponseEntity.ok(responses);
    }

    /**
     * Check drug interactions
     */
    @PostMapping("/interactions/check")
    @PreAuthorize("hasAnyRole('PHARMACIST', 'PHARMACY_ADMIN', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<DrugInteractionResponse>> checkDrugInteractions(
            @RequestBody List<UUID> drugIds) {
        List<DrugInteractionResponse> responses = drugService.checkDrugInteractions(drugIds);
        return ResponseEntity.ok(responses);
    }
}
