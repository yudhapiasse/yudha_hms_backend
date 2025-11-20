package com.yudha.hms.clinical.service;

import com.yudha.hms.clinical.dto.*;
import com.yudha.hms.clinical.entity.*;
import com.yudha.hms.clinical.repository.EncounterDiagnosisRepository;
import com.yudha.hms.clinical.repository.EncounterRepository;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Visit History Service.
 *
 * Provides patient encounter history with filtering, timeline visualization,
 * readmission detection, and chronic disease progression tracking.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-20
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class VisitHistoryService {

    private final EncounterRepository encounterRepository;
    private final EncounterDiagnosisRepository encounterDiagnosisRepository;

    // ========== Visit History Operations ==========

    public VisitHistoryResponse getPatientVisitHistory(UUID patientId, VisitHistoryFilterRequest filter) {
        log.info("Fetching visit history for patient: {}", patientId);

        // Build specification for filtering
        Specification<Encounter> spec = buildEncounterSpecification(patientId, filter);

        // Configure pagination and sorting
        Pageable pageable = buildPageable(filter);

        // Execute query
        Page<Encounter> encounterPage = encounterRepository.findAll(spec, pageable);

        // Get all encounters for statistics (without pagination)
        List<Encounter> allEncounters = encounterRepository.findAll(buildEncounterSpecification(patientId, null));

        // Detect readmissions
        Map<UUID, ReadmissionInfo> readmissionMap = detectReadmissions(allEncounters);

        // Convert to response
        List<VisitHistoryItemResponse> visits = encounterPage.getContent().stream()
            .map(encounter -> mapToVisitHistoryItem(encounter, readmissionMap))
            .collect(Collectors.toList());

        // Calculate statistics
        return VisitHistoryResponse.builder()
            .visits(visits)
            .totalVisits((long) allEncounters.size())
            .outpatientVisits(countByType(allEncounters, EncounterType.OUTPATIENT))
            .inpatientVisits(countByType(allEncounters, EncounterType.INPATIENT))
            .emergencyVisits(countByType(allEncounters, EncounterType.EMERGENCY))
            .readmissions((long) readmissionMap.size())
            .bpjsVisits(allEncounters.stream().filter(e -> Boolean.TRUE.equals(e.getIsBpjs())).count())
            .currentPage(encounterPage.getNumber())
            .totalPages(encounterPage.getTotalPages())
            .pageSize(encounterPage.getSize())
            .hasNext(encounterPage.hasNext())
            .hasPrevious(encounterPage.hasPrevious())
            .build();
    }

    // ========== Timeline Operations ==========

    public TimelineResponse getPatientTimeline(UUID patientId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Generating timeline for patient: {}", patientId);

        // Fetch all encounters in date range
        List<Encounter> encounters = encounterRepository.findByPatientIdOrderByEncounterStartDesc(patientId)
            .stream()
            .filter(e -> {
                if (startDate != null && e.getEncounterStart().isBefore(startDate)) return false;
                if (endDate != null && e.getEncounterStart().isAfter(endDate)) return false;
                return true;
            })
            .sorted(Comparator.comparing(Encounter::getEncounterStart))
            .collect(Collectors.toList());

        if (encounters.isEmpty()) {
            throw new ResourceNotFoundException(
                "No encounters found for patient: " + patientId
            );
        }

        // Detect readmissions
        Map<UUID, ReadmissionInfo> readmissionMap = detectReadmissions(encounters);
        List<ReadmissionEventResponse> readmissionEvents = buildReadmissionEvents(encounters, readmissionMap);

        // Build timeline events
        List<TimelineEventResponse> events = new ArrayList<>();
        for (Encounter encounter : encounters) {
            events.addAll(buildTimelineEvents(encounter, readmissionMap));
        }

        // Analyze chronic diseases
        List<ChronicDiseaseProgressionResponse> chronicDiseaseProgression = analyzeChronicDiseases(encounters);

        // Analyze treatment patterns
        Map<String, Integer> departmentCounts = encounters.stream()
            .filter(e -> e.getCurrentDepartment() != null)
            .collect(Collectors.groupingBy(
                Encounter::getCurrentDepartment,
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));

        Map<String, List<LocalDateTime>> recurringDiagnoses = analyzeRecurringDiagnoses(encounters);

        return TimelineResponse.builder()
            .patientId(patientId)
            .events(events)
            .firstEncounter(encounters.get(0).getEncounterStart())
            .lastEncounter(encounters.get(encounters.size() - 1).getEncounterStart())
            .totalEncounters(encounters.size())
            .totalReadmissions(readmissionMap.size())
            .readmissionsWithin30Days((int) readmissionEvents.stream()
                .filter(r -> r.getDaysBetween() <= 30)
                .count())
            .totalChronicConditions(chronicDiseaseProgression.size())
            .chronicDiseaseProgression(chronicDiseaseProgression)
            .departmentVisitCounts(departmentCounts)
            .recurringDiagnoses(recurringDiagnoses)
            .readmissionEvents(readmissionEvents)
            .build();
    }

    // ========== Helper Methods ==========

    private Specification<Encounter> buildEncounterSpecification(UUID patientId, VisitHistoryFilterRequest filter) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Patient ID filter (required)
            predicates.add(cb.equal(root.get("patientId"), patientId));

            if (filter != null) {
                // Date range filter
                if (filter.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("encounterStart"), filter.getStartDate()));
                }
                if (filter.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("encounterStart"), filter.getEndDate()));
                }

                // Encounter type filter
                if (filter.getEncounterType() != null && !filter.getEncounterType().isEmpty()) {
                    predicates.add(cb.equal(root.get("encounterType"), EncounterType.valueOf(filter.getEncounterType())));
                }

                // Department filter
                if (filter.getDepartment() != null && !filter.getDepartment().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("currentDepartment")),
                        "%" + filter.getDepartment().toLowerCase() + "%"));
                }

                // Doctor filter
                if (filter.getDoctorId() != null) {
                    predicates.add(cb.equal(root.get("attendingDoctorId"), filter.getDoctorId()));
                }
                if (filter.getDoctorName() != null && !filter.getDoctorName().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("attendingDoctorName")),
                        "%" + filter.getDoctorName().toLowerCase() + "%"));
                }

                // Status filter
                if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                    predicates.add(cb.equal(root.get("status"), EncounterStatus.valueOf(filter.getStatus())));
                }

                // BPJS filter
                if (Boolean.TRUE.equals(filter.getIsBpjsOnly())) {
                    predicates.add(cb.isTrue(root.get("isBpjs")));
                }
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private Pageable buildPageable(VisitHistoryFilterRequest filter) {
        int page = filter != null && filter.getPage() != null ? filter.getPage() : 0;
        int size = filter != null && filter.getSize() != null ? filter.getSize() : 20;
        String sortBy = filter != null && filter.getSortBy() != null ? filter.getSortBy() : "encounterStart";
        String sortDirection = filter != null && filter.getSortDirection() != null ? filter.getSortDirection() : "DESC";

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private Map<UUID, ReadmissionInfo> detectReadmissions(List<Encounter> encounters) {
        Map<UUID, ReadmissionInfo> readmissionMap = new HashMap<>();

        // Sort by encounter start date
        List<Encounter> sortedEncounters = encounters.stream()
            .filter(e -> e.getEncounterType() == EncounterType.INPATIENT)
            .sorted(Comparator.comparing(Encounter::getEncounterStart))
            .collect(Collectors.toList());

        for (int i = 1; i < sortedEncounters.size(); i++) {
            Encounter current = sortedEncounters.get(i);
            Encounter previous = sortedEncounters.get(i - 1);

            if (previous.getDischargeDate() != null) {
                long daysBetween = ChronoUnit.DAYS.between(
                    previous.getDischargeDate().toLocalDate(),
                    current.getEncounterStart().toLocalDate()
                );

                if (daysBetween <= 30) {
                    ReadmissionInfo info = new ReadmissionInfo();
                    info.previousEncounter = previous;
                    info.daysSincePreviousDischarge = (int) daysBetween;
                    info.isReadmission = true;
                    readmissionMap.put(current.getId(), info);
                }
            }
        }

        return readmissionMap;
    }

    private VisitHistoryItemResponse mapToVisitHistoryItem(Encounter encounter, Map<UUID, ReadmissionInfo> readmissionMap) {
        ReadmissionInfo readmissionInfo = readmissionMap.get(encounter.getId());

        // Get primary diagnosis
        List<EncounterDiagnosis> diagnoses = encounterDiagnosisRepository.findByEncounter(encounter);
        EncounterDiagnosis primaryDiagnosis = diagnoses.stream()
            .filter(d -> d.getDiagnosisType() == DiagnosisType.PRIMARY)
            .findFirst()
            .orElse(null);

        return VisitHistoryItemResponse.builder()
            .id(encounter.getId())
            .encounterNumber(encounter.getEncounterNumber())
            .encounterType(encounter.getEncounterType().name())
            .encounterTypeDisplay(encounter.getEncounterType().getDisplayName())
            .encounterClass(encounter.getEncounterClass().name())
            .encounterClassDisplay(encounter.getEncounterClass().getDisplayName())
            .encounterStart(encounter.getEncounterStart())
            .encounterEnd(encounter.getEncounterEnd())
            .lengthOfStayDays(encounter.getLengthOfStayDays())
            .lengthOfStayHours(encounter.getLengthOfStayHours())
            .status(encounter.getStatus().name())
            .statusDisplay(encounter.getStatus().getDisplayName())
            .currentDepartment(encounter.getCurrentDepartment())
            .currentLocation(encounter.getCurrentLocation())
            .admittingDepartment(encounter.getAdmittingDepartment())
            .attendingDoctorId(encounter.getAttendingDoctorId())
            .attendingDoctorName(encounter.getAttendingDoctorName())
            .primaryNurseName(encounter.getPrimaryNurseName())
            .chiefComplaint(encounter.getChiefComplaint())
            .reasonForVisit(encounter.getReasonForVisit())
            .primaryDiagnosisCode(primaryDiagnosis != null ? primaryDiagnosis.getDiagnosisCode() : null)
            .primaryDiagnosisText(primaryDiagnosis != null ? primaryDiagnosis.getDiagnosisText() : null)
            .additionalDiagnosesCount(diagnoses.size() - (primaryDiagnosis != null ? 1 : 0))
            .dischargeDisposition(encounter.getDischargeDisposition())
            .dischargeDate(encounter.getDischargeDate())
            .priority(encounter.getPriority() != null ? encounter.getPriority().name() : null)
            .priorityDisplay(encounter.getPriority() != null ? encounter.getPriority().getDisplayName() : null)
            .insuranceType(encounter.getInsuranceType() != null ? encounter.getInsuranceType().name() : null)
            .isBpjs(encounter.getIsBpjs())
            .isReadmission(readmissionInfo != null && readmissionInfo.isReadmission)
            .daysSincePreviousDischarge(readmissionInfo != null ? readmissionInfo.daysSincePreviousDischarge : null)
            .isActive(encounter.getStatus().isActive())
            .isCompleted(encounter.getStatus().isCompleted())
            .createdAt(encounter.getCreatedAt())
            .build();
    }

    private List<TimelineEventResponse> buildTimelineEvents(Encounter encounter, Map<UUID, ReadmissionInfo> readmissionMap) {
        List<TimelineEventResponse> events = new ArrayList<>();
        ReadmissionInfo readmissionInfo = readmissionMap.get(encounter.getId());

        // Get diagnoses
        List<EncounterDiagnosis> diagnoses = encounterDiagnosisRepository.findByEncounter(encounter);
        EncounterDiagnosis primaryDiagnosis = diagnoses.stream()
            .filter(d -> d.getDiagnosisType() == DiagnosisType.PRIMARY)
            .findFirst()
            .orElse(null);

        // Admission event (for inpatient/emergency)
        if (encounter.getEncounterType() == EncounterType.INPATIENT ||
            encounter.getEncounterType() == EncounterType.EMERGENCY) {
            events.add(TimelineEventResponse.builder()
                .encounterId(encounter.getId())
                .encounterNumber(encounter.getEncounterNumber())
                .eventType("ADMISSION")
                .encounterType(encounter.getEncounterType().name())
                .eventDate(encounter.getEncounterStart())
                .admissionDate(encounter.getEncounterStart())
                .department(encounter.getCurrentDepartment())
                .location(encounter.getCurrentLocation())
                .attendingDoctor(encounter.getAttendingDoctorName())
                .primaryDiagnosis(primaryDiagnosis != null ? primaryDiagnosis.getDiagnosisText() : null)
                .diagnoses(diagnoses.stream().map(EncounterDiagnosis::getDiagnosisText).collect(Collectors.toList()))
                .isReadmission(readmissionInfo != null && readmissionInfo.isReadmission)
                .daysSincePreviousDischarge(readmissionInfo != null ? readmissionInfo.daysSincePreviousDischarge : null)
                .color(determineEventColor(encounter, readmissionInfo))
                .icon("admission")
                .severity(determineSeverity(encounter, readmissionInfo))
                .build());
        }

        // Discharge event
        if (encounter.getDischargeDate() != null) {
            events.add(TimelineEventResponse.builder()
                .encounterId(encounter.getId())
                .encounterNumber(encounter.getEncounterNumber())
                .eventType("DISCHARGE")
                .encounterType(encounter.getEncounterType().name())
                .eventDate(encounter.getDischargeDate())
                .dischargeDate(encounter.getDischargeDate())
                .lengthOfStayDays(encounter.getLengthOfStayDays())
                .department(encounter.getCurrentDepartment())
                .attendingDoctor(encounter.getAttendingDoctorName())
                .primaryDiagnosis(primaryDiagnosis != null ? primaryDiagnosis.getDiagnosisText() : null)
                .color("#4CAF50")
                .icon("discharge")
                .severity("LOW")
                .build());
        }

        return events;
    }

    private List<ChronicDiseaseProgressionResponse> analyzeChronicDiseases(List<Encounter> encounters) {
        // Group diagnoses by code to find recurring/chronic conditions
        Map<String, List<LocalDateTime>> diagnosisByCode = new HashMap<>();
        Map<String, String> diagnosisNames = new HashMap<>();

        for (Encounter encounter : encounters) {
            List<EncounterDiagnosis> diagnoses = encounterDiagnosisRepository.findByEncounter(encounter);
            for (EncounterDiagnosis diagnosis : diagnoses) {
                diagnosisByCode.computeIfAbsent(diagnosis.getDiagnosisCode(), k -> new ArrayList<>())
                    .add(encounter.getEncounterStart());
                diagnosisNames.putIfAbsent(diagnosis.getDiagnosisCode(), diagnosis.getDiagnosisText());
            }
        }

        // Consider a diagnosis chronic if it appears in 3+ encounters
        return diagnosisByCode.entrySet().stream()
            .filter(entry -> entry.getValue().size() >= 3)
            .map(entry -> {
                List<LocalDateTime> visitDates = entry.getValue().stream()
                    .sorted()
                    .collect(Collectors.toList());

                return ChronicDiseaseProgressionResponse.builder()
                    .diagnosisCode(entry.getKey())
                    .diagnosisName(diagnosisNames.get(entry.getKey()))
                    .diseaseCategory(extractDiseaseCategory(entry.getKey()))
                    .firstDiagnosed(visitDates.get(0))
                    .lastEncounter(visitDates.get(visitDates.size() - 1))
                    .totalRelatedVisits(visitDates.size())
                    .visitDates(visitDates)
                    .progressionTrend("STABLE") // Would require more analysis
                    .severity("MODERATE") // Would require clinical assessment
                    .build();
            })
            .collect(Collectors.toList());
    }

    private Map<String, List<LocalDateTime>> analyzeRecurringDiagnoses(List<Encounter> encounters) {
        Map<String, List<LocalDateTime>> recurring = new HashMap<>();

        for (Encounter encounter : encounters) {
            List<EncounterDiagnosis> diagnoses = encounterDiagnosisRepository.findByEncounter(encounter);
            for (EncounterDiagnosis diagnosis : diagnoses) {
                recurring.computeIfAbsent(diagnosis.getDiagnosisCode(), k -> new ArrayList<>())
                    .add(encounter.getEncounterStart());
            }
        }

        // Return only diagnoses that appear multiple times
        return recurring.entrySet().stream()
            .filter(entry -> entry.getValue().size() > 1)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<ReadmissionEventResponse> buildReadmissionEvents(
        List<Encounter> encounters,
        Map<UUID, ReadmissionInfo> readmissionMap
    ) {
        return readmissionMap.entrySet().stream()
            .map(entry -> {
                Encounter current = encounters.stream()
                    .filter(e -> e.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElse(null);

                if (current == null) return null;

                ReadmissionInfo info = entry.getValue();
                Encounter previous = info.previousEncounter;

                // Get primary diagnoses
                String currentDx = getPrimaryDiagnosisText(current);
                String previousDx = getPrimaryDiagnosisText(previous);

                int daysBetween = info.daysSincePreviousDischarge;
                String riskLevel = daysBetween < 7 ? "HIGH" : daysBetween < 30 ? "MEDIUM" : "LOW";

                return ReadmissionEventResponse.builder()
                    .currentEncounterId(current.getId())
                    .currentEncounterNumber(current.getEncounterNumber())
                    .currentAdmissionDate(current.getEncounterStart())
                    .currentPrimaryDiagnosis(currentDx)
                    .previousEncounterId(previous.getId())
                    .previousEncounterNumber(previous.getEncounterNumber())
                    .previousDischargeDate(previous.getDischargeDate())
                    .previousPrimaryDiagnosis(previousDx)
                    .daysBetween(daysBetween)
                    .isUnplannedReadmission(true)
                    .isSameDiagnosisCategory(isSameDiagnosisCategory(currentDx, previousDx))
                    .isHighRisk(daysBetween < 7)
                    .isMediumRisk(daysBetween >= 7 && daysBetween <= 30)
                    .riskLevel(riskLevel)
                    .build();
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private String getPrimaryDiagnosisText(Encounter encounter) {
        return encounterDiagnosisRepository.findByEncounter(encounter).stream()
            .filter(d -> d.getDiagnosisType() == DiagnosisType.PRIMARY)
            .findFirst()
            .map(EncounterDiagnosis::getDiagnosisText)
            .orElse("Unknown");
    }

    private String determineEventColor(Encounter encounter, ReadmissionInfo readmissionInfo) {
        if (readmissionInfo != null && readmissionInfo.isReadmission) {
            if (readmissionInfo.daysSincePreviousDischarge < 7) return "#F44336"; // Red
            if (readmissionInfo.daysSincePreviousDischarge < 30) return "#FF9800"; // Orange
        }
        if (encounter.getEncounterType() == EncounterType.EMERGENCY) return "#FF5722";
        if (encounter.getEncounterType() == EncounterType.INPATIENT) return "#2196F3";
        return "#4CAF50";
    }

    private String determineSeverity(Encounter encounter, ReadmissionInfo readmissionInfo) {
        if (readmissionInfo != null && readmissionInfo.daysSincePreviousDischarge < 7) return "CRITICAL";
        if (encounter.getPriority() == Priority.EMERGENCY || encounter.getPriority() == Priority.STAT) return "HIGH";
        if (encounter.getEncounterType() == EncounterType.EMERGENCY) return "HIGH";
        if (readmissionInfo != null) return "MEDIUM";
        return "LOW";
    }

    private String extractDiseaseCategory(String icd10Code) {
        if (icd10Code == null || icd10Code.length() < 1) return "Unknown";
        // Extract major category from ICD-10 code (first letter)
        char firstChar = icd10Code.charAt(0);
        return switch (firstChar) {
            case 'A', 'B' -> "Infectious and Parasitic Diseases";
            case 'C', 'D' -> "Neoplasms";
            case 'E' -> "Endocrine, Nutritional and Metabolic Diseases";
            case 'F' -> "Mental and Behavioral Disorders";
            case 'G' -> "Diseases of the Nervous System";
            case 'H' -> "Diseases of the Eye and Ear";
            case 'I' -> "Diseases of the Circulatory System";
            case 'J' -> "Diseases of the Respiratory System";
            case 'K' -> "Diseases of the Digestive System";
            case 'L' -> "Diseases of the Skin";
            case 'M' -> "Diseases of the Musculoskeletal System";
            case 'N' -> "Diseases of the Genitourinary System";
            case 'O' -> "Pregnancy, Childbirth and the Puerperium";
            case 'P' -> "Conditions Originating in the Perinatal Period";
            case 'Q' -> "Congenital Malformations";
            case 'R' -> "Symptoms and Abnormal Findings";
            case 'S', 'T' -> "Injury, Poisoning";
            default -> "Other";
        };
    }

    private boolean isSameDiagnosisCategory(String dx1, String dx2) {
        if (dx1 == null || dx2 == null) return false;
        if (dx1.length() < 3 || dx2.length() < 3) return false;
        // Compare first 3 characters of ICD-10 codes
        return dx1.substring(0, 3).equals(dx2.substring(0, 3));
    }

    private long countByType(List<Encounter> encounters, EncounterType type) {
        return encounters.stream()
            .filter(e -> e.getEncounterType() == type)
            .count();
    }

    // Helper class for readmission detection
    private static class ReadmissionInfo {
        Encounter previousEncounter;
        int daysSincePreviousDischarge;
        boolean isReadmission;
    }
}
