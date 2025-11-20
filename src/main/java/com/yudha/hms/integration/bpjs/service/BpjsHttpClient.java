package com.yudha.hms.integration.bpjs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudha.hms.integration.bpjs.config.BpjsConfig;
import com.yudha.hms.integration.bpjs.exception.BpjsHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * BPJS HTTP Client Wrapper.
 *
 * Handles all HTTP communication with BPJS web services including:
 * - Request authentication (HMAC-SHA256 signature)
 * - Response decryption (AES-256)
 * - Response decompression (LZ-String)
 * - Error handling and logging
 * - Timeout management
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BpjsHttpClient {

    private final BpjsConfig bpjsConfig;
    private final BpjsAuthenticationService authenticationService;
    private final BpjsEncryptionService encryptionService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Send GET request to BPJS service.
     *
     * @param baseUrl Base URL of the BPJS service
     * @param endpoint API endpoint
     * @param encrypted Whether the response is encrypted
     * @return Response as JsonNode
     * @throws BpjsHttpException if request fails
     */
    public JsonNode get(String baseUrl, String endpoint, boolean encrypted) {
        return sendRequest(baseUrl, endpoint, HttpMethod.GET, null, encrypted);
    }

    /**
     * Send POST request to BPJS service.
     *
     * @param baseUrl Base URL of the BPJS service
     * @param endpoint API endpoint
     * @param requestBody Request body as object
     * @param encrypted Whether the response is encrypted
     * @return Response as JsonNode
     * @throws BpjsHttpException if request fails
     */
    public JsonNode post(String baseUrl, String endpoint, Object requestBody, boolean encrypted) {
        return sendRequest(baseUrl, endpoint, HttpMethod.POST, requestBody, encrypted);
    }

    /**
     * Send PUT request to BPJS service.
     *
     * @param baseUrl Base URL of the BPJS service
     * @param endpoint API endpoint
     * @param requestBody Request body as object
     * @param encrypted Whether the response is encrypted
     * @return Response as JsonNode
     * @throws BpjsHttpException if request fails
     */
    public JsonNode put(String baseUrl, String endpoint, Object requestBody, boolean encrypted) {
        return sendRequest(baseUrl, endpoint, HttpMethod.PUT, requestBody, encrypted);
    }

    /**
     * Send DELETE request to BPJS service.
     *
     * @param baseUrl Base URL of the BPJS service
     * @param endpoint API endpoint
     * @param encrypted Whether the response is encrypted
     * @return Response as JsonNode
     * @throws BpjsHttpException if request fails
     */
    public JsonNode delete(String baseUrl, String endpoint, boolean encrypted) {
        return sendRequest(baseUrl, endpoint, HttpMethod.DELETE, null, encrypted);
    }

    /**
     * Core method to send HTTP requests to BPJS services.
     *
     * @param baseUrl Base URL
     * @param endpoint API endpoint
     * @param method HTTP method
     * @param requestBody Request body (can be null)
     * @param encrypted Whether response is encrypted
     * @return Parsed response as JsonNode
     */
    private JsonNode sendRequest(String baseUrl, String endpoint, HttpMethod method,
                                  Object requestBody, boolean encrypted) {
        if (!bpjsConfig.isEnabled()) {
            throw new BpjsHttpException("BPJS integration is disabled");
        }

        if (!bpjsConfig.isConfigured()) {
            throw new BpjsHttpException("BPJS integration is not properly configured");
        }

        String url = baseUrl + endpoint;
        long startTime = System.currentTimeMillis();

        try {
            // Generate authentication headers
            Map<String, String> authHeaders = authenticationService.generateAuthHeaders();

            // Build HTTP headers
            HttpHeaders headers = new HttpHeaders();
            authHeaders.forEach(headers::set);

            // Create HTTP entity
            HttpEntity<?> entity;
            if (requestBody != null) {
                entity = new HttpEntity<>(requestBody, headers);
            } else {
                entity = new HttpEntity<>(headers);
            }

            if (bpjsConfig.isLoggingEnabled()) {
                log.info("BPJS Request - {} {} (encrypted: {})", method, url, encrypted);
                if (requestBody != null) {
                    log.debug("Request body: {}", objectMapper.writeValueAsString(requestBody));
                }
            }

            // Send request
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                method,
                entity,
                String.class
            );

            long duration = System.currentTimeMillis() - startTime;

            // Process response
            String responseBody = response.getBody();
            if (responseBody == null || responseBody.isEmpty()) {
                log.warn("Received empty response from BPJS");
                return objectMapper.createObjectNode();
            }

            // Parse the response JSON
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // Check BPJS metaData for errors
            if (responseJson.has("metaData")) {
                JsonNode metaData = responseJson.get("metaData");
                String code = metaData.has("code") ? metaData.get("code").asText() : null;
                String message = metaData.has("message") ? metaData.get("message").asText() : null;

                if (!"200".equals(code)) {
                    log.error("BPJS API returned error - code: {}, message: {}", code, message);
                    throw new BpjsHttpException("BPJS API error: " + message, code);
                }
            }

            // Decrypt and decompress if needed
            if (encrypted && responseJson.has("response")) {
                String encryptedData = responseJson.get("response").asText();
                String decryptedData = encryptionService.decryptAndDecompress(encryptedData);

                // Replace response field with decrypted data
                JsonNode decryptedJson = objectMapper.readTree(decryptedData);
                ((com.fasterxml.jackson.databind.node.ObjectNode) responseJson).set("response", decryptedJson);
            }

            if (bpjsConfig.isLoggingEnabled()) {
                log.info("BPJS Response - {} {} completed in {}ms", method, url, duration);
                log.debug("Response: {}", objectMapper.writeValueAsString(responseJson));
            }

            return responseJson;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("BPJS HTTP error - {} {} failed in {}ms: {} {}",
                method, url, duration, e.getStatusCode(), e.getResponseBodyAsString());
            throw new BpjsHttpException(
                "HTTP request failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                String.valueOf(e.getStatusCode().value())
            );

        } catch (BpjsHttpException e) {
            throw e;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("BPJS request failed - {} {} failed in {}ms",
                method, url, duration, e);
            throw new BpjsHttpException("Request failed: " + e.getMessage(), e);
        }
    }

    /**
     * VClaim service GET request.
     *
     * @param endpoint API endpoint
     * @return Response as JsonNode
     */
    public JsonNode vclaimGet(String endpoint) {
        return get(bpjsConfig.getVClaimBaseUrl(), endpoint, true);
    }

    /**
     * VClaim service POST request.
     *
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response as JsonNode
     */
    public JsonNode vclaimPost(String endpoint, Object requestBody) {
        return post(bpjsConfig.getVClaimBaseUrl(), endpoint, requestBody, true);
    }

    /**
     * Antrean RS (Queue) service GET request.
     *
     * @param endpoint API endpoint
     * @return Response as JsonNode
     */
    public JsonNode antreanRsGet(String endpoint) {
        return get(bpjsConfig.getAntreanRsBaseUrl(), endpoint, true);
    }

    /**
     * Antrean RS (Queue) service POST request.
     *
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response as JsonNode
     */
    public JsonNode antreanRsPost(String endpoint, Object requestBody) {
        return post(bpjsConfig.getAntreanRsBaseUrl(), endpoint, requestBody, true);
    }

    /**
     * Apotek (Pharmacy) service GET request.
     *
     * @param endpoint API endpoint
     * @return Response as JsonNode
     */
    public JsonNode apotekGet(String endpoint) {
        return get(bpjsConfig.getApotekBaseUrl(), endpoint, true);
    }

    /**
     * Apotek (Pharmacy) service POST request.
     *
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response as JsonNode
     */
    public JsonNode apotekPost(String endpoint, Object requestBody) {
        return post(bpjsConfig.getApotekBaseUrl(), endpoint, requestBody, true);
    }

    /**
     * Aplicares service GET request.
     *
     * @param endpoint API endpoint
     * @return Response as JsonNode
     */
    public JsonNode aplicaresGet(String endpoint) {
        return get(bpjsConfig.getAplicaresBaseUrl(), endpoint, true);
    }

    /**
     * Aplicares service POST request.
     *
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response as JsonNode
     */
    public JsonNode aplicaresPost(String endpoint, Object requestBody) {
        return post(bpjsConfig.getAplicaresBaseUrl(), endpoint, requestBody, true);
    }

    /**
     * iCare JKN service GET request.
     *
     * @param endpoint API endpoint
     * @return Response as JsonNode
     */
    public JsonNode icareGet(String endpoint) {
        return get(bpjsConfig.getICareBaseUrl(), endpoint, true);
    }

    /**
     * iCare JKN service POST request.
     *
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response as JsonNode
     */
    public JsonNode icarePost(String endpoint, Object requestBody) {
        return post(bpjsConfig.getICareBaseUrl(), endpoint, requestBody, true);
    }

    /**
     * eRekamMedis (EMR) service GET request.
     *
     * @param endpoint API endpoint
     * @return Response as JsonNode
     */
    public JsonNode erekammedisGet(String endpoint) {
        return get(bpjsConfig.getERekamMedisBaseUrl(), endpoint, true);
    }

    /**
     * eRekamMedis (EMR) service POST request.
     *
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response as JsonNode
     */
    public JsonNode erekammedisPost(String endpoint, Object requestBody) {
        return post(bpjsConfig.getERekamMedisBaseUrl(), endpoint, requestBody, true);
    }

    /**
     * PCare (Primary Care) service GET request.
     *
     * @param endpoint API endpoint
     * @return Response as JsonNode
     */
    public JsonNode pcareGet(String endpoint) {
        return get(bpjsConfig.getPCareBaseUrl(), endpoint, true);
    }

    /**
     * PCare (Primary Care) service POST request.
     *
     * @param endpoint API endpoint
     * @param requestBody Request body
     * @return Response as JsonNode
     */
    public JsonNode pcarePost(String endpoint, Object requestBody) {
        return post(bpjsConfig.getPCareBaseUrl(), endpoint, requestBody, true);
    }
}
