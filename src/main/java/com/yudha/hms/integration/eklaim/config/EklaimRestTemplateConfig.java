package com.yudha.hms.integration.eklaim.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate configuration for E-Klaim HTTP client.
 *
 * Configures:
 * - Connection timeouts (30 seconds default)
 * - Read timeouts (60 seconds default)
 * - Request/response buffering for logging
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Configuration
public class EklaimRestTemplateConfig {

    @Bean(name = "eklaimRestTemplate")
    public RestTemplate eklaimRestTemplate(RestTemplateBuilder builder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(30));
        requestFactory.setReadTimeout(Duration.ofSeconds(60));

        return builder
            .requestFactory(() -> new BufferingClientHttpRequestFactory(requestFactory))
            .build();
    }
}
