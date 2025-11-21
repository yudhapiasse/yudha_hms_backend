package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.StockReceiptStatus;
import com.yudha.hms.pharmacy.entity.StockReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockReceiptRepository extends JpaRepository<StockReceipt, UUID>,
        JpaSpecificationExecutor<StockReceipt> {

    Optional<StockReceipt> findByReceiptNumber(String receiptNumber);

    List<StockReceipt> findByStatusOrderByReceiptDateDesc(StockReceiptStatus status);

    List<StockReceipt> findByLocationIdOrderByReceiptDateDesc(UUID locationId);

    @Query("SELECT r FROM StockReceipt r WHERE r.receiptDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.receiptDate DESC")
    List<StockReceipt> findByDateRange(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM StockReceipt r WHERE r.status = 'RECEIVED' " +
           "AND r.active = true ORDER BY r.receivedAt ASC")
    List<StockReceipt> findPendingInspection();

    boolean existsByReceiptNumber(String receiptNumber);
}
