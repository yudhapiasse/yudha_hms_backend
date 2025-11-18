package com.yudha.hms.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Photo Upload Response DTO.
 *
 * Response after successful photo upload.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUploadResponse {

    /**
     * Original photo URL
     */
    private String photoUrl;

    /**
     * Thumbnail photo URL
     */
    private String thumbnailUrl;

    /**
     * Original filename
     */
    private String originalFilename;

    /**
     * Stored filename
     */
    private String storedFilename;

    /**
     * File size in bytes
     */
    private long fileSizeBytes;

    /**
     * File size in human-readable format
     */
    private String fileSize;

    /**
     * Content type (MIME type)
     */
    private String contentType;

    /**
     * Upload timestamp
     */
    private java.time.LocalDateTime uploadedAt;

    /**
     * Format file size to human-readable string
     *
     * @param bytes file size in bytes
     * @return formatted file size (e.g., "2.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }
}