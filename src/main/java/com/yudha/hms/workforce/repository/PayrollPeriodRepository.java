package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.PayrollPeriodStatus;
import com.yudha.hms.workforce.entity.PayrollPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollPeriodRepository extends JpaRepository<PayrollPeriod, UUID> {

    Optional<PayrollPeriod> findByPeriodCode(String periodCode);

    List<PayrollPeriod> findByPeriodYearAndPeriodMonthOrderByStartDateDesc(Integer periodYear, Integer periodMonth);

    List<PayrollPeriod> findByStatusOrderByStartDateDesc(PayrollPeriodStatus status);

    List<PayrollPeriod> findByPeriodYearOrderByPeriodMonthDesc(Integer periodYear);

    List<PayrollPeriod> findByIsThrPeriodTrue();

    @Query("SELECT p FROM PayrollPeriod p WHERE p.status = :status AND p.endDate >= :currentDate ORDER BY p.startDate DESC")
    List<PayrollPeriod> findActivePeriodsFromDate(@Param("status") PayrollPeriodStatus status, @Param("currentDate") LocalDate currentDate);

    @Query("SELECT p FROM PayrollPeriod p WHERE p.periodYear = :year AND p.periodMonth = :month")
    Optional<PayrollPeriod> findByYearAndMonth(@Param("year") Integer year, @Param("month") Integer month);

    @Query("SELECT p FROM PayrollPeriod p WHERE :date BETWEEN p.startDate AND p.endDate")
    Optional<PayrollPeriod> findByDate(@Param("date") LocalDate date);
}
