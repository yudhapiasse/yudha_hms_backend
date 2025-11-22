package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.EmploymentStatus;
import com.yudha.hms.workforce.constant.EmploymentType;
import com.yudha.hms.workforce.entity.Employee;
import com.yudha.hms.workforce.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeByNumber(String employeeNumber) {
        return employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("Employee not found with number: " + employeeNumber));
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeByNik(String nik) {
        return employeeRepository.findByNik(nik)
                .orElseThrow(() -> new RuntimeException("Employee not found with NIK: " + nik));
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllActiveEmployees() {
        return employeeRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByDepartment(UUID departmentId) {
        return employeeRepository.findByDepartmentIdAndIsActiveTrue(departmentId);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByPosition(UUID positionId) {
        return employeeRepository.findByPositionIdAndIsActiveTrue(positionId);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByStatus(EmploymentStatus status) {
        return employeeRepository.findByEmploymentStatusAndIsActiveTrue(status);
    }

    @Transactional(readOnly = true)
    public List<Employee> getEmployeesByType(EmploymentType type) {
        return employeeRepository.findByEmploymentTypeAndIsActiveTrue(type);
    }

    @Transactional(readOnly = true)
    public List<Employee> searchEmployees(String keyword) {
        return employeeRepository.searchEmployees(keyword);
    }

    @Transactional(readOnly = true)
    public List<Employee> getContractsExpiringBetween(EmploymentStatus status, LocalDate startDate, LocalDate endDate) {
        return employeeRepository.findContractsExpiringBetween(status, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Employee> getContractsExpiringSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return employeeRepository.findContractsExpiringBetween(EmploymentStatus.CONTRACT, today, futureDate);
    }

    @Transactional(readOnly = true)
    public Long countActiveEmployeesByDepartment(UUID departmentId) {
        return employeeRepository.countActiveByDepartment(departmentId);
    }

    @Transactional(readOnly = true)
    public Long countEmployeesByStatus(EmploymentStatus status) {
        return employeeRepository.countByStatus(status);
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        employee.setIsActive(true);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(UUID id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);

        employee.setFullName(employeeDetails.getFullName());
        employee.setPlaceOfBirth(employeeDetails.getPlaceOfBirth());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        employee.setGender(employeeDetails.getGender());
        employee.setReligion(employeeDetails.getReligion());
        employee.setMaritalStatus(employeeDetails.getMaritalStatus());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setMobileNumber(employeeDetails.getMobileNumber());
        employee.setAddress(employeeDetails.getAddress());
        employee.setCity(employeeDetails.getCity());
        employee.setProvince(employeeDetails.getProvince());
        employee.setPostalCode(employeeDetails.getPostalCode());
        employee.setNpwp(employeeDetails.getNpwp());
        employee.setBpjsKesehatanNumber(employeeDetails.getBpjsKesehatanNumber());
        employee.setBpjsKetenagakerjaanNumber(employeeDetails.getBpjsKetenagakerjaanNumber());
        employee.setPositionId(employeeDetails.getPositionId());
        employee.setDepartmentId(employeeDetails.getDepartmentId());
        employee.setEmploymentStatus(employeeDetails.getEmploymentStatus());
        employee.setEmploymentType(employeeDetails.getEmploymentType());
        employee.setBankName(employeeDetails.getBankName());
        employee.setBankAccountNumber(employeeDetails.getBankAccountNumber());
        employee.setBankAccountName(employeeDetails.getBankAccountName());
        employee.setBaseSalary(employeeDetails.getBaseSalary());
        employee.setEmergencyContactName(employeeDetails.getEmergencyContactName());
        employee.setEmergencyContactRelationship(employeeDetails.getEmergencyContactRelationship());
        employee.setEmergencyContactPhone(employeeDetails.getEmergencyContactPhone());
        employee.setEmergencyContactAddress(employeeDetails.getEmergencyContactAddress());
        employee.setPhotoUrl(employeeDetails.getPhotoUrl());
        employee.setNotes(employeeDetails.getNotes());

        return employeeRepository.save(employee);
    }

    @Transactional
    public void promoteEmployeeToPermanent(UUID id, LocalDate permanentDate) {
        Employee employee = getEmployeeById(id);
        employee.setEmploymentStatus(EmploymentStatus.PERMANENT);
        employee.setPermanentDate(permanentDate);
        employee.setContractEndDate(null);
        employeeRepository.save(employee);
    }

    @Transactional
    public void terminateEmployee(UUID id, LocalDate resignationDate) {
        Employee employee = getEmployeeById(id);
        employee.setResignationDate(resignationDate);
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    @Transactional
    public void deactivateEmployee(UUID id) {
        Employee employee = getEmployeeById(id);
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    @Transactional
    public void reactivateEmployee(UUID id) {
        Employee employee = getEmployeeById(id);
        employee.setIsActive(true);
        employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        employeeRepository.deleteById(id);
    }
}
