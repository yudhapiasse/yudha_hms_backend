package com.yudha.hms.radiology.dto.request;

import com.yudha.hms.radiology.constant.PregnancyTestResult;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Record Pregnancy Test Request DTO.
 *
 * Used for recording pregnancy test results.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordPregnancyTestRequest {

    /**
     * Pregnancy test result
     */
    @NotNull(message = "Hasil tes kehamilan harus diisi")
    private PregnancyTestResult result;

    /**
     * Test date
     */
    @NotNull(message = "Tanggal tes harus diisi")
    private LocalDate testDate;
}
