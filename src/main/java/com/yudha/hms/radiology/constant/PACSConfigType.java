package com.yudha.hms.radiology.constant;

import lombok.Getter;

/**
 * PACS Configuration Type Enum.
 *
 * Represents the type of PACS configuration.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-11-22
 */
@Getter
public enum PACSConfigType {

    LOCAL_PACS("Local PACS", "PACS Lokal"),
    CLOUD_PACS("Cloud PACS", "PACS Cloud"),
    HYBRID("Hybrid PACS", "PACS Hybrid"),
    MODALITY_WORKLIST("Modality Worklist", "Worklist Modalitas"),
    STORAGE_NODE("Storage Node", "Node Penyimpanan"),
    VIEWER("DICOM Viewer", "Penampil DICOM");

    private final String englishName;
    private final String indonesianName;

    PACSConfigType(String englishName, String indonesianName) {
        this.englishName = englishName;
        this.indonesianName = indonesianName;
    }
}
