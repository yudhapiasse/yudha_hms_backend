package com.yudha.hms.patient.controller;

import com.yudha.hms.patient.dto.PhotoUploadResponse;
import com.yudha.hms.patient.service.PatientService;
import com.yudha.hms.shared.dto.ApiResponse;
import com.yudha.hms.shared.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Patient Photo Management Controller.
 *
 * RESTful endpoints for patient photo operations:
 * - Photo upload with validation
 * - Photo retrieval with caching
 * - Thumbnail retrieval
 * - Photo deletion (GDPR-compliant)
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientPhotoController {

    private final PatientService patientService;
    private final FileStorageService fileStorageService;

    /**
     * Upload patient photo.
     *
     * POST /api/patients/{id}/photo
     *
     * Features:
     * - File size validation (max 5MB)
     * - File type validation (jpg, jpeg, png, gif, webp)
     * - Automatic thumbnail generation
     * - Old photo replacement
     *
     * @param id patient UUID
     * @param file photo file
     * @return upload response with URLs
     */
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PhotoUploadResponse>> uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        log.info("POST /api/patients/{}/photo - Uploading photo: {}",
            id, file.getOriginalFilename());

        PhotoUploadResponse response = patientService.uploadPatientPhoto(id, file);

        return ResponseEntity.ok(
            ApiResponse.success("Foto pasien berhasil diunggah", response)
        );
    }

    /**
     * Get patient photo.
     *
     * GET /api/patients/photos/{filename}
     *
     * Features:
     * - Returns image file
     * - Aggressive caching (30 days)
     * - Content-Type based on file extension
     *
     * @param filename photo filename
     * @return photo file resource
     */
    @GetMapping("/photos/{filename:.+}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {
        log.debug("GET /api/patients/photos/{} - Retrieving photo", filename);

        Resource resource = fileStorageService.loadFileAsResource(filename, false);

        // Determine content type
        String contentType = determineContentType(filename);

        // Build response with caching headers
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + filename + "\"")
            .body(resource);
    }

    /**
     * Get patient photo thumbnail.
     *
     * GET /api/patients/photos/thumbnails/{filename}
     *
     * Features:
     * - Returns thumbnail image (150x150)
     * - Aggressive caching (30 days)
     * - Faster loading for lists and grids
     *
     * @param filename thumbnail filename
     * @return thumbnail file resource
     */
    @GetMapping("/photos/thumbnails/{filename:.+}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable String filename) {
        log.debug("GET /api/patients/photos/thumbnails/{} - Retrieving thumbnail", filename);

        Resource resource = fileStorageService.loadFileAsResource(filename, true);

        String contentType = determineContentType(filename);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic())
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"thumb_" + filename + "\"")
            .body(resource);
    }

    /**
     * Delete patient photo (GDPR-compliant).
     *
     * DELETE /api/patients/{id}/photo
     *
     * Features:
     * - Deletes original photo
     * - Deletes thumbnail
     * - Updates patient record
     * - Complies with GDPR right to erasure
     *
     * @param id patient UUID
     * @return success message
     */
    @DeleteMapping("/{id}/photo")
    public ResponseEntity<ApiResponse<Void>> deletePhoto(@PathVariable UUID id) {
        log.info("DELETE /api/patients/{}/photo - Deleting photo", id);

        patientService.deletePatientPhoto(id);

        return ResponseEntity.ok(
            ApiResponse.success("Foto pasien berhasil dihapus", null)
        );
    }

    /**
     * Get patient photo URL (or default avatar).
     *
     * GET /api/patients/{id}/photo-url
     *
     * Returns the photo URL or fallback to default avatar if no photo exists.
     *
     * @param id patient UUID
     * @return photo URL
     */
    @GetMapping("/{id}/photo-url")
    public ResponseEntity<ApiResponse<String>> getPhotoUrl(@PathVariable UUID id) {
        log.debug("GET /api/patients/{}/photo-url - Getting photo URL", id);

        String photoUrl = patientService.getPatientPhotoUrl(id);

        return ResponseEntity.ok(
            ApiResponse.success("URL foto berhasil diambil", photoUrl)
        );
    }

    /**
     * Determine content type from filename extension.
     *
     * @param filename file name
     * @return MIME type
     */
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}
