package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.constant.CDRequestStatus;
import com.yudha.hms.radiology.constant.CDRequestType;
import com.yudha.hms.radiology.constant.OrderPriority;
import com.yudha.hms.radiology.entity.CDBurningRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for CDBurningRequest entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Repository
public interface CDBurningRequestRepository extends JpaRepository<CDBurningRequest, UUID> {

    Optional<CDBurningRequest> findByRequestNumber(String requestNumber);

    List<CDBurningRequest> findByPatientIdAndDeletedAtIsNull(UUID patientId);

    Page<CDBurningRequest> findByPatientIdAndDeletedAtIsNull(UUID patientId, Pageable pageable);

    List<CDBurningRequest> findByStatusAndDeletedAtIsNull(CDRequestStatus status);

    List<CDBurningRequest> findByRequestTypeAndDeletedAtIsNull(CDRequestType requestType);

    @Query("SELECT c FROM CDBurningRequest c WHERE c.status IN :statuses AND c.deletedAt IS NULL ORDER BY c.priority DESC, c.createdAt ASC")
    List<CDBurningRequest> findPendingRequests(@Param("statuses") List<CDRequestStatus> statuses);

    @Query("SELECT c FROM CDBurningRequest c WHERE c.assignedTo = :userId AND c.status IN ('QUEUED', 'PROCESSING', 'BURNING') AND c.deletedAt IS NULL")
    List<CDBurningRequest> findActiveRequestsByUser(@Param("userId") UUID userId);

    @Query("SELECT c FROM CDBurningRequest c WHERE c.requestedBy = :userId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<CDBurningRequest> findByRequestedBy(@Param("userId") UUID userId);

    @Query("SELECT c FROM CDBurningRequest c WHERE c.priority = :priority AND c.status NOT IN ('COMPLETED', 'CANCELLED', 'DELIVERED') AND c.deletedAt IS NULL")
    List<CDBurningRequest> findByPriorityAndNotCompleted(@Param("priority") OrderPriority priority);

    @Query("SELECT c FROM CDBurningRequest c WHERE c.status = 'COMPLETED' AND c.burnedAt BETWEEN :startDate AND :endDate AND c.deletedAt IS NULL")
    List<CDBurningRequest> findCompletedInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT c FROM CDBurningRequest c WHERE c.failed = true AND c.retryCount < :maxRetries AND c.deletedAt IS NULL")
    List<CDBurningRequest> findFailedRequestsForRetry(@Param("maxRetries") Integer maxRetries);

    Optional<CDBurningRequest> findTopByOrderByCreatedAtDesc();

    long countByStatusAndDeletedAtIsNull(CDRequestStatus status);

    long countByRequestTypeAndDeletedAtIsNull(CDRequestType requestType);
}
