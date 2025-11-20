package com.yudha.hms.integration.bpjs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * BPJS Kesehatan Configuration.
 *
 * Manages BPJS endpoints, credentials, and integration settings
 * for Indonesia's national health insurance system (BPJS Kesehatan).
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Configuration
@ConfigurationProperties(prefix = "bpjs")
@Data
public class BpjsConfig {

    /**
     * Environment: development or production
     */
    private String environment = "development";

    /**
     * Enable/disable BPJS integration
     */
    private boolean enabled = false;

    /**
     * Consumer ID from BPJS Kesehatan
     */
    private String consId;

    /**
     * Consumer secret key for signature generation
     */
    private String consSecret;

    /**
     * User key for web service access
     */
    private String userKey;

    /**
     * Facility/hospital code registered with BPJS
     */
    private String facilityCode;

    /**
     * Facility name
     */
    private String facilityName;

    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeout = 30000; // 30 seconds

    /**
     * Read timeout in milliseconds
     */
    private int readTimeout = 60000; // 60 seconds

    /**
     * Enable request/response logging
     */
    private boolean loggingEnabled = true;

    /**
     * Timezone for Indonesia (UTC+7)
     */
    private String timezone = "Asia/Jakarta";

    /**
     * VClaim service endpoints
     */
    private VClaimEndpoints vclaim = new VClaimEndpoints();

    /**
     * Antrean RS (Queue) service endpoints
     */
    private AntreanRsEndpoints antreanRs = new AntreanRsEndpoints();

    /**
     * Apotek (Pharmacy) service endpoints
     */
    private ApotekEndpoints apotek = new ApotekEndpoints();

    /**
     * Aplicares service endpoints
     */
    private AplicaresEndpoints aplicares = new AplicaresEndpoints();

    /**
     * iCare JKN service endpoints
     */
    private ICareEndpoints icare = new ICareEndpoints();

    /**
     * eRekamMedis (Electronic Medical Record) service endpoints
     */
    private ERekamMedisEndpoints erekammedis = new ERekamMedisEndpoints();

    /**
     * PCare (Primary Care) service endpoints
     */
    private PCareEndpoints pcare = new PCareEndpoints();

    /**
     * VClaim Endpoints (Claims and Eligibility)
     */
    @Data
    public static class VClaimEndpoints {
        private String devBaseUrl = "https://apijkn-dev.bpjs-kesehatan.go.id/vclaim-rest-dev";
        private String prodBaseUrl = "https://apijkn.bpjs-kesehatan.go.id/vclaim-rest";
    }

    /**
     * Antrean RS Endpoints (Hospital Queue Management)
     */
    @Data
    public static class AntreanRsEndpoints {
        private String devBaseUrl = "https://apijkn-dev.bpjs-kesehatan.go.id/antreanrs-dev";
        private String prodBaseUrl = "https://apijkn.bpjs-kesehatan.go.id/antreanrs";
    }

    /**
     * Apotek Endpoints (Pharmacy Services)
     */
    @Data
    public static class ApotekEndpoints {
        private String devBaseUrl = "https://apijkn-dev.bpjs-kesehatan.go.id/apotek-rest-dev";
        private String prodBaseUrl = "https://apijkn.bpjs-kesehatan.go.id/apotek-rest";
    }

    /**
     * Aplicares Endpoints (Care Application Services)
     */
    @Data
    public static class AplicaresEndpoints {
        private String devBaseUrl = "https://apijkn-dev.bpjs-kesehatan.go.id/aplicaresws-rest-dev";
        private String prodBaseUrl = "https://new-api.bpjs-kesehatan.go.id/aplicaresws";
    }

    /**
     * iCare JKN Endpoints (Hospital Information System Integration)
     */
    @Data
    public static class ICareEndpoints {
        private String devBaseUrl = "https://apijkn-dev.bpjs-kesehatan.go.id/icare-dev";
        private String prodBaseUrl = "https://apijkn.bpjs-kesehatan.go.id/wsihs/api/rs";
    }

    /**
     * eRekamMedis Endpoints (Electronic Medical Records)
     */
    @Data
    public static class ERekamMedisEndpoints {
        private String devBaseUrl = "https://apijkn-dev.bpjs-kesehatan.go.id/erekammedis-dev";
        private String prodBaseUrl = "https://dvlp.bpjs-kesehatan.go.id/eRekamMedis";
    }

    /**
     * PCare Endpoints (Primary Care Services)
     */
    @Data
    public static class PCareEndpoints {
        private String devBaseUrl = "https://apijkn-dev.bpjs-kesehatan.go.id/pcare-rest-dev";
        private String prodBaseUrl = "https://apijkn.bpjs-kesehatan.go.id/pcare-rest";
    }

    /**
     * Get base URL for VClaim service based on environment
     */
    public String getVClaimBaseUrl() {
        return "production".equalsIgnoreCase(environment) ?
                vclaim.getProdBaseUrl() : vclaim.getDevBaseUrl();
    }

    /**
     * Get base URL for Antrean RS service based on environment
     */
    public String getAntreanRsBaseUrl() {
        return "production".equalsIgnoreCase(environment) ?
                antreanRs.getProdBaseUrl() : antreanRs.getDevBaseUrl();
    }

    /**
     * Get base URL for Apotek service based on environment
     */
    public String getApotekBaseUrl() {
        return "production".equalsIgnoreCase(environment) ?
                apotek.getProdBaseUrl() : apotek.getDevBaseUrl();
    }

    /**
     * Get base URL for Aplicares service based on environment
     */
    public String getAplicaresBaseUrl() {
        return "production".equalsIgnoreCase(environment) ?
                aplicares.getProdBaseUrl() : aplicares.getDevBaseUrl();
    }

    /**
     * Get base URL for iCare service based on environment
     */
    public String getICareBaseUrl() {
        return "production".equalsIgnoreCase(environment) ?
                icare.getProdBaseUrl() : icare.getDevBaseUrl();
    }

    /**
     * Get base URL for eRekamMedis service based on environment
     */
    public String getERekamMedisBaseUrl() {
        return "production".equalsIgnoreCase(environment) ?
                erekammedis.getProdBaseUrl() : erekammedis.getDevBaseUrl();
    }

    /**
     * Get base URL for PCare service based on environment
     */
    public String getPCareBaseUrl() {
        return "production".equalsIgnoreCase(environment) ?
                pcare.getProdBaseUrl() : pcare.getDevBaseUrl();
    }

    /**
     * Check if running in production mode
     */
    public boolean isProduction() {
        return "production".equalsIgnoreCase(environment);
    }

    /**
     * Check if BPJS integration is enabled and properly configured
     */
    public boolean isConfigured() {
        return enabled &&
               consId != null && !consId.isEmpty() &&
               consSecret != null && !consSecret.isEmpty() &&
               userKey != null && !userKey.isEmpty();
    }
}
