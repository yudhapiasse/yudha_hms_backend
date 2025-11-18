package com.yudha.hms.shared.service;

import com.yudha.hms.shared.config.FileStorageProperties;
import com.yudha.hms.shared.exception.FileStorageException;
import com.yudha.hms.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * File Storage Service.
 *
 * Handles file upload, download, and deletion operations.
 * Supports patient photos with validation and security checks.
 *
 * Features:
 * - Secure file name generation
 * - File type validation
 * - File size validation
 * - Directory creation and management
 * - GDPR-compliant file deletion
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    private Path patientPhotosLocation;
    private Path thumbnailsLocation;

    /**
     * Initialize storage directories
     */
    @PostConstruct
    public void init() {
        try {
            this.patientPhotosLocation = Paths.get(fileStorageProperties.getPatientPhotosDir())
                .toAbsolutePath().normalize();
            this.thumbnailsLocation = Paths.get(fileStorageProperties.getThumbnailsDir())
                .toAbsolutePath().normalize();

            // Create directories if they don't exist
            Files.createDirectories(this.patientPhotosLocation);
            Files.createDirectories(this.thumbnailsLocation);

            log.info("File storage initialized:");
            log.info("  - Patient photos: {}", this.patientPhotosLocation);
            log.info("  - Thumbnails: {}", this.thumbnailsLocation);

        } catch (Exception ex) {
            throw new FileStorageException("Could not create upload directories", ex);
        }
    }

    /**
     * Store patient photo file
     *
     * @param file multipart file to store
     * @param patientId patient UUID
     * @return stored file name
     * @throws FileStorageException if file storage fails
     */
    public String storePatientPhoto(MultipartFile file, UUID patientId) {
        // Validate file
        validateFile(file);

        // Generate secure filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(originalFilename);
        String filename = generatePatientPhotoFilename(patientId, extension);

        try {
            // Copy file to target location
            Path targetLocation = this.patientPhotosLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {} (size: {} bytes)", filename, file.getSize());
            return filename;

        } catch (IOException ex) {
            log.error("Failed to store file: {}", filename, ex);
            throw new FileStorageException("Failed to store file: " + filename, ex);
        }
    }

    /**
     * Store thumbnail file
     *
     * @param thumbnailBytes thumbnail image bytes
     * @param patientId patient UUID
     * @param extension file extension
     * @return stored thumbnail filename
     * @throws FileStorageException if storage fails
     */
    public String storeThumbnail(byte[] thumbnailBytes, UUID patientId, String extension) {
        String filename = generateThumbnailFilename(patientId, extension);

        try {
            Path targetLocation = this.thumbnailsLocation.resolve(filename);
            Files.write(targetLocation, thumbnailBytes);

            log.debug("Thumbnail stored successfully: {}", filename);
            return filename;

        } catch (IOException ex) {
            log.error("Failed to store thumbnail: {}", filename, ex);
            throw new FileStorageException("Failed to store thumbnail: " + filename, ex);
        }
    }

    /**
     * Load file as resource
     *
     * @param filename file name
     * @param isThumbnail whether to load from thumbnails directory
     * @return file resource
     * @throws ResourceNotFoundException if file not found
     */
    public Resource loadFileAsResource(String filename, boolean isThumbnail) {
        try {
            Path fileLocation = isThumbnail ? this.thumbnailsLocation : this.patientPhotosLocation;
            Path filePath = fileLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File", "filename", filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File", "filename", filename);
        }
    }

    /**
     * Delete patient photo and thumbnail (GDPR-compliant)
     *
     * @param photoUrl photo URL to delete
     * @return true if deleted successfully
     */
    public boolean deletePatientPhoto(String photoUrl) {
        if (photoUrl == null || photoUrl.isEmpty()) {
            return false;
        }

        try {
            // Extract filename from URL
            String filename = extractFilenameFromUrl(photoUrl);
            if (filename == null) {
                return false;
            }

            // Delete photo
            Path photoPath = this.patientPhotosLocation.resolve(filename);
            boolean photoDeleted = Files.deleteIfExists(photoPath);

            // Delete thumbnail
            Path thumbnailPath = this.thumbnailsLocation.resolve(filename);
            boolean thumbnailDeleted = Files.deleteIfExists(thumbnailPath);

            log.info("Photo deletion - Photo: {}, Thumbnail: {}", photoDeleted, thumbnailDeleted);
            return photoDeleted;

        } catch (IOException ex) {
            log.error("Failed to delete patient photo: {}", photoUrl, ex);
            return false;
        }
    }

    /**
     * Validate uploaded file
     *
     * @param file file to validate
     * @throws FileStorageException if validation fails
     */
    private void validateFile(MultipartFile file) {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot store empty file");
        }

        // Check file size
        if (file.getSize() > fileStorageProperties.getMaxFileSize()) {
            throw new FileStorageException(
                String.format("File size exceeds maximum allowed size of %d bytes",
                    fileStorageProperties.getMaxFileSize())
            );
        }

        // Check file extension
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(filename).toLowerCase();

        if (!fileStorageProperties.isAllowedExtension(extension)) {
            throw new FileStorageException(
                String.format("File type '%s' is not allowed. Allowed types: %s",
                    extension, fileStorageProperties.getAllowedExtensions())
            );
        }

        // Check for path traversal attack
        if (filename.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + filename);
        }
    }

    /**
     * Generate secure filename for patient photo
     *
     * @param patientId patient UUID
     * @param extension file extension
     * @return generated filename
     */
    private String generatePatientPhotoFilename(UUID patientId, String extension) {
        return String.format("patient_%s.%s", patientId.toString(), extension);
    }

    /**
     * Generate filename for thumbnail
     *
     * @param patientId patient UUID
     * @param extension file extension
     * @return generated filename
     */
    private String generateThumbnailFilename(UUID patientId, String extension) {
        return String.format("patient_%s.%s", patientId.toString(), extension);
    }

    /**
     * Extract filename from URL
     *
     * @param url photo URL
     * @return extracted filename
     */
    private String extractFilenameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }

        // Extract filename from URL (e.g., "/api/patients/photos/patient_uuid.jpg" -> "patient_uuid.jpg")
        String[] parts = url.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }

    /**
     * Get patient photos directory path
     *
     * @return patient photos directory path
     */
    public Path getPatientPhotosLocation() {
        return patientPhotosLocation;
    }

    /**
     * Get thumbnails directory path
     *
     * @return thumbnails directory path
     */
    public Path getThumbnailsLocation() {
        return thumbnailsLocation;
    }
}