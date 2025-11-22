package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.PositionLevel;
import com.yudha.hms.workforce.entity.EmployeePosition;
import com.yudha.hms.workforce.repository.EmployeePositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeePositionService {

    private final EmployeePositionRepository employeePositionRepository;

    @Transactional(readOnly = true)
    public EmployeePosition getPositionById(UUID id) {
        return employeePositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Position not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public EmployeePosition getPositionByCode(String positionCode) {
        return employeePositionRepository.findByPositionCode(positionCode)
                .orElseThrow(() -> new RuntimeException("Position not found with code: " + positionCode));
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getAllActivePositions() {
        return employeePositionRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getPositionsByDepartment(UUID departmentId) {
        return employeePositionRepository.findByDepartmentIdAndIsActiveTrue(departmentId);
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getPositionsByLevel(PositionLevel positionLevel) {
        return employeePositionRepository.findByPositionLevel(positionLevel);
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getSubordinatePositions(UUID parentPositionId) {
        return employeePositionRepository.findByParentPositionId(parentPositionId);
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getPositionsRequiringSTR() {
        return employeePositionRepository.findByRequiresStrTrueAndIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getPositionsRequiringSIP() {
        return employeePositionRepository.findByRequiresSipTrueAndIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<EmployeePosition> getPositionsRequiringLicense() {
        return employeePositionRepository.findPositionsRequiringLicense();
    }

    @Transactional
    public EmployeePosition createPosition(EmployeePosition position) {
        position.setIsActive(true);
        return employeePositionRepository.save(position);
    }

    @Transactional
    public EmployeePosition updatePosition(UUID id, EmployeePosition positionDetails) {
        EmployeePosition position = getPositionById(id);

        position.setPositionName(positionDetails.getPositionName());
        position.setPositionNameId(positionDetails.getPositionNameId());
        position.setDepartmentId(positionDetails.getDepartmentId());
        position.setParentPositionId(positionDetails.getParentPositionId());
        position.setPositionLevel(positionDetails.getPositionLevel());
        position.setRequiresStr(positionDetails.getRequiresStr());
        position.setRequiresSip(positionDetails.getRequiresSip());
        position.setMinEducationLevel(positionDetails.getMinEducationLevel());
        position.setJobDescription(positionDetails.getJobDescription());
        position.setJobDescriptionId(positionDetails.getJobDescriptionId());

        return employeePositionRepository.save(position);
    }

    @Transactional
    public void deactivatePosition(UUID id) {
        EmployeePosition position = getPositionById(id);
        position.setIsActive(false);
        employeePositionRepository.save(position);
    }

    @Transactional
    public void activatePosition(UUID id) {
        EmployeePosition position = getPositionById(id);
        position.setIsActive(true);
        employeePositionRepository.save(position);
    }

    @Transactional
    public void deletePosition(UUID id) {
        employeePositionRepository.deleteById(id);
    }
}
