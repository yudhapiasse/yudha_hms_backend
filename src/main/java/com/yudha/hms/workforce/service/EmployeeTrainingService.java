package com.yudha.hms.workforce.service;

import com.yudha.hms.workforce.constant.CompletionStatus;
import com.yudha.hms.workforce.constant.TrainingType;
import com.yudha.hms.workforce.entity.EmployeeTraining;
import com.yudha.hms.workforce.repository.EmployeeTrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeTrainingService {

    private final EmployeeTrainingRepository employeeTrainingRepository;

    @Transactional(readOnly = true)
    public EmployeeTraining getTrainingById(UUID id) {
        return employeeTrainingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Training record not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<EmployeeTraining> getTrainingByEmployee(UUID employeeId) {
        return employeeTrainingRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public List<EmployeeTraining> getTrainingByEmployeeAndStatus(UUID employeeId, CompletionStatus status) {
        return employeeTrainingRepository.findByEmployeeIdAndCompletionStatus(employeeId, status);
    }

    @Transactional(readOnly = true)
    public List<EmployeeTraining> getTrainingByType(TrainingType trainingType) {
        return employeeTrainingRepository.findByTrainingType(trainingType);
    }

    @Transactional(readOnly = true)
    public List<EmployeeTraining> getTrainingByStatus(CompletionStatus status) {
        return employeeTrainingRepository.findByCompletionStatus(status);
    }

    @Transactional(readOnly = true)
    public List<EmployeeTraining> getTrainingByDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return employeeTrainingRepository.findByEmployeeAndDateRange(employeeId, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<EmployeeTraining> getCertificatesExpiringSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(daysAhead);
        return employeeTrainingRepository.findCertificatesExpiringBetween(today, futureDate);
    }

    @Transactional(readOnly = true)
    public Long countCompletedTrainings(UUID employeeId) {
        return employeeTrainingRepository.countCompletedTrainingsByEmployee(employeeId);
    }

    @Transactional
    public EmployeeTraining createTraining(EmployeeTraining training) {
        return employeeTrainingRepository.save(training);
    }

    @Transactional
    public EmployeeTraining updateTraining(UUID id, EmployeeTraining trainingDetails) {
        EmployeeTraining training = getTrainingById(id);

        training.setTrainingType(trainingDetails.getTrainingType());
        training.setTrainingName(trainingDetails.getTrainingName());
        training.setTrainingProvider(trainingDetails.getTrainingProvider());
        training.setTrainingCategory(trainingDetails.getTrainingCategory());
        training.setStartDate(trainingDetails.getStartDate());
        training.setEndDate(trainingDetails.getEndDate());
        training.setDurationHours(trainingDetails.getDurationHours());
        training.setLocation(trainingDetails.getLocation());
        training.setIssuesCertificate(trainingDetails.getIssuesCertificate());
        training.setCertificateNumber(trainingDetails.getCertificateNumber());
        training.setCertificateUrl(trainingDetails.getCertificateUrl());
        training.setCertificateExpiryDate(trainingDetails.getCertificateExpiryDate());
        training.setIsMandatory(trainingDetails.getIsMandatory());
        training.setIsRegulatoryRequired(trainingDetails.getIsRegulatoryRequired());
        training.setTrainingCost(trainingDetails.getTrainingCost());
        training.setCurrency(trainingDetails.getCurrency());
        training.setPaidBy(trainingDetails.getPaidBy());
        training.setAttended(trainingDetails.getAttended());
        training.setCompletionStatus(trainingDetails.getCompletionStatus());
        training.setEvaluationScore(trainingDetails.getEvaluationScore());
        training.setPassFailStatus(trainingDetails.getPassFailStatus());
        training.setNotes(trainingDetails.getNotes());

        return employeeTrainingRepository.save(training);
    }

    @Transactional
    public void markAsCompleted(UUID id) {
        EmployeeTraining training = getTrainingById(id);
        training.setCompletionStatus(CompletionStatus.COMPLETED);
        employeeTrainingRepository.save(training);
    }

    @Transactional
    public void markAsCancelled(UUID id) {
        EmployeeTraining training = getTrainingById(id);
        training.setCompletionStatus(CompletionStatus.CANCELLED);
        employeeTrainingRepository.save(training);
    }

    @Transactional
    public void deleteTraining(UUID id) {
        employeeTrainingRepository.deleteById(id);
    }
}
