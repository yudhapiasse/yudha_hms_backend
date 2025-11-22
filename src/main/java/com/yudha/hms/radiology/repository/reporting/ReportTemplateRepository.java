package com.yudha.hms.radiology.repository.reporting;

import com.yudha.hms.radiology.constant.reporting.ReportTemplateType;
import com.yudha.hms.radiology.entity.reporting.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, UUID> {

    Optional<ReportTemplate> findByTemplateCode(String templateCode);

    List<ReportTemplate> findByTemplateTypeAndIsActiveTrue(ReportTemplateType templateType);

    List<ReportTemplate> findByModalityCodeAndIsActiveTrue(String modalityCode);

    List<ReportTemplate> findByModalityCodeAndTemplateTypeAndIsActiveTrue(
            String modalityCode,
            ReportTemplateType templateType
    );

    List<ReportTemplate> findByIsActiveTrueOrderByTemplateNameAsc();

    boolean existsByTemplateCode(String templateCode);
}
