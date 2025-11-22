package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.constant.HolidayType;
import com.yudha.hms.workforce.entity.PublicHoliday;
import com.yudha.hms.workforce.service.PublicHolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller untuk Manajemen Hari Libur Nasional (Public Holiday Management)
 *
 * Endpoints:
 * - Create/update public holidays
 * - View holidays by year/type
 * - Check if date is holiday
 *
 * Mendukung hari libur nasional Indonesia dan cuti bersama
 */
@RestController
@RequestMapping("/api/workforce/public-holidays")
@RequiredArgsConstructor
@Slf4j
public class PublicHolidayController {

    private final PublicHolidayService publicHolidayService;

    /**
     * Buat hari libur baru
     * POST /api/workforce/public-holidays
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PublicHoliday>> createPublicHoliday(
            @RequestBody PublicHoliday publicHoliday) {

        log.info("POST /api/workforce/public-holidays - Name: {}, Date: {}",
                publicHoliday.getHolidayName(), publicHoliday.getHolidayDate());

        PublicHoliday created = publicHolidayService.createPublicHoliday(publicHoliday);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hari libur berhasil dibuat", created));
    }

    /**
     * Update hari libur
     * PUT /api/workforce/public-holidays/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicHoliday>> updatePublicHoliday(
            @PathVariable UUID id,
            @RequestBody PublicHoliday publicHoliday) {

        log.info("PUT /api/workforce/public-holidays/{}", id);

        PublicHoliday updated = publicHolidayService.updatePublicHoliday(id, publicHoliday);

        return ResponseEntity.ok(ApiResponse.success("Hari libur berhasil diupdate", updated));
    }

    /**
     * Dapatkan hari libur berdasarkan ID
     * GET /api/workforce/public-holidays/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PublicHoliday>> getPublicHolidayById(@PathVariable UUID id) {

        log.info("GET /api/workforce/public-holidays/{}", id);

        PublicHoliday holiday = publicHolidayService.getPublicHolidayById(id);

        return ResponseEntity.ok(ApiResponse.success("Hari libur ditemukan", holiday));
    }

    /**
     * Dapatkan hari libur berdasarkan tahun
     * GET /api/workforce/public-holidays/year/{year}
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<ApiResponse<List<PublicHoliday>>> getHolidaysByYear(
            @PathVariable Integer year,
            @RequestParam(required = false) HolidayType type) {

        log.info("GET /api/workforce/public-holidays/year/{} - Type: {}", year, type);

        List<PublicHoliday> holidays = type != null
                ? publicHolidayService.getHolidaysByYearAndType(year, type)
                : publicHolidayService.getHolidaysByYear(year);

        return ResponseEntity.ok(ApiResponse.success("Hari libur tahun " + year, holidays));
    }

    /**
     * Dapatkan hari libur dalam rentang tanggal
     * GET /api/workforce/public-holidays/range
     */
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<PublicHoliday>>> getHolidaysInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("GET /api/workforce/public-holidays/range - Period: {} to {}", startDate, endDate);

        List<PublicHoliday> holidays = publicHolidayService.getHolidaysInDateRange(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("Hari libur dalam periode", holidays));
    }

    /**
     * Cek apakah tanggal adalah hari libur
     * GET /api/workforce/public-holidays/check/{date}
     */
    @GetMapping("/check/{date}")
    public ResponseEntity<ApiResponse<Boolean>> isHoliday(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("GET /api/workforce/public-holidays/check/{}", date);

        boolean isHoliday = publicHolidayService.isHoliday(date);

        return ResponseEntity.ok(ApiResponse.success(
                isHoliday ? "Tanggal ini adalah hari libur" : "Tanggal ini bukan hari libur",
                isHoliday
        ));
    }

    /**
     * Hapus hari libur (soft delete)
     * DELETE /api/workforce/public-holidays/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePublicHoliday(@PathVariable UUID id) {

        log.info("DELETE /api/workforce/public-holidays/{}", id);

        publicHolidayService.deletePublicHoliday(id);

        return ResponseEntity.ok(ApiResponse.success("Hari libur berhasil dihapus", null));
    }
}
