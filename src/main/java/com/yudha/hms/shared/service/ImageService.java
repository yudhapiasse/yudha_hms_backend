package com.yudha.hms.shared.service;

import com.yudha.hms.shared.config.FileStorageProperties;
import com.yudha.hms.shared.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Image Processing Service.
 *
 * Handles image processing operations including:
 * - Thumbnail generation
 * - Image resizing
 * - Image format conversion
 * - Image optimization
 *
 * Uses Thumbnailator library for high-quality image processing.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final FileStorageProperties fileStorageProperties;

    /**
     * Generate thumbnail from uploaded image
     *
     * @param file multipart file containing image
     * @return thumbnail image as byte array
     * @throws FileStorageException if thumbnail generation fails
     */
    public byte[] generateThumbnail(MultipartFile file) {
        try {
            log.debug("Generating thumbnail for file: {}", file.getOriginalFilename());

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(file.getInputStream())
                .size(fileStorageProperties.getThumbnailWidth(),
                      fileStorageProperties.getThumbnailHeight())
                .keepAspectRatio(true)
                .outputQuality(0.9)
                .toOutputStream(outputStream);

            byte[] thumbnailBytes = outputStream.toByteArray();

            log.debug("Thumbnail generated successfully. Original size: {} bytes, Thumbnail size: {} bytes",
                file.getSize(), thumbnailBytes.length);

            return thumbnailBytes;

        } catch (IOException ex) {
            log.error("Failed to generate thumbnail for file: {}", file.getOriginalFilename(), ex);
            throw new FileStorageException("Failed to generate thumbnail", ex);
        }
    }

    /**
     * Generate thumbnail from image bytes
     *
     * @param imageBytes image data
     * @param width thumbnail width
     * @param height thumbnail height
     * @return thumbnail image as byte array
     * @throws FileStorageException if thumbnail generation fails
     */
    public byte[] generateThumbnail(byte[] imageBytes, int width, int height) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(new java.io.ByteArrayInputStream(imageBytes))
                .size(width, height)
                .keepAspectRatio(true)
                .outputQuality(0.9)
                .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (IOException ex) {
            log.error("Failed to generate thumbnail from bytes", ex);
            throw new FileStorageException("Failed to generate thumbnail", ex);
        }
    }

    /**
     * Resize image to specific dimensions
     *
     * @param file multipart file containing image
     * @param width target width
     * @param height target height
     * @return resized image as byte array
     * @throws FileStorageException if resize fails
     */
    public byte[] resizeImage(MultipartFile file, int width, int height) {
        try {
            log.debug("Resizing image: {} to {}x{}", file.getOriginalFilename(), width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(file.getInputStream())
                .size(width, height)
                .keepAspectRatio(false) // Exact size
                .outputQuality(0.95)
                .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (IOException ex) {
            log.error("Failed to resize image: {}", file.getOriginalFilename(), ex);
            throw new FileStorageException("Failed to resize image", ex);
        }
    }

    /**
     * Optimize image for web (reduce file size while maintaining quality)
     *
     * @param file multipart file containing image
     * @param maxSizeKB maximum file size in kilobytes
     * @return optimized image as byte array
     * @throws FileStorageException if optimization fails
     */
    public byte[] optimizeForWeb(MultipartFile file, int maxSizeKB) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Start with high quality
            double quality = 0.95;
            byte[] result;

            do {
                outputStream.reset();
                Thumbnails.of(file.getInputStream())
                    .scale(1.0) // Keep original size
                    .outputQuality(quality)
                    .toOutputStream(outputStream);

                result = outputStream.toByteArray();
                quality -= 0.05; // Reduce quality if needed

            } while (result.length > maxSizeKB * 1024 && quality > 0.5);

            log.debug("Image optimized: {} KB -> {} KB",
                file.getSize() / 1024, result.length / 1024);

            return result;

        } catch (IOException ex) {
            log.error("Failed to optimize image: {}", file.getOriginalFilename(), ex);
            throw new FileStorageException("Failed to optimize image", ex);
        }
    }

    /**
     * Convert image to different format
     *
     * @param file multipart file containing image
     * @param targetFormat target format (jpg, png, webp)
     * @return converted image as byte array
     * @throws FileStorageException if conversion fails
     */
    public byte[] convertFormat(MultipartFile file, String targetFormat) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(file.getInputStream())
                .scale(1.0)
                .outputFormat(targetFormat)
                .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (IOException ex) {
            log.error("Failed to convert image format: {}", file.getOriginalFilename(), ex);
            throw new FileStorageException("Failed to convert image format", ex);
        }
    }

    /**
     * Validate image file
     *
     * @param file multipart file to validate
     * @return true if valid image, false otherwise
     */
    public boolean isValidImage(MultipartFile file) {
        try {
            // Try to read image
            javax.imageio.ImageIO.read(file.getInputStream());
            return true;
        } catch (IOException ex) {
            log.warn("Invalid image file: {}", file.getOriginalFilename());
            return false;
        }
    }
}