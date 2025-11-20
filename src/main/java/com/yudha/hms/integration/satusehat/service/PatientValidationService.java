package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.Patient;
import com.yudha.hms.integration.satusehat.exception.SatusehatValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for validating Patient data before submission to SATUSEHAT.
 *
 * Validates:
 * - NIK format (16 digits) and structure
 * - Required FHIR Patient fields
 * - Administrative code formats
 * - Business rules (age, date validations)
 * - Data completeness
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientValidationService {

    private static final Pattern NIK_PATTERN = Pattern.compile("^[0-9]{16}$");
    private static final Pattern BPJS_PATTERN = Pattern.compile("^[0-9]{13}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[0-9]{5}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Validate HMS patient before creating FHIR resource.
     *
     * @param hmsPatient HMS patient entity
     * @throws SatusehatValidationException if validation fails
     */
    public void validateHmsPatient(com.yudha.hms.patient.entity.Patient hmsPatient) {
        log.debug("Validating HMS patient: {}", hmsPatient.getMrn());

        List<String> errors = new ArrayList<>();

        // Validate NIK
        if (hmsPatient.getNik() == null || hmsPatient.getNik().isEmpty()) {
            errors.add("NIK is required for SATUSEHAT submission");
        } else {
            validateNik(hmsPatient.getNik(), errors);
        }

        // Validate required fields
        if (hmsPatient.getFullName() == null || hmsPatient.getFullName().isEmpty()) {
            errors.add("Full name is required");
        }

        if (hmsPatient.getBirthDate() == null) {
            errors.add("Birth date is required");
        } else {
            // Validate birth date is in the past
            if (hmsPatient.getBirthDate().isAfter(LocalDate.now())) {
                errors.add("Birth date cannot be in the future");
            }
        }

        if (hmsPatient.getGender() == null) {
            errors.add("Gender is required");
        }

        // Validate phone numbers if provided
        if (hmsPatient.getPhonePrimary() != null && !hmsPatient.getPhonePrimary().isEmpty()) {
            if (!PHONE_PATTERN.matcher(hmsPatient.getPhonePrimary()).matches()) {
                errors.add("Invalid primary phone number format");
            }
        }

        if (hmsPatient.getPhoneSecondary() != null && !hmsPatient.getPhoneSecondary().isEmpty()) {
            if (!PHONE_PATTERN.matcher(hmsPatient.getPhoneSecondary()).matches()) {
                errors.add("Invalid secondary phone number format");
            }
        }

        // Validate addresses if provided
        if (hmsPatient.getAddresses() != null && !hmsPatient.getAddresses().isEmpty()) {
            for (int i = 0; i < hmsPatient.getAddresses().size(); i++) {
                var address = hmsPatient.getAddresses().get(i);
                validateHmsAddress(address, i, errors);
            }
        }

        // Throw exception if there are errors
        if (!errors.isEmpty()) {
            String errorMessage = "Patient validation failed: " + String.join("; ", errors);
            log.error(errorMessage);
            throw new SatusehatValidationException(errorMessage);
        }

        log.debug("HMS patient validation passed: {}", hmsPatient.getMrn());
    }

    /**
     * Validate FHIR Patient resource before submission.
     *
     * @param patient FHIR Patient resource
     * @throws SatusehatValidationException if validation fails
     */
    public void validateFhirPatient(Patient patient) {
        log.debug("Validating FHIR Patient resource");

        List<String> errors = new ArrayList<>();

        // Validate resource type
        if (!"Patient".equals(patient.getResourceType())) {
            errors.add("Invalid resource type: must be 'Patient'");
        }

        // Validate identifiers
        if (patient.getIdentifier() == null || patient.getIdentifier().isEmpty()) {
            errors.add("At least one identifier is required");
        } else {
            // Validate NIK presence
            boolean hasNik = patient.getIdentifier().stream()
                .anyMatch(id -> "https://fhir.kemkes.go.id/id/nik".equals(id.getSystem()));

            if (!hasNik) {
                errors.add("NIK identifier is required");
            }

            // Validate each identifier
            patient.getIdentifier().forEach(id -> {
                if (id.getSystem() == null || id.getSystem().isEmpty()) {
                    errors.add("Identifier system is required");
                }
                if (id.getValue() == null || id.getValue().isEmpty()) {
                    errors.add("Identifier value is required");
                }

                // Validate NIK format
                if ("https://fhir.kemkes.go.id/id/nik".equals(id.getSystem())) {
                    validateNik(id.getValue(), errors);
                }
            });
        }

        // Validate name
        if (patient.getName() == null || patient.getName().isEmpty()) {
            errors.add("At least one name is required");
        } else {
            patient.getName().forEach(name -> {
                if (name.getText() == null || name.getText().isEmpty()) {
                    errors.add("Name text is required");
                }
            });
        }

        // Validate gender
        if (patient.getGender() == null || patient.getGender().isEmpty()) {
            errors.add("Gender is required");
        } else {
            if (!List.of("male", "female", "other", "unknown").contains(patient.getGender())) {
                errors.add("Invalid gender value: must be male, female, other, or unknown");
            }
        }

        // Validate birth date
        if (patient.getBirthDate() == null || patient.getBirthDate().isEmpty()) {
            errors.add("Birth date is required");
        } else {
            try {
                LocalDate birthDate = LocalDate.parse(patient.getBirthDate(), DATE_FORMATTER);
                if (birthDate.isAfter(LocalDate.now())) {
                    errors.add("Birth date cannot be in the future");
                }
            } catch (DateTimeParseException e) {
                errors.add("Invalid birth date format: must be YYYY-MM-DD");
            }
        }

        // Validate addresses if provided
        if (patient.getAddress() != null && !patient.getAddress().isEmpty()) {
            patient.getAddress().forEach(address -> {
                if (address.getLine() == null || address.getLine().isEmpty()) {
                    errors.add("Address line is required");
                }
                if (address.getCity() == null || address.getCity().isEmpty()) {
                    errors.add("Address city is required");
                }
                if (address.getState() == null || address.getState().isEmpty()) {
                    errors.add("Address state (province) is required");
                }
                if (address.getCountry() == null || address.getCountry().isEmpty()) {
                    errors.add("Address country is required");
                }

                // Validate postal code format if provided
                if (address.getPostalCode() != null && !address.getPostalCode().isEmpty()) {
                    if (!POSTAL_CODE_PATTERN.matcher(address.getPostalCode()).matches()) {
                        errors.add("Invalid postal code format: must be 5 digits");
                    }
                }
            });
        }

        // Throw exception if there are errors
        if (!errors.isEmpty()) {
            String errorMessage = "FHIR Patient validation failed: " + String.join("; ", errors);
            log.error(errorMessage);
            throw new SatusehatValidationException(errorMessage);
        }

        log.debug("FHIR Patient validation passed");
    }

    /**
     * Validate NIK format and structure.
     *
     * NIK format: PPKKSSDDMMYYXXXX
     * - PP: Province code (2 digits)
     * - KK: City/Regency code (2 digits)
     * - SS: District code (2 digits)
     * - DDMMYY: Birth date (6 digits)
     * - XXXX: Sequential number (4 digits)
     *
     * @param nik NIK to validate
     * @param errors List to collect error messages
     */
    private void validateNik(String nik, List<String> errors) {
        // Check format
        if (!NIK_PATTERN.matcher(nik).matches()) {
            errors.add("Invalid NIK format: must be exactly 16 digits");
            return;
        }

        // Validate NIK structure
        try {
            // Extract birth date from NIK (positions 6-11: DDMMYY)
            int day = Integer.parseInt(nik.substring(6, 8));
            int month = Integer.parseInt(nik.substring(8, 10));
            int year = Integer.parseInt(nik.substring(10, 12));

            // For females, day is added by 40
            if (day > 40) {
                day -= 40;
            }

            // Adjust year (assume 1900s for year >= 50, 2000s for year < 50)
            int fullYear = year >= 50 ? 1900 + year : 2000 + year;

            // Validate date components
            if (day < 1 || day > 31) {
                errors.add("Invalid NIK: day must be between 1-31");
            }
            if (month < 1 || month > 12) {
                errors.add("Invalid NIK: month must be between 1-12");
            }

            // Try to create a valid date
            try {
                LocalDate.of(fullYear, month, day);
            } catch (Exception e) {
                errors.add("Invalid NIK: birth date is not a valid calendar date");
            }

        } catch (NumberFormatException e) {
            errors.add("Invalid NIK: unable to parse birth date components");
        }
    }

    /**
     * Validate HMS patient address.
     */
    private void validateHmsAddress(com.yudha.hms.patient.entity.PatientAddress address,
                                     int index,
                                     List<String> errors) {
        String prefix = "Address[" + index + "]";

        if (address.getAddressLine1() == null || address.getAddressLine1().isEmpty()) {
            errors.add(prefix + ": Address line 1 is required");
        }

        if (address.getProvinceId() == null) {
            errors.add(prefix + ": Province is required");
        }

        if (address.getCityId() == null) {
            errors.add(prefix + ": City is required");
        }

        if (address.getDistrictId() == null) {
            errors.add(prefix + ": District is required");
        }

        if (address.getVillageId() == null) {
            errors.add(prefix + ": Village is required");
        }

        if (address.getPostalCode() != null && !address.getPostalCode().isEmpty()) {
            if (!POSTAL_CODE_PATTERN.matcher(address.getPostalCode()).matches()) {
                errors.add(prefix + ": Invalid postal code format (must be 5 digits)");
            }
        }
    }

    /**
     * Check if NIK is valid format.
     *
     * @param nik NIK to check
     * @return true if valid format
     */
    public boolean isValidNikFormat(String nik) {
        if (nik == null || nik.isEmpty()) {
            return false;
        }
        return NIK_PATTERN.matcher(nik).matches();
    }

    /**
     * Extract birth date from NIK.
     *
     * @param nik NIK (16 digits)
     * @return Birth date extracted from NIK, or null if invalid
     */
    public LocalDate extractBirthDateFromNik(String nik) {
        if (!isValidNikFormat(nik)) {
            return null;
        }

        try {
            int day = Integer.parseInt(nik.substring(6, 8));
            int month = Integer.parseInt(nik.substring(8, 10));
            int year = Integer.parseInt(nik.substring(10, 12));

            // For females, day is added by 40
            if (day > 40) {
                day -= 40;
            }

            // Adjust year
            int fullYear = year >= 50 ? 1900 + year : 2000 + year;

            return LocalDate.of(fullYear, month, day);
        } catch (Exception e) {
            log.warn("Failed to extract birth date from NIK: {}", nik, e);
            return null;
        }
    }

    /**
     * Extract gender from NIK.
     *
     * @param nik NIK (16 digits)
     * @return Gender ('M' for male, 'F' for female), or null if invalid
     */
    public Character extractGenderFromNik(String nik) {
        if (!isValidNikFormat(nik)) {
            return null;
        }

        try {
            int day = Integer.parseInt(nik.substring(6, 8));
            // If day > 40, it's female
            return day > 40 ? 'F' : 'M';
        } catch (Exception e) {
            log.warn("Failed to extract gender from NIK: {}", nik, e);
            return null;
        }
    }
}
