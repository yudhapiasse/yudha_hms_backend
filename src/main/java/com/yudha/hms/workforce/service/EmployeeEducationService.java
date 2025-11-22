package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.EducationLevel;
import com.yudha.hms.workforce.entity.EmployeeEducation;
import com.yudha.hms.workforce.repository.EmployeeEducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeEducationService {

    private final EmployeeEducationRepository employeeEducationRepository;

    @Transactional(readOnly = true)
    public EmployeeEducation getEducationById(UUID id) {
        return employeeEducationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education record not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeEducation> getEducationByEmployee(UUID employeeId) {
        return employeeEducationRepository.findByEmployeeIdOrderByGraduationYearDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public EmployeeEducation getHighestEducation(UUID employeeId) {
        return employeeEducationRepository.findByEmployeeIdAndIsHighestEducationTrue(employeeId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<EmployeeEducation> getEducationByLevel(EducationLevel educationLevel) {
        return employeeEducationRepository.findByEducationLevel(educationLevel);
    }

    @Transactional(readOnly = true)
    public List<EmployeeEducation> searchByInstitutionOrField(String keyword) {
        return employeeEducationRepository.searchByInstitutionOrField(keyword);
    }

    @Transactional
    public EmployeeEducation createEducation(EmployeeEducation education) {
        if (education.getIsHighestEducation()) {
            clearHighestEducationFlag(education.getEmployeeId());
        }
        return employeeEducationRepository.save(education);
    }

    @Transactional
    public EmployeeEducation updateEducation(UUID id, EmployeeEducation educationDetails) {
        EmployeeEducation education = getEducationById(id);

        if (educationDetails.getIsHighestEducation() && !education.getIsHighestEducation()) {
            clearHighestEducationFlag(education.getEmployeeId());
        }

        education.setEducationLevel(educationDetails.getEducationLevel());
        education.setInstitutionName(educationDetails.getInstitutionName());
        education.setMajorFieldOfStudy(educationDetails.getMajorFieldOfStudy());
        education.setDegreeTitle(educationDetails.getDegreeTitle());
        education.setStartYear(educationDetails.getStartYear());
        education.setGraduationYear(educationDetails.getGraduationYear());
        education.setGpa(educationDetails.getGpa());
        education.setGpaScale(educationDetails.getGpaScale());
        education.setCountry(educationDetails.getCountry());
        education.setCity(educationDetails.getCity());
        education.setCertificateNumber(educationDetails.getCertificateNumber());
        education.setCertificateUrl(educationDetails.getCertificateUrl());
        education.setIsHighestEducation(educationDetails.getIsHighestEducation());
        education.setIsVerified(educationDetails.getIsVerified());
        education.setNotes(educationDetails.getNotes());

        return employeeEducationRepository.save(education);
    }

    @Transactional
    public void setAsHighestEducation(UUID id) {
        EmployeeEducation education = getEducationById(id);
        clearHighestEducationFlag(education.getEmployeeId());
        education.setIsHighestEducation(true);
        employeeEducationRepository.save(education);
    }

    @Transactional
    public void deleteEducation(UUID id) {
        employeeEducationRepository.deleteById(id);
    }

    private void clearHighestEducationFlag(UUID employeeId) {
        List<EmployeeEducation> educations = employeeEducationRepository.findByEmployeeId(employeeId);
        for (EmployeeEducation edu : educations) {
            if (edu.getIsHighestEducation()) {
                edu.setIsHighestEducation(false);
                employeeEducationRepository.save(edu);
            }
        }
    }
}
