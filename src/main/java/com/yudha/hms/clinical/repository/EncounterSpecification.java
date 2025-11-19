package com.yudha.hms.clinical.repository;

import com.yudha.hms.clinical.dto.EncounterSearchCriteria;
import com.yudha.hms.clinical.entity.Encounter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Encounter Specification.
 * Dynamic query builder for encounter search.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-19
 */
public class EncounterSpecification {

    /**
     * Build specification from search criteria.
     */
    public static Specification<Encounter> fromCriteria(EncounterSearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Patient ID
            if (criteria.getPatientId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("patientId"), criteria.getPatientId()));
            }

            // Encounter Number (partial match)
            if (criteria.getEncounterNumber() != null && !criteria.getEncounterNumber().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("encounterNumber")),
                    "%" + criteria.getEncounterNumber().toLowerCase() + "%"
                ));
            }

            // Encounter Type
            if (criteria.getEncounterType() != null) {
                predicates.add(criteriaBuilder.equal(root.get("encounterType"), criteria.getEncounterType()));
            }

            // Encounter Class
            if (criteria.getEncounterClass() != null) {
                predicates.add(criteriaBuilder.equal(root.get("encounterClass"), criteria.getEncounterClass()));
            }

            // Status
            if (criteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), criteria.getStatus()));
            }

            // Department (partial match)
            if (criteria.getDepartment() != null && !criteria.getDepartment().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("currentDepartment")),
                    "%" + criteria.getDepartment().toLowerCase() + "%"
                ));
            }

            // Attending Doctor
            if (criteria.getAttendingDoctorId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("attendingDoctorId"), criteria.getAttendingDoctorId()));
            }

            // BPJS
            if (criteria.getIsBpjs() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isBpjs"), criteria.getIsBpjs()));
            }

            // SEP Number
            if (criteria.getSepNumber() != null && !criteria.getSepNumber().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("sepNumber"), criteria.getSepNumber()));
            }

            // Encounter Start Date Range
            if (criteria.getEncounterStartFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("encounterStart"),
                    criteria.getEncounterStartFrom()
                ));
            }
            if (criteria.getEncounterStartTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("encounterStart"),
                    criteria.getEncounterStartTo()
                ));
            }

            // Encounter End Date Range
            if (criteria.getEncounterEndFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                    root.get("encounterEnd"),
                    criteria.getEncounterEndFrom()
                ));
            }
            if (criteria.getEncounterEndTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                    root.get("encounterEnd"),
                    criteria.getEncounterEndTo()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
