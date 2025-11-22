package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.constant.BankFileFormat;
import com.yudha.hms.workforce.service.BankTransferFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Bank Transfer REST Controller.
 *
 * Provides RESTful endpoints for bank transfer file generation:
 * - Generate bank transfer files in various Indonesian bank formats
 * - Download bank transfer files
 * - Get file metadata
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/workforce/bank-transfer")
@RequiredArgsConstructor
@Slf4j
public class BankTransferController {

    private final BankTransferFileService bankTransferFileService;

    /**
     * Generate bank transfer file for a payroll period.
     *
     * GET /api/workforce/bank-transfer/period/{periodId}/generate
     *
     * @param periodId payroll period UUID
     * @param format bank file format (default: STANDARD_CSV)
     * @return bank transfer file content
     */
    @GetMapping("/period/{periodId}/generate")
    public ResponseEntity<String> generateBankTransferFile(
            @PathVariable UUID periodId,
            @RequestParam(required = false, defaultValue = "STANDARD_CSV") BankFileFormat format) {

        log.info("GET /api/workforce/bank-transfer/period/{}/generate?format={} - Generating bank transfer file",
                periodId, format);

        String fileContent = bankTransferFileService.generateBankTransferFile(periodId, format);
        Map<String, Object> metadata = bankTransferFileService.getBankTransferFileMetadata(periodId, format);

        String fileName = (String) metadata.get("fileName");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
    }

    /**
     * Get bank transfer file metadata.
     *
     * GET /api/workforce/bank-transfer/period/{periodId}/metadata
     *
     * @param periodId payroll period UUID
     * @param format bank file format (default: STANDARD_CSV)
     * @return file metadata with 200 OK
     */
    @GetMapping("/period/{periodId}/metadata")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBankTransferFileMetadata(
            @PathVariable UUID periodId,
            @RequestParam(required = false, defaultValue = "STANDARD_CSV") BankFileFormat format) {

        log.info("GET /api/workforce/bank-transfer/period/{}/metadata?format={} - Fetching bank transfer file metadata",
                periodId, format);

        Map<String, Object> metadata = bankTransferFileService.getBankTransferFileMetadata(periodId, format);

        return ResponseEntity.ok(
                ApiResponse.success("Bank transfer file metadata retrieved successfully", metadata)
        );
    }

    /**
     * Generate bank transfer files grouped by bank.
     *
     * GET /api/workforce/bank-transfer/period/{periodId}/generate-by-bank
     *
     * @param periodId payroll period UUID
     * @return map of bank name to file content with 200 OK
     */
    @GetMapping("/period/{periodId}/generate-by-bank")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateBankTransferFilesByBank(
            @PathVariable UUID periodId) {

        log.info("GET /api/workforce/bank-transfer/period/{}/generate-by-bank - Generating bank transfer files by bank",
                periodId);

        Map<String, String> filesByBank = bankTransferFileService.generateBankTransferFilesByBank(periodId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        String.format("Generated %d bank transfer files", filesByBank.size()),
                        filesByBank
                )
        );
    }
}
