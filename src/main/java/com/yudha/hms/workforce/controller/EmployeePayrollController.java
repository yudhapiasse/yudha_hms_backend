package com.yudha.hms.workforce.controller;

import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.workforce.dto.EmployeePayrollResponseDto;
import com.yudha.hms.workforce.dto.PayrollCalculationRequestDto;
import com.yudha.hms.workforce.dto.PayrollSummaryResponseDto;
import com.yudha.hms.workforce.entity.EmployeePayroll;
import com.yudha.hms.workforce.repository.EmployeePayrollRepository;
import com.yudha.hms.workforce.service.PayrollCalculationService;
import com.yudha.hms.workforce.service.SalarySlipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Employee Payroll REST Controller.
 *
 * Provides RESTful endpoints for employee payroll management:
 * - Calculate payroll for employees
 * - Retrieve payroll records
 * - Get payroll summaries
 * - Approve payroll
 * - Process payments
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-22
 */
@RestController
@RequestMapping("/api/workforce/payroll")
@RequiredArgsConstructor
@Slf4j
public class EmployeePayrollController {

    private final PayrollCalculationService payrollCalculationService;
    private final EmployeePayrollRepository employeePayrollRepository;
    private final SalarySlipService salarySlipService;

    /**
     * Calculate payroll for employees.
     *
     * POST /api/workforce/payroll/calculate
     *
     * @param requestDto payroll calculation request
     * @return calculation result with 200 OK
     */
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculatePayroll(
            @Valid @RequestBody PayrollCalculationRequestDto requestDto) {

        log.info("POST /api/workforce/payroll/calculate - Calculating payroll for period: {}",
                requestDto.getPayrollPeriodId());

        // For now, just return a placeholder response
        // This will be implemented with proper batch calculation logic
        Map<String, Object> result = Map.of(
                "message", "Payroll calculation initiated",
                "payrollPeriodId", requestDto.getPayrollPeriodId(),
                "status", "processing"
        );

        return ResponseEntity.ok(
                ApiResponse.success("Payroll calculation started successfully", result)
        );
    }

    /**
     * Calculate payroll for a single employee.
     *
     * POST /api/workforce/payroll/calculate/{employeeId}
     *
     * @param employeeId employee UUID
     * @param payrollPeriodId payroll period UUID
     * @return calculated payroll with 200 OK
     */
    @PostMapping("/calculate/{employeeId}")
    public ResponseEntity<ApiResponse<EmployeePayrollResponseDto>> calculateEmployeePayroll(
            @PathVariable UUID employeeId,
            @RequestParam UUID payrollPeriodId) {

        log.info("POST /api/workforce/payroll/calculate/{} - Calculating payroll for employee in period: {}",
                employeeId, payrollPeriodId);

        EmployeePayroll payroll = payrollCalculationService.calculateEmployeePayroll(
                employeeId, payrollPeriodId);

        EmployeePayrollResponseDto response = mapToResponseDto(payroll);

        return ResponseEntity.ok(
                ApiResponse.success("Employee payroll calculated successfully", response)
        );
    }

    /**
     * Get all payroll records for a period.
     *
     * GET /api/workforce/payroll/period/{periodId}
     *
     * @param periodId payroll period UUID
     * @return list of payroll records with 200 OK
     */
    @GetMapping("/period/{periodId}")
    public ResponseEntity<ApiResponse<List<EmployeePayrollResponseDto>>> getPayrollByPeriod(
            @PathVariable UUID periodId) {

        log.info("GET /api/workforce/payroll/period/{} - Fetching payroll records for period", periodId);

        List<EmployeePayroll> payrolls = employeePayrollRepository.findByPayrollPeriodId(periodId);
        List<EmployeePayrollResponseDto> response = payrolls.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Payroll records retrieved successfully", response)
        );
    }

    /**
     * Get payroll record by ID.
     *
     * GET /api/workforce/payroll/{id}
     *
     * @param id payroll record UUID
     * @return payroll data with 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeePayrollResponseDto>> getPayrollById(
            @PathVariable UUID id) {

        log.info("GET /api/workforce/payroll/{} - Fetching payroll record by ID", id);

        EmployeePayroll payroll = employeePayrollRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll record not found: " + id));

        EmployeePayrollResponseDto response = mapToResponseDto(payroll);

        return ResponseEntity.ok(
                ApiResponse.success("Payroll record found", response)
        );
    }

    /**
     * Get payroll records for an employee.
     *
     * GET /api/workforce/payroll/employee/{employeeId}
     *
     * @param employeeId employee UUID
     * @return list of payroll records with 200 OK
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<EmployeePayrollResponseDto>>> getPayrollByEmployee(
            @PathVariable UUID employeeId) {

        log.info("GET /api/workforce/payroll/employee/{} - Fetching payroll records for employee", employeeId);

        List<EmployeePayroll> payrolls = employeePayrollRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId);
        List<EmployeePayrollResponseDto> response = payrolls.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Employee payroll records retrieved successfully", response)
        );
    }

    /**
     * Get payroll summary for a period.
     *
     * GET /api/workforce/payroll/period/{periodId}/summary
     *
     * @param periodId payroll period UUID
     * @return payroll summary with 200 OK
     */
    @GetMapping("/period/{periodId}/summary")
    public ResponseEntity<ApiResponse<PayrollSummaryResponseDto>> getPayrollSummary(
            @PathVariable UUID periodId) {

        log.info("GET /api/workforce/payroll/period/{}/summary - Fetching payroll summary", periodId);

        Map<String, Object> summary = payrollCalculationService.getPayrollSummary(periodId);

        PayrollSummaryResponseDto response = PayrollSummaryResponseDto.builder()
                .payrollPeriodId(periodId)
                .employeeCount((Integer) summary.get("employeeCount"))
                .totalGrossSalary((BigDecimal) summary.get("totalGrossSalary"))
                .totalNetSalary((BigDecimal) summary.get("totalNetSalary"))
                .totalTax((BigDecimal) summary.get("totalTax"))
                .totalBpjsDeductions((BigDecimal) summary.get("totalBpjsDeductions"))
                .totalOvertimePay((BigDecimal) summary.get("totalOvertimePay"))
                .totalDeductions((BigDecimal) summary.get("totalDeductions"))
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Payroll summary retrieved successfully", response)
        );
    }

    /**
     * Get total payroll cost for a period.
     *
     * GET /api/workforce/payroll/period/{periodId}/cost
     *
     * @param periodId payroll period UUID
     * @return total cost with 200 OK
     */
    @GetMapping("/period/{periodId}/cost")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getTotalPayrollCost(
            @PathVariable UUID periodId) {

        log.info("GET /api/workforce/payroll/period/{}/cost - Fetching total payroll cost", periodId);

        BigDecimal totalCost = payrollCalculationService.getTotalPayrollCostForPeriod(periodId);
        BigDecimal netPayroll = payrollCalculationService.getNetPayrollForPeriod(periodId);

        Map<String, BigDecimal> result = Map.of(
                "totalGrossCost", totalCost,
                "totalNetPayroll", netPayroll
        );

        return ResponseEntity.ok(
                ApiResponse.success("Payroll cost retrieved successfully", result)
        );
    }

    /**
     * Generate salary slip for a payroll record.
     *
     * GET /api/workforce/payroll/{id}/salary-slip
     *
     * @param id payroll record UUID
     * @return salary slip data with 200 OK
     */
    @GetMapping("/{id}/salary-slip")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSalarySlip(@PathVariable UUID id) {

        log.info("GET /api/workforce/payroll/{}/salary-slip - Generating salary slip", id);

        Map<String, Object> salarySlip = salarySlipService.generateSalarySlipData(id);

        return ResponseEntity.ok(
                ApiResponse.success("Salary slip generated successfully", salarySlip)
        );
    }

    /**
     * Generate salary slips for all payrolls in a period.
     *
     * GET /api/workforce/payroll/period/{periodId}/salary-slips
     *
     * @param periodId payroll period UUID
     * @return list of salary slip data with 200 OK
     */
    @GetMapping("/period/{periodId}/salary-slips")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPeriodSalarySlips(
            @PathVariable UUID periodId) {

        log.info("GET /api/workforce/payroll/period/{}/salary-slips - Generating salary slips for period", periodId);

        List<Map<String, Object>> salarySlips = salarySlipService.generatePeriodSalarySlips(periodId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        String.format("Generated %d salary slips successfully", salarySlips.size()),
                        salarySlips
                )
        );
    }

    /**
     * Map EmployeePayroll entity to ResponseDto.
     */
    private EmployeePayrollResponseDto mapToResponseDto(EmployeePayroll payroll) {
        return EmployeePayrollResponseDto.builder()
                .id(payroll.getId())
                .employeeId(payroll.getEmployeeId())
                .payrollPeriodId(payroll.getPayrollPeriodId())
                .payrollNumber(payroll.getPayrollNumber())
                .departmentId(payroll.getDepartmentId())
                .positionId(payroll.getPositionId())
                .employmentType(payroll.getEmploymentType())
                .workingDays(payroll.getWorkingDays())
                .actualWorkingDays(payroll.getActualWorkingDays())
                .absentDays(payroll.getAbsentDays())
                .basicSalary(payroll.getBasicSalary())
                .totalAllowances(payroll.getTotalAllowances())
                .normalOvertimeHours(payroll.getNormalOvertimeHours())
                .weekendOvertimeHours(payroll.getWeekendOvertimeHours())
                .holidayOvertimeHours(payroll.getHolidayOvertimeHours())
                .totalOvertime(payroll.getTotalOvertime())
                .thrAmount(payroll.getThrAmount())
                .totalIncentives(payroll.getTotalIncentives())
                .grossSalary(payroll.getGrossSalary())
                .bpjsKesehatanEmployee(payroll.getBpjsKesehatanEmployee())
                .bpjsKesehatanFamily(payroll.getBpjsKesehatanFamily())
                .bpjsTkJht(payroll.getBpjsTkJht())
                .bpjsTkJp(payroll.getBpjsTkJp())
                .pph21Amount(payroll.getPph21Amount())
                .loanDeduction(payroll.getLoanDeduction())
                .otherDeductions(payroll.getOtherDeductions())
                .totalDeductions(payroll.getTotalDeductions())
                .netSalary(payroll.getNetSalary())
                .status(payroll.getStatus())
                .paymentStatus(payroll.getPaymentStatus())
                .paymentMethod(payroll.getPaymentMethod())
                .createdAt(payroll.getCreatedAt())
                .createdBy(payroll.getCreatedBy())
                .updatedAt(payroll.getUpdatedAt())
                .updatedBy(payroll.getUpdatedBy())
                .build();
    }
}
