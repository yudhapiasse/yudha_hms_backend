package com.yudha.hms.integration.eklaim.service;

import com.yudha.hms.integration.eklaim.entity.EklaimConfig;
import com.yudha.hms.integration.eklaim.exception.EklaimEncryptionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

/**
 * E-Klaim Encryption/Decryption Service.
 *
 * Implements E-Klaim 5.10.x security protocol:
 * - AES-256-CBC encryption
 * - HMAC-SHA256 signature (first 10 bytes)
 * - IV (Initialization Vector) randomization
 *
 * Encryption Format (Base64 encoded):
 * [HMAC(10 bytes)][IV(16 bytes)][Encrypted Data]
 *
 * Decryption Process:
 * 1. Base64 decode
 * 2. Extract signature (first 10 bytes)
 * 3. Extract IV (next 16 bytes)
 * 4. Extract encrypted data (remaining bytes)
 * 5. Verify HMAC signature
 * 6. Decrypt using AES-256-CBC
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EklaimEncryptionService {

    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int SIGNATURE_LENGTH = 10; // First 10 bytes of HMAC
    private static final int IV_LENGTH = 16; // AES block size
    private static final int KEY_LENGTH = 32; // 256 bits

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Encrypt data for E-Klaim transmission.
     *
     * @param plaintext Data to encrypt
     * @param config E-Klaim configuration with secret key
     * @return Base64 encoded encrypted data with signature and IV
     * @throws EklaimEncryptionException if encryption fails
     */
    public String encrypt(String plaintext, EklaimConfig config) {
        if (plaintext == null || plaintext.isEmpty()) {
            throw new EklaimEncryptionException("Plaintext cannot be null or empty");
        }

        try {
            // Parse key from hex string
            byte[] keyBytes = parseHexKey(config.getSecretKey());

            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            // Encrypt data
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Generate HMAC signature
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec hmacKeySpec = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
            hmac.init(hmacKeySpec);
            hmac.update(encryptedBytes);
            byte[] signature = Arrays.copyOf(hmac.doFinal(), SIGNATURE_LENGTH);

            // Combine: signature + IV + encrypted data
            ByteBuffer combined = ByteBuffer.allocate(
                SIGNATURE_LENGTH + IV_LENGTH + encryptedBytes.length
            );
            combined.put(signature);
            combined.put(iv);
            combined.put(encryptedBytes);

            // Encode to Base64
            String result = Base64.getEncoder().encodeToString(combined.array());

            log.debug("Encrypted data for E-Klaim (length: {} -> {})",
                plaintext.length(), result.length());

            return result;

        } catch (Exception e) {
            log.error("E-Klaim encryption failed", e);
            throw new EklaimEncryptionException("Encryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypt data received from E-Klaim.
     *
     * @param encryptedData Base64 encoded encrypted data
     * @param config E-Klaim configuration with secret key
     * @return Decrypted plaintext
     * @throws EklaimEncryptionException if decryption fails
     */
    public String decrypt(String encryptedData, EklaimConfig config) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            throw new EklaimEncryptionException("Encrypted data cannot be null or empty");
        }

        try {
            // Parse key from hex string
            byte[] keyBytes = parseHexKey(config.getSecretKey());

            // Decode Base64
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            // Validate minimum length
            if (combined.length < SIGNATURE_LENGTH + IV_LENGTH) {
                throw new EklaimEncryptionException(
                    "Invalid encrypted data format: too short"
                );
            }

            // Extract components
            byte[] signature = Arrays.copyOfRange(combined, 0, SIGNATURE_LENGTH);
            byte[] iv = Arrays.copyOfRange(combined, SIGNATURE_LENGTH,
                SIGNATURE_LENGTH + IV_LENGTH);
            byte[] encryptedBytes = Arrays.copyOfRange(combined,
                SIGNATURE_LENGTH + IV_LENGTH, combined.length);

            // Verify HMAC signature
            Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec hmacKeySpec = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
            hmac.init(hmacKeySpec);
            hmac.update(encryptedBytes);
            byte[] expectedSignature = Arrays.copyOf(hmac.doFinal(), SIGNATURE_LENGTH);

            if (!Arrays.equals(signature, expectedSignature)) {
                throw new EklaimEncryptionException(
                    "HMAC signature verification failed - data integrity compromised"
                );
            }

            // Decrypt data
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            String result = new String(decryptedBytes, StandardCharsets.UTF_8);

            log.debug("Decrypted E-Klaim response (length: {} -> {})",
                encryptedData.length(), result.length());

            return result;

        } catch (EklaimEncryptionException e) {
            throw e;
        } catch (Exception e) {
            log.error("E-Klaim decryption failed", e);
            throw new EklaimEncryptionException("Decryption failed: " + e.getMessage(), e);
        }
    }

    /**
     * Parse hexadecimal key string to byte array.
     *
     * @param hexKey Hexadecimal key string (64 characters for 256-bit key)
     * @return Key bytes
     * @throws EklaimEncryptionException if key format is invalid
     */
    private byte[] parseHexKey(String hexKey) {
        if (hexKey == null || hexKey.isEmpty()) {
            throw new EklaimEncryptionException("Secret key is not configured");
        }

        // Remove any whitespace or common separators
        hexKey = hexKey.replaceAll("[\\s:-]", "");

        // Validate hex string length (should be 64 for 256-bit key)
        if (hexKey.length() != KEY_LENGTH * 2) {
            throw new EklaimEncryptionException(
                String.format("Invalid key length: expected %d hex characters, got %d",
                    KEY_LENGTH * 2, hexKey.length())
            );
        }

        try {
            return HexFormat.of().parseHex(hexKey);
        } catch (IllegalArgumentException e) {
            throw new EklaimEncryptionException(
                "Invalid hex key format: " + e.getMessage(), e
            );
        }
    }

    /**
     * Validate encryption configuration.
     *
     * @param config E-Klaim configuration
     * @return true if configuration is valid
     */
    public boolean validateConfiguration(EklaimConfig config) {
        try {
            // Test encryption/decryption cycle
            String testData = "EKLAIM_TEST_" + System.currentTimeMillis();
            String encrypted = encrypt(testData, config);
            String decrypted = decrypt(encrypted, config);

            boolean isValid = testData.equals(decrypted);
            if (isValid) {
                log.info("E-Klaim encryption configuration validated successfully for hospital: {}",
                    config.getHospitalCode());
            } else {
                log.error("E-Klaim encryption validation failed - data mismatch");
            }
            return isValid;

        } catch (Exception e) {
            log.error("E-Klaim encryption configuration validation failed", e);
            return false;
        }
    }
}
