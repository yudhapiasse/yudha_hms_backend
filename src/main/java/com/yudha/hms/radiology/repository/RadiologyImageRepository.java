package com.yudha.hms.radiology.repository;

import com.yudha.hms.radiology.entity.RadiologyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for RadiologyImage entity.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-21
 */
@Repository
public interface RadiologyImageRepository extends JpaRepository<RadiologyImage, UUID> {

    /**
     * Find by result
     */
    List<RadiologyImage> findByResultIdOrderByImageNumberAsc(UUID resultId);

    /**
     * Find by DICOM study UID
     */
    List<RadiologyImage> findByDicomStudyUid(String dicomStudyUid);

    /**
     * Find by DICOM series UID
     */
    List<RadiologyImage> findByDicomSeriesUid(String dicomSeriesUid);

    /**
     * Find by DICOM instance UID
     */
    Optional<RadiologyImage> findByDicomInstanceUid(String dicomInstanceUid);

    /**
     * Find key images for result
     */
    @Query("SELECT i FROM RadiologyImage i WHERE i.result.id = :resultId AND i.isKeyImage = true ORDER BY i.imageNumber ASC")
    List<RadiologyImage> findKeyImagesByResult(@Param("resultId") UUID resultId);

    /**
     * Count images by result
     */
    long countByResultId(UUID resultId);

    /**
     * Count key images by result
     */
    long countByResultIdAndIsKeyImageTrue(UUID resultId);
}
