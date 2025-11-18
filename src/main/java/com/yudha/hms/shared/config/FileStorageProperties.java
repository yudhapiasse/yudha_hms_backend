package com.yudha.hms.shared.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * File Storage Configuration Properties.
 *
 * Binds file storage configuration from application.yml.
 * Configures upload directories, file size limits, and image settings.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Configuration
@ConfigurationProperties(prefix = "hms.file-storage")
@Data
public class FileStorageProperties {

    /**
     * Base upload directory
     * Default: ./uploads
     */
    private String uploadDir = "./uploads";

    /**
     * Patient photos directory
     * Default: ./uploads/patient-photos
     */
    private String patientPhotosDir = "./uploads/patient-photos";

    /**
     * Thumbnails directory
     * Default: ./uploads/patient-photos/thumbnails
     */
    private String thumbnailsDir = "./uploads/patient-photos/thumbnails";

    /**
     * Maximum file size in bytes
     * Default: 5MB (5242880 bytes)
     */
    private long maxFileSize = 5242880L; // 5MB

    /**
     * Allowed file extensions (comma-separated)
     * Default: jpg,jpeg,png,gif,webp
     */
    private String allowedExtensions = "jpg,jpeg,png,gif,webp";

    /**
     * Thumbnail width in pixels
     * Default: 150
     */
    private int thumbnailWidth = 150;

    /**
     * Thumbnail height in pixels
     * Default: 150
     */
    private int thumbnailHeight = 150;

    /**
     * Default avatar URL
     * Default: /api/files/default-avatar.png
     */
    private String defaultAvatarUrl = "/api/files/default-avatar.png";

    /**
     * Get list of allowed extensions
     *
     * @return list of allowed file extensions
     */
    public List<String> getAllowedExtensionsList() {
        return Arrays.asList(allowedExtensions.split(","));
    }

    /**
     * Check if file extension is allowed
     *
     * @param extension file extension to check
     * @return true if allowed, false otherwise
     */
    public boolean isAllowedExtension(String extension) {
        return getAllowedExtensionsList().contains(extension.toLowerCase());
    }
}