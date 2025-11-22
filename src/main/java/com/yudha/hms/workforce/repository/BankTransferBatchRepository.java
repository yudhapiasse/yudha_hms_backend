package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.BankFileFormat;
import com.yudha.hms.workforce.constant.TransferBatchStatus;
import com.yudha.hms.workforce.entity.BankTransferBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankTransferBatchRepository extends JpaRepository<BankTransferBatch, UUID> {

    Optional<BankTransferBatch> findByBatchNumber(String batchNumber);

    List<BankTransferBatch> findByPayrollPeriodIdOrderByCreatedAtDesc(UUID payrollPeriodId);

    List<BankTransferBatch> findByStatusOrderByCreatedAtDesc(TransferBatchStatus status);

    List<BankTransferBatch> findByFileFormatAndStatusOrderByCreatedAtDesc(BankFileFormat fileFormat, TransferBatchStatus status);

    @Query("SELECT b FROM BankTransferBatch b WHERE b.payrollPeriodId = :periodId AND b.status = :status")
    List<BankTransferBatch> findByPeriodAndStatus(@Param("periodId") UUID periodId, @Param("status") TransferBatchStatus status);

    @Query("SELECT SUM(b.totalAmount) FROM BankTransferBatch b WHERE b.payrollPeriodId = :periodId")
    Double getTotalAmountByPeriod(@Param("periodId") UUID periodId);
}
