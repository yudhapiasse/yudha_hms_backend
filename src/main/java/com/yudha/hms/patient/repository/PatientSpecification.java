package com.yudha.hms.patient.repository;

import com.yudha.hms.patient.dto.PatientSearchCriteria;
import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.patient.entity.PatientAddress;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Patient Specification Builder.
 *
 * Builds dynamic JPA Criteria queries based on search criteria.
 * Supports advanced filtering, full-text search, and multiple search conditions.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
public class PatientSpecification {

    /**
     * Build specification from search criteria
     *
     * @param criteria search criteria
     * @return JPA Specification
     */
    public static Specification<Patient> buildSpecification(PatientSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude soft-deleted patients
            predicates.add(cb.isNull(root.get("deletedAt")));

            // Quick search (searches across multiple fields)
            if (criteria.hasQuickSearch()) {
                predicates.add(quickSearchPredicate(criteria.getSearchTerm(), root, cb));
            }

            // Specific field searches
            addSpecificSearchPredicates(criteria, root, cb, predicates);

            // Advanced filters
            addAdvancedFilterPredicates(criteria, root, cb, predicates, query);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Quick search across multiple fields
     */
    private static Predicate quickSearchPredicate(String searchTerm, Root<Patient> root, CriteriaBuilder cb) {
        String searchPattern = "%" + searchTerm.toLowerCase() + "%";

        return cb.or(
            // Search by MRN (exact or partial)
            cb.like(cb.lower(root.get("mrn")), searchPattern),

            // Search by NIK (exact or partial)
            cb.like(cb.lower(root.get("nik")), searchPattern),

            // Search by BPJS number (exact or partial)
            cb.like(cb.lower(root.get("bpjsNumber")), searchPattern),

            // Search by full name (partial match)
            cb.like(cb.lower(root.get("fullName")), searchPattern),

            // Search by phone (partial match)
            cb.or(
                cb.like(cb.lower(root.get("phonePrimary")), searchPattern),
                cb.like(cb.lower(root.get("phoneSecondary")), searchPattern)
            )
        );
    }

    /**
     * Add specific field search predicates
     */
    private static void addSpecificSearchPredicates(PatientSearchCriteria criteria, Root<Patient> root,
                                                     CriteriaBuilder cb, List<Predicate> predicates) {
        // Search by MRN (exact match)
        if (criteria.getMrn() != null && !criteria.getMrn().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("mrn")), criteria.getMrn().toLowerCase()));
        }

        // Search by NIK (exact match)
        if (criteria.getNik() != null && !criteria.getNik().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("nik")), criteria.getNik().toLowerCase()));
        }

        // Search by BPJS number (exact match)
        if (criteria.getBpjsNumber() != null && !criteria.getBpjsNumber().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("bpjsNumber")), criteria.getBpjsNumber().toLowerCase()));
        }

        // Search by name (partial match with LIKE)
        if (criteria.getName() != null && !criteria.getName().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("fullName")),
                "%" + criteria.getName().toLowerCase() + "%"));
        }

        // Search by phone (partial match)
        if (criteria.getPhone() != null && !criteria.getPhone().trim().isEmpty()) {
            String phonePattern = "%" + criteria.getPhone() + "%";
            predicates.add(cb.or(
                cb.like(root.get("phonePrimary"), phonePattern),
                cb.like(root.get("phoneSecondary"), phonePattern)
            ));
        }

        // Search by email (partial match)
        if (criteria.getEmail() != null && !criteria.getEmail().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("email")),
                "%" + criteria.getEmail().toLowerCase() + "%"));
        }
    }

    /**
     * Add advanced filter predicates
     */
    private static void addAdvancedFilterPredicates(PatientSearchCriteria criteria, Root<Patient> root,
                                                     CriteriaBuilder cb, List<Predicate> predicates,
                                                     CriteriaQuery<?> query) {
        // Filter by gender
        if (criteria.getGender() != null && !criteria.getGender().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("gender"), criteria.getGender().toUpperCase()));
        }

        // Filter by birth date range
        if (criteria.getBirthDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), criteria.getBirthDateFrom()));
        }
        if (criteria.getBirthDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), criteria.getBirthDateTo()));
        }

        // Filter by age range (calculate from birth date)
        if (criteria.getAgeFrom() != null || criteria.getAgeTo() != null) {
            LocalDate today = LocalDate.now();

            if (criteria.getAgeFrom() != null) {
                LocalDate maxBirthDate = today.minusYears(criteria.getAgeFrom());
                predicates.add(cb.lessThanOrEqualTo(root.get("birthDate"), maxBirthDate));
            }

            if (criteria.getAgeTo() != null) {
                LocalDate minBirthDate = today.minusYears(criteria.getAgeTo() + 1).plusDays(1);
                predicates.add(cb.greaterThanOrEqualTo(root.get("birthDate"), minBirthDate));
            }
        }

        // Filter by registration date range
        if (criteria.getRegistrationDateFrom() != null) {
            LocalDateTime fromDateTime = criteria.getRegistrationDateFrom().atStartOfDay();
            predicates.add(cb.greaterThanOrEqualTo(root.get("registrationDate"), fromDateTime));
        }
        if (criteria.getRegistrationDateTo() != null) {
            LocalDateTime toDateTime = criteria.getRegistrationDateTo().atTime(23, 59, 59);
            predicates.add(cb.lessThanOrEqualTo(root.get("registrationDate"), toDateTime));
        }

        // Filter by religion
        if (criteria.getReligionId() != null && !criteria.getReligionId().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("religion").get("id"), UUID.fromString(criteria.getReligionId())));
        }

        // Filter by blood type
        if (criteria.getBloodTypeId() != null && !criteria.getBloodTypeId().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("bloodType").get("id"), UUID.fromString(criteria.getBloodTypeId())));
        }

        // Filter by marital status
        if (criteria.getMaritalStatusId() != null && !criteria.getMaritalStatusId().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("maritalStatus").get("id"), UUID.fromString(criteria.getMaritalStatusId())));
        }

        // Filter by address (province, city, district)
        if (criteria.getProvinceId() != null || criteria.getCityId() != null || criteria.getDistrictId() != null) {
            // Join with patient_address table
            Join<Patient, PatientAddress> addressJoin = root.join("addresses", JoinType.LEFT);

            if (criteria.getProvinceId() != null && !criteria.getProvinceId().trim().isEmpty()) {
                predicates.add(cb.equal(addressJoin.get("province").get("id"), UUID.fromString(criteria.getProvinceId())));
            }

            if (criteria.getCityId() != null && !criteria.getCityId().trim().isEmpty()) {
                predicates.add(cb.equal(addressJoin.get("city").get("id"), UUID.fromString(criteria.getCityId())));
            }

            if (criteria.getDistrictId() != null && !criteria.getDistrictId().trim().isEmpty()) {
                predicates.add(cb.equal(addressJoin.get("district").get("id"), UUID.fromString(criteria.getDistrictId())));
            }

            // Avoid duplicate results from join
            query.distinct(true);
        }

        // Filter by active status
        if (criteria.getIsActive() != null) {
            predicates.add(cb.equal(root.get("isActive"), criteria.getIsActive()));
        }

        // Filter by deceased status
        if (criteria.getIsDeceased() != null) {
            predicates.add(cb.equal(root.get("isDeceased"), criteria.getIsDeceased()));
        }

        // Filter by VIP status
        if (criteria.getIsVip() != null) {
            predicates.add(cb.equal(root.get("isVip"), criteria.getIsVip()));
        }

        // Filter by BPJS active status
        if (criteria.getBpjsActive() != null) {
            predicates.add(cb.equal(root.get("bpjsActive"), criteria.getBpjsActive()));
        }

        // Filter by BPJS class
        if (criteria.getBpjsClass() != null && !criteria.getBpjsClass().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("bpjsClass"), criteria.getBpjsClass()));
        }
    }

    /**
     * Full-text search specification using PostgreSQL
     * This can be used for more advanced search capabilities
     */
    public static Specification<Patient> fullTextSearch(String searchTerm) {
        return (root, query, cb) -> {
            // PostgreSQL full-text search using to_tsvector and to_tsquery
            // This requires a native query for best performance
            return cb.like(cb.lower(root.get("fullName")),
                "%" + searchTerm.toLowerCase() + "%");
        };
    }
}
