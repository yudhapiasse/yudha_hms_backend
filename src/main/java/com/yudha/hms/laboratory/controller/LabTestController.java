package com.yudha.hms.laboratory.controller;

import com.yudha.hms.laboratory.dto.request.LabTestRequest;
import com.yudha.hms.laboratory.dto.response.ApiResponse;
import com.yudha.hms.laboratory.dto.response.LabTestParameterResponse;
import com.yudha.hms.laboratory.dto.response.LabTestResponse;
import com.yudha.hms.laboratory.dto.response.PageResponse;
import com.yudha.hms.laboratory.dto.search.TestSearchCriteria;
import com.yudha.hms.laboratory.entity.LabTest;
import com.yudha.hms.laboratory.entity.LabTestParameter;
import com.yudha.hms.laboratory.service.LabTestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Laboratory Test Controller.
 *
 * REST controller for managing laboratory tests.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@RestController
@RequestMapping("/api/laboratory/tests")
@RequiredArgsConstructor
@Slf4j
public class LabTestController {

    private final LabTestService testService;

    /**
     * Create new laboratory test
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LabTestResponse>> createTest(
            @Valid @RequestBody LabTestRequest request) {
        log.info("Creating laboratory test: {}", request.getName());

        // Convert DTO to entity
        LabTest test = convertToEntity(request);
        LabTest savedTest = testService.createTest(test);
        LabTestResponse response = toResponse(savedTest);

        log.info("Laboratory test created successfully: {}", savedTest.getTestCode());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Test created successfully", response));
    }

    /**
     * Update existing laboratory test
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LabTestResponse>> updateTest(
            @PathVariable UUID id,
            @Valid @RequestBody LabTestRequest request) {
        log.info("Updating laboratory test ID: {}", id);

        LabTest testUpdate = convertToEntity(request);
        LabTest test = testService.updateTest(id, testUpdate);
        LabTestResponse response = toResponse(test);

        log.info("Laboratory test updated successfully: {}", test.getTestCode());

        return ResponseEntity.ok(ApiResponse.success("Test updated successfully", response));
    }

    /**
     * Get laboratory test by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabTestResponse>> getTestById(
            @PathVariable UUID id) {
        log.info("Fetching laboratory test ID: {}", id);

        LabTest test = testService.getTestById(id);
        LabTestResponse response = toResponse(test);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Search laboratory tests
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<LabTestResponse>>> searchTests(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        log.info("Searching laboratory tests - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<LabTest> tests = testService.searchTests(search, pageable);
        Page<LabTestResponse> responsePage = tests.map(this::toResponse);
        PageResponse<LabTestResponse> pageResponse = PageResponse.of(responsePage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse));
    }

    /**
     * Get test by test code
     */
    @GetMapping("/code/{testCode}")
    public ResponseEntity<ApiResponse<LabTestResponse>> getTestByCode(
            @PathVariable String testCode) {
        log.info("Fetching laboratory test by code: {}", testCode);

        LabTest test = testService.getTestByCode(testCode);
        LabTestResponse response = toResponse(test);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get test by LOINC code
     */
    @GetMapping("/loinc/{loincCode}")
    public ResponseEntity<ApiResponse<LabTestResponse>> getTestByLoincCode(
            @PathVariable String loincCode) {
        log.info("Fetching laboratory test by LOINC code: {}", loincCode);

        LabTest test = testService.getTestByLoincCode(loincCode);
        LabTestResponse response = toResponse(test);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get test parameters
     */
    @GetMapping("/{id}/parameters")
    public ResponseEntity<ApiResponse<List<LabTestParameterResponse>>> getTestParameters(
            @PathVariable UUID id) {
        log.info("Fetching parameters for test ID: {}", id);

        List<LabTestParameter> parameters = testService.getTestParameters(id);
        List<LabTestParameterResponse> responses = parameters.stream()
                .map(this::toParameterResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Activate laboratory test
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<LabTestResponse>> activateTest(
            @PathVariable UUID id) {
        log.info("Activating laboratory test ID: {}", id);

        testService.activateTest(id);
        LabTest test = testService.getTestById(id);
        LabTestResponse response = toResponse(test);

        log.info("Laboratory test activated successfully: {}", test.getTestCode());

        return ResponseEntity.ok(ApiResponse.success("Test activated successfully", response));
    }

    /**
     * Deactivate laboratory test
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<LabTestResponse>> deactivateTest(
            @PathVariable UUID id) {
        log.info("Deactivating laboratory test ID: {}", id);

        testService.deactivateTest(id);
        LabTest test = testService.getTestById(id);
        LabTestResponse response = toResponse(test);

        log.info("Laboratory test deactivated successfully: {}", test.getTestCode());

        return ResponseEntity.ok(ApiResponse.success("Test deactivated successfully", response));
    }

    /**
     * Soft delete laboratory test
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTest(
            @PathVariable UUID id) {
        log.info("Deleting laboratory test ID: {}", id);

        testService.deleteTest(id, "SYSTEM");
        log.info("Laboratory test deleted successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success("Test deleted successfully"));
    }

    /**
     * Convert entity to response DTO
     */
    private LabTestResponse toResponse(LabTest test) {
        LabTestResponse response = new LabTestResponse();
        response.setId(test.getId());
        response.setName(test.getName());
        response.setTestCode(test.getTestCode());
        response.setShortName(test.getShortName());
        response.setCategoryId(test.getCategory() != null ? test.getCategory().getId() : null);
        response.setCategoryName(test.getCategory() != null ? test.getCategory().getName() : null);
        response.setTestMethodology(test.getTestMethodology());
        response.setSampleType(test.getSampleType());
        response.setSampleVolumeMl(test.getSampleVolumeMl());
        response.setSampleVolumeUnit(test.getSampleVolumeUnit());
        response.setSampleContainer(test.getSampleContainer());
        response.setProcessingTimeMinutes(test.getProcessingTimeMinutes());
        response.setCitoProcessingTimeMinutes(test.getCitoProcessingTimeMinutes());
        response.setBaseCost(test.getBaseCost());
        response.setUrgentCost(test.getUrgentCost());
        response.setLoincCode(test.getLoincCode());
        response.setActive(test.getActive());
        response.setCreatedAt(test.getCreatedAt());
        response.setUpdatedAt(test.getUpdatedAt());
        return response;
    }

    /**
     * Convert parameter entity to response DTO
     */
    private LabTestParameterResponse toParameterResponse(LabTestParameter parameter) {
        LabTestParameterResponse response = new LabTestParameterResponse();
        response.setId(parameter.getId());
        response.setParameterName(parameter.getParameterName());
        response.setParameterCode(parameter.getParameterCode());
        response.setDataType(parameter.getDataType());
        response.setUnit(parameter.getUnit());
        response.setNormalRangeMin(parameter.getNormalRangeLow());
        response.setNormalRangeMax(parameter.getNormalRangeHigh());
        response.setNormalRangeText(parameter.getNormalRangeText());
        response.setCriticalLowValue(parameter.getCriticalLow());
        response.setCriticalHighValue(parameter.getCriticalHigh());
        response.setPanicLowValue(parameter.getPanicLow());
        response.setPanicHighValue(parameter.getPanicHigh());
        response.setDisplayOrder(parameter.getDisplayOrder());
        response.setIsMandatory(parameter.getIsMandatory());
        response.setCreatedAt(parameter.getCreatedAt());
        response.setUpdatedAt(parameter.getUpdatedAt());
        return response;
    }

    /**
     * Convert request DTO to entity
     */
    private LabTest convertToEntity(LabTestRequest request) {
        LabTest test = new LabTest();
        test.setTestCode(request.getTestCode());
        test.setName(request.getName());
        test.setShortName(request.getShortName());
        test.setLoincCode(request.getLoincCode());
        test.setSampleType(request.getSampleType());
        test.setSampleVolumeMl(request.getSampleVolumeMl());
        test.setSampleVolumeUnit(request.getSampleVolumeUnit());
        test.setSampleContainer(request.getSampleContainer());
        test.setPreparationInstructions(request.getPreparationInstructions());
        test.setProcessingTimeMinutes(request.getProcessingTimeMinutes());
        test.setCitoProcessingTimeMinutes(request.getCitoProcessingTimeMinutes());
        test.setBaseCost(request.getBaseCost());
        test.setUrgentCost(request.getUrgentCost());
        test.setBpjsTariff(request.getBpjsTariff());
        test.setTestMethodology(request.getTestMethodology());
        test.setRequiresApproval(request.getRequiresApproval());
        test.setRequiresPathologistReview(request.getRequiresPathologistReview());
        test.setFastingRequired(request.getFastingRequired());
        test.setFastingDurationHours(request.getFastingDurationHours());
        test.setActive(request.getActive());

        // Set category - need to create a minimal category object with just the ID
        if (request.getCategoryId() != null) {
            com.yudha.hms.laboratory.entity.LabTestCategory category = new com.yudha.hms.laboratory.entity.LabTestCategory();
            category.setId(request.getCategoryId());
            test.setCategory(category);
        }

        return test;
    }
}
