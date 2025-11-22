package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.FamilyRelationship;
import com.yudha.hms.workforce.entity.EmployeeFamily;
import com.yudha.hms.workforce.repository.EmployeeFamilyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeFamilyService {

    private final EmployeeFamilyRepository employeeFamilyRepository;

    @Transactional(readOnly = true)
    public EmployeeFamily getFamilyMemberById(UUID id) {
        return employeeFamilyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Family member not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeFamily> getFamilyMembersByEmployee(UUID employeeId) {
        return employeeFamilyRepository.findByEmployeeId(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeFamily> getFamilyMembersByRelationship(UUID employeeId, FamilyRelationship relationship) {
        return employeeFamilyRepository.findByEmployeeIdAndRelationship(employeeId, relationship);
    }

    @Transactional(readOnly = true)
    public List<EmployeeFamily> getDependents(UUID employeeId) {
        return employeeFamilyRepository.findByEmployeeIdAndIsDependentTrue(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeFamily> getEmergencyContacts(UUID employeeId) {
        return employeeFamilyRepository.findByEmployeeIdAndIsEmergencyContactTrue(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeFamily> getBpjsCoveredMembers(UUID employeeId) {
        return employeeFamilyRepository.findByEmployeeIdAndCoveredByHealthInsuranceTrue(employeeId);
    }

    @Transactional(readOnly = true)
    public Long countBpjsCoveredMembers(UUID employeeId) {
        return employeeFamilyRepository.countBpjsCoveredMembers(employeeId);
    }

    @Transactional
    public EmployeeFamily createFamilyMember(EmployeeFamily familyMember) {
        return employeeFamilyRepository.save(familyMember);
    }

    @Transactional
    public EmployeeFamily updateFamilyMember(UUID id, EmployeeFamily familyMemberDetails) {
        EmployeeFamily familyMember = getFamilyMemberById(id);

        familyMember.setRelationship(familyMemberDetails.getRelationship());
        familyMember.setFullName(familyMemberDetails.getFullName());
        familyMember.setNik(familyMemberDetails.getNik());
        familyMember.setGender(familyMemberDetails.getGender());
        familyMember.setPlaceOfBirth(familyMemberDetails.getPlaceOfBirth());
        familyMember.setDateOfBirth(familyMemberDetails.getDateOfBirth());
        familyMember.setOccupation(familyMemberDetails.getOccupation());
        familyMember.setPhoneNumber(familyMemberDetails.getPhoneNumber());
        familyMember.setAddress(familyMemberDetails.getAddress());
        familyMember.setIsDependent(familyMemberDetails.getIsDependent());
        familyMember.setIsEmergencyContact(familyMemberDetails.getIsEmergencyContact());
        familyMember.setCoveredByHealthInsurance(familyMemberDetails.getCoveredByHealthInsurance());
        familyMember.setBpjsNumber(familyMemberDetails.getBpjsNumber());
        familyMember.setNotes(familyMemberDetails.getNotes());

        return employeeFamilyRepository.save(familyMember);
    }

    @Transactional
    public void deleteFamilyMember(UUID id) {
        employeeFamilyRepository.deleteById(id);
    }
}
