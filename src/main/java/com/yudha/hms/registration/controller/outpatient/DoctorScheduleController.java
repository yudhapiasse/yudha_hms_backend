package com.yudha.hms.registration.controller.outpatient;

import com.yudha.hms.registration.dto.outpatient.DoctorAvailabilityResponse;
import com.yudha.hms.registration.entity.DoctorSchedule;
import com.yudha.hms.registration.service.outpatient.DoctorScheduleService;
import com.yudha.hms.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for doctor schedule and availability endpoints.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@RestController
@RequestMapping("/api/outpatient/doctors")
@RequiredArgsConstructor
@Slf4j
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    /**
     * Get doctor's schedules.
     * GET /api/outpatient/doctors/{doctorId}/schedules
     */
    @GetMapping("/{doctorId}/schedules")
    public ResponseEntity<ApiResponse<List<DoctorSchedule>>> getDoctorSchedules(
        @PathVariable UUID doctorId
    ) {
        log.info("GET /api/outpatient/doctors/{}/schedules - Fetch doctor schedules", doctorId);

        List<DoctorSchedule> schedules = doctorScheduleService.getDoctorSchedules(doctorId);

        return ResponseEntity.ok(ApiResponse.success(
            "Doctor schedules retrieved successfully",
            schedules
        ));
    }

    /**
     * Get doctor availability with time slots.
     * GET /api/outpatient/doctors/{doctorId}/availability
     */
    @GetMapping("/{doctorId}/availability")
    public ResponseEntity<ApiResponse<DoctorAvailabilityResponse>> getDoctorAvailability(
        @PathVariable UUID doctorId,
        @RequestParam UUID polyclinicId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("GET /api/outpatient/doctors/{}/availability?polyclinicId={}&date={} - Fetch availability",
            doctorId, polyclinicId, date);

        DoctorAvailabilityResponse availability = doctorScheduleService.getDoctorAvailability(
            doctorId,
            polyclinicId,
            date
        );

        return ResponseEntity.ok(ApiResponse.success(
            "Doctor availability retrieved successfully",
            availability
        ));
    }

    /**
     * Get available doctors at polyclinic.
     * GET /api/outpatient/polyclinics/{polyclinicId}/available-doctors
     */
    @GetMapping("/polyclinics/{polyclinicId}/available-doctors")
    public ResponseEntity<ApiResponse<List<DoctorAvailabilityResponse>>> getAvailableDoctors(
        @PathVariable UUID polyclinicId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("GET /api/outpatient/polyclinics/{}/available-doctors?date={} - Fetch available doctors",
            polyclinicId, date);

        List<DoctorAvailabilityResponse> availableDoctors = doctorScheduleService
            .getAvailableDoctorsAtPolyclinic(polyclinicId, date);

        return ResponseEntity.ok(ApiResponse.success(
            "Available doctors retrieved successfully",
            availableDoctors
        ));
    }
}