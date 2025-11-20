package com.yudha.hms.integration.bpjs.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Configuration for BPJS Integration.
 *
 * Configures RestTemplate with appropriate timeouts and settings
 * for BPJS web service communication.
 *
 * @author HMS Development Team
 * @version 1.0.0
 * @since 2025-01-20
 */
@Configuration
@RequiredArgsConstructor
public class BpjsRestTemplateConfig {

    private final BpjsConfig bpjsConfig;

    /**
     * Create RestTemplate bean for BPJS HTTP communication.
     * Configures connection and read timeouts from BPJS configuration.
     *
     * @return Configured RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(bpjsConfig.getConnectionTimeout());
        requestFactory.setReadTimeout(bpjsConfig.getReadTimeout());

        RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(requestFactory)
        );

        return restTemplate;
    }
}

