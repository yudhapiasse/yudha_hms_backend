package com.yudha.hms.integration.satusehat.service;

import com.yudha.hms.integration.satusehat.dto.fhir.*;
import com.yudha.hms.patient.entity.PatientAddress;
import com.yudha.hms.shared.constant.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for mapping HMS patient data to FHIR Patient resources.
 *
 * Converts Indonesian hospital patient data to SATUSEHAT-compliant FHIR format.
 * Handles:
 * - NIK to FHIR identifier mapping
 * - Indonesian administrative codes (province, city, district, village)
 * - Indonesian-specific extensions (religion, nationality, blood type)
 * - Name formatting with uppercase conversion
 * - Address mapping with RT/RW data
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PatientMappingService {

    // TODO: Inject master data repositories when available
    // private final ReligionRepository religionRepository;
    // private final MaritalStatusRepository maritalStatusRepository;
    // private final BloodTypeRepository bloodTypeRepository;
    // private final ProvinceRepository provinceRepository;
    // private final CityRepository cityRepository;
    // private final DistrictRepository districtRepository;
    // private final VillageRepository villageRepository;

    /**
     * Convert HMS Patient to FHIR Patient resource.
     *
     * @param hmsPatient HMS patient entity
     * @param ihsNumber IHS number from SATUSEHAT (null for new patients)
     * @return FHIR Patient resource
     */
    public Patient toFhirPatient(com.yudha.hms.patient.entity.Patient hmsPatient, String ihsNumber) {
        log.debug("Converting HMS patient {} to FHIR Patient", hmsPatient.getMrn());

        Patient.PatientBuilder builder = Patient.builder()
            .resourceType("Patient")
            .active(hmsPatient.getIsActive());

        // Set IHS number if available
        if (ihsNumber != null && !ihsNumber.isEmpty()) {
            builder.id(ihsNumber);

            // Set meta with profile
            builder.meta(Patient.Meta.builder()
                .profile(List.of("https://fhir.kemkes.go.id/r4/StructureDefinition/Patient"))
                .build());
        }

        // Identifiers
        builder.identifier(buildIdentifiers(hmsPatient, ihsNumber));

        // Name
        builder.name(buildNames(hmsPatient));

        // Telecom
        builder.telecom(buildTelecom(hmsPatient));

        // Gender
        builder.gender(mapGender(hmsPatient.getGender()));

        // Birth date
        if (hmsPatient.getBirthDate() != null) {
            builder.birthDate(hmsPatient.getBirthDate().toString());
        }

        // Deceased
        if (hmsPatient.getIsDeceased() != null && hmsPatient.getIsDeceased()) {
            builder.deceasedBoolean(true);
        }

        // Address
        if (hmsPatient.getAddresses() != null && !hmsPatient.getAddresses().isEmpty()) {
            builder.address(buildAddresses(hmsPatient.getAddresses()));
        }

        // Marital Status
        if (hmsPatient.getMaritalStatusId() != null) {
            builder.maritalStatus(buildMaritalStatus(hmsPatient.getMaritalStatusId()));
        }

        // Extensions
        List<Extension> extensions = new ArrayList<>();

        // Religion extension
        if (hmsPatient.getReligionId() != null) {
            Extension religionExt = buildReligionExtension(hmsPatient.getReligionId());
            if (religionExt != null) {
                extensions.add(religionExt);
            }
        }

        // Nationality extension
        if (hmsPatient.getNationality() != null) {
            String nationalityCode = "ID"; // Default to Indonesia
            if (!"Indonesian".equalsIgnoreCase(hmsPatient.getNationality())) {
                // Map other nationalities to ISO 3166-1 alpha-2 codes
                nationalityCode = mapNationalityToCode(hmsPatient.getNationality());
            }
            extensions.add(Extension.createNationality(nationalityCode));
        }

        // Blood type extension
        if (hmsPatient.getBloodTypeId() != null) {
            Extension bloodTypeExt = buildBloodTypeExtension(hmsPatient.getBloodTypeId());
            if (bloodTypeExt != null) {
                extensions.add(bloodTypeExt);
            }
        }

        if (!extensions.isEmpty()) {
            builder.extension(extensions);
        }

        // Communication (language)
        builder.communication(List.of(
            Patient.Communication.builder()
                .language(CodeableConcept.createLanguage("id-ID", "Indonesian"))
                .preferred(true)
                .build()
        ));

        return builder.build();
    }

    /**
     * Build FHIR identifiers from HMS patient.
     */
    private List<Identifier> buildIdentifiers(com.yudha.hms.patient.entity.Patient hmsPatient, String ihsNumber) {
        List<Identifier> identifiers = new ArrayList<>();

        // NIK identifier (required)
        if (hmsPatient.getNik() != null && !hmsPatient.getNik().isEmpty()) {
            identifiers.add(Identifier.createNik(hmsPatient.getNik()));
        }

        // IHS number identifier
        if (ihsNumber != null && !ihsNumber.isEmpty()) {
            identifiers.add(Identifier.createIhsNumber(ihsNumber));
        }

        // Medical Record Number identifier
        if (hmsPatient.getMrn() != null && !hmsPatient.getMrn().isEmpty()) {
            identifiers.add(Identifier.createMedicalRecordNumber(hmsPatient.getMrn()));
        }

        return identifiers;
    }

    /**
     * Build FHIR names from HMS patient.
     */
    private List<HumanName> buildNames(com.yudha.hms.patient.entity.Patient hmsPatient) {
        List<HumanName> names = new ArrayList<>();

        if (hmsPatient.getFullName() != null && !hmsPatient.getFullName().isEmpty()) {
            // Split full name into family and given names
            String[] nameParts = hmsPatient.getFullName().trim().split("\\s+");
            String familyName = nameParts[nameParts.length - 1]; // Last word as family name
            List<String> givenNames = Arrays.asList(nameParts).subList(0, nameParts.length - 1);

            names.add(HumanName.createOfficial(
                hmsPatient.getFullName(),
                familyName,
                givenNames.isEmpty() ? null : givenNames
            ));
        }

        return names;
    }

    /**
     * Build FHIR telecom (contact points) from HMS patient.
     */
    private List<ContactPoint> buildTelecom(com.yudha.hms.patient.entity.Patient hmsPatient) {
        List<ContactPoint> telecom = new ArrayList<>();

        // Primary phone
        if (hmsPatient.getPhonePrimary() != null && !hmsPatient.getPhonePrimary().isEmpty()) {
            telecom.add(ContactPoint.createMobilePhone(hmsPatient.getPhonePrimary()));
        }

        // Secondary phone
        if (hmsPatient.getPhoneSecondary() != null && !hmsPatient.getPhoneSecondary().isEmpty()) {
            telecom.add(ContactPoint.createHomePhone(hmsPatient.getPhoneSecondary()));
        }

        // Email
        if (hmsPatient.getEmail() != null && !hmsPatient.getEmail().isEmpty()) {
            telecom.add(ContactPoint.createEmail(hmsPatient.getEmail()));
        }

        return telecom;
    }

    /**
     * Build FHIR addresses from HMS patient addresses.
     */
    private List<Address> buildAddresses(List<PatientAddress> hmsAddresses) {
        return hmsAddresses.stream()
            .map(this::buildAddress)
            .collect(Collectors.toList());
    }

    /**
     * Build single FHIR address from HMS patient address.
     */
    private Address buildAddress(PatientAddress hmsAddress) {
        // Build street lines
        List<String> lines = new ArrayList<>();
        if (hmsAddress.getAddressLine1() != null) {
            lines.add(hmsAddress.getAddressLine1());
        }
        if (hmsAddress.getAddressLine2() != null && !hmsAddress.getAddressLine2().isEmpty()) {
            lines.add(hmsAddress.getAddressLine2());
        }
        if (hmsAddress.getRt() != null && hmsAddress.getRw() != null) {
            lines.add("RT " + hmsAddress.getRt() + "/RW " + hmsAddress.getRw());
        }

        // Get administrative division names and codes
        // TODO: Lookup actual names and codes from master data
        String provinceCode = lookupProvinceCode(hmsAddress.getProvinceId());
        String cityCode = lookupCityCode(hmsAddress.getCityId());
        String districtCode = lookupDistrictCode(hmsAddress.getDistrictId());
        String villageCode = lookupVillageCode(hmsAddress.getVillageId());

        String provinceName = lookupProvinceName(hmsAddress.getProvinceId());
        String cityName = lookupCityName(hmsAddress.getCityId());
        String districtName = lookupDistrictName(hmsAddress.getDistrictId());

        return Address.createHomeAddress(
            hmsAddress.getFullAddress(),
            lines,
            cityName,
            districtName,
            provinceName,
            hmsAddress.getPostalCode(),
            provinceCode,
            cityCode,
            districtCode,
            villageCode
        );
    }

    /**
     * Map HMS Gender to FHIR gender code.
     */
    private String mapGender(Gender gender) {
        if (gender == null) return "unknown";
        return switch (gender) {
            case MALE -> "male";
            case FEMALE -> "female";
        };
    }

    /**
     * Build marital status CodeableConcept.
     */
    private CodeableConcept buildMaritalStatus(java.util.UUID maritalStatusId) {
        // TODO: Lookup actual marital status from master data
        // For now, return default
        String code = lookupMaritalStatusCode(maritalStatusId);
        String display = lookupMaritalStatusDisplay(maritalStatusId);

        if (code != null && display != null) {
            return CodeableConcept.createMaritalStatus(code, display);
        }

        return null;
    }

    /**
     * Build religion extension.
     */
    private Extension buildReligionExtension(java.util.UUID religionId) {
        // TODO: Lookup actual religion from master data
        String code = lookupReligionCode(religionId);
        String display = lookupReligionDisplay(religionId);

        if (code != null && display != null) {
            return Extension.createReligion(code, display);
        }

        return null;
    }

    /**
     * Build blood type extension.
     */
    private Extension buildBloodTypeExtension(java.util.UUID bloodTypeId) {
        // TODO: Lookup actual blood type from master data
        String code = lookupBloodTypeCode(bloodTypeId);
        String display = lookupBloodTypeDisplay(bloodTypeId);

        if (code != null && display != null) {
            return Extension.createBloodType(code, display);
        }

        return null;
    }

    // ========================================================================
    // MASTER DATA LOOKUP METHODS (TODO: Implement with actual repositories)
    // ========================================================================

    private String lookupProvinceCode(java.util.UUID provinceId) {
        // TODO: Implement with ProvinceRepository
        return provinceId != null ? "31" : null; // Example: DKI Jakarta
    }

    private String lookupProvinceName(java.util.UUID provinceId) {
        // TODO: Implement with ProvinceRepository
        return provinceId != null ? "DKI JAKARTA" : null;
    }

    private String lookupCityCode(java.util.UUID cityId) {
        // TODO: Implement with CityRepository
        return cityId != null ? "3171" : null; // Example: Jakarta Selatan
    }

    private String lookupCityName(java.util.UUID cityId) {
        // TODO: Implement with CityRepository
        return cityId != null ? "JAKARTA SELATAN" : null;
    }

    private String lookupDistrictCode(java.util.UUID districtId) {
        // TODO: Implement with DistrictRepository
        return districtId != null ? "317101" : null;
    }

    private String lookupDistrictName(java.util.UUID districtId) {
        // TODO: Implement with DistrictRepository
        return districtId != null ? "KEBAYORAN BARU" : null;
    }

    private String lookupVillageCode(java.util.UUID villageId) {
        // TODO: Implement with VillageRepository
        return villageId != null ? "3171011001" : null;
    }

    private String lookupReligionCode(java.util.UUID religionId) {
        // TODO: Implement with ReligionRepository
        // Mapping: Islam=1, Kristen=2, Katolik=3, Hindu=4, Buddha=5, Konghucu=6, Kepercayaan=7
        return "1"; // Example: Islam
    }

    private String lookupReligionDisplay(java.util.UUID religionId) {
        // TODO: Implement with ReligionRepository
        return "Islam";
    }

    private String lookupMaritalStatusCode(java.util.UUID maritalStatusId) {
        // TODO: Implement with MaritalStatusRepository
        // FHIR codes: S=Never Married, M=Married, D=Divorced, W=Widowed
        return "M"; // Example: Married
    }

    private String lookupMaritalStatusDisplay(java.util.UUID maritalStatusId) {
        // TODO: Implement with MaritalStatusRepository
        return "Married";
    }

    private String lookupBloodTypeCode(java.util.UUID bloodTypeId) {
        // TODO: Implement with BloodTypeRepository
        // Codes: A+, A-, B+, B-, AB+, AB-, O+, O-
        return "A";
    }

    private String lookupBloodTypeDisplay(java.util.UUID bloodTypeId) {
        // TODO: Implement with BloodTypeRepository
        return "A";
    }

    private String mapNationalityToCode(String nationality) {
        // TODO: Implement comprehensive nationality mapping
        // For now, return ID for Indonesian, others need mapping
        return switch (nationality.toLowerCase()) {
            case "indonesian", "indonesia" -> "ID";
            case "singaporean", "singapore" -> "SG";
            case "malaysian", "malaysia" -> "MY";
            default -> "ID";
        };
    }
}
