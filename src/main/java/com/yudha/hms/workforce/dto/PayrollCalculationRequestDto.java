package com.yudha.hms.workforce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for triggering payroll calculation.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollCalculationRequestDto {

    /**
     * Payroll period ID
     */
    @NotNull(message = "Payroll period ID is required")
    private UUID payrollPeriodId;

    /**
     * Specific employee IDs to calculate (if null, calculate for all active employees)
     */
    private List<UUID> employeeIds;

    /**
     * Department IDs to filter employees (optional)
     */
    private List<UUID> departmentIds;

    /**
     * Recalculate even if already calculated
     */
    private Boolean forceRecalculate;

    /**
     * Calculate in test mode (don't save results)
     */
    private Boolean testMode;
}
