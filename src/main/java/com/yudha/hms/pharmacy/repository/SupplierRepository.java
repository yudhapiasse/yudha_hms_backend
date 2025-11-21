package com.yudha.hms.pharmacy.repository;

import com.yudha.hms.pharmacy.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID>,
        JpaSpecificationExecutor<Supplier> {
    
    Optional<Supplier> findByCode(String code);
    List<Supplier> findByActiveOrderByName(Boolean active);
    List<Supplier> findByIsPreferredOrderByName(Boolean isPreferred);
    
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND s.active = true AND s.deletedAt IS NULL ORDER BY s.name")
    List<Supplier> searchByName(@Param("name") String name);
    
    boolean existsByCode(String code);
}
