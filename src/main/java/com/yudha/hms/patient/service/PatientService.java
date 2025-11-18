package com.yudha.hms.patient.service;

import com.yudha.hms.patient.dto.*;
import com.yudha.hms.patient.entity.EmergencyContact;
import com.yudha.hms.patient.entity.Patient;
import com.yudha.hms.patient.entity.PatientAddress;
import com.yudha.hms.patient.entity.PatientAllergy;
import com.yudha.hms.patient.repository.PatientRepository;
import com.yudha.hms.shared.constant.AddressType;
import com.yudha.hms.shared.exception.DuplicateResourceException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import com.yudha.hms.shared.exception.ValidationException;
import com.yudha.hms.shared.util.BpjsValidator;
import com.yudha.hms.shared.util.MrnGenerator;
import com.yudha.hms.shared.util.NikValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Patient Service.
 *
 * Business logic for patient management including:
 * - Patient registration with NIK and BPJS validation
 * - Duplicate checking
 * - MRN auto-generation
 * - DUKCAPIL integration preparation
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;

    /**
     * Register a new patient.
     *
     * Performs comprehensive validation:
     * - NIK format and validity
     * - BPJS number format
     * - Duplicate checking
     * - Cross-field validation
     *
     * @param requestDto patient registration request
     * @return registered patient response
     * @throws ValidationException if validation fails
     * @throws DuplicateResourceException if NIK or BPJS already exists
     */
    @Transactional
    public PatientResponseDto registerPatient(PatientRequestDto requestDto) {
        log.info("Registering new patient: {}", requestDto.getFullName());

        // Comprehensive validation
        validatePatientRequest(requestDto);

        // Check for duplicates
        checkDuplicates(requestDto);

        // Generate MRN
        Long sequence = patientRepository.getNextMrnSequence();
        String mrn = MrnGenerator.generate(sequence);
        log.debug("Generated MRN: {}", mrn);

        // Build patient entity
        Patient patient = buildPatientEntity(requestDto, mrn);

        // Save patient
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient registered successfully with MRN: {}", savedPatient.getMrn());

        // Map to response DTO
        return mapToResponseDto(savedPatient);
    }

    /**
     * Get patient by ID.
     *
     * @param id patient UUID
     * @return patient response
     * @throws ResourceNotFoundException if patient not found
     */
    public PatientResponseDto getPatientById(UUID id) {
        log.debug("Fetching patient by ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", id));
        return mapToResponseDto(patient);
    }

    /**
     * Get patient by MRN.
     *
     * @param mrn medical record number
     * @return patient response
     * @throws ResourceNotFoundException if patient not found
     */
    public PatientResponseDto getPatientByMrn(String mrn) {
        log.debug("Fetching patient by MRN: {}", mrn);
        Patient patient = patientRepository.findByMrn(mrn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "MRN", mrn));
        return mapToResponseDto(patient);
    }

    /**
     * Get patient by NIK.
     *
     * @param nik Indonesian national ID
     * @return patient response
     * @throws ResourceNotFoundException if patient not found
     */
    public PatientResponseDto getPatientByNik(String nik) {
        log.debug("Fetching patient by NIK: {}", nik);
        Patient patient = patientRepository.findByNik(nik)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "NIK", nik));
        return mapToResponseDto(patient);
    }

    /**
     * Search patients by name.
     *
     * @param name name to search (case-insensitive, partial match)
     * @return list of matching patients
     */
    public List<PatientResponseDto> searchPatientsByName(String name) {
        log.debug("Searching patients by name: {}", name);
        List<Patient> patients = patientRepository.findByFullNameContainingIgnoreCase(name);
        return patients.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Update patient information.
     *
     * @param id patient ID
     * @param requestDto update request
     * @return updated patient response
     * @throws ResourceNotFoundException if patient not found
     * @throws DuplicateResourceException if NIK or BPJS already exists for another patient
     */
    @Transactional
    public PatientResponseDto updatePatient(UUID id, PatientRequestDto requestDto) {
        log.info("Updating patient: {}", id);

        // Fetch existing patient
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", id));

        // Validate request
        validatePatientRequest(requestDto);

        // Check duplicates (excluding current patient)
        checkDuplicatesForUpdate(requestDto, id);

        // Update patient fields
        updatePatientFields(patient, requestDto);

        // Save updated patient
        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient updated successfully: {}", savedPatient.getMrn());

        return mapToResponseDto(savedPatient);
    }

    /**
     * Soft delete patient.
     *
     * @param id patient ID
     * @throws ResourceNotFoundException if patient not found
     */
    @Transactional
    public void deletePatient(UUID id) {
        log.info("Soft deleting patient: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "ID", id));

        // Soft delete will be handled by @SQLDelete annotation
        patientRepository.delete(patient);
        log.info("Patient soft deleted successfully: {}", patient.getMrn());
    }

    /**
     * Get all active patients.
     *
     * @return list of active patients
     */
    public List<PatientResponseDto> getActivePatients() {
        log.debug("Fetching all active patients");
        List<Patient> patients = patientRepository.findByIsActiveTrue();
        return patients.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all VIP patients.
     *
     * @return list of VIP patients
     */
    public List<PatientResponseDto> getVipPatients() {
        log.debug("Fetching all VIP patients");
        List<Patient> patients = patientRepository.findByIsVipTrue();
        return patients.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // ========== VALIDATION METHODS ==========

    /**
     * Comprehensive patient request validation.
     *
     * @param requestDto request to validate
     * @throws ValidationException if validation fails
     */
    private void validatePatientRequest(PatientRequestDto requestDto) {
        Map<String, String> errors = new HashMap<>();

        // Validate NIK if provided
        if (StringUtils.hasText(requestDto.getNik())) {
            String nikValidation = NikValidator.validateWithMessage(requestDto.getNik());
            if (nikValidation != null) {
                errors.put("nik", nikValidation);
            } else {
                // Cross-validate NIK birth date with provided birth date
                LocalDate nikBirthDate = NikValidator.extractBirthDate(requestDto.getNik());
                if (nikBirthDate != null && !nikBirthDate.equals(requestDto.getBirthDate())) {
                    errors.put("birthDate",
                        String.format("Tanggal lahir tidak sesuai dengan NIK (NIK: %s, Input: %s)",
                            nikBirthDate, requestDto.getBirthDate()));
                }

                // Cross-validate NIK gender with provided gender
                String nikGender = NikValidator.extractGender(requestDto.getNik());
                if (nikGender != null && !nikGender.equals(requestDto.getGender().name().substring(0, 1))) {
                    errors.put("gender",
                        String.format("Jenis kelamin tidak sesuai dengan NIK (NIK: %s, Input: %s)",
                            nikGender.equals("L") ? "Laki-laki" : "Perempuan",
                            requestDto.getGender()));
                }
            }
        }

        // Validate BPJS number if provided
        if (StringUtils.hasText(requestDto.getBpjsNumber())) {
            String bpjsValidation = BpjsValidator.validateWithMessage(requestDto.getBpjsNumber());
            if (bpjsValidation != null) {
                errors.put("bpjsNumber", bpjsValidation);
            }
        }

        // Validate age
        if (requestDto.getBirthDate() != null) {
            LocalDate today = LocalDate.now();
            if (requestDto.getBirthDate().isAfter(today)) {
                errors.put("birthDate", "Tanggal lahir tidak boleh di masa depan");
            }

            LocalDate maxBirthDate = today.minusYears(150);
            if (requestDto.getBirthDate().isBefore(maxBirthDate)) {
                errors.put("birthDate", "Tanggal lahir tidak valid (lebih dari 150 tahun)");
            }
        }

        // Validate addresses
        if (requestDto.getAddresses() != null && !requestDto.getAddresses().isEmpty()) {
            validateAddresses(requestDto.getAddresses(), errors);
        }

        // Validate emergency contacts
        if (requestDto.getEmergencyContacts() != null && !requestDto.getEmergencyContacts().isEmpty()) {
            validateEmergencyContacts(requestDto.getEmergencyContacts(), errors);
        }

        // Throw exception if there are validation errors
        if (!errors.isEmpty()) {
            throw new ValidationException("Validasi data pasien gagal", errors);
        }

        // DUKCAPIL Integration Point
        // TODO: Implement NIK verification with DUKCAPIL API
        // This would verify NIK authenticity and fetch official data
        log.debug("DUKCAPIL verification not yet implemented for NIK: {}", requestDto.getNik());
    }

    /**
     * Validate addresses.
     *
     * @param addresses addresses to validate
     * @param errors error map to populate
     */
    private void validateAddresses(List<PatientAddressDto> addresses, Map<String, String> errors) {
        long ktpCount = addresses.stream()
                .filter(a -> a.getAddressType() == AddressType.KTP)
                .count();

        if (ktpCount > 1) {
            errors.put("addresses", "Hanya boleh ada satu alamat KTP");
        }

        long primaryCount = addresses.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsPrimary()))
                .count();

        if (primaryCount > 1) {
            errors.put("addresses", "Hanya boleh ada satu alamat utama");
        }
    }

    /**
     * Validate emergency contacts.
     *
     * @param contacts contacts to validate
     * @param errors error map to populate
     */
    private void validateEmergencyContacts(List<EmergencyContactDto> contacts, Map<String, String> errors) {
        Set<Integer> priorities = new HashSet<>();
        for (EmergencyContactDto contact : contacts) {
            if (contact.getPriority() != null) {
                if (!priorities.add(contact.getPriority())) {
                    errors.put("emergencyContacts",
                        "Prioritas kontak darurat tidak boleh duplikat: " + contact.getPriority());
                }
            }
        }
    }

    /**
     * Check for duplicate NIK and BPJS number.
     *
     * @param requestDto request to check
     * @throws DuplicateResourceException if duplicate found
     */
    private void checkDuplicates(PatientRequestDto requestDto) {
        // Check NIK
        if (StringUtils.hasText(requestDto.getNik()) &&
            patientRepository.existsByNik(requestDto.getNik())) {
            throw new DuplicateResourceException("Pasien", "NIK", requestDto.getNik());
        }

        // Check BPJS number
        if (StringUtils.hasText(requestDto.getBpjsNumber()) &&
            patientRepository.existsByBpjsNumber(requestDto.getBpjsNumber())) {
            throw new DuplicateResourceException("Pasien", "Nomor BPJS", requestDto.getBpjsNumber());
        }
    }

    /**
     * Check for duplicates when updating (excluding current patient).
     *
     * @param requestDto request to check
     * @param currentPatientId current patient ID to exclude
     * @throws DuplicateResourceException if duplicate found
     */
    private void checkDuplicatesForUpdate(PatientRequestDto requestDto, UUID currentPatientId) {
        // Check NIK
        if (StringUtils.hasText(requestDto.getNik()) &&
            patientRepository.existsByNikAndIdNot(requestDto.getNik(), currentPatientId)) {
            throw new DuplicateResourceException("Pasien", "NIK", requestDto.getNik());
        }

        // Check BPJS number
        if (StringUtils.hasText(requestDto.getBpjsNumber()) &&
            patientRepository.existsByBpjsNumberAndIdNot(requestDto.getBpjsNumber(), currentPatientId)) {
            throw new DuplicateResourceException("Pasien", "Nomor BPJS", requestDto.getBpjsNumber());
        }
    }

    // ========== MAPPING METHODS ==========

    /**
     * Build Patient entity from request DTO.
     *
     * @param requestDto request DTO
     * @param mrn generated MRN
     * @return patient entity
     */
    private Patient buildPatientEntity(PatientRequestDto requestDto, String mrn) {
        Patient patient = Patient.builder()
                .mrn(mrn)
                .nik(requestDto.getNik())
                .nikVerified(false) // Will be set to true after DUKCAPIL verification
                .bpjsNumber(requestDto.getBpjsNumber())
                .bpjsActive(requestDto.getBpjsActive())
                .bpjsClass(requestDto.getBpjsClass())
                .title(requestDto.getTitle())
                .fullName(requestDto.getFullName())
                .birthPlace(requestDto.getBirthPlace())
                .birthDate(requestDto.getBirthDate())
                .gender(requestDto.getGender())
                .religionId(requestDto.getReligionId())
                .maritalStatusId(requestDto.getMaritalStatusId())
                .bloodTypeId(requestDto.getBloodTypeId())
                .educationId(requestDto.getEducationId())
                .occupationId(requestDto.getOccupationId())
                .phonePrimary(requestDto.getPhonePrimary())
                .phoneSecondary(requestDto.getPhoneSecondary())
                .email(requestDto.getEmail())
                .photoUrl(requestDto.getPhotoUrl())
                .registrationDate(LocalDateTime.now())
                .registrationSource(requestDto.getRegistrationSource())
                .isActive(true)
                .isVip(requestDto.getIsVip() != null ? requestDto.getIsVip() : false)
                .build();

        // Add addresses
        if (requestDto.getAddresses() != null) {
            for (PatientAddressDto addressDto : requestDto.getAddresses()) {
                patient.addAddress(buildAddressEntity(addressDto, patient));
            }
        }

        // Add emergency contacts
        if (requestDto.getEmergencyContacts() != null) {
            for (EmergencyContactDto contactDto : requestDto.getEmergencyContacts()) {
                patient.addEmergencyContact(buildEmergencyContactEntity(contactDto, patient));
            }
        }

        // Add allergies
        if (requestDto.getAllergies() != null) {
            for (PatientAllergyDto allergyDto : requestDto.getAllergies()) {
                patient.addAllergy(buildAllergyEntity(allergyDto, patient));
            }
        }

        return patient;
    }

    /**
     * Build PatientAddress entity from DTO.
     *
     * @param dto address DTO
     * @param patient parent patient
     * @return address entity
     */
    private PatientAddress buildAddressEntity(PatientAddressDto dto, Patient patient) {
        return PatientAddress.builder()
                .patient(patient)
                .addressType(dto.getAddressType())
                .addressLine1(dto.getAddressLine1())
                .addressLine2(dto.getAddressLine2())
                .villageId(dto.getVillageId())
                .districtId(dto.getDistrictId())
                .cityId(dto.getCityId())
                .provinceId(dto.getProvinceId())
                .postalCode(dto.getPostalCode())
                .rt(dto.getRt())
                .rw(dto.getRw())
                .isPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false)
                .build();
    }

    /**
     * Build EmergencyContact entity from DTO.
     *
     * @param dto contact DTO
     * @param patient parent patient
     * @return contact entity
     */
    private EmergencyContact buildEmergencyContactEntity(EmergencyContactDto dto, Patient patient) {
        return EmergencyContact.builder()
                .patient(patient)
                .fullName(dto.getFullName())
                .relationship(dto.getRelationship())
                .phonePrimary(dto.getPhonePrimary())
                .phoneSecondary(dto.getPhoneSecondary())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .priority(dto.getPriority() != null ? dto.getPriority() : 1)
                .notes(dto.getNotes())
                .build();
    }

    /**
     * Build PatientAllergy entity from DTO.
     *
     * @param dto allergy DTO
     * @param patient parent patient
     * @return allergy entity
     */
    private PatientAllergy buildAllergyEntity(PatientAllergyDto dto, Patient patient) {
        return PatientAllergy.builder()
                .patient(patient)
                .allergenType(dto.getAllergenType())
                .allergenName(dto.getAllergenName())
                .reaction(dto.getReaction())
                .severity(dto.getSeverity())
                .notes(dto.getNotes())
                .build();
    }

    /**
     * Update patient fields from request DTO.
     *
     * @param patient patient entity to update
     * @param requestDto update request
     */
    private void updatePatientFields(Patient patient, PatientRequestDto requestDto) {
        patient.setNik(requestDto.getNik());
        patient.setBpjsNumber(requestDto.getBpjsNumber());
        patient.setBpjsActive(requestDto.getBpjsActive());
        patient.setBpjsClass(requestDto.getBpjsClass());
        patient.setTitle(requestDto.getTitle());
        patient.setFullName(requestDto.getFullName());
        patient.setBirthPlace(requestDto.getBirthPlace());
        patient.setBirthDate(requestDto.getBirthDate());
        patient.setGender(requestDto.getGender());
        patient.setReligionId(requestDto.getReligionId());
        patient.setMaritalStatusId(requestDto.getMaritalStatusId());
        patient.setBloodTypeId(requestDto.getBloodTypeId());
        patient.setEducationId(requestDto.getEducationId());
        patient.setOccupationId(requestDto.getOccupationId());
        patient.setPhonePrimary(requestDto.getPhonePrimary());
        patient.setPhoneSecondary(requestDto.getPhoneSecondary());
        patient.setEmail(requestDto.getEmail());
        patient.setPhotoUrl(requestDto.getPhotoUrl());
        patient.setIsVip(requestDto.getIsVip());

        // Update addresses (clear and re-add)
        patient.getAddresses().clear();
        if (requestDto.getAddresses() != null) {
            for (PatientAddressDto addressDto : requestDto.getAddresses()) {
                patient.addAddress(buildAddressEntity(addressDto, patient));
            }
        }

        // Update emergency contacts (clear and re-add)
        patient.getEmergencyContacts().clear();
        if (requestDto.getEmergencyContacts() != null) {
            for (EmergencyContactDto contactDto : requestDto.getEmergencyContacts()) {
                patient.addEmergencyContact(buildEmergencyContactEntity(contactDto, patient));
            }
        }

        // Update allergies (clear and re-add)
        patient.getAllergies().clear();
        if (requestDto.getAllergies() != null) {
            for (PatientAllergyDto allergyDto : requestDto.getAllergies()) {
                patient.addAllergy(buildAllergyEntity(allergyDto, patient));
            }
        }
    }

    /**
     * Map Patient entity to response DTO.
     *
     * @param patient patient entity
     * @return response DTO
     */
    private PatientResponseDto mapToResponseDto(Patient patient) {
        return PatientResponseDto.builder()
                .id(patient.getId())
                .mrn(patient.getMrn())
                .nik(patient.getNik())
                .nikVerified(patient.getNikVerified())
                .bpjsNumber(patient.getBpjsNumber())
                .bpjsActive(patient.getBpjsActive())
                .bpjsClass(patient.getBpjsClass())
                .title(patient.getTitle())
                .fullName(patient.getFullName())
                .displayName(patient.getDisplayName())
                .birthPlace(patient.getBirthPlace())
                .birthDate(patient.getBirthDate())
                .age(patient.getAge())
                .gender(patient.getGender())
                .religionId(patient.getReligionId())
                .maritalStatusId(patient.getMaritalStatusId())
                .bloodTypeId(patient.getBloodTypeId())
                .phonePrimary(patient.getPhonePrimary())
                .phoneSecondary(patient.getPhoneSecondary())
                .email(patient.getEmail())
                .photoUrl(patient.getPhotoUrl())
                .registrationDate(patient.getRegistrationDate())
                .registrationSource(patient.getRegistrationSource())
                .isActive(patient.getIsActive())
                .isVip(patient.getIsVip())
                .addresses(patient.getAddresses() != null ?
                    patient.getAddresses().stream()
                        .map(this::mapToAddressDto)
                        .collect(Collectors.toList()) : null)
                .emergencyContacts(patient.getEmergencyContacts() != null ?
                    patient.getEmergencyContacts().stream()
                        .map(this::mapToEmergencyContactDto)
                        .collect(Collectors.toList()) : null)
                .allergies(patient.getAllergies() != null ?
                    patient.getAllergies().stream()
                        .map(this::mapToAllergyDto)
                        .collect(Collectors.toList()) : null)
                .createdAt(patient.getCreatedAt())
                .createdBy(patient.getCreatedBy())
                .updatedAt(patient.getUpdatedAt())
                .updatedBy(patient.getUpdatedBy())
                .build();
    }

    /**
     * Map PatientAddress entity to DTO.
     *
     * @param address address entity
     * @return address DTO
     */
    private PatientAddressDto mapToAddressDto(PatientAddress address) {
        return PatientAddressDto.builder()
                .addressType(address.getAddressType())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .villageId(address.getVillageId())
                .districtId(address.getDistrictId())
                .cityId(address.getCityId())
                .provinceId(address.getProvinceId())
                .postalCode(address.getPostalCode())
                .rt(address.getRt())
                .rw(address.getRw())
                .isPrimary(address.getIsPrimary())
                .build();
    }

    /**
     * Map EmergencyContact entity to DTO.
     *
     * @param contact contact entity
     * @return contact DTO
     */
    private EmergencyContactDto mapToEmergencyContactDto(EmergencyContact contact) {
        return EmergencyContactDto.builder()
                .fullName(contact.getFullName())
                .relationship(contact.getRelationship())
                .phonePrimary(contact.getPhonePrimary())
                .phoneSecondary(contact.getPhoneSecondary())
                .email(contact.getEmail())
                .address(contact.getAddress())
                .priority(contact.getPriority())
                .notes(contact.getNotes())
                .build();
    }

    /**
     * Map PatientAllergy entity to DTO.
     *
     * @param allergy allergy entity
     * @return allergy DTO
     */
    private PatientAllergyDto mapToAllergyDto(PatientAllergy allergy) {
        return PatientAllergyDto.builder()
                .allergenType(allergy.getAllergenType())
                .allergenName(allergy.getAllergenName())
                .reaction(allergy.getReaction())
                .severity(allergy.getSeverity())
                .notes(allergy.getNotes())
                .build();
    }

    // ============================================================================
    // SEARCH FUNCTIONALITY
    // ============================================================================

    /**
     * Search patients with advanced criteria.
     *
     * Supports:
     * - Quick search across multiple fields
     * - Specific field searches (MRN, NIK, BPJS, name, phone, email)
     * - Advanced filters (age, gender, dates, address, status)
     * - Pagination and sorting
     * - Configurable data depth (BASIC, DETAILED, COMPLETE)
     *
     * @param criteria search criteria
     * @return paginated search results
     */
    public PatientSearchResponse<?> searchPatients(PatientSearchCriteria criteria) {
        long startTime = System.currentTimeMillis();
        log.debug("Searching patients with criteria: {}", criteria);

        // Build specification
        org.springframework.data.jpa.domain.Specification<Patient> spec =
            com.yudha.hms.patient.repository.PatientSpecification.buildSpecification(criteria);

        // Build sort
        org.springframework.data.domain.Sort sort = buildSort(criteria.getSortBy(), criteria.getSortDirection());

        // Build pageable
        org.springframework.data.domain.Pageable pageable =
            org.springframework.data.domain.PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

        // Execute search
        org.springframework.data.domain.Page<Patient> page = patientRepository.findAll(spec, pageable);

        // Map results based on data depth
        Object results = mapSearchResults(page.getContent(), criteria.getDataDepth());

        // Calculate execution time
        long executionTime = System.currentTimeMillis() - startTime;

        // Build response
        return buildSearchResponse(page, results, criteria, executionTime);
    }

    /**
     * Quick search patients by term.
     * Searches across MRN, NIK, BPJS number, and name.
     *
     * @param searchTerm search term
     * @param page page number
     * @param size page size
     * @return search results
     */
    public PatientSearchResponse<?> quickSearch(String searchTerm, int page, int size) {
        PatientSearchCriteria criteria = PatientSearchCriteria.builder()
            .searchTerm(searchTerm)
            .page(page)
            .size(size)
            .dataDepth(PatientSearchCriteria.DataDepth.DETAILED)
            .build();

        return searchPatients(criteria);
    }

    /**
     * Search patients by name with full-text search.
     *
     * @param name patient name
     * @param page page number
     * @param size page size
     * @return search results
     */
    public PatientSearchResponse<?> searchByName(String name, int page, int size) {
        PatientSearchCriteria criteria = PatientSearchCriteria.builder()
            .name(name)
            .page(page)
            .size(size)
            .dataDepth(PatientSearchCriteria.DataDepth.DETAILED)
            .build();

        return searchPatients(criteria);
    }

    /**
     * Build sort object from criteria.
     */
    private org.springframework.data.domain.Sort buildSort(String sortBy, String sortDirection) {
        org.springframework.data.domain.Sort.Direction direction =
            "ASC".equalsIgnoreCase(sortDirection) ?
                org.springframework.data.domain.Sort.Direction.ASC :
                org.springframework.data.domain.Sort.Direction.DESC;

        // Map sort field to entity field
        String entityField = switch (sortBy.toLowerCase()) {
            case "mrn" -> "mrn";
            case "fullname", "name" -> "fullName";
            case "birthdate" -> "birthDate";
            case "registrationdate" -> "registrationDate";
            default -> "createdAt";
        };

        return org.springframework.data.domain.Sort.by(direction, entityField);
    }

    /**
     * Map search results based on data depth.
     */
    private Object mapSearchResults(List<Patient> patients, PatientSearchCriteria.DataDepth dataDepth) {
        return switch (dataDepth) {
            case BASIC -> patients.stream()
                .map(this::mapToBasicDto)
                .collect(Collectors.toList());
            case DETAILED -> patients.stream()
                .map(this::mapToDetailedDto)
                .collect(Collectors.toList());
            case COMPLETE -> patients.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
        };
    }

    /**
     * Build search response with metadata.
     */
    @SuppressWarnings("unchecked")
    private PatientSearchResponse<?> buildSearchResponse(org.springframework.data.domain.Page<Patient> page,
                                                          Object results,
                                                          PatientSearchCriteria criteria,
                                                          long executionTime) {
        PatientSearchResponse.SearchMetadata metadata = PatientSearchResponse.SearchMetadata.builder()
            .executionTimeMs(executionTime)
            .dataDepth(criteria.getDataDepth().name())
            .sortBy(criteria.getSortBy())
            .sortDirection(criteria.getSortDirection())
            .quickSearchUsed(criteria.hasQuickSearch())
            .advancedFiltersUsed(criteria.hasAdvancedFilters())
            .build();

        @SuppressWarnings("rawtypes")
        PatientSearchResponse response = PatientSearchResponse.builder()
            .patients((List) results)
            .currentPage(page.getNumber())
            .pageSize(page.getSize())
            .totalRecords(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .isFirst(page.isFirst())
            .isLast(page.isLast())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .numberOfRecords(page.getNumberOfElements())
            .metadata(metadata)
            .build();

        return response;
    }

    /**
     * Map Patient entity to Basic DTO.
     */
    private PatientBasicDto mapToBasicDto(Patient patient) {
        PatientBasicDto dto = PatientBasicDto.builder()
            .id(patient.getId())
            .mrn(patient.getMrn())
            .nik(patient.getNik())
            .bpjsNumber(patient.getBpjsNumber())
            .fullName(patient.getFullName())
            .birthDate(patient.getBirthDate())
            .gender(patient.getGender() != null ? patient.getGender().name() : null)
            .phonePrimary(patient.getPhonePrimary())
            .photoUrl(patient.getPhotoUrl())
            .isActive(patient.getIsActive())
            .isDeceased(patient.getIsDeceased())
            .isVip(patient.getIsVip())
            .bpjsActive(patient.getBpjsActive())
            .bpjsClass(patient.getBpjsClass())
            .build();

        // Calculate age
        dto.setAge(dto.calculateAge());

        return dto;
    }

    /**
     * Map Patient entity to Detailed DTO.
     */
    private PatientDetailedDto mapToDetailedDto(Patient patient) {
        PatientDetailedDto dto = PatientDetailedDto.builder()
            .id(patient.getId())
            .mrn(patient.getMrn())
            .nik(patient.getNik())
            .nikVerified(patient.getNikVerified())
            .bpjsNumber(patient.getBpjsNumber())
            .bpjsActive(patient.getBpjsActive())
            .bpjsClass(patient.getBpjsClass())
            .bpjsProviderCode(patient.getBpjsProviderCode())
            .title(patient.getTitle())
            .fullName(patient.getFullName())
            .birthPlace(patient.getBirthPlace())
            .birthDate(patient.getBirthDate())
            .gender(patient.getGender() != null ? patient.getGender().name() : null)
            .religionId(patient.getReligionId())
            .maritalStatusId(patient.getMaritalStatusId())
            .bloodTypeId(patient.getBloodTypeId())
            .educationId(patient.getEducationId())
            .occupationId(patient.getOccupationId())
            .nationality(patient.getNationality())
            .citizenship(patient.getCitizenship())
            .motherMaidenName(patient.getMotherMaidenName())
            .phonePrimary(patient.getPhonePrimary())
            .phoneSecondary(patient.getPhoneSecondary())
            .email(patient.getEmail())
            .photoUrl(patient.getPhotoUrl())
            .registrationDate(patient.getRegistrationDate())
            .registrationSource(patient.getRegistrationSource() != null ? patient.getRegistrationSource().name() : null)
            .isActive(patient.getIsActive())
            .isDeceased(patient.getIsDeceased())
            .deceasedDate(patient.getDeceasedDate())
            .isVip(patient.getIsVip())
            .vipNotes(patient.getVipNotes())
            .notes(patient.getNotes())
            .build();

        // Calculate age
        dto.setAge(dto.calculateAge());

        // Map addresses
        if (patient.getAddresses() != null && !patient.getAddresses().isEmpty()) {
            dto.setAddresses(patient.getAddresses().stream()
                .map(this::mapToAddressDto)
                .collect(Collectors.toList()));
        }

        return dto;
    }
}
