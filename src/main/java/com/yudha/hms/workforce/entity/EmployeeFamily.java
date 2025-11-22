package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.FamilyRelationship;
import com.yudha.hms.workforce.constant.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee_family", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeFamily extends SoftDeletableEntity {

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", length = 20, nullable = false)
    private FamilyRelationship relationship;

    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;

    @Column(name = "nik", length = 16)
    private String nik;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "place_of_birth", length = 100)
    private String placeOfBirth;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "is_dependent", nullable = false)
    private Boolean isDependent = false;

    @Column(name = "is_emergency_contact", nullable = false)
    private Boolean isEmergencyContact = false;

    @Column(name = "covered_by_health_insurance", nullable = false)
    private Boolean coveredByHealthInsurance = false;

    @Column(name = "bpjs_number", length = 20)
    private String bpjsNumber;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
