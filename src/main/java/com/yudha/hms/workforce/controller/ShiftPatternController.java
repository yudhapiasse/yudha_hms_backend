package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.constant.ShiftType;
import com.yudha.hms.workforce.entity.ShiftPattern;
import com.yudha.hms.workforce.service.ShiftPatternService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller untuk Manajemen Pola Shift (Shift Pattern Management)
 *
 * Endpoints:
 * - Create/update shift patterns
 * - View shift patterns (pagi, siang, malam, libur)
 * - Delete shift patterns
 *
 * Mendukung pola shift: Pagi, Siang, Malam, Libur
 */
@RestController
@RequestMapping("/api/workforce/shift-patterns")
@RequiredArgsConstructor
@Slf4j
public class ShiftPatternController {

    private final ShiftPatternService shiftPatternService;

    /**
     * Buat pola shift baru
     * POST /api/workforce/shift-patterns
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ShiftPattern>> createShiftPattern(
            @RequestBody ShiftPattern shiftPattern) {

        log.info("POST /api/workforce/shift-patterns - Name: {}, Type: {}",
                shiftPattern.getShiftName(), shiftPattern.getShiftType());

        ShiftPattern created = shiftPatternService.createShiftPattern(shiftPattern);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pola shift berhasil dibuat", created));
    }

    /**
     * Update pola shift
     * PUT /api/workforce/shift-patterns/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftPattern>> updateShiftPattern(
            @PathVariable UUID id,
            @RequestBody ShiftPattern shiftPattern) {

        log.info("PUT /api/workforce/shift-patterns/{}", id);

        ShiftPattern updated = shiftPatternService.updateShiftPattern(id, shiftPattern);

        return ResponseEntity.ok(ApiResponse.success("Pola shift berhasil diupdate", updated));
    }

    /**
     * Dapatkan pola shift berdasarkan ID
     * GET /api/workforce/shift-patterns/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftPattern>> getShiftPatternById(@PathVariable UUID id) {

        log.info("GET /api/workforce/shift-patterns/{}", id);

        ShiftPattern shiftPattern = shiftPatternService.getShiftPatternById(id);

        return ResponseEntity.ok(ApiResponse.success("Pola shift ditemukan", shiftPattern));
    }

    /**
     * Dapatkan semua pola shift aktif
     * GET /api/workforce/shift-patterns
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftPattern>>> getAllActiveShiftPatterns(
            @RequestParam(required = false) ShiftType type,
            @RequestParam(required = false) UUID departmentId) {

        log.info("GET /api/workforce/shift-patterns - Type: {}, Department: {}", type, departmentId);

        List<ShiftPattern> patterns;

        if (type != null) {
            patterns = shiftPatternService.getShiftPatternsByType(type);
        } else if (departmentId != null) {
            patterns = shiftPatternService.getShiftPatternsByDepartment(departmentId);
        } else {
            patterns = shiftPatternService.getAllActiveShiftPatterns();
        }

        return ResponseEntity.ok(ApiResponse.success("Pola shift ditemukan", patterns));
    }

    /**
     * Dapatkan pola shift default
     * GET /api/workforce/shift-patterns/default
     */
    @GetMapping("/default")
    public ResponseEntity<ApiResponse<ShiftPattern>> getDefaultShiftPattern() {

        log.info("GET /api/workforce/shift-patterns/default");

        ShiftPattern shiftPattern = shiftPatternService.getDefaultShiftPattern();

        return ResponseEntity.ok(ApiResponse.success("Pola shift default", shiftPattern));
    }

    /**
     * Hapus pola shift (soft delete)
     * DELETE /api/workforce/shift-patterns/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteShiftPattern(@PathVariable UUID id) {

        log.info("DELETE /api/workforce/shift-patterns/{}", id);

        shiftPatternService.deleteShiftPattern(id);

        return ResponseEntity.ok(ApiResponse.success("Pola shift berhasil dihapus", null));
    }
}
