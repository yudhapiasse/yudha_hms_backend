package com.yudha.hms.registration.service.outpatient;

import com.yudha.hms.registration.dto.outpatient.DoctorAvailabilityResponse;
import com.yudha.hms.registration.entity.Doctor;
import com.yudha.hms.registration.entity.DoctorSchedule;
import com.yudha.hms.registration.entity.OutpatientRegistration;
import com.yudha.hms.registration.entity.Polyclinic;
import com.yudha.hms.registration.repository.DoctorRepository;
import com.yudha.hms.registration.repository.DoctorScheduleRepository;
import com.yudha.hms.registration.repository.OutpatientRegistrationRepository;
import com.yudha.hms.registration.repository.PolyclinicRepository;
import com.yudha.hms.shared.exception.BusinessException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for doctor schedule management and availability checking.
 * Handles schedule validation, time slot generation, and availability queries.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DoctorScheduleService {

    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final PolyclinicRepository polyclinicRepository;
    private final OutpatientRegistrationRepository registrationRepository;

    /**
     * Check if a doctor is available at a specific polyclinic on a given date and time.
     *
     * @param doctorId doctor ID
     * @param polyclinicId polyclinic ID
     * @param date appointment date
     * @param time appointment time
     * @return true if available, false otherwise
     */
    public boolean isDoctorAvailable(UUID doctorId, UUID polyclinicId, LocalDate date, LocalTime time) {
        log.debug("Checking doctor availability: doctor={}, polyclinic={}, date={}, time={}",
            doctorId, polyclinicId, date, time);

        // Check if doctor exists and is active
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "ID", doctorId));

        if (!doctor.canPractice()) {
            log.warn("Doctor {} cannot practice (inactive or invalid license)", doctorId);
            return false;
        }

        // Get doctor schedule for the date
        String dayOfWeek = date.getDayOfWeek().name();
        Optional<DoctorSchedule> scheduleOpt = scheduleRepository.findScheduleForDate(
            doctorId, polyclinicId, dayOfWeek, date
        );

        if (scheduleOpt.isEmpty()) {
            log.debug("No schedule found for doctor {} on {}", doctorId, date);
            return false;
        }

        DoctorSchedule schedule = scheduleOpt.get();

        // Check if time is within schedule hours
        if (!schedule.isTimeWithinSchedule(time)) {
            log.debug("Time {} is outside schedule hours ({} - {})",
                time, schedule.getStartTime(), schedule.getEndTime());
            return false;
        }

        // Check if capacity is reached
        long registrationCount = registrationRepository.countByDoctorIdAndRegistrationDate(doctorId, date);
        if (registrationCount >= schedule.getMaxPatients()) {
            log.debug("Doctor {} has reached maximum capacity ({}) for {}",
                doctorId, schedule.getMaxPatients(), date);
            return false;
        }

        // Check if specific time slot is already booked
        LocalTime endTime = time.plusMinutes(schedule.getAppointmentDurationMinutes());
        boolean isBooked = registrationRepository.isTimeSlotBooked(doctorId, date, time, endTime);
        if (isBooked) {
            log.debug("Time slot {} - {} is already booked", time, endTime);
            return false;
        }

        return true;
    }

    /**
     * Get doctor availability with time slots for a specific date.
     *
     * @param doctorId doctor ID
     * @param polyclinicId polyclinic ID
     * @param date appointment date
     * @return doctor availability response with time slots
     */
    public DoctorAvailabilityResponse getDoctorAvailability(UUID doctorId, UUID polyclinicId, LocalDate date) {
        log.info("Getting doctor availability: doctor={}, polyclinic={}, date={}",
            doctorId, polyclinicId, date);

        // Validate doctor and polyclinic
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "ID", doctorId));

        Polyclinic polyclinic = polyclinicRepository.findById(polyclinicId)
            .orElseThrow(() -> new ResourceNotFoundException("Polyclinic", "ID", polyclinicId));

        // Get schedule for the date
        String dayOfWeek = date.getDayOfWeek().name();
        Optional<DoctorSchedule> scheduleOpt = scheduleRepository.findScheduleForDate(
            doctorId, polyclinicId, dayOfWeek, date
        );

        if (scheduleOpt.isEmpty()) {
            log.info("No schedule found for doctor {} at polyclinic {} on {}",
                doctorId, polyclinicId, date);

            return DoctorAvailabilityResponse.builder()
                .doctorId(doctorId)
                .doctorName(doctor.getFullName())
                .doctorTitle(doctor.getTitle())
                .specialization(doctor.getSpecialization())
                .polyclinicId(polyclinicId)
                .polyclinicName(polyclinic.getName())
                .date(date)
                .dayOfWeek(dayOfWeek)
                .isAvailable(false)
                .totalSlots(0)
                .availableSlots(0)
                .bookedSlots(0)
                .timeSlots(new ArrayList<>())
                .build();
        }

        DoctorSchedule schedule = scheduleOpt.get();

        // Generate time slots
        List<DoctorAvailabilityResponse.TimeSlotInfo> timeSlots = generateTimeSlots(
            schedule, doctorId, date
        );

        // Calculate availability
        long bookedSlots = timeSlots.stream()
            .filter(slot -> !slot.getIsAvailable())
            .count();

        int totalSlots = timeSlots.size();
        int availableSlots = totalSlots - (int) bookedSlots;

        return DoctorAvailabilityResponse.builder()
            .doctorId(doctorId)
            .doctorName(doctor.getFullName())
            .doctorTitle(doctor.getTitle())
            .specialization(doctor.getSpecialization())
            .polyclinicId(polyclinicId)
            .polyclinicName(polyclinic.getName())
            .scheduleId(schedule.getId())
            .date(date)
            .dayOfWeek(dayOfWeek)
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .isAvailable(availableSlots > 0)
            .totalSlots(totalSlots)
            .availableSlots(availableSlots)
            .bookedSlots((int) bookedSlots)
            .timeSlots(timeSlots)
            .build();
    }

    /**
     * Get all doctors available at a polyclinic on a specific date.
     *
     * @param polyclinicId polyclinic ID
     * @param date date to check
     * @return list of available doctors with availability info
     */
    public List<DoctorAvailabilityResponse> getAvailableDoctorsAtPolyclinic(UUID polyclinicId, LocalDate date) {
        log.info("Getting available doctors at polyclinic {} on {}", polyclinicId, date);

        String dayOfWeek = date.getDayOfWeek().name();

        // Get all schedules for the polyclinic on this day
        List<DoctorSchedule> schedules = scheduleRepository.findValidSchedulesForDate(dayOfWeek, date);

        return schedules.stream()
            .filter(schedule -> schedule.getPolyclinic().getId().equals(polyclinicId))
            .filter(schedule -> schedule.getDoctor().canPractice())
            .map(schedule -> getDoctorAvailability(
                schedule.getDoctor().getId(),
                polyclinicId,
                date
            ))
            .filter(availability -> Boolean.TRUE.equals(availability.getIsAvailable()))
            .collect(Collectors.toList());
    }

    /**
     * Validate if an appointment can be booked.
     *
     * @param doctorId doctor ID
     * @param polyclinicId polyclinic ID
     * @param appointmentDate appointment date
     * @param appointmentTime appointment time
     * @throws BusinessException if appointment cannot be booked
     */
    public void validateAppointment(UUID doctorId, UUID polyclinicId, LocalDate appointmentDate, LocalTime appointmentTime) {
        // Check if date is not in the past
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new BusinessException("Cannot book appointment in the past");
        }

        // Check if date is today (appointments should be for future dates)
        if (appointmentDate.equals(LocalDate.now())) {
            throw new BusinessException("Appointments must be booked at least 1 day in advance. Please use walk-in registration for same-day visits.");
        }

        // Check if doctor is available
        if (!isDoctorAvailable(doctorId, polyclinicId, appointmentDate, appointmentTime)) {
            throw new BusinessException("Doctor is not available at the requested date and time");
        }

        log.info("Appointment validation passed for doctor {} at {} on {}",
            doctorId, appointmentTime, appointmentDate);
    }

    /**
     * Get doctor's schedule for a specific day of week at a polyclinic.
     *
     * @param doctorId doctor ID
     * @param polyclinicId polyclinic ID
     * @param dayOfWeek day of week
     * @return optional doctor schedule
     */
    public Optional<DoctorSchedule> getDoctorSchedule(UUID doctorId, UUID polyclinicId, DayOfWeek dayOfWeek) {
        return scheduleRepository.findByDoctorIdAndPolyclinicIdAndDayOfWeekAndIsActiveTrue(
            doctorId,
            polyclinicId,
            dayOfWeek.name()
        );
    }

    /**
     * Get all schedules for a doctor.
     *
     * @param doctorId doctor ID
     * @return list of doctor schedules
     */
    public List<DoctorSchedule> getDoctorSchedules(UUID doctorId) {
        return scheduleRepository.findByDoctorIdAndIsActiveTrue(doctorId);
    }

    // ========== Private Helper Methods ==========

    /**
     * Generate time slots for a doctor schedule.
     *
     * @param schedule doctor schedule
     * @param doctorId doctor ID
     * @param date appointment date
     * @return list of time slot info
     */
    private List<DoctorAvailabilityResponse.TimeSlotInfo> generateTimeSlots(
        DoctorSchedule schedule,
        UUID doctorId,
        LocalDate date
    ) {
        List<DoctorAvailabilityResponse.TimeSlotInfo> slots = new ArrayList<>();

        LocalTime currentTime = schedule.getStartTime();
        int duration = schedule.getAppointmentDurationMinutes();

        // Get all appointments for this doctor on this date
        List<OutpatientRegistration> appointments = registrationRepository
            .findAppointmentsByDoctorAndDate(doctorId, date);

        while (currentTime.isBefore(schedule.getEndTime())) {
            LocalTime slotEndTime = currentTime.plusMinutes(duration);

            // Don't create slot if it goes beyond end time
            if (slotEndTime.isAfter(schedule.getEndTime())) {
                break;
            }

            // Check if this slot is booked
            final LocalTime finalCurrentTime = currentTime;
            boolean isBooked = appointments.stream()
                .anyMatch(apt ->
                    !apt.getAppointmentTime().isAfter(finalCurrentTime) &&
                    !apt.getAppointmentEndTime().isBefore(slotEndTime)
                );

            DoctorAvailabilityResponse.TimeSlotInfo slotInfo = DoctorAvailabilityResponse.TimeSlotInfo.builder()
                .startTime(currentTime)
                .endTime(slotEndTime)
                .isAvailable(!isBooked)
                .slotsRemaining(isBooked ? 0 : 1)
                .status(isBooked ? "Booked" : "Available")
                .build();

            slots.add(slotInfo);

            currentTime = slotEndTime;
        }

        return slots;
    }
}