package com.yudha.hms.workforce.repository;

import com.yudha.hms.workforce.constant.PayrollComponentType;
import com.yudha.hms.workforce.entity.PayrollComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PayrollComponentRepository extends JpaRepository<PayrollComponent, UUID> {

    Optional<PayrollComponent> findByComponentCode(String componentCode);

    List<PayrollComponent> findByComponentTypeAndActiveTrueOrderByPriorityOrder(PayrollComponentType componentType);

    List<PayrollComponent> findByActiveTrueOrderByComponentType();

    List<PayrollComponent> findByIsTaxableTrueAndActiveTrueOrderByPriorityOrder();

    List<PayrollComponent> findByIsRecurringTrueAndActiveTrueOrderByPriorityOrder();

    @Query("SELECT p FROM PayrollComponent p WHERE p.componentType = :type AND p.isRecurring = true AND p.active = true ORDER BY p.priorityOrder")
    List<PayrollComponent> findRecurringByType(@Param("type") PayrollComponentType type);

    @Query("SELECT p FROM PayrollComponent p WHERE p.componentName LIKE %:keyword% OR p.componentCode LIKE %:keyword%")
    List<PayrollComponent> searchComponents(@Param("keyword") String keyword);
}
