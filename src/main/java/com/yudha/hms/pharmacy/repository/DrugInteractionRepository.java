package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.constant.InteractionSeverity;
import com.yudha.hms.pharmacy.entity.Drug;
import com.yudha.hms.pharmacy.entity.DrugInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrugInteractionRepository extends JpaRepository<DrugInteraction, UUID>,
        JpaSpecificationExecutor<DrugInteraction> {
    
    @Query("SELECT i FROM DrugInteraction i WHERE (i.drug1 = :drug OR i.drug2 = :drug) " +
           "AND i.active = true ORDER BY i.severity")
    List<DrugInteraction> findByDrug(@Param("drug") Drug drug);
    
    @Query("SELECT i FROM DrugInteraction i WHERE " +
           "((i.drug1 = :drug1 AND i.drug2 = :drug2) OR (i.drug1 = :drug2 AND i.drug2 = :drug1)) " +
           "AND i.active = true")
    List<DrugInteraction> findByDrugPair(@Param("drug1") Drug drug1, @Param("drug2") Drug drug2);
    
    List<DrugInteraction> findBySeverityOrderByDrug1(InteractionSeverity severity);
    
    @Query("SELECT i FROM DrugInteraction i WHERE (i.drug1.id IN :drugIds OR i.drug2.id IN :drugIds) " +
           "AND i.active = true ORDER BY i.severity")
    List<DrugInteraction> findInteractionsByDrugList(@Param("drugIds") List<UUID> drugIds);
}
