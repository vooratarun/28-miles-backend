package com.quodex._miles.feign.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign Configuration
 *
 * This configuration provides common interceptors and settings for Feign clients.
 * It demonstrates:
 * - Basic Authentication
 * - Custom request/response handling
 * - Error handling
 */
@Configuration
public class FeignConfig {

    /**
     * Basic Auth Interceptor for external APIs
     * Uncomment and configure as needed
     */
    @Bean
    @ConditionalOnProperty(name = "feign.auth.enabled", havingValue = "true")
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("username", "password");
    }
}

