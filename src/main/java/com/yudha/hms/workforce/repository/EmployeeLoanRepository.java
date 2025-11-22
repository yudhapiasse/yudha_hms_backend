package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.LoanStatus;
import com.yudha.hms.workforce.constant.LoanType;
import com.yudha.hms.workforce.entity.EmployeeLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeLoanRepository extends JpaRepository<EmployeeLoan, UUID> {

    Optional<EmployeeLoan> findByLoanNumber(String loanNumber);

    List<EmployeeLoan> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);

    List<EmployeeLoan> findByEmployeeIdAndStatusOrderByCreatedAtDesc(UUID employeeId, LoanStatus status);

    List<EmployeeLoan> findByStatusOrderByCreatedAtDesc(LoanStatus status);

    List<EmployeeLoan> findByLoanTypeAndStatusOrderByCreatedAtDesc(LoanType loanType, LoanStatus status);

    @Query("SELECT l FROM EmployeeLoan l WHERE l.employeeId = :employeeId AND l.status = :status")
    List<EmployeeLoan> findActiveByEmployee(@Param("employeeId") UUID employeeId, @Param("status") LoanStatus status);

    @Query("SELECT SUM(l.outstandingAmount) FROM EmployeeLoan l WHERE l.employeeId = :employeeId AND l.status = :status")
    Double getTotalOutstandingByEmployee(@Param("employeeId") UUID employeeId, @Param("status") LoanStatus status);

    @Query("SELECT COUNT(l) FROM EmployeeLoan l WHERE l.employeeId = :employeeId AND l.status = 'ACTIVE'")
    Long countActiveByEmployee(@Param("employeeId") UUID employeeId);
}
