package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.HolidayType;
import com.yudha.hms.workforce.entity.PublicHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PublicHolidayRepository extends JpaRepository<PublicHoliday, UUID> {

    Optional<PublicHoliday> findByHolidayDate(LocalDate holidayDate);

    List<PublicHoliday> findByYear(Integer year);

    List<PublicHoliday> findByYearAndActiveTrue(Integer year);

    List<PublicHoliday> findByHolidayDateBetween(LocalDate startDate, LocalDate endDate);

    List<PublicHoliday> findByYearAndHolidayType(Integer year, HolidayType holidayType);

    List<PublicHoliday> findByIsNationalTrue();

    @Query("SELECT ph FROM PublicHoliday ph WHERE ph.holidayDate BETWEEN :startDate AND :endDate AND ph.active = true AND (ph.isNational = true OR (ph.province = :province OR ph.city = :city))")
    List<PublicHoliday> findApplicableHolidays(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("province") String province, @Param("city") String city);

    @Query("SELECT COUNT(ph) FROM PublicHoliday ph WHERE ph.holidayDate BETWEEN :startDate AND :endDate AND ph.active = true")
    Long countHolidaysBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsByHolidayDate(LocalDate holidayDate);
}
