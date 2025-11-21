package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.TransferStatus;
import com.yudha.hms.pharmacy.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, UUID>,
        JpaSpecificationExecutor<StockTransfer> {

    Optional<StockTransfer> findByTransferNumber(String transferNumber);

    List<StockTransfer> findByStatusOrderByTransferDateDesc(TransferStatus status);

    List<StockTransfer> findByFromLocationIdOrderByTransferDateDesc(UUID fromLocationId);

    List<StockTransfer> findByToLocationIdOrderByTransferDateDesc(UUID toLocationId);

    @Query("SELECT t FROM StockTransfer t WHERE t.status = 'PENDING' " +
           "AND t.active = true ORDER BY t.transferDate ASC")
    List<StockTransfer> findPendingApproval();

    @Query("SELECT t FROM StockTransfer t WHERE t.status = 'IN_TRANSIT' " +
           "AND t.active = true ORDER BY t.sentAt ASC")
    List<StockTransfer> findInTransit();
}
