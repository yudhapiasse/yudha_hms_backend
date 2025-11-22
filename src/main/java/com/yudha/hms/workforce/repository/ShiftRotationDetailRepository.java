package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.entity.ShiftRotationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShiftRotationDetailRepository extends JpaRepository<ShiftRotationDetail, UUID> {

    List<ShiftRotationDetail> findByRotationIdOrderByDaySequence(UUID rotationId);

    Optional<ShiftRotationDetail> findByRotationIdAndDaySequence(UUID rotationId, Integer daySequence);

    @Query("SELECT srd FROM ShiftRotationDetail srd WHERE srd.rotationId = :rotationId ORDER BY srd.daySequence")
    List<ShiftRotationDetail> findRotationPattern(@Param("rotationId") UUID rotationId);

    void deleteByRotationId(UUID rotationId);
}
