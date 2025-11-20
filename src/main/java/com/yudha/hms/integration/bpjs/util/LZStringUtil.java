package com.yudha.hms.integration.bpjs.util;

import com.yudha.hms.integration.bpjs.exception.LZStringDecompressionException;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * LZ-String Decompression Utility.
 *
 * Java implementation of the JavaScript LZ-String algorithm used by BPJS
 * for response compression. This utility handles Base64-encoded compressed
 * data returned by BPJS web services.
 *
 * Based on: https://github.com/pieroxy/lz-string
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Slf4j
public class LZStringUtil {

    private static final String KEY_STR_BASE64 =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    /**
     * Decompress LZ-String compressed data from Base64 format.
     *
     * @param compressed Base64 encoded compressed string
     * @return Decompressed string
     * @throws IllegalArgumentException if input is null or empty
     */
    public static String decompressFromBase64(String compressed) {
        if (compressed == null || compressed.isEmpty()) {
            log.warn("Attempted to decompress null or empty string");
            return "";
        }

        try {
            return decompress(compressed.length(), 32, index -> getBaseValue(KEY_STR_BASE64, compressed.charAt(index)));
        } catch (Exception e) {
            log.error("Failed to decompress LZ-String data", e);
            throw new LZStringDecompressionException("Decompression failed: " + e.getMessage(), e);
        }
    }

    /**
     * Get the numeric value of a character from the key string.
     *
     * @param alphabet The alphabet/key string
     * @param character The character to look up
     * @return Numeric value of the character
     */
    private static int getBaseValue(String alphabet, char character) {
        return alphabet.indexOf(character);
    }

    /**
     * Core decompression algorithm.
     *
     * @param length Length of compressed data
     * @param resetValue Reset value for bit operations
     * @param getNextValue Function to get next value from compressed data
     * @return Decompressed string
     */
    private static String decompress(int length, int resetValue, GetNextValue getNextValue) {
        Map<Integer, String> dictionary = new HashMap<>();
        int enlargeIn = 4;
        int dictSize = 4;
        int numBits = 3;
        String entry;
        StringBuilder result = new StringBuilder();
        int data, dataVal, dataPosition, dataIndex;

        char bits = 0;
        int maxpower, power;
        String c;

        dataVal = getNextValue.get(0);
        dataPosition = resetValue;
        dataIndex = 1;

        for (int i = 0; i < 3; i++) {
            dictionary.put(i, String.valueOf((char) i));
        }

        bits = 0;
        maxpower = (int) Math.pow(2, 2);
        power = 1;

        while (power != maxpower) {
            int resb = dataVal & dataPosition;
            dataPosition >>= 1;
            if (dataPosition == 0) {
                dataPosition = resetValue;
                dataVal = getNextValue.get(dataIndex++);
            }
            bits |= (resb > 0 ? 1 : 0) * power;
            power <<= 1;
        }

        int next = bits;
        switch (next) {
            case 0:
                bits = 0;
                maxpower = (int) Math.pow(2, 8);
                power = 1;
                while (power != maxpower) {
                    int resb = dataVal & dataPosition;
                    dataPosition >>= 1;
                    if (dataPosition == 0) {
                        dataPosition = resetValue;
                        dataVal = getNextValue.get(dataIndex++);
                    }
                    bits |= (resb > 0 ? 1 : 0) * power;
                    power <<= 1;
                }
                c = String.valueOf((char) (int) bits);
                break;
            case 1:
                bits = 0;
                maxpower = (int) Math.pow(2, 16);
                power = 1;
                while (power != maxpower) {
                    int resb = dataVal & dataPosition;
                    dataPosition >>= 1;
                    if (dataPosition == 0) {
                        dataPosition = resetValue;
                        dataVal = getNextValue.get(dataIndex++);
                    }
                    bits |= (resb > 0 ? 1 : 0) * power;
                    power <<= 1;
                }
                c = String.valueOf((char) (int) bits);
                break;
            default:
                return "";
        }

        dictionary.put(3, c);
        String w = c;
        result.append(c);

        while (true) {
            if (dataIndex > length) {
                return "";
            }

            bits = 0;
            maxpower = (int) Math.pow(2, numBits);
            power = 1;
            while (power != maxpower) {
                int resb = dataVal & dataPosition;
                dataPosition >>= 1;
                if (dataPosition == 0) {
                    dataPosition = resetValue;
                    dataVal = getNextValue.get(dataIndex++);
                }
                bits |= (resb > 0 ? 1 : 0) * power;
                power <<= 1;
            }

            int cc = bits;

            switch (cc) {
                case 0:
                    bits = 0;
                    maxpower = (int) Math.pow(2, 8);
                    power = 1;
                    while (power != maxpower) {
                        int resb = dataVal & dataPosition;
                        dataPosition >>= 1;
                        if (dataPosition == 0) {
                            dataPosition = resetValue;
                            dataVal = getNextValue.get(dataIndex++);
                        }
                        bits |= (resb > 0 ? 1 : 0) * power;
                        power <<= 1;
                    }

                    dictionary.put(dictSize++, String.valueOf((char) (int) bits));
                    cc = dictSize - 1;
                    enlargeIn--;
                    break;
                case 1:
                    bits = 0;
                    maxpower = (int) Math.pow(2, 16);
                    power = 1;
                    while (power != maxpower) {
                        int resb = dataVal & dataPosition;
                        dataPosition >>= 1;
                        if (dataPosition == 0) {
                            dataPosition = resetValue;
                            dataVal = getNextValue.get(dataIndex++);
                        }
                        bits |= (resb > 0 ? 1 : 0) * power;
                        power <<= 1;
                    }
                    dictionary.put(dictSize++, String.valueOf((char) (int) bits));
                    cc = dictSize - 1;
                    enlargeIn--;
                    break;
                case 2:
                    return result.toString();
            }

            if (enlargeIn == 0) {
                enlargeIn = (int) Math.pow(2, numBits);
                numBits++;
            }

            if (dictionary.containsKey(cc)) {
                entry = dictionary.get(cc);
            } else {
                if (cc == dictSize) {
                    entry = w + w.charAt(0);
                } else {
                    return "";
                }
            }
            result.append(entry);

            dictionary.put(dictSize++, w + entry.charAt(0));
            enlargeIn--;

            w = entry;

            if (enlargeIn == 0) {
                enlargeIn = (int) Math.pow(2, numBits);
                numBits++;
            }
        }
    }

    /**
     * Functional interface for getting next value during decompression.
     */
    @FunctionalInterface
    private interface GetNextValue {
        int get(int index);
    }
}
