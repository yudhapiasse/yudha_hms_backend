package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.PaymentStatus;
import com.yudha.hms.workforce.entity.BankTransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankTransferItemRepository extends JpaRepository<BankTransferItem, UUID> {

    List<BankTransferItem> findByBankTransferBatchIdOrderBySequenceNumber(UUID bankTransferBatchId);

    List<BankTransferItem> findByEmployeePayrollId(UUID employeePayrollId);

    List<BankTransferItem> findByEmployeeId(UUID employeeId);

    @Query("SELECT b FROM BankTransferItem b WHERE b.bankTransferBatchId = :batchId AND b.status = :status ORDER BY b.sequenceNumber")
    List<BankTransferItem> findByBatchAndStatus(@Param("batchId") UUID batchId, @Param("status") PaymentStatus status);

    @Query("SELECT SUM(b.transferAmount) FROM BankTransferItem b WHERE b.bankTransferBatchId = :batchId")
    Double getTotalAmountByBatch(@Param("batchId") UUID batchId);

    @Query("SELECT COUNT(b) FROM BankTransferItem b WHERE b.bankTransferBatchId = :batchId AND b.status = :status")
    Long countByBatchAndStatus(@Param("batchId") UUID batchId, @Param("status") PaymentStatus status);
}
