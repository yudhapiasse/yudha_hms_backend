package com.yudha.hms.workforce.entity;

import com.yudha.hms.shared.entity.SoftDeletableEntity;
import com.yudha.hms.workforce.constant.EmploymentStatus;
import com.yudha.hms.workforce.constant.EmploymentType;
import com.yudha.hms.workforce.constant.Gender;
import com.yudha.hms.workforce.constant.MaritalStatus;
import com.yudha.hms.workforce.constant.Religion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employee", schema = "workforce_schema")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends SoftDeletableEntity {

    @Column(name = "employee_number", length = 50, nullable = false, unique = true)
    private String employeeNumber;

    @Column(name = "nik", length = 16, nullable = false, unique = true)
    private String nik;

    @Column(name = "npwp", length = 20)
    private String npwp;

    @Column(name = "bpjs_kesehatan_number", length = 20)
    private String bpjsKesehatanNumber;

    @Column(name = "bpjs_ketenagakerjaan_number", length = 20)
    private String bpjsKetenagakerjaanNumber;

    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;

    @Column(name = "place_of_birth", length = 100)
    private String placeOfBirth;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "religion", length = 20)
    private Religion religion;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "province", length = 100)
    private String province;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "position_id")
    private UUID positionId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", length = 30, nullable = false)
    private EmploymentStatus employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 20)
    private EmploymentType employmentType;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "permanent_date")
    private LocalDate permanentDate;

    @Column(name = "resignation_date")
    private LocalDate resignationDate;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    @Column(name = "bank_account_name", length = 200)
    private String bankAccountName;

    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "emergency_contact_name", length = 200)
    private String emergencyContactName;

    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_address", columnDefinition = "TEXT")
    private String emergencyContactAddress;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
