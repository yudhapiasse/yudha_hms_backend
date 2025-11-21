package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.AdjustmentReason;
import com.yudha.hms.pharmacy.entity.StockAdjustment;
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
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, UUID>,
        JpaSpecificationExecutor<StockAdjustment> {

    Optional<StockAdjustment> findByAdjustmentNumber(String adjustmentNumber);

    List<StockAdjustment> findByLocationIdOrderByAdjustmentDateDesc(UUID locationId);

    List<StockAdjustment> findByReasonOrderByAdjustmentDateDesc(AdjustmentReason reason);

    @Query("SELECT a FROM StockAdjustment a WHERE a.isApproved = false " +
           "AND a.active = true ORDER BY a.adjustmentDate ASC")
    List<StockAdjustment> findPendingApproval();

    @Query("SELECT a FROM StockAdjustment a WHERE a.adjustmentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY a.adjustmentDate DESC")
    List<StockAdjustment> findByDateRange(@Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);
}
