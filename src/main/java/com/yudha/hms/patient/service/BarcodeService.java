package com.yudha.hms.patient.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Barcode and QR Code Generation Service.
 *
 * Generates barcodes and QR codes for patient identification cards.
 * Uses ZXing library for code generation.
 *
 * Features:
 * - QR code generation with patient MRN
 * - Configurable size and format
 * - Base64 encoding for embedding in HTML/PDF
 * - Error handling and logging
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-18
 */
@Service
@Slf4j
public class BarcodeService {

    private static final int DEFAULT_QR_WIDTH = 250;
    private static final int DEFAULT_QR_HEIGHT = 250;
    private static final int DEFAULT_BARCODE_WIDTH = 300;
    private static final int DEFAULT_BARCODE_HEIGHT = 100;

    /**
     * Generate QR code for patient MRN
     *
     * @param mrn Medical Record Number
     * @return Base64 encoded QR code image
     */
    public String generatePatientQRCode(String mrn) {
        return generatePatientQRCode(mrn, DEFAULT_QR_WIDTH, DEFAULT_QR_HEIGHT);
    }

    /**
     * Generate QR code for patient MRN with custom size
     *
     * @param mrn Medical Record Number
     * @param width QR code width in pixels
     * @param height QR code height in pixels
     * @return Base64 encoded QR code image
     */
    public String generatePatientQRCode(String mrn, int width, int height) {
        try {
            log.debug("Generating QR code for MRN: {}", mrn);

            // Create QR code content (can be enhanced with more data)
            String qrContent = buildQRCodeContent(mrn);

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height);

            // Convert to image bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // Encode to Base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.debug("QR code generated successfully for MRN: {}", mrn);
            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code for MRN: {}", mrn, e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Generate barcode for patient MRN (Code 128)
     *
     * @param mrn Medical Record Number
     * @return Base64 encoded barcode image
     */
    public String generatePatientBarcode(String mrn) {
        return generatePatientBarcode(mrn, DEFAULT_BARCODE_WIDTH, DEFAULT_BARCODE_HEIGHT);
    }

    /**
     * Generate barcode for patient MRN with custom size
     *
     * @param mrn Medical Record Number
     * @param width Barcode width in pixels
     * @param height Barcode height in pixels
     * @return Base64 encoded barcode image
     */
    public String generatePatientBarcode(String mrn, int width, int height) {
        try {
            log.debug("Generating barcode for MRN: {}", mrn);

            // Generate CODE_128 barcode
            QRCodeWriter barcodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = barcodeWriter.encode(mrn, BarcodeFormat.CODE_128, width, height);

            // Convert to image bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            // Encode to Base64
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            log.debug("Barcode generated successfully for MRN: {}", mrn);
            return "data:image/png;base64," + base64Image;

        } catch (WriterException | IOException e) {
            log.error("Failed to generate barcode for MRN: {}", mrn, e);
            throw new RuntimeException("Failed to generate barcode", e);
        }
    }

    /**
     * Build QR code content with patient information
     * Format: MRN|HOSPITAL_CODE|TIMESTAMP
     *
     * @param mrn Medical Record Number
     * @return QR code content string
     */
    private String buildQRCodeContent(String mrn) {
        // Simple format for now - can be enhanced to include more data
        // Example: MRN|HOSPITAL_CODE|GENERATED_TIME
        return String.format("MRN:%s|HOSPITAL:HMS|GEN:%d", mrn, System.currentTimeMillis());
    }

    /**
     * Parse QR code content to extract MRN
     *
     * @param qrContent QR code content string
     * @return Medical Record Number
     */
    public String parseMRNFromQRCode(String qrContent) {
        try {
            if (qrContent != null && qrContent.startsWith("MRN:")) {
                String[] parts = qrContent.split("\\|");
                if (parts.length > 0) {
                    return parts[0].substring(4); // Remove "MRN:" prefix
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse QR code content: {}", qrContent, e);
        }
        return null;
    }

    /**
     * Generate patient card data with QR code and barcode
     *
     * @param mrn Medical Record Number
     * @return PatientCardData with both QR code and barcode
     */
    public PatientCardData generatePatientCardData(String mrn) {
        return PatientCardData.builder()
            .mrn(mrn)
            .qrCode(generatePatientQRCode(mrn))
            .barcode(generatePatientBarcode(mrn))
            .build();
    }

    /**
     * Patient Card Data DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PatientCardData {
        private String mrn;
        private String qrCode;   // Base64 encoded QR code
        private String barcode;  // Base64 encoded barcode
    }
}
