package com.quodex._miles.feign.client;

import feign.Logger;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Shipping Service Feign Configuration
 *
 * Provides custom configuration for the Shipping Service Feign Client:
 * - HTTP client setup (OkHttpClient)
 * - Logging level
 * - Error handling
 * - Request/Response interceptors
 */
@Configuration
public class ShippingFeignConfig {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Log request/response body
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ShippingErrorDecoder();
    }
}

