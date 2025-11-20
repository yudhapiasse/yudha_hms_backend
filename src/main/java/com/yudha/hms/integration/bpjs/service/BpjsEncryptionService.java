package com.yudha.hms.integration.bpjs.service;

import com.yudha.hms.integration.bpjs.config.BpjsConfig;
import com.yudha.hms.integration.bpjs.exception.BpjsEncryptionException;
import com.yudha.hms.integration.bpjs.exception.LZStringDecompressionException;
import com.yudha.hms.integration.bpjs.util.LZStringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

/**
 * BPJS Encryption/Decryption Service.
 *
 * Handles encryption and decryption of BPJS web service responses using:
 * - AES-256-CBC encryption
 * - LZ-String compression/decompression
 *
 * BPJS Response Processing Flow:
 * 1. Receive encrypted response (Base64)
 * 2. Decrypt using AES-256 with cons_secret as key
 * 3. Decompress using LZ-String algorithm
 * 4. Parse JSON result
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BpjsEncryptionService {

    private final BpjsConfig bpjsConfig;

    /**
     * Decrypt BPJS response data.
     * Uses AES-256-ECB mode with cons_secret as key.
     *
     * @param encryptedData Base64 encoded encrypted data
     * @return Decrypted string (still compressed)
     * @throws BpjsEncryptionException if decryption fails
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            log.warn("Attempted to decrypt null or empty data");
            return "";
        }

        try {
            // BPJS uses cons_secret as the AES key
            String consSecret = bpjsConfig.getConsSecret();
            if (consSecret == null || consSecret.isEmpty()) {
                throw new BpjsEncryptionException("Consumer secret is not configured");
            }

            // Create AES key from cons_secret
            byte[] keyBytes = consSecret.getBytes(StandardCharsets.UTF_8);
            Key key = new SecretKeySpec(keyBytes, "AES");

            // Initialize cipher for AES/ECB mode
            // Note: BPJS uses ECB mode (not recommended for general use, but required by BPJS)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decode Base64 and decrypt
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            String decrypted = new String(decryptedBytes, StandardCharsets.UTF_8);

            if (bpjsConfig.isLoggingEnabled()) {
                log.debug("Successfully decrypted BPJS response (length: {})", decrypted.length());
            }

            return decrypted;

        } catch (Exception e) {
            log.error("Failed to decrypt BPJS response", e);
            throw new BpjsEncryptionException("Decryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decompress BPJS response data using LZ-String algorithm.
     *
     * @param compressedData LZ-String compressed data (Base64 encoded)
     * @return Decompressed string
     * @throws BpjsEncryptionException if decompression fails
     */
    public String decompress(String compressedData) {
        try {
            return LZStringUtil.decompressFromBase64(compressedData);
        } catch (LZStringDecompressionException e) {
            log.error("Failed to decompress BPJS response", e);
            throw new BpjsEncryptionException("Decompression failed: " + e.getMessage(), e);
        }
    }

    /**
     * Full response processing: decrypt then decompress.
     * This is the typical flow for BPJS encrypted responses.
     *
     * @param encryptedResponse Base64 encoded encrypted response
     * @return Decrypted and decompressed JSON string
     * @throws BpjsEncryptionException if processing fails
     */
    public String decryptAndDecompress(String encryptedResponse) {
        if (encryptedResponse == null || encryptedResponse.isEmpty()) {
            return "";
        }

        try {
            // Step 1: Decrypt
            String decrypted = decrypt(encryptedResponse);

            // Step 2: Decompress
            String decompressed = decompress(decrypted);

            if (bpjsConfig.isLoggingEnabled()) {
                log.debug("Successfully processed BPJS response - " +
                    "encrypted length: {}, decrypted length: {}, final length: {}",
                    encryptedResponse.length(), decrypted.length(), decompressed.length());
            }

            return decompressed;

        } catch (Exception e) {
            log.error("Failed to process BPJS encrypted response", e);
            throw new BpjsEncryptionException(
                "Failed to decrypt and decompress response: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypt data for BPJS requests (if needed).
     * Uses AES-256-ECB mode with cons_secret as key.
     *
     * @param plainData Plain text data to encrypt
     * @return Base64 encoded encrypted data
     * @throws BpjsEncryptionException if encryption fails
     */
    public String encrypt(String plainData) {
        if (plainData == null || plainData.isEmpty()) {
            return "";
        }

        try {
            String consSecret = bpjsConfig.getConsSecret();
            if (consSecret == null || consSecret.isEmpty()) {
                throw new BpjsEncryptionException("Consumer secret is not configured");
            }

            // Create AES key from cons_secret
            byte[] keyBytes = consSecret.getBytes(StandardCharsets.UTF_8);
            Key key = new SecretKeySpec(keyBytes, "AES");

            // Initialize cipher for AES/ECB mode
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // Encrypt and encode to Base64
            byte[] plainBytes = plainData.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedBytes = cipher.doFinal(plainBytes);
            String encrypted = Base64.getEncoder().encodeToString(encryptedBytes);

            if (bpjsConfig.isLoggingEnabled()) {
                log.debug("Successfully encrypted data (length: {} -> {})",
                    plainData.length(), encrypted.length());
            }

            return encrypted;

        } catch (Exception e) {
            log.error("Failed to encrypt data for BPJS", e);
            throw new BpjsEncryptionException("Encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Validate encryption/decryption capability.
     *
     * @return true if encryption is properly configured
     */
    public boolean validateConfiguration() {
        try {
            // Test encryption/decryption cycle
            String testData = "TEST";
            String encrypted = encrypt(testData);
            String decrypted = decrypt(encrypted);

            boolean isValid = testData.equals(decrypted);
            if (isValid) {
                log.info("BPJS encryption configuration is valid");
            } else {
                log.error("BPJS encryption validation failed - data mismatch");
            }
            return isValid;

        } catch (Exception e) {
            log.error("BPJS encryption configuration validation failed", e);
            return false;
        }
    }
}
