package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.entity.DrugCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DrugCategoryRepository extends JpaRepository<DrugCategory, UUID>,
        JpaSpecificationExecutor<DrugCategory> {
    Optional<DrugCategory> findByCode(String code);
    List<DrugCategory> findByActiveOrderByDisplayOrder(Boolean active);
    List<DrugCategory> findByParentOrderByDisplayOrder(DrugCategory parent);
    
    @Query("SELECT c FROM DrugCategory c WHERE c.parent IS NULL AND c.active = true " +
           "AND c.deletedAt IS NULL ORDER BY c.displayOrder")
    List<DrugCategory> findRootCategories();
    
    boolean existsByCode(String code);
}
