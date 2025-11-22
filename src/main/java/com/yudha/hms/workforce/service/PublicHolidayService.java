package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.HolidayType;
import com.yudha.hms.workforce.entity.PublicHoliday;
import com.yudha.hms.workforce.repository.PublicHolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service untuk mengelola hari libur nasional (Public Holiday)
 * Manages Indonesian public holidays configuration
 */
@Service
@RequiredArgsConstructor
public class PublicHolidayService {

    private final PublicHolidayRepository publicHolidayRepository;

    @Transactional(readOnly = true)
    public PublicHoliday getPublicHolidayById(UUID id) {
        return publicHolidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hari libur tidak ditemukan dengan id: " + id));
    }

    @Transactional(readOnly = true)
    public PublicHoliday getHolidayByDate(LocalDate date) {
        return publicHolidayRepository.findByHolidayDate(date).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PublicHoliday> getHolidaysByYear(Integer year) {
        return publicHolidayRepository.findByYearAndActiveTrue(year);
    }

    @Transactional(readOnly = true)
    public List<PublicHoliday> getHolidaysInDateRange(LocalDate startDate, LocalDate endDate) {
        return publicHolidayRepository.findByHolidayDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<PublicHoliday> getHolidaysByYearAndType(Integer year, HolidayType type) {
        return publicHolidayRepository.findByYearAndHolidayType(year, type);
    }

    @Transactional(readOnly = true)
    public boolean isHoliday(LocalDate date) {
        return publicHolidayRepository.existsByHolidayDate(date);
    }

    @Transactional(readOnly = true)
    public Long countHolidaysBetween(LocalDate startDate, LocalDate endDate) {
        return publicHolidayRepository.countHolidaysBetween(startDate, endDate);
    }

    @Transactional
    public PublicHoliday createPublicHoliday(PublicHoliday publicHoliday) {
        if (publicHolidayRepository.existsByHolidayDate(publicHoliday.getHolidayDate())) {
            throw new RuntimeException("Hari libur sudah ada untuk tanggal ini");
        }
        return publicHolidayRepository.save(publicHoliday);
    }

    @Transactional
    public PublicHoliday updatePublicHoliday(UUID id, PublicHoliday publicHoliday) {
        PublicHoliday existing = getPublicHolidayById(id);
        existing.setHolidayName(publicHoliday.getHolidayName());
        existing.setHolidayNameId(publicHoliday.getHolidayNameId());
        existing.setHolidayType(publicHoliday.getHolidayType());
        existing.setReligion(publicHoliday.getReligion());
        existing.setIsPaidLeave(publicHoliday.getIsPaidLeave());
        existing.setRequiresCompensation(publicHoliday.getRequiresCompensation());
        existing.setCompensationMultiplier(publicHoliday.getCompensationMultiplier());
        existing.setActive(publicHoliday.getActive());
        existing.setNotes(publicHoliday.getNotes());
        return publicHolidayRepository.save(existing);
    }

    @Transactional
    public void deletePublicHoliday(UUID id) {
        PublicHoliday publicHoliday = getPublicHolidayById(id);
        publicHoliday.setActive(false);
        publicHolidayRepository.save(publicHoliday);
    }
}
