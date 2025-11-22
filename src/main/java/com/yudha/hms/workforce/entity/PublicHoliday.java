package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.HolidayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "public_holiday", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicHoliday extends SoftDeletableEntity {

    @Column(name = "holiday_date", nullable = false, unique = true)
    private LocalDate holidayDate;

    @Column(name = "holiday_name", length = 200, nullable = false)
    private String holidayName;

    @Column(name = "holiday_name_id", length = 200, nullable = false)
    private String holidayNameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_type", length = 30, nullable = false)
    private HolidayType holidayType;

    @Column(name = "religion", length = 30)
    private String religion;

    @Column(name = "is_national")
    private Boolean isNational = true;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "is_paid_leave")
    private Boolean isPaidLeave = true;

    @Column(name = "requires_compensation")
    private Boolean requiresCompensation = false;

    @Column(name = "compensation_multiplier", precision = 4, scale = 2)
    private BigDecimal compensationMultiplier = BigDecimal.valueOf(2.0);

    @Column(name = "is_substitute_holiday")
    private Boolean isSubstituteHoliday = false;

    @Column(name = "original_date")
    private LocalDate originalDate;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
