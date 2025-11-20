package com.yudha.hms.integration.satusehat.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * SATUSEHAT Integration Configuration.
 *
 * Configures:
 * - RestTemplate with appropriate timeouts
 * - Async support for audit logging
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Configuration
@EnableAsync
public class SatusehatConfig {

    /**
     * RestTemplate for SATUSEHAT API calls.
     *
     * Configured with:
     * - Connection timeout: 30 seconds
     * - Read timeout: 60 seconds
     */
    @Bean(name = "satusehatRestTemplate")
    public RestTemplate satusehatRestTemplate(RestTemplateBuilder builder) {
        return builder
            .requestFactory(() -> {
                var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
                factory.setConnectTimeout(Duration.ofSeconds(30));
                factory.setReadTimeout(Duration.ofSeconds(60));
                return factory;
            })
            .build();
    }
}
